package com.cabily.cabilydriver.adapter;

        import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.location.Location;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceManager;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.DriverAlertActivity;
import com.cabily.cabilydriver.DriverMapActivity;
import com.cabily.cabilydriver.Helper.GEODBHelper;
        import com.cabily.cabilydriver.NavigationDrawerNew;
        import com.cabily.cabilydriver.R;
import com.cabily.cabilydriver.TripPage;
import com.cabily.cabilydriver.TripSummaryDetail;
import com.cabily.cabilydriver.Utils.GPSTracker;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.widgets.PkDialog;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by Administrator on 11/23/2015.
 */
public class ContinuousRequestAdapter {
    public String currentVersion = "";
    private SessionManager sm;
    private LayoutInflater mInflater;
    private Activity context;
    private LinearLayout listview;
    public int req_count;
    private CircleProgressView mCircleView;
    private CountDownTimer timer;
    private String KEY1 = "key1";
    private String KEY2 = "key2";
    private String KEY3 = "key3";
    private String rider_id = "";
    private Dialog dialog;
    private int seconds = 0;
    private MediaPlayer mediaPlayer;
    private Location myLocation;
    private DriverAlertActivity.TimerCompletCallback timerCompletCallback;
    private Handler mHandler = new Handler();
    public static String userID;
    private GPSTracker gps;
    private GEODBHelper myDBHelper;
    public CircularHandler ch;
    public  int cur_count;
    private ServiceRequest mRequest_update;
    private float totalDistanceTravelled;
    ViewHolder holder1;
    Boolean clicked=false;

    ArrayList<CircularHandler> arrobj=new ArrayList<CircularHandler>();
    public class ViewHolder {
        public  int count;
        public Button accept;
        public  Button decline;
        public  CircleProgressView circularProgressBar;
        public  TextView cabily_alert_address;
        private  LinearLayout Ll_ride_Requst_layout;
        public  JSONObject data;


    }

    public ContinuousRequestAdapter(Activity context, Location myLocation,LinearLayout listview) {
        this.context = context;
        sm = new SessionManager(context);
        mInflater = LayoutInflater.from(context);
        this.listview = listview;
        this.myLocation=myLocation;
        myDBHelper = new GEODBHelper(context);
        gps = new GPSTracker(context);
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {

            System.out.println("======gps enabled===========");

            this.myLocation=gps.getLocation();
            //  addData(getIntent());
        }
        else
        {
            gps.showSettingsAlert();
            //  enableGpsService();
        }

    }

    public void setTimerCompleteCallBack(DriverAlertActivity.TimerCompletCallback callBack){
        this.timerCompletCallback = callBack;
    }

    public View getView(int i, JSONObject data) {
        View view;
        ViewHolder holder;
        String data1 = " ";
        // JSONObject data = (JSONObject) getItem(i);
        view = mInflater.inflate(R.layout.driver_alert, null, false);
        holder = new ViewHolder();
        holder.data = data;
        holder.count = i;
        holder.accept = (Button) view.findViewById(R.id.cabily_driver_alert_accept_btn);
        holder.decline = (Button) view.findViewById(R.id.cabily_driver_alert_reject_btn);
        holder.cabily_alert_address = (TextView) view.findViewById(R.id.cabily_alert_address);
        holder.circularProgressBar = (CircleProgressView) view.findViewById(R.id.timer_circleView);
        holder.Ll_ride_Requst_layout  = (LinearLayout)view.findViewById(R.id.Ll_ride_request_layout);
        view.setTag(holder);
        HashMap<String, Integer> user = sm.getRequestCount();
        req_count = user.get(SessionManager.KEY_COUNT);
        // System.out.println("---------------req_count top-------------------"+req_count);
        System.out.println("---------------JSONO DATA-gf-------------------"+holder.data);
        holder.accept.setTag(holder);
        holder.decline.setTag(holder);
        holder.accept.setOnClickListener(acceptBtnlistener);
        holder.decline.setOnClickListener(new DeclineBtnListener(i));
        holder.cabily_alert_address.setText("" + getDataForPosition(i, KEY3,data));
        holder.circularProgressBar.setEnabled(false);
        String position = getDataForPosition(i, KEY3,data);
        holder.circularProgressBar.setFocusable(false);
        holder.circularProgressBar.setMaxValue(Integer.parseInt(getDataForPosition(i, KEY2,data)));
        holder.circularProgressBar.setValueAnimated(0);
        holder.circularProgressBar.setTextSize(30);
        holder.circularProgressBar.setAutoTextSize(true);
        holder.circularProgressBar.setTextScale(0.6f);
        holder.circularProgressBar.setTextColor(context.getResources().getColor(R.color.progress_ripplecolor));
        //      mHandler.post(new CircularHandler(holder,Integer.parseInt(getDataForPosition(i, KEY2,data))));
        ch=new CircularHandler(holder,Integer.parseInt(getDataForPosition(i, KEY2,data)),i);
        mHandler.post(ch);
        arrobj.add(ch);

        String  rideid = getDataForPosition(i, KEY1,holder.data);
        System.out.println("acknowledge ride jai  id "+rideid);

        PostRequest(ServiceConstant.acknowledge_ride,rideid);



        return view;
    }

    private class CircularHandler implements Runnable {
        ViewHolder holder;
        Integer value ;
        int pos;
        boolean isRunning;

        public CircularHandler(ViewHolder holder,Integer val,int i) {
            this.holder = holder;
            isRunning = true;
            value=val;
            pos=i;
        }

        @Override
        public void run() {
            if (isRunning) {
                value = value - 1;

                holder.circularProgressBar.setText(String.valueOf(Math.abs(value)));
                holder.circularProgressBar.setTextMode(TextMode.TEXT);
                holder.circularProgressBar.setValueAnimated(value, 500);
                mHandler.postDelayed(this, 1000);
                System.out.println("counter-----jai--------------"+value);
                if (value == 0) {
                    mHandler.removeCallbacks(this);
                    if (timerCompletCallback != null){
                        clicked=false;
                        holder.Ll_ride_Requst_layout.setVisibility(View.GONE);

                        System.out.println("requestcount2 above-----jai-------------"+req_count);

                        String  rideId = getDataForPosition(pos, KEY1,holder.data);
                        System.out.println("timeout ride jai  id "+rideId);
                        NavigationDrawerNew.sPushType = false;
                        PostRequest(ServiceConstant.delete_ride,rideId);



                        HashMap<String, Integer> user = sm.getRequestCount();
                        req_count = user.get(SessionManager.KEY_COUNT);
                        System.out.println("requestcount2 above----jai--------------"+req_count);


                        SessionManager session=new SessionManager(context);



                        if(req_count>0)
                        {
                            req_count=req_count-1;
                            session.setRequestCount(req_count);
                        }
                        else {

                        }
                        System.out.println("requestcount2 below---------jai---------"+req_count);

                        if(req_count==0)
                        {

                            System.out.println("activity  finished--------jai----------"+req_count);
                            NavigationDrawerNew.sPushType = false;
                            sm.setRequestCount(0);
                            context.finish();
                            stopPlayer();
                            context.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        }
                    }
                    isRunning = false;
                }
            }
        }
    }

    private String getDataForPosition(int i, String key,JSONObject data) {
        try {
            return (String) data.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private View.OnClickListener acceptBtnlistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NavigationDrawerNew.sPushType = false;

            if(!clicked) {
                clicked=true;


           /* for(int i=1;i<=arrobj.size();i++)
            {
                CircularHandler ch2 =arrobj.get(i-1);
                if(ch2!=null)
                {
                    mHandler.removeCallbacks(ch2);
                    System.out.println("jai----cancel");
                }
            }
            sm.setRequestCount(0);*/

                ViewHolder holder = (ViewHolder) view.getTag();
                HashMap<String, String> jsonParams = new HashMap<String, String>();
                HashMap<String, String> userDetails = sm.getUserDetails();
                String driverId = userDetails.get(SessionManager.KEY_DRIVERID);
                rider_id = getDataForPosition(holder.count, KEY1, holder.data);
                cur_count = holder.count;
                holder1 = holder;
                myDBHelper.insertRide_id(rider_id);
                //String address = getDataForPosition(i,KEY3);
                jsonParams.put("ride_id", "" + rider_id);
                jsonParams.put("driver_id", "" + driverId);
                jsonParams.put("driver_lat", "" + myLocation.getLatitude());
                jsonParams.put("driver_lon", "" + myLocation.getLongitude());
                jsonParams.put("version",currentVersion);

//                System.out.println("rideid---------" + rider_id);
//                System.out.println("driver_id---------" + driverId);
//                System.out.println("driver_lat-----gps----" + myLocation.getLatitude());
//                System.out.println("driver_lon----gps-----" + myLocation.getLongitude());


                ArrayList<LatLng> distance_travelled = myDBHelper.getDataDistance("1");
                calculateDistance(distance_travelled);
                jsonParams.put("distance", String.valueOf(String.format("%.2f", (totalDistanceTravelled / 1000))));

                System.out.println("=============total=================" + String.valueOf(totalDistanceTravelled / 1000));
                System.out.println("-----------jai---total_distance-------------------" + distance_travelled.toString().replace("[", "").replace("]", "").replace(" ", ""));

                myDBHelper.Delete("");


                if (DriverMapActivity.myLocation != null) {
                    jsonParams.put("driver_lat", "" + DriverMapActivity.myLocation.getLatitude());
                    jsonParams.put("driver_lon", "" + DriverMapActivity.myLocation.getLongitude());
//                    System.out.println("rideid---------" + rider_id);
//                    System.out.println("driver_id---------" + driverId);
//                    System.out.println("driver_lat---------" + DriverMapActivity.myLocation.getLatitude());
//                    System.out.println("driver_lon---------" + DriverMapActivity.myLocation.getLongitude());
                }

                System.out.println("acceptrideurl-jsonParams-----------" + jsonParams);
                ServiceManager manager = new ServiceManager(context, acceptServicelistener);
                manager.makeServiceRequest(ServiceConstant.ACCEPTING_RIDE_REQUEST, Request.Method.POST, jsonParams);
                System.out.println("acceptrideurl-yu-----------" + ServiceConstant.ACCEPTING_RIDE_REQUEST);
                showDialog();
            }
            else
            {
                System.out.println("-------------secont click");
            }
        }
    };

    ServiceManager.ServiceListener acceptServicelistener = new ServiceManager.ServiceListener() {
        @Override
        public void onCompleteListener(Object object) {
            dismissDialog();
            String Sstatus = "",SResponse="", Str_Username = "",Str_UserId="", Str_User_email = "", Str_Userphoneno = "", Str_Userrating = "", Str_userimg = "", Str_pickuplocation = "", Str_pickuplat = "", Str_pickup_long = "",
                    Str_pickup_time = "", Str_message = "",Str_droplat="",Str_droplon="",str_drop_location="",Zero_res="";

            if (object instanceof String) {
                String jsonString = (String) object;
                System.out.println("Responseaccept---------" + jsonString);

                Log.e("accept",jsonString);

                try {
                    JSONObject object1 = new JSONObject(jsonString);
                    Sstatus = object1.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")){
                        System.out.println("status-----------" + Sstatus);
                        JSONObject jobject = object1.getJSONObject("response");
                        JSONObject jobject2 = jobject.getJSONObject("user_profile");
                        Str_message = jobject.getString("message");
                        Str_Username = jobject2.getString("user_name");

                        //Str_UserId = jobject2.getString("user_id");
                        userID = jobject2.getString("user_id");
                        myDBHelper.insertuser_id(userID);
                        Str_User_email = jobject2.getString("user_email");
                        Str_Userphoneno = jobject2.getString("phone_number");
                        Str_Userrating = jobject2.getString("user_review");
                        Str_pickuplocation = jobject2.getString("pickup_location");
                        Str_pickuplat = jobject2.getString("pickup_lat");
                        Str_pickup_long = jobject2.getString("pickup_lon");
                        Str_pickup_time = jobject2.getString("pickup_time");
                        Str_userimg = jobject2.getString("user_image");
                        Str_droplat = jobject2.getString("drop_lat");
                        Str_droplon = jobject2.getString("drop_lon");
                        str_drop_location = jobject2.getString("drop_loc");
                        // System.out.println("userid-------------"+Str_UserId);

                    }
                    if (Sstatus.equalsIgnoreCase("0")){
                        Zero_res=object1.getString("ride_view");
                        SResponse = object1.getString("response");
                    }

                    if (Sstatus.equalsIgnoreCase("1")) {


                        for(int i=1;i<=arrobj.size();i++)
                        {
                            CircularHandler ch2 =arrobj.get(i-1);
                            if(ch2!=null)
                            {
                                mHandler.removeCallbacks(ch2);
                                System.out.println("jai----cancel");
                            }
                        }
                        sm.setRequestCount(0);
                        stopPlayer();

                        Intent broadcastIntent_trip = new Intent();
                        broadcastIntent_trip.setAction("com.finish.tripPage");
                        context.sendBroadcast(broadcastIntent_trip);



                        Intent trip_intent = new Intent(context, TripPage.class);
                        context.startActivity(trip_intent);
                        context.finish();



                        /*Intent intent = new Intent(context, ArrivedTrip.class);
                        intent.putExtra("address", Str_pickuplocation);
                        intent.putExtra("rideId", rider_id);
                        intent.putExtra("pickuplat", Str_pickuplat);
                        intent.putExtra("pickup_long", Str_pickup_long);
                        intent.putExtra("username", Str_Username);
                        intent.putExtra("userrating", Str_Userrating);
                        intent.putExtra("phoneno", Str_Userphoneno);
                        intent.putExtra("userimg", Str_userimg);
                        intent.putExtra("UserId",userID);
                        intent.putExtra("drop_lat",Str_droplat);
                        intent.putExtra("drop_lon",Str_droplon);
                        intent.putExtra("pickup_time",Str_pickup_time);
                        intent.putExtra("drop_location",str_drop_location);

                        context.startActivity(intent);
                        context.finish();*/
                    } else {

                        if (Sstatus.equalsIgnoreCase("0")){

                            if(Zero_res.equals("stay"))
                            {
                                clicked=false;
                                Alert(context.getString(R.string.alert_sorry_label_title), SResponse);
                            }
                            if(Zero_res.equals("next"))
                            {

                                for(int i=1;i<=arrobj.size();i++)
                                {
                                    CircularHandler ch2 =arrobj.get(i-1);
                                    if(ch2!=null)
                                    {
                                        mHandler.removeCallbacks(ch2);
                                        System.out.println("jai----cancel");
                                    }
                                }
                                sm.setRequestCount(0);
                                stopPlayer();

                                Intent broadcastIntent_trip = new Intent();
                                broadcastIntent_trip.setAction("com.finish.tripPage");
                                context.sendBroadcast(broadcastIntent_trip);

                                Intent trip_intent = new Intent(context, TripPage.class);
                                trip_intent.putExtra("interrupted","No");
                                context.startActivity(trip_intent);
                                context.finish();

                              /*  Intent intent = new Intent(context, ArrivedTrip.class);
                                intent.putExtra("address", Str_pickuplocation);
                                intent.putExtra("rideId", rider_id);
                                intent.putExtra("pickuplat", Str_pickuplat);
                                intent.putExtra("pickup_long", Str_pickup_long);
                                intent.putExtra("username", Str_Username);
                                intent.putExtra("userrating", Str_Userrating);
                                intent.putExtra("phoneno", Str_Userphoneno);
                                intent.putExtra("userimg", Str_userimg);
                                intent.putExtra("UserId",userID);
                                intent.putExtra("drop_lat",Str_droplat);
                                intent.putExtra("drop_lon",Str_droplon);
                                intent.putExtra("pickup_time",Str_pickup_time);
                                intent.putExtra("drop_location",str_drop_location);

                                context.startActivity(intent);
                                context.finish();*/

                            }
                            if(Zero_res.equals("detail"))
                            {
                                Intent intent = new Intent(context, TripSummaryDetail.class);
                                intent.putExtra("ride_id", rider_id);
                                context.startActivity(intent);
                                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                            if(Zero_res.equals("home"))
                            {
                                System.out.println("cur_count---jai------" + cur_count);

                                //   Toast.makeText(context,SResponse,Toast.LENGTH_LONG).show();
                                holder1.count = holder1.count-1;

                                HashMap<String, Integer> user = sm.getRequestCount();
                                int req_count = user.get(SessionManager.KEY_COUNT);
                                req_count=req_count-1;

                                System.out.println("----------inside declineBtnListener req_count-----jai-----------"+req_count);

                                sm.setRequestCount(req_count);
                                holder1.Ll_ride_Requst_layout.setVisibility(View.GONE);
                                CircularHandler ch1 =arrobj.get(cur_count-1);
                                if(ch1!=null)
                                {
                                    mHandler.removeCallbacks(ch1);
                                }

                                if(req_count==0)
                                {

                                    if (DriverAlertActivity.mediaPlayer != null && DriverAlertActivity.mediaPlayer.isPlaying()) {
                                        DriverAlertActivity.mediaPlayer.stop();
                                    }

                                    AlertHome(context.getString(R.string.alert_sorry_label_title), SResponse);
                                    mHandler.removeCallbacks(null);
                                    sm.setRequestCount(0);

                                }
                                else
                                {
                                    clicked=false;
                                    Alert(context.getString(R.string.alert_sorry_label_title), SResponse);
                                }



                            }

                        }

                       /* SResponse = object1.getString("response");
                        Alert(context.getString(R.string.alert_sorry_label_title), SResponse);
                        final PkDialog mdialog = new PkDialog(context);
                        mdialog.setDialogTitle(context.getString(R.string.alert_sorry_label_title));
                        mdialog.setDialogMessage(SResponse);
                        mdialog.setPositiveButton(context.getString(R.string.alert_label_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mdialog.dismiss();

                                        //--------remove list after
                                        listview.removeViewAt(0);
                                        context.finish();


                                    }
                                }
                        );
                        mdialog.show();*/
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                }


            }
        }

        @Override
        public void onErrorListener(Object error) {
            dismissDialog();
            context.finish();
        }
    };

    public void showDialog() {
        dialog = new Dialog(context);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void dismissDialog() {
        try {
            if (dialog != null)
                dialog.dismiss();
        } catch (Exception e) {
        }
    }


    private void stopPlayer() {
        try {
            if (DriverAlertActivity.mediaPlayer != null && DriverAlertActivity.mediaPlayer.isPlaying()) {
                DriverAlertActivity.mediaPlayer.stop();
            }
        } catch (Exception e) {
        }
    }



    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(context);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(context.getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
    private void AlertHome(String title, String message) {
        final PkDialog mDialog = new PkDialog(context);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(context.getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();


                sm.setRequestCount(0);

                context.finish();
                if (DriverAlertActivity.mediaPlayer != null && DriverAlertActivity.mediaPlayer.isPlaying()) {
                    DriverAlertActivity.mediaPlayer.stop();
                }

            }
        });
        mDialog.show();
    }



    public class DeclineBtnListener implements View.OnClickListener
    {
        int mPosition;
        DeclineBtnListener(int position)
        {
            mPosition=position;
        }
        @Override
        public void onClick(View v) {
            NavigationDrawerNew.sPushType = false;

            ViewHolder holder = (ViewHolder) v.getTag();

            String  ride_id = getDataForPosition(mPosition, KEY1,holder.data);

            System.out.println("ride_id-----delete-----------jai------------"+ride_id);
            PostRequest(ServiceConstant.delete_ride,ride_id);

            try {

                if (timerCompletCallback != null){
                    holder.count = holder.count-1;

                    HashMap<String, Integer> user = sm.getRequestCount();
                    int req_count = user.get(SessionManager.KEY_COUNT);
                    req_count=req_count-1;

                    System.out.println("----------inside declineBtnListener req_count-----jai-----------"+req_count);

                    sm.setRequestCount(req_count);
                    holder.Ll_ride_Requst_layout.setVisibility(View.GONE);
                    CircularHandler ch1 =arrobj.get(mPosition-1);
                    if(ch1!=null)
                    {
                        mHandler.removeCallbacks(ch1);
                    }

                    if(req_count==0)
                    {
                        mHandler.removeCallbacks(null);
                        sm.setRequestCount(0);
                        context.finish();
                        if (DriverAlertActivity.mediaPlayer != null && DriverAlertActivity.mediaPlayer.isPlaying()) {
                            DriverAlertActivity.mediaPlayer.stop();
                        }
                    }
                    //req_count=req_count-1;
                    // sm.setRequestCount(req_count);
                    // timerCompletCallback.timerCompleteCallBack(holder);
                }
            } catch (Exception e) {
            }
        }
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

    private void PostRequest(String Url,String ride_id) {


        System.out.println("acknowledge or delete Url"+Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        HashMap<String, String> userDetails = sm.getUserDetails();
        String driverId = userDetails.get("driverid");


        jsonParams.put("driver_id", "" + driverId);
        jsonParams.put("ride_id", "" + ride_id);


        System.out.println("acknowledge or delete paramerter"+jsonParams);


        mRequest_update = new ServiceRequest(context);
        mRequest_update.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {


                System.out.println("acknowledge or delete response"+response);


                String Str_status = "",
                        Str_availablestaus = "",
                        Str_message = "";

                try {
                    JSONObject jobject = new JSONObject(response);


                } catch (Exception e) {
                }
            }

            @Override
            public void onErrorListener() {
            }

        });

    }



}
