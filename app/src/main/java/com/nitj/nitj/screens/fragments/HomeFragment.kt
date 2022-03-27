package com.nitj.nitj.screens.fragments

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.RemoteMessage
import com.nitj.nitj.R
import java.util.*

class HomeFragment : Fragment() {

    private val CHANNEL_ID = "channelId"
    private lateinit var sendNotification: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        findViews(view)
        return view
    }

    fun createNotification1(message: RemoteMessage){
        val deeplink = findNavController().createDeepLink()
            .setDestination(R.id.notice_dest)
            .createPendingIntent()

        val notificationId = Random().nextInt()

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "deeplink", "Deep Links", NotificationManager.IMPORTANCE_HIGH)
            )
        }

        val builder = NotificationCompat.Builder(
            requireContext(), CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(deeplink)
            .setAutoCancel(true)
        notificationManager.notify(notificationId, builder.build())
    }

    fun createNotification(message: RemoteMessage){
        val deeplink = findNavController().createDeepLink()
            .setDestination(R.id.notice_dest)
            .createPendingIntent()
        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random().nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(manager)
        }

        val notification: Notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setContentIntent(deeplink)
            .build()

        manager.notify(notificationId, notification)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(manager: NotificationManager) {
        val channel =
            NotificationChannel(CHANNEL_ID, "ChannelName", NotificationManager.IMPORTANCE_HIGH)
        channel.description = "MyDescription"
        channel.enableLights(true)
        channel.lightColor = Color.WHITE
        manager.createNotificationChannel(channel)
    }

    private fun findViews(view: View) {
        sendNotification = view.findViewById(R.id.sendNotification)
    }

}