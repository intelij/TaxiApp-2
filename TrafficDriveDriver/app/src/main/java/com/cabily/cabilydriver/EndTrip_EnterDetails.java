package com.cabily.cabilydriver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.EmojiExcludeFilter;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.widgets.PkDialog;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;


/**
 * Created by user88 on 11/17/2015.
 */
public class EndTrip_EnterDetails extends FragmentActivity {
    private static final String TAG ="swipe" ;
    private String driver_id = "";
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private  Dialog dialog;
    private EditText Et_hours, Et_minuts, Et_distance, Et_drop_location, Et_drop_time;
    private Button Bt_shimmer_endtrip;
    float initialX, initialY;
    private ServiceRequest mRequest;
    private  String Str_status = "",Str_pickup_time="",Str_response="",Str_ridefare="",Str_timetaken="",Str_waitingtime="",Str_need_payment="",Str_currency="",Str_ride_distance="",str_recievecash="";
    private StringRequest postrequest;
    private String Str_rideid = "";
    private RelativeLayout Rl_layout_back;
    private int googlerequestcode = 100;
    private   String Slattitude="";
    private String Slongitude="";

    private String mins;
    private String secs;
    private String hours;
    private String wait_time="";

    // private SimpleDateFormat mFormatter = new SimpleDateFormat("MMM/dd,hh:mm aa");
    private SimpleDateFormat mFormatter = new SimpleDateFormat("ddMMM,yyyy hh:mm aa");

    private SimpleDateFormat coupon_mFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");

    private SimpleDateFormat coupon_time_mFormatter = new SimpleDateFormat("hh:mm aa");
    private SimpleDateFormat mTime_Formatter = new SimpleDateFormat("HH");
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_trip_detail);
        initialize();

        //Code for broadcat receive
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.finish.endtripenterdetail");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.finish.endtripenterdetail")) {
                    finish();
                }
            }
        };
        registerReceiver(receiver, filter);


        Rl_layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        Bt_shimmer_endtrip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getX();
                        initialY = event.getY();

                        Log.d(TAG, "Action was DOWN");
                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "Action was MOVE");
                        break;

                    case MotionEvent.ACTION_UP:
                        float finalX = event.getX();
                        float finalY = event.getY();

                        Log.d(TAG, "Action was UP");

                        if (initialX < finalX) {
                            mins = Et_minuts.getText().toString();
                            hours = Et_hours.getText().toString();

                            System.out.println("hour----------------"+hours);
                            System.out.println("mins----------------"+mins);

                            wait_time = ("" + hours + ":"
                                    +  mins) + ":"
                                    + 000;

                            System.out.println("waittime----------------" + wait_time);
                            cd = new ConnectionDetector(getApplicationContext());
                            isInternetPresent = cd.isConnectingToInternet();
                            if (isInternetPresent) {
                                postRequest_EnterTripdetails(ServiceConstant.endtrip_url);
                                System.out.println("enterdetails------------------" + ServiceConstant.endtrip_url);
                            } else {
                                Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                            }
                            Log.d(TAG, "Left to Right swipe performed");
                        }

                        if (initialX > finalX) {
                            Log.d(TAG, "Right to Left swipe performed");
                        }

                        if (initialY < finalY) {
                            Log.d(TAG, "Up to Down swipe performed");
                        }

                        if (initialY > finalY) {
                            Log.d(TAG, "Down to Up swipe performed");
                        }

                        break;

                    case MotionEvent.ACTION_CANCEL:
                        Log.d(TAG,"Action was CANCEL");
                        break;

                    case MotionEvent.ACTION_OUTSIDE:
                        Log.d(TAG, "Movement occurred outside bounds of current screen element");
                        break;
                }
                return true;


            }
        });


        Et_drop_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndTrip_EnterDetails.this, GooglePlaceSearch.class);
                startActivityForResult(intent, googlerequestcode);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        Et_drop_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();

            }
        });

    }

    //----------------DatePicker Listener------------
    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 1);
            Date d = cal.getTime();
            String currenttime = mTime_Formatter.format(d);
            String selecedtime = mTime_Formatter.format(date);
            String displaytime = coupon_mFormatter.format(date);

            Toast.makeText(getApplicationContext(),coupon_mFormatter.format(date), Toast.LENGTH_SHORT).show();

            System.out.println("-----------------current date---------------------" + currenttime);
            System.out.println("-----------------selected date---------------------" + selecedtime);

            if (selecedtime.equalsIgnoreCase("00")) {
                selecedtime = "24";
            }

            if (Integer.parseInt(currenttime) >= Integer.parseInt(selecedtime))
            {
                Et_drop_time.setText(displaytime);

                System.out.println("droptime-----------"+displaytime);

            } else {
                Alert(getApplicationContext().getResources().getString(R.string.alert_label_trip_title), getApplicationContext().getResources().getString(R.string.alert_label_trip_content));
            }
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.end_trip_enter_deatis_toast_cancel), Toast.LENGTH_SHORT).show();
        }
    };



    private void initialize() {
        session = new SessionManager(EndTrip_EnterDetails.this);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);

        Intent i = getIntent();
        Str_rideid = i.getStringExtra("rideid");
        Str_pickup_time = i.getStringExtra("pickuptime");


        System.out.println("pickuptime------------------"+Str_pickup_time);

        Et_hours = (EditText) findViewById(R.id.arrived_tripdetail_waittime_Et_hours);
        Et_minuts = (EditText) findViewById(R.id.arrived_tripdetail_waittime_Et_mins);
        Et_distance = (EditText) findViewById(R.id.arrived_ridedistance_Et);
        Et_drop_location = (EditText) findViewById(R.id.arrived_tripdetail_droplocation_Et);
        Et_drop_time = (EditText) findViewById(R.id.arrived_tripdetail_droptime_Et);
        Rl_layout_back = (RelativeLayout) findViewById(R.id.layout_arrivd_detail_back);
        Bt_shimmer_endtrip = (Button)findViewById(R.id.btn_endtrip);


        Et_hours.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_minuts.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_distance.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_drop_location.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_drop_time.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == googlerequestcode) {
            try{
                String Saddress = data.getStringExtra("address");
                Slattitude  = data.getStringExtra("Lattitude");
                Slongitude  = data.getStringExtra("Longitude");
                System.out.println("msggoogleplace-------------" + Saddress);
                Et_drop_location.setText(Saddress);
            }catch (Exception e){
            }
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(EndTrip_EnterDetails.this);
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

    //-------------------Show Summery fare  Method--------------------
    private void showfaresummerydetails() {

        final MaterialDialog dialog = new MaterialDialog(EndTrip_EnterDetails.this);
        View view = LayoutInflater.from(EndTrip_EnterDetails.this).inflate(R.layout.fare_summery_alert_dialog, null);
        final TextView Tv_reqest = (TextView)view.findViewById(R.id.requst);
        TextView tv_fare_totalamount = (TextView) view.findViewById(R.id.fare_summery_total_amount);
        TextView tv_ridedistance = (TextView) view.findViewById(R.id.fare_summery_ride_distance_value);
        TextView tv_timetaken = (TextView) view.findViewById(R.id.fare_summery_ride_timetaken_value);
        TextView tv_waittime = (TextView) view.findViewById(R.id.fare_summery_wait_time_value);
        RelativeLayout layout_request_payment = (RelativeLayout)view.findViewById(R.id.layout_faresummery_requstpayment);
        RelativeLayout layout_receive_cash = (RelativeLayout)view.findViewById(R.id.fare_summery_receive_cash_layout);
        tv_fare_totalamount.setText(Str_ridefare);
        tv_ridedistance.setText(Str_ride_distance);
        tv_timetaken.setText(Str_timetaken);
        tv_waittime.setText(Str_waitingtime);
        dialog.setView(view).show();


        //if (Str_need_payment.equalsIgnoreCase("YES")){

        layout_receive_cash.setVisibility(View.VISIBLE);
        layout_request_payment.setVisibility(View.VISIBLE);
        Tv_reqest.setText(EndTrip_EnterDetails.this.getResources().getString(R.string.lbel_fare_summery_requestpayment));

        //}else{
        //  layout_receive_cash.setVisibility(View.GONE);
        // Tv_reqest.setText(EndTrip.this.getResources().getString(R.string.alert_label_ok));

        //}

        layout_receive_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndTrip_EnterDetails.this, OtpPage.class);
                intent.putExtra("rideid", Str_rideid);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        layout_request_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(EndTrip_EnterDetails.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {

                    if (Tv_reqest.getText().toString().equalsIgnoreCase(EndTrip_EnterDetails.this.getResources().getString(R.string.lbel_fare_summery_requestpayment))) {
                        postRequest_Reqqustpayment(ServiceConstant.request_paymnet_url);
                        System.out.println("arrived------------------" + ServiceConstant.request_paymnet_url);
                    } else {
                        Intent intent = new Intent(EndTrip_EnterDetails.this, RatingsPage.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                } else {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });

    }
    private void showfaresummerydetails1() {

        final MaterialDialog dialog = new MaterialDialog(EndTrip_EnterDetails.this);
        View view = LayoutInflater.from(EndTrip_EnterDetails.this).inflate(R.layout.fare_summery_alert_dialog, null);
        final TextView Tv_reqest = (TextView)view.findViewById(R.id.requst);
        TextView tv_fare_totalamount = (TextView) view.findViewById(R.id.fare_summery_total_amount);
        TextView tv_ridedistance = (TextView) view.findViewById(R.id.fare_summery_ride_distance_value);
        TextView tv_timetaken = (TextView) view.findViewById(R.id.fare_summery_ride_timetaken_value);
        TextView tv_waittime = (TextView) view.findViewById(R.id.fare_summery_wait_time_value);
        RelativeLayout layout_request_payment = (RelativeLayout)view.findViewById(R.id.layout_faresummery_requstpayment);
        RelativeLayout layout_receive_cash = (RelativeLayout)view.findViewById(R.id.fare_summery_receive_cash_layout);
        tv_fare_totalamount.setText(Str_ridefare);
        tv_ridedistance.setText(Str_ride_distance);
        tv_timetaken.setText(Str_timetaken);
        tv_waittime.setText(Str_waitingtime);
        dialog.setView(view).show();


        // if (Str_need_payment.equalsIgnoreCase("YES")){

        layout_receive_cash.setVisibility(View.GONE);
        layout_request_payment.setVisibility(View.VISIBLE);
        Tv_reqest.setText(EndTrip_EnterDetails.this.getResources().getString(R.string.lbel_fare_summery_requestpayment));

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
                cd = new ConnectionDetector(EndTrip_EnterDetails.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {

                    if (Tv_reqest.getText().toString().equalsIgnoreCase(EndTrip_EnterDetails.this.getResources().getString(R.string.lbel_fare_summery_requestpayment))) {
                        postRequest_Reqqustpayment(ServiceConstant.request_paymnet_url);
                        System.out.println("arrived------------------" + ServiceConstant.request_paymnet_url);
                    } else {
                        Intent intent = new Intent(EndTrip_EnterDetails.this, RatingsPage.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                } else {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });

    }


    private void showfaresummerydetails2() {

        final MaterialDialog dialog = new MaterialDialog(EndTrip_EnterDetails.this);
        View view = LayoutInflater.from(EndTrip_EnterDetails.this).inflate(R.layout.fare_summery_alert_dialog, null);
        final TextView Tv_reqest = (TextView)view.findViewById(R.id.requst);
        TextView tv_fare_totalamount = (TextView) view.findViewById(R.id.fare_summery_total_amount);
        TextView tv_ridedistance = (TextView) view.findViewById(R.id.fare_summery_ride_distance_value);
        TextView tv_timetaken = (TextView) view.findViewById(R.id.fare_summery_ride_timetaken_value);
        TextView tv_waittime = (TextView) view.findViewById(R.id.fare_summery_wait_time_value);
        RelativeLayout layout_request_payment = (RelativeLayout)view.findViewById(R.id.layout_faresummery_requstpayment);
        RelativeLayout layout_receive_cash = (RelativeLayout)view.findViewById(R.id.fare_summery_receive_cash_layout);
        tv_fare_totalamount.setText(Str_ridefare);
        tv_ridedistance.setText(Str_ride_distance);
        tv_timetaken.setText(Str_timetaken);
        tv_waittime.setText(Str_waitingtime);
        dialog.setView(view).show();


        // if (Str_need_payment.equalsIgnoreCase("YES")){

        layout_receive_cash.setVisibility(View.GONE);
        layout_request_payment.setVisibility(View.VISIBLE);
        Tv_reqest.setText(EndTrip_EnterDetails.this.getResources().getString(R.string.lbel_notification_ok));

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
                cd = new ConnectionDetector(EndTrip_EnterDetails.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {


                    Intent intent = new Intent(EndTrip_EnterDetails.this, RatingsPage.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                } else {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });

    }


    //-----------------------Post Request Enter Trip Details Details-----------------
    private void postRequest_EnterTripdetails(String Url) {
        dialog = new Dialog(EndTrip_EnterDetails.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------endtripenter----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id",driver_id);
        jsonParams.put("ride_id",Str_rideid);
        jsonParams.put("drop_lat",Slattitude);
        jsonParams.put("drop_lon",Slongitude);
        jsonParams.put("interrupted","YES");
        jsonParams.put("drop_loc",Et_drop_location.getText().toString());
        jsonParams.put("distance",Et_distance.getText().toString());
        jsonParams.put("wait_time",wait_time);
        jsonParams.put("drop_time",Et_drop_time.getText().toString());

        System.out.println("driver_id----------" + driver_id);

        System.out.println("drop_loc----------" + Et_drop_location.getText().toString());

        System.out.println("distance----------" + Et_distance.getText().toString());

        System.out.println("drop_time----------" + Et_drop_time.getText().toString());

        System.out.println("wait_time----------" +wait_time);

        System.out.println("drop_lat----------" +Slattitude);

        System.out.println("drop_lon----------" +Slongitude);

        System.out.println("interrupted----------" +"YES");

        System.out.println("ride_id----------" +Str_rideid);


        mRequest = new ServiceRequest(EndTrip_EnterDetails.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                Log.e("endtripresponse",response);

                System.out.println("response-----------------" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    System.out.println("status--------------"+Str_status);

                    if (Str_status.equalsIgnoreCase("1")){
                        JSONObject jsonObject= object.getJSONObject("response");
                        JSONObject jobject = jsonObject.getJSONObject("fare_details");

                        Str_need_payment = jsonObject.getString("need_payment");
                        str_recievecash = jsonObject.getString("receive_cash");

                        Str_currency = jobject.getString("currency");

                        Currency currencycode = Currency.getInstance(getLocale(Str_currency));
                        Str_ridefare = currencycode.getSymbol()+jobject.getString("ride_fare");
                        Str_timetaken = jobject.getString("ride_duration");
                        Str_waitingtime = jobject.getString("waiting_duration");
                        Str_ride_distance = jobject.getString("ride_distance");
                        Str_need_payment = jobject.getString("need_payment");
                    }else{

                        Str_response = object.getString("response");

                    }

                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();
                if (Str_status.equalsIgnoreCase("1")){

                    //  endTripHandler.removeCallbacks(endTripRunnable);


                    if (Str_need_payment.equalsIgnoreCase("YES")){
                        System.out.println("sucess------------" + Str_need_payment);
                        if(str_recievecash.matches("Enable")) {
                            showfaresummerydetails();
                        }
                        else
                        {
                            showfaresummerydetails1();
                        }

                    }else{
                        showfaresummerydetails2();
                    }

                }else {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);
                }


                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();

            }

        });

    }


/*            private void postRequest_EnterTripdetails1(String Url) {
        dialog = new Dialog(EndTrip_EnterDetails.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        System.out.println("-------------enterdetail Url----------------" + Url);
        postrequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("endtripresponse",response);
                        System.out.println("response-----------------" + response);
                    try {
                            JSONObject object = new JSONObject(response);
                            Str_status = object.getString("status");
                        System.out.println("status--------------"+Str_status);

                            if (Str_status.equalsIgnoreCase("1")){
                                JSONObject jsonObject= object.getJSONObject("response");
                                JSONObject jobject = jsonObject.getJSONObject("fare_details");

                                Str_currency = jobject.getString("currency");

                                Currency currencycode = Currency.getInstance(getLocale(Str_currency));
                                Str_ridefare = currencycode.getSymbol()+jobject.getString("ride_fare");
                                Str_timetaken = jobject.getString("ride_duration");
                                Str_waitingtime = jobject.getString("waiting_duration");
                                Str_ride_distance = jobject.getString("ride_distance");
                                Str_need_payment = jobject.getString("need_payment");
                            }else{

                                Str_response = object.getString("response");

                            }

                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        if (Str_status.equalsIgnoreCase("1")){
                            if (Str_need_payment.equalsIgnoreCase("YES")){
                                System.out.println("sucess------------"+Str_need_payment);
                                showfaresummerydetails();
                            }else{

                                Intent intent= new Intent(EndTrip_EnterDetails.this,RatingsPage.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                            }

                        }else {
                            Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);

                        }
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                VolleyErrorResponse.VolleyError(EndTrip_EnterDetails.this, error);
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
                jsonParams.put("drop_lat",Slattitude);
                jsonParams.put("drop_lon",Slongitude);
                jsonParams.put("interrupted","YES");
                jsonParams.put("drop_loc",Et_drop_location.getText().toString());
                jsonParams.put("distance",Et_distance.getText().toString());
                jsonParams.put("wait_time",wait_time);
                jsonParams.put("drop_time",Et_drop_time.getText().toString());

                System.out.println("driver_id----------" + driver_id);

                System.out.println("drop_loc----------" + Et_drop_location.getText().toString());

                System.out.println("distance----------" + Et_distance.getText().toString());

                System.out.println("drop_time----------" + Et_drop_time.getText().toString());

                System.out.println("wait_time----------" +wait_time);

                System.out.println("drop_lat----------" +Slattitude);

                System.out.println("drop_lon----------" +Slongitude);

                System.out.println("interrupted----------" +"YES");

                System.out.println("ride_id----------" +Str_rideid);


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
        dialog = new Dialog(EndTrip_EnterDetails.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------endtripenter----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);
        jsonParams.put("ride_id", Str_rideid);

        System.out
                .println("--------------driver_id-------------------"
                        + driver_id);
        System.out
                .println("--------------ride_id-------------------"
                        + Str_rideid);

        mRequest = new ServiceRequest(EndTrip_EnterDetails.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
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

            @Override
            public void onErrorListener() {

                dialog.dismiss();

            }


        });


    }

    /*        private void postRequest_Reqqustpayment1(String Url) {
        dialog = new Dialog(EndTrip_EnterDetails.this);
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
                VolleyErrorResponse.VolleyError(EndTrip_EnterDetails.this, error);
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
