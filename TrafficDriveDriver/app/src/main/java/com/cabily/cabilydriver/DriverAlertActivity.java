package com.cabily.cabilydriver;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.Hockeyapp.ActionBarActivityHockeyApp;
import com.cabily.cabilydriver.Utils.GPSTracker;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.adapter.ContinuousRequestAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import at.grabner.circleprogress.CircleProgressView;


public class DriverAlertActivity extends ActionBarActivityHockeyApp implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private SessionManager sessionManager;
    private static ArrayList<JSONObject> details = new ArrayList<>();
    private CircleProgressView mCircleView;
    private CountDownTimer timer;
    public static String EXTRA = "EXTRA";
    private int seconds = 0;
    public static MediaPlayer mediaPlayer;
    public JSONObject dataObject;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static Location myLocation;

    private LinearLayout listView;
    private ContinuousRequestAdapter continuousRequestAdapter;
    private int count = 0;
    private GPSTracker gps;
    private PendingResult<LocationSettingsResult> result;
    private final static int REQUEST_LOCATION = 199;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_alert);
        listView = (LinearLayout) findViewById(R.id.linearList);
        initView();
    }

    void initView() {
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, getResources().getString(R.string.drivernotlogged_label_title), Toast.LENGTH_SHORT).show();
        }
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        //alertAdapter = new DriverAlertAdapter(sessionManager, this, myLocation);
        continuousRequestAdapter = new ContinuousRequestAdapter(this, myLocation, listView);
        continuousRequestAdapter.setTimerCompleteCallBack(timerCompletCallback);
        setLocationRequest();
        buildGoogleApiClient();
        gps = new GPSTracker(DriverAlertActivity.this);
        if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {

            System.out.println("======gps enabled===========");

            myLocation=gps.getLocation();

            addData(getIntent());


        }
        else
        {
            gps.showSettingsAlert();
            enableGpsService();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {

            System.out.println("======gps enabled===========");

            myLocation=gps.getLocation();
            addData(intent);
        }
        else
        {
            gps.showSettingsAlert();
            enableGpsService();
        }
    }


    private TimerCompletCallback timerCompletCallback = new TimerCompletCallback() {
        @Override
        public void timerCompleteCallBack(ContinuousRequestAdapter.ViewHolder holder) {
            try {

                HashMap<String, Integer> user = sessionManager.getRequestCount();
                int req_count = user.get(SessionManager.KEY_COUNT);
                req_count = req_count - 1;
                sessionManager.setRequestCount(req_count);

                System.out.println("------- req_count inside decline--------------" + req_count);

                if (req_count == 0) {
                    sessionManager.setRequestCount(0);
                    finish();
                    if (DriverAlertActivity.mediaPlayer != null && DriverAlertActivity.mediaPlayer.isPlaying()) {
                        DriverAlertActivity.mediaPlayer.stop();
                    }
                }

                listView.removeViewAt(holder.count);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public interface TimerCompletCallback {
        void timerCompleteCallBack(ContinuousRequestAdapter.ViewHolder holder);
    }

    private void addData(Intent intent) {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);

            }
        }

        if (intent != null) {

            HashMap<String, Integer> user = sessionManager.getRequestCount();
            int req_count = user.get(SessionManager.KEY_COUNT);
            req_count = req_count + 1;
            sessionManager.setRequestCount(req_count);

            System.out.println("=-----------------start req_count---------------" + req_count);


            Bundle extra = intent.getExtras();
            if (extra != null) {
                String data = (String) extra.get(EXTRA);

                System.out.println("-----------------JSONO DATA------------" + data);

                try {
                    if (data != null) {
                        String decodeData = URLDecoder.decode(data, "UTF-8");
                        System.out.println("decode"+decodeData);
                        count = count + 1;
                        JSONObject dataObject = new JSONObject(decodeData);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        listView.addView(continuousRequestAdapter.getView(count, dataObject), params);
                    }
                    else
                    {
                        System.out.println("data"+data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        System.out.println("onconnect-------------------");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        this.myLocation=location;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(myLocation==null)
        {
            myLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public class UTF8ToAscii {
        public void convert(String arg) throws Exception {
            BufferedReader r = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(arg),
                            "UTF-8"
                    )
            );
            String line = r.readLine();
            while (line != null) {
                System.out.println(unicodeEscape(line));
                line = r.readLine();
            }
            r.close();
        }

        private final char[] hexChar = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        private String unicodeEscape(String s) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if ((c >> 7) > 0) {
                    sb.append("\\u");
                    sb.append(hexChar[(c >> 12) & 0xF]); // append the hex character for the left-most 4-bits
                    sb.append(hexChar[(c >> 8) & 0xF]);  // hex for the second group of 4-bits from the left
                    sb.append(hexChar[(c >> 4) & 0xF]);  // hex for the third group
                    sb.append(hexChar[c & 0xF]);         // hex for the last group, e.g., the right most 4-bits
                } else {
                    sb.append(c);
                       }
            }
            return sb.toString();
        }
    }

    @Override
    protected void onDestroy() {

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }


        super.onDestroy();

    }
    private void enableGpsService() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(DriverAlertActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        myLocation=gps.getLocation();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        enableGpsService();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }
}
