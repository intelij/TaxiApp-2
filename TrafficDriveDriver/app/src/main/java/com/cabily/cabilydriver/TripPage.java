package com.cabily.cabilydriver;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.cabily.cabilydriver.Pojo.FarePojo;
import com.cabily.cabilydriver.Pojo.MultipleLatLongPojo;
import com.cabily.cabilydriver.Pojo.PoolRateCard;
import com.cabily.cabilydriver.Pojo.UserPojo;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.CurrencySymbolConverter;
import com.cabily.cabilydriver.Utils.GPSTracker;
import com.cabily.cabilydriver.Utils.GoogleNavigationService;
import com.cabily.cabilydriver.Utils.HorizontalListView;
import com.cabily.cabilydriver.Utils.RoundedImageView;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.adapter.FareAdaptewr;
import com.cabily.cabilydriver.adapter.SelectSeatAdapter;
import com.cabily.cabilydriver.adapter.UserListAdapter;
import com.cabily.cabilydriver.googlemappath.GMapV2GetRouteDirection;
import com.cabily.cabilydriver.subclass.SubclassActivity;
import com.cabily.cabilydriver.widgets.PkDialog;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

import static com.cabily.cabilydriver.NewTripAlert.mediaPlayer1;

/**
 * Created by jayachandran on 7/6/2017.
 */

public class TripPage extends SubclassActivity implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SeekBar.OnSeekBarChangeListener {

    private RefreshReceiver finishReceiver;
    private LocationRequest mLocationRequest;
    public static Location myLocation;
    private GoogleApiClient mGoogleApiClient;
    private SessionManager session;
    GMapV2GetRouteDirection v2GetRouteDirection;
    LatLng fromPosition;
    LatLng toPosition;
    MarkerOptions markerOptions;
    Location location;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GoogleMap googleMap;
    private GPSTracker gps;
    private RelativeLayout alert_layout, Rl_traffic;
    PendingResult<LocationSettingsResult> result;
    private Marker pickupmarker, dropmarker;
    MarkerOptions marker;
    private GEODBHelper myDBHelper;
    String traffic_status;
    String base64;
    Bitmap bmp;
    ImageButton traffic_button;

    private String droplocation[];
    private String startlocation[];
    private LatLng destlatlng, startlatlng;
    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    private float distance_to = 0;
    private LatLng newLatLng, oldLatLng;

    double myMovingDistance = 0.0;
    private boolean isPathShowing;

    SeekBar sliderSeekBar;
    ShimmerButton Bt_slider;
    Shimmer shimmer;
    CardView destination_address_layout, driverDetail;
    private ServiceRequest mRequest;
    private Dialog dialog;
    private String driver_id = "", Str_Latitude = "", Str_longitude = "", active_ride = "", Str_user_phone_no = "";
    private TextView Tv_Address, tv_start, tv_stop, tv_timer, tv_drop_address, tv_seat_count;
    private RelativeLayout Rl_layout_userinfo, Rl_layout_arrived, wait_time_layout, share_seat_layout;
    private ImageView phone_call, Rl_layout_enable_voicenavigation, wait_time_start, user_info, wait_time_stop;
    ArrayList<UserPojo> Userlist = new ArrayList<UserPojo>();
    ArrayList<MultipleLatLongPojo> multiple_latlon_list = new ArrayList<MultipleLatLongPojo>();
    private HorizontalListView listview;
    UserListAdapter adapter;

    private ArrayList<LatLng> wayPointList;
    private LatLngBounds.Builder wayPointBuilder;
    private List<Polyline> polyLines;
    final int PERMISSION_REQUEST_CODE = 111;
    final int PERMISSION_REQUEST_NAVIGATION_CODE = 222;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private Dialog cantact_dialog, sms_popup;
    ImageButton refresh_button;

    public static final int DropLocationRequestCode = 5000;
    private String str_drop_location = "";
    private String str_drop_Latitude = "";
    private String str_drop_Longitude = "";
    private String Str_Interrupt = "";

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private boolean waitingStatus = false;


    private int mins;
    private int secs;
    private int hours;

    private int milliseconds;

    private Boolean idFareAvailable = false;

    ArrayList<FarePojo> farearray = new ArrayList<FarePojo>();
    ArrayList<PoolRateCard> poolRateCardList = new ArrayList<PoolRateCard>();
    private String carIcon = "";
    private String ride_type = "";

    LatLngInterpolator mLatLngInterpolator;
    Marker drivermarker;
    Location oldLocation;
    private TextView user_name;
    private RoundedImageView user_icon;
    private RelativeLayout rl;
    private Marker currentMarker;


    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_CLASS_TrackYourRide_REFRESH_page")) {

              /* Intent broadcastIntent_trip = new Intent();
               broadcastIntent_trip.setAction("com.finish.tripPage");
               context.sendBroadcast(broadcastIntent_trip);

               Intent trip_intent = new Intent(context, TripPage.class);
               context.startActivity(trip_intent);*/
//                if (intent.getExtras() != null) {

                session = new SessionManager(TripPage.this);
                HashMap<String, String> user = session.getUserDetails();
                driver_id = user.get(SessionManager.KEY_DRIVERID);
                System.out.println("--------------Jai Refresh-------------------------");

                if (isInternetPresent) {
                    postRequest(ServiceConstant.trip_Track_Driver);
                } else {
                    Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                }


//                }

            } else if (intent.getAction().equals("com.app.finish.ArrivedTrip")) {
                finish();
            }


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_layout_final);

        finishReceiver = new RefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.app.finish.ArrivedTrip");
        filter.addAction("com.package.ACTION_CLASS_TrackYourRide_REFRESH_page");
        registerReceiver(finishReceiver, filter);

        initialize();
        try {
            setLocationRequest();
            buildGoogleApiClient();
            initilizeMap();
        } catch (Exception e) {
        }


        destination_address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSTracker gps = new GPSTracker(TripPage.this);
                if (gps.canGetLocation()&& gps.isgpsenabled()) {
                    Intent intent = new Intent(TripPage.this, DropLocationSelect.class);
                    startActivityForResult(intent, DropLocationRequestCode);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Toast.makeText(TripPage.this, "Enable Gps", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });


        user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("-----------Userlist----------------" + Userlist.size());

                System.out.println("-----------active_ride----------------" + active_ride);
                for (int i = 0; i < Userlist.size(); i++) {
                    if (active_ride.equals(Userlist.get(i).getRide_id())) {
                        Intent intent = new Intent(TripPage.this, UserInfo.class);
                        intent.putExtra("user_name", Userlist.get(i).getUser_name());
                        intent.putExtra("user_phoneno", Userlist.get(i).getPhone_number());
                        intent.putExtra("user_rating", Userlist.get(i).getUser_review());
                        intent.putExtra("user_image", Userlist.get(i).getUser_image());
                        intent.putExtra("RideId", Userlist.get(i).getRide_id());
                        intent.putExtra("Btn_group", Userlist.get(i).getBtn_group());
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    }

                }


            }

        });


        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wait_time_stop.setVisibility(View.VISIBLE);
                wait_time_start.setVisibility(View.INVISIBLE);
                tv_stop.setVisibility(View.VISIBLE);
                tv_start.setVisibility(View.GONE);
                //    layout_timer.setVisibility(View.VISIBLE);
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                waitingStatus = true;
                session.setWaitingStatus("true");
                Str_Interrupt = "Yes";
            }
        });
        tv_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wait_time_stop.setVisibility(View.INVISIBLE);
                wait_time_start.setVisibility(View.VISIBLE);
                tv_start.setVisibility(View.VISIBLE);
                tv_stop.setVisibility(View.GONE);
                session.setWaitingStatus("false");
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
                waitingStatus = false;

            }
        });


        wait_time_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wait_time_stop.setVisibility(View.VISIBLE);
                wait_time_start.setVisibility(View.INVISIBLE);
                tv_stop.setVisibility(View.VISIBLE);
                tv_start.setVisibility(View.GONE);
                //    layout_timer.setVisibility(View.VISIBLE);
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                waitingStatus = true;
                session.setWaitingStatus("true");
                Str_Interrupt = "Yes";
            }
        });
        wait_time_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wait_time_stop.setVisibility(View.INVISIBLE);
                wait_time_start.setVisibility(View.VISIBLE);
                tv_start.setVisibility(View.VISIBLE);
                tv_stop.setVisibility(View.GONE);
                session.setWaitingStatus("false");
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
                waitingStatus = false;
            }
        });
        phone_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("-----------Userlist----------------" + Userlist.size());

                for (int i = 0; i < Userlist.size(); i++) {
                    System.out.println("-----------active_ride--------getRide_id--------" + active_ride + Userlist.get(i).getRide_id());
                    if (active_ride.equals(Userlist.get(i).getRide_id())) {
                        chooseContactOptions(Userlist.get(i).getRide_id(), Userlist.get(i).getPhone_number());
                    }

                }
            }
        });

        refresh_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //feb 1
                //  ChatingService.startDriverAction(DriverMapActivity.this);
                setLocationRequest();
                buildGoogleApiClient();


                if (gps.canGetLocation()&& gps.isgpsenabled()) {
                    double Dlatitude = gps.getLatitude();
                    double Dlongitude = gps.getLongitude();

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                   /* if (bmp != null) {
                        LatLng toPosition = new LatLng(Dlatitude, Dlongitude);

                        if (drivermarker != null) {
                            drivermarker.remove();
                        }
                        drivermarker = googleMap.
                                addMarker(new MarkerOptions()
                                        .position(new LatLng(Dlatitude, Dlongitude))
                                        .icon(BitmapDescriptorFactory
                                                .fromBitmap(bmp)));


                    }*/
                }

            }
        });
        share_seat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < Userlist.size(); i++) {
                    if (active_ride.equals(Userlist.get(i).getRide_id())) {
                        //  no_of_seat=Userlist.get(i).getNo_of_seat();

                        select_Shareseat_Dialog(Userlist.get(i).getMax_no_of_seat(), i);
                    }
                }
            }
        });
        Rl_layout_enable_voicenavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (!waitingStatus) {
                    if (Build.VERSION.SDK_INT >= 23) {

                        if (!checkWriteExternalStoragePermission()) {
                            requestNavigationPermission();
                        } else {
                            if (!Settings.canDrawOverlays(TripPage.this)) {

                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);

                            } else {

                                System.out.println("-----------Userlist----------------" + Userlist.size());

                                for (int i = 0; i < Userlist.size(); i++) {
                                    System.out.println("-----------active_ride--------getRide_id--------" + active_ride + Userlist.get(i).getRide_id());
                                    if (active_ride.equals(Userlist.get(i).getRide_id())) {
                                        if (Userlist.get(i).getBtn_group().equals("2")) {
                                            moveNavigationPickup(Userlist.get(i).getPickup_lat(), Userlist.get(i).getPickup_lon());
                                        } else {
                                            moveNavigationdrop(Userlist.get(i).getDrop_lat(), Userlist.get(i).getDrop_lon());
                                        }
                                    }

                                }


                                //  moveNavigation();


                            }
                        }
                    } else {

                        System.out.println("-----------Userlist----------------" + Userlist.size());

                        for (int i = 0; i < Userlist.size(); i++) {
                            System.out.println("-----------active_ride--------getRide_id--------" + active_ride + Userlist.get(i).getRide_id());
                            if (active_ride.equals(Userlist.get(i).getRide_id())) {
                                if (Userlist.get(i).getBtn_group().equals("2")) {
                                    moveNavigationPickup(Userlist.get(i).getPickup_lat(), Userlist.get(i).getPickup_lon());
                                } else {
                                    moveNavigationdrop(Userlist.get(i).getDrop_lat(), Userlist.get(i).getDrop_lon());
                                }
                            }

                        }


                    }
               /* } else {
                    AlertNavigation(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.voice_navigationlabel_continue));
                }*/


            }
        });


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


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                System.out.println("-----------active_ride---------position-----------" + Userlist.get(position).getRide_id() + "postion" + position);
                active_ride = Userlist.get(position).getRide_id();
                for (int i = 0; i < Userlist.size(); i++) {
                    if (position == i) {
                        Userlist.get(position).setActive_ride_id(active_ride);
                    } else {
                        Userlist.get(i).setActive_ride_id("");
                    }
                }
                if (Userlist.get(position).getBtn_group().equalsIgnoreCase("2")) {
                    Arrived(Userlist.get(position).getPickup_location());
                } else if (Userlist.get(position).getBtn_group().equalsIgnoreCase("3")) {
                    if (!Userlist.get(position).getDrop_location().equals("")) {
                        Begin(Userlist.get(position).getDrop_location(), Userlist.get(position).getNo_of_seat(), Userlist.get(position).getMax_no_of_seat(), Userlist.get(position).getRide_type());
                    } else {
                        Begin("", Userlist.get(position).getNo_of_seat(), Userlist.get(position).getMax_no_of_seat(), Userlist.get(position).getRide_type());
                    }

                } else if (Userlist.get(position).getBtn_group().equalsIgnoreCase("4")) {
                    if (!Userlist.get(position).getDrop_location().equals("")) {
                        End(Userlist.get(position).getDrop_location(), Userlist.get(position).getRide_type());
                    }
                } else if (Userlist.get(position).getBtn_group().equalsIgnoreCase("5")) {

                    if (isMyServiceRunning(GoogleNavigationService.class)) {
                        Intent serviceIntent = new Intent(getApplicationContext(), GoogleNavigationService.class);
                        stopService(serviceIntent);
                    }
                    idFareAvailable = true;
                    farePopup(Userlist.get(position).getTotal_payable_amount(), Userlist.get(position).getNeed_payment(), Userlist.get(position).getReceive_cash(), Userlist.get(position).getReq_payment(), Userlist.get(position).getFarelist(), Userlist.get(position).getRide_id());
                } else if (Userlist.get(position).getBtn_group().equalsIgnoreCase("8")) {

                    Intent intent = new Intent(TripPage.this, RatingsPage.class);
                    intent.putExtra("rideid", Userlist.get(position).getRide_id());
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

                adapter.notifyDataSetChanged();


            }
        });


    }

    private void initialize() {
        session = new SessionManager(TripPage.this);
        gps = new GPSTracker(TripPage.this);
        v2GetRouteDirection = new GMapV2GetRouteDirection();

        wayPointList = new ArrayList<LatLng>();
        polyLines = new ArrayList<Polyline>();


        myDBHelper = new GEODBHelper(getApplicationContext());
        myDBHelper.insertDriverStatus("1");
        HashMap<String, String> user = session.getUserDetails();
        HashMap<String, String> bitmap = session.getBitmapCode();
        base64 = bitmap.get(SessionManager.KEY_VEHICLE_BitMap_IMAGE);
        bmp = StringToBitMap(base64);

        driver_id = user.get(SessionManager.KEY_DRIVERID);
        Rl_traffic = (RelativeLayout) findViewById(R.id.traffic_btn_layout);
        refresh_button = (ImageButton) findViewById(R.id.refresh);
        traffic_button = (ImageButton) findViewById(R.id.traffic);
        phone_call = (ImageView) findViewById(R.id.call_image);
        user_info = (ImageView) findViewById(R.id.info_image);
        Tv_Address = (TextView) findViewById(R.id.trip_user_address);
        Rl_layout_userinfo = (RelativeLayout) findViewById(R.id.info_layout);
        Rl_layout_arrived = (RelativeLayout) findViewById(R.id.layout_begintrip);
        wait_time_layout = (RelativeLayout) findViewById(R.id.navi_layout);
        share_seat_layout = (RelativeLayout) findViewById(R.id.share_seat_layout);
        listview = (HorizontalListView) findViewById(R.id.user_listview);
        Rl_layout_enable_voicenavigation = (ImageView) findViewById(R.id.google_navigation);
        wait_time_start = (ImageView) findViewById(R.id.wait_img);
        wait_time_stop = (ImageView) findViewById(R.id.wait_img1);
        destination_address_layout = (CardView) findViewById(R.id.book_cardview_destination_address_layout);
        driverDetail = (CardView) findViewById(R.id.driver_details);
        tv_start = (TextView) findViewById(R.id.start_time);
        tv_stop = (TextView) findViewById(R.id.stop_time);
        tv_timer = (TextView) findViewById(R.id.wait_time);
        tv_seat_count = (TextView) findViewById(R.id.seat_label_count);
        tv_drop_address = (TextView) findViewById(R.id.location_drop_address);

        rl=(RelativeLayout) findViewById(R.id.user_list_normal_layout);
        user_name = (TextView) findViewById(R.id.user_title);
        user_icon = (RoundedImageView) findViewById(R.id.user_icon);


        shimmer = new Shimmer();
        sliderSeekBar = (SeekBar) findViewById(R.id.Trip_seek);
        Bt_slider = (ShimmerButton) findViewById(R.id.Trip_slider_button);
        shimmer.start(Bt_slider);

        System.out.println("");
        sliderSeekBar.setOnSeekBarChangeListener(this);


        Intent i = getIntent();
        Str_Interrupt = i.getStringExtra("interrupted");

        if (mediaPlayer1 != null && mediaPlayer1.isPlaying()) {
            mediaPlayer1.stop();
        }


        cd = new ConnectionDetector(TripPage.this);
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            postRequest(ServiceConstant.trip_Track_Driver);
        } else {
            Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
        }

        session.setTripStatus("1");

    }


    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            waitingStatus = true;
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
                    tv_timer.setText(String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":"
                            + String.format("%02d", secs));
                    //          System.out.println("thread----------------" + timerValue);

                    customHandler.postDelayed(this, 3 * 1000);
                }
            }
        }

    };


    //-------------    ------Show Share Seat Method--------------------
    private void select_Shareseat_Dialog(final String num, final int pos) {
        final MaterialDialog dialog = new MaterialDialog(TripPage.this);
        View view = LayoutInflater.from(TripPage.this).inflate(R.layout.select_share_seat, null);

        ListView car_listview = (ListView) view.findViewById(R.id.seat_type_dialog_listView);
        RelativeLayout ok = (RelativeLayout) view.findViewById(R.id.select_seatype_single_Bottm_layout);
        poolRateCardList.clear();
        for (int i = 0; i < Integer.parseInt(num); i++) {
            PoolRateCard pojo = new PoolRateCard();
            pojo.setSeat(String.valueOf(i + 1));
            pojo.setSelect("no");
            poolRateCardList.add(pojo);
        }

        //poolRateCardList.get(0).setSelect("yes");
        final SelectSeatAdapter seat_adapter = new SelectSeatAdapter(TripPage.this, poolRateCardList);
        car_listview.setAdapter(seat_adapter);
        seat_adapter.notifyDataSetChanged();

        dialog.setTitle(TripPage.this.getResources().getString(R.string.car_type_select_dialog_label_shareSeat));
        /*dialog.setPositiveButton(getActivity().getResources().getString(R.string.estimate_detail_label_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                }
        );*/

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        car_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //         dialog.dismiss();
                System.out.println("poolRateCardList--------------------jai" + poolRateCardList.size());

                for (int j = 0; j < poolRateCardList.size(); j++) {
                    System.out.println("poolRateCardList---------------j-----jai" + j);
                    System.out.println("poolRateCardList-------------pos-------jai" + position);

                    if (j == position) {
                        poolRateCardList.get(position).setSelect("yes");
                        System.out.println("poolRateCardList-----------bm----j-----jai" + poolRateCardList.get(position).getSeat());
                        String no_of_seat = poolRateCardList.get(position).getSeat();
                        Userlist.get(pos).setNo_of_seat(no_of_seat);
                        tv_seat_count.setText(no_of_seat + "/" + num);

                    } else {
                        poolRateCardList.get(j).setSelect("no");
                    }
                }
                seat_adapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
        dialog.setView(view).show();
    }


    private void chooseContactOptions(final String ride_id, final String Str_user_phoneno) {
        cantact_dialog = new Dialog(TripPage.this);
        cantact_dialog.getWindow();
        cantact_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cantact_dialog.setContentView(R.layout.choose_contact_popup);
        cantact_dialog.setCanceledOnTouchOutside(true);
        cantact_dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        cantact_dialog.show();
        cantact_dialog.getWindow().setGravity(Gravity.CENTER);

        RelativeLayout call = (RelativeLayout) cantact_dialog
                .findViewById(R.id.call_layout);
        RelativeLayout message = (RelativeLayout) cantact_dialog
                .findViewById(R.id.message_layout);
        RelativeLayout bottom_layout = (RelativeLayout) cantact_dialog
                .findViewById(R.id.bottom_layout);

        bottom_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantact_dialog.dismiss();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                HashMap<String, String> mask = session.getPhoneMasking();
                String mask_status = mask.get(SessionManager.KEY_PHONE_MASKING_STATUS);


                System.out.println("=========PHONEMASKINGSTATUS track ur ride get ==========>= " + mask_status);


                if (mask_status.equalsIgnoreCase("Yes")) {

                    System.out.println("=========PHONEMASKINGSTATUS track ur ride YES ==========>= " + mask_status);

                    cd = new ConnectionDetector(TripPage.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        phonemask_Call(ServiceConstant.phoneMasking, ride_id);
                    } else {
                        Alert1(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                } else {
                    if (Str_user_phoneno != null) {
                        Str_user_phone_no = Str_user_phoneno;
                        System.out.println("=========PHONEMASKINGSTATUS NO==========>= " + mask_status);

                        if (Build.VERSION.SDK_INT >= 23) {
                            // Marshmallow+
                            if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                                requestPermission();
                            } else {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Str_user_phoneno));
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Str_user_phoneno));
                            startActivity(intent);

                        }
                    } else {
                        Alert1(TripPage.this.getResources().getString(R.string.alert_label_title), TripPage.this.getResources().getString(R.string.arrived_alert_content1));
                    }
                }


                cantact_dialog.dismiss();


            }
        });


        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                cantact_dialog.dismiss();
                HashMap<String, String> mask = session.getPhoneMasking();
                String mask_status = mask.get(SessionManager.KEY_PHONE_MASKING_STATUS);


                /*System.out.println("=========PHONEMASKINGSTATUS track ur ride get ==========>= " + mask_status);

                Intent n = new Intent(Intent.ACTION_VIEW);
                n.setType("vnd.android-dir/mms-sms");
                n.putExtra("address", driverMobile);
                startActivity(n);*/

                if (mask_status.equalsIgnoreCase("Yes")) {
                    showMessagePopup(ride_id);


                } else {

                    try {

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("smsto:" + Uri.encode(Str_user_phoneno)));
                        startActivity(intent);
                    } catch (Exception e) {

                    }


                    /*Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setType("vnd.android-dir/mms-sms");
                    i.putExtra("address", Str_user_phoneno);
                    startActivity(i);*/


                }


            }
        });
    }


    private boolean isGoogleMapsInstalled() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void moveNavigationPickup(String plat, String plong) {

        if (isGoogleMapsInstalled()) {

            if (!isMyServiceRunning(GoogleNavigationService.class)) {
                startService(new Intent(getApplicationContext(), GoogleNavigationService.class));
            }
            try {
                //      session.setGoogleNavicationValueArrived(Str_address,Str_RideId,Str_pickUp_Lat,Str_pickUp_Long,Str_username,Str_user_rating,Str_user_phoneno,Str_user_img,Suser_Id,Str_droplat,Str_droplon,str_drop_location);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(String.format("google.navigation:ll=%s,%s%s", Double.parseDouble(plat), Double.parseDouble(plong), "&mode=c")));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            } catch (Exception e) {
            }

        } else {

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_label_no_google_map_installed), Toast.LENGTH_LONG).show();

        }

//        addBackLayout();
    }


    private void moveNavigationdrop(String Dlat, String Dlong) {


        if (isGoogleMapsInstalled()) {


            if (!Dlat.equalsIgnoreCase("") && !Dlong.equalsIgnoreCase("")) {

                if (!isMyServiceRunning(GoogleNavigationService.class)) {
                    startService(new Intent(getApplicationContext(), GoogleNavigationService.class));
                }

                //    session.setGoogleNavicationValue(Str_name,Str_rideid,Str_mobilno,address,beginAddress,Str_User_Id,Str_Interrupt,Str_profilpic);

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(String.format("google.navigation:ll=%s,%s%s", Double.parseDouble(Dlat), Double.parseDouble(Dlong), "&mode=c")));
                intent.setPackage("com.google.android.apps.maps");
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

    private void AlertNavigation(String title, String message) {
        final PkDialog mDialog = new PkDialog(TripPage.this);
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


    public void showMessagePopup(final String str_ride_id) {

        sms_popup = new Dialog(TripPage.this);
        sms_popup.getWindow();
        sms_popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sms_popup.setContentView(R.layout.sms_popup);
        sms_popup.setCanceledOnTouchOutside(true);
        sms_popup.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        sms_popup.show();
        sms_popup.getWindow().setGravity(Gravity.CENTER);

        TextView cancel = (TextView) sms_popup
                .findViewById(R.id.cancel);
        final EditText ed_msg = (EditText) sms_popup
                .findViewById(R.id.text_editview);
        TextView send = (TextView) sms_popup
                .findViewById(R.id.send);

        //     ed_msg.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        //       ed_msg.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        // tFields.addView(inputs[i]);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sms_popup.dismiss();


            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sms = ed_msg.getText().toString();
                sms_popup.dismiss();
                if (sms.trim().length() > 0) {
                    phonemask_sms(ServiceConstant.phoneMasking_sms, sms, str_ride_id);
                } else {
                    Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.sms_masking_text));
                }

            }
        });
    }

    private void phonemask_sms(String Url, String msg, final String Str_RideId) {
        dialog = new Dialog(TripPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));
        System.out.println("-------------phonemask_sms----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", Str_RideId);
        jsonParams.put("user_type", "driver");
        jsonParams.put("sms_content", msg);
        System.out.println("ride_id---------" + Str_RideId);
        System.out.println("user_type---------" + "user");
        System.out.println("sms_content---------" + msg);
        mRequest = new ServiceRequest(TripPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println(response);
                Log.e("phonemask_sms", response);
                String Sstatus = "", SResponse = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    SResponse = object.getString("response");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Alert1(getResources().getString(R.string.action_loading_sucess), SResponse);
                    } else {
                        Alert1(getResources().getString(R.string.alert_sorry_label_title), SResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    private void phonemask_Call(String Url, String Str_RideId) {
        dialog = new Dialog(TripPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));
        System.out.println("-------------phone Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", Str_RideId);
        jsonParams.put("user_type", "driver");

        System.out.println("ride_id---------" + Str_RideId);
        System.out.println("user_type---------" + "driver");

        mRequest = new ServiceRequest(TripPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println(response);
                Log.e("phonemask", response);
                String Sstatus = "", SResponse = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    SResponse = object.getString("response");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Alert1(getResources().getString(R.string.action_loading_sucess), SResponse);
                    } else {
                        Alert1(getResources().getString(R.string.alert_sorry_label_title), SResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });


    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    private void postRequest(String Url) {
        if (dialog == null) {
            dialog = new Dialog(TripPage.this);
            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();

            TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
            dialog_title.setText(getResources().getString(R.string.action_loading));
        }


        System.out.println("-------------Trip_Page----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);


        System.out.println("-------------dashboard--Trip_Page--------------" + jsonParams);
        mRequest = new ServiceRequest(TripPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                String Str_status = "", Str_response = "", duty_id = "", currency = "";
                ;
                Log.e("Trip Page", response);
                try {

                    JSONObject jobject = new JSONObject(response);

                    Str_status = jobject.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject object = jobject.getJSONObject("response");
                        ride_type = object.getString("ride_type");
                        duty_id = object.getString("duty_id");
                        active_ride = object.getString("active_ride");
                        JSONArray jarray = object.getJSONArray("rides");
                        Userlist.clear();
                        if (jarray.length() > 0) {
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject j_ride_object = jarray.getJSONObject(i);
                                UserPojo u_pojo = new UserPojo();
                                u_pojo.setBtn_group(j_ride_object.getString("btn_group"));
                                currency = j_ride_object.getString("currency");
                                u_pojo.setRide_id(j_ride_object.getString("ride_id"));
                                u_pojo.setRide_status(j_ride_object.getString("ride_status"));
                                u_pojo.setUser_id(j_ride_object.getString("user_id"));
                                u_pojo.setUser_name(j_ride_object.getString("user_name"));
                                u_pojo.setUser_email(j_ride_object.getString("user_email"));
                                u_pojo.setPhone_number(j_ride_object.getString("phone_number"));
                                u_pojo.setUser_image(j_ride_object.getString("user_image"));
                                u_pojo.setUser_review(j_ride_object.getString("user_review"));
                                u_pojo.setPickup_location(j_ride_object.getString("pickup_location"));
                                u_pojo.setPickup_lat(j_ride_object.getString("pickup_lat"));
                                u_pojo.setPickup_lon(j_ride_object.getString("pickup_lon"));
                                u_pojo.setPickup_time(j_ride_object.getString("pickup_time"));
                                u_pojo.setDrop_location(j_ride_object.getString("drop_loc"));
                                u_pojo.setDrop_lat(j_ride_object.getString("drop_lat"));
                                u_pojo.setDrop_lon(j_ride_object.getString("drop_lon"));
                                carIcon = j_ride_object.getString("car_icon");
                                if (j_ride_object.has("no_of_seat")) {
                                    u_pojo.setNo_of_seat(j_ride_object.getString("no_of_seat"));
                                }
                                if (j_ride_object.has("max_no_of_seat")) {
                                    u_pojo.setMax_no_of_seat(j_ride_object.getString("max_no_of_seat"));
                                }

                                u_pojo.setActive_ride_id(active_ride);
                                u_pojo.setRide_type(ride_type);
                                //  Userlist.add(u_pojo);
                                String sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(currency);
                                if (j_ride_object.getString("btn_group").equals("5")) {
                                    JSONArray triparray = j_ride_object.getJSONArray("trip_summary");
                                    u_pojo.setNeed_payment(j_ride_object.getString("need_payment"));
                                    u_pojo.setReceive_cash(j_ride_object.getString("receive_cash"));
                                    u_pojo.setReq_payment(j_ride_object.getString("req_payment"));
                                    u_pojo.setTotal_payable_amount(sCurrencySymbol + j_ride_object.getString("total_payable_amount"));


                                    if (triparray.length() > 0) {
                                        farearray.clear();
                                        for (int j = 0; j < triparray.length(); j++) {
                                            JSONObject fare_obj = triparray.getJSONObject(j);
                                            FarePojo fpojo = new FarePojo();
                                            fpojo.setTitle(fare_obj.getString("title"));
                                            fpojo.setValue(fare_obj.getString("value") + " " + fare_obj.getString("unit"));
                                            farearray.add(fpojo);
                                            idFareAvailable = true;

                                        }


                                    }
                                    u_pojo.setFarelist(farearray);


                                }
                                Userlist.add(u_pojo);

                            }
                        }
                        Object check_driver_object = object.get("map_locations");
                        if (check_driver_object instanceof JSONArray) {
                            JSONArray j_latlon_array = object.getJSONArray("map_locations");
                            multiple_latlon_list.clear();
                            if (j_latlon_array.length() > 0) {
                                for (int i = 0; i < j_latlon_array.length(); i++) {
                                    JSONObject j_ride_object = j_latlon_array.getJSONObject(i);
                                    MultipleLatLongPojo u_pojo = new MultipleLatLongPojo();
                                    u_pojo.setLat(j_ride_object.getString("lat"));
                                    u_pojo.setLon(j_ride_object.getString("lon"));
                                    u_pojo.setTxt(j_ride_object.getString("txt"));
                                    multiple_latlon_list.add(u_pojo);
                                }
                            }
                        }

                        if (Userlist.size() > 0) {
                            if (googleMap != null) {
                                googleMap.clear();
                            }

                            myDBHelper.deleteUser();
                            for (int i = 0; i < Userlist.size(); i++) {
                                if (Userlist.get(i).getRide_id().equalsIgnoreCase(active_ride)) {
                                    if (Userlist.get(i).getBtn_group().equalsIgnoreCase("2")) {
                                        Arrived(Userlist.get(i).getPickup_location());
                                    } else if (Userlist.get(i).getBtn_group().equalsIgnoreCase("3")) {
                                        if (!Userlist.get(i).getDrop_location().equals("")) {
                                            Begin(Userlist.get(i).getDrop_location(), Userlist.get(i).getNo_of_seat(), Userlist.get(i).getMax_no_of_seat(), Userlist.get(i).getRide_type());
                                        } else {
                                            Begin("", Userlist.get(i).getNo_of_seat(), Userlist.get(i).getMax_no_of_seat(), Userlist.get(i).getRide_type());
                                        }

                                    } else if (Userlist.get(i).getBtn_group().equalsIgnoreCase("4")) {
                                        if (!Userlist.get(i).getDrop_location().equals("")) {
                                            End(Userlist.get(i).getDrop_location(), Userlist.get(i).getRide_type());
                                        }
                                    } else if (Userlist.get(i).getBtn_group().equalsIgnoreCase("5")) {

                                        if (isMyServiceRunning(GoogleNavigationService.class)) {
                                            Intent serviceIntent = new Intent(getApplicationContext(), GoogleNavigationService.class);
                                            stopService(serviceIntent);
                                        }

                                        farePopup(Userlist.get(i).getTotal_payable_amount(), Userlist.get(i).getNeed_payment(), Userlist.get(i).getReceive_cash(), Userlist.get(i).getReq_payment(), Userlist.get(i).getFarelist(), Userlist.get(i).getRide_id());
                                    } else if (Userlist.get(i).getBtn_group().equalsIgnoreCase("8")) {

                                        Intent intent = new Intent(TripPage.this, RatingsPage.class);
                                        intent.putExtra("rideid", Userlist.get(i).getRide_id());
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    }
                                }
                                System.out.println("insert user details db");
                                myDBHelper.insertUserDetails(Userlist.get(i).getUser_id(), Userlist.get(i).getBtn_group(), Userlist.get(i).getRide_id(), duty_id);
                                 myDBHelper.userCount();
                            }

                            if (Userlist.size() > 1) {
                                rl.setVisibility(View.GONE);
                                listview.setVisibility(View.VISIBLE);
                                adapter = new UserListAdapter(TripPage.this, Userlist);
                                listview.setAdapter(adapter);
                            }else{
                                listview.setVisibility(View.GONE);
                                rl.setVisibility(View.VISIBLE);
                                Picasso.with(TripPage.this).load(String.valueOf(Userlist.get(0).getUser_image())).placeholder(R.drawable.placeholder_icon).into(user_icon);
                                user_name.setText(Userlist.get(0).getUser_name());
                            }
                        }

                        Picasso.with(TripPage.this)
                                .load(carIcon)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                        if (bitmap != null) {
                                            String s = BitMapToString(bitmap);
                                            System.out.println("session bitmap" + s);
                                            session.setVehicle_BitmapImage(s);
                                            bmp = bitmap;
                                           /*  double Dlatitude = MyCurrent_lat;
                                            double Dlongitude = MyCurrent_long;

                                           if (drivermarker != null) {
                                                drivermarker.remove();
                                            }

                                            drivermarker = googleMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(Dlatitude, Dlongitude))
                                                    .icon(BitmapDescriptorFactory.fromBitmap(bmp)));*/
                                        }

                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });


                        if (multiple_latlon_list.size() >= 2) {
                            GetRouteTask getRoute = new GetRouteTask(multiple_latlon_list, String.valueOf(MyCurrent_lat), String.valueOf(MyCurrent_long));
                            getRoute.execute();
                        }

                        if (currentMarker != null) {
                            currentMarker.remove();
                        }
                        currentMarker = googleMap.
                                addMarker(new MarkerOptions()
                                        .position(new LatLng(MyCurrent_lat, MyCurrent_long))
                                        .icon(BitmapDescriptorFactory
                                                .fromBitmap(bmp)));

                       /* if (bmp != null) {
                            if (drivermarker != null) {
                                drivermarker.remove();
                            }
                            drivermarker = googleMap.
                                    addMarker(new MarkerOptions()
                                            .position(new LatLng(MyCurrent_lat, MyCurrent_long))
                                            .icon(BitmapDescriptorFactory
                                                    .fromBitmap(bmp)));


                        }*/

                        if (!ride_type.equals("Share")) {

                            if ("Confirmed".equalsIgnoreCase(Userlist.get(0).getRide_status())) {

                                MarkerOptions marker = new MarkerOptions().position(new LatLng(MyCurrent_lat, MyCurrent_long)).title("Pickup");
                                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
                                googleMap.addMarker(marker);

                                MarkerOptions marker1 = new MarkerOptions().position(new LatLng(Double.parseDouble(Userlist.get(0).getPickup_lat()), Double.parseDouble(Userlist.get(0).getPickup_lon()))).title("Drop");
                                marker1.icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                                googleMap.addMarker(marker1);

                            } else {
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(Userlist.get(0).getPickup_lat()), Double.parseDouble(Userlist.get(0).getPickup_lon()))).title("Pickup");
                                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
                                googleMap.addMarker(marker);

                                if (!"".equalsIgnoreCase(Userlist.get(0).getDrop_lat()) || !"".equalsIgnoreCase(Userlist.get(0).getDrop_lon())) {

                                    MarkerOptions marker1 = new MarkerOptions().position(new LatLng(Double.parseDouble(Userlist.get(0).getDrop_lat()), Double.parseDouble(Userlist.get(0).getDrop_lon()))).title("Drop");
                                    marker1.icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                                    googleMap.addMarker(marker1);
                                }


                            }


                        }

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
                                    waitingStatus = true;
                                    wait_time_stop.setVisibility(View.VISIBLE);
                                    wait_time_start.setVisibility(View.INVISIBLE);
                                    tv_stop.setVisibility(View.VISIBLE);
                                    tv_start.setVisibility(View.GONE);
                                    customHandler.postDelayed(updateTimerThread, 0);
                                    //    layout_timer.setVisibility(View.VISIBLE);
                                } else {
                                    waitingStatus = false;
                                    wait_time_stop.setVisibility(View.INVISIBLE);
                                    wait_time_start.setVisibility(View.VISIBLE);
                                    System.out.println("jai 2");
                                    tv_stop.setVisibility(View.GONE);
                                    tv_start.setVisibility(View.VISIBLE);
                                    //      layout_timer.setVisibility(View.VISIBLE);
                                }
                            } else {
                                //    layout_timer.setVisibility(View.GONE);
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
                                    tv_timer.setText(String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":"
                                            + String.format("%02d", secs));
                                } else {
                                    session.setWaitingStatus("0");
                                    //      layout_timer.setVisibility(View.GONE);
                                    tv_timer.setText("00:00:00");
                                }
                            }

                        } else {
                            session.setWaitingStatus("0");
                            //    layout_timer.setVisibility(View.GONE);
                            tv_timer.setText("00:00:00");
                        }


                        if (ride_type.equals("Share")) {
                            wait_time_layout.setVisibility(View.GONE);
                        }

                    } else {
                        Str_response = jobject.getString("response");
                        Alert1(getResources().getString(R.string.alert_sorry_label_title), Str_response);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    public void farePopup(final String amt, String need_payment, String receive_cash, String req_payment, ArrayList<FarePojo> farelist, final String ride_id) {
        Rl_layout_arrived.setVisibility(View.GONE);
        wait_time_layout.setVisibility(View.GONE);
        share_seat_layout.setVisibility(View.GONE);
        destination_address_layout.setVisibility(View.GONE);


        if (idFareAvailable) {


            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.fare_popup, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            builder.create().setCanceledOnTouchOutside(false);
            builder.setCancelable(false);
            builder.show();
            final TextView Tv_amt = (TextView) view.findViewById(R.id.my_rides_detail_Total_fare);
            final TextView Tv_requst = (TextView) view.findViewById(R.id.requst);
            final ExpandableHeightListView listview = (ExpandableHeightListView) view.findViewById(R.id.my_rides_payment_detail_listView);

            RelativeLayout layout_request_payment = (RelativeLayout) view.findViewById(R.id.layout_faresummery_requstpayment);
            RelativeLayout layout_receive_cash = (RelativeLayout) view.findViewById(R.id.fare_summery_receive_cash_layout);

            Tv_amt.setText(amt);

            FareAdaptewr adaptewr = new FareAdaptewr(TripPage.this, farelist);
            listview.setAdapter(adaptewr);
            listview.setExpanded(true);


            //if (Str_need_payment.equalsIgnoreCase("YES")){
            if (need_payment.equals("1")) {
                if (receive_cash.equals("1") && req_payment.equals("1")) {
                    Tv_requst.setText(getResources().getString(R.string.fare_summary_ride_request_payment));
                    layout_receive_cash.setVisibility(View.VISIBLE);
                    layout_request_payment.setVisibility(View.VISIBLE);
                } else if (receive_cash.equals("0") && req_payment.equals("1")) {
                    layout_receive_cash.setVisibility(View.GONE);
                    Tv_requst.setText(getResources().getString(R.string.fare_summary_ride_request_payment));
                    layout_request_payment.setVisibility(View.VISIBLE);
                } else if (receive_cash.equals("1") && req_payment.equals("0")) {
                    layout_receive_cash.setVisibility(View.VISIBLE);
                    //  Tv_requst.setText(getResources().getString(R.string.fare_summary_ride_request_payment));
                    layout_request_payment.setVisibility(View.GONE);
                } else {
                    Tv_requst.setText(getResources().getString(R.string.lbel_notification_ok));
                    layout_receive_cash.setVisibility(View.GONE);
                    layout_request_payment.setVisibility(View.VISIBLE);
                }

            } else {
                if (receive_cash.equals("0") && req_payment.equals("0")) {
                    Tv_requst.setText(getResources().getString(R.string.lbel_notification_ok));
                    layout_receive_cash.setVisibility(View.GONE);
                    layout_request_payment.setVisibility(View.VISIBLE);
                }
            }


            layout_receive_cash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(TripPage.this, PaymentPage.class);
                    intent.putExtra("amount", amt);
                    intent.putExtra("rideid", ride_id);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                }
            });

            layout_request_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cd = new ConnectionDetector(TripPage.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {

                        if (Tv_requst.getText().toString().equalsIgnoreCase(getResources().getString(R.string.fare_summary_ride_request_payment))) {
                            postRequest_Reqqustpayment(ServiceConstant.request_paymnet_url, ride_id);
                            System.out.println("arrived------------------" + ServiceConstant.request_paymnet_url);
                        } else {
                            Intent intent = new Intent(TripPage.this, RatingsPage.class);
                            intent.putExtra("rideid", ride_id);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }

                    } else {
                        Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }
            });


        }


    }


    //-----------------------Code for arrived post request-----------------
    private void postRequest_Reqqustpayment(String Url, final String ride_id) {
        dialog = new Dialog(TripPage.this);
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
        jsonParams.put("ride_id", ride_id);

        System.out
                .println("--------------driver_id-------------------"
                        + driver_id);


        System.out
                .println("--------------ride_id-------------------"
                        + ride_id);

        mRequest = new ServiceRequest(TripPage.this);
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
                    Alert_req(getResources().getString(R.string.alert_sorry_label_title), Str_response, ride_id);

                } else {
                    Alert_req(getResources().getString(R.string.label_pushnotification_cashreceived), Str_response, ride_id);
                }
            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();
            }

        });

    }

    //--------------Alert Method-----------
    private void Alert_req(String title, String message, final String ride_id) {
        final PkDialog mDialog = new PkDialog(TripPage.this);
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

                Intent broadcastIntent_trip = new Intent();
                broadcastIntent_trip.setAction("com.finish.tripPage");
                sendBroadcast(broadcastIntent_trip);


                Intent intent = new Intent(TripPage.this, LoadingPage.class);
                intent.putExtra("Driverid", driver_id);
                intent.putExtra("RideId", ride_id);
                startActivity(intent);

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        mDialog.show();
    }


    private void PostRequest(String Url, String btn_stat, final String ride_id, final String str_drop_Latitude, final String str_drop_Longitude, String ride_type1, String no_of_seat) {
        dialog = new Dialog(TripPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------Trip_Page----------------" + Url);
        System.out.println("---------ride_type---------------" + ride_type1);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);
        jsonParams.put("ride_id", ride_id);

        if (btn_stat.equals("2")) {
            jsonParams.put("driver_lat", String.valueOf(MyCurrent_lat));
            jsonParams.put("driver_lon", String.valueOf(MyCurrent_long));
        } else if (btn_stat.equals("3")) {
            if (ride_type1.equals("Share")) {
                jsonParams.put("no_of_seat", no_of_seat);
            }
            jsonParams.put("pickup_lat", String.valueOf(MyCurrent_lat));
            jsonParams.put("pickup_lon", String.valueOf(MyCurrent_long));
            jsonParams.put("drop_lat", String.valueOf(str_drop_Latitude));
            jsonParams.put("drop_lon", String.valueOf(str_drop_Longitude));
            jsonParams.put("distance", "0");


        } else if (btn_stat.equals("4")) {
            ArrayList<String> travel_history = myDBHelper.getDataEndTrip(ride_id);
            System.out.println("-----------jai---total_distance-------------------" + travel_history.toString().replace("[", "").replace("]", "").replace(" ", ""));
            StringBuilder builder = new StringBuilder();
            for (String string : travel_history) {
                builder.append("," + string);
            }
            jsonParams.put("wait_time_frame", String.valueOf(tv_timer.getText().toString()));
            jsonParams.put("travel_history", builder.toString());
            jsonParams.put("drop_lat", String.valueOf(MyCurrent_lat));
            jsonParams.put("drop_lon", String.valueOf(MyCurrent_long));
            jsonParams.put("distance", "0");

        }

        System.out.println("---------------Trip_Page jsonParams--------------" + jsonParams);

        mRequest = new ServiceRequest(TripPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                String Str_status = "", Str_response = "", duty_id = "", currency = "", Zero_response = "";
                Log.e("Trip Page", response);
                try {

                    JSONObject jobject = new JSONObject(response);

                    Str_status = jobject.getString("status");

                    if (Str_status.equalsIgnoreCase("0")) {
                        Zero_response = jobject.getString("ride_view");
                        Str_response = jobject.getString("response");
                    }

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject object = jobject.getJSONObject("response");

                        ride_type = object.getString("ride_type");
                        duty_id = object.getString("duty_id");
                        active_ride = object.getString("active_ride");
                        JSONArray jarray = object.getJSONArray("rides");

                        Userlist.clear();
                        if (jarray.length() > 0) {
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject j_ride_object = jarray.getJSONObject(i);
                                UserPojo u_pojo = new UserPojo();
                                u_pojo.setBtn_group(j_ride_object.getString("btn_group"));
                                currency = j_ride_object.getString("currency");
                                u_pojo.setRide_id(j_ride_object.getString("ride_id"));
                                u_pojo.setRide_status(j_ride_object.getString("ride_status"));
                                u_pojo.setUser_id(j_ride_object.getString("user_id"));
                                u_pojo.setUser_name(j_ride_object.getString("user_name"));
                                u_pojo.setUser_email(j_ride_object.getString("user_email"));
                                u_pojo.setPhone_number(j_ride_object.getString("phone_number"));
                                u_pojo.setUser_image(j_ride_object.getString("user_image"));
                                u_pojo.setUser_review(j_ride_object.getString("user_review"));
                                u_pojo.setPickup_location(j_ride_object.getString("pickup_location"));
                                u_pojo.setPickup_lat(j_ride_object.getString("pickup_lat"));
                                u_pojo.setPickup_lon(j_ride_object.getString("pickup_lon"));
                                u_pojo.setPickup_time(j_ride_object.getString("pickup_time"));
                                u_pojo.setDrop_location(j_ride_object.getString("drop_loc"));
                                u_pojo.setDrop_lat(j_ride_object.getString("drop_lat"));
                                u_pojo.setDrop_lon(j_ride_object.getString("drop_lon"));
                                carIcon = j_ride_object.getString("car_icon");
                                if (j_ride_object.has("no_of_seat")) {
                                    u_pojo.setNo_of_seat(j_ride_object.getString("no_of_seat"));
                                }
                                if (j_ride_object.has("max_no_of_seat")) {
                                    u_pojo.setMax_no_of_seat(j_ride_object.getString("max_no_of_seat"));
                                }
                                u_pojo.setActive_ride_id(active_ride);
                                u_pojo.setRide_type(ride_type);
//                                Userlist.add(u_pojo);
                                String sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(currency);
                                if (j_ride_object.getString("btn_group").equals("5")) {
                                    JSONArray triparray = j_ride_object.getJSONArray("trip_summary");
                                    u_pojo.setNeed_payment(j_ride_object.getString("need_payment"));
                                    u_pojo.setReceive_cash(j_ride_object.getString("receive_cash"));
                                    u_pojo.setReq_payment(j_ride_object.getString("req_payment"));
                                    u_pojo.setTotal_payable_amount(sCurrencySymbol + j_ride_object.getString("total_payable_amount"));


                                    if (triparray.length() > 0) {
                                        farearray.clear();
                                        for (int j = 0; j < triparray.length(); j++) {
                                            JSONObject fare_obj = triparray.getJSONObject(j);
                                            FarePojo fpojo = new FarePojo();
                                            fpojo.setTitle(fare_obj.getString("title"));
                                            fpojo.setValue(fare_obj.getString("value") + " " + fare_obj.getString("unit"));
                                            farearray.add(fpojo);
                                            idFareAvailable = true;

                                        }


                                    }
                                    u_pojo.setFarelist(farearray);


                                }
                                Userlist.add(u_pojo);

                            }
                        }

                        JSONArray j_latlon_array = object.getJSONArray("map_locations");
                        multiple_latlon_list.clear();
                        if (j_latlon_array.length() > 0) {
                            for (int i = 0; i < j_latlon_array.length(); i++) {
                                JSONObject j_ride_object = j_latlon_array.getJSONObject(i);
                                MultipleLatLongPojo u_pojo = new MultipleLatLongPojo();
                                u_pojo.setLat(j_ride_object.getString("lat"));
                                u_pojo.setLon(j_ride_object.getString("lon"));
                                u_pojo.setTxt(j_ride_object.getString("txt"));
                                multiple_latlon_list.add(u_pojo);
                            }
                        }

                        if (Userlist.size() > 0) {
                            if (googleMap != null) {
                                googleMap.clear();
                            }
                            myDBHelper.deleteUser();
                            for (int i = 0; i < Userlist.size(); i++) {
                                if (Userlist.get(i).getRide_id().equalsIgnoreCase(active_ride)) {
                                    if (Userlist.get(i).getBtn_group().equalsIgnoreCase("2")) {
                                        Arrived(Userlist.get(i).getPickup_location());
                                    } else if (Userlist.get(i).getBtn_group().equalsIgnoreCase("3")) {
                                        if (!Userlist.get(i).getDrop_location().equals("")) {
                                            Begin(Userlist.get(i).getDrop_location(), Userlist.get(i).getNo_of_seat(), Userlist.get(i).getMax_no_of_seat(), Userlist.get(i).getRide_type());
                                        } else {
                                            Begin("", Userlist.get(i).getNo_of_seat(), Userlist.get(i).getMax_no_of_seat(), Userlist.get(i).getRide_type());
                                        }

                                    } else if (Userlist.get(i).getBtn_group().equalsIgnoreCase("4")) {
                                        if (!Userlist.get(i).getDrop_location().equals("")) {
                                            End(Userlist.get(i).getDrop_location(), Userlist.get(i).getRide_type());
                                        }
                                    } else if (Userlist.get(i).getBtn_group().equalsIgnoreCase("5")) {
                                        idFareAvailable = true;
                                        session.setWaitingStatus("0");
                                        session.setWaitingTime("0");

                                        if (isMyServiceRunning(GoogleNavigationService.class)) {
                                            Intent serviceIntent = new Intent(getApplicationContext(), GoogleNavigationService.class);
                                            stopService(serviceIntent);
                                        }

                                        farePopup(Userlist.get(i).getTotal_payable_amount(), Userlist.get(i).getNeed_payment(), Userlist.get(i).getReceive_cash(), Userlist.get(i).getReq_payment(), Userlist.get(i).getFarelist(), Userlist.get(i).getRide_id());
                                    } else if (Userlist.get(i).getBtn_group().equalsIgnoreCase("8")) {

                                        Intent intent = new Intent(TripPage.this, RatingsPage.class);
                                        intent.putExtra("rideid", Userlist.get(i).getRide_id());
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    }
                                }

                                System.out.println("insert user details db");

                                myDBHelper.insertUserDetails(Userlist.get(i).getUser_id(), Userlist.get(i).getBtn_group(), Userlist.get(i).getRide_id(), duty_id);
                                myDBHelper.userCount();
                                System.out.println("---------jai multiple_latlon_list-----------" + multiple_latlon_list.size());


                                Picasso.with(TripPage.this)
                                        .load(carIcon)
                                        .into(new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                                if (bitmap != null) {
                                                    String s = BitMapToString(bitmap);
                                                    System.out.println("session bitmap" + s);
                                                    session.setVehicle_BitmapImage(s);
                                                    bmp = bitmap;
                                                   /* double Dlatitude = MyCurrent_lat;
                                                    double Dlongitude = MyCurrent_long;

                                                    drivermarker = googleMap.addMarker(new MarkerOptions()
                                                            .position(new LatLng(Dlatitude, Dlongitude))
                                                            .icon(BitmapDescriptorFactory.fromBitmap(bmp)));*/
                                                }

                                            }

                                            @Override
                                            public void onBitmapFailed(Drawable errorDrawable) {

                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                            }
                                        });

                                if (multiple_latlon_list.size() >= 2) {
                                    GetRouteTask getRoute = new GetRouteTask(multiple_latlon_list, String.valueOf(MyCurrent_lat), String.valueOf(MyCurrent_long));
                                    getRoute.execute();
                                }
                                if (bmp != null) {
                                    /*if (drivermarker != null) {
                                        drivermarker.remove();
                                    }
                                    drivermarker = googleMap.
                                            addMarker(new MarkerOptions()
                                                    .position(new LatLng(MyCurrent_lat, MyCurrent_long))
                                                    .icon(BitmapDescriptorFactory
                                                            .fromBitmap(bmp)));*/

                                    if (currentMarker != null) {
                                        currentMarker.remove();
                                    }
                                    currentMarker = googleMap.
                                            addMarker(new MarkerOptions()
                                                    .position(new LatLng(MyCurrent_lat, MyCurrent_long))
                                                    .icon(BitmapDescriptorFactory
                                                            .fromBitmap(bmp)));

                                }

                                if (!ride_type.equals("Share")) {

                                    if ("Confirmed".equalsIgnoreCase(Userlist.get(0).getRide_status())) {

                                        MarkerOptions marker = new MarkerOptions().position(new LatLng(MyCurrent_lat, MyCurrent_long)).title("Pickup");
                                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
                                        googleMap.addMarker(marker);

                                        MarkerOptions marker1 = new MarkerOptions().position(new LatLng(Double.parseDouble(Userlist.get(0).getPickup_lat()), Double.parseDouble(Userlist.get(0).getPickup_lon()))).title("Drop");
                                        marker1.icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                                        googleMap.addMarker(marker1);

                                    } else {
                                        MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(Userlist.get(0).getPickup_lat()), Double.parseDouble(Userlist.get(0).getPickup_lon()))).title("Pickup");
                                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
                                        googleMap.addMarker(marker);

                                        if (!"".equalsIgnoreCase(Userlist.get(0).getDrop_lat()) || !"".equalsIgnoreCase(Userlist.get(0).getDrop_lon())) {

                                            MarkerOptions marker1 = new MarkerOptions().position(new LatLng(Double.parseDouble(Userlist.get(0).getDrop_lat()), Double.parseDouble(Userlist.get(0).getDrop_lon()))).title("Drop");
                                            marker1.icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                                            googleMap.addMarker(marker1);
                                        }


                                    }


                                }


                            }

                            adapter = new UserListAdapter(TripPage.this, Userlist);
                            listview.setAdapter(adapter);

                            if (dialog != null) {
                                dialog.dismiss();
                            }

                        }

                        if (dialog != null) {
                            dialog.dismiss();
                        }

                    } else {
                        if (dialog != null) {
                            dialog.dismiss();
                        }

                        if (Str_status.equalsIgnoreCase("0")) {

                            if (Zero_response.equals("stay")) {
                                Alert1(getResources().getString(R.string.alert_sorry_label_title), Str_response);
                            }
                            if (Zero_response.equals("next")) {
                                Intent broadcastIntent1 = new Intent();
                                broadcastIntent1.setAction("com.package.ACTION_CLASS_TrackYourRide_REFRESH_page");
                                sendBroadcast(broadcastIntent1);

                            }
                            if (Zero_response.equals("detail")) {
                                finish();
                                Intent intent = new Intent(TripPage.this, TripSummaryDetail.class);
                                intent.putExtra("ride_id", ride_id);
                                intent.putExtra("type", "trip");
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
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

                                DashBoardDriver.isOnline = true;
                                Intent i = new Intent(TripPage.this, DriverMapActivity.class);
                                i.putExtra("availability", "Yes");
                                startActivity(i);
                                finish();

                            }
                        }

                        // Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);
                    } /*else {
                        Str_response = jobject.getString("response");
                        Alert1(getResources().getString(R.string.alert_sorry_label_title), Str_response);
                    }*/


                } catch (Exception e) {
                    e.printStackTrace();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
                if (dialog != null) {
                    dialog.dismiss();
                }

            }

            @Override
            public void onErrorListener() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
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


    public void Arrived(String loc) {
        Rl_layout_enable_voicenavigation.setVisibility(View.VISIBLE);
        wait_time_layout.setVisibility(View.GONE);
        Rl_layout_arrived.setVisibility(View.VISIBLE);
        share_seat_layout.setVisibility(View.GONE);
        Tv_Address.setText(loc);
        Bt_slider.setText(getResources().getString(R.string.arrivedtrip_arrivedtriptv_label));
        sliderSeekBar.setProgress(0);
        sliderSeekBar.setBackgroundResource(R.color.app_color);
        Bt_slider.setVisibility(View.VISIBLE);
        shimmer.start(Bt_slider);

    }

    public void End(String Loc, String type) {
        if (type.equals("Share")) {
            wait_time_layout.setVisibility(View.GONE);
        } else {
            wait_time_layout.setVisibility(View.VISIBLE);
        }
        Rl_layout_enable_voicenavigation.setVisibility(View.VISIBLE);
        share_seat_layout.setVisibility(View.GONE);
        destination_address_layout.setVisibility(View.GONE);
        driverDetail.setVisibility(View.VISIBLE);
        Rl_layout_arrived.setVisibility(View.VISIBLE);
        Tv_Address.setText(Loc);
        Bt_slider.setText(getResources().getString(R.string.lbel_endtrip));
        sliderSeekBar.setProgress(0);
        sliderSeekBar.setBackgroundResource(R.color.app_color);
        Bt_slider.setVisibility(View.VISIBLE);
        shimmer.start(Bt_slider);
    }

    public void Begin(String Drop_loc, String no_seat, String max_no_seat, String type) {
        if (!Drop_loc.equals("")) {
            driverDetail.setVisibility(View.VISIBLE);
            Tv_Address.setText(Drop_loc);
            Rl_layout_arrived.setVisibility(View.VISIBLE);
            destination_address_layout.setVisibility(View.GONE);
        } else {
            driverDetail.setVisibility(View.GONE);
            destination_address_layout.setVisibility(View.VISIBLE);
            Rl_layout_arrived.setVisibility(View.GONE);
            tv_drop_address.setText(getResources().getString(R.string.action_enter_drop_location));
        }
        if (type.equals("Share")) {
            share_seat_layout.setVisibility(View.VISIBLE);
        } else {
            share_seat_layout.setVisibility(View.GONE);
        }

        Rl_layout_enable_voicenavigation.setVisibility(View.INVISIBLE);
        wait_time_layout.setVisibility(View.GONE);

        tv_seat_count.setText(no_seat + "/" + max_no_seat);
        Bt_slider.setText(getResources().getString(R.string.lbel_begintrip));
        sliderSeekBar.setProgress(0);
        sliderSeekBar.setBackgroundResource(R.color.app_color);
        Bt_slider.setVisibility(View.VISIBLE);
        shimmer.start(Bt_slider);
    }

    public class GetRouteTask extends AsyncTask<String, Void, String> {

        String response = "";

        private ArrayList<LatLng> wayLatLng;
        private String dLat, dLong;
        private ArrayList<MultipleLatLongPojo> multipleDropList;

        GetRouteTask(ArrayList<MultipleLatLongPojo> multipleDropList, String lat, String lon) {

            this.multipleDropList = multipleDropList;
            dLat = lat;
            dLong = lon;
            wayLatLng = addWayPointPoint(multipleDropList, dLat, dLong);
            if (wayLatLng.size() < 2) {
                wayLatLng.clear();
                wayLatLng = addWayPointPoint(multiple_latlon_list, dLat, dLong);
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {

            System.out.println("-------jai wayLatLng---------" + wayLatLng);
            try {
                if (wayLatLng.size() >= 2) {
                    Routing routing = new Routing.Builder()
                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                            .withListener(listner)
                            .alternativeRoutes(true)
                            .waypoints(wayLatLng)
                            .build();
                    routing.execute();
                }

            } catch (Exception e) {

            }
            response = "Success";
            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("Success")) {
            }
        }
    }

    private ArrayList<LatLng> addWayPointPoint(ArrayList<MultipleLatLongPojo> mMultipleDropLatLng, String lat, String lon) {

        try {
            if (googleMap != null) {
//                googleMap.clear();
                wayPointList.clear();

                wayPointBuilder = new LatLngBounds.Builder();


                if (mMultipleDropLatLng != null) {

                    for (int i = 0; i < mMultipleDropLatLng.size(); i++) {

                        String sLat = mMultipleDropLatLng.get(i).getLat();
                        String sLng = mMultipleDropLatLng.get(i).getLon();
                        String sTxt = mMultipleDropLatLng.get(i).getTxt();

                        double Dlatitude = Double.parseDouble(sLat);
                        double Dlongitude = Double.parseDouble(sLng);

                        System.out.println("------jai----lat and long-----------" + Dlatitude + "sfsdfsdfdsd" + Dlongitude);

                        wayPointList.add(new LatLng(Dlatitude, Dlongitude));
                        wayPointBuilder.include(new LatLng(Dlatitude, Dlongitude));

                        if (ride_type.equals("Share")) {

                            if (sTxt != null && !"".equalsIgnoreCase(sTxt)) {

                                if (sTxt.contains("Pickup")) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude)).title(sTxt);
                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
                                    googleMap.addMarker(marker);
                                } else if (sTxt.contains("Drop")) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude)).title(sTxt);
                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                                    googleMap.addMarker(marker);
                                } else {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude)).title(sTxt);
                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_map_pointer_pin));
                                    googleMap.addMarker(marker);
                                }
                            } else {

                                if (i == 0) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
                                    googleMap.addMarker(marker);
                                } else if (i == mMultipleDropLatLng.size() - 1) {

                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                                    googleMap.addMarker(marker);
                                } else {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_map_pointer_pin));
                                    googleMap.addMarker(marker);
                                }
                            }
                        }

                       /* if (bmp != null) {
                            if (drivermarker != null) {
                                drivermarker.remove();
                            }
                            drivermarker = googleMap.
                                    addMarker(new MarkerOptions()
                                            .position(new LatLng(MyCurrent_lat, MyCurrent_long))
                                            .icon(BitmapDescriptorFactory
                                                    .fromBitmap(bmp)));


                        }*/

                        // wayPointBuilder.include(new LatLng(Dlatitude, Dlongitude));


                    }

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wayPointList;


    }

    RoutingListener listner = new RoutingListener() {
        @Override
        public void onRoutingFailure(RouteException e) {

            System.out.println("-----------jai onRoutingFailure-----------------" + e);

            if (multiple_latlon_list.size() >= 2) {
                GetRouteTask getRoute = new GetRouteTask(multiple_latlon_list, String.valueOf(MyCurrent_lat), String.valueOf(MyCurrent_long));
                getRoute.execute();
            } else {
                Intent broadcastIntent1 = new Intent();
                broadcastIntent1.setAction("com.package.ACTION_CLASS_TrackYourRide_REFRESH_page");
                sendBroadcast(broadcastIntent1);
            }
        }

        @Override
        public void onRoutingStart() {

        }

        @Override
        public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
            if (polyLines.size() > 0) {
                for (Polyline poly : polyLines) {
                    poly.remove();
                }
            }

            polyLines = new ArrayList<>();

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(R.color.app_color));
            polyOptions.width(7);
            polyOptions.addAll(arrayList.get(0).getPoints());
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polyLines.add(polyline);


            System.out.println("------------route--------------jai----" + arrayList);


            if (wayPointBuilder != null) {
                LatLngBounds bounds = wayPointBuilder.build();
                int padding = 90; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleMap.animateCamera(cu);
            }
        }

        @Override
        public void onRoutingCancelled() {

        }
    };


    private void initilizeMap() {
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

    }

    public void loadMap(GoogleMap arg) {

        googleMap = arg;
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        TripPage.this, R.raw.mapstyle));

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
        if (gps.canGetLocation()&& gps.isgpsenabled()) {
            double Dlatitude = gps.getLatitude();
            double Dlongitude = gps.getLongitude();

            MyCurrent_lat = Dlatitude;
            MyCurrent_long = Dlongitude;


            // Move the camera to last position with a zoom level
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //----------------------set marker------------------

            if (bmp != null) {


              /*  if (drivermarker != null) {
                    drivermarker.remove();
                }
                drivermarker = googleMap.
                        addMarker(new MarkerOptions()
                                .position(new LatLng(Dlatitude, Dlongitude))
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(bmp)));*/
            }

        } else {
            Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_gpsEnable));

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
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
            shimmer.start(Bt_slider);
        } else if (seekBar.getProgress() > 70) {
            seekBar.setProgress(100);
            Bt_slider.setVisibility(View.VISIBLE);
            //        Bt_slider.setText(getResources().getString(R.string.arrivedtrip_arrivedtriptv_label));
            shimmer.start(Bt_slider);
            sliderSeekBar.setVisibility(View.VISIBLE);

            cd = new ConnectionDetector(TripPage.this);
            isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                for (int i = 0; i < Userlist.size(); i++) {
                    if (active_ride.equals(Userlist.get(i).getRide_id())) {
                        if (Userlist.get(i).getBtn_group().equals("2")) {
                            Bt_slider.setText(getResources().getString(R.string.arrivedtrip_arrivedtriptv_label));
                            PostRequest(ServiceConstant.arrivedtrip_url, "2", Userlist.get(i).getRide_id(), Userlist.get(i).getDrop_lat(), Userlist.get(i).getDrop_lon(), Userlist.get(i).getRide_type(), "");
                        } else if (Userlist.get(i).getBtn_group().equals("3")) {
                            Bt_slider.setText(getResources().getString(R.string.lbel_begintrip));
                            PostRequest(ServiceConstant.begintrip_url, "3", Userlist.get(i).getRide_id(), Userlist.get(i).getDrop_lat(), Userlist.get(i).getDrop_lon(), Userlist.get(i).getRide_type(), Userlist.get(i).getNo_of_seat());
                        } else if (Userlist.get(i).getBtn_group().equals("4")) {
                            if (!waitingStatus) {
                                Bt_slider.setText(getResources().getString(R.string.lbel_endtrip));
                                PostRequest(ServiceConstant.endtrip_url, "4", Userlist.get(i).getRide_id(), Userlist.get(i).getDrop_lat(), Userlist.get(i).getDrop_lon(), Userlist.get(i).getRide_type(), "");
                            } else {

                                AlertNavigation(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.voice_navigationlabel_continue));

                            }
                        }
                    }

                }


                //    PostRequest(ServiceConstant.arrivedtrip_url);

            } else {
                Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {


        if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {
        }
        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
        cd = new ConnectionDetector(TripPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
        } else {
            Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        if (this.myLocation != null) {
            distance_to = location.distanceTo(myLocation);
            System.out.println("---------distance to-----------" + location.distanceTo(myLocation));
        }
        this.myLocation = location;
        cd = new ConnectionDetector(TripPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            if (!isPathShowing) {
                System.out.println("-------------jai1-----------map in location change");
                if (myLocation != null) {
                    if (googleMap != null) {
                        if (startlatlng != null && destlatlng != null) {
                        }
                    }

                }
            }
        } else {
            //  Alert1(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
            Toast.makeText(TripPage.this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_LONG).show();
        }

        if (myLocation != null) {
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

                System.out.println("movingdistacn------------" + myMovingDistance);

                if (myMovingDistance > 2) {

                    if (currentMarker != null) {
                        currentMarker.remove();
                    }

                    if (googleMap != null) {
                        if (bmp != null) {

                            if (drivermarker != null) {
                                System.out.println("---------inside new bearing value drivermarker != null-------------++" + bearingValue);

                                if (!String.valueOf(bearingValue).equalsIgnoreCase("NaN")) {
                                    if (location.getAccuracy() < 100.0 && location.getSpeed() < 6.95) {
                                        //drivermarker.setRotation(bearingValue);
                                        rotateMarker(drivermarker, bearingValue, googleMap);
                                        MarkerAnimation.animateMarkerToGB(drivermarker, latLng, mLatLngInterpolator);

                                        float zoom = googleMap.getCameraPosition().zoom;
                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
                                        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                                        googleMap.moveCamera(camUpdate);
//new change
                                    }
                                }
                            } else {
                                if (currentMarker != null) {
                                    currentMarker.remove();
                                }
                                drivermarker = googleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                        .anchor(0.5f, 0.5f)
                                        .rotation(myLocation.getBearing())
                                        .flat(true));
                            }


                        }
                    }
                }
                oldLatLng = newLatLng;
                //      sendLocationToUser(myLocation);
            } catch (Exception e) {
            }


        }

    }

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

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        //     mHandlerTask.run();
    }

    private void setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    private void Alert1(String title, String message) {
        final PkDialog mDialog = new PkDialog(TripPage.this);
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


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE, android.Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }

    private void requestNavigationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_NAVIGATION_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == DropLocationRequestCode) {


                String sAddress = data.getStringExtra("Selected_Location");
                String Slattitude = data.getStringExtra("Selected_Latitude");
                String Slongitude = data.getStringExtra("Selected_Longitude");
                tv_drop_address.setText(sAddress);

                str_drop_location = sAddress;
                str_drop_Latitude = Slattitude;
                str_drop_Longitude = Slongitude;

                if (tv_drop_address.getText().toString().length() > 0) {
                    Rl_layout_arrived.setVisibility(View.VISIBLE);


                    GPSTracker gps = new GPSTracker(TripPage.this);
                    if (gps.canGetLocation()&& gps.isgpsenabled()) {
                        double dLatitude = gps.getLatitude();
                        double dLongitude = gps.getLongitude();
                        MyCurrent_lat = dLatitude;
                        MyCurrent_long = dLongitude;
                        if (Userlist.size() == 1) {

                            System.out.println("-jai----------normal ride ------------ lat long----------" + str_drop_Latitude + " " + str_drop_Longitude);
                            Userlist.get(0).setDrop_lat(str_drop_Latitude);
                            Userlist.get(0).setDrop_lon(str_drop_Longitude);
                            Userlist.get(0).setDrop_location(str_drop_location);
                            Begin(str_drop_location, "", "", "Normal");

                        }

                        LatLng fromLat = new LatLng(MyCurrent_lat, MyCurrent_long);
                        LatLng toLat = new LatLng(Double.parseDouble(str_drop_Latitude), Double.parseDouble(str_drop_Longitude));

                        if (googleMap != null) {
                            googleMap.clear();

                            MarkerOptions marker = new MarkerOptions().position(fromLat).title("Pickup");
                            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
                            googleMap.addMarker(marker);

                            MarkerOptions marker1 = new MarkerOptions().position(toLat).title("Drop");
                            marker1.icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                            googleMap.addMarker(marker1);
                        } else {
                            Toast.makeText(TripPage.this, "Prabu", Toast.LENGTH_LONG);
                        }


                        multiple_latlon_list.clear();
                        MultipleLatLongPojo nor_u_pojo = new MultipleLatLongPojo();
                        nor_u_pojo.setLat(String.valueOf(MyCurrent_lat));
                        nor_u_pojo.setLon(String.valueOf(MyCurrent_long));
                        nor_u_pojo.setTxt(Userlist.get(0).getUser_name() + " " + "Pickup");
                        multiple_latlon_list.add(nor_u_pojo);

                        MultipleLatLongPojo nor_u_pojo1 = new MultipleLatLongPojo();
                        nor_u_pojo1.setLat(String.valueOf(str_drop_Latitude));
                        nor_u_pojo1.setLon(String.valueOf(str_drop_Longitude));
                        nor_u_pojo.setTxt(Userlist.get(0).getUser_name() + " " + "Drop");
                        multiple_latlon_list.add(nor_u_pojo1);

                        if (!"".equalsIgnoreCase(carIcon)) {

                            Picasso.with(TripPage.this)
                                    .load(carIcon)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                            if (bitmap != null) {
                                                String s = BitMapToString(bitmap);
                                                System.out.println("session bitmap" + s);
                                                session.setVehicle_BitmapImage(s);
                                                bmp = bitmap;
                                                double Dlatitude = MyCurrent_lat;
                                                double Dlongitude = MyCurrent_long;


                                                if (currentMarker != null) {
                                                    currentMarker.remove();
                                                }
                                                currentMarker = googleMap.
                                                        addMarker(new MarkerOptions()
                                                                .position(new LatLng(Dlatitude, Dlongitude))
                                                                .icon(BitmapDescriptorFactory
                                                                        .fromBitmap(bmp)));


                                               /* if (drivermarker != null)
                                                {
                                                    drivermarker.remove();
                                                }
                                                drivermarker = googleMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(Dlatitude, Dlongitude))
                                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp)));*/
                                            }

                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                        }

                        if (multiple_latlon_list.size() >= 2) {
                            GetRouteTask getRoute = new GetRouteTask(multiple_latlon_list, String.valueOf(MyCurrent_lat), String.valueOf(MyCurrent_long));
                            getRoute.execute();
                        }

                    }
                } else {
                    Rl_layout_arrived.setVisibility(View.GONE);
                }

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + Str_user_phone_no));
                    startActivity(callIntent);
                }
                break;
            case PERMISSION_REQUEST_NAVIGATION_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(TripPage.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234);
                    } else {
                        System.out.println("-----------Userlist----------------" + Userlist.size());

                        for (int i = 0; i < Userlist.size(); i++) {
                            System.out.println("-----------active_ride--------getRide_id--------" + active_ride + Userlist.get(i).getRide_id());
                            if (active_ride.equals(Userlist.get(i).getRide_id())) {
                                if (Userlist.get(i).getBtn_group().equals("2")) {
                                    moveNavigationPickup(Userlist.get(i).getPickup_lat(), Userlist.get(i).getPickup_lon());
                                } else {
                                    moveNavigationdrop(Userlist.get(i).getDrop_lat(), Userlist.get(i).getDrop_lon());
                                }
                            }

                        }
                    }
                }
        }
    }


    /* private class GetRouteTask extends AsyncTask<String, Void, String> {

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
                         isPathShowing=true;
                         //       mHandler.removeCallbacks(mHandlerTask);
                     }
                 }
             }
             catch (Exception e)
             {
                 e.printStackTrace();
             }
         }
     }*/
//-----------------Move Back on  phone pressed  back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            // nothing
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (customHandler != null) {
                customHandler.removeCallbacks(updateTimerThread);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        unregisterReceiver(finishReceiver);
    }
}
