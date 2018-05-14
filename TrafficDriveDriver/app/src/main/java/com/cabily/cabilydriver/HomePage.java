package com.cabily.cabilydriver;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Hockeyapp.ActivityHockeyApp;
import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.app.xmpp.XmppService;
import com.cabily.cabilydriver.Utils.GPSTracker;
import com.cabily.cabilydriver.Utils.SessionManager;
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


public class HomePage extends ActivityHockeyApp implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Button mSignIn;
    private Button mRegister;
    private SessionManager session;
    private GPSTracker gps;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private PendingResult<LocationSettingsResult> result;
    private final static int REQUEST_LOCATION = 199;
    private String package_name = "com.cabily.cabilydriver";
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2313;

    private ServiceRequest mRequest;
    public static HomePage homePage;
    private boolean isAppInfoAvailable = false;

    String driver_id = "", sLatitude = "", sLongitude = "", Language_code = "",driver_image = "",driverName ="";
    /* private void onRequestRunTimePermission() {
         if (ContextCompat.checkSelfPermission(this,
                 Manifest.permission.ACCESS_FINE_LOCATION)
                 != PackageManager.PERMISSION_GRANTED) {
             ActivityCompat.requestPermissions(this,
                     new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                     MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
         }
     }*/
    final int PERMISSION_REQUEST_CODE = 111;

    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        progress = (ProgressBar) findViewById(R.id.progress_splash);
        homePage = HomePage.this;

        // onRequestRunTimePermission();
        mSignIn = (Button) findViewById(R.id.btn_signin);
        mRegister = (Button) findViewById(R.id.btn_register);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
        }
        // web_update();
        gps = new GPSTracker(getApplicationContext());
        mGoogleApiClient = new GoogleApiClient.Builder(HomePage.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        try {

        } catch (Exception e) {

        }
        session = new SessionManager(HomePage.this);
        session.createSessionOnline("0");
        session.setRequestCount(0);
        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);
        if (gps.isgpsenabled() && gps.canGetLocation()) {
            //do nothing

            sLatitude = String.valueOf(gps.getLatitude());
            sLongitude = String.valueOf(gps.getLongitude());
        } else {
            enableGpsService();
        }
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() || !checkWriteExternalStoragePermission() || !checkCameraPermission()) {
                requestPermission();
            } else {


                postRequest_applaunch(ServiceConstant.app_launching_url);



                    /*ChatingService.startDriverAction(HomePage.this);
                    Intent i = new Intent(getApplicationContext(), NavigationDrawer.class);
                    startActivity(i);
                    finish();*/


            }
        } else {


            postRequest_applaunch(ServiceConstant.app_launching_url);
                /*ChatingService.startDriverAction(HomePage.this);
                Intent i = new Intent(getApplicationContext(), NavigationDrawer.class);
                startActivity(i);
                finish();*/


        }


        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServiceConstant.Register_URL));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/

                Intent intent = new Intent(HomePage.this, RegisterPageWebview.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                //Alert(HomePage.this.getResources().getString(R.string.lbel_alert_inform), HomePage.this.getResources().getString(R.string.lbel_alert_inform2));
            }
        });
    }


    private long value(String string) {
        string = string.trim();
        if (string.contains(".")) {
            final int index = string.lastIndexOf(".");
            return value(string.substring(0, index)) * 100 + value(string.substring(index + 1));
        } else {
            return Long.valueOf(string);
        }
    }

    //--------------------------code to update checker------------------
    private boolean web_update() {
        try {
            String curVersion = HomePage.this.getPackageManager().getPackageInfo(package_name, 0).versionName;

            System.out.println("currentversion-----------" + curVersion);

            String newVersion = curVersion;

            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + package_name + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();

            System.out.println("Newversion-----------" + newVersion);

            return (value(curVersion) < value(newVersion)) ? true : false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(HomePage.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

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
                            status.startResolutionForResult(HomePage.this, REQUEST_LOCATION);
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
                                gps = new GPSTracker(HomePage.this);

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
                        enableGpsService();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }


    private boolean checkAccessFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAccessCoarseLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkWriteExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkCameraPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    postRequest_applaunch(ServiceConstant.app_launching_url);
                        /*ChatingService.startDriverAction(HomePage.this);
                        Intent i = new Intent(getApplicationContext(), NavigationDrawer.class);
                        startActivity(i);
                        finish();*/


                } else {
                    finish();
                }
                break;
        }
    }


    private void postRequest_applaunch(String Url) {

        System.out.println("-------------Splash App Information Url----------------" + Url);
        progress.setVisibility(View.VISIBLE);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "driver");
        jsonParams.put("id", driver_id);
        jsonParams.put("latitude", sLatitude);
        jsonParams.put("longitude", sLongitude);

        System.out.println("-------------Splash App Information jsonParams----------------" + jsonParams);


        mRequest = new ServiceRequest(HomePage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Splash App Information Response----------------" + response);


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
                              /*  sFacebookId = info_object.getString("facebook_app_id");
                                sGooglePlusId = info_object.getString("google_plus_app_id");
                                sPhoneMasking = info_object.getString("phone_masking_status");*/

                                /*server_mode = info_object.getString("server_mode");
                                site_mode = info_object.getString("site_mode");
                                site_string = info_object.getString("site_mode_string");
                                site_url = info_object.getString("site_url");*/
                                driver_image = info_object.getString("driver_image");
                                driverName = info_object.getString("driver_name");
                                Language_code = info_object.getString("lang_code");
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
                        session.setXmpp(sXmppHostUrl, sHostName);


                        if (session.isLoggedIn()) {
                            postRequest_SetUserLocation(ServiceConstant.UPDATE_CURRENT_LOCATION);
                        } else {
                            Locale locale = null;

                            switch (Language_code) {

                                case "en":
                                    locale = new Locale("en");
                                    session.setlamguage("en", "en");
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
                                    session.setlamguage("es", "es");
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
                                    session.setlamguage("ta", "ta");
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
                                    session.setlamguage("en", "en");
                                    break;
                            }

                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());
                            session.setdriver_image(driver_image);
                            session.setdriverNameUpdate(driverName);

                        }
                       /* if(site_mode.equalsIgnoreCase("dev"))
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
                        if(server_mode.equalsIgnoreCase("0"))
                        {
                            Toast.makeText(context, site_url, Toast.LENGTH_SHORT).show();
                        }*/
                    } else {
                        Toast.makeText(HomePage.this, "BAD URL", Toast.LENGTH_SHORT).show();

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

                progress.setVisibility(View.GONE);

            }

            @Override
            public void onErrorListener() {
                Toast.makeText(HomePage.this, ServiceConstant.baseurl, Toast.LENGTH_SHORT).show();
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
        mRequest = new ServiceRequest(HomePage.this);
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
                    startService(new Intent(HomePage.this, XmppService.class));
                }

                session.setXmppServiceState("online");

                Intent i = new Intent(getApplicationContext(), NavigationDrawerNew.class);
                startActivity(i);
                finish();
               /* Intent i = new Intent(HomePage.this, NavigationDrawer.class);
                startActivity(i);
                finish();*/
                // overridePendingTransition(R.anim.enter, R.anim.exit);

            }

            @Override
            public void onErrorListener() {
                //FEB 1
                        /*if (!ChatingService.isConnected) {
                            ChatingService.startDriverAction(getActivity());
                        }*/
                /*Intent i = new Intent(getApplicationContext(), NavigationDrawer.class);
                startActivity(i);
                finish();*/

                if (!isMyServiceRunning(XmppService.class)) {
                    startService(new Intent(HomePage.this, XmppService.class));
                }

                session.setXmppServiceState("online");

                Intent i = new Intent(getApplicationContext(), NavigationDrawerNew.class);
                startActivity(i);
                finish();


                /*Intent i = new Intent(HomePage.this, NavigationDrawer.class);
                startActivity(i);
                finish();*/
                //   overridePendingTransition(R.anim.enter, R.anim.exit);
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
