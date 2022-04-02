package com.nitj.nitj.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nitj.nitj.R
import com.nitj.nitj.models.NoticeData
import com.nitj.nitj.screens.fragments.HomeFragmentDirections
import com.nitj.nitj.screens.fragments.NoticeFragmentDirections
import com.squareup.picasso.Picasso

class NoticeAdapter(
    private val context: Context,
    private val listItem: ArrayList<NoticeData>
) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tile_notice, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val noticeData = listItem[position]
        try {
            Picasso.get().load(noticeData.image).error(R.drawable.logo)
                .into(holder.noticeImage)
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }
        holder.noticeDate.text = "Date : ${noticeData.date}"
        holder.noticeTime.text = "Time : ${noticeData.time}"
        holder.noticeTitle.text = noticeData.title

        holder.noticeImage.setOnClickListener {
            val action = NoticeFragmentDirections.actionNoticeDestToFullImageViewFragment(noticeData.title,noticeData.image)
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noticeImage: ImageView = view.findViewById(R.id.noticeImage)
        val noticeTitle: TextView = view.findViewById(R.id.noticeTitle)
        val noticeDate: TextView = view.findViewById(R.id.noticeDate)
        val noticeTime: TextView = view.findViewById(R.id.noticeTime)
    }
}