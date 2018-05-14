package com.app.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.NewTripAlert;
import com.cabily.cabilydriver.PushNotificationAlert;
import com.cabily.cabilydriver.R;
import com.cabily.cabilydriver.Splash;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.widgets.PkDialog;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 */

public class GCMNotificationIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    Context context = GCMNotificationIntentService.this;

    private String driverID = "", driver_image = "",driverName ="", driverEmail = "", driverImage = "", driverRating = "",
            driverLat = "", driverLong = "", driverTime = "",rideID = "", driverMobile = "",
            driverCar_no = "", driverCar_model = "";
    private SessionManager session;
    private String key1 = "", key2 = "", key3 = "",key5="", key8="",key9="",message = "", action = "",key4="",key6="",key7="",key10="",msg1,title,banner;;

    private Boolean isInternetPresent = false;

    private String driver_id="";
    private ServiceRequest mRequest;
    private boolean isAppInfoAvailable = false;

    private ConnectionDetector cd;
    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString(),new JSONObject());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString(),new JSONObject());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
               /* for (int i = 0; i < 3; i++) {
                    Log.d("Messsage Coming... ", "" + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {

                    }
                }*/
                //       Log.e("Message Completed work @ ", "" + SystemClock.elapsedRealtime());

                Log.e("Received: ", "" + extras.toString());
                Log.e("Received: ", "" + extras.toString());

                if (extras != null) {

                   /* try {
                        if (extras.containsKey(Iconstant.DriverID)) {
                            driverID = extras.get(Iconstant.DriverID).toString();
                        }
                        if (extras.containsKey(Iconstant.DriverName)) {
                            driverName = extras.get(Iconstant.DriverName).toString();
                        }
                        if (extras.containsKey(Iconstant.DriverEmail)) {
                            driverEmail = extras.get(Iconstant.DriverEmail).toString();
                        }
                        if (extras.containsKey(Iconstant.DriverImage)) {
                            driverImage = extras.get(Iconstant.DriverImage).toString();
                        }
                        if (extras.containsKey(Iconstant.DriverRating)) {
                            driverRating = extras.get(Iconstant.DriverRating).toString();
                        }
                        if (extras.containsKey(Iconstant.DriverLat)) {
                            driverLat = extras.get(Iconstant.DriverLat).toString();
                        }
                        if (extras.containsKey(Iconstant.DriverLong)) {
                            driverLong = extras.get(Iconstant.DriverLong).toString();
                        }
                        if (extras.containsKey(Iconstant.DriverTime)) {
                            driverTime = extras.get(Iconstant.DriverTime).toString();
                        }
                        if (extras.containsKey(Iconstant.RideID)) {
                            rideID = extras.get(Iconstant.RideID).toString();
                        }
                        if (extras.containsKey(Iconstant.DriverMobile)) {
                            driverMobile = extras.get(Iconstant.DriverMobile).toString();
                        }
                        if (extras.containsKey(Iconstant.DriverCar_No)) {
                            driverCar_no = extras.get(Iconstant.DriverCar_No).toString();
                        }
                        if (extras.containsKey(Iconstant.DriverCar_Model)) {
                            driverCar_model = extras.get(Iconstant.DriverCar_Model).toString();
                        }
                        if (extras.containsKey(Iconstant.Push_Message)) {
                            message = extras.get(Iconstant.Push_Message).toString();
                        }

                        //sendNotification(message.toString());

                        *//*Intent local = new Intent();
                        local.setAction("com.app.pushnotification");
                        local.putExtra("driverID", driverID);
                        local.putExtra("driverName",driverName);
                        local.putExtra("driverEmail",driverEmail);
                        local.putExtra("driverImage",driverImage);
                        local.putExtra("driverRating",driverRating);
                        local.putExtra("driverLat",driverLat);
                        local.putExtra("driverLong",driverLong);
                        local.putExtra("driverTime",driverTime);
                        local.putExtra("rideID",rideID);
                        local.putExtra("driverMobile",driverMobile);
                        local.putExtra("driverCar_no",driverCar_no);
                        local.putExtra("driverCar_model",driverCar_model);
                        local.putExtra("message",message);
                        this.sendBroadcast(local);*//*

                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    try {

                        if (extras.containsKey("action")) {
                            action = extras.get("action").toString();
                        }
                        if (extras.containsKey("message")) {
                            message = extras.get("message").toString();
                        }

                        if (extras.containsKey("key1")) {
                            key1 = extras.get("key1").toString();
                        }
                        if (extras.containsKey("key2")) {
                            key2 = extras.get("key2").toString();
                        }
                        if (extras.containsKey("key3")) {
                            key3 = extras.get("key3").toString();
                        }
                        if (extras.containsKey("key4")) {
                            key4 = extras.get("key4").toString();
                        }
                        if (extras.containsKey("key5")) {
                            key5 = extras.get("key5").toString();
                        }
                        if (extras.containsKey("key6")) {
                            key6 = extras.get("key6").toString();
                        }
                        if (extras.containsKey("key7")) {
                            key7 = extras.get("key7").toString();
                        }
                        if (extras.containsKey("key8")) {
                            key8 = extras.get("key8").toString();
                        }
                        if (extras.containsKey("key9")) {
                            key9 = extras.get("key9").toString();
                        }
                        if (extras.containsKey("key10")) {
                            key10 = extras.get("key10").toString();
                        }

                        if (action.equalsIgnoreCase(ServiceConstant.pushNotification_Ads)) {
                            title = extras.get("key1").toString();
                            msg1 = extras.get("key2").toString();
                            banner = extras.get("key3").toString();

                        }


                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("message", message);
                        jsonObject.put("action", action);
                        jsonObject.put("key1", key1);
                        jsonObject.put("key2", key2);
                        jsonObject.put("key3", key3);
                        jsonObject.put("key4", key4);
                        jsonObject.put("key5", key5);
                        jsonObject.put("key6", key6);
                        jsonObject.put("key7", key7);
                        jsonObject.put("key8", key8);
                        jsonObject.put("key9", key9);
                        jsonObject.put("key10", key10);


                        System.out.println("----------push notification------" + jsonObject);

                        //  if (ServiceConstant.ACTION_TAG_RIDE_REQUEST.equalsIgnoreCase(action)) {
                        sendNotification(message.toString(), jsonObject);
                        if (ServiceConstant.ACTION_TAG_PAYMENT_PAID.equalsIgnoreCase(action)) {

                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.finish.OtpPage");
                            context.sendOrderedBroadcast(broadcastIntent,null);

                            Intent broadcastIntent_payment = new Intent();
                            broadcastIntent_payment.setAction("com.finish.PaymentPage");
                            context.sendOrderedBroadcast(broadcastIntent_payment,null);

                            Intent broadcastIntent_paymenttrip = new Intent();
                            broadcastIntent_paymenttrip.setAction("com.finish.tripsummerydetail");
                            context.sendOrderedBroadcast(broadcastIntent_paymenttrip,null);

                            Intent broadcastIntent_pushnotification = new Intent();
                            broadcastIntent_pushnotification.setAction("com.finish.PushNotificationAlert");
                            context.sendOrderedBroadcast(broadcastIntent_pushnotification,null);

                            Intent intent1 = new Intent(context, PushNotificationAlert.class);
                            intent1.putExtra("Message", jsonObject.getString("message"));
                            intent1.putExtra("Action", jsonObject.getString("action"));
                            intent1.putExtra("RideId", jsonObject.getString("key1"));
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent1);

                        }

                        /*Intent local = new Intent();
                        local.setAction("com.app.pushnotification");
                        local.putExtra("driverID", driverID);
                        local.putExtra("driverName",driverName);
                        local.putExtra("driverEmail",driverEmail);
                        local.putExtra("driverImage",driverImage);
                        local.putExtra("driverRating",driverRating);
                        local.putExtra("driverLat",driverLat);
                        local.putExtra("driverLong",driverLong);
                        local.putExtra("driverTime",driverTime);
                        local.putExtra("rideID",rideID);
                        local.putExtra("driverMobile",driverMobile);
                        local.putExtra("driverCar_no",driverCar_no);
                        local.putExtra("driverCar_model",driverCar_model);
                        local.putExtra("message",message);
                        this.sendBroadcast(local);*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    @SuppressWarnings("deprecation")
    private void sendNotification(String msg, JSONObject data) {
        Intent notificationIntent = null;

        int id=createID();


        session = new SessionManager(GCMNotificationIntentService.this);

       /* if (session.isLoggedIn() && ServiceConstant.ACTION_TAG_RIDE_REQUEST.equalsIgnoreCase(action)) {
            cd = new ConnectionDetector(GCMNotificationIntentService.this);
            isInternetPresent = cd.isConnectingToInternet();


            System.out.println("jai---first");
            if (isInternetPresent) {
                postRequest_applaunch(ServiceConstant.app_launching_url);
                System.out.println("applaunch------------------" + ServiceConstant.app_launching_url);
            } else {
                Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
            }
            System.out.println("jai---third");


            notificationIntent = new Intent(getApplicationContext(), Push_Noti_Request_Alert.class);
            notificationIntent.putExtra(Push_Noti_Request_Alert.EXTRA, data);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        }*/


        session.setNotificationStatus("");


        if (session.isLoggedIn() && action.equalsIgnoreCase(ServiceConstant.ACTION_TAG_RIDE_REQUEST)) {

            notificationIntent = new Intent(GCMNotificationIntentService.this, Splash.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.putExtra("ad", "false");
            notificationIntent.putExtra("push", "true");
            notificationIntent.putExtra("data", data.toString());
        }


        else  if(ServiceConstant.ACTION_TAG_NEW_TRIP.equalsIgnoreCase(action)) {
            System.out.println("---------------inside new trip--------gcm----------");

            //  Intent intent = new Intent(context, NewTripAlert.class);

            cd = new ConnectionDetector(GCMNotificationIntentService.this);
            isInternetPresent = cd.isConnectingToInternet();

            System.out.println("jai---first");
            if (isInternetPresent) {
                postRequest_applaunch(ServiceConstant.app_launching_url);
                System.out.println("applaunch------------------" + ServiceConstant.app_launching_url);
            } else {
                Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
            }
            System.out.println("jai---third");

            notificationIntent= new Intent(context, NewTripAlert.class);
            notificationIntent.putExtra("Message", message);
            notificationIntent.putExtra("Action", action);
            notificationIntent.putExtra("Username", key1);
            notificationIntent.putExtra("Mobilenumber", key3);
            notificationIntent.putExtra("UserImage", key4);
            notificationIntent.putExtra("UserRating", key5);
            notificationIntent.putExtra("RideId", key6);
            notificationIntent.putExtra("UserPickuplocation", key7);
            notificationIntent.putExtra("UserPickupTime", key10);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            //   startActivity(intent);
        }
        else  if(ServiceConstant.pushNotification_Ads.equalsIgnoreCase(action)) {
            notificationIntent = new Intent(GCMNotificationIntentService.this, Splash.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.putExtra("title", title);
            notificationIntent.putExtra("msg", msg1);
            notificationIntent.putExtra("banner", banner);
            notificationIntent.putExtra("ad", "true");
            notificationIntent.putExtra("push", "false");
        }
        else {
            notificationIntent = new Intent(GCMNotificationIntentService.this, Splash.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.putExtra("ad", "false");
            notificationIntent.putExtra("push", "false");
        }

        PendingIntent contentIntent = PendingIntent.getActivity(GCMNotificationIntentService.this, id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) GCMNotificationIntentService.this.getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = GCMNotificationIntentService.this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(GCMNotificationIntentService.this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.pushlogodri)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.applogo))
                .setTicker(msg)
                .setColor(Color.RED)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setLights(0xffff0000, 100, 2000)
                .setPriority(Notification.DEFAULT_SOUND)
                .setContentText(msg);

        Notification n = builder.getNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int smallIconViewId = getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());

            if (smallIconViewId != 0) {
                if (n.contentView != null)
                    n.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                if (n.headsUpContentView != null)
                    n.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                if (n.bigContentView != null)
                    n.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
            }
        }

        n.defaults |= Notification.DEFAULT_ALL;
        nm.notify(NOTIFICATION_ID, n);

        if (action.equalsIgnoreCase(ServiceConstant.ACTION_TAG_RIDE_REQUEST)){
            removeNotification(NOTIFICATION_ID,nm);
        }

    }

    private void removeNotification(final int id, final NotificationManager notificationManager) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(id);
            }
        }, 20000 );
    }
    /* private void sendNotification(String msg) {
         Intent notificationIntent = null;
         notificationIntent = new Intent(GCMNotificationIntentService.this, Splash.class);
         notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


         PendingIntent contentIntent = PendingIntent.getActivity(GCMNotificationIntentService.this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

         NotificationManager nm = (NotificationManager) GCMNotificationIntentService.this.getSystemService(Context.NOTIFICATION_SERVICE);

         Resources res = GCMNotificationIntentService.this.getResources();
         NotificationCompat.Builder builder = new NotificationCompat.Builder(GCMNotificationIntentService.this);
         builder.setContentIntent(contentIntent)
                 .setSmallIcon(R.drawable.app_logo)
                 .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.app_logo))
                 .setTicker(msg)
                 .setWhen(System.currentTimeMillis())
                 .setAutoCancel(true)
                 .setContentTitle("Cabily")
                 .setLights(0xffff0000, 100, 2000)
                 .setPriority(Notification.DEFAULT_SOUND)
                 .setContentText(msg);

         Notification n = builder.getNotification();

         n.defaults |= Notification.DEFAULT_ALL;
         nm.notify(0, n);

     } GCMIntentManager  notificationManager ;

     public GCMNotificationIntentService() {
         super("GCMNotificationIntentService");
     }

     public GCMNotificationIntentService(String name) {
         super(name);
     }

     @Override
     protected void onHandleIntent(Intent intent) {
         Bundle extras = intent.getExtras();
         GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
         String messageType = gcm.getMessageType(intent);
         notificationManager = new GCMIntentManager(getApplicationContext());
         if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
             notificationManager.sendNotification(extras.toString());
         }
         GcmBroadcastReceiver.completeWakefulIntent(intent);
     }
 */

    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }

    private void postRequest_applaunch(String Url) {

        System.out.println("-------------Splash App Information Url----------------" + Url);

        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "driver");
        jsonParams.put("id", driver_id);
        mRequest = new ServiceRequest(GCMNotificationIntentService.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Splash App Information Response----------------" + response);

                String server_mode="",site_mode="",site_string="",site_url="",app_identity_name="",Language_code="";

                String Str_status = "", sContact_mail = "", sCustomerServiceNumber = "", sSiteUrl = "", sXmppHostUrl = "", sHostName = "", sFacebookId = "", sGooglePlusId = "", sPhoneMasking = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            JSONObject info_object = response_object.getJSONObject("info");
                            if (info_object.length() > 0) {
                                sContact_mail = info_object.getString("site_contact_mail");
                                sCustomerServiceNumber = info_object.getString("customer_service_number");
                                sSiteUrl = info_object.getString("site_url");
                                sXmppHostUrl = info_object.getString("xmpp_host_url");
                                sHostName = info_object.getString("xmpp_host_name");
                              /*sFacebookId = info_object.getString("facebook_app_id");
                                sGooglePlusId = info_object.getString("google_plus_app_id");
                                sPhoneMasking = info_object.getString("phone_masking_status");*/

                                app_identity_name = info_object.getString("app_identity_name");
                                server_mode = info_object.getString("server_mode");
                                site_mode = info_object.getString("site_mode");
                                site_string = info_object.getString("site_mode_string");
                                site_url = info_object.getString("site_url");
                               /* Language_code="ta";*/
                                driver_image = info_object.getString("driver_image");
                                driverName = info_object.getString("driver_name");
                                Language_code= info_object.getString("lang_code");
                                isAppInfoAvailable = true;
                            } else {
                                isAppInfoAvailable = false;
                            }

                           /* sPendingRideId= response_object.getString("pending_rideid");
                            sRatingStatus= response_object.getString("ride_status");*/

                        } else {
                            isAppInfoAvailable = false;
                        }
                    } else {
                        isAppInfoAvailable = false;
                    }
                    if (Str_status.equalsIgnoreCase("1") && isAppInfoAvailable) {

                        HashMap<String, String> language = session.getLanaguage();
                        Locale locale = null;

                        switch (Language_code){

                            case "en":
                                locale = new Locale("en");
                                session.setlamguage("en","en");
                                //  System.out.println("========English Language========"+language_change.getSelectedItem().toString()+"\t\ten");
                                //  Intent in=new Intent(ProfilePage.this,NavigationDrawer.class);
                                //   finish();
                                //  startActivity(in);

//                        Intent bi = new Intent();
//                        bi.setAction("homepage");
//                        sendBroadcast(bi);
//                        finish();

                                break;
                            case "es":
                                locale = new Locale("es");
                                session.setlamguage("es","es");
                                //     System.out.println("========Arabic Language========"+language_change.getSelectedItem().toString()+"\t\tar");
                                //     Intent i=new Intent(ProfilePage.this,NavigationDrawer.class);
                                //     finish();
                                //     startActivity(i);

//                        Intent bii = new Intent();
//                        bii.setAction("homepage");
//                        sendBroadcast(bii);
//                        finish();
                                break;

                            default:
                                locale = new Locale("en");
                                session.setlamguage("en","en");
                                break;
                        }

                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());

                        session.setXmpp(sXmppHostUrl,sHostName);
                        session.setAgent(app_identity_name);
                        session.setdriver_image(driver_image);
                        session.setdriverNameUpdate(driverName);
                        System.out.println("jai---second");


                    } else {
                        Toast.makeText(context, "BAD URL", Toast.LENGTH_SHORT).show();

                        /*mInfoDialog = new PkDialogWithoutButton(Splash.this);
                        mInfoDialog.setDialogTitle("");
                        mInfoDialog.setDialogMessage("");
                        mInfoDialog.show();*/
                    }
/*                    if (Str_status.equalsIgnoreCase("1") && isAppInfoAvailable) {

                        session = new SessionManager(Splash.this);
                        if (session.isLoggedIn()) {
                            Intent i = new Intent(Splash.this, DashBoardDriver.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            Intent i = new Intent(Splash.this, HomePage.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    } else {

                        Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.fetchdatatoast));
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {
                Toast.makeText(context, ServiceConstant.MAIN_URL, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(GCMNotificationIntentService.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
}