package com.cabily.cabilydriver.Utils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.app.xmpp.XmppService;
import com.cabily.cabilydriver.Helper.GEODBHelper;

import java.util.HashMap;

/**
 * Created by user88 on 1/6/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private SessionManager session;
    private GEODBHelper myDBHelper;
    public static boolean firstTime = true;
    private String sState = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isOnline(context)) {

            session = new SessionManager(context);
            myDBHelper = new GEODBHelper(context);

            String status = myDBHelper.retriveStatus();
            System.out.println("---------prabu session.isLoggedIn-------"+session.isLoggedIn());
            if (firstTime) {
                firstTime = false;
                if (session.isLoggedIn()) {
                    System.out.println("---------prabu retriveStatus-------" + status);
                    if (("1".equalsIgnoreCase(status)) || ("3".equalsIgnoreCase(status))) {

                        if (!isMyServiceRunning(context, XmppService.class)) {
                            context.startService(new Intent(context, XmppService.class));
                            System.out.println("---------prabu Xmpp connected-------");
                        } else {
                            context.stopService(new Intent(context, XmppService.class));
                            context.startService(new Intent(context, XmppService.class));
                            System.out.println("---------prabu Xmpp connected-------");
                        }

                        session.setXmppServiceState("online");
                    }else{
                        session = new SessionManager(context);

                        HashMap<String, String> state = session.getAppStatus();
                        sState = state.get(SessionManager.KEY_APP_STATUS);

                        if (sState.equalsIgnoreCase("resume")|| sState.equalsIgnoreCase("pause")) {
                            if (!isMyServiceRunning(context, XmppService.class)) {
                                context.startService(new Intent(context, XmppService.class));
                                System.out.println("---------prabu Xmpp connected-------");
                            } else {
                                context.stopService(new Intent(context, XmppService.class));
                                context.startService(new Intent(context, XmppService.class));
                                System.out.println("---------prabu Xmpp connected-------");
                            }
                            session.setXmppServiceState("online");
                        }
                    }
                }

            }


        } else {

            firstTime = true;
        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }

    public boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
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