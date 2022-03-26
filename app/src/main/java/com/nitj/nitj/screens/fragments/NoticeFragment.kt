package com.nitj.nitj.screens.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nitj.nitj.R
import com.nitj.nitj.adapters.AllBranchAdapter
import com.nitj.nitj.adapters.NoticeAdapter
import com.nitj.nitj.models.DepartmentData
import com.nitj.nitj.models.FacultyData
import com.nitj.nitj.models.NoticeData
import com.nitj.nitj.util.ConnectionManager

class NoticeFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var noticeRecycler: RecyclerView
    private lateinit var databaseRef: DatabaseReference
    private var noticeList = arrayListOf<NoticeData>()
    private var noticeDisplayList = arrayListOf<NoticeData>()
    private lateinit var noData: CardView
    private lateinit var noDataText: TextView
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notice, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        findView(view)
        getSearchView(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    private fun findView(view: View) {
        databaseRef = Firebase.database.reference
        progressBar = view.findViewById(R.id.progressBar)
        searchView = view.findViewById(R.id.searchView)
        noData = view.findViewById(R.id.noData)
        noDataText = view.findViewById(R.id.noDataText)
        noticeRecycler = view.findViewById(R.id.noticeRecycler)
        getNotice()
    }

    private fun getNotice() {
        progressBar.visibility = View.VISIBLE
        if (activity?.let { ConnectionManager().checkConnectivity(it.applicationContext) } == true) {

            try {
                databaseRef = databaseRef.ref.child("Notice")
                databaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            noticeList.clear()
                            for (data in dataSnapshot.children) {
                                val noticeData = data.getValue(NoticeData::class.java)
                                noticeList.add(noticeData!!)
                            }
                            noticeRecycler.visibility = View.VISIBLE
                            noData.visibility = View.GONE
                            searchView.visibility = View.VISIBLE
                        } else {
                            noticeRecycler.visibility = View.GONE
                            noData.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            searchView.visibility = View.GONE
                        }
                        setAdapter()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        print("${databaseError.toException()}")
                        progressBar.visibility = View.GONE
                        noticeRecycler.visibility = View.GONE
                        noData.visibility = View.VISIBLE
                        searchView.visibility = View.GONE
                        noDataText.text = databaseError.toException().toString()
                        Log.w(
                            "Database Get Notice",
                            "Load Notice:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                noticeRecycler.visibility = View.GONE
                searchView.visibility = View.GONE
                noData.visibility = View.VISIBLE
                noDataText.text = e.toString()
                Log.w(
                    "Database Get Faculty",
                    "loadFaculty:onCancelled",
                    e
                )
            }
        } else {
            progressBar.visibility = View.GONE
            noticeRecycler.visibility = View.GONE
            searchView.visibility = View.GONE
            noData.visibility = View.VISIBLE
            noDataText.text = "Internet Connection Not Found"
            val dialog = AlertDialog.Builder(activity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(requireActivity())
            }
            dialog.create()
            dialog.show()
        }

    }

    private fun setAdapter() {
        noticeDisplayList.clear()
        noticeDisplayList.addAll(noticeList)
        layoutManager = LinearLayoutManager(activity)
        noticeAdapter = NoticeAdapter(requireContext(), noticeDisplayList)
        noticeRecycler.layoutManager = layoutManager
        noticeRecycler.adapter = noticeAdapter
        progressBar.visibility = View.GONE
        searchView.visibility = View.VISIBLE
    }

    private fun getSearchView(view: View) {
        searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(nextText: String?): Boolean {
                if (nextText!!.isNotBlank()) {
                    noticeDisplayList.clear()
                    val search = nextText.toLowerCase()
                    noticeList.forEach {
                        if (it.title.toLowerCase().contains(search)) {
                            noticeDisplayList.add(it)
                        }
                    }
                    if (noticeDisplayList.isEmpty()) {
                        Toast.makeText(context, "Notice Not Found!!", Toast.LENGTH_SHORT).show()
                    }
                    noticeAdapter.notifyDataSetChanged()
                } else {
                    noticeDisplayList.clear()
                    noticeDisplayList.addAll(noticeList)
                    noticeAdapter.notifyDataSetChanged()
                }
                return true
            }
        })

    }

}