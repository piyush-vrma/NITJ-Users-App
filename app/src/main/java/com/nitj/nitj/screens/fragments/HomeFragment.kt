package com.nitj.nitj.screens.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitj.nitj.R
import com.nitj.nitj.adapters.DownloadAdapter
import com.nitj.nitj.adapters.OtherAdapter
import com.nitj.nitj.adapters.SCAdapter
import com.nitj.nitj.adapters.SliderAdapter
import com.nitj.nitj.models.HomeRecyclerModel
import com.nitj.nitj.models.SliderItem
import com.nitj.nitj.screens.loginRegisterScreens.LoginFragmentDirections
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView


class HomeFragment : Fragment() {

    private lateinit var sliderView: SliderView
    private lateinit var adapter: SliderAdapter
    private lateinit var container: RelativeLayout
    private lateinit var progressbar: ProgressBar
    private lateinit var compendium: LinearLayout
    private lateinit var tvUpEv: TextView
    private lateinit var tvSC: TextView
    private lateinit var tvScShip: TextView

    private lateinit var studentCornerRecycler: RecyclerView
    lateinit var scLayoutManager: RecyclerView.LayoutManager
    private lateinit var scRecyclerAdapter: SCAdapter
    private val studentCornerList: ArrayList<HomeRecyclerModel> = arrayListOf()
    private val upcomingEventList: ArrayList<HomeRecyclerModel> = arrayListOf()
    private val scholarShipDataList: ArrayList<HomeRecyclerModel> = arrayListOf()

    private lateinit var downloadsRecycler: RecyclerView
    lateinit var downloadLayoutManager: RecyclerView.LayoutManager
    private lateinit var downloadRecyclerAdapter: DownloadAdapter
    private val downloadsList: ArrayList<HomeRecyclerModel> = arrayListOf()

    private lateinit var otherLinksRecycler: RecyclerView
    lateinit var otherLayoutManager: RecyclerView.LayoutManager
    private lateinit var otherRecyclerAdapter: OtherAdapter
    private val otherLinksList: ArrayList<HomeRecyclerModel> = arrayListOf()

    var upEvent = true
    var studentCorner = false
    var scholarShip = false


    var url1 = "https://www.geeksforgeeks.org/wp-content/uploads/gfg_200X200-1.png"
    var tx1 = "Hello Piyush the boss"
    var tx2 = "Hello praan"
    var tx3 = "Hello Mayank"
    var tx4 = "Text checking"
    var url2 = "https://qphs.fs.quoracdn.net/main-qimg-8e203d34a6a56345f86f1a92570557ba.webp"
    var url3 =
        "https://bizzbucket.co/wp-content/uploads/2020/08/Life-in-The-Metro-Blog-Title-22.png"
    var url4 = "https://www.nitj.ac.in//images/cce/nitj3.jpg"

    private val mSliderItems: ArrayList<SliderItem> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        findViews(view)

        inflateLists()

        tvUpEv.setOnClickListener {

            if (!upEvent) {
                setStudentCornerAdapter(upcomingEventList)
                upEvent = true
                tvUpEv.setBackgroundResource(R.color.nitJ_primary)
                tvUpEv.setTextColor(resources.getColor(R.color.white))
                studentCorner = false
                tvSC.setBackgroundResource(R.color.white)
                tvSC.setTextColor(resources.getColor(R.color.nitJ_primary))
                scholarShip = false
                tvScShip.setBackgroundResource(R.color.white)
                tvScShip.setTextColor(resources.getColor(R.color.nitJ_primary))
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
            val action = HomeFragmentDirections.actionHomeDestToWebViewFragment("Compendium","https://www.nitj.ac.in/NITJ-Compendium/")
            findNavController().navigate(action)
        }

        setStudentCornerAdapter(upcomingEventList)
        setOtherAdapters()

        return view
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

    private fun setStudentCornerAdapter(list: ArrayList<HomeRecyclerModel>){
        scLayoutManager = LinearLayoutManager(activity)
        scRecyclerAdapter = SCAdapter(requireContext(), list)
        studentCornerRecycler.layoutManager = scLayoutManager
        studentCornerRecycler.adapter = scRecyclerAdapter
    }

    private fun inflateLists() {
        studentCornerList.clear()
        studentCornerList.add(HomeRecyclerModel(tx1, "https://www.nitj.ac.in/"))
        studentCornerList.add(HomeRecyclerModel(tx2, "https://www.nitj.ac.in/nitj_files/links/Brochure_FDP_Blockchain_210322_76311.pdf"))
        upcomingEventList.clear()
        upcomingEventList.add(HomeRecyclerModel(tx3, url3))
        upcomingEventList.add(HomeRecyclerModel(tx2, url2))
        scholarShipDataList.clear()
        scholarShipDataList.add(HomeRecyclerModel(tx4, url4))
        scholarShipDataList.add(HomeRecyclerModel(tx1, url1))
        downloadsList.clear()
        downloadsList.add(HomeRecyclerModel(tx4, url4))
        downloadsList.add(HomeRecyclerModel(tx1, url1))
        otherLinksList.clear()
        otherLinksList.add(HomeRecyclerModel(tx3, url3))
        otherLinksList.add(HomeRecyclerModel(tx2, url2))
    }

    private fun setSlider() {
        mSliderItems.clear()
        mSliderItems.add(SliderItem(url1, tx1))
        mSliderItems.add(SliderItem(url2, tx2))
        mSliderItems.add(SliderItem(url3, tx3))
        mSliderItems.add(SliderItem(url4, tx3))

        adapter = SliderAdapter(requireContext(), mSliderItems)
        sliderView.setSliderAdapter(adapter)
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH;
        sliderView.indicatorSelectedColor = Color.WHITE;
        sliderView.indicatorUnselectedColor = Color.GRAY;
        sliderView.scrollTimeInSec = 3; //set scroll delay in seconds :
        sliderView.startAutoCycle();

    }

    private fun findViews(view: View) {
        sliderView = view.findViewById(R.id.imageSlider)
        container = view.findViewById(R.id.container)
        compendium = view.findViewById(R.id.compendium)
        progressbar = view.findViewById(R.id.progressbar)
        tvUpEv = view.findViewById(R.id.tvUpEv)
        tvSC = view.findViewById(R.id.tvSC)
        tvScShip = view.findViewById(R.id.tvScShip)
        studentCornerRecycler = view.findViewById(R.id.studentCornerRecycler)
        downloadsRecycler = view.findViewById(R.id.downloadsRecycler)
        otherLinksRecycler = view.findViewById(R.id.otherLinksRecycler)
        setSlider()
    }

}