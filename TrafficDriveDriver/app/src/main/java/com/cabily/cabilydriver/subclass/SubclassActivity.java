package com.cabily.cabilydriver.subclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.Hockeyapp.ActivityHockeyApp;

/**
 */
public class SubclassActivity extends ActivityHockeyApp {
    BroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.finish.ArrivedTrip");
        filter.addAction("com.finish.UserInfo");
        filter.addAction("com.finish.BeginTrip");
        filter.addAction("com.finish.EndTrip");
        filter.addAction("com.finish.OtpPage");
        filter.addAction("com.finish.PaymentPage");
        filter.addAction("com.finish.tripsummerydetail");
        filter.addAction("com.finish.endtripenterdetail");
        filter.addAction("com.finish.tripsummerylist");
        filter.addAction("com.finish.loadingpage");
        filter.addAction("com.finish.canceltrip.DriverMapActivity.finish");
        filter.addAction("com.finish.tripPage");


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.finish.ArrivedTrip")) {
                    finish();
                }else if (intent.getAction().equals("com.finish.UserInfo")){
                    finish();
                }else if (intent.getAction().equals("com.finish.BeginTrip")){
                    finish();
                }else if (intent.getAction().equals("com.finish.OtpPage")){
                    finish();
                }else if(intent.getAction().equals("com.finish.EndTrip")){
                    finish();
                }else if(intent.getAction().equals("com.finish.PaymentPage")){
                    finish();
                }else if (intent.getAction().equals("com.finish.tripsummerydetail")){
                    finish();
                }else if(intent.getAction().equals("com.finish.endtripenterdetail")){
                    finish();
                }else if (intent.getAction().equals("com.finish.tripsummerylist")){
                    finish();
                }else if(intent.getAction().equals("com.finish.loadingpage")){
                    finish();
                }
                else if(intent.getAction().equals("com.finish.canceltrip.DriverMapActivity.finish")){
                    finish();
                }
                else if(intent.getAction().equals("com.finish.tripPage")){
                    finish();
                }
            }
        };
        registerReceiver(receiver, filter);
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

}
