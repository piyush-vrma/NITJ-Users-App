package com.nitj.nitj.screens.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.nitj.nitj.R
import com.nitj.nitj.screens.MainActivity


class WebViewFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var title: String
    private lateinit var url: String
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_web_view, container, false)
        (activity as MainActivity).setDrawerEnabled(enabled = false)
        findView(view)
        startWebView(url)
        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setDrawerEnabled(enabled = false)
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).setDrawerEnabled(enabled = true)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView(url: String) {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        webView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        webView.settings.builtInZoomControls = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()
                }
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Error:$description", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        if (url.contains(".pdf")) {
            webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$url")
        } else {
            webView.loadUrl(url)
        }

        webView.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action === KeyEvent.ACTION_DOWN) {
                    val webView = v as WebView
                    when (keyCode) {
                        KeyEvent.KEYCODE_BACK -> if (webView.canGoBack()) {
                            webView.goBack()
                            return true
                        }
                    }
                }
                return false
            }
        })
    }


    private fun findView(view: View) {
        webView = view.findViewById(R.id.webView)
        val safeArgs: WebViewFragmentArgs by navArgs()
        title = safeArgs.title
        url = safeArgs.url
    }

}