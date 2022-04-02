package com.nitj.nitj.adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nitj.nitj.R
import com.nitj.nitj.models.HomeRecyclerModel
import com.nitj.nitj.screens.fragments.HomeFragmentDirections


class DownloadAdapter(
    private val context: Context,
    private val listItem: ArrayList<HomeRecyclerModel>
) : RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tile_home_recycler, parent, false)
        return DownloadViewHolder(view)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        val downloadData = listItem[position]

        holder.title.text = downloadData.title
        holder.containerView.setOnClickListener {
            if (downloadData.url.contains(".docx") || downloadData.url.contains(".pptx")) {
                try {
                    val uri = Uri.parse("googlechrome://navigate?url=${downloadData.url}")
                    val i = Intent(Intent.ACTION_VIEW, uri)
                    if (i.resolveActivity(context.packageManager) == null) {
                        i.data = Uri.parse(downloadData.url)
                    }
                    context.startActivity(i)
                } catch (e: ActivityNotFoundException) {
                    // Chrome is not installed
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(downloadData.url))
                    context.startActivity(i)
                }
            }  else{
            Toast.makeText(context, downloadData.url, Toast.LENGTH_LONG).show()
            val action = HomeFragmentDirections.actionHomeDestToWebViewFragment(downloadData.title,downloadData.url)
            it.findNavController().navigate(action)
        }

        }

    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class DownloadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val containerView: CardView = view.findViewById(R.id.containerView)
    }
}