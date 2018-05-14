package com.app.gcm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent)
        {

            System.out.println("-------------received push notification--------------"+intent);

            ComponentName comp = new ComponentName(context.getPackageName(), GCMNotificationIntentService.class.getName());
            startWakefulService(context, (intent.setComponent(comp)));
            //setResultCode(Activity.RESULT_OK);
        }

/*
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Intent i = new Intent(context, DriverAlertActivity.class);
            Bundle mBundle = intent.getExtras();
            String data = mBundle.toString();
            String key1 = (String) mBundle.get("Key1");
            String key2 = (String) mBundle.get("Key2");
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            String messageType = gcm.getMessageType(intent);
            i.putExtra("extra", messageType);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
        }
    }*/
}
