package com.nitj.nitj.screens.fragments

import android.R.attr.defaultValue
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.nitj.nitj.R
import com.nitj.nitj.screens.MainActivity


class FullImageViewFragment : Fragment() {

    private lateinit var photoView: PhotoView
    private lateinit var title: String
    private lateinit var url: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        val view = inflater.inflate(R.layout.fragment_full_image_view, container, false)
        findView(view)
        if(url == "R.drawable.nitj_about"){
            Glide.with(requireActivity()).load(R.drawable.nitj_about).into(photoView)
        }else{
            Glide.with(requireActivity()).load(url).into(photoView)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    private fun findView(view: View) {
        photoView = view.findViewById(R.id.photoView)

        val bundle = this.arguments
        if (bundle != null) {
            title = bundle.getString("title", "")
            url = bundle.getString("url", "")
        } else {
            val safeArgs: FullImageViewFragmentArgs by navArgs()
            title = safeArgs.title
            url = safeArgs.url
        }

    }

}