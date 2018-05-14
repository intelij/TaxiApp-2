package com.cabily.cabilydriver;

import android.Manifest;
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
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.app.latlnginterpolation.LatLngInterpolator;
import com.app.latlnginterpolation.MarkerAnimation;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.app.xmpp.XmppService;
import com.cabily.cabilydriver.Helper.GEODBHelper;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.GPSTracker;
import com.cabily.cabilydriver.Utils.GoogleNavigationService;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;

import org.jivesoftware.smack.chat.Chat;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by user88 on 10/28/2015.
 */
public class ArrivedTrip extends SubclassActivity implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "swipe";
    private Context context;
    private SessionManager session;
    private String driver_id = "";
    private String Str_RideId = "";
    private String Str_address = "";
    private String Str_pickUp_Lat = "";
    private String Str_pickUp_Long = "";
    private String Str_username = "";
    private String Str_user_rating = "";
    private String Str_user_phoneno = "";
    private String Str_user_img = "";
    private String Str_droplat = "";
    private String Str_droplon = "";
    private String str_drop_location = "",str_pickup_time="";
    private TextView Tv_Address, Tv_RideId, Tv_usename,Tv_date;
    private RelativeLayout Rl_layout_userinfo, Rl_layout_arrived;
    private String ERROR_TAG = "Unknown Error Occured";
    private RelativeLayout Rl_layout_enable_voicenavigation;
    float[] results;
    final static int REQUEST_LOCATION = 199;
    float initialX, initialY;
    // List<Overlay> mapOverlays;
    private Barcode.GeoPoint point1, point2;
    private LocationManager locManager;
    Drawable drawable;
    Document document;
    GMapV2GetRouteDirection v2GetRouteDirection;
    LatLng fromPosition;
    LatLng toPosition;
    MarkerOptions markerOptions;
    Location location;
    private StringRequest postrequest;
    private Dialog dialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GoogleMap googleMap;
    private GPSTracker gps;
    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    private RelativeLayout alert_layout,Rl_traffic;
    private TextView alert_textview;
    private ImageView phone_call;
    private String Str_Latitude = "", Str_longitude = "";

    /*  private final static int INTERVAL = 45000;
      Handler mHandler;*/
    private BroadcastReceiver finishReceiver;

    public static ArrivedTrip arrivedTrip_class;

    private ServiceRequest mRequest;
    private String Suser_Id = "";
    private LocationRequest mLocationRequest;
    public static Location myLocation;
    private GoogleApiClient mGoogleApiClient;

    PendingResult<LocationSettingsResult> result;
    private Marker currentMarker , pickupmarker,dropmarker;
    MarkerOptions marker;
    private LatLng latLng;
    double previous_lat, previous_lon, current_lat, current_lon;
    private GEODBHelper myDBHelper;
    String traffic_status;
    //Slider Design Declaration
    SeekBar sliderSeekBar;
    ShimmerButton Bt_slider;
    Shimmer shimmer;
    String base64;
    Bitmap bmp;
    ImageButton traffic_button;
    final int PERMISSION_REQUEST_CODE = 111;
    final int PERMISSION_REQUEST_NAVIGATION_CODE = 222;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private Dialog cantact_dialog,sms_popup;
    //-----------------------------code for car moving handler------------
    private Handler arrivedTripHandler = new Handler();
    private int count = 0;

   /* private Runnable arrivedTripRunnable = new Runnable() {
        @Override
        public void run() {
            gps = new GPSTracker(ArrivedTrip.this);
            if (gps != null && gps.canGetLocation()) {
            } else {
                enableGpsService();
            }
            arrivedTripHandler.postDelayed(this, 600);
        }
    };*/
    Chat chat;



    public static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arrivedtrip);
        arrivedTrip_class = ArrivedTrip.this;

        // Receiving the data from broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.app.finish.ArrivedTrip");
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
            initilizeMap();
        } catch (Exception e) {
        }

        //Starting Xmpp service
     //   ChatingService.startDriverAction(ArrivedTrip.this);

        Rl_layout_userinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArrivedTrip.this, UserInfo.class);
                intent.putExtra("user_name", Str_username);
                intent.putExtra("user_phoneno", Str_user_phoneno);
                intent.putExtra("user_rating", Str_user_rating);
                intent.putExtra("user_image", Str_user_img);
                intent.putExtra("RideId", Str_RideId);
                intent.putExtra("Btn_group", "2");
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        phone_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseContactOptions();

                          }
        });


        Rl_layout_enable_voicenavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {

                    if(!checkWriteExternalStoragePermission())
                    {
                        requestNavigationPermission();
                    }else
                    {
                        if (!Settings.canDrawOverlays(ArrivedTrip.this)) {
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

             /*   String voice_curent_lat_long = MyCurrent_lat + "," + MyCurrent_long;
                String voice_destination_lat_long = Str_pickUp_Lat + "," + Str_pickUp_Long;
                System.out.println("----------fromPosition---------------" + voice_curent_lat_long);
                System.out.println("----------toPosition---------------" + voice_destination_lat_long);
                String locationUrl = "http://maps.google.com/maps?saddr=" + voice_curent_lat_long + "&daddr=" + voice_destination_lat_long;
                System.out.println("----------locationUrl---------------" + locationUrl);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(locationUrl));
                startActivity(intent);*/
            }
        });


    }

    private void chooseContactOptions()
    {
        cantact_dialog = new Dialog(ArrivedTrip.this);
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

                    cd = new ConnectionDetector(ArrivedTrip.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        phonemask_Call(ServiceConstant.phoneMasking);
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                } else {
                    if (Str_user_phoneno != null) {

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
                        Alert(ArrivedTrip.this.getResources().getString(R.string.alert_label_title), ArrivedTrip.this.getResources().getString(R.string.arrived_alert_content1));
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
                    showMessagePopup();


                }
                else {

                    try {

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("smsto:" + Uri.encode(Str_user_phoneno)));
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {

                    }


                    /*Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setType("vnd.android-dir/mms-sms");
                    i.putExtra("address", Str_user_phoneno);
                    startActivity(i);*/


                }



            }
        });
    }

    public void showMessagePopup()
    {

        sms_popup = new Dialog(ArrivedTrip.this);
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
                String sms=ed_msg.getText().toString();
                sms_popup.dismiss();
                if(sms.trim().length()>0) {
                    phonemask_sms(ServiceConstant.phoneMasking_sms, sms);
                }
                else
                {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.sms_masking_text));
                }

            }
        });
    }
    private void phonemask_sms(String Url,String msg) {
        dialog = new Dialog(ArrivedTrip.this);
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
        mRequest = new ServiceRequest(ArrivedTrip.this);
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
                        Alert(getResources().getString(R.string.action_loading_sucess), SResponse);
                    } else {
                        Alert(getResources().getString(R.string.alert_sorry_label_title), SResponse);
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
    private void phonemask_Call(String Url) {
        dialog = new Dialog(ArrivedTrip.this);
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

        mRequest = new ServiceRequest(ArrivedTrip.this);
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
                        Alert(getResources().getString(R.string.action_loading_sucess), SResponse);
                    } else {
                        Alert(getResources().getString(R.string.alert_sorry_label_title), SResponse);
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


    private void initialize() {
        session = new SessionManager(ArrivedTrip.this);
        gps = new GPSTracker(ArrivedTrip.this);
        v2GetRouteDirection = new GMapV2GetRouteDirection();
        myDBHelper = new GEODBHelper(getApplicationContext());
        myDBHelper.insertDriverStatus("3");
   //     mHandler=new Handler();

        session.setApp_Current_Page_Status("ArrivedTrip");

     //   arrivedTripHandler.post(arrivedTripRunnable);
        HashMap<String, String> user = session.getUserDetails();
        HashMap<String, String> bitmap = session.getBitmapCode();
        base64 = bitmap.get(SessionManager.KEY_VEHICLE_BitMap_IMAGE);
        bmp=StringToBitMap(base64);

        driver_id = user.get(SessionManager.KEY_DRIVERID);
        Rl_traffic = (RelativeLayout) findViewById(R.id.traffic_btn_layout);
        ImageButton refresh_button = (ImageButton) findViewById(R.id.refresh);


        Tv_RideId = (TextView) findViewById(R.id.trip_arrived_ride_id);
        Tv_date = (TextView) findViewById(R.id.trip_arrived_user_date);
        alert_textview = (TextView) findViewById(R.id.arrivd_Tripaccpt_alert_textView);
        alert_layout = (RelativeLayout) findViewById(R.id.arrivd_Tripaccpt_alert_layout);
        phone_call = (ImageView) findViewById(R.id.user_phonecall);

        Intent i = getIntent();

        Str_address = i.getStringExtra("address");
        Str_RideId = i.getStringExtra("rideId");
        Str_pickUp_Lat = i.getStringExtra("pickuplat");
        Str_pickUp_Long = i.getStringExtra("pickup_long");
        Str_username = i.getStringExtra("username");
        Str_user_rating = i.getStringExtra("userrating");
        Str_user_phoneno = i.getStringExtra("phoneno");
        Str_user_img = i.getStringExtra("userimg");
        Str_droplat = i.getStringExtra("drop_lat");
        Str_droplon = i.getStringExtra("drop_lon");
        str_drop_location = i.getStringExtra("drop_location");
        str_pickup_time= i.getStringExtra("pickup_time");

        System.out.println("KKKKKKK---------" + Str_droplat);
        Suser_Id = i.getStringExtra("UserId");

        ContinuousRequestAdapter.userID = Suser_Id;


        System.out.println("UserId---------" + Suser_Id);
        System.out.println("adres---------" + Str_address);
        System.out.println("id---------" + Str_RideId);
        Tv_Address = (TextView) findViewById(R.id.trip_arrived_user_address);
        // Tv_RideId = (TextView) findViewById(R.id.trip_arrived_user_id);
        Tv_usename = (TextView) findViewById(R.id.trip_arrived_usernameTxt);
        Rl_layout_userinfo = (RelativeLayout) findViewById(R.id.layout_arrived_trip_userinfo);
        Rl_layout_arrived = (RelativeLayout) findViewById(R.id.layout_arrivedbtn);
        Rl_layout_enable_voicenavigation = (RelativeLayout) findViewById(R.id.layout_arrived_Enable_voice);

        shimmer = new Shimmer();
        sliderSeekBar = (SeekBar) findViewById(R.id.arrived_Trip_seek);
        Bt_slider = (ShimmerButton) findViewById(R.id.arrived_Trip_slider_button);
        shimmer.start(Bt_slider);

        sliderSeekBar.setOnSeekBarChangeListener(this);

        Tv_Address.setText(Str_address);
        // Tv_RideId.setText(Str_RideId);
        Tv_usename.setText(Str_username);
        Tv_RideId.setText(Str_RideId);
        Tv_date.setText(str_pickup_time);
        cd = new ConnectionDetector(ArrivedTrip.this);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
       //     mHandlerTask.run();
        } else {
            Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
        }
        traffic_button = (ImageButton) findViewById(R.id.traffic);

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

                    // two cars in arrive issue
                    /* if(bmp!=null) {
                        LatLng   toPosition = new LatLng(Dlatitude, Dlongitude);
                        drivermarker=      googleMap.addMarker(new MarkerOptions()
                                .position(toPosition)
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp)));

                        //                       currentMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude)).icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                    }*/






                }

              /*  Intent i = new Intent(DriverMapActivity.this, DriverMapActivity.class);
                i.putExtra("availability",availability);
                finish();
                startActivity(i);*/


            }
        });



        traffic_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HashMap<String, String> user = session.getTrafficImage();
                traffic_status = user.get(SessionManager.KEY_Traffic);
                if("1".equals(traffic_status))
                {
                    googleMap.setTrafficEnabled(false);
                    session.setTrafficImage("0");
                    traffic_button.setBackgroundResource(R.drawable.traffic_off_new);
                    Rl_traffic.setBackgroundResource(R.drawable.traffic_conner_bc);
                }
                else
                {
                    googleMap.setTrafficEnabled(true);
                    session.setTrafficImage("1");
                    traffic_button.setBackgroundResource(R.drawable.traffic_on_new);
                    Rl_traffic.setBackgroundResource(R.drawable.traffic_conner_on_bc);
                }
            }
        });

        if (!isMyServiceRunning(XmppService.class)) {
            startService(new Intent(ArrivedTrip.this, XmppService.class));
        }
        session.setXmppServiceState("online");


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(ArrivedTrip.this);
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


    //--------------code for location updatre--------
/*    Runnable mHandlerTask = new Runnable()
    { @Override public void run() {

        gps = new GPSTracker(ArrivedTrip.this);
        cd = new ConnectionDetector(ArrivedTrip.this);
        isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent)
        {
            if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {

                Str_Latitude = String.valueOf(gps.getLatitude());
                Str_longitude = String.valueOf(gps.getLongitude());

                postRequest_UpdateProviderLocation(ServiceConstant.UPDATE_CURRENT_LOCATION);
            }
        }else
        {
            Toast.makeText(ArrivedTrip.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        mHandler.postDelayed(mHandlerTask, INTERVAL);
    }
    };*/


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

/*        if (googleMap == null) {
            googleMap = ((MapFragment) ArrivedTrip.this.getFragmentManager().findFragmentById(R.id.arrived_trip_view_map)).getMap();
            if (googleMap == null) {
                Toast.makeText(ArrivedTrip.this, getResources().getString(R.string.action_alert_unabletocreatemap), Toast.LENGTH_SHORT).show();
            }
        }
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(false);

        */



    }
    public void loadMap(GoogleMap arg) {
        googleMap = arg;
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        // Enable / Disable zooming functionality

        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        ArrivedTrip.this, R.raw.mapstyle));

        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
   //     googleMap.setTrafficEnabled(true);
        HashMap<String, String> user1 = session.getTrafficImage();
        traffic_status = user1.get(SessionManager.KEY_Traffic);

        if("1".equals(traffic_status))
        {
            googleMap.setTrafficEnabled(true);
            //  session.setTrafficImage("1");
            traffic_button.setBackgroundResource(R.drawable.traffic_on_new);
            Rl_traffic.setBackgroundResource(R.drawable.traffic_conner_on_bc);
        }
        if (gps != null && gps.canGetLocation()) {
            double Dlatitude = gps.getLatitude();
            double Dlongitude = gps.getLongitude();
            MyCurrent_lat = Dlatitude;
            MyCurrent_long = Dlongitude;

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            System.out.println("currntlat----------" + MyCurrent_lat);
            System.out.println("currntlon----------" + MyCurrent_long);


        } else {
            alert_layout.setVisibility(View.VISIBLE);
            alert_textview.setText(getResources().getString(R.string.alert_gpsEnable));
        }
        markerOptions = new MarkerOptions();
        if (googleMap == null) {
            /*Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();*/
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {

        if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {
        }
        myLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        if (myLocation != null) {



            if (googleMap != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
                        17));
            }

            if (currentMarker != null) {
                currentMarker.remove();
            }


            currentMarker = googleMap.
                    addMarker(new MarkerOptions()
                            .position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(bmp)));


        }

    }

    MarkerOptions mm = new MarkerOptions();
    Marker drivermarker;
    JSONObject job = new JSONObject();
    Location oldLocation;
    LatLngInterpolator mLatLngInterpolator;
    LatLng oldLatLng,newLatLng;
    double myMovingDistance = 0.0;
    @Override
    public void onLocationChanged(Location location) {
        this.myLocation = location;
        System.out.println("locatbegintrip-----------" + location);
        if (myLocation != null) {
            try {
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                drawRouteInMap();



                if (oldLatLng == null) {
                    System.out.println("----------inside oldLatLngnull--------");
                    oldLatLng = latLng;
                }
                newLatLng = latLng;
                if (mLatLngInterpolator == null) {
                    mLatLngInterpolator = new LatLngInterpolator.Linear();
                }

           //     sendLocationToTheUser(myLocation);
                /*if (drivermarker != null) {
                    drivermarker.remove();
                }*/
                if (googleMap != null) {
                    if(bmp!=null) {
                       /* if (drivermarker != null) {
                            drivermarker.remove();
                        }*/


                        oldLocation = new Location("");
                        oldLocation.setLatitude(oldLatLng.latitude);
                        oldLocation.setLongitude(oldLatLng.longitude);


                        float bearingValue = oldLocation.bearingTo(location);



                        myMovingDistance = oldLocation.distanceTo(location);

                        //  Toast.makeText(EndTrip.this, String.valueOf(myMovingDistance), Toast.LENGTH_SHORT).show();

                        System.out.println("movingdistacn------------" + myMovingDistance);

                        if (myMovingDistance > 2) {
                            System.out.println("---------------------------jai-------------------inside distance");
                            if (currentMarker != null) {
                                currentMarker.remove();
                            }

                            if (drivermarker != null) {
                                System.out.println("---------------------------jai-------------------inside drivermarker");
                                System.out.println("---------inside new bearing value drivermarker != null-------------++" + bearingValue);
                                //        System.out.println("---------inside drivermarker != null-------------++" + getBearing(oldLatLng, newLatLng));
                                //Toast.makeText(EndTrip.this, String.valueOf(bearingValue), Toast.LENGTH_SHORT).show();

                                if (!String.valueOf(bearingValue).equalsIgnoreCase("NaN")) {
                                    if (location.getAccuracy() < 100.0 && location.getSpeed() < 6.95) {
                                        //drivermarker.setRotation(bearingValue);
                                        rotateMarker(drivermarker, bearingValue, googleMap);
                                        MarkerAnimation.animateMarkerToGB(drivermarker, latLng, mLatLngInterpolator);
                                        System.out.println("---------------------------jai-------------------inside getAccuracy");
                                        float zoom = googleMap.getCameraPosition().zoom;

//new change
                                       /* CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
                                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                                    }
                                }
                            } else {

                                System.out.println("---------------------------jai-------------------inside drivermarker create");

                                currentMarker.remove();
                                drivermarker = googleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                        .anchor(0.5f, 0.5f)
                                        .rotation(myLocation.getBearing())
                                        .flat(true));
                            }

                        }

                        System.out.println("---------------------------jai-------------------outside drivermarker create");
                        oldLatLng = newLatLng;
                       /* drivermarker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp)));

                        float zoom = googleMap.getCameraPosition().zoom;*/



                       /* CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
                        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        googleMap.moveCamera(camUpdate);*/
                    }
                }
               /* drivermarker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.carmove)));
                float zoom = googleMap.getCameraPosition().zoom;
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
                CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                googleMap.moveCamera(camUpdate);*/


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
               /* System.out.println("---------------------------jai-------------------inside handler");*/
                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }



    private void drawRouteInMap() {
        try {
            if(!isDrawnOnMap){
                fromPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                toPosition = new LatLng(Double.parseDouble(Str_pickUp_Lat), Double.parseDouble(Str_pickUp_Long));
                marker = new MarkerOptions().position(new LatLng(MyCurrent_lat, MyCurrent_long));
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.dark_green_flag));

                if(pickupmarker!=null)
                {
                    pickupmarker.remove();
                }
                if(dropmarker!=null)
                {
                    dropmarker.remove();
                }
                pickupmarker =    googleMap.addMarker(new MarkerOptions()
                        .position(fromPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker)));
                dropmarker =    googleMap.addMarker(new MarkerOptions()
                        .position(toPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.usericon_new)));


               /* if (googleMap != null) {
                    currentMarker = googleMap.addMarker(marker);
                }*/


                if (fromPosition != null && toPosition != null) {
                    GetRouteTask getRoute = new GetRouteTask();
                    getRoute.execute();
                }
            }else{
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), ERROR_TAG, Toast.LENGTH_SHORT).show();
        }
    }

  /*  private void sendLocationToTheUser(Location location) throws JSONException {

        System.out.println("userid-------------"+chatID);

        String sendlat = Double.valueOf(location.getLatitude()).toString();
        String sendlng = Double.valueOf(location.getLongitude()).toString();
        if(job == null){
            job = new JSONObject();
        }
        job.put("action", "driver_loc");
        job.put("latitude", sendlat);
        job.put("longitude", sendlng);
        job.put("ride_id", Str_RideId);
        builder.sendMessage(chatID,job.toString());

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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        super.onStop();
  //      mHandler.removeCallbacks(mHandlerTask);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishReceiver);

        if(chat != null){
            chat.close();
        }

     //   mHandler.removeCallbacks(mHandlerTask);

    }
    boolean isDrawnOnMap = false;
    private class GetRouteTask extends AsyncTask<String, Void, String> {

        String response = "";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            document = v2GetRouteDirection.getDocument(fromPosition, toPosition, GMapV2GetRouteDirection.MODE_DRIVING);
            response = "Success";
            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            // googleMap.clear();
            ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
            PolylineOptions rectLine = new PolylineOptions().width(15).color(getResources().getColor(R.color.app_color));

            for (int i = 0; i < directionPoint.size(); i++) {
                rectLine.add(directionPoint.get(i));
            }
            // Adding route on the map
            if (googleMap != null) {
                googleMap.addPolyline(rectLine);
                markerOptions.position(fromPosition);
                markerOptions.position(toPosition);
                markerOptions.draggable(true);
                //googleMap.addMarker(markerOptions);
                isDrawnOnMap = true;

                if(bmp!=null) {
                    if (drivermarker != null) {
                        drivermarker.remove();
                    }


                    if (currentMarker != null) {
                        currentMarker.remove();
                    }


                    drivermarker = googleMap.addMarker(new MarkerOptions()
                            .position(fromPosition)
                            .icon(BitmapDescriptorFactory.fromBitmap(bmp)));



                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(toPosition);
                    builder.include(fromPosition);
                    LatLngBounds bounds = builder.build();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                }
            }
        }
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


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    //-----------------------Code for arrived post request-----------------
    private void PostRequest(String Url) {
        dialog = new Dialog(ArrivedTrip.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));
        System.out.println("-------------Arrived Trip Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);
        jsonParams.put("ride_id", Str_RideId);
        jsonParams.put("driver_lat", String.valueOf(MyCurrent_lat));
        jsonParams.put("driver_lon", String.valueOf(MyCurrent_long));

        System.out.println("---------------Arrived Trip jsonParams---------------------------"+ jsonParams);


        mRequest = new ServiceRequest(ArrivedTrip.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                JSONObject object = null;
                Log.e("arrived", response);

                System.out.println("response---------" + response);

                String Str_status = "", Str_response = "",Zero_response="";

                try {
                    object   = new JSONObject(response);
                    Str_status = object.getString("status");
                    Str_response = object.getString("response");

                    if (Str_status.equalsIgnoreCase("0")){
                        Zero_response=object.getString("ride_view");
                        Str_response = object.getString("response");
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();
                if (Str_status.equalsIgnoreCase("1")) {
                    if (Str_droplat != null && !Str_droplon.equalsIgnoreCase("") && Str_droplat != null && !Str_droplon.equalsIgnoreCase("")) {
                        Intent intent = new Intent(ArrivedTrip.this, BeginTrip.class);
                        intent.putExtra("user_name", Str_username);
                        intent.putExtra("rideid", Str_RideId);
                        intent.putExtra("user_image", Str_user_img);
                        intent.putExtra("user_phoneno", Str_user_phoneno);
                        intent.putExtra("drop_location", str_drop_location);
                        intent.putExtra("DropLatitude", Str_droplat);
                        intent.putExtra("DropLongitude", Str_droplon);
                        intent.putExtra("UserId",Suser_Id);
                        String locationaddressstartingpoint = String.valueOf(MyCurrent_lat + "," + MyCurrent_long);
                        Log.d("myloc", locationaddressstartingpoint);
                        intent.putExtra("pickuplatlng", Str_droplat + "," + Str_droplon);
                        intent.putExtra("startpoint", locationaddressstartingpoint);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {
                        Intent intent = new Intent(ArrivedTrip.this, BeginTrip.class);
                        intent.putExtra("user_name", Str_username);
                        intent.putExtra("user_phoneno", Str_user_phoneno);
                        intent.putExtra("user_image", Str_user_img);
                        intent.putExtra("rideid", Str_RideId);
                        intent.putExtra("UserId",Suser_Id);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                } else {


                    if (Str_status.equalsIgnoreCase("0")) {

                        if (Zero_response.equals("stay")) {
                            Alert(getString(R.string.alert_sorry_label_title), Str_response);
                        }
                        if (Zero_response.equals("next")) {

                            if (Str_droplat != null && !Str_droplon.equalsIgnoreCase("") && Str_droplat != null && !Str_droplon.equalsIgnoreCase("")) {
                                Intent intent = new Intent(ArrivedTrip.this, BeginTrip.class);
                                intent.putExtra("user_name", Str_username);
                                intent.putExtra("rideid", Str_RideId);
                                intent.putExtra("user_image", Str_user_img);
                                intent.putExtra("user_phoneno", Str_user_phoneno);
                                intent.putExtra("drop_location", str_drop_location);
                                intent.putExtra("DropLatitude", Str_droplat);
                                intent.putExtra("DropLongitude", Str_droplon);
                                intent.putExtra("UserId",Suser_Id);
                                String locationaddressstartingpoint = String.valueOf(MyCurrent_lat + "," + MyCurrent_long);
                                Log.d("myloc", locationaddressstartingpoint);
                                intent.putExtra("pickuplatlng", Str_droplat + "," + Str_droplon);
                                intent.putExtra("startpoint", locationaddressstartingpoint);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            } else {
                                Intent intent = new Intent(ArrivedTrip.this, BeginTrip.class);
                                intent.putExtra("user_name", Str_username);
                                intent.putExtra("user_phoneno", Str_user_phoneno);
                                intent.putExtra("user_image", Str_user_img);
                                intent.putExtra("rideid", Str_RideId);
                                intent.putExtra("UserId",Suser_Id);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }
                        }
                        if (Zero_response.equals("detail")) {
                            finish();


                            Intent intent = new Intent(ArrivedTrip.this, TripSummaryDetail.class);
                            intent.putExtra("ride_id", Str_RideId);
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

                                       /* Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);*/
                }

            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();
            }

        });


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
                            status.startResolutionForResult(ArrivedTrip.this, REQUEST_LOCATION);
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
            Bt_slider.setText(getResources().getString(R.string.arrivedtrip_arrivedtriptv_label));
            shimmer.start(Bt_slider);
        } else if (seekBar.getProgress() > 70) {
            seekBar.setProgress(100);
            Bt_slider.setVisibility(View.VISIBLE);
            Bt_slider.setText(getResources().getString(R.string.arrivedtrip_arrivedtriptv_label));
            shimmer.start(Bt_slider);
            sliderSeekBar.setVisibility(View.VISIBLE);

            cd = new ConnectionDetector(ArrivedTrip.this);
            isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                PostRequest(ServiceConstant.arrivedtrip_url);
                System.out.println("arrived------------------" + ServiceConstant.arrivedtrip_url);
            } else {
                Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
            }
        }
    }
    //-----------------------Update current Location for notification  Post Request-----------------
    private void postRequest_UpdateProviderLocation(String Url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id",Str_RideId);
        jsonParams.put("latitude",Str_Latitude );
        jsonParams.put("longitude",Str_longitude );
        jsonParams.put("driver_id",driver_id);

        System.out.println("-------------Arrivedsendrequestride_id----------------" + Str_RideId);
        System.out.println("-------------Arrivedsendrequestlatitude----------------" + Str_Latitude );
        System.out.println("-------------Arrivedsendrequestlongitude----------------" + Str_longitude);

        System.out.println("-------------latlongupdate----------------" + Url);
        mRequest = new ServiceRequest(ArrivedTrip.this);
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
        if(chat != null){
            chat.close();
        }
    //    mHandler.removeCallbacks(mHandlerTask);
    }
    private void moveNavigation() {

        if(isGoogleMapsInstalled()) {

            if (!isMyServiceRunning(GoogleNavigationService.class)) {
                startService(new Intent(getApplicationContext(), GoogleNavigationService.class));
            }

            session.setGoogleNavicationValueArrived(Str_address,Str_RideId,Str_pickUp_Lat,Str_pickUp_Long,Str_username,Str_user_rating,Str_user_phoneno,Str_user_img,Suser_Id,Str_droplat,Str_droplon,str_drop_location);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(String.format("google.navigation:ll=%s,%s%s", Double.parseDouble(Str_pickUp_Lat), Double.parseDouble(Str_pickUp_Long), "&mode=c")));
            startActivity(intent);

        }else{

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_label_no_google_map_installed), Toast.LENGTH_LONG).show();

        }

//        addBackLayout();
    }

    //Adding back layout for Voice Navigation
    public void addBackLayout() {

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = getLayoutInflater();
        final View viewa = inflater.inflate(R.layout.navigation_back_layout, null);
        viewa.findViewById(R.id.imageView_kill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewa.findViewById(R.id.imageView_kill).setVisibility(View.GONE);
                addBackLayout_two();
                viewa.setVisibility(View.GONE);
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
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.navigation_back_full_view, null);
        view.findViewById(R.id.imageView_kill_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent myIntent = new Intent(v.getContext(), ArrivedTrip.class);
                    myIntent.addCategory(Intent.CATEGORY_HOME);
                    myIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    myIntent.addCategory(Intent.CATEGORY_MONKEY);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.putExtra("address", Str_address);
                    myIntent.putExtra("rideId", Str_RideId);
                    myIntent.putExtra("pickuplat", Str_pickUp_Lat);
                    myIntent.putExtra("pickup_long", Str_pickUp_Long);
                    myIntent.putExtra("username", Str_username);
                    myIntent.putExtra("userrating", Str_user_rating);
                    myIntent.putExtra("phoneno", Str_user_phoneno);
                    myIntent.putExtra("userimg", Str_user_img);
                    myIntent.putExtra("UserId", Suser_Id);
                    myIntent.putExtra("drop_lat", Str_droplat);
                    myIntent.putExtra("drop_lon", Str_droplon);
                    myIntent.putExtra("drop_location", str_drop_location);

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


    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
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
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }
    private void requestNavigationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_NAVIGATION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + Str_user_phoneno));
                    startActivity(callIntent);
                }
                break;
            case PERMISSION_REQUEST_NAVIGATION_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(ArrivedTrip.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234);
                    } else {
                        moveNavigation();
                    }
                }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(ArrivedTrip.this)) {
                    Toast.makeText(ArrivedTrip.this, "SYSTEM_ALERT_WINDOW permission not granted...", Toast.LENGTH_SHORT).show();
                } else {
                    moveNavigation();
                }
            }
        }
    }

    public Bitmap StringToBitMap(String encodedString){
        System.out.println("base 64"+encodedString);
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
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



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        boolean b = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                b = true;
                break;
            } else {
                b = false;
            }
        }
        System.out.println("3 not running");
        return b;
    }



}
