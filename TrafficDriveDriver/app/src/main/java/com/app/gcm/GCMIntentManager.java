package com.app.gcm;

public class GCMIntentManager {













   /* private static final int NOTIFICATION_ID = 1;
    private Context mContext;
    private String TITLE ="Cabily Driver Alert";

	public GCMIntentManager(Context context) {
		this.mContext = context;
	}

	public void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, DriverAlertActivity.class), 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(""+TITLE)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }*/
}
