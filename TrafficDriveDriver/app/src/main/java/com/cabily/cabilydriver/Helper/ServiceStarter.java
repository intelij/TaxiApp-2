package com.cabily.cabilydriver.Helper;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cabily.cabilydriver.Utils.SessionManager;

import java.util.HashMap;

/**
 * Created by user14 on 1/18/2018.
 */

public class ServiceStarter extends BroadcastReceiver {


    private String onlineState = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        SessionManager session = new SessionManager(context);
        HashMap<String, String> onlinedetails = session.getOnlineDetails();

        onlineState = onlinedetails.get(SessionManager.KEY_ONLINE);
        System.out.println("-----------prabu onlineState--------------"+onlineState);
        if ("1".equalsIgnoreCase(onlineState)) {
            if (!isMyServiceRunning(context, GEOService.class)) {
                System.out.println("-----------prabu GEOService Started--------------");
                Intent serviceIntent = new Intent(context, GEOService.class);
                context.startService(serviceIntent);
            }
        }
    }

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        boolean b = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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