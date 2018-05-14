package com.cabily.cabilydriver.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.app.xmpp.XmppService;

import java.util.HashMap;
import java.util.List;


public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private String checkLifeCycleStatus = "foreground";

    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
        if (!isAppIsInBackground(activity)) {
            if (checkLifeCycleStatus.equalsIgnoreCase("foreground")) {
                available(activity);
                checkLifeCycleStatus="background";
            }
        }
    }

    public void onActivitySaveInstanceState(Activity activity,Bundle outState) {
    }

    public void onActivityStarted(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
        if (isAppIsInBackground(activity)) {
            if (checkLifeCycleStatus.equalsIgnoreCase("background")) {
                unAvailable(activity);
                checkLifeCycleStatus="foreground";
            }
        }
    }



    private void available(Activity activity) {
        SessionManager sessionManager = new SessionManager(activity);
        sessionManager.setAppStatus("resume");

        AppOpenCheck_Session appSession = new AppOpenCheck_Session(activity);
        appSession.setAppOpenStatus("open");

        HashMap<String, String> state = sessionManager.getXmppServiceState();
        String sState = state.get(SessionManager.KEY_XMPP_SERVICE_RESTART_STATE);


        if (sState.equalsIgnoreCase("online")) {
            if (!isMyServiceRunning(XmppService.class, activity)) {
                activity.startService(new Intent(activity, XmppService.class));
            }
            else {
                activity.stopService(new Intent(activity, XmppService.class));
                activity.startService(new Intent(activity, XmppService.class));
            }

        }

        ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(activity, "available");
        chatAvailability.postChatRequest();

        if (!isMyServiceRunning(IdentifyAppKilled.class, activity)) {
            activity.startService(new Intent(activity, IdentifyAppKilled.class));
        }



    }


    private void unAvailable(Activity activity) {
        SessionManager sessionManager = new SessionManager(activity);
        sessionManager.setAppStatus("pause");

        AppOpenCheck_Session appSession = new AppOpenCheck_Session(activity);
        appSession.setAppOpenStatus("close");

        ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(activity, "unavailable");
        chatAvailability.postChatRequest();
    }


    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if(runningProcesses!=null) {
                if(runningProcesses.size()>0) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
                }
            }

        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
    private boolean isMyServiceRunning(Class<?> serviceClass, Activity activity) {
        boolean b = false;
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                b = true;
                break;
            } else {
                b = false;
            }
        }
        return b;
    }

}
