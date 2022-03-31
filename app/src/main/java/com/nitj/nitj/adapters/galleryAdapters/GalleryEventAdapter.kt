package com.nitj.nitj.adapters.galleryAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitj.nitj.R
import com.nitj.nitj.models.gallery.GalleryEvent

class GalleryEventAdapter(
    private val context: Context,
    private val listItem: ArrayList<GalleryEvent>,
    private val galleryEventRecycler: RecyclerView
) : RecyclerView.Adapter<GalleryEventAdapter.GalleryEventViewHolder>() {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: GalleryDataAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryEventViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tile_gallery_event, parent, false)
        return GalleryEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryEventViewHolder, position: Int) {
        val galleryEvent = listItem[position]
        holder.eventName.text = galleryEvent.eventName
        layoutManager = GridLayoutManager(context,3)
        recyclerAdapter = GalleryDataAdapter(context, galleryEvent.arrayList)
        holder.galleryDataRecycler.layoutManager = layoutManager
        holder.galleryDataRecycler.adapter = recyclerAdapter

    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class GalleryEventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventName: TextView = view.findViewById(R.id.eventName)
        val galleryDataRecycler: RecyclerView = view.findViewById(R.id.GalleryDataRecycler)
    }

}