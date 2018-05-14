package com.cabily.cabilydriver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.subclass.SubclassActivity;
import com.cabily.cabilydriver.widgets.PkDialog;

import org.json.JSONObject;

import java.util.HashMap;



/**
 * Created by user115 on 2/18/2016.
 */
public class LoadingPage extends SubclassActivity {


    private ServiceRequest mRequest;

    private  String SdriverId="";
    private String time_out="6";
    private  String SrideId="";
   Button checkstatuss;
    private Handler mapHandler = new Handler();

    private Runnable mapRunnable = new Runnable() {
        @Override
        public void run() {

            postRequest_Reqqustpayment(ServiceConstant.check_trip_status);
            /*if (!HomePage.TIPS_TIMEOUT.isEmpty()) {
                mapHandler.postDelayed(this, Long.parseLong(HomePage.TIPS_TIMEOUT) * 1000);
            }else{
                mapHandler.postDelayed(this, 60000);
            }*/
            mapHandler.postDelayed(this, Long.parseLong(time_out)*1000);
        }
    };
    BroadcastReceiver logoutReciver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.finish.loadingpage");
        logoutReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("----------broadcost  testi ng-----------");
                mapHandler.removeCallbacks(mapRunnable);
                finish();

            }
        };
        registerReceiver(logoutReciver, filter);
        checkstatuss = (Button)findViewById(R.id.checkstatus);

        //FEB 1
                        /*if (!ChatingService.isConnected) {
                            ChatingService.startDriverAction(getActivity());
                        }*/

        Intent i = getIntent();
        SdriverId = i.getStringExtra("Driverid");
        SrideId  = i.getStringExtra("RideId");

       // System.out.println("loading reques------------------" + ServiceConstant.request_paymnet_url);
        checkstatuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                postRequest_Reqqustpayment(ServiceConstant.check_trip_status);

                Log.d("TRIP STATUS...", ServiceConstant.check_trip_status);


            }
        });
     //   mapHandler.post(mapRunnable);
        mapRunnable.run();
    }


    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(LoadingPage.this);
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

               //finish();
            }
        });
        mDialog.show();

    }





    //-----------------------Code for arrived post request-----------------
    private void postRequest_Reqqustpayment(String Url) {
       /* dialog = new Dialog(EndTrip.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);

        // dialog_title.setText(getResources().getString(R.string.action_loading));
        LinearLayout main = (LinearLayout)findViewById(R.id.main_layout);
        View view = getLayoutInflater().inflate(R.layout.waiting, null,false);
        main.addView(view);*/


        System.out.println("-------------endtrip----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id",SdriverId);
        jsonParams.put("ride_id", SrideId);

        System.out
                .println("--------------driver_id-------------------"
                        + SdriverId);


        System.out
                .println("--------------ride_id-------------------"
                        + SrideId);

        mRequest = new ServiceRequest(LoadingPage.this);
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
                    JSONObject jb =object.getJSONObject("response");
                    String trip_waiting= jb.getString("trip_waiting");
                    String rating_pending= jb.getString("ratting_pending");

                    Log.d("TRIPWAITING",trip_waiting);
                    Log.d("Pending", rating_pending);


                    if(trip_waiting.equalsIgnoreCase("Yes"))
                    {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_please_wait),Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        mapHandler.removeCallbacks(mapRunnable);
                        if(rating_pending.equalsIgnoreCase("Yes"))
                        {
                            Intent i = new Intent(LoadingPage.this,RatingsPage.class);
                            i.putExtra("rideid",SrideId);
                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            Intent i = new Intent(LoadingPage.this,HomePage.class);
                            startActivity(i);
                            finish();
                        }
                    }

                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }



              /*  if (Str_status.equalsIgnoreCase("0"))
                {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);

                }else{
                    Alert(getResources().getString(R.string.label_pushnotification_cashreceived), Str_response);
                }*/
            }
            @Override
            public void onErrorListener() {

            }

        });

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


    @Override
    protected void onStop() {
        super.onStop();
        mapHandler.removeCallbacks(mapRunnable);
    }

    @Override
    public void onDestroy() {
        mapHandler.removeCallbacks(mapRunnable);
        unregisterReceiver(logoutReciver);
        super.onDestroy();
    }




}
