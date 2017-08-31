package com.getbooks.android;

import com.getbooks.android.events.Events;
import com.getbooks.android.util.LogUtil;
import com.getbooks.android.util.NotificationUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by marina on 11.07.17.
 */

public class GetbooksFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        LogUtil.log(this, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            LogUtil.log(this, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            LogUtil.log(this, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            LogUtil.log(this, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        NotificationUtil.showNotification(this, remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody());
        EventBus.getDefault().post(new Events.UpDateMainScreen());
    }
}
