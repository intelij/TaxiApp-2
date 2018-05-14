package com.Hockeyapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.app.service.ServiceConstant;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;


/**
 * Created by Prem Kumar and Anitha on 11/12/2015.
 */
public class ActivityHockeyApp extends Activity
{

    private static  String APP_ID = ServiceConstant.ACTION_ACTION_HOCKYAPPID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        checkForUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void checkForCrashes() {
        CrashManager.register(this, APP_ID);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this, APP_ID);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
        // unregister other managers if necessary...
    }
}
