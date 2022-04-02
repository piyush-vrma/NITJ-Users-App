package com.nitj.nitj.screens.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.nitj.nitj.R

class AboutFragment : Fragment() {

    private lateinit var nitjRanking: CardView
    private lateinit var nitjWebsite: CardView
    private lateinit var nitjLocation: CardView
    private lateinit var nitjImage: CardView
    private lateinit var piyushLinkedInButton: Button
    private lateinit var piyushGitHubButton: Button
    private lateinit var mayankLinkedInButton: Button
    private lateinit var praanLinkedInButton: Button

    private val nitjRankingUrl = "https://www.nitj.ac.in/index.php/nitj_cinfo/pages/483"
    private val nitjWebsiteUrl = "https://www.nitj.ac.in"
    private val nitjLocationUrl = "geo:0, 0?q=NIT Jalandhar"

    private val piyushLinkedInButtonUrl = "https://www.linkedin.com/in/piyush-verma-152022196"
    private val piyushGitHubButtonUrl = "https://github.com/piyush-vrma"
    private val mayankLinkedInButtonUrl =
        "https://www.linkedin.com/in/mayank-bhagoria-369a561a2/?originalSubdomain=in"
    private val praanLinkedInButtonUrl =
        "https://www.linkedin.com/in/praan-garg-a20aa91b3/?originalSubdomain=in"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        findView(view)
        nitjRanking.setOnClickListener {
            Toast.makeText(context, nitjRankingUrl, Toast.LENGTH_LONG).show()
            val action = AboutFragmentDirections.actionAboutDestToWebViewFragment(
                "NIT Jalandhar Rankings",
                nitjRankingUrl
            )
            findNavController().navigate(action)
        }
        nitjWebsite.setOnClickListener {
            Toast.makeText(context, nitjWebsiteUrl, Toast.LENGTH_LONG).show()
            val action = AboutFragmentDirections.actionAboutDestToWebViewFragment(
                "NIT Jalandhar Website",
                nitjWebsiteUrl
            )
            findNavController().navigate(action)
        }
        nitjLocation.setOnClickListener {
            val uri = Uri.parse(nitjLocationUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent)
        }

        piyushLinkedInButton.setOnClickListener {
            try {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(piyushLinkedInButtonUrl))
                requireActivity().startActivity(i)
            } catch (e: ActivityNotFoundException) {
                val uri = Uri.parse("googlechrome://navigate?url=${piyushLinkedInButtonUrl}")
                val i = Intent(Intent.ACTION_VIEW, uri)
                if (i.resolveActivity(requireActivity().packageManager) == null) {
                    i.data = Uri.parse(piyushLinkedInButtonUrl)
                }
                requireActivity().startActivity(i)
                Toast.makeText(context, "Required App Not Found", Toast.LENGTH_LONG).show()
            }
        }

        piyushGitHubButton.setOnClickListener {
            try {
                val uri = Uri.parse("googlechrome://navigate?url=${piyushGitHubButtonUrl}")
                val i = Intent(Intent.ACTION_VIEW, uri)
                if (i.resolveActivity(requireActivity().packageManager) == null) {
                    i.data = Uri.parse(piyushGitHubButtonUrl)
                }
                requireActivity().startActivity(i)

            } catch (e: ActivityNotFoundException) {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(piyushGitHubButtonUrl))
                requireActivity().startActivity(i)
                Toast.makeText(context, "Required App Not Found", Toast.LENGTH_LONG).show()
            }
        }

        mayankLinkedInButton.setOnClickListener {
            try {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(mayankLinkedInButtonUrl))
                requireActivity().startActivity(i)
            } catch (e: ActivityNotFoundException) {
                val uri = Uri.parse("googlechrome://navigate?url=${mayankLinkedInButtonUrl}")
                val i = Intent(Intent.ACTION_VIEW, uri)
                if (i.resolveActivity(requireActivity().packageManager) == null) {
                    i.data = Uri.parse(mayankLinkedInButtonUrl)
                }
                requireActivity().startActivity(i)
                Toast.makeText(context, "Required App Not Found", Toast.LENGTH_LONG).show()
            }
        }

        praanLinkedInButton.setOnClickListener {
            try {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(praanLinkedInButtonUrl))
                requireActivity().startActivity(i)
            } catch (e: ActivityNotFoundException) {
                val uri = Uri.parse("googlechrome://navigate?url=${praanLinkedInButtonUrl}")
                val i = Intent(Intent.ACTION_VIEW, uri)
                if (i.resolveActivity(requireActivity().packageManager) == null) {
                    i.data = Uri.parse(praanLinkedInButtonUrl)
                }
                requireActivity().startActivity(i)
                Toast.makeText(context, "Required App Not Found", Toast.LENGTH_LONG).show()
            }
        }

        nitjImage.setOnClickListener {
            val action = AboutFragmentDirections.actionAboutDestToFullImageViewFragment("NIT Jalandhar","R.drawable.nitj_about")
            findNavController().navigate(action)
        }

        return view
    }

    private fun findView(view: View) {
        nitjRanking = view.findViewById(R.id.nitjRanking)
        nitjWebsite = view.findViewById(R.id.nitjWebsite)
        nitjLocation = view.findViewById(R.id.nitjLocation)
        nitjImage = view.findViewById(R.id.nitjImage)
        piyushLinkedInButton = view.findViewById(R.id.piyushLinkedInButton)
        piyushGitHubButton = view.findViewById(R.id.piyushGitHubButton)
        mayankLinkedInButton = view.findViewById(R.id.mayankLinkedInButton)
        praanLinkedInButton = view.findViewById(R.id.praanLinkedInButton)
    }


}