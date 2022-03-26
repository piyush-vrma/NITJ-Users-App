package com.nitj.nitj.screens.fragments

import FacultyAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nitj.nitj.R
import com.nitj.nitj.models.FacultyData
import com.nitj.nitj.screens.MainActivity
import com.nitj.nitj.util.ConnectionManager

class FacultyFragment : Fragment() {

    private lateinit var noFacultyData: CardView
    private lateinit var noFacultyDataText: TextView
    private lateinit var facultyDepartmentHeader: TextView
    private var departmentName: String = ""
    lateinit var facultyRecycler: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: FacultyAdapter
    private var facultyList = arrayListOf<FacultyData>()

    private lateinit var databaseRef: DatabaseReference
    private lateinit var progressBar: ProgressBar
    var count: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faculty, container, false)
        (activity as MainActivity).setDrawerEnabled(enabled = false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return view
    }


    override fun onResume() {
        view?.let { findView(it) }
        super.onResume()
        (activity as MainActivity).closeDrawer()
        (activity as MainActivity).setDrawerEnabled(enabled = false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).setDrawerEnabled(enabled = true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    private fun getFaculty() {
        progressBar.visibility = View.VISIBLE
        if (activity?.let { ConnectionManager().checkConnectivity(it.applicationContext) } == true) {

            try {
                databaseRef =
                    databaseRef.ref.child("Faculty").child(departmentName)
                databaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val children = dataSnapshot.children

                        if (dataSnapshot.exists()) {
                            print("data coming")
                            facultyList.clear()
                            for (data in dataSnapshot.children) {
                                val facultyData = data.getValue(FacultyData::class.java)
                                facultyList.add(facultyData!!)
                            }
                            facultyRecycler.visibility = View.VISIBLE
                            noFacultyData.visibility = View.GONE
                            progressBar.visibility = View.GONE

                        } else {
                            print("data not foung")
                            facultyRecycler.visibility = View.GONE
                            noFacultyData.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                        }
                        setAdapter()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        print("${databaseError.toException()}")
                        progressBar.visibility = View.GONE
                        facultyRecycler.visibility = View.GONE
                        noFacultyData.visibility = View.VISIBLE
                        noFacultyDataText.text = databaseError.toException().toString()
                        Log.w(
                            "Database Get Faculty",
                            "loadFaculty:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                facultyRecycler.visibility = View.GONE
                noFacultyData.visibility = View.VISIBLE
                noFacultyDataText.text = e.toString()
                Log.w(
                    "Database Get Faculty",
                    "loadFaculty:onCancelled",
                    e
                )
            }
        } else {
            progressBar.visibility = View.GONE
            facultyRecycler.visibility = View.GONE
            noFacultyData.visibility = View.VISIBLE
            noFacultyDataText.text = "Internet Connection Not Found"
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
        layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = FacultyAdapter(requireContext(), facultyList)
        facultyRecycler.layoutManager = layoutManager
        facultyRecycler.adapter = recyclerAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun findView(view: View) {
        databaseRef = Firebase.database.reference
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
        facultyDepartmentHeader = view.findViewById(R.id.facultyDepartmentHeader)
        val safeArgs: FacultyFragmentArgs by navArgs()
        departmentName = safeArgs.department
        facultyRecycler = view.findViewById(R.id.facultyRecycler)
        noFacultyData = view.findViewById(R.id.noFacultyData)
        noFacultyDataText = view.findViewById(R.id.noFacultyDataText)
        facultyDepartmentHeader.text = "Faculty : $departmentName"
        getFaculty()
    }

}