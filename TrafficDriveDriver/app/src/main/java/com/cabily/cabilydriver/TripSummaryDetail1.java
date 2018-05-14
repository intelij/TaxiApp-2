package com.cabily.cabilydriver;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.Pojo.CancelReasonPojo;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.CurrencySymbolConverter;
import com.cabily.cabilydriver.Utils.GPSTracker;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.adapter.CancelReasonAdapter;
import com.cabily.cabilydriver.subclass.SubclassActivity;
import com.cabily.cabilydriver.widgets.PkDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by user14 on 9/22/2015.
 */
public class TripSummaryDetail1 extends SubclassActivity {

    private RelativeLayout Rl_total_bills_details;
    private RelativeLayout Rl_layout_Tripststus;
    private RelativeLayout Rl_layout_amount_status;
    private LinearLayout Rl_layout_pickup_details;
    private RelativeLayout Rl_layout_drop_details;
    private LinearLayout Ll_layouit_tripsummerydetail_timings;

    private TextView Tv_tripdetail_rideId, TV_drop_time, Tv_tripdetail_address, Tv_tripdetail_pickup, Tv_tripdetail_drop, Tv_tripdetail_ride_distance,
            Tv_tripdetail_timetaken, Tv_tripdetail_waitingtime, Tv_tripdetail_total_paid, Tv_tripdetail_total_amount_paid, Tv_trip_status, Tv_trip_paid_status, Tv_wallet_uage, Tv_coupon_discount;

    Dialog dialog;
    Dialog ridecancel_dialog;
    private String sCurrencySymbol = "";

    private String Str_ride_distance = "", Str_time_taken = "", Str_wait_time = "", Str_totalpaid = "";
    private String Str_totalbill;
    private ServiceRequest mRequest;

    private String Str_continue_ridedetail = "";
    private String Str_rideId = "", type = "";
    private String driver_id = "";
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private Context context;
    private SessionManager session;

    private StringRequest postrequest;
    private RelativeLayout Rl_layout_tripdetail_back_img;
    private RelativeLayout layout_completed_details;
    private RelativeLayout layout_address_and_loction_details;
    private LinearLayout Rl_drop_loc;
    private LinearLayout Rl_drop_time;

    private Button Bt_Cancel_ride, Bt_Continue_Ride, Bt_RequestPayment;
    private GoogleMap googleMap;
    private boolean show_progress_status = false;
    GPSTracker gps;

    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;

    private String Str_lattitude = "", Str_logitude = "";
    private String Str_droplattitude = "", Str_droplongitude = "";
    private double strlat, strlon;
    private String Str_loctionaddress = "", Str_drop = "";
    private String Str_Username = "", Str_useremail = "", Str_pickup_date = "", Str_drop_date = "", Str_phoneno = "", Str_pickup_time = "", Str_userimg = "", Str_userrating = "", Str_rideid = "", Str_pickuplocation = "", Str_pickup_lat = "", Str_pickup_long = "";


    private TextView Tv_drop_addres_location;

    private String sUserID = "", str_recievecash = "", str_req_payment;

    private String Str_drop_lat = "", Str_drop_lon = "", Str_drop_location = "", disp_ride_status, disp_pay_status;

    private RelativeLayout alert_layout;
    private TextView alert_textview;
    private ListView cancel_listview;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<CancelReasonPojo> Cancelreason_arraylist;
    private CancelReasonAdapter adapter;
    private StringRequest canceltrip_postrequest;

    private String Str_currency = "";

    private TextView Tv_driverTip;
    private String Str_distance_unit = "";
    private String sTripType = "";
    private RelativeLayout Rl_wait_Time;
    private ImageView Iv_routeMap;
    private boolean isPickUpAvailable = false;
    private String sInvoiceSrc = "";
    private RelativeLayout Rl_mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tripsummer_list_detail);
        initialize();
        initilizeMap();
        //--------Disabling the map functionality---------


        Rl_layout_tripdetail_back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!type.equalsIgnoreCase("noti")) {
                    onBackPressed();

                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else if (type.equalsIgnoreCase("trip")){
                    DashBoardDriver.isOnline = true;
                    Intent i = new Intent(TripSummaryDetail1.this, DriverMapActivity.class);
                    i.putExtra("availability", "Yes");
                    startActivity(i);
                    finish();

                }else if (type.equalsIgnoreCase("tripList")){
                    onBackPressed();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else
                {
                    Intent broadcastIntent_drivermap = new Intent();
                    broadcastIntent_drivermap.setAction("com.finish.canceltrip.DriverMapActivity.finish");
                    sendBroadcast(broadcastIntent_drivermap);
                    DashBoardDriver.isOnline = true;
                    Intent i = new Intent(TripSummaryDetail1.this, DriverMapActivity.class);
                    i.putExtra("availability", "Yes");
                    startActivity(i);

                }
            }
        });

        Bt_Cancel_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TripSummaryDetail1.this, CancelTrip.class);
                intent.putExtra("RideId", Str_rideId);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        Bt_RequestPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showfaresummerydetails();

            }
        });

        Bt_Continue_Ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent trip_intent = new Intent(TripSummaryDetail1.this, TripPage.class);
                trip_intent.putExtra("interrupted", "Yes");
                startActivity(trip_intent);

                /*if (Str_continue_ridedetail.equalsIgnoreCase("arrived")) {
                    Intent intent = new Intent(TripSummaryDetail.this, ArrivedTrip.class);
                    intent.putExtra("address", Str_loctionaddress);
                    intent.putExtra("rideId", Str_rideId);
                    intent.putExtra("pickuplat", Str_pickup_lat);
                    intent.putExtra("pickup_long", Str_pickup_long);
                    intent.putExtra("username", Str_Username);
                    intent.putExtra("userrating", Str_userrating);
                    intent.putExtra("phoneno", Str_phoneno);
                    intent.putExtra("userimg", Str_userimg);
                    intent.putExtra("UserId", sUserID);
                    intent.putExtra("drop_lat", Str_drop_lat);
                    intent.putExtra("drop_lon", Str_drop_lon);
                    intent.putExtra("drop_location", Str_drop_location);
                    intent.putExtra("pickup_time", Str_pickup_time);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (Str_continue_ridedetail.equalsIgnoreCase("begin")) {
                    Intent intent = new Intent(TripSummaryDetail.this, BeginTrip.class);
                    intent.putExtra("user_name", Str_Username);
                    intent.putExtra("user_phoneno", Str_phoneno);
                    intent.putExtra("user_image", Str_userimg);
                    intent.putExtra("rideid", Str_rideId);
                    intent.putExtra("UserId", sUserID);
                    intent.putExtra("DropLatitude", Str_drop_lat);
                    intent.putExtra("DropLongitude", Str_drop_lon);
                    intent.putExtra("drop_location", Str_drop_location);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (Str_continue_ridedetail.equalsIgnoreCase("end")) {
                    Intent intent = new Intent(TripSummaryDetail.this, EndTrip.class);
                    String locationaddressstartingpoint = Str_pickup_lat + "," + Str_pickup_long;
                    String sDropLocation = Str_drop_lat + "," + Str_drop_lon;
                    intent.putExtra("name", Str_Username);
                    intent.putExtra("rideid", Str_rideId);
                    intent.putExtra("mobilno", Str_phoneno);
                    intent.putExtra("user_id", sUserID);
                    intent.putExtra("user_image", Str_userimg);
                    intent.putExtra("drop_lat", Str_drop_lat);
                    intent.putExtra("drop_lon", Str_drop_lon);
                    intent.putExtra("drop_location", Str_drop_location);
                    intent.putExtra("interrupted", "Yes");
                    intent.putExtra("pickuplatlng", sDropLocation);
                    intent.putExtra("startpoint", locationaddressstartingpoint);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Intent intent = new Intent(TripSummaryDetail.this, EndTrip.class);
                    String locationaddressstartingpoint = Str_pickup_lat + "," + Str_pickup_long;
                    String sDropLocation = Str_drop_lat + "," + Str_drop_lon;
                    intent.putExtra("name", Str_Username);
                    intent.putExtra("rideid", Str_rideId);
                    intent.putExtra("mobilno", Str_phoneno);
                    intent.putExtra("user_id", sUserID);
                    intent.putExtra("user_image", Str_userimg);
                    intent.putExtra("drop_lat", Str_drop_lat);
                    intent.putExtra("drop_lon", Str_drop_lon);
                    intent.putExtra("drop_location", Str_drop_location);
                    intent.putExtra("interrupted", "Yes");
                    intent.putExtra("pickuplatlng", sDropLocation);
                    intent.putExtra("startpoint", locationaddressstartingpoint);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
*/
            }
        });

    }

    private void initialize() {
        session = new SessionManager(TripSummaryDetail1.this);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);

        Cancelreason_arraylist = new ArrayList<CancelReasonPojo>();
        Intent i = getIntent();
        Str_rideId = i.getStringExtra("ride_id");
        if (i.hasExtra("type")) {
            type = i.getStringExtra("type");
        }
        System.out.println("rideid----------------" + Str_rideId);
        Tv_trip_paid_status = (TextView) findViewById(R.id.payment_paid_Textview_tripdetail);
        Tv_trip_status = (TextView) findViewById(R.id.tripdetail_status);
        Tv_tripdetail_total_amount_paid = (TextView) findViewById(R.id.tripdetail_view_total_paid_amount);
        Tv_tripdetail_total_paid = (TextView) findViewById(R.id.trip_detail_view_total_amount);

        Tv_tripdetail_ride_distance = (TextView) findViewById(R.id.trip_detail_distancekm);
        Tv_tripdetail_timetaken = (TextView) findViewById(R.id.tripdetail_timetaken_value);
        Tv_tripdetail_waitingtime = (TextView) findViewById(R.id.tripdetail_wait_time_value);
        TV_drop_time = (TextView) findViewById(R.id.trip_view_dropupdates);
        Tv_coupon_discount = (TextView) findViewById(R.id.coupon_discount);
        Tv_wallet_uage = (TextView) findViewById(R.id.wallet_usage);
        Bt_Continue_Ride = (Button) findViewById(R.id.trip_summerydetail_continue_ride_button);
        Bt_Cancel_ride = (Button) findViewById(R.id.trip_summerydetail_cancelride_button);
        Bt_RequestPayment = (Button) findViewById(R.id.trip_summerydetail_requestpayment_button);
        Rl_wait_Time = (RelativeLayout) findViewById(R.id.trip_detail_maximum_button2);

        

        Tv_tripdetail_drop = (TextView) findViewById(R.id.Tv_tripsummery_view_dropaddress);
        Tv_tripdetail_pickup = (TextView) findViewById(R.id.trip_view_pickupdates);
        Tv_tripdetail_address = (TextView) findViewById(R.id.Tv_tripsummery_view_address);
        Tv_tripdetail_rideId = (TextView) findViewById(R.id.tripsummry_rideidTv);
        Ll_layouit_tripsummerydetail_timings = (LinearLayout) findViewById(R.id.trip_details_view_details_time);
        Rl_total_bills_details = (RelativeLayout) findViewById(R.id.trip_detail_bill_details);
        Rl_layout_Tripststus = (RelativeLayout) findViewById(R.id.layout_trip_summery_details_status);
        Rl_layout_amount_status = (RelativeLayout) findViewById(R.id.layout_tripdetail_payment_status);
        Rl_layout_tripdetail_back_img = (RelativeLayout) findViewById(R.id.tripsummry_layouts);
        layout_completed_details = (RelativeLayout) findViewById(R.id.layoutsummery_and_bill_details);
        layout_address_and_loction_details = (RelativeLayout) findViewById(R.id.layout_rideaddress_and_locarions_details);
        Rl_layout_pickup_details = (LinearLayout) findViewById(R.id.layout_tripsummery_pickup);
        Rl_drop_time = (LinearLayout) findViewById(R.id.layout_tripsummery_drop);
        Rl_drop_loc = (LinearLayout) findViewById(R.id.layout_tripsummery_detail_dropaddress);
        // Rl_layout_drop_details = (RelativeLayout)findViewById(R.id.trip_summery_layout_drop_details);
        Tv_driverTip = (TextView) findViewById(R.id.driver_tip_tv);

        Rl_mapView = (RelativeLayout) findViewById(R.id.my_rides_detail_mapview_layout);
        Iv_routeMap = (ImageView) findViewById(R.id.my_rides_detail_route_map_imageview);

        cd = new ConnectionDetector(TripSummaryDetail1.this);
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            postRequest_tripdetail(ServiceConstant.tripsummery_view_url);
        } else {
            Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }


    //-------------------Show Summery fare  Method--------------------
    private void showfaresummerydetails() {

        final MaterialDialog dialog = new MaterialDialog(TripSummaryDetail1.this);
        View view = LayoutInflater.from(TripSummaryDetail1.this).inflate(R.layout.fare_summery_alert_dialog, null);


        final TextView Tv_reqest = (TextView) view.findViewById(R.id.requst);
        TextView tv_fare_totalamount = (TextView) view.findViewById(R.id.fare_summery_total_amount);
        TextView tv_ridedistance = (TextView) view.findViewById(R.id.fare_summery_ride_distance_value);
        TextView tv_timetaken = (TextView) view.findViewById(R.id.fare_summery_ride_timetaken_value);
        TextView tv_waittime = (TextView) view.findViewById(R.id.fare_summery_wait_time_value);
        RelativeLayout layout_request_payment = (RelativeLayout) view.findViewById(R.id.layout_faresummery_requstpayment);
        RelativeLayout layout_receive_cash = (RelativeLayout) view.findViewById(R.id.fare_summery_receive_cash_layout);


        tv_fare_totalamount.setText(Str_totalbill);
        tv_ridedistance.setText(Str_ride_distance + " " + getResources().getString(R.string.tripsummery_add_km_label));
        tv_timetaken.setText(Str_time_taken);
        tv_waittime.setText(Str_wait_time);
        dialog.setView(view).show();

        if (str_recievecash.matches("Enable")) {
            layout_receive_cash.setVisibility(View.VISIBLE);
            // layout_request_payment.setVisibility(View.VISIBLE);
        } else {
            layout_receive_cash.setVisibility(View.GONE);
            //   layout_request_payment.setVisibility(View.VISIBLE);
        }
        if (str_req_payment.matches("Enable")) {
            layout_request_payment.setVisibility(View.VISIBLE);
            Tv_reqest.setText(TripSummaryDetail1.this.getResources().getString(R.string.lbel_fare_summery_requestpayment));

            // layout_request_payment.setVisibility(View.VISIBLE);
        } else {
            layout_request_payment.setVisibility(View.GONE);
            //   layout_request_payment.setVisibility(View.VISIBLE);
        }


        layout_receive_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TripSummaryDetail1.this, PaymentPage.class);
                intent.putExtra("amount", Str_totalbill);
                intent.putExtra("rideid", Str_rideId);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


               /* Intent intent = new Intent(TripSummaryDetail.this, OtpPage.class);
                intent.putExtra("rideid",Str_rideId);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/

            }
        });

        layout_request_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(TripSummaryDetail1.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {

                    if (Tv_reqest.getText().toString().equalsIgnoreCase(TripSummaryDetail1.this.getResources().getString(R.string.lbel_fare_summery_requestpayment))) {
                        postRequest_Reqqustpayment_TripDetail(ServiceConstant.request_paymnet_url);
                        System.out.println("arrived------------------" + ServiceConstant.request_paymnet_url);
                    } else {
                        Intent intent = new Intent(TripSummaryDetail1.this, RatingsPage.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                } else {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });

    }


    private void initilizeMap() {


        if (googleMap == null) {
            MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.tripsummery_view_map));
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap arg) {
                    loadMap(arg);
                }
            });
        }

       /* if (googleMap == null) {
            googleMap = ((MapFragment)TripSummaryDetail.this.getFragmentManager().findFragmentById(R.id.tripsummery_view_map)).getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(TripSummaryDetail.this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }*/


    }

    public void loadMap(GoogleMap arg) {
        googleMap = arg;
        // Changing map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Showing / hiding your current location
        googleMap.setMyLocationEnabled(false);
        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Enable / Disable Compass icon
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        TripSummaryDetail1.this, R.raw.mapstyle));
        googleMap.getUiSettings().setCompassEnabled(false);
        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);

    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(TripSummaryDetail1.this);
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

    private void Alert1(String title, String message) {
        final PkDialog mDialog = new PkDialog(TripSummaryDetail1.this);
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
                Intent intent = new Intent(TripSummaryDetail1.this, LoadingPage.class);
                intent.putExtra("Driverid", driver_id);
                intent.putExtra("RideId", Str_rideId);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();

            }
        });
        mDialog.show();
    }

    //-----------------------Change Post Request-----------------
    private void postRequest_tripdetail(String Url) {
        dialog = new Dialog(TripSummaryDetail1.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------dashboard----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);
        jsonParams.put("ride_id", Str_rideId);

        System.out.println("driver_id--------------" + driver_id);
        System.out.println("ride_id--------------" + Str_rideId);

        mRequest = new ServiceRequest(TripSummaryDetail1.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------tripdetail----------------" + response);
                String Sstatus = "", Str_ridestatus = "", Str_rideid = "", Smessage = "", Str_coupon_code = "", Str_cancel = "", Str_wallet_usage = "", Str_cabtype = "", Str_drop_date = "",
                        trip_paid_status = "", Str_continu_ride = "";
                Currency currencycode = null;
                String Str_tipStatus = "", Str_tipAmount = "";

                try {
                    JSONObject object = new JSONObject(response);

                    Sstatus = object.getString("status");
                    System.out.println("status-----------" + Sstatus);

                    JSONObject jobject = object.getJSONObject("response");
                    if (jobject.length() > 0) {
                        JSONObject jsonObject = jobject.getJSONObject("details");

                        if (jsonObject.length() > 0) {
                            Str_currency = jsonObject.getString("currency");
                            //   currencycode = Currency.getInstance(getLocale(Str_currency));

                            sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Str_currency);

                            Str_ridestatus = jsonObject.getString("ride_status");
                            Str_rideid = jsonObject.getString("ride_id");
                            disp_ride_status = jsonObject.getString("disp_status");
                            disp_pay_status = jsonObject.getString("disp_pay_status");
                            trip_paid_status = jsonObject.getString("pay_status");
                            Str_cancel = jsonObject.getString("do_cancel_action");
                            Str_continue_ridedetail = jsonObject.getString("continue_ride");
                            str_recievecash = jobject.getString("receive_cash");
                            str_req_payment = jobject.getString("req_payment");
                            sUserID = jsonObject.getString("user_id");
                            sTripType = jsonObject.getString("trip_type");
                            sInvoiceSrc = jsonObject.getString("invoice_src");


                            System.out.println("sUserID------------" + sUserID);

                        }

                        JSONObject jobject2 = jsonObject.getJSONObject("pickup");

                        if (jobject2.length() > 0) {
                            Str_loctionaddress = jobject2.getString("location");

                            JSONObject jobject3 = jobject2.getJSONObject("latlong");
                            Str_lattitude = jobject3.getString("lat");
                            Str_logitude = jobject3.getString("lon");

                            System.out.println("lat---------" + Str_lattitude);
                            System.out.println("lon---------" + Str_logitude);

                            strlat = Double.parseDouble(Str_lattitude);
                            strlon = Double.parseDouble(Str_logitude);


                            isPickUpAvailable = true;
                        } else {
                            isPickUpAvailable = false;
                        }


                        JSONObject jobject_drop = jsonObject.getJSONObject("drop");

                        if (jobject_drop.length() > 0) {

                            Str_drop = jobject_drop.getString("location");

                            JSONObject jobject_drop1 = jobject2.getJSONObject("latlong");
                            Str_droplattitude = jobject_drop1.getString("lon");
                            Str_droplongitude = jobject_drop1.getString("lat");

                            System.out.println("drop-------------" + Str_drop);

                        }

                        Str_pickup_date = jsonObject.getString("pickup_date");

                        if (Str_ridestatus.equalsIgnoreCase("Completed") || Str_ridestatus.equalsIgnoreCase("Finished")) {

                            JSONObject jobject4 = jsonObject.getJSONObject("summary");
                            if (jobject4.length() > 0) {
                                Str_ride_distance = jobject4.getString("ride_distance");
                                Str_time_taken = jobject4.getString("ride_duration");
                                Str_wait_time = jobject4.getString("waiting_duration");
                            }
                            Str_drop_date = jsonObject.getString("drop_date");
                            JSONObject jobject5 = jsonObject.getJSONObject("fare");
                            if (jobject5.length() > 0) {
                                Str_totalbill = sCurrencySymbol + jobject5.getString("grand_bill");
                                Str_totalpaid = sCurrencySymbol + jobject5.getString("total_paid");
                                Str_coupon_code = jobject5.getString("coupon_discount");
                                Str_wallet_usage = sCurrencySymbol + jobject5.getString("wallet_usage");
                            }


                            JSONObject tips_object = jsonObject.getJSONObject("tips");
                            if (tips_object.length() > 0) {
                                Str_tipStatus = sCurrencySymbol + tips_object.getString("tips_status");
                                Str_tipAmount = sCurrencySymbol + tips_object.getString("tips_amount");
                            }

                        }


                        String data = jobject.getString("user_profile");
                        Object json = new JSONTokener(data).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jobject_profile = new JSONObject(data);
                            Str_useremail = jobject_profile.getString("user_email");
                            Str_Username = jobject_profile.getString("user_name");
                            Str_phoneno = jobject_profile.getString("phone_number");
                            Str_userimg = jobject_profile.getString("user_image");
                            Str_userrating = jobject_profile.getString("user_review");
                            Str_rideid = jobject_profile.getString("ride_id");
                            Str_pickuplocation = jobject_profile.getString("pickup_location");
                            Str_pickup_lat = jobject_profile.getString("pickup_lat");
                            Str_pickup_long = jobject_profile.getString("pickup_lon");
                            Str_pickup_time = jobject_profile.getString("pickup_time");

                            Str_drop_location = jobject_profile.getString("drop_loc");
                            Str_drop_lat = jobject_profile.getString("drop_lat");
                            Str_drop_lon = jobject_profile.getString("drop_lon");

                            System.out.println("phone-----------------" + Str_phoneno);
                            System.out.println("pickup_long-----------------" + Str_pickup_long);
                            System.out.println("pickup_lat-----------------" + Str_pickup_lat);
                            System.out.println("username-----------------" + Str_Username);

                        } else if (json instanceof JSONArray) {
                        }
                        Str_continu_ride = jsonObject.getString("continue_ride");
                        Str_distance_unit = jsonObject.getString("distance_unit");

                    }

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Tv_tripdetail_address.setText(Str_loctionaddress);
                        Tv_tripdetail_pickup.setText(Str_pickup_date);
                        //  Tv_tripdetail_drop.setText(Str_drop);
                        Tv_tripdetail_total_paid.setText(Str_totalbill);
                        Tv_tripdetail_total_amount_paid.setText(Str_totalpaid);
                        Tv_tripdetail_rideId.setText(Str_rideid);
                        Tv_trip_status.setText(getResources().getString(R.string.tripsummery_add_Ride_label) + " " + disp_ride_status);
                        Tv_trip_paid_status.setText(getResources().getString(R.string.tripsummery_add_Payment_label) + " " + disp_pay_status);

                        /*Tv_tripdetail_ride_distance.setText(Str_ride_distance+" "+getResources().getString(R.string.tripsummery_add_km_label));
                        Tv_tripdetail_timetaken.setText(Str_time_taken+" "+getResources().getString(R.string.tripsummery_add_mins_label));
                        Tv_tripdetail_waitingtime.setText(Str_wait_time+" "+getResources().getString(R.string.tripsummery_add_mins_label));*/
//                        Tv_tripdetail_ride_distance.setText(Str_ride_distance+" "+getResources().getString(R.string.tripsummery_add_km_label));
                        Tv_tripdetail_ride_distance.setText(Str_ride_distance + " " + Str_distance_unit);
                        Tv_tripdetail_timetaken.setText(Str_time_taken);



                        Display display = getWindowManager().getDefaultDisplay();
                        int width = display.getWidth();

                        System.out.println("---------------screen width-----------------"+width);


                        if (600 <= width && 800 >= width){
                            Iv_routeMap.getLayoutParams().height = 380;
                        }else if (801 <= width && 1500 >= width){
                            Iv_routeMap.getLayoutParams().height = 500  ;
                        }else if (1501 <= width){
                            Iv_routeMap.getLayoutParams().height = 640;
                        }


                        if (isPickUpAvailable) {
                            if (!"".equalsIgnoreCase(sInvoiceSrc) && sInvoiceSrc != null) {
                                Rl_mapView.setVisibility(View.GONE);
                                Iv_routeMap.setVisibility(View.VISIBLE);
                                Picasso.with(TripSummaryDetail1.this).load(sInvoiceSrc).placeholder(R.drawable.nouserimg).into(Iv_routeMap);
                            } else {
                                Iv_routeMap.setVisibility(View.GONE);
                                Rl_mapView.setVisibility(View.VISIBLE);

                                //-------------------code for set marker-------------------------
                                googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng((strlat), (strlon)))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker)));
                                // Move the camera to last position with a zoom level
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng((strlat), (strlon))).zoom(17).build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                            }
                        }




                        if ("Share".equalsIgnoreCase(sTripType)){
                            Rl_wait_Time.setVisibility(View.GONE);

                        }else {
                            Rl_wait_Time.setVisibility(View.VISIBLE);
                            Tv_tripdetail_waitingtime.setText(Str_wait_time);
                        }


                        //----------------------code for ride details---------------------
                        if (Str_ridestatus.equalsIgnoreCase("Completed") || Str_ridestatus.equalsIgnoreCase("Finished")) {
                            layout_address_and_loction_details.setVisibility(View.VISIBLE);
                            layout_completed_details.setVisibility(View.VISIBLE);
                            Rl_layout_amount_status.setVisibility(View.VISIBLE);
                            Rl_layout_Tripststus.setVisibility(View.VISIBLE);
                            Rl_layout_pickup_details.setVisibility(View.VISIBLE);
                            Rl_drop_loc.setVisibility(View.VISIBLE);
                            Rl_drop_time.setVisibility(View.VISIBLE);

                            //  Rl_layout_drop_details.setVisibility(View.VISIBLE);

                            Tv_tripdetail_drop.setText(Str_drop);
                            TV_drop_time.setText(Str_drop_date);


                            //--------------code for discount and wallet usage------------
                            if (Str_wallet_usage.length() > 0) {
                                Tv_wallet_uage.setVisibility(View.VISIBLE);
                                Tv_wallet_uage.setText(getResources().getString(R.string.cabily_wallet_used_label) + ": " + Str_wallet_usage);
                            } else {
                                Tv_wallet_uage.setVisibility(View.GONE);
                            }

                            if (Str_coupon_code.length() > 0) {
                                Tv_coupon_discount.setVisibility(View.VISIBLE);
                                Tv_coupon_discount.setVisibility(View.GONE);
                                Tv_coupon_discount.setText("Coupon discount used" + ": " + Str_coupon_code);
                            }

                            if (Str_tipStatus.equalsIgnoreCase("0")) {
                                Tv_driverTip.setVisibility(View.GONE);
                            } else {
                                Tv_driverTip.setVisibility(View.VISIBLE);
                                Tv_driverTip.setText(getResources().getString(R.string.cabily_driver_tip_amount) + " " + Str_tipAmount);
                            }

                        } else {
                            Rl_drop_loc.setVisibility(View.INVISIBLE);
                            Rl_drop_time.setVisibility(View.INVISIBLE);
                            Rl_layout_amount_status.setVisibility(View.GONE);
                            // Rl_layout_drop_details.setVisibility(View.GONE);
                            Rl_layout_Tripststus.setVisibility(View.VISIBLE);
                            layout_address_and_loction_details.setVisibility(View.VISIBLE);
                            Rl_layout_pickup_details.setVisibility(View.VISIBLE);
                        }

                        //-----------------code to visible Request payment button---------------

                        if (Str_ridestatus.equalsIgnoreCase("Finished")) {

                            System.out.println("ridestatus--------------" + Str_ridestatus);

                            Bt_RequestPayment.setVisibility(View.VISIBLE);
                        } else {
                            Bt_RequestPayment.setVisibility(View.GONE);
                        }

                        if (Str_ridestatus.equalsIgnoreCase("Onride")) {
                            Rl_drop_loc.setVisibility(View.VISIBLE);
                            Tv_tripdetail_drop.setText(Str_drop);
                        }


                        //------------code for continue ride---------------
                        if (Str_continue_ridedetail.equalsIgnoreCase("arrived")) {
                            Bt_Continue_Ride.setVisibility(View.VISIBLE);
                            Bt_Cancel_ride.setVisibility(View.VISIBLE);
                        } else if (Str_continue_ridedetail.equalsIgnoreCase("begin")) {
                            Bt_Continue_Ride.setVisibility(View.VISIBLE);
                            Bt_Cancel_ride.setVisibility(View.VISIBLE);
                        } else if (Str_continue_ridedetail.equalsIgnoreCase("end")) {
                            Bt_Continue_Ride.setVisibility(View.VISIBLE);
                            Bt_Cancel_ride.setVisibility(View.GONE);
                        } else {
                            Bt_Continue_Ride.setVisibility(View.GONE);
                            // Rl_layout_drop_details.setVisibility(View.GONE);
                        }

                        //---------code for cancel ride----------
                        if (Str_cancel.equalsIgnoreCase("1")) {
                            Bt_Cancel_ride.setVisibility(View.VISIBLE);
                        } else {
                            Bt_Cancel_ride.setVisibility(View.GONE);
                        }

                    } else {
                        Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.fetchdatatoast));
                        // Toast.makeText(TripSummaryDetail.this,getResources().getString(R.string.fetchdatatoast),Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
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


/*

            private void postRequest_tripdetail1(String Url){
           System.out.println("post---------------------");

        dialog = new Dialog(TripSummaryDetail.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title=(TextView)dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------tripdetail----------------" + Url);

        postrequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("-------------tripdetail----------------" + response);
                        String Sstatus = "",Scurrency_code="", Str_ridestatus="",Str_rideid="",Smessage = "",Str_totalbill="",Str_coupon_code="",Str_cancel="",Str_wallet_usage="",Str_cabtype="",Str_drop_date="",
                                trip_paid_status="",Str_continu_ride="";
                        Currency currencycode = null;
                        String Str_tipStatus="",Str_tipAmount="";

                        try {
                            JSONObject object = new JSONObject(response);

                            Sstatus = object.getString("status");
                            System.out.println("status-----------"+Sstatus);

                            JSONObject  jobject = object.getJSONObject("response");
                            if (jobject.length()>0)
                            {
                                JSONObject jsonObject = jobject.getJSONObject("details");

                                if (jsonObject.length()>0)
                                {
                                    Str_currency = jsonObject.getString("currency");
                                 //   currencycode = Currency.getInstance(getLocale(Str_currency));

                                    sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Str_currency);

                                    Str_ridestatus = jsonObject.getString("ride_status");
                                    Str_rideid = jsonObject.getString("ride_id");
                                    trip_paid_status = jsonObject.getString("pay_status");
                                    Str_cancel = jsonObject.getString("do_cancel_action");
                                    Str_continue_ridedetail = jsonObject.getString("continue_ride");

                                }

                                JSONObject jobject2 = jsonObject.getJSONObject("pickup");

                                if (jobject2.length()>0)
                                {
                                    Str_loctionaddress = jobject2.getString("location");

                                    JSONObject jobject3 = jobject2.getJSONObject("latlong");
                                    Str_lattitude  = jobject3.getString("lat");
                                    Str_logitude = jobject3.getString("lon");

                                    System.out.println("lat---------"+Str_lattitude);
                                    System.out.println("lon---------"+Str_logitude);

                                    strlat = Double.parseDouble(Str_lattitude);
                                    strlon = Double.parseDouble(Str_logitude);

                                    //-------------------code for set marker-------------------------
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng((strlat),(strlon)))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                                    // Move the camera to last position with a zoom level
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng((strlat), (strlon))).zoom(12).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                }


                               */
/* JSONObject jobject_drop = jsonObject.getJSONObject("drop");

                                if (jobject_drop.length()>0){

                                    Str_drop =jobject_drop.getString("location");

                                    JSONObject jobject_drop1 = jobject2.getJSONObject("latlong");
                                    Str_droplattitude = jobject_drop1.getString("lon");
                                    Str_droplongitude = jobject_drop1.getString("lat");

                                    System.out.println("drop-------------"+Str_drop);

                                }*//*


                                Str_pickup_date = jsonObject.getString("pickup_date");

                                if (Str_ridestatus.equalsIgnoreCase("Completed")||Str_ridestatus.equalsIgnoreCase("Finished"))
                                {

                                    JSONObject jobject4 = jsonObject.getJSONObject("summary");
                                    if (jobject4.length()>0)
                                    {
                                        Str_ride_distance = jobject4.getString("ride_distance");
                                        Str_time_taken = jobject4.getString("ride_duration");
                                        Str_wait_time = jobject4.getString("waiting_duration");
                                    }

                                    JSONObject jobject5 = jsonObject.getJSONObject("fare");
                                    if (jobject5.length()>0)
                                    {
                                        Str_totalbill = sCurrencySymbol+jobject5.getString("grand_bill");
                                        Str_totalpaid =  sCurrencySymbol+jobject5.getString("total_paid");
                                        Str_coupon_code = jobject5.getString("coupon_discount");
                                        Str_wallet_usage = jobject5.getString("wallet_usage");
                                    }


                                    JSONObject tips_object = jsonObject.getJSONObject("tips");
                                    if (tips_object.length()>0)
                                    {
                                        Str_tipStatus = sCurrencySymbol+tips_object.getString("tips_status");
                                        Str_tipAmount =  sCurrencySymbol+tips_object.getString("tips_amount");
                                    }

                                }


                                String  data = jobject.getString("user_profile");
                                Object json = new JSONTokener(data).nextValue();
                                if (json instanceof JSONObject){
                                    JSONObject jobject_profile = new JSONObject(data);
                                    Str_useremail =  jobject_profile.getString("user_email");
                                    Str_Username = jobject_profile.getString("user_name");
                                    Str_phoneno = jobject_profile.getString("phone_number");
                                    Str_userimg = jobject_profile.getString("user_image");
                                    Str_userrating = jobject_profile.getString("user_review");
                                    Str_rideid = jobject_profile.getString("ride_id");
                                    Str_pickuplocation = jobject_profile.getString("pickup_location");
                                    Str_pickup_lat = jobject_profile.getString("pickup_lat");
                                    Str_pickup_long = jobject_profile.getString("pickup_lon");
                                    Str_pickup_time = jobject_profile.getString("pickup_time");
                                    System.out.println("phone-----------------"+Str_phoneno);
                                    System.out.println("pickup_long-----------------"+Str_pickup_long);
                                    System.out.println("pickup_lat-----------------"+Str_pickup_lat);
                                    System.out.println("username-----------------"+Str_Username);

                                } else if(json instanceof JSONArray){
                                }
                                Str_continu_ride  =jsonObject.getString("continue_ride");
                            }

                            if (Sstatus.equalsIgnoreCase("1"))
                            {
                                Tv_tripdetail_address.setText(Str_loctionaddress);
                                Tv_tripdetail_pickup.setText(Str_pickup_date);
                               //  Tv_tripdetail_drop.setText(Str_drop);
                                Tv_tripdetail_total_paid.setText(Str_totalbill);
                                Tv_tripdetail_total_amount_paid.setText(Str_totalpaid);
                                Tv_tripdetail_rideId.setText(Str_rideid);
                                Tv_trip_status.setText(getResources().getString(R.string.tripsummery_add_Ride_label)+" "+Str_ridestatus);
                                Tv_trip_paid_status.setText(getResources().getString(R.string.tripsummery_add_Payment_label)+" "+trip_paid_status);

                                Tv_tripdetail_ride_distance.setText(Str_ride_distance+" "+getResources().getString(R.string.tripsummery_add_km_label));
                                Tv_tripdetail_timetaken.setText(Str_time_taken+" "+getResources().getString(R.string.tripsummery_add_mins_label));
                                Tv_tripdetail_waitingtime.setText(Str_wait_time+" "+getResources().getString(R.string.tripsummery_add_mins_label));

                                //----------------------code for ride details---------------------
                                if (Str_ridestatus.equalsIgnoreCase("Completed")||Str_ridestatus.equalsIgnoreCase("Finished"))
                                {
                                    layout_address_and_loction_details.setVisibility(View.VISIBLE);
                                    layout_completed_details.setVisibility(View.VISIBLE);
                                    Rl_layout_amount_status.setVisibility(View.VISIBLE);
                                    Rl_layout_Tripststus.setVisibility(View.VISIBLE);
                                    Rl_layout_pickup_details.setVisibility(View.VISIBLE);
                                  //  Rl_layout_drop_details.setVisibility(View.VISIBLE);

                                    //--------------code for discount and wallet usage------------
                                    if (Str_wallet_usage.length()>0){
                                        Tv_wallet_uage.setVisibility(View.VISIBLE);
                                        Tv_wallet_uage.setVisibility(View.GONE);
                                        Tv_wallet_uage.setText(getResources().getString(R.string.cabily_wallet_used_label)+Str_wallet_usage);
                                    }else{
                                        Tv_wallet_uage.setVisibility(View.GONE);
                                    }

                                    if (Str_coupon_code.length()>0){
                                        Tv_coupon_discount.setVisibility(View.VISIBLE);
                                        Tv_coupon_discount.setVisibility(View.GONE);
                                        Tv_coupon_discount.setText("Coupon discount used" + Str_coupon_code);
                                    }

                                    if(Str_tipStatus.equalsIgnoreCase("0"))
                                    {
                                        Tv_driverTip.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        Tv_driverTip.setVisibility(View.VISIBLE);
                                        Tv_driverTip.setText(getResources().getString(R.string.cabily_driver_tip_amount)+" "+Str_tipAmount);
                                    }

                                }else{
                                    Rl_layout_amount_status.setVisibility(View.GONE);
                                   // Rl_layout_drop_details.setVisibility(View.GONE);
                                    Rl_layout_Tripststus.setVisibility(View.VISIBLE);
                                    layout_address_and_loction_details.setVisibility(View.VISIBLE);
                                    Rl_layout_pickup_details.setVisibility(View.VISIBLE);
                                }

                                //-----------------code to visible Request payment button---------------

                                if (Str_ridestatus.equalsIgnoreCase("Finished")){

                                    System.out.println("ridestatus--------------"+Str_ridestatus);

                                    Bt_RequestPayment.setVisibility(View.VISIBLE);
                                }else{
                                    Bt_RequestPayment.setVisibility(View.GONE);
                                }


                                //------------code for continue ride---------------
                                if (Str_continue_ridedetail.equalsIgnoreCase("arrived")){
                                    Bt_Continue_Ride.setVisibility(View.VISIBLE);
                                    Bt_Cancel_ride.setVisibility(View.VISIBLE);
                                }else if(Str_continue_ridedetail.equalsIgnoreCase("begin")){
                                    Bt_Continue_Ride.setVisibility(View.VISIBLE);
                                    Bt_Cancel_ride.setVisibility(View.VISIBLE);
                                }else if(Str_continue_ridedetail.equalsIgnoreCase("end")){
                                    Bt_Continue_Ride.setVisibility(View.VISIBLE);
                                    Bt_Cancel_ride.setVisibility(View.GONE);
                                }else{
                                    Bt_Continue_Ride.setVisibility(View.GONE);
                                   // Rl_layout_drop_details.setVisibility(View.GONE);
                                }

                                //---------code for cancel ride----------
                                if (Str_cancel.equalsIgnoreCase("1")){
                                    Bt_Cancel_ride.setVisibility(View.VISIBLE);
                                }else {
                                    Bt_Cancel_ride.setVisibility(View.GONE);
                                }

                            }
                            else{
                                Alert(getResources().getString(R.string.alert_sorry_label_title),getResources().getString(R.string.fetchdatatoast));
                               // Toast.makeText(TripSummaryDetail.this,getResources().getString(R.string.fetchdatatoast),Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                VolleyErrorResponse.VolleyError(TripSummaryDetail.this, error);
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

                jsonParams.put("driver_id",driver_id);
                jsonParams.put("ride_id",Str_rideId);

                System.out.println("driver_id--------------" + driver_id);
                System.out.println("ride_id--------------" +Str_rideId);

                return jsonParams;
            }
        };
        AppController.getInstance().addToRequestQueue(postrequest);
    }

*/


    //-----------------------Code for post request-----------------
    private void postRequest_Reqqustpayment_TripDetail(String Url) {
        dialog = new Dialog(TripSummaryDetail1.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------trip----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);
        jsonParams.put("ride_id", Str_rideId);

        System.out
                .println("--------------driver_id-------------------"
                        + driver_id);

        System.out
                .println("--------------ride_id-------------------"
                        + Str_rideId);


        mRequest = new ServiceRequest(TripSummaryDetail1.this);
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
                    Alert1(getResources().getString(R.string.label_pushnotification_cashreceived), Str_response);

                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }

        });

    }


/*
            private void postRequest_Reqqustpayment_TripDetail1(String Url) {
        dialog = new Dialog(TripSummaryDetail.this);
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
                VolleyErrorResponse.VolleyError(TripSummaryDetail.this, error);
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
                jsonParams.put("ride_id", Str_rideId);

                System.out
                        .println("--------------driver_id-------------------"
                                + driver_id);

                System.out
                        .println("--------------ride_id-------------------"
                                + Str_rideId);


                return jsonParams;
            }
        };
        postrequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        postrequest.setShouldCache(false);

        AppController.getInstance().addToRequestQueue(postrequest);
    }




*/


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


}
