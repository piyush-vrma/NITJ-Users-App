package com.nitj.nitj.adapters

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.nitj.nitj.BuildConfig
import com.nitj.nitj.R
import com.nitj.nitj.models.Ebook.Ebook
import com.nitj.nitj.screens.fragments.EbookFragmentDirections
import com.squareup.picasso.Picasso
import java.io.File


class EBookAdapter(
    private val context: Context,
    private val listItem: ArrayList<Ebook>
) : RecyclerView.Adapter<EBookAdapter.EbookViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EbookViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tile_ebook, parent, false)
        return EbookViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EbookViewHolder, position: Int) {
        val ebookData = listItem[position]
        holder.fileTitle.text = ebookData.ebookTitle
        holder.uploadDate.text = "Upload Date : ${ebookData.date}"
        holder.fileName.text = ebookData.pickedFileName
        var fileExtension = ""
        fileExtension = when {
            ebookData.pickedFileName.contains(".pdf") -> {
                ".pdf"
            }
            ebookData.pickedFileName.contains(".pptx") -> {
                ".pptx"
            }
            else -> {
                ".docx"
            }
        }

        try {
            when {
                ebookData.pickedFileName.contains(".pdf") -> {
                    Picasso.get().load(R.drawable.pdf).error(R.drawable.logo)
                        .into(holder.fileTypeIcon)
                }
                ebookData.pickedFileName.contains(".pptx") -> {
                    Picasso.get().load(R.drawable.ppt).error(R.drawable.logo)
                        .into(holder.fileTypeIcon)
                }
                else -> {
                    Picasso.get().load(R.drawable.word).error(R.drawable.logo)
                        .into(holder.fileTypeIcon)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }

        holder.fileCardTile.setOnClickListener { it ->
            if (fileExtension == ".pdf") {
                val action = EbookFragmentDirections.actionEbookDestToPdfViewDest(
                    ebookData.pickedFileName,
                    ebookData.ebookUrl
                )
                it.findNavController().navigate(action)
            } else if (fileExtension == ".pptx") {

                val fileName = ebookData.pickedFileName
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName
                )
                if (file.exists()) {
                    Toast.makeText(
                        context,
                        "File EXist",
                        Toast.LENGTH_LONG
                    ).show()
                   // openFile(file, "vnd.openxmlformats-officedocument.presentationml.presentation")
                    openFile(file)
                } else {
                    downloadWordOrPPT(
                        ebookData,
                        "vnd.openxmlformats-officedocument.presentationml.presentation"
                    )
                }
            } else {
                val fileName = ebookData.pickedFileName
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName
                )
                if (file.exists()) {
                    Toast.makeText(
                        context,
                        "File EXist",
                        Toast.LENGTH_LONG
                    ).show()
                   // openFile1(file, "vnd.openxmlformats-officedocument.wordprocessingml.document")
                    openFile(file)
                } else {
                    downloadWordOrPPT(
                        ebookData,
                        "vnd.openxmlformats-officedocument.wordprocessingml.document"
                    )
                }
            }
        }

        holder.downloadButton.setOnClickListener {
            Toast.makeText(context, "Downloading...", Toast.LENGTH_LONG).show()
            downloadFile(
                context,
                ebookData.pickedFileName,
                fileExtension,
                DIRECTORY_DOWNLOADS,
                ebookData.ebookUrl
            )
        }

    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class EbookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileTypeIcon: ImageView = view.findViewById(R.id.fileTypeIcon)
        val downloadButton: ImageView = view.findViewById(R.id.downloadButton)
        val fileTitle: TextView = view.findViewById(R.id.fileTitle)
        val fileName: TextView = view.findViewById(R.id.fileName)
        val uploadDate: TextView = view.findViewById(R.id.uploadDate)
        val fileCardTile: MaterialCardView = view.findViewById(R.id.fileCardTile)
    }

    private fun downloadWordOrPPT(ebookData: Ebook, type: String) {
        val dmr = DownloadManager.Request(Uri.parse(ebookData.ebookUrl))
        val fileName = ebookData.pickedFileName
        dmr.setTitle(fileName)
        dmr.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, fileName)
        dmr.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        dmr.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(dmr)
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (file.exists()) {
           // openFile(file, type)
            openFile(file)
        }
    }

    private fun openFile(file: File) {


       val url = Uri.parse(file.path)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(url, "application/msword")
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(url, "application/vnd.ms-powerpoint")
        }
        try {
            if (file.exists()) context.startActivity(
                Intent.createChooser(
                    intent,
                    "Open File"
                )
            ) else Toast.makeText(context, "File is corrupted", Toast.LENGTH_LONG).show()
        } catch (ex: java.lang.Exception) {
            Toast.makeText(
                context, "No Application is found to open this file. The File is saved at${DIRECTORY_DOWNLOADS}".trimIndent(), Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun downloadFile(
        context: Context,
        fileName: String,
        fileExtension: String,
        destinationDirectory: String?,
        url: String?
    ) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(
            context,
            destinationDirectory,
            fileName + fileExtension
        )
        downloadManager.enqueue(request)
    }
}