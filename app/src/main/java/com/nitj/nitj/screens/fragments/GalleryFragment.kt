package com.nitj.nitj.screens.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
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
import com.nitj.nitj.adapters.galleryAdapters.GalleryEventAdapter
import com.nitj.nitj.models.FacultyData
import com.nitj.nitj.models.gallery.GalleryData
import com.nitj.nitj.models.gallery.GalleryEvent
import com.nitj.nitj.util.ConnectionManager
import java.util.ArrayList


class GalleryFragment : Fragment() {

    private lateinit var noGalleryData: CardView
    private lateinit var noGalleryDataText: TextView
    private lateinit var galleryEventRecycler: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: GalleryEventAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var progressDialog: ProgressDialog
    private var galleryEventList = arrayListOf<GalleryEvent>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        findViews(view)
        return view
    }

    private fun findViews(view: View) {
        databaseRef = Firebase.database.reference
        galleryEventRecycler = view.findViewById(R.id.GalleryEventRecycler)
        noGalleryData = view.findViewById(R.id.noGalleryData)
        noGalleryDataText = view.findViewById(R.id.noGalleryDataText)
        getGalleryData()
    }

    private fun setAdapter() {
        layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = GalleryEventAdapter(requireContext(), galleryEventList,galleryEventRecycler)
        galleryEventRecycler.layoutManager = layoutManager
        galleryEventRecycler.adapter = recyclerAdapter
    }

    private fun getGalleryData() {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading.....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        if (activity?.let { ConnectionManager().checkConnectivity(it.applicationContext) } == true) {

            try {
                databaseRef =
                    databaseRef.ref.child("Gallery")
                databaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            galleryEventList.clear()
                            for (events in dataSnapshot.children.reversed()) {
                                val galleryEventName = events.key.toString()
                                val galleryDataArray = ArrayList<GalleryData>()
                                for (data in events.children) {
                                    val galleryData = data.getValue(GalleryData::class.java)
                                    galleryDataArray.add(galleryData!!)
                                }
                                val galleryEvent = GalleryEvent(galleryEventName,galleryDataArray)
                                galleryEventList.add(galleryEvent)
                            }

                            galleryEventRecycler.visibility = View.VISIBLE
                            noGalleryData.visibility = View.GONE
                            progressDialog.dismiss()

                        } else {
                            print("data not found")
                            galleryEventRecycler.visibility = View.GONE
                            noGalleryData.visibility = View.VISIBLE
                            progressDialog.dismiss()
                        }
                        setAdapter()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        print("${databaseError.toException()}")
                        progressDialog.dismiss()
                        galleryEventRecycler.visibility = View.GONE
                        noGalleryData.visibility = View.VISIBLE
                        noGalleryDataText.text = databaseError.toException().toString()
                        Log.w(
                            "Database Get Gallery",
                            "loadGallery:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                progressDialog.dismiss()
                galleryEventRecycler.visibility = View.GONE
                noGalleryData.visibility = View.VISIBLE
                noGalleryDataText.text = e.toString()
                Log.w(
                    "Database Get Gallery",
                    "loadGallery:onCancelled",
                    e
                )
            }
        } else {
            progressDialog.dismiss()
            galleryEventRecycler.visibility = View.GONE
            noGalleryData.visibility = View.VISIBLE
            noGalleryDataText.text = "Internet Connection Not Found"
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

}