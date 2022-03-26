package com.nitj.nitj.firebaseNotifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nitj.nitj.R
import com.nitj.nitj.screens.fragments.HomeFragment

private const val CHANNEL_ID = "my_channel"

private val TAG = "FirebaseService"


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {

        if (message.notification != null) {
           //createNotification(message)
            HomeFragment().createNotification(message)
            Log.e(TAG, "onMessageReceived: ${message.notification!!.title} ${message.notification!!.body} ${message.data["message"]}")
        }
    }

//    private fun createNotification(message: RemoteMessage){
//
//            val deeplink = findNavController().createDeepLink()
//                .setDestination(R.id.notice_dest)
//                .createPendingIntent()
//
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                notificationManager.createNotificationChannel(
//                    NotificationChannel(
//                        "deeplink", "Deep Links", IMPORTANCE_HIGH)
//                )
//            }
//
//            val builder = NotificationCompat.Builder(
//                applicationContext, "deeplink")
//                .setContentTitle(message.data["title"])
//                .setContentText(message.data["message"])
//                .setSmallIcon(R.drawable.logo)
//                .setContentIntent(deeplink)
//                .setAutoCancel(true)
//            notificationManager.notify(0, builder.build())
//
//    }

//    private fun createNotification1(message: RemoteMessage) {
//        val intent = Intent(this, DeleteNotice::class.java)
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val notificationID = Random.nextInt()
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel(notificationManager)
//        }
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
//
//        Log.d(TAG, "${message.data["title"]} ${message.data["message"]}")
//
//        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
//            .setContentTitle(message.data["title"])
//            .setContentText(message.data["message"])
//            .setSmallIcon(R.drawable.logo)
//            .setAutoCancel(true)
//            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
//            .setOnlyAlertOnce(true)
//            .setContentIntent(pendingIntent)
//
//        notificationManager.notify(notificationID, notification.build())
//
//    }
//
//    private fun createNotificationChannel(notificationManager: NotificationManager) {
//        val channelName = "channelName"
//        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH)
//        notificationManager.createNotificationChannel(channel)
//    }
}