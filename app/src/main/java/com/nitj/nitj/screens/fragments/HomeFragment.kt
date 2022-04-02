package com.nitj.nitj.screens.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nitj.nitj.R
import com.nitj.nitj.adapters.DownloadAdapter
import com.nitj.nitj.adapters.OtherAdapter
import com.nitj.nitj.adapters.SCAdapter
import com.nitj.nitj.adapters.SliderAdapter
import com.nitj.nitj.models.CommonData
import com.nitj.nitj.models.HomeRecyclerModel
import com.nitj.nitj.models.NoticeData
import com.nitj.nitj.models.SliderItem
import com.nitj.nitj.util.ConnectionManager
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView


class HomeFragment : Fragment() {

    // full outer layout
    private lateinit var container: RelativeLayout
    private lateinit var progressbar: ProgressBar

    // slider layout
    private lateinit var sliderView: SliderView
    private lateinit var imageSliderProgressBar: ProgressBar
    private lateinit var adapter: SliderAdapter
    private lateinit var noSliderData: CardView
    private lateinit var noSliderDataText: TextView
    private val mSliderItems: ArrayList<SliderItem> = arrayListOf()

    // compendium layout
    private lateinit var compendium: LinearLayout

    // student Data layout
    private lateinit var studentDataLayout: LinearLayout
    private lateinit var studentDataProgressBar: ProgressBar
    private lateinit var noStudentCornerData: CardView
    private lateinit var noStudentCornerText: TextView
    private lateinit var tvUpEv: TextView
    private lateinit var tvSC: TextView
    private lateinit var tvScShip: TextView

    // student corner recycler View
    private lateinit var studentCornerRecycler: RecyclerView
    lateinit var scLayoutManager: RecyclerView.LayoutManager
    private lateinit var scRecyclerAdapter: SCAdapter
    private val studentCornerList: ArrayList<HomeRecyclerModel> = arrayListOf()
    private val upcomingEventList: ArrayList<HomeRecyclerModel> = arrayListOf()
    private val scholarShipDataList: ArrayList<HomeRecyclerModel> = arrayListOf()

    // download / otherlinks layout
    private lateinit var download_otherLinkDataLayout: LinearLayout
    private lateinit var downloadDataProgressBar: ProgressBar
    private lateinit var noDownloadOtherData: CardView
    private lateinit var noDownloadOtherText: TextView

    private lateinit var downloadsRecycler: RecyclerView
    lateinit var downloadLayoutManager: RecyclerView.LayoutManager
    private lateinit var downloadRecyclerAdapter: DownloadAdapter
    private val downloadsList: ArrayList<HomeRecyclerModel> = arrayListOf()

    private lateinit var otherLinksRecycler: RecyclerView
    lateinit var otherLayoutManager: RecyclerView.LayoutManager
    private lateinit var otherRecyclerAdapter: OtherAdapter
    private val otherLinksList: ArrayList<HomeRecyclerModel> = arrayListOf()

    // database reg
    private lateinit var databaseRef: DatabaseReference

    private var upEvent = true
    private var studentCorner = false
    private var scholarShip = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        findViews(view)
        // Getting Data for Slider Image View from Firebase
        getSliderData()

        // Getting Data for Student Corner from firebase
        getStudentCornerData()

        // Getting Data for Downloads and OtherLinks from firebase
        getDownload_OtherLinksData()


        defaultSetting()

        tvUpEv.setOnClickListener {
            if (!upEvent) {
                defaultSetting()
            }
        }

        tvSC.setOnClickListener {
            if (!studentCorner) {
                setStudentCornerAdapter(studentCornerList)
                studentCorner = true
                tvSC.setBackgroundResource(R.color.nitJ_primary)
                tvSC.setTextColor(resources.getColor(R.color.white))
                upEvent = false
                tvUpEv.setBackgroundResource(R.color.white)
                tvUpEv.setTextColor(resources.getColor(R.color.nitJ_primary))
                scholarShip = false
                tvScShip.setBackgroundResource(R.color.white)
                tvScShip.setTextColor(resources.getColor(R.color.nitJ_primary))
            }
        }

        tvScShip.setOnClickListener {
            setStudentCornerAdapter(scholarShipDataList)
            if (!scholarShip) {
                scholarShip = true
                tvScShip.setBackgroundResource(R.color.nitJ_primary)
                tvScShip.setTextColor(resources.getColor(R.color.white))
                studentCorner = false
                tvSC.setBackgroundResource(R.color.white)
                tvSC.setTextColor(resources.getColor(R.color.nitJ_primary))
                upEvent = false
                tvUpEv.setBackgroundResource(R.color.white)
                tvUpEv.setTextColor(resources.getColor(R.color.nitJ_primary))
            }
        }

        compendium.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeDestToWebViewFragment(
                "Compendium",
                "https://www.nitj.ac.in/NITJ-Compendium/"
            )
            findNavController().navigate(action)
        }

        setOtherAdapters()

        return view
    }

    private fun getDownload_OtherLinksData() {
        downloadDataProgressBar.visibility = View.VISIBLE
        if (activity?.let { ConnectionManager().checkConnectivity(it.applicationContext) } == true) {

            try {
                val newRef = databaseRef.ref.child("Downloads_OtherLinks")
                newRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            downloadsList.clear()
                            otherLinksList.clear()

                            for (data in dataSnapshot.children) {
                                when (data.key.toString()) {
                                    "Downloads" -> {
                                        for (downloadData in data.children.reversed()) {
                                            val homeRecyclerData =
                                                downloadData.getValue(HomeRecyclerModel::class.java)
                                            downloadsList.add(homeRecyclerData!!)
                                        }
                                    }
                                    "OtherLinks" -> {
                                        for (otherLinkData in data.children.reversed()) {
                                            val homeRecyclerData =
                                                otherLinkData.getValue(HomeRecyclerModel::class.java)
                                            otherLinksList.add(homeRecyclerData!!)
                                        }
                                    }
                                }

                            }
                            download_otherLinkDataLayout.visibility = View.VISIBLE
                            noDownloadOtherData.visibility = View.GONE
                            downloadDataProgressBar.visibility = View.GONE
                        } else {
                            download_otherLinkDataLayout.visibility = View.GONE
                            noDownloadOtherData.visibility = View.VISIBLE
                            downloadDataProgressBar.visibility = View.GONE
                        }
                        setOtherAdapters()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        print("${databaseError.toException()}")
                        downloadDataProgressBar.visibility = View.GONE
                        download_otherLinkDataLayout.visibility = View.GONE
                        noDownloadOtherData.visibility = View.VISIBLE
                        noDownloadOtherText.text = databaseError.toException().toString()
                        Log.w(
                            "Database Get Notice",
                            "Load Notice:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                downloadDataProgressBar.visibility = View.GONE
                download_otherLinkDataLayout.visibility = View.GONE
                noDownloadOtherData.visibility = View.VISIBLE
                noDownloadOtherText.text = e.toString()
                Log.w(
                    "Database Get Faculty",
                    "loadFaculty:onCancelled",
                    e
                )
            }
        } else {
            downloadDataProgressBar.visibility = View.GONE
            download_otherLinkDataLayout.visibility = View.GONE
            noDownloadOtherData.visibility = View.VISIBLE
            noDownloadOtherText.text = "Internet Connection Not Found"
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

    private fun getStudentCornerData() {
        studentDataProgressBar.visibility = View.VISIBLE
        if (activity?.let { ConnectionManager().checkConnectivity(it.applicationContext) } == true) {

            try {
                val newRef = databaseRef.ref.child("Student Common Data")
                newRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            studentCornerList.clear()
                            upcomingEventList.clear()
                            scholarShipDataList.clear()
                            for (data in dataSnapshot.children) {
                                when (data.key.toString()) {
                                    "Upcoming Event" -> {
                                        for (upcomingEventData in data.children.reversed()) {
                                            val homeRecyclerData =
                                                upcomingEventData.getValue(HomeRecyclerModel::class.java)
                                            upcomingEventList.add(homeRecyclerData!!)
                                        }
                                    }
                                    "Student Corner" -> {
                                        for (studentCornerData in data.children.reversed()) {
                                            val homeRecyclerData =
                                                studentCornerData.getValue(HomeRecyclerModel::class.java)
                                            studentCornerList.add(homeRecyclerData!!)
                                        }
                                    }
                                    "ScholarShip" -> {
                                        for (scholarShipData in data.children.reversed()) {
                                            val homeRecyclerData =
                                                scholarShipData.getValue(HomeRecyclerModel::class.java)
                                            scholarShipDataList.add(homeRecyclerData!!)
                                        }
                                    }
                                }

                            }
                            studentDataLayout.visibility = View.VISIBLE
                            noStudentCornerData.visibility = View.GONE
                            studentDataProgressBar.visibility = View.GONE
                        } else {
                            studentDataLayout.visibility = View.GONE
                            noStudentCornerData.visibility = View.VISIBLE
                            studentDataProgressBar.visibility = View.GONE
                        }
                        setStudentCornerAdapter(upcomingEventList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        print("${databaseError.toException()}")
                        studentDataProgressBar.visibility = View.GONE
                        studentDataLayout.visibility = View.GONE
                        noStudentCornerData.visibility = View.VISIBLE
                        noStudentCornerText.text = databaseError.toException().toString()
                        Log.w(
                            "Database Get Notice",
                            "Load Notice:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                studentDataProgressBar.visibility = View.GONE
                studentDataLayout.visibility = View.GONE
                noStudentCornerData.visibility = View.VISIBLE
                noStudentCornerText.text = e.toString()
                Log.w(
                    "Database Get Faculty",
                    "loadFaculty:onCancelled",
                    e
                )
            }
        } else {
            studentDataProgressBar.visibility = View.GONE
            studentDataLayout.visibility = View.GONE
            noStudentCornerData.visibility = View.VISIBLE
            noStudentCornerText.text = "Internet Connection Not Found"
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

    private fun defaultSetting() {
        upEvent = true
        tvUpEv.setBackgroundResource(R.color.nitJ_primary)
        tvUpEv.setTextColor(resources.getColor(R.color.white))
        studentCorner = false
        tvSC.setBackgroundResource(R.color.white)
        tvSC.setTextColor(resources.getColor(R.color.nitJ_primary))
        scholarShip = false
        tvScShip.setBackgroundResource(R.color.white)
        tvScShip.setTextColor(resources.getColor(R.color.nitJ_primary))
        setStudentCornerAdapter(upcomingEventList)
    }

    private fun getSliderData() {
        imageSliderProgressBar.visibility = View.VISIBLE
        if (activity?.let { ConnectionManager().checkConnectivity(it.applicationContext) } == true) {

            try {
                val newRef = databaseRef.ref.child("Slider Image")
                newRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            mSliderItems.clear()
                            var i = 0
                            for (data in dataSnapshot.children.reversed()) {
                                val commonData = data.getValue(CommonData::class.java)
                                val sliderItem = SliderItem(commonData?.url, commonData?.title)
                                mSliderItems.add(sliderItem)
                                i++
                                if (i >= 5) break
                            }
                            sliderView.visibility = View.VISIBLE
                            noSliderData.visibility = View.GONE
                            imageSliderProgressBar.visibility = View.GONE
                        } else {
                            sliderView.visibility = View.GONE
                            noSliderData.visibility = View.VISIBLE
                            imageSliderProgressBar.visibility = View.GONE
                        }
                        setSlider()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        print("${databaseError.toException()}")
                        imageSliderProgressBar.visibility = View.GONE
                        sliderView.visibility = View.GONE
                        noSliderData.visibility = View.VISIBLE
                        noSliderDataText.text = databaseError.toException().toString()
                        Log.w(
                            "Database Get Notice",
                            "Load Notice:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                imageSliderProgressBar.visibility = View.GONE
                sliderView.visibility = View.GONE
                noSliderData.visibility = View.VISIBLE
                noSliderDataText.text = e.toString()
                Log.w(
                    "Database Get Faculty",
                    "loadFaculty:onCancelled",
                    e
                )
            }
        } else {
            imageSliderProgressBar.visibility = View.GONE
            sliderView.visibility = View.GONE
            noSliderData.visibility = View.VISIBLE
            noSliderDataText.text = "Internet Connection Not Found"
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

    private fun setOtherAdapters() {
        // Setting download adapter
        downloadLayoutManager = LinearLayoutManager(activity)
        downloadRecyclerAdapter = DownloadAdapter(requireContext(), downloadsList)
        downloadsRecycler.layoutManager = downloadLayoutManager
        downloadsRecycler.adapter = downloadRecyclerAdapter

        //Setting other link adapter
        otherLayoutManager = LinearLayoutManager(activity)
        otherRecyclerAdapter = OtherAdapter(requireContext(), otherLinksList)
        otherLinksRecycler.layoutManager = otherLayoutManager
        otherLinksRecycler.adapter = otherRecyclerAdapter
    }

    private fun setStudentCornerAdapter(list: ArrayList<HomeRecyclerModel>) {
        scLayoutManager = LinearLayoutManager(activity)
        scRecyclerAdapter = SCAdapter(requireContext(), list)
        studentCornerRecycler.layoutManager = scLayoutManager
        studentCornerRecycler.adapter = scRecyclerAdapter
    }


    private fun setSlider() {
        adapter = SliderAdapter(requireContext(), mSliderItems)
        sliderView.setSliderAdapter(adapter)
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH;
        sliderView.indicatorSelectedColor = Color.WHITE;
        sliderView.indicatorUnselectedColor = Color.GRAY;
        sliderView.scrollTimeInSec = 3; //set scroll delay in seconds :
        sliderView.startAutoCycle()
    }

    private fun findViews(view: View) {
        databaseRef = Firebase.database.reference
        sliderView = view.findViewById(R.id.imageSlider)
        imageSliderProgressBar = view.findViewById(R.id.imageSliderProgressBar)
        noSliderData = view.findViewById(R.id.noSliderData)
        noSliderDataText = view.findViewById(R.id.noSliderDataText)
        container = view.findViewById(R.id.container)
        compendium = view.findViewById(R.id.compendium)
        progressbar = view.findViewById(R.id.progressbar)
        tvUpEv = view.findViewById(R.id.tvUpEv)
        tvSC = view.findViewById(R.id.tvSC)
        tvScShip = view.findViewById(R.id.tvScShip)
        studentCornerRecycler = view.findViewById(R.id.studentCornerRecycler)
        downloadsRecycler = view.findViewById(R.id.downloadsRecycler)
        otherLinksRecycler = view.findViewById(R.id.otherLinksRecycler)
        studentDataLayout = view.findViewById(R.id.studentDataLayout)
        studentDataProgressBar = view.findViewById(R.id.studentDataProgressBar)
        noStudentCornerData = view.findViewById(R.id.noStudentCornerData)
        noStudentCornerText = view.findViewById(R.id.noStudentCornerText)
        download_otherLinkDataLayout = view.findViewById(R.id.download_otherLinkDataLayout)
        downloadDataProgressBar = view.findViewById(R.id.downloadDataProgressBar)
        noDownloadOtherData = view.findViewById(R.id.noDownloadOtherData)
        noDownloadOtherText = view.findViewById(R.id.noDownloadOtherText)
    }

}