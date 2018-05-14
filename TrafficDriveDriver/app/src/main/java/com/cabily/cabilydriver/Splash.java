package com.cabily.cabilydriver;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.app.service.UpdateLocationService;
import com.app.xmpp.XmppService;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.GPSTracker;
import com.cabily.cabilydriver.Utils.IdentifyAppKilled;
import com.cabily.cabilydriver.Utils.PkDialogWithoutButton;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.Utils.SessionManager_Applaunch;
import com.cabily.cabilydriver.widgets.PkDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by user88 on 6/3/2016.
 */
public class Splash extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    Context context;
    private ServiceRequest mRequest;
    String Strstatus = "",Str_phonemasking_status="";
    private ProgressBar progress;

    String sPendingRideId="",sRatingStatus="";
    private boolean isAppInfoAvailable = false;
    PkDialogWithoutButton mInfoDialog;
    private SessionManager_Applaunch session_launch;
    public static String PHONEMASKINGSTATUS = "";
    private SessionManager session;
    private String driver_id="",currentVersion = "", sLatitude = "", sLongitude = "";
    private String server_mode,site_mode,site_string,site_url,app_identity_name="",Language_code="",driver_image = "",driverName ="";
    private final static int REQUEST_LOCATION = 199;
    private GPSTracker gps;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private PendingResult<LocationSettingsResult> result;
    private String pushtype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        context = getApplicationContext();
        cd = new ConnectionDetector(Splash.this);
        isInternetPresent = cd.isConnectingToInternet();
        session =  new SessionManager(Splash.this);
        gps = new GPSTracker(getApplicationContext());

        mGoogleApiClient = new GoogleApiClient.Builder(Splash.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        session_launch  = new SessionManager_Applaunch(Splash.this);
        progress = (ProgressBar)findViewById(R.id.progress_splash);

        cd = new ConnectionDetector(Splash.this);
        isInternetPresent = cd.isConnectingToInternet();

        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);

        try {
            currentVersion = Splash.this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Intent i = getIntent();
        if (i != null) {
            if(i.hasExtra("ad"))
            {
                String type = i.getStringExtra("ad");
                pushtype = i.getStringExtra("push");
                if(type.equalsIgnoreCase("true")) {
                    String title = i.getStringExtra("title");
                    String msg = i.getStringExtra("msg");
                    String banner = i.getStringExtra("banner");
                    session.setADS(true);
                    session.setAds(title, msg, banner);
                    System.out.println("-ads jai-------title"+title);
                }
                if(pushtype.equalsIgnoreCase("true")) {
                    String data = i.getStringExtra("data");
                    session.setNotificationStatus(ServiceConstant.ACTION_TAG_RIDE_REQUEST);
                    session.setDriverAlertData(data.toString());
                }
            }



        }



        //--------Start Service to identify app killed or not---------
      /*  startService(new Intent(getBaseContext(), IdentifyAppKilled.class));

        Intent alarmIntent = new Intent(this, UpdateLocationService.class);
        alarmIntent.putExtra("Mode","available");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);*/




       /* HashMap<String, String> language = session.getLanaguage();
        Locale locale = null;

        switch (language.get(SessionManager.KEY_Language)){

            case "English":
                locale = new Locale("en");
                // session.setlamguage("en",language_change.getSelectedItem().toString());
                //  System.out.println("========English Language========"+language_change.getSelectedItem().toString()+"\t\ten");
                //  Intent in=new Intent(ProfilePage.this,NavigationDrawer.class);
                //   finish();
                //  startActivity(in);

//                        Intent bi = new Intent();
//                        bi.setAction("homepage");
//                        sendBroadcast(bi);
//                        finish();

                break;
            case "Tamil":
                locale = new Locale("ta");
                //      session.setlamguage("Ar",language_change.getSelectedItem().toString());
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
                break;
        }

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());
*/
        if (isInternetPresent) {



            GetVersionCode versionCode=new GetVersionCode();
            versionCode.execute();



            System.out.println("applaunch------------------" + ServiceConstant.app_launching_url);



        } else {
            Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
        }


        if (gps.isgpsenabled() && gps.canGetLocation()) {
            //do nothing

            sLatitude = String.valueOf(gps.getLatitude());
            sLongitude = String.valueOf(gps.getLongitude());
        } else {
            enableGpsService();
        }


       /* Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(Splash.this,HomePage.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();*/
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    //    finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //Code to check playStore update version
    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = "";
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + Splash.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);

            if(!onlineVersion.equals("")) {
                if (onlineVersion != null && !onlineVersion.isEmpty()) {
                    if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                        if (Splash.this != null && !Splash.this.isFinishing()) {

                            AlertPlayStore(getResources().getString(R.string.app_name), "There is newer version of this application available, click OK to upgrade now?");
                        }
                        //                Alert(getResources().getString(R.string.app_name), "There is newer version of this application available, click OK to upgrade now?");
                    } else {
                        postRequest_applaunch(ServiceConstant.app_launching_url);



                       /* if (Build.VERSION.SDK_INT >= 23) {
                            // Marshmallow+
                            if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() || !checkWriteExternalStoragePermission()) {
                                requestPermission();
                            } else {
                                setLocation();
                            }
                        } else {
                            setLocation();
                        }*/
                    }
                }

            }
            else {
                postRequest_applaunch(ServiceConstant.app_launching_url);
               /* if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() || !checkWriteExternalStoragePermission()) {
                        requestPermission();
                    } else {
                        setLocation();
                    }
                } else {
                    setLocation();
                }*/
            }
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }
    }



    private void AlertPlayStore(String title, String alert) {
        try {
            final PkDialog mDialog = new PkDialog(Splash.this);
            mDialog.setDialogTitle(title);
            mDialog.setDialogMessage(alert);
            mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    finish();
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
            mDialog.setNegativeButton(getResources().getString(R.string.lbel_begintrip_label_cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    finish();
                }
            });
            mDialog.show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(Splash.this);
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


    //-----------------------Post Request-----------------
/*    private void postRequest_applaunch(String Url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        mRequest = new ServiceRequest(SplashPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                System.out.println("applaunch-----------------" + response);
                Log.e("launch",response);
                try{
                    JSONObject object = new JSONObject(response);
                    Strstatus  = object.getString("status");

                    if (Strstatus.equalsIgnoreCase("1")){

                        JSONObject jobject = object.getJSONObject("response");
                        JSONObject object_launch = jobject.getJSONObject("info");

                        Str_phonemasking_status = object_launch.getString("phone_masking_status");
                    }

                    if (Strstatus.equalsIgnoreCase("1")){
                        session_launch.createSessionPhoneMask(Str_phonemasking_status);
                    }else{
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onErrorListener() {

            }

        });
    }*/



    //Enabling Gps Service
    private void enableGpsService() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Splash.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                session = new SessionManager(getApplicationContext());
                                gps = new GPSTracker(Splash.this);

                                HashMap<String, String> user = session.getUserDetails();
                                driver_id = user.get(SessionManager.KEY_DRIVERID);

                                sLatitude = String.valueOf(gps.getLatitude());
                                sLongitude = String.valueOf(gps.getLongitude());


                                postRequest_applaunch(ServiceConstant.app_launching_url);
                        /*ChatingService.startDriverAction(HomePage.this);
                        Intent i = new Intent(getApplicationContext(), NavigationDrawer.class);
                        startActivity(i);
                        finish();*/


                                //  postRequest_AppInformation(Iconstant.app_info_url);
                                //   postRequest_SetUserLocation(Iconstant.setUserLocation);

                            }
                        }, 2000);
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        finish();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();


        System.out.println("-------------jai serivce----------------");
        //--------Start Service to identify app killed or not---------
        startService(new Intent(getBaseContext(), IdentifyAppKilled.class));

        Intent alarmIntent = new Intent(this, UpdateLocationService.class);
        alarmIntent.putExtra("Mode","available");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);


    }

    //-----------------------App Information Post Request-----------------
    private void postRequest_applaunch(String Url) {

        System.out.println("-------------Splash App Information Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "driver");
        jsonParams.put("id", driver_id);

        mRequest = new ServiceRequest(Splash.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Splash App Information Response----------------" + response);

                String Str_status = "", user_img = "",About_Content="" ,customer_service_number="",customer_service_address="" ,sCustomerServiceNumber = "", sSiteUrl = "", sXmppHostUrl = "", sHostName = "", sFacebookId = "", sGooglePlusId = "", sPhoneMasking = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            JSONObject info_object = response_object.getJSONObject("info");
                            if (info_object.length() > 0) {

                                sCustomerServiceNumber = info_object.getString("customer_service_number");
                                sSiteUrl = info_object.getString("site_url");
                                sXmppHostUrl = info_object.getString("xmpp_host_url");
                                sHostName = info_object.getString("xmpp_host_name");
                              /*  sFacebookId = info_object.getString("facebook_app_id");
                                sGooglePlusId = info_object.getString("google_plus_app_id");
                                sPhoneMasking = info_object.getString("phone_masking_status");*/
                                app_identity_name = info_object.getString("app_identity_name");
                                server_mode = info_object.getString("server_mode");
                                user_img = info_object.getString("user_image");
                                site_mode = info_object.getString("site_mode");
                                site_string = info_object.getString("site_mode_string");
                                site_url = info_object.getString("site_url");
                                About_Content= info_object.getString("about_content");
                                driver_image = info_object.getString("driver_image");
                                driverName = info_object.getString("driver_name");
                                Language_code= info_object.getString("lang_code");
                                if(info_object.has("customer_service_number")) {
                                    customer_service_number = info_object.getString("customer_service_number");
                                }
                                if(info_object.has("site_contact_address")) {
                                    customer_service_address = info_object.getString("site_contact_address");
                                }
                                if (info_object.has("phone_masking_status")) {
                                    String phoneMaskingStatus = info_object.getString("phone_masking_status");
                                    PHONEMASKINGSTATUS = phoneMaskingStatus;
                                    // set phone masking
                                    session.setKeyPhoneMaskingStatus(PHONEMASKINGSTATUS);
                                    System.out.println("=====>>>===PHONEMASKINGSTATUS ==========>>>>> " + PHONEMASKINGSTATUS);
                                }


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

                            case "ta":
                                locale = new Locale("ta");
                                session.setlamguage("ta","ta");
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
                        session.setDriver_image(driver_image);
                        session.setcustomerdetail(customer_service_number,customer_service_address);
                        session.setXmpp(sXmppHostUrl,sHostName);
                        session.setaboutus(About_Content,sSiteUrl);
                        session.setAgent(app_identity_name);
                        session.setdriver_image(driver_image);
                        session.setdriverNameUpdate(driverName);

                        if(server_mode.equalsIgnoreCase("0"))
                        {
                            Toast.makeText(context, site_url, Toast.LENGTH_SHORT).show();
                        }

                        if (gps.isgpsenabled() && gps.canGetLocation()) {
                            sLatitude = String.valueOf(gps.getLatitude());
                            sLongitude = String.valueOf(gps.getLongitude());

                            if (session.isLoggedIn()) {
                                postRequest_SetUserLocation(ServiceConstant.UPDATE_CURRENT_LOCATION);
                            } else {
                                if(site_mode.equalsIgnoreCase("development"))
                                {
                                    mInfoDialog = new PkDialogWithoutButton(Splash.this);
                                    mInfoDialog.setDialogTitle("ALERT");
                                    mInfoDialog.setDialogMessage(site_string);
                                    mInfoDialog.show();
                                }
                                else
                                {
                                    Intent intent = new Intent(Splash.this,HomePage.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }

                        } else {
                            enableGpsService();
                        }



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


    private void postRequest_SetUserLocation(String Url) {

        System.out.println("----------sLatitude----------" + sLatitude);
        System.out.println("----------sLongitude----------" + sLongitude);
        System.out.println("----------driver_id----------" + driver_id);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);
        jsonParams.put("latitude", sLatitude);
        jsonParams.put("longitude", sLongitude);

        System.out.println("-------------Splash UserLocation Url----------------" + Url);
        mRequest = new ServiceRequest(Splash.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Splash UserLocation Response----------------" + response);

                String Str_status = "", sCategoryID = "", sTripProgress = "", sRideId = "", sRideStage = "";
                try {
                    JSONObject object = new JSONObject(response);
                  /*  Str_status = object.getString("status");
                    sCategoryID = object.getString("category_id");
                    sTripProgress = object.getString("trip_in_progress");
                    sRideId = object.getString("ride_id");
                    sRideStage = object.getString("ride_stage");*/

                   /* if (Str_status.equalsIgnoreCase("1")) {
                        session.setCategoryID(sCategoryID);
                        session.setRidePendingStatus(sTripProgress,sRideId,sRideStage);
                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //FEB 1
                        /*if (!ChatingService.isConnected) {
                            ChatingService.startDriverAction(getActivity());
                        }*/


                /*Intent i = new Intent(getApplicationContext(), NavigationDrawer.class);
                startActivity(i);
                finish();*/

                if (!isMyServiceRunning(XmppService.class)) {
                    startService(new Intent(Splash.this, XmppService.class));
                }
               else {
                    stopService(new Intent(Splash.this, XmppService.class));
                    startService(new Intent(Splash.this, XmppService.class));
                }

                session.setXmppServiceState("online");

                Intent i = new Intent(getApplicationContext(), NavigationDrawerNew.class);
                if ("true".equalsIgnoreCase(pushtype))
                {
                    i.putExtra("type","push");
                }
                startActivity(i);
                finish();

            }

            @Override
            public void onErrorListener() {

                if (!isMyServiceRunning(XmppService.class)) {
                    startService(new Intent(Splash.this, XmppService.class));
                }

                else {
                    stopService(new Intent(Splash.this, XmppService.class));
                    startService(new Intent(Splash.this, XmppService.class));
                }

                session.setXmppServiceState("online");

                Intent i = new Intent(getApplicationContext(), NavigationDrawerNew.class);
                if ("true".equalsIgnoreCase(pushtype))
                {
                    i.putExtra("type","push");
                }
                startActivity(i);
                finish();

            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        boolean b = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                System.out.println("1 already running");
                b = true;
                break;
            } else {
                System.out.println("2 not running");
                b = false;
            }
        }
        System.out.println("3 not running");
        return b;
    }

}







