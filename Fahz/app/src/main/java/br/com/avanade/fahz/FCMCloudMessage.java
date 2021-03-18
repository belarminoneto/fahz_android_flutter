package br.com.avanade.fahz;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import br.com.avanade.fahz.util.Constants;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import br.com.avanade.fahz.activities.MainActivity;

public class FCMCloudMessage extends FirebaseMessagingService {
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        LogUtils.debug(LOG_TAG, "Message received");
        super.onMessageReceived(remoteMessage);
        if (remoteMessage != null && remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification(), null);
        }
        if (remoteMessage != null && remoteMessage.getData() != null) {
            sendNotification(null, remoteMessage.getData());
        }
    }

    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PUSH_NOTIFICATION_ACTIVITY, Constants.LIST_MESSAGE_ACTIVITY);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String title;
        String body;
        if(notification != null) {
            title = notification.getTitle();
            body = notification.getTitle();
        }
        else {
            title = data.get(Constants.TITLE_DATA);
            body = data.get(Constants.BODY_DATA);
        }

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(1)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher_logo_notification);
            notificationBuilder.setColor(getResources().getColor(R.color.white01));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher_logo_notification);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(132584687, notificationBuilder.build());
        }
    }
}
