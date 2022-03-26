package com.nitj.nitj.screens.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nitj.nitj.R
import com.nitj.nitj.adapters.EBookAdapter
import com.nitj.nitj.models.Ebook.Ebook
import com.nitj.nitj.models.Ebook.EbookMain
import com.nitj.nitj.screens.MainActivity
import com.nitj.nitj.util.ConnectionManager
import java.util.*

class EbookFragment : Fragment() {

    private lateinit var nitJEbookTitle: TextView
    private lateinit var noDepartmentDataText: TextView
    private lateinit var belowTitle: TextView
    private lateinit var bookRecycler: RecyclerView
    private lateinit var noEbookData: CardView
    private lateinit var selectDepartmentForEbookSpinner: Spinner
    private lateinit var searchViewEbook: SearchView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: EBookAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var progressDialog: ProgressDialog
    private lateinit var progressBar: ProgressBar
    private var ebookList = arrayListOf<Ebook>()
    private var ebookDisplayList = arrayListOf<Ebook>()
    private var ebookMainDataList = arrayListOf<EbookMain>()
    private var department: String = "Select Department"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ebook, container, false)

        (activity as MainActivity).setDrawerEnabled(enabled = false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        activity?.onBackPressedDispatcher?.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (progressBar.isVisible) {
                        // in here you can do logic when backPress is clicked
                    } else {
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }
            })

        findViews(view)
        inflateSpinner()
        changeDepartmentAndEbookData()
        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as MainActivity).setDrawerEnabled(enabled = true)
    }

    private fun changeDepartmentAndEbookData() {
        selectDepartmentForEbookSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    department = selectDepartmentForEbookSpinner.selectedItem.toString()
                    if (department != "Select Department") {
                        searchViewEbook.visibility = View.VISIBLE
                        belowTitle.visibility = View.GONE
                        noEbookData.visibility = View.GONE
                        bookRecycler.visibility = View.VISIBLE
                        getData()
                    } else {
                        searchViewEbook.visibility = View.GONE
                        belowTitle.visibility = View.VISIBLE
                        noEbookData.visibility = View.VISIBLE
                        bookRecycler.visibility = View.GONE
                        noDepartmentDataText.text = "Please select the available departments"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    fun getData() {
        ebookList.clear()
        ebookMainDataList.forEach {
            if (it.department == department) {
                ebookList.addAll(it.arrayList)
            }
        }
        if(ebookList.size == 0){
            noEbookData.visibility = View.VISIBLE
        }
        getSearchView()
        setAdapter()
    }

    private fun findViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        noDepartmentDataText = view.findViewById(R.id.noDepartmentDataText)
        searchViewEbook = view.findViewById(R.id.searchViewEbook)
        databaseRef = Firebase.database.reference
        nitJEbookTitle = view.findViewById(R.id.nitjEbookTitle)
        bookRecycler = view.findViewById(R.id.bookRecycler)
        selectDepartmentForEbookSpinner = view.findViewById(R.id.selectDepartmentForEbookSpinner)
        searchViewEbook = view.findViewById(R.id.searchViewEbook)
        belowTitle = view.findViewById(R.id.belowTitle)
        noEbookData = view.findViewById(R.id.noEbookData)
    }

    private fun setAdapter() {
        ebookDisplayList.clear()
        ebookDisplayList.addAll(ebookList)
        layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = EBookAdapter(requireContext(), ebookDisplayList)
        bookRecycler.layoutManager = layoutManager
        bookRecycler.adapter = recyclerAdapter
        progressBar.visibility = View.GONE
        (activity as MainActivity).setDrawerEnabled(enabled = true)
    }

    private fun getEbookData() {
        progressBar.visibility = View.VISIBLE
        bookRecycler.visibility = View.VISIBLE
        progressDialog.dismiss()

        if (ConnectionManager().checkConnectivity(requireContext())) {
            try {
                databaseRef = databaseRef.ref.child("Ebook").child("Electronics & Comm Engineering")
                databaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            progressBar.visibility = View.GONE
                            bookRecycler.visibility = View.GONE
                            noEbookData.visibility = View.VISIBLE

                            progressDialog.dismiss()
                            searchViewEbook.visibility = View.GONE
                            belowTitle.visibility = View.VISIBLE
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "data present",
                                Toast.LENGTH_SHORT
                            ).show()
                            ebookList.clear()
                            for (data in dataSnapshot.children) {
                                Toast.makeText(
                                    requireContext(),
                                    "${data.key}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val ebookData: Ebook? = data.getValue(Ebook::class.java)
                                ebookList.add(ebookData!!)
                            }
                            Log.i("ebookData", "This is the $ebookList")
                            print(ebookList)
                            progressBar.visibility = View.GONE
                            bookRecycler.visibility = View.VISIBLE
                            noEbookData.visibility = View.GONE
                            searchViewEbook.visibility = View.VISIBLE
                            belowTitle.visibility = View.GONE
                            progressDialog.dismiss()
                        }
                        setAdapter()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        progressDialog.dismiss()
                        searchViewEbook.visibility = View.GONE
                        belowTitle.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "${databaseError.toException()}\nSomething went wrong : Department Loading Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.w(
                            "Department OnCancelled",
                            "LoadDepartment:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                progressDialog.dismiss()
                progressBar.visibility = View.GONE
                searchViewEbook.visibility = View.GONE
                belowTitle.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "$e\nSomething went wrong :  DepartmentLoading Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            progressDialog.dismiss()
            progressBar.visibility = View.GONE
            searchViewEbook.visibility = View.GONE
            belowTitle.visibility = View.VISIBLE
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                (context as AppCompatActivity).finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(context as AppCompatActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun inflateSpinner() {

        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading.....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        if (ConnectionManager().checkConnectivity(requireContext())) {
            try {
                databaseRef = databaseRef.ref.child("Ebook")
                databaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            progressBar.visibility = View.GONE
                            bookRecycler.visibility = View.GONE
                            searchViewEbook.visibility = View.GONE
                            belowTitle.visibility = View.VISIBLE
                            noEbookData.visibility = View.VISIBLE

                            val items = mutableListOf("Select Department")
                            val immutableList = Collections.unmodifiableList(items)
                            selectDepartmentForEbookSpinner.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                immutableList.toList()
                            )
                            progressDialog.dismiss()
                        } else {
                            val items = mutableListOf("Select Department")
                            ebookMainDataList.clear()

                            for (data in dataSnapshot.children) {
                                val category = data.key
                                items.add(category!!)
                                /////////////////////
                                val ebookDepartment = data.key.toString()
                                val arrayList: ArrayList<Ebook> = ArrayList<Ebook>()
                                for (ebook in data.children) {
                                    val ebookData: Ebook? = ebook.getValue(Ebook::class.java)
                                    arrayList.add(ebookData!!)
                                }
                                val ebookMain = EbookMain(ebookDepartment, arrayList)
                                ebookMainDataList.add(ebookMain)
                            }
                            val immutableList = Collections.unmodifiableList(items)
                            selectDepartmentForEbookSpinner.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                immutableList.toList()
                            )
                            progressBar.visibility = View.GONE
                            bookRecycler.visibility = View.VISIBLE
                            searchViewEbook.visibility = View.VISIBLE
                            belowTitle.visibility = View.GONE
                            noEbookData.visibility = View.GONE
                            progressDialog.dismiss()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        progressDialog.dismiss()
                        searchViewEbook.visibility = View.GONE
                        belowTitle.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        noEbookData.visibility = View.VISIBLE
                        noDepartmentDataText.text = databaseError.toException().toString()
                        Toast.makeText(
                            requireContext(),
                            "${databaseError.toException()}\nSomething went wrong : Department Loading Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.w(
                            "Department OnCancelled",
                            "LoadDepartment:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                progressDialog.dismiss()
                progressBar.visibility = View.GONE
                searchViewEbook.visibility = View.GONE
                belowTitle.visibility = View.VISIBLE
                noEbookData.visibility = View.VISIBLE
                noDepartmentDataText.text = e.toString()
                Toast.makeText(
                    requireContext(),
                    "$e\nSomething went wrong :  DepartmentLoading Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            progressDialog.dismiss()
            progressBar.visibility = View.GONE
            searchViewEbook.visibility = View.GONE
            noEbookData.visibility = View.VISIBLE
            belowTitle.visibility = View.VISIBLE
            noDepartmentDataText.text = "Internet Connection Not Found"
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                (context as AppCompatActivity).finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(context as AppCompatActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun getSearchView() {
        searchViewEbook.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchViewEbook.clearFocus()
                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(nextText: String?): Boolean {
                if (nextText!!.isNotBlank()) {
                    ebookDisplayList.clear()
                    val search = nextText.toLowerCase()
                    ebookList.forEach {
                        if (it.ebookTitle.toLowerCase().contains(search)) {
                            ebookDisplayList.add(it)
                        } else if (it.pickedFileName.toLowerCase().contains(search)) {
                            ebookDisplayList.add(it)
                        }
                    }
                    if (ebookDisplayList.isEmpty()) {
                        Toast.makeText(context, "Ebook Not Found!!", Toast.LENGTH_SHORT).show()
                    }
                    recyclerAdapter.notifyDataSetChanged()
                } else {
                    ebookDisplayList.clear()
                    ebookDisplayList.addAll(ebookList)
                    recyclerAdapter.notifyDataSetChanged()
                }
                return true
            }
        })

    }
}

