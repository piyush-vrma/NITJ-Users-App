package com.nitj.nitj.firebaseNotificationJava;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nitj.nitj.screens.fragments.HomeFragment;

public class FirebaseServiceJava extends FirebaseMessagingService {

    private final String CHANNEL_ID = "channelId";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
      //  new HomeFragment().createNotification(message);


////
////        val deeplink = findNavController().createDeepLink()
////                .setDestination(R.id.notice_dest)
////                .createPendingIntent();
//
//        Intent intent = new Intent(this, NoticeFragment.class);
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        int notificationId = new Random().nextInt();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel(manager);
//        }
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent intent1 = PendingIntent.getActivities(this,0,new Intent[]{intent},PendingIntent.FLAG_ONE_SHOT);
//        Notification notification;
//
//            notification = new NotificationCompat.Builder(this,CHANNEL_ID)
//                    .setContentTitle(message.getData().get("title"))
//                    .setContentText(message.getData().get("message"))
//                    .setSmallIcon(R.drawable.logo)
//                    .setAutoCancel(true)
//                    .setContentIntent(intent1)
//                    .build();
//
//        manager.notify(notificationId,notification);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void createNotificationChannel(NotificationManager manager) {
//        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"ChannelName",NotificationManager.IMPORTANCE_HIGH);
//        channel.setDescription("MyDescription");
//        channel.enableLights(true);
//        channel.setLightColor(Color.WHITE);
//        manager.createNotificationChannel(channel);
//    }
}
