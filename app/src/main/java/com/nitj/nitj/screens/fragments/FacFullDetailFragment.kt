package com.nitj.nitj.screens.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.nitj.nitj.R
import com.nitj.nitj.screens.MainActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class FacFullDetailFragment : Fragment() {

    private lateinit var facImage: CircleImageView
    private lateinit var facName: TextView
    private lateinit var facDesignation: TextView
    private lateinit var facEmail: TextView
    private lateinit var departmentHeader: TextView
    private lateinit var departmentName: TextView
    private lateinit var eduHeader: TextView
    private lateinit var edu1: TextView
    private lateinit var edu2: TextView
    private lateinit var edu3: TextView
    private lateinit var researchHeader: TextView
    private lateinit var researchContent: TextView
    private lateinit var faxHeader: TextView
    private lateinit var fax: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fac_full_detail, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        findView(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        view?.let { findView(it) }
        (activity as MainActivity).closeDrawer()
        (activity as MainActivity).setDrawerEnabled(enabled = false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).setDrawerEnabled(enabled = false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    private fun getValues() {
        val safeArgs: FacFullDetailFragmentArgs by navArgs()
        val profilePic = safeArgs.facImage
        try {
            Picasso.get().load(profilePic).error(R.drawable.logo).into(facImage)
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }
        facName.text = safeArgs.facName
        facDesignation.text = safeArgs.designation
        departmentName.text = safeArgs.department
        facEmail.text = safeArgs.email
        edu1.text = safeArgs.qualification1
        edu2.text = safeArgs.qualification2
        edu3.text = safeArgs.qualification3
        fax.text = safeArgs.fax
        researchContent.text = safeArgs.researchInterest

        val str = SpannableString(facEmail.text)
        str.setSpan(UnderlineSpan(), 0, facEmail.text.length, 0)
        facEmail.text = str
    }

    private fun getValues1() {
        val profilePic = arguments?.getString("profileImage")
        try {
            Picasso.get().load(profilePic).error(R.drawable.logo).into(facImage)
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }
        facName.text = arguments?.getString("name")
        facDesignation.text = arguments?.getString("designation")
        departmentName.text = arguments?.getString("department")
        facEmail.text = arguments?.getString("emailId")
        edu1.text = arguments?.getString("qualification1")
        edu2.text = arguments?.getString("qualification2")
        edu3.text = arguments?.getString("qualification3")
        fax.text = arguments?.getString("fax")
        researchContent.text = arguments?.getString("researchInterests")

        val str = SpannableString(facEmail.text)
        str.setSpan(UnderlineSpan(), 0, facEmail.text.length, 0)
        facEmail.text = str
    }

    private fun findView(view: View) {
        facImage = view.findViewById(R.id.facImage)
        facName = view.findViewById(R.id.facName)
        facDesignation = view.findViewById(R.id.facDesignation)
        facEmail = view.findViewById(R.id.facEmail)
        departmentHeader = view.findViewById(R.id.departmentHeader)
        departmentName = view.findViewById(R.id.departmentName)
        eduHeader = view.findViewById(R.id.eduHeader)
        edu1 = view.findViewById(R.id.edu1)
        edu2 = view.findViewById(R.id.edu2)
        edu3 = view.findViewById(R.id.edu3)
        researchHeader = view.findViewById(R.id.researchHeader)
        researchContent = view.findViewById(R.id.researchContent)
        faxHeader = view.findViewById(R.id.faxHeader)
        fax = view.findViewById(R.id.fax)

        var str = SpannableString(departmentHeader.text)
        str.setSpan(UnderlineSpan(), 0, departmentHeader.text.length, 0)
        departmentHeader.text = str

        str = SpannableString(eduHeader.text)
        str.setSpan(UnderlineSpan(), 0, eduHeader.text.length, 0)
        eduHeader.text = str

        str = SpannableString(researchHeader.text)
        str.setSpan(UnderlineSpan(), 0, researchHeader.text.length, 0)
        researchHeader.text = str

        str = SpannableString(faxHeader.text)
        str.setSpan(UnderlineSpan(), 0, faxHeader.text.length, 0)
        faxHeader.text = str

        facEmail.setOnClickListener {
            val arrayOfString = arrayOf(facEmail.text.toString())
            openEmail(arrayOfString)
        }
        getValues()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openEmail(mail: Array<String>) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, mail)
        }

        if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
            requireActivity().startActivity(emailIntent)
        } else {
            Toast.makeText(context, "Required App is not installed!!", Toast.LENGTH_LONG).show()
        }
    }

}