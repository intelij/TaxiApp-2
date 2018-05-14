package com.cabily.cabilydriver;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.latlnginterpolation.LatLngInterpolator;
import com.app.latlnginterpolation.MarkerAnimation;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.Helper.GEODBHelper;
import com.cabily.cabilydriver.Helper.GEOService;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.CurrencySymbolConverter;
import com.cabily.cabilydriver.Utils.GPSTracker;
import com.cabily.cabilydriver.Utils.GoogleNavigationService;
import com.cabily.cabilydriver.Utils.RoundedImageView;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.adapter.ContinuousRequestAdapter;
import com.cabily.cabilydriver.googlemappath.GMapV2GetRouteDirection;
import com.cabily.cabilydriver.subclass.SubclassActivity;
import com.cabily.cabilydriver.widgets.PkDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.chat.Chat;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by user88 on 10/29/2015.
 */
public class EndTrip extends SubclassActivity implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SeekBar.OnSeekBarChangeListener {
    private final static int REQUEST_LOCATION = 199;
    private PendingResult<LocationSettingsResult> result;
    private String driver_id = "";
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private TextView Tv_name, Tv_mobilno, Tv_rideid, Tv_start_wait, Tv_stop_wait;
    private RelativeLayout Rl_layout_back;
    private Button Bt_Endtrip;
    private String Str_name = "", Str_mobilno = "", Str_rideid = "", Str_User_Id = "";
    private RelativeLayout alert_layout;
    private TextView alert_textview;
    private String droplocation[];
    private String startlocation[];
    private MarkerOptions marker;
    private double previous_lat, previous_lon, current_lat, current_lon, dis = 0.0;
    public static Location myLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Marker currentMarker;
    private ServiceRequest mRequest;
    private GoogleMap googleMap;
    private Dialog dialog;
    private GMapV2GetRouteDirection v2GetRouteDirection;
    private Document document;
    private int mins;
    private int secs;
    private int hours;

    private int milliseconds;
    private Button Bt_Enable_voice;
    private String Str_status = "", Str_profilpic = "", Str_response = "", Str_ridefare = "", Str_timetaken = "", Str_waitingtime = "", Str_need_payment = "", Str_currency = "", Str_ride_distance = "", str_recievecash = "", str_requestpay = "", Zero_response = "";
    private GPSTracker gps;
    private LatLng destlatlng, startlatlng;
    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    private TextView timerValue;
    private RelativeLayout layout_timer, Rl_layout_enable_voicenavigation, Rl_traffic;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    float[] results;
    LocationManager locationManager;
    Barcode.GeoPoint geoPoint;
    double location;
    private String beginAddress;
    private RoundedImageView profile_img;
    private String endAddress, address;
    private String sCurrencySymbol = "";
    private String distance = "";
    private LatLng latLng;
    private PolylineOptions mPolylineOptions;
    private SeekBar sliderSeekBar;
    private ShimmerButton Bt_slider;
    private Shimmer shimmer;
    private float distance_to = 0;
    private LatLng newLatLng, oldLatLng;
    private String Str_Latitude = "", Str_longitude = "";
    ImageButton traffic_button;
    String traffic_status;
    /* private final static int INTERVAL = 10000;
     Handler mHandler;*/
    private GEODBHelper myDBHelper;
    private MarkerOptions markerOptions;
    final int PERMISSION_REQUEST_CODE = 111;
    final int PERMISSION_REQUEST_NAVIGATION_CODE = 222;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private boolean waitingStatus = false;
    String base64;
    Bitmap bmp;
    private ImageView callimg;
    private String Str_Interrupt = "";
    double myMovingDistance = 0.0;
    private boolean isPathShowing;

    private BroadcastReceiver finishReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endtrip);

        // Receiving the data from broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.app.finish.TripPage");
        finishReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(finishReceiver, filter);

        initialize();
        try {
            setLocationRequest();
            buildGoogleApiClient();
            initializeMap();
        } catch (Exception e) {
        }

        //    ChatingService.startDriverAction(EndTrip.this);

        Tv_start_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tv_stop_wait.setVisibility(View.VISIBLE);
                Tv_start_wait.setVisibility(View.GONE);
                layout_timer.setVisibility(View.VISIBLE);
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                waitingStatus = true;
                session.setWaitingStatus("true");
                Str_Interrupt = "Yes";
            }
        });
        Tv_stop_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tv_start_wait.setVisibility(View.VISIBLE);
                Tv_stop_wait.setVisibility(View.GONE);
                session.setWaitingStatus("false");
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
                waitingStatus = false;

            }
        });


        Rl_layout_enable_voicenavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!waitingStatus) {
                    if (Build.VERSION.SDK_INT >= 23) {

                        if (!checkWriteExternalStoragePermission()) {
                            requestNavigationPermission();
                        } else {
                            if (!Settings.canDrawOverlays(EndTrip.this)) {

                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);

                            } else {
                                moveNavigation();
                            }
                        }
                    } else {
                        moveNavigation();
                    }
                } else {
                    AlertNavigation(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.voice_navigationlabel_continue));
                }
               /* String voice_destination_lat_long="";
                if (!droplocation[0].equalsIgnoreCase("null") && !droplocation[1].equalsIgnoreCase("null")){
                    double latitude = Double.parseDouble(droplocation[0]);
                    double longitude = Double.parseDouble(droplocation[1]);
                    voice_destination_lat_long = latitude + "," + longitude;
                }
                else
                {
                    voice_destination_lat_long =0.0+","+0.0;
                }
                String voice_curent_lat_long = MyCurrent_lat + "," + MyCurrent_long;



                System.out.println("----------fromPosition---------------" + voice_curent_lat_long);
                System.out.println("----------toPosition---------------" + voice_destination_lat_long);
                String locationUrl = "http://maps.google.com/maps?saddr=" + voice_curent_lat_long + "&daddr=" + voice_destination_lat_long;
                System.out.println("----------locationUrl---------------" + locationUrl);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(locationUrl));
                startActivity(intent);*/

            }
        });
        callimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Str_mobilno != null) {

                    if (Build.VERSION.SDK_INT >= 23) {
                        // Marshmallow+
                        if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                            requestPermission();
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + Str_mobilno));
                            startActivity(callIntent);
                        }
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + Str_mobilno));
                        startActivity(callIntent);
                    }
                } else {
                    Alert1(EndTrip.this.getResources().getString(R.string.alert_sorry_label_title), EndTrip.this.getResources().getString(R.string.arrived_alert_content1));
                }
            }
        });
    }


    private void initialize() {
        session = new SessionManager(EndTrip.this);
        gps = new GPSTracker(EndTrip.this);

        session.setApp_Current_Page_Status("EndTrip");

        //    mHandler = new Handler();
        markerOptions = new MarkerOptions();
        myDBHelper = new GEODBHelper(getApplicationContext());
        // get user data from session

        myDBHelper.Delete("");
        myDBHelper.insertDriverStatus("1");
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);

        HashMap<String, String> bitmap = session.getBitmapCode();
        base64 = bitmap.get(SessionManager.KEY_VEHICLE_BitMap_IMAGE);
        bmp = StringToBitMap(base64);


        v2GetRouteDirection = new GMapV2GetRouteDirection();
        Rl_layout_enable_voicenavigation = (RelativeLayout) findViewById(R.id.layout_end_Enable_voice);
        Bundle b = getIntent().getExtras();
        if (b != null && b.containsKey("pickuplatlng")) {
            droplocation = b.getString("pickuplatlng").split(",");
            address = b.getString("pickuplatlng");
        }
        if (b != null && b.containsKey("startpoint")) {
            beginAddress = b.getString("startpoint");
            startlocation = b.getString("startpoint").split(",");
            try {
                double latitude = Double.parseDouble(droplocation[0]);
                double longitude = Double.parseDouble(droplocation[1]);
                double startlatitude = Double.parseDouble(startlocation[0]);
                double startlongitude = Double.parseDouble(startlocation[1]);
                destlatlng = new LatLng(latitude, longitude);
                startlatlng = new LatLng(startlatitude, startlongitude);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //---------------set polyline color and width----------------
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(Color.BLUE).width(10);
        Intent i = getIntent();
        Str_rideid = i.getStringExtra("rideid");
        Str_name = i.getStringExtra("name");
        Str_mobilno = i.getStringExtra("mobilno");
        Str_User_Id = i.getStringExtra("user_id");
        Str_profilpic = i.getStringExtra("user_image");
        Str_Interrupt = i.getStringExtra("interrupted");
        ContinuousRequestAdapter.userID = Str_User_Id;

        Rl_traffic = (RelativeLayout) findViewById(R.id.traffic_btn_layout);


        System.out.println("--------------user_id----------" + ContinuousRequestAdapter.userID);

        ImageButton refresh_button = (ImageButton) findViewById(R.id.refresh);


        Tv_name = (TextView) findViewById(R.id.end_trip_name);
        Tv_mobilno = (TextView) findViewById(R.id.end_trip_mobilno);
       /* Tv_rideid = (TextView) findViewById(R.id.beginendtrip_rideid);*/
        Tv_start_wait = (TextView) findViewById(R.id.begin_waitingtime_tv_start);
        Tv_stop_wait = (TextView) findViewById(R.id.begin_waitingtime_tv_stop);
        timerValue = (TextView) findViewById(R.id.timerValue);
        layout_timer = (RelativeLayout) findViewById(R.id.layout_timer);
        alert_layout = (RelativeLayout) findViewById(R.id.end_trip_alert_layout);
        alert_textview = (TextView) findViewById(R.id.end_trip_alert_textView);
        profile_img = (RoundedImageView) findViewById(R.id.profile_image_endtrip);
        callimg = (ImageView) findViewById(R.id.begintrip_call);
        shimmer = new Shimmer();
        sliderSeekBar = (SeekBar) findViewById(R.id.end_Trip_seek);
        Bt_slider = (ShimmerButton) findViewById(R.id.end_Trip_slider_button);
        shimmer.start(Bt_slider);

        sliderSeekBar.setOnSeekBarChangeListener(this);

        if ("Yes".equalsIgnoreCase(Str_Interrupt)) {
            // waitingStatus=true;
            //   Str_Interrupt="Yes";
            startTime = SystemClock.uptimeMillis();

            HashMap<String, String> wait = session.getWaitingTime();

            String waittime = wait.get(SessionManager.KEY_WAIT);
            HashMap<String, String> wait_status = session.getWaitingStatus();
            String waitstatus = wait_status.get(SessionManager.KEY_WAIT_STATUS);

            if (!waitstatus.equals("0")) {
                if (waitstatus.equals("true")) {
                    System.out.println("jai 1");
                    Tv_stop_wait.setVisibility(View.VISIBLE);
                    Tv_start_wait.setVisibility(View.GONE);
                    customHandler.postDelayed(updateTimerThread, 0);
                    layout_timer.setVisibility(View.VISIBLE);
                } else {
                    System.out.println("jai 2");
                    Tv_stop_wait.setVisibility(View.GONE);
                    Tv_start_wait.setVisibility(View.VISIBLE);
                    layout_timer.setVisibility(View.VISIBLE);
                }
            } else {
                layout_timer.setVisibility(View.GONE);
            }
            if (!waittime.equals("0")) {
                if (!waittime.isEmpty()) {
                    System.out.println("jai 3");
                    timeSwapBuff = Long.parseLong(waittime);
                    secs = (int) (timeSwapBuff / 1000);
                    hours = secs / (60 * 60);
                    mins = secs / 60;
                    secs = secs % 60;
                    if (mins >= 60) {
                        mins = 00;
                    }
                    milliseconds = (int) (timeSwapBuff % 1000);
                    timerValue.setText(String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":"
                            + String.format("%02d", secs));
                } else {
                    session.setWaitingStatus("0");
                    layout_timer.setVisibility(View.GONE);
                    timerValue.setText("00:00:00");
                }
            }
        } else {
            session.setWaitingStatus("0");
            layout_timer.setVisibility(View.GONE);
            timerValue.setText("00:00:00");
        }


        System.out.println("profile pic jai" + Str_profilpic);
        System.out.println("profile name jai" + Str_name);
        System.out.println("phone number jai" + Str_mobilno);
        Picasso.with(EndTrip.this).load(String.valueOf(Str_profilpic)).placeholder(R.drawable.nouserimg).memoryPolicy(MemoryPolicy.NO_CACHE).into(profile_img);


        isPathShowing = false;
        Tv_name.setText(Str_name);
        Tv_mobilno.setText(Str_rideid);

        cd = new ConnectionDetector(EndTrip.this);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            System.out.println("--------------jai1-----map1----------");
            //    mHandler.postDelayed(mHandlerTask, 0);

            //         mHandlerTask.run();
        } else {
            Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
        }
        HashMap<String, String> service = session.getServiceStatus();
        String service_status = service.get(SessionManager.KEY_SERVICE_STATUS);
        if (service_status.equalsIgnoreCase("1")) {
            //Service already starts
            System.out.println("already running");
            if (isMyServiceRunning(GEOService.class)) {
                System.out.println("already running");
            } else {
                Intent serviceIntent = new Intent(this, GEOService.class);
                startService(serviceIntent);
                System.out.println("not running");
            }
        } else {

            if (isMyServiceRunning(GEOService.class)) {
                System.out.println("already running");
                session.createServiceStatus("1");
            } else {
                Intent serviceIntent = new Intent(this, GEOService.class);
                startService(serviceIntent);
                System.out.println("not running");
            }
            System.out.println("srvice starts");
           /* Intent serviceIntent = new Intent(this, GEOService.class);
            startService(serviceIntent);*/
        }

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

              /*  Intent i = new Intent(DriverMapActivity.this, DriverMapActivity.class);
                i.putExtra("availability",availability);
                finish();
                startActivity(i);*/


            }
        });


        traffic_button = (ImageButton) findViewById(R.id.traffic);
        traffic_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HashMap<String, String> user = session.getTrafficImage();
                traffic_status = user.get(SessionManager.KEY_Traffic);
                if ("1".equals(traffic_status)) {
                    googleMap.setTrafficEnabled(false);
                    session.setTrafficImage("0");
                    traffic_button.setBackgroundResource(R.drawable.traffic_off_new);
                    Rl_traffic.setBackgroundResource(R.drawable.traffic_conner_bc);
                } else {
                    googleMap.setTrafficEnabled(true);
                    session.setTrafficImage("1");
                    traffic_button.setBackgroundResource(R.drawable.traffic_on_new);
                    Rl_traffic.setBackgroundResource(R.drawable.traffic_conner_on_bc);
                }
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

    private void initializeMap() {

        if (googleMap == null) {
            MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.arrived_trip_view_map));
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap arg) {
                    loadMap(arg);
                }
            });
        }

      /*  if (googleMap == null) {
            googleMap = ((MapFragment) EndTrip.this.getFragmentManager().findFragmentById(R.id.arrived_trip_view_map)).getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(EndTrip.this, getResources().getString(R.string.action_alert_unabletocreatemap), Toast.LENGTH_SHORT).show();
            }
        }
        // Changing map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Showing / hiding your current location
        googleMap.setMyLocationEnabled(false);
        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(false);
        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(false);


        if (gps.canGetLocation()) {
            double Dlatitude = gps.getLatitude();
            double Dlongitude = gps.getLongitude();

            MyCurrent_lat = Dlatitude;
            MyCurrent_long = Dlongitude;

            previous_lat = MyCurrent_lat;
            previous_lon = MyCurrent_long;
            // Move the camera to last position with a zoom level
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //----------------------set marker------------------
            marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.carmove));
            currentMarker = googleMap.addMarker(marker);

        } else {
            alert_layout.setVisibility(View.VISIBLE);
            alert_textview.setText(getResources().getString(R.string.alert_gpsEnable));
        }*/
    }

    public void loadMap(GoogleMap arg) {
        googleMap = arg;
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        EndTrip.this, R.raw.mapstyle));

        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        //   googleMap.setTrafficEnabled(true);
        HashMap<String, String> user1 = session.getTrafficImage();
        traffic_status = user1.get(SessionManager.KEY_Traffic);

        if ("1".equals(traffic_status)) {
            googleMap.setTrafficEnabled(true);
            //  session.setTrafficImage("1");
            traffic_button.setBackgroundResource(R.drawable.traffic_on_new);
            Rl_traffic.setBackgroundResource(R.drawable.traffic_conner_on_bc);
        }
        if (gps.canGetLocation()) {
            double Dlatitude = gps.getLatitude();
            double Dlongitude = gps.getLongitude();

            MyCurrent_lat = Dlatitude;
            MyCurrent_long = Dlongitude;

            previous_lat = MyCurrent_lat;
            previous_lon = MyCurrent_long;
            // Move the camera to last position with a zoom level
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //----------------------set marker------------------

            if (bmp != null) {
/*                        drivermarker = googleMap.addMarker(new MarkerOptions()
                                .position(toPosition)
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp)));*/
                if (currentMarker != null) {
                    currentMarker.remove();
                }
                currentMarker = googleMap.
                        addMarker(new MarkerOptions()
                                .position(new LatLng(Dlatitude, Dlongitude))
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(bmp)));
            }
           /* marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.carmove));*/
            //  currentMarker = googleMap.addMarker(marker);

        } else {
            alert_layout.setVisibility(View.VISIBLE);
            alert_textview.setText(getResources().getString(R.string.alert_gpsEnable));
        }

    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(EndTrip.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                Intent broadcastIntent_begintrip = new Intent();
                broadcastIntent_begintrip.setAction("com.finish.com.finish.BeginTrip");
                sendBroadcast(broadcastIntent_begintrip);

                Intent broadcastIntent_arrivedtrip = new Intent();
                broadcastIntent_arrivedtrip.setAction("com.finish.ArrivedTrip");
                sendBroadcast(broadcastIntent_arrivedtrip);

                Intent broadcastIntent_endtrip = new Intent();
                broadcastIntent_endtrip.setAction("com.finish.EndTrip");
                sendBroadcast(broadcastIntent_endtrip);

                Intent intent = new Intent(EndTrip.this, LoadingPage.class);
                intent.putExtra("Driverid", driver_id);
                intent.putExtra("RideId", Str_rideid);
                startActivity(intent);

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        mDialog.show();
    }


    //------------------------------code for distance----------------------------
    @Override
    protected void onResume() {
        super.onResume();
//        mHandlerTask.run();
        startLocationUpdates();
    }

    private void setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        //    mHandler.removeCallbacks(mHandlerTask);
    }


    public void onConnected(Bundle bundle) {

        if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {
        }
        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
        cd = new ConnectionDetector(EndTrip.this);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {


            if (myLocation != null) {
                if (googleMap != null) {
                    if (startlatlng != null && destlatlng != null) {

                        GetRouteTask getRoute = new GetRouteTask();
                        getRoute.execute();
                    }
                }
          /*  if (googleMap == null)
                googleMap = ((MapFragment) EndTrip.this.getFragmentManager().findFragmentById(R.id.arrived_trip_view_map)).getMap();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 16));

                if (startlatlng != null && destlatlng != null) {
                    GetRouteTask getRoute = new GetRouteTask();
                    getRoute.execute();
                }*/
            }
        } else {
            Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }

    public void onConnectionSuspended(int i) {
    }

    JSONObject job;
    LatLngInterpolator mLatLngInterpolator;
    Marker drivermarker;
    Location oldLocation;

    @Override
    public void onLocationChanged(Location location) {

        if (this.myLocation != null) {
            distance_to = location.distanceTo(myLocation);
            System.out.println("---------distance to-----------" + location.distanceTo(myLocation));
        }
        this.myLocation = location;
        cd = new ConnectionDetector(EndTrip.this);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            if (!isPathShowing) {
                System.out.println("-------------jai1-----------map in location change");
                if (myLocation != null) {
                    if (googleMap != null) {
                        if (startlatlng != null && destlatlng != null) {

                            GetRouteTask getRoute = new GetRouteTask();
                            getRoute.execute();
                        }
                    }
          /*  if (googleMap == null)
                googleMap = ((MapFragment) EndTrip.this.getFragmentManager().findFragmentById(R.id.arrived_trip_view_map)).getMap();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 16));

                if (startlatlng != null && destlatlng != null) {
                    GetRouteTask getRoute = new GetRouteTask();
                    getRoute.execute();
                }*/
                }
            }

        } else {
            //  Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
            Toast.makeText(EndTrip.this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_LONG).show();
        }

        if (myLocation != null) {

            //-------------Updating Marker-------
            try {

                MyCurrent_lat = myLocation.getLatitude();
                MyCurrent_long = myLocation.getLongitude();

                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                if (oldLatLng == null) {
                    System.out.println("----------inside oldLatLngnull--------");
                    oldLatLng = latLng;
                }
                newLatLng = latLng;
                if (mLatLngInterpolator == null) {
                    mLatLngInterpolator = new LatLngInterpolator.Linear();
                }
                oldLocation = new Location("");
                oldLocation.setLatitude(oldLatLng.latitude);
                oldLocation.setLongitude(oldLatLng.longitude);

                float bearingValue = oldLocation.bearingTo(location);


                myMovingDistance = oldLocation.distanceTo(location);

                //  Toast.makeText(EndTrip.this, String.valueOf(myMovingDistance), Toast.LENGTH_SHORT).show();

                System.out.println("movingdistacn------------" + myMovingDistance);

                if (myMovingDistance > 2) {

                    if (currentMarker != null) {
                        currentMarker.remove();
                    }
                    if (googleMap != null) {
                        if (bmp != null) {

                            if (drivermarker != null) {
                                System.out.println("---------inside new bearing value drivermarker != null-------------++" + bearingValue);
                                //        System.out.println("---------inside drivermarker != null-------------++" + getBearing(oldLatLng, newLatLng));
                                //Toast.makeText(EndTrip.this, String.valueOf(bearingValue), Toast.LENGTH_SHORT).show();

                                if (!String.valueOf(bearingValue).equalsIgnoreCase("NaN")) {
                                    if (location.getAccuracy() < 100.0 && location.getSpeed() < 6.95) {
                                        //drivermarker.setRotation(bearingValue);
                                        rotateMarker(drivermarker, bearingValue, googleMap);
                                        MarkerAnimation.animateMarkerToGB(drivermarker, latLng, mLatLngInterpolator);

                                        float zoom = googleMap.getCameraPosition().zoom;
//new change
                                       /* CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
                                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                                    }
                                }
                            } else {
                                currentMarker.remove();
                                drivermarker = googleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                        .anchor(0.5f, 0.5f)
                                        .rotation(myLocation.getBearing())
                                        .flat(true));
                            }
//                        /*if (!String.valueOf(getBearing(oldLatLng, newLatLng)).equalsIgnoreCase("NaN")) {
//                            sendLocationToUser(myLocation, bearingValue);
//                        }*/


                       /* if (currentMarker != null) {
                            currentMarker.remove();
                        }
                        currentMarker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bmp)));*/


                        }
                    /*currentMarker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.carmove)));*/

                        //new change

                       /* float zoom = googleMap.getCameraPosition().zoom;
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
                        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        googleMap.moveCamera(camUpdate);*/
                    }
                }
                oldLatLng = newLatLng;
                //      sendLocationToUser(myLocation);
            } catch (Exception e) {
            }
            //------Calculating Distance------
            float[] f = new float[1];
            current_lat = location.getLatitude();
            current_lon = location.getLongitude();
            if (current_lat != previous_lat || current_lon != previous_lon) {

                if (distance_to >= 1.0) {
                    Location.distanceBetween(previous_lat, previous_lon, current_lat, current_lon, f);
                    dis += Double.parseDouble(String.valueOf(f[0]));
                }
                previous_lat = current_lat;
                previous_lon = current_lon;
                System.out.println("distance inside----------------------" + dis);
            } else {
                previous_lat = current_lat;
                previous_lon = current_lon;
                dis = dis;
            }
            previous_lat = current_lat;
            previous_lon = current_lon;

        }
    }


    Chat chat;

 /*   private void sendLocationToUser(Location location) throws JSONException {

        System.out.println("endtripchatID--------------"+chatID);

        String sendLat = Double.valueOf(location.getLatitude()).toString();
        String sendLng = Double.valueOf(location.getLongitude()).toString();
        if (job == null) {
            job = new JSONObject();
        }
        job.put("action", "driver_loc");
        job.put("latitude", sendLat);
        job.put("longitude", sendLng);
        job.put("ride_id", Str_rideid);
        builder.sendMessage(chatID, job.toString());

        
       *//* String sToID = ContinuousRequestAdapter.userID + "@" + ServiceConstant.XMPP_SERVICE_NAME;
        try {
            if(chat  != null){
                chat.sendMessage(job.toString());
            }else{
                chat = ChatingService.createChat(sToID);
                chat.sendMessage(job.toString());
            }
        } catch (SmackException.NotConnectedException e) {
            try {
                chat = ChatingService.createChat(sToID);
                chat.sendMessage(job.toString());
            }catch (SmackException.NotConnectedException e1){
                Toast.makeText(this,"Not Able to send data to the user Network Error",Toast.LENGTH_SHORT).show();
            }
        }*//*

    }*/

    static public void rotateMarker(final Marker marker, final float toRotation, GoogleMap map) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private class GetRouteTask extends AsyncTask<String, Void, String> {

        String response = "";
        GMapV2GetRouteDirection v2GetRouteDirection = new GMapV2GetRouteDirection();
        Document document;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            document = v2GetRouteDirection.getDocument(startlatlng, destlatlng, GMapV2GetRouteDirection.MODE_DRIVING);
            response = "Success";
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (result.equalsIgnoreCase("Success")) {
                    ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                    PolylineOptions rectLine = new PolylineOptions().width(15).color(getResources().getColor(R.color.app_color));
                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    Marker m[] = new Marker[2];
                    if (googleMap != null) {
                        m[0] = googleMap.addMarker(new MarkerOptions().position(startlatlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker)));
                        m[1] = googleMap.addMarker(new MarkerOptions().position(destlatlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)));


                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (Marker marker : m) {
                            builder.include(marker.getPosition());
                        }
                        LatLngBounds bounds = builder.build();
                        int padding = 100; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                        googleMap.moveCamera(cu);
                        // googleMap.animateCamera(cu);

                        // Adding route on the map
                        googleMap.addPolyline(rectLine);
                        markerOptions.position(destlatlng);
                        markerOptions.position(startlatlng);
                        markerOptions.draggable(true);
                        isPathShowing = true;
                        //       mHandler.removeCallbacks(mHandlerTask);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double latA = Math.toRadians(lat1);
        double lonA = Math.toRadians(lon1);
        double latB = Math.toRadians(lat2);
        double lonB = Math.toRadians(lon2);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB - lonA)) +
                (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist = ang * 6371;
        return dist;
    }


    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            HashMap<String, String> wait_status = session.getWaitingStatus();
            String waitstatus = wait_status.get(SessionManager.KEY_WAIT_STATUS);
            if (!wait_status.equals("")) {
                if (waitstatus.equals("true")) {

                    timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

                    updatedTime = timeSwapBuff + timeInMilliseconds;
/*
            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            milliseconds = (int) (updatedTime % 1000);
            timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs));
            */

                    secs = (int) (updatedTime / 1000);
                    hours = secs / (60 * 60);
                    mins = secs / 60;
                    secs = secs % 60;
                    if (mins >= 60) {
                        mins = 0;
                    }
                    session.setWaitingTime(String.valueOf(updatedTime));
                    milliseconds = (int) (updatedTime % 1000);
            /*timerValue.setText(hours + ":" + mins + ":"
                    + String.format("%02d", secs));*/
                    timerValue.setText(String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":"
                            + String.format("%02d", secs));
                    System.out.println("thread----------------" + timerValue);

                    customHandler.postDelayed(this, 3 * 1000);
                }
            }
        }

    };


  /*  Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {


            System.out.println("------------jai1---------------map path handler ");
            gps = new GPSTracker(EndTrip.this);
            cd = new ConnectionDetector(EndTrip.this);
            isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {


                    *//*Str_Latitude = String.valueOf(gps.getLatitude());
                    Str_longitude = String.valueOf(gps.getLongitude());

                    postRequest_UpdateProviderLocation(ServiceConstant.UPDATE_CURRENT_LOCATION);*//*
                }
            } else {
                Toast.makeText(EndTrip.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }

            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };*/



   /* Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {

            gps = new GPSTracker(EndTrip.this);
            cd = new ConnectionDetector(EndTrip.this);
            isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {

                    Str_Latitude = String.valueOf(gps.getLatitude());
                    Str_longitude = String.valueOf(gps.getLongitude());

                    postRequest_UpdateProviderLocation(ServiceConstant.UPDATE_CURRENT_LOCATION);
                }
            } else {
                Toast.makeText(EndTrip.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }

            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };*/


    public static final float[] calculateDistanceTo(Location fromLocation, Location toLocation) {
        float[] results = new float[0];
        double startLatitude = fromLocation.getLatitude();
        double startLongitude = fromLocation.getLongitude();
        double endLatitude = toLocation.getLatitude();
        double endLongitude = toLocation.getLongitude();
        fromLocation.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results;
    }


    /*
    public double getDistance(double a[]) {
        double earthRadius = 6371; //kilometers
        double dLat = Math.toRadians(a[2] -a[0] );
        double dLng = Math.toRadians(a[3] - a[1]);
        double b = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(a[0])) * Math.cos(Math.toRadians(a[2])) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(b), Math.sqrt(1-b));
        float dist = (float) (earthRadius * c);

        return dist;
    }
*/


    //-------------------Show Summery fare  Method--------------------
    private void showfaresummerydetails() {

        final MaterialDialog dialog = new MaterialDialog(EndTrip.this);
        View view = LayoutInflater.from(EndTrip.this).inflate(R.layout.fare_summery_alert_dialog, null);
        final TextView Tv_reqest = (TextView) view.findViewById(R.id.requst);
        TextView tv_fare_totalamount = (TextView) view.findViewById(R.id.fare_summery_total_amount);
        TextView tv_ridedistance = (TextView) view.findViewById(R.id.fare_summery_ride_distance_value);
        TextView tv_timetaken = (TextView) view.findViewById(R.id.fare_summery_ride_timetaken_value);
        TextView tv_waittime = (TextView) view.findViewById(R.id.fare_summery_wait_time_value);
        RelativeLayout layout_request_payment = (RelativeLayout) view.findViewById(R.id.layout_faresummery_requstpayment);
        RelativeLayout layout_receive_cash = (RelativeLayout) view.findViewById(R.id.fare_summery_receive_cash_layout);
        tv_fare_totalamount.setText(Str_ridefare);
        tv_ridedistance.setText(Str_ride_distance);
        tv_timetaken.setText(Str_timetaken);
        tv_waittime.setText(Str_waitingtime);
        dialog.setView(view).show();
        //if (Str_need_payment.equalsIgnoreCase("YES")){
        layout_receive_cash.setVisibility(View.VISIBLE);
        layout_request_payment.setVisibility(View.VISIBLE);
        Tv_reqest.setText(EndTrip.this.getResources().getString(R.string.lbel_fare_summery_requestpayment));


        layout_receive_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(EndTrip.this, PaymentPage.class);
                intent.putExtra("amount", Str_ridefare);
                intent.putExtra("rideid", Str_rideid);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);



             /*   Intent intent = new Intent(EndTrip.this, OtpPage.class);
                intent.putExtra("rideid", Str_rideid);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/

            }
        });

        layout_request_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(EndTrip.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {

                    if (Tv_reqest.getText().toString().equalsIgnoreCase(EndTrip.this.getResources().getString(R.string.lbel_fare_summery_requestpayment))) {
                        postRequest_Reqqustpayment(ServiceConstant.request_paymnet_url);
                        System.out.println("arrived------------------" + ServiceConstant.request_paymnet_url);
                    } else {
                        Intent intent = new Intent(EndTrip.this, RatingsPage.class);
                        intent.putExtra("rideid", Str_rideid);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                } else {
                    Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });

    }

    private void Alert1(String title, String message) {
        final PkDialog mDialog = new PkDialog(EndTrip.this);
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

    private void showfaresummerydetails1() {

        final MaterialDialog dialog = new MaterialDialog(EndTrip.this);
        View view = LayoutInflater.from(EndTrip.this).inflate(R.layout.fare_summery_alert_dialog, null);
        final TextView Tv_reqest = (TextView) view.findViewById(R.id.requst);
        TextView tv_fare_totalamount = (TextView) view.findViewById(R.id.fare_summery_total_amount);
        TextView tv_ridedistance = (TextView) view.findViewById(R.id.fare_summery_ride_distance_value);
        TextView tv_timetaken = (TextView) view.findViewById(R.id.fare_summery_ride_timetaken_value);
        TextView tv_waittime = (TextView) view.findViewById(R.id.fare_summery_wait_time_value);
        RelativeLayout layout_request_payment = (RelativeLayout) view.findViewById(R.id.layout_faresummery_requstpayment);
        RelativeLayout layout_receive_cash = (RelativeLayout) view.findViewById(R.id.fare_summery_receive_cash_layout);
        tv_fare_totalamount.setText(Str_ridefare);
        tv_ridedistance.setText(Str_ride_distance);
        tv_timetaken.setText(Str_timetaken);
        tv_waittime.setText(Str_waitingtime);
        dialog.setView(view).show();


        // if (Str_need_payment.equalsIgnoreCase("YES")){

        layout_receive_cash.setVisibility(View.GONE);
        layout_request_payment.setVisibility(View.VISIBLE);
        Tv_reqest.setText(EndTrip.this.getResources().getString(R.string.lbel_fare_summery_requestpayment));

        //  }else{
        // layout_receive_cash.setVisibility(View.GONE);
        //  Tv_reqest.setText(EndTrip.this.getResources().getString(R.string.alert_label_ok));

        // }
//
        /*layout_receive_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndTrip.this, OtpPage.class);
                intent.putExtra("rideid", Str_rideid);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });*/

        layout_request_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(EndTrip.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {

                    if (Tv_reqest.getText().toString().equalsIgnoreCase(EndTrip.this.getResources().getString(R.string.lbel_fare_summery_requestpayment))) {
                        postRequest_Reqqustpayment(ServiceConstant.request_paymnet_url);
                        System.out.println("arrived------------------" + ServiceConstant.request_paymnet_url);
                    } else {
                        Intent intent = new Intent(EndTrip.this, RatingsPage.class);
                        intent.putExtra("rideid", Str_rideid);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                } else {
                    Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });

    }


    private void showfaresummerydetails3() {

        final MaterialDialog dialog = new MaterialDialog(EndTrip.this);
        View view = LayoutInflater.from(EndTrip.this).inflate(R.layout.fare_summery_alert_dialog, null);
        final TextView Tv_reqest = (TextView) view.findViewById(R.id.requst);
        TextView tv_fare_totalamount = (TextView) view.findViewById(R.id.fare_summery_total_amount);
        TextView tv_ridedistance = (TextView) view.findViewById(R.id.fare_summery_ride_distance_value);
        TextView tv_timetaken = (TextView) view.findViewById(R.id.fare_summery_ride_timetaken_value);
        TextView tv_waittime = (TextView) view.findViewById(R.id.fare_summery_wait_time_value);
        RelativeLayout layout_request_payment = (RelativeLayout) view.findViewById(R.id.layout_faresummery_requstpayment);
        RelativeLayout layout_receive_cash = (RelativeLayout) view.findViewById(R.id.fare_summery_receive_cash_layout);
        tv_fare_totalamount.setText(Str_ridefare);
        tv_ridedistance.setText(Str_ride_distance);
        tv_timetaken.setText(Str_timetaken);
        tv_waittime.setText(Str_waitingtime);
        dialog.setView(view).show();


        // if (Str_need_payment.equalsIgnoreCase("YES")){

        layout_receive_cash.setVisibility(View.VISIBLE);
        layout_request_payment.setVisibility(View.GONE);
        // Tv_reqest.setText(EndTrip.this.getResources().getString(R.string.lbel_fare_summery_requestpayment));

        //  }else{
        // layout_receive_cash.setVisibility(View.GONE);
        //  Tv_reqest.setText(EndTrip.this.getResources().getString(R.string.alert_label_ok));

        // }
//
        /*layout_receive_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndTrip.this, OtpPage.class);
                intent.putExtra("rideid", Str_rideid);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });*/

        layout_receive_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EndTrip.this, PaymentPage.class);
                intent.putExtra("amount", Str_ridefare);
                intent.putExtra("rideid", Str_rideid);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

    }


    private void showfaresummerydetails2() {

        final MaterialDialog dialog = new MaterialDialog(EndTrip.this);
        View view = LayoutInflater.from(EndTrip.this).inflate(R.layout.fare_summery_alert_dialog, null);
        final TextView Tv_reqest = (TextView) view.findViewById(R.id.requst);
        TextView tv_fare_totalamount = (TextView) view.findViewById(R.id.fare_summery_total_amount);
        TextView tv_ridedistance = (TextView) view.findViewById(R.id.fare_summery_ride_distance_value);
        TextView tv_timetaken = (TextView) view.findViewById(R.id.fare_summery_ride_timetaken_value);
        TextView tv_waittime = (TextView) view.findViewById(R.id.fare_summery_wait_time_value);
        RelativeLayout layout_request_payment = (RelativeLayout) view.findViewById(R.id.layout_faresummery_requstpayment);
        RelativeLayout layout_receive_cash = (RelativeLayout) view.findViewById(R.id.fare_summery_receive_cash_layout);
        tv_fare_totalamount.setText(Str_ridefare);
        tv_ridedistance.setText(Str_ride_distance);
        tv_timetaken.setText(Str_timetaken);
        tv_waittime.setText(Str_waitingtime);
        dialog.setView(view).show();


        // if (Str_need_payment.equalsIgnoreCase("YES")){

        layout_receive_cash.setVisibility(View.GONE);
        layout_request_payment.setVisibility(View.VISIBLE);
        Tv_reqest.setText(EndTrip.this.getResources().getString(R.string.lbel_notification_ok));

        //  }else{
        // layout_receive_cash.setVisibility(View.GONE);
        //  Tv_reqest.setText(EndTrip.this.getResources().getString(R.string.alert_label_ok));

        // }
//
        /*layout_receive_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndTrip.this, OtpPage.class);
                intent.putExtra("rideid", Str_rideid);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });*/

        layout_request_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(EndTrip.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {


                    Intent intent = new Intent(EndTrip.this, RatingsPage.class);
                    intent.putExtra("rideid", Str_rideid);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                } else {
                    Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });

    }

    //-----------------------Code for begin trip post request-----------------
    private void PostRequest(String Url) {
        dialog = new Dialog(EndTrip.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------endtrip----------------" + Url);
        ArrayList<String> travel_history = myDBHelper.getDataEndTrip(Str_rideid);
        StringBuilder builder = new StringBuilder();
        for (String string : travel_history) {
            builder.append("," + string);
        }
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);
        jsonParams.put("ride_id", Str_rideid);
        jsonParams.put("drop_lat", String.valueOf(MyCurrent_lat));
        jsonParams.put("drop_lon", String.valueOf(MyCurrent_long));
        jsonParams.put("distance", String.valueOf(dis / 1000));
        jsonParams.put("wait_time", String.valueOf(timerValue.getText().toString()));
        jsonParams.put("travel_history", builder.toString());
        System.out.println("-----------endtrip---jsonParams-------------------" + String.valueOf(dis / 1000));

        System.out.println("-----------endtrip---total_distance-------------------" + builder.toString());

        mRequest = new ServiceRequest(EndTrip.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                Log.e("end", response);

                System.out.println("endtrip---------" + response);

                //  String Str_status = "",Str_response="",Str_ridefare="",Str_timetaken="",Str_waitingtime="",Str_currency="",Str_ride_distance="";

                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    Str_response = object.getString("response");

                    if (Str_status.equalsIgnoreCase("0")) {
                        Zero_response = object.getString("ride_view");
                        Str_response = object.getString("response");
                    } else {


                        JSONObject jsonObject = object.getJSONObject("response");
                        JSONObject jobject = jsonObject.getJSONObject("fare_details");
                        Str_need_payment = jsonObject.getString("need_payment");
                        str_recievecash = jsonObject.getString("receive_cash");
                        str_requestpay = jsonObject.getString("req_payment");
                        Str_currency = jobject.getString("currency");

                        //Currency currencycode = Currency.getInstance(getLocale(Str_currency));
                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Str_currency);

                        Str_ridefare = sCurrencySymbol + jobject.getString("ride_fare");
                        Str_timetaken = jobject.getString("ride_duration");
                        Str_waitingtime = jobject.getString("waiting_duration");
                        Str_ride_distance = jobject.getString("ride_distance");
                        Str_need_payment = jobject.getString("need_payment");


                        Log.d("RECEIVE", str_recievecash);

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();


                if (Str_status.equalsIgnoreCase("1")) {
                    myDBHelper.Delete("");
                    //  endTripHandler.removeCallbacks(endTripRunnable);
                    myDBHelper.insertDriverStatus("0");
                    session.setWaitingStatus("0");
                    session.setWaitingTime("0");
                    if (Str_need_payment.equalsIgnoreCase("YES")) {
                        System.out.println("sucess------------" + Str_need_payment);
                        /*if (str_recievecash.matches("Enable")) {
                            showfaresummerydetails();
                        } else {
                            showfaresummerydetails1();
                        }

                        if (str_requestpay.matches("Enable")) {
                            showfaresummerydetails3();
                        } else {
                            showfaresummerydetails();
                        }*/
                        if (str_recievecash.matches("Enable") && str_requestpay.matches("Enable")) {
                            showfaresummerydetails();
                        } else if (str_recievecash.matches("Enable") && str_requestpay.matches("Disable")) {
                            showfaresummerydetails3();
                        } else if (str_requestpay.matches("Enable") && str_recievecash.matches("Disable")) {
                            showfaresummerydetails1();
                        }


                    } else {
                        showfaresummerydetails2();
                    }

                } else {


                    if (Str_status.equalsIgnoreCase("0")) {

                        if (Zero_response.equals("stay")) {
                            Alert1(getString(R.string.alert_sorry_label_title), Str_response);
                        }
                        if (Zero_response.equals("next")) {
                            //  endTripHandler.removeCallbacks(endTripRunnable);
                            myDBHelper.insertDriverStatus("0");

                            if (Str_need_payment.equalsIgnoreCase("YES")) {
                                System.out.println("sucess------------" + Str_need_payment);

                                if (str_recievecash.matches("Enable") && str_requestpay.matches("Enable")) {
                                    showfaresummerydetails();
                                } else if (str_recievecash.matches("Enable") && str_requestpay.matches("Disable")) {
                                    showfaresummerydetails3();
                                } else if (str_requestpay.matches("Enable") && str_recievecash.matches("Disable")) {
                                    showfaresummerydetails1();
                                } /*else {
                                    showfaresummerydetails();
                                }*/

                            } else {
                                showfaresummerydetails2();
                            }
                        }
                        if (Zero_response.equals("detail")) {
                            finish();
                            Intent intent = new Intent(EndTrip.this, TripSummaryDetail.class);
                            intent.putExtra("ride_id", Str_rideid);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                        if (Zero_response.equals("home")) {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.finish.ArrivedTrip");
                            sendBroadcast(broadcastIntent);
                            Intent broadcastIntent1 = new Intent();
                            broadcastIntent1.setAction("com.finish.BeginTrip");
                            sendBroadcast(broadcastIntent1);
                            Intent broadcastIntent_userinfo = new Intent();
                            broadcastIntent_userinfo.setAction("com.finish.UserInfo");
                            sendBroadcast(broadcastIntent_userinfo);

                            Intent broadcastIntent_tripdetail = new Intent();
                            broadcastIntent_tripdetail.setAction("com.finish.tripsummerydetail");
                            sendBroadcast(broadcastIntent_tripdetail);

                            Intent broadcastIntent_drivermap = new Intent();
                            broadcastIntent_drivermap.setAction("com.finish.canceltrip.DriverMapActivity");
                            sendBroadcast(broadcastIntent_drivermap);

                            finish();

                        }
                    }


                    //       Alert1(getResources().getString(R.string.alert_sorry_label_title), Str_response);
                }


                dialog.dismiss();

            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }

        });
    }

 /*           private void PostRequest1(String Url) {
        dialog = new Dialog(EndTrip.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title=(TextView)dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));


        postrequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("end", response);

                        System.out.println("endtrip---------"+response);

                      //  String Str_status = "",Str_response="",Str_ridefare="",Str_timetaken="",Str_waitingtime="",Str_currency="",Str_ride_distance="";

                       try {
                            JSONObject object = new JSONObject(response);
                            Str_status = object.getString("status");
                           Str_response = object.getString("response");

                           JSONObject jsonObject= object.getJSONObject("response");
                           JSONObject jobject = jsonObject.getJSONObject("fare_details");

                           Str_currency = jobject.getString("currency");

                           //Currency currencycode = Currency.getInstance(getLocale(Str_currency));
                           sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Str_currency);

                           Str_ridefare = sCurrencySymbol + jobject.getString("ride_fare");
                           Str_timetaken = jobject.getString("ride_duration");
                           Str_waitingtime = jobject.getString("waiting_duration");
                           Str_ride_distance = jobject.getString("ride_distance");
                           Str_need_payment = jobject.getString("need_payment");

                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        if (Str_status.equalsIgnoreCase("1")){

                            endTripHandler.removeCallbacks(endTripRunnable);

                            if (Str_need_payment.equalsIgnoreCase("YES")){
                                System.out.println("sucess------------"+Str_need_payment);
                                showfaresummerydetails();
                            }else{
                                showfaresummerydetails();
                            }

                        }else {
                            Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);

                        }

                        dialog.dismiss();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorResponse.VolleyError(EndTrip.this, error);
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-agent", ServiceConstant.useragent);
                headers.put("isapplication",ServiceConstant.isapplication);
                headers.put("applanguage",ServiceConstant.applanguage);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("driver_id",driver_id);
                jsonParams.put("ride_id",Str_rideid);
                jsonParams.put("drop_lat",String.valueOf(MyCurrent_lat));
                jsonParams.put("drop_lon",String.valueOf(MyCurrent_long));
                jsonParams.put("distance",String.valueOf(dis/1000));
                jsonParams.put("wait_time","0");

                //jsonParams.put("wait_time",String.valueOf(mins).replace(":","."));
               *//* jsonParams.put("wait_time",String.valueOf( String.valueOf("" + mins + ":"
                        + String.format("%02d", secs) + ":"
                        + String.format("%03d", milliseconds))).replace(":","."));*//*


                System.out
                        .println("--------------driver_id-------------------"
                                + driver_id);
                System.out
                        .println("--------------drop_lat-------------------"
                                + String.valueOf(MyCurrent_lat));
                System.out
                        .println("--------------drop_lon-------------------"
                                + String.valueOf(MyCurrent_long));

                System.out
                        .println("--------------postdistance-------------------"
                                +String.valueOf(dis/1000));




                return jsonParams;
            }
        };
         postrequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                 DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                 DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
         postrequest.setShouldCache(false);

        AppController.getInstance().addToRequestQueue(postrequest);
    }*/


    //-----------------------Code for arrived post request-----------------
    private void postRequest_Reqqustpayment(String Url) {
        dialog = new Dialog(EndTrip.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);

        dialog_title.setText(getResources().getString(R.string.action_loading));
     /*  LinearLayout main = (LinearLayout)findViewById(R.id.main_layout);
        View view = getLayoutInflater().inflate(R.layout.waiting, main,false);
        main.addView(view);
*/

        System.out.println("-------------endtrip----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);
        jsonParams.put("ride_id", Str_rideid);

        System.out
                .println("--------------driver_id-------------------"
                        + driver_id);


        System.out
                .println("--------------ride_id-------------------"
                        + Str_rideid);

        mRequest = new ServiceRequest(EndTrip.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                Log.e("requestpayment", response);

                System.out.println("response---------" + response);

                String Str_status = "", Str_response = "", Str_currency = "", Str_rideid = "", Str_action = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Str_response = object.getString("response");
                    Str_status = object.getString("status");

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Str_status.equalsIgnoreCase("0")) {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);

                } else {
                    Alert(getResources().getString(R.string.label_pushnotification_cashreceived), Str_response);
                }
            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();
            }

        });

    }


/*            private void postRequest_Reqqustpayment1(String Url) {
        dialog = new Dialog(EndTrip.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        System.out.println("loadin-----------");
        TextView dialog_title=(TextView)dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));


        postrequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("requestpayment", response);

                        System.out.println("response---------"+response);

                        String Str_status = "",Str_response="",Str_currency="",Str_rideid="",Str_action="";

                        try {
                            JSONObject object = new JSONObject(response);
                            Str_response = object.getString("response");
                            Str_status = object.getString("status");

                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        if (Str_status.equalsIgnoreCase("0"))
                        {
                            Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);

                        }else{
                            Alert(getResources().getString(R.string.label_pushnotification_cashreceived), Str_response);

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorResponse.VolleyError(EndTrip.this, error);
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-agent",ServiceConstant.useragent);
                headers.put("isapplication",ServiceConstant.isapplication);
                headers.put("applanguage",ServiceConstant.applanguage);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("driver_id", driver_id);
                jsonParams.put("ride_id", Str_rideid);

                System.out
                        .println("--------------driver_id-------------------"
                                + driver_id);


                System.out
                        .println("--------------ride_id-------------------"
                                + Str_rideid);


                return jsonParams;
            }
        };
        postrequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        postrequest.setShouldCache(false);

        AppController.getInstance().addToRequestQueue(postrequest);
    }*/

    //method to convert currency code to currency symbol
    private static Locale getLocale(String strCode) {

        for (Locale locale : NumberFormat.getAvailableLocales()) {
            String code = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
            if (strCode.equals(code)) {
                return locale;
            }
        }
        return null;
    }


    public void onRoutingSuccess(PolylineOptions mPolyOptions) {
        PolylineOptions polyoptions = new PolylineOptions();
        polyoptions.color(Color.BLUE);
        polyoptions.width(10);
        polyoptions.addAll(mPolyOptions.getPoints());
        googleMap.addPolyline(polyoptions);
    }


    //-----------------Move Back on  phone pressed  back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            // nothing
            return true;
        }
        return false;
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
                            status.startResolutionForResult(EndTrip.this, REQUEST_LOCATION);
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
        if (requestCode == REQUEST_LOCATION) {
            System.out.println("----------inside request location------------------");

            switch (resultCode) {
                case Activity.RESULT_OK: {
                    Toast.makeText(EndTrip.this, getResources().getString(R.string.end_trip_toast_loaction_enable), Toast.LENGTH_LONG).show();
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

        }
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(EndTrip.this)) {
                    Toast.makeText(EndTrip.this, "SYSTEM_ALERT_WINDOW permission not granted...", Toast.LENGTH_SHORT).show();
                } else {
                    moveNavigation();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress > 95) {
            seekBar.setThumb(getResources().getDrawable(R.drawable.slidetounlock_arrow));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Bt_slider.setVisibility(View.INVISIBLE);


    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        shimmer = new Shimmer();
        if (seekBar.getProgress() < 70) {
            seekBar.setProgress(0);
            sliderSeekBar.setBackgroundResource(R.color.app_color);
            Bt_slider.setVisibility(View.VISIBLE);
            Bt_slider.setText(getResources().getString(R.string.lbel_endtrip));
            shimmer.start(Bt_slider);
        } else if (seekBar.getProgress() > 70) {
            seekBar.setProgress(100);
            Bt_slider.setVisibility(View.VISIBLE);
            Bt_slider.setText(getResources().getString(R.string.lbel_endtrip));
            shimmer.start(Bt_slider);
            sliderSeekBar.setVisibility(View.VISIBLE);
            System.out.println("------------------sliding completed----------------");

            cd = new ConnectionDetector(EndTrip.this);
            isInternetPresent = cd.isConnectingToInternet();

            if (isInternetPresent) {
                PostRequest(ServiceConstant.endtrip_url);
                System.out.println("end------------------" + ServiceConstant.endtrip_url);
            } else {

                Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
            }
        }
    }


    //-----------------------Update current Location for notification  Post Request-----------------
    private void postRequest_UpdateProviderLocation(String Url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", Str_rideid);
        jsonParams.put("latitude", Str_Latitude);
        jsonParams.put("longitude", Str_longitude);
        jsonParams.put("driver_id", driver_id);

        System.out.println("-------------Endtripride_id----------------" + Str_longitude);
        System.out.println("-------------Endtriplatitude----------------" + Str_Latitude);
        System.out.println("-------------Endtriplongitude----------------" + Str_longitude);

        System.out.println("-------------latlongupdate----------------" + Url);
        mRequest = new ServiceRequest(EndTrip.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                Log.e("updatelocation", response);

                System.out.println("-------------latlongupdate----------------" + response);

            }

            @Override
            public void onErrorListener() {

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        System.gc();
        if (chat != null) {
            chat.close();
        }
        //     mHandler.removeCallbacks(mHandlerTask);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //     mHandler.removeCallbacks(mHandlerTask);
    }

    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private boolean checkWriteExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private void requestNavigationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_NAVIGATION_CODE);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE, android.Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + Str_mobilno));
                    startActivity(callIntent);
                }
                break;

            case PERMISSION_REQUEST_NAVIGATION_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(EndTrip.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234);
                    } else {
                        moveNavigation();
                    }
                }
        }
    }

    private void moveNavigation() {


        if (isGoogleMapsInstalled()) {


            if (!droplocation[0].equalsIgnoreCase("null") && !droplocation[1].equalsIgnoreCase("null")) {

                if (!isMyServiceRunning(GoogleNavigationService.class)) {
                    startService(new Intent(getApplicationContext(), GoogleNavigationService.class));
                }

                session.setGoogleNavicationValue(Str_name, Str_rideid, Str_mobilno, address, beginAddress, Str_User_Id, Str_Interrupt, Str_profilpic);

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(String.format("google.navigation:ll=%s,%s%s", Double.parseDouble(droplocation[0]), Double.parseDouble(droplocation[1]), "&mode=c")));
                startActivity(intent);
                System.out.println("----jai--1-----------");
//            addBackLayout();
            } else {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.endtrip_alert_lbl), Toast.LENGTH_LONG).show();
            }

        } else {

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_label_no_google_map_installed), Toast.LENGTH_LONG).show();

        }


        System.out.println("lat and long ------jai-------------" + droplocation);
   /* Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(String.format("google.navigation:ll=%s,%s%s", Double.parseDouble(droplocation[0]), Double.parseDouble(droplocation[1]), "&mode=c")));
    startActivity(intent);
    addBackLayout();*/
    }

    //Adding back layout for Voice Navigation
    public void addBackLayout() {
        System.out.println("----jai--2-----------");
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = getLayoutInflater();
        final View viewa = inflater.inflate(R.layout.navigation_back_layout, null);
        ImageView im;
        LinearLayout l = (LinearLayout) viewa.findViewById(R.id.linear);
        im = (ImageView) viewa.findViewById(R.id.imageView_kill);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("----jai--5-----------");
            }
        });
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("----jai--6-----------");
            }
        });
        viewa.findViewById(R.id.imageView_kill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("----jai--3-----------");
                viewa.findViewById(R.id.imageView_kill).setVisibility(View.GONE);
                addBackLayout_two();
                viewa.setVisibility(View.GONE);
            }
        });
        viewa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("----jai--4-----------");
            }
        });
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 600;
        windowManager.addView(viewa, params);

    }


    public void addBackLayout_two() {
        System.out.println("----jai--3-----------");

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.navigation_back_full_view, null);
        view.findViewById(R.id.imageView_kill_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent myIntent = new Intent(v.getContext(), EndTrip.class);
                    myIntent.addCategory(Intent.CATEGORY_HOME);
                    myIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    myIntent.addCategory(Intent.CATEGORY_MONKEY);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.putExtra("name", Str_name);
                    myIntent.putExtra("rideid", Str_rideid);
                    myIntent.putExtra("mobilno", Str_mobilno);
                    myIntent.putExtra("pickuplatlng", address);
                    myIntent.putExtra("startpoint", beginAddress);
                    myIntent.putExtra("user_id", Str_User_Id);
                    myIntent.putExtra("interrupted", Str_Interrupt);
                    myIntent.putExtra("user_image", Str_profilpic);
                    //   intent.putExtra("user_image",Str_profilpic);
                    //      myIntent.putExtra("interrupted",Str_Interrupt);
                    startActivity(myIntent);
                    view.setVisibility(View.GONE);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 600;
        windowManager.addView(view, params);
    }

    private void AlertNavigation(String title, String message) {
        final PkDialog mDialog = new PkDialog(EndTrip.this);
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


    private boolean isGoogleMapsInstalled() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


}
