package com.nitj.nitj.screens.fragments

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.github.barteksc.pdfviewer.PDFView
import com.nitj.nitj.R
import com.nitj.nitj.screens.MainActivity
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


lateinit var pdfView: PDFView
private lateinit var progressDialog: ProgressDialog

class PdfViewFragment : Fragment() {

    private lateinit var url: String
    private lateinit var pdfName: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pdf_view, container, false)
        (activity as MainActivity).setDrawerEnabled(enabled = false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        findView(view)
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

    private fun findView(view: View) {
        pdfView = view.findViewById(R.id.pdfView)
        pdfName = view.findViewById(R.id.pdfName)
        progressDialog = ProgressDialog(context)
        val safeArgs: PdfViewFragmentArgs by navArgs()
        url = safeArgs.fileUrl
        pdfName.text = "PDF : ${safeArgs.fileName}"
        PdfDownload().execute(url)
    }

    private class PdfDownload() : AsyncTask<String, Void, InputStream>() {
        override fun doInBackground(vararg p0: String?): InputStream? {
            var inputStream: InputStream? = null
            try {
                val url: URL = URL(p0[0])
                val urlConnection: HttpURLConnection = url.openConnection() as (HttpURLConnection)
                if (urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }

            } catch (e: Exception) {
                e.printStackTrace();
            }
            return inputStream
        }

        override fun onPreExecute() {
            progressDialog.setTitle("Please Wait")
            progressDialog.setMessage("Loading.....")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun onProgressUpdate(vararg values: Void?) {
            progressDialog.setMessage("Loading.....${values[0]}%")
        }

        override fun onPostExecute(result: InputStream?) {
            pdfView.fromStream(result).load()
            progressDialog.dismiss()
        }

    }



}