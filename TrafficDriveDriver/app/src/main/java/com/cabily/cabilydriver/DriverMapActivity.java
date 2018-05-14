package com.cabily.cabilydriver;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceManager;
import com.app.service.ServiceRequest;
import com.app.xmpp.XmppService;
import com.cabily.cabilydriver.Helper.GEODBHelper;
import com.cabily.cabilydriver.Helper.GEOService;
import com.cabily.cabilydriver.Utils.ChatAvailabilityCheck;
import com.cabily.cabilydriver.Utils.GPSTracker;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.subclass.SubclassActivity;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 */
public class DriverMapActivity extends SubclassActivity implements View.OnClickListener,
        com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    // Google Map
    private GoogleMap googleMap;
    private Location location = null;
    public static Location myLocation;
    private SessionManager session;
    private Dialog dialog;
    private Marker currentMarker;
    private RelativeLayout Rl_layout_available_status, Rl_traffic, Rl_layout_verify_status;
    private String Str_rideId = "";
    private String driver_id = "", vehicle_image = "", driver_name, vehicle_no;
    private boolean isGpsEnabled;
    private GEODBHelper myDBHelper;
    private ServiceRequest mRequest;
    private String availability;
    private BroadcastReceiver receiver;
    private GPSTracker gps;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private PendingResult<LocationSettingsResult> result;
    private final static int REQUEST_LOCATION = 199;
    private Handler mapHandler = new Handler();
    Button goOffline;
    Marker drivermarker;
    String traffic_status;
    ImageButton traffic_button;
    Bitmap bmp;
    String base64;
    private ServiceRequest mRequest_update;
    TextView tv_driver_name, driver_vehicle_number1, tv_verify_driver;

    private float totalDistanceTravelled;


    private Runnable mapRunnable = new Runnable() {
        @Override
        public void run() {
            gps = new GPSTracker(DriverMapActivity.this);
            if (gps != null && gps.canGetLocation()) {
                /*System.out.println("======map handler===========");
                postRequest(ServiceConstant.UPDATE_CURRENT_LOCATION);*/
                if (myLocation != null) {

                    if (mRequest_update != null) {
                        mRequest_update.cancelRequest();
                    }
                    System.out.println("-----prabu----UPDATE_CURRENT_LOCATION Handler------------------" + ServiceConstant.UPDATE_CURRENT_LOCATION);
                    PostRequest(ServiceConstant.UPDATE_CURRENT_LOCATION);
                }

            } else {
                enableGpsService();
            }
            mapHandler.postDelayed(this, 40000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roadmap);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            if (!isMyServiceRunning(XmppService.class)) {
                startService(new Intent(DriverMapActivity.this, XmppService.class));
            } else {
                stopService(new Intent(DriverMapActivity.this, XmppService.class));
                startService(new Intent(DriverMapActivity.this, XmppService.class));
            }


        session = new SessionManager(DriverMapActivity.this);
        gps = new GPSTracker(this);
        Intent i = getIntent();


        availability = i.getStringExtra("availability");
        System.out.println("Driver availability jai----->" + availability);


        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);
        vehicle_image = user.get(SessionManager.KEY_VEHICLE_IMAGE);
        base64 = user.get(SessionManager.KEY_VEHICLE_BitMap_IMAGE);
        vehicle_no = user.get(SessionManager.KEY_VEHICLENO);
        driver_name = user.get(SessionManager.KEY_DRIVER_NAME);

        bmp = StringToBitMap(base64);

        ImageButton refresh_button = (ImageButton) findViewById(R.id.refresh);
        Rl_traffic = (RelativeLayout) findViewById(R.id.traffic_btn_layout);
        traffic_button = (ImageButton) findViewById(R.id.traffic);
        tv_driver_name = (TextView) findViewById(R.id.driver_name);
        driver_vehicle_number1 = (TextView) findViewById(R.id.driver_vehicle_number);
        tv_verify_driver = (TextView) findViewById(R.id.map_verify_status);

        tv_driver_name.setText(driver_name);
        driver_vehicle_number1.setText(vehicle_no);

        refresh_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //feb 1
                //  ChatingService.startDriverAction(DriverMapActivity.this);
                setLocationRequest();
                buildGoogleApiClient();


                if (drivermarker != null) {
                    drivermarker.remove();
                }


                if (gps.canGetLocation()) {
                    double Dlatitude = gps.getLatitude();
                    double Dlongitude = gps.getLongitude();

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    if (bmp != null) {
                        LatLng toPosition = new LatLng(Dlatitude, Dlongitude);
                        drivermarker = googleMap.addMarker(new MarkerOptions()
                                .position(toPosition)
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp)));

                        //                       currentMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude)).icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                    }
                }


            }
        });


        traffic_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                HashMap<String, String> user = session.getTrafficImage();
                traffic_status = user.get(SessionManager.KEY_Traffic);
                if ("1".equals(traffic_status)) {
                    googleMap.setTrafficEnabled(false);
                    session.setTrafficImage("0");
                    //    traffic_button.setBackgroundResource(R.drawable.traffic_off);
                    traffic_button.setBackgroundResource(R.drawable.traffic_off_new);
                    Rl_traffic.setBackgroundResource(R.drawable.traffic_conner_bc);
                } else {
                    googleMap.setTrafficEnabled(true);
                    session.setTrafficImage("1");
                   /* traffic_button.setBackgroundResource(R.drawable.traffic_on);*/
                    traffic_button.setBackgroundResource(R.drawable.traffic_on_new);
                    Rl_traffic.setBackgroundResource(R.drawable.traffic_conner_on_bc);

                }


            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.finish.canceltrip.DriverMapActivity");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.finish.canceltrip.DriverMapActivity")) {
                    Intent i = new Intent(DriverMapActivity.this, DriverMapActivity.class);
                    i.putExtra("availability", "Yes");
                    finish();
                    startActivity(i);
                    Rl_layout_available_status.setVisibility(View.GONE);
                }
            }
        };
        try {
            registerReceiver(receiver, filter);
        } catch (Exception e) {

        }
        goOffline = (Button) findViewById(R.id.go_offline);
        Rl_layout_available_status = (RelativeLayout) findViewById(R.id.layout_available_status);
        Rl_layout_verify_status = (RelativeLayout) findViewById(R.id.layout_verify_status);
        Rl_layout_available_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Str_rideId.equals("")) {
                    Intent intent = new Intent(DriverMapActivity.this, TripSummaryDetail.class);
                    intent.putExtra("ride_id", Str_rideId);
                    System.out.println("StrRideID---------" + Str_rideId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

            }
        });

        goOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goOffLine();
            }

        });

        try {
            session = new SessionManager(this);
            setLocationRequest();
            buildGoogleApiClient();

            initilizeMap();
        } catch (Exception e) {
        }
        initView();

        String status = myDBHelper.retriveStatus();
        if (availability.equalsIgnoreCase("No")) {
            Rl_layout_available_status.setVisibility(View.VISIBLE);
            goOffline.setVisibility(View.INVISIBLE);


            if (status.equalsIgnoreCase("1")) {
                myDBHelper.insertDriverStatus("1");
            } else if (status.equalsIgnoreCase("0")) {
                myDBHelper.insertDriverStatus("0");
            } else if (status.equalsIgnoreCase("3")) {
                myDBHelper.insertDriverStatus("3");
            } else {
                myDBHelper.insertDriverStatus("0");
            }


        } else {
            myDBHelper.insertDriverStatus("0");
            goOffline.setVisibility(View.VISIBLE);
            Rl_layout_available_status.setVisibility(View.GONE);
        }
        String status1 = myDBHelper.retriveStatus();
        System.out.println("driver  status jai " + status1);

        HashMap<String, String> service = session.getServiceStatus();
        String service_status = service.get(SessionManager.KEY_SERVICE_STATUS);

        System.out.println("service status jai " + service_status);

        if (service_status.equalsIgnoreCase("1")) {
            //Service already starts
            System.out.println("already running");
            if (isMyServiceRunning(GEOService.class)) {
                System.out.println("already running");

            } else {
                Intent serviceIntent = new Intent(getApplicationContext(), GEOService.class);
                startService(serviceIntent);
                System.out.println("not running");
            }
        } else {
            System.out.println("srvice starts");

            if (isMyServiceRunning(GEOService.class)) {
                System.out.println("already running");
                session.createServiceStatus("1");

            } else {
                Intent serviceIntent = new Intent(getApplicationContext(), GEOService.class);
                startService(serviceIntent);
                System.out.println("not running");
            }
           /* Intent serviceIntent = new Intent(this, GEOService.class);
            startService(serviceIntent);*/
        }
        if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {
        } else {
            enableGpsService();
            //showGpsDisableDialog(getResources().getString(R.string.label_gps_textview));
        }

        String notifStatus = session.getNotificationStatus();
        System.out.println("DriverMapActivity Notification Status" + notifStatus);

        if (notifStatus.equalsIgnoreCase(ServiceConstant.ACTION_TAG_RIDE_REQUEST)) {

            if (session.getDriverAlertData().length() > 0) {
                Intent intent = new Intent(DriverMapActivity.this, DriverAlertActivity.class);
                intent.putExtra(DriverAlertActivity.EXTRA, session.getDriverAlertData());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                session.setNotificationStatus("");
            }

        }


    }


    @Override
    public void onBackPressed() {
        //   super.onBackPressed();
        showBackPressedDialog();
    }

    private void showBackPressedDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.label_sure_go_offline)
                .setPositiveButton(getResources().getString(R.string.navigation_drawer_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //   finish();
                        goOffLine();
                        //         finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.navigation_drawer_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapHandler.removeCallbacks(mapRunnable);
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("------prabu--onResume----------");
        mapRunnable.run();
        startLocationUpdates();
        if (myLocation == null) {
            myLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }

        ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(DriverMapActivity.this, "available");
        chatAvailability.postChatRequest();

    }

    private void initView() {
        myDBHelper = new GEODBHelper(getApplicationContext());
        String status = myDBHelper.retriveStatus();

        System.out.println("Ride status-------jai--------" + status);
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

    public void showDialog(String message) {
        dialog = new Dialog(this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    public void dismissDialog() {
        dialog.dismiss();
    }

    private void PostRequest(String Url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        HashMap<String, String> userDetails = session.getUserDetails();
        String driverId = userDetails.get("driverid");
        System.out.println("driverId-------------" + driverId);
        System.out.println("latitude-------------" + myLocation.getLatitude());
        System.out.println("longitude-------------" + myLocation.getLongitude());

        jsonParams.put("driver_id", "" + driverId);
        jsonParams.put("latitude", "" + myLocation.getLatitude());
        jsonParams.put("longitude", "" + myLocation.getLongitude());

        System.out.println("-----------------------------PostRequest-----urkl-------------------------" + Url);
        mRequest_update = new ServiceRequest(DriverMapActivity.this);
        mRequest_update.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                String Str_status = "",
                        Str_availablestaus = "",
                        Str_message = "";

                System.out.println("-----------------------------PostRequest------------------------------" + response);
                try {
                    JSONObject jobject = new JSONObject(response);

                    Str_status = jobject.getString("status");
                    if ("1".equalsIgnoreCase(Str_status)) {
                        JSONObject jobject2 = jobject.getJSONObject("response");
                        Str_availablestaus = jobject2.getString("availability");
                        Str_message = jobject2.getString("message");
                        Str_rideId = jobject2.getString("ride_id");
                        System.out.println("rideIDDresponse----------" + Str_rideId);
                        System.out.println("online----------" + response);
                        if (Str_availablestaus.equalsIgnoreCase("Unavailable")) {
                            Rl_layout_available_status.setVisibility(View.INVISIBLE);
                            goOffline.setVisibility(View.INVISIBLE);

//                            if (session.getTripStatus().equalsIgnoreCase("1")) {
                                if (!NavigationDrawerNew.sPushType) {
                                    Intent trip_intent = new Intent(DriverMapActivity.this, TripPage.class);
                                    trip_intent.putExtra("interrupted", "Yes");
                                    startActivity(trip_intent);
                                }
//                            }
                            //   finish();

                        } else {
                            Rl_layout_available_status.setVisibility(View.GONE);
                            goOffline.setVisibility(View.VISIBLE);
                        }
                        showVerifyStatus(jobject2);

                        if ("No".equalsIgnoreCase(jobject2.getString("verify_status")) || Str_availablestaus.equalsIgnoreCase("Unavailable")) {
                            Rl_traffic.setVisibility(View.INVISIBLE);
                        } else {
                            Rl_traffic.setVisibility(View.VISIBLE);
                        }

                        if (session.isAds()) {
                            HashMap<String, String> ads = session.getAds();
                            String title = ads.get(SessionManager.KEY_AD_TITLE);
                            String msg = ads.get(SessionManager.KEY_AD_MSG);
                            String banner = ads.get(SessionManager.KEY_AD_BANNER);

                            System.out.println("--------jai----ads-title-----------" + title);
                            System.out.println("--------jai----ads-msg-----------" + msg);
                            System.out.println("--------jai----ads-banner-----------" + banner);

                            Intent i1 = new Intent(DriverMapActivity.this, AdsPage.class);
                            i1.putExtra("AdsTitle", title);
                            i1.putExtra("AdsMessage", msg);
                            i1.putExtra("AdsBanner", banner);
                            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i1);

                        }

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onErrorListener() {
            }

        });

    }

    private void showVerifyStatus(JSONObject object) {
        try {
            String verify_status = object.getString("verify_status");
            if ("No".equalsIgnoreCase(verify_status)) {
                String verify_string = object.getString("errorMsg");
                Rl_traffic.setVisibility(View.INVISIBLE);
                Rl_layout_verify_status.setVisibility(View.VISIBLE);
                tv_verify_driver.setText(verify_string);
                //  findViewById(R.id.layout_verify_status).setVisibility(View.VISIBLE);
            } else {
                Rl_traffic.setVisibility(View.VISIBLE);
                Rl_layout_verify_status.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            Rl_layout_verify_status.setVisibility(View.GONE);
        }
    }


    private void initilizeMap() {
        // latitude and longitude
        /// gps = new GPSTracker(this);
        double latitude;
        double longitude;
        if (googleMap == null) {
            MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map));
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap arg) {
                    loadMap(arg);
                }
            });
        }
    }

    public void loadMap(GoogleMap arg) {
        googleMap = arg;
        //  googleMap.setMyLocationEnabled(false);

        googleMap.getUiSettings().setCompassEnabled(true);

        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);


        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        DriverMapActivity.this, R.raw.mapstyle));

        googleMap.getUiSettings().setTiltGesturesEnabled(false);


        //     googleMap.setMyLocationEnabled(true);
        //     googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        HashMap<String, String> user1 = session.getTrafficImage();
        traffic_status = user1.get(SessionManager.KEY_Traffic);

        if ("1".equals(traffic_status)) {
            googleMap.setTrafficEnabled(true);
            //  session.setTrafficImage("1");
            traffic_button.setBackgroundResource(R.drawable.traffic_on_new);
            Rl_traffic.setBackgroundResource(R.drawable.traffic_conner_on_bc);

        }
        if (googleMap == null) {
            /*Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();*/
        }

    }

    public void goOffLine() {

        DashBoardDriver.isOnline = false;

        showDialog("");

        ArrayList<LatLng> distance_travelled = myDBHelper.getDataDistance("1");

        calculateDistance(distance_travelled);


        System.out.println("-----------jai---total_distance-------------------" + distance_travelled.toString().replace("[", "").replace("]", "").replace(" ", ""));

        myDBHelper.Delete("");


        HashMap<String, String> jsonParams = new HashMap<String, String>();

        HashMap<String, String> userDetails = session.getUserDetails();

        String driverId = userDetails.get("driverid");

        jsonParams.put("driver_id", "" + driverId);

        jsonParams.put("availability", "" + "No");

        /*jsonParams.put("distance", "20");*/

        jsonParams.put("distance", String.valueOf(totalDistanceTravelled / 1000));


        System.out.println("og offline-----------jsonParams-------------" + jsonParams);

        System.out.println("og offline-----------url-------------" + ServiceConstant.UPDATE_AVAILABILITY);

        ServiceManager manager = new ServiceManager(this, updateAvailabilityServiceListener);

//        ChatingService.closeConnection();

        manager.makeServiceRequest(ServiceConstant.UPDATE_AVAILABILITY, Request.Method.POST, jsonParams);

        myDBHelper.insertDriverStatus("2");

        session.createServiceStatus("");

        Intent serviceIntent = new Intent(getApplicationContext(), GEOService.class);

        stopService(serviceIntent);

    }


    private void calculateDistance(ArrayList<LatLng> points) {

        float tempTotalDistance = 0;

        for (int i = 0; i < points.size() - 1; i++) {
            LatLng pointA = points.get(i);
            LatLng pointB = points.get(i + 1);
            float[] results = new float[3];
            Location.distanceBetween(pointA.latitude, pointA.longitude, pointB.latitude, pointB.longitude, results);
            tempTotalDistance += results[0];
        }

        totalDistanceTravelled = tempTotalDistance;
    }


    private ServiceManager.ServiceListener updateAvailabilityServiceListener = new ServiceManager.ServiceListener() {
        @Override
        public void onCompleteListener(Object object) {
            try {
                dismissDialog();
                String response = (String) object;
                System.out.println("goofflineresponse---------" + response);


                JSONObject object1 = new JSONObject(response);

                /*session.setXmppServiceState("");

                //Stop xmpp service
                stopService(new Intent(getApplicationContext(), XmppService.class));*/


                if (object1.length() > 0) {
                    String status = object1.getString("status");
                    if (status.equalsIgnoreCase("1")) {

                        Intent i = new Intent(getApplicationContext(), NavigationDrawerNew.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorListener(Object obj) {
            dismissDialog();
            finish();
        }
    };

    private void setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(Bundle bundle) {
        try {
            if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {
            }
            myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            if (myLocation != null) {
                if (drivermarker != null) {
                    drivermarker.remove();
                }

                if (googleMap != null) {
                    HashMap<String, String> bitmap = session.getBitmapCode();
                    base64 = bitmap.get(SessionManager.KEY_VEHICLE_BitMap_IMAGE);
                    bmp = StringToBitMap(base64);
                    if (bmp != null) {
                        drivermarker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
                                17));
                    }
                }
                PostRequest(ServiceConstant.UPDATE_CURRENT_LOCATION);
                System.out.println("-----prabu----online------------------" + ServiceConstant.UPDATE_CURRENT_LOCATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }


    @Override
    public void onLocationChanged(Location location) {

        this.myLocation = location;
        if (myLocation != null) {
            try {
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                if (drivermarker != null) {
                    drivermarker.remove();
                }
                if (googleMap != null) {
                    drivermarker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                    float zoom = googleMap.getCameraPosition().zoom;
                }

            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onClick(View view) {

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
                            status.startResolutionForResult(DriverMapActivity.this, REQUEST_LOCATION);
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

    @Override
    public void onDestroy() {
        // Unregister the logout receiver
        mapHandler.removeCallbacks(mapRunnable);
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapHandler.removeCallbacks(mapRunnable);
    }

    public Bitmap StringToBitMap(String encodedString) {
        System.out.println("base 64" + encodedString);
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
