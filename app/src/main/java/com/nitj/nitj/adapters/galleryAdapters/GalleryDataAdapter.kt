package com.nitj.nitj.adapters.galleryAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nitj.nitj.R
import com.nitj.nitj.models.gallery.GalleryData
import com.squareup.picasso.Picasso

class GalleryDataAdapter(
    private val context: Context,
    private val listItem: ArrayList<GalleryData>
) : RecyclerView.Adapter<GalleryDataAdapter.GalleryDataViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GalleryDataViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tile_gallery_data, parent, false)
        return GalleryDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryDataAdapter.GalleryDataViewHolder, position: Int) {
        val galleryData = listItem[position]
        try {
            Picasso.get().load(galleryData.imageUrl).error(R.drawable.logo)
                .into(holder.eventImage)
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class GalleryDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageCardView: CardView = view.findViewById(R.id.imageCardView)
        val eventImage: ImageView = view.findViewById(R.id.eventImage)
    }
}