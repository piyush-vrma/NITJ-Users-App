package com.nitj.nitj.adapters

import android.content.Context
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

class OtherAdapter(
    private val context: Context,
    private val listItem: ArrayList<HomeRecyclerModel>
) : RecyclerView.Adapter<OtherAdapter.OtherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tile_home_recycler, parent, false)
        return OtherViewHolder(view)
    }

    override fun onBindViewHolder(holder: OtherViewHolder, position: Int) {
        val otherData = listItem[position]

        holder.title.text = otherData.title
        holder.containerView.setOnClickListener {
            Toast.makeText(context, otherData.url, Toast.LENGTH_LONG).show()
            val action = HomeFragmentDirections.actionHomeDestToWebViewFragment(otherData.title,otherData.url)
            it.findNavController().navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class OtherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val containerView: CardView = view.findViewById(R.id.containerView)
    }
}