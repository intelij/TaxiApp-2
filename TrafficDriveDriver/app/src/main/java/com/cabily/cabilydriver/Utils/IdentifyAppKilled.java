package com.cabily.cabilydriver.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.app.xmpp.XmppService;
import com.cabily.cabilydriver.Helper.GEODBHelper;
import com.cabily.cabilydriver.Helper.GEOService;

import java.util.Timer;


public class IdentifyAppKilled extends Service {

    private static final String TAG = "UEService";
    private Timer timer;
    private static final int delay = 1000; // delay for 1 sec before first start
    private static final int period = 10000;
    private GEODBHelper myDBHelper;
    AppOpenCheck_Session appSession;
    SessionManager sessionManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        appSession = new AppOpenCheck_Session(IdentifyAppKilled.this);
        sessionManager = new SessionManager(IdentifyAppKilled.this);
        myDBHelper = new GEODBHelper(IdentifyAppKilled.this);
        // Toast.makeText(IdentifyAppKilled.this, "App Started", Toast.LENGTH_SHORT).show();
        System.out.println("-------------jai IdentifyAppKilled----------------");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (appSession != null) {
            appSession.setAppOpenStatus("close");
        }

        if (sessionManager != null) {
            sessionManager.setAppStatus("dead");
        }

        String status = myDBHelper.retriveStatus();
        try {
            if (("1".equalsIgnoreCase(status)) || ("3".equalsIgnoreCase(status))) {
                System.out.println("----------------jai------------------ONRIDE-------------------------------");
            } else if (("0".equalsIgnoreCase(status))) {
                System.out.println("----------------jai------------------ONRIDE-------------------------------");
                sessionManager.setXmppServiceState("");
                stopService(new Intent(getApplicationContext(), XmppService.class));
            } else {
                System.out.println("----------------jai------------------DESTROY SERVICE-------------------------------");
                sessionManager.setXmppServiceState("");

                stopService(new Intent(getApplicationContext(), XmppService.class));
                stopService(new Intent(getApplicationContext(), GEOService.class));
                Log.e("ClearFromRecentService", "Service Destroyed");
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void onTaskRemoved(Intent rootIntent) {
        //Code here
        stopSelf();
        Log.e("ClearFromRecentService", "END");

        if (appSession != null) {
            appSession.setAppOpenStatus("close");
        }

    }

}