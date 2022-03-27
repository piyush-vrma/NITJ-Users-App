package com.nitj.nitj.firebaseNotifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nitj.nitj.R
import com.nitj.nitj.screens.MainActivity
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


private const val CHANNEL_ID = "my_channel"

private val TAG = "FirebaseService"


class FirebaseService : FirebaseMessagingService() {

    private val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        createNotification(message)
    }

    private fun createNotification(message: RemoteMessage) {

        // very very important open any destination fragment using services
        // open fragment by clicking on firebase notification which is triggered by background service
        val deeplink = NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.notice_dest)
            .createPendingIntent()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random().nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(manager)
        }

        val title = message.data["title"]
        val body = message.data["message"]
        val image = message.data["image"]
        val bitmapImage: Bitmap? = getBitmapFromUrl(image)


        val notificationStyle = NotificationCompat.BigPictureStyle()
        notificationStyle.setSummaryText(body)
        notificationStyle.bigPicture(bitmapImage)

        val notification: Notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(this, R.color.nitJ_primary))
            .setSound(soundUri)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(
                longArrayOf(
                    100,
                    200,
                    300,
                    400,
                    500,
                    400,
                    300,
                    200,
                    400
                )
            )
            .setOnlyAlertOnce(true)
            .setLargeIcon(bitmapImage)
            .setStyle(notificationStyle)
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
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(
            100,
            200,
            300,
            400,
            500,
            400,
            300,
            200,
            400
        )
        channel.setBypassDnd(true);
        channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC;
        channel.setShowBadge(true);

        if (soundUri != null) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            channel.setSound(soundUri, audioAttributes)
        }
        manager.createNotificationChannel(channel)
    }

    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection =
                url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}