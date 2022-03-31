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
import com.nitj.nitj.screens.fragments.DepartmentsFragmentDirections
import com.nitj.nitj.screens.fragments.HomeFragmentDirections

class SCAdapter(
    private val context: Context,
    private val listItem: ArrayList<HomeRecyclerModel>
) : RecyclerView.Adapter<SCAdapter.SCViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SCViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tile_home_recycler, parent, false)
        return SCViewHolder(view)
    }

    override fun onBindViewHolder(holder: SCViewHolder, position: Int) {
        val scData = listItem[position]

        holder.title.text = scData.title
        holder.containerView.setOnClickListener {
            Toast.makeText(context, scData.url, Toast.LENGTH_LONG).show()
            val action = HomeFragmentDirections.actionHomeDestToWebViewFragment(scData.title,scData.url)
            it.findNavController().navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class SCViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val containerView: CardView = view.findViewById(R.id.containerView)
    }
}