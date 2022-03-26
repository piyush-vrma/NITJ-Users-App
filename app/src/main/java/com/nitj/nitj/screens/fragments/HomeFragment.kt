package com.nitj.nitj.screens.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.RemoteMessage
import com.nitj.nitj.R

class HomeFragment : Fragment() {

    private lateinit var sendNotification: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        findViews(view)
        sendNotif()
        return view
    }

    fun createNotification(message: RemoteMessage){
        val deeplink = findNavController().createDeepLink()
            .setDestination(R.id.notice_dest)
            .createPendingIntent()

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "deeplink", "Deep Links", NotificationManager.IMPORTANCE_HIGH)
            )
        }

        val builder = NotificationCompat.Builder(
            requireContext(), "deeplink")
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(deeplink)
            .setAutoCancel(true)
        notificationManager.notify(0, builder.build())
    }

     fun sendNotif(){
        sendNotification.setOnClickListener {

        }
    }

    private fun findViews(view: View) {
        sendNotification = view.findViewById(R.id.sendNotification)
    }


}