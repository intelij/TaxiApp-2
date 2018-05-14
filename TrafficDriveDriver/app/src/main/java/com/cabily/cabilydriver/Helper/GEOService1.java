package com.cabily.cabilydriver.Helper;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.app.xmpp.LocalBinder;
import com.app.xmpp.XmppService;
import com.cabily.cabilydriver.Pojo.UserPojo;
import com.cabily.cabilydriver.Utils.GPSTracker;
import com.cabily.cabilydriver.Utils.LocationHandler;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class GEOService1 extends Service implements GEOCommonValues, LocationListener {

    private String TAG = GEOService1.class.getSimpleName();
    private Context myContext;
    private GEOGpsLocation myGPSLocation;
    private GEODBHelper myDBHelper;
    private SessionManager session;
    //  protected ChatConfigurationBuilder builder;
    protected String chatID;
    private JSONObject job;
    int insertDB = 0;
    int xmppCount = 0;
    private double previous_lat = 0.0;
    private double previous_lon = 0.0;
    private float total_amount;
    private long previous_time = 0;

    Handler handler;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    protected double currentLat = 0.0;
    protected double currentLong = 0.0;
    protected double PrevLat = 0.0;
    protected double PrevLong = 0.0;
    protected int periodicTime = 3000;
    static String ride_id = "";
    public Location myLocation;
    float bearingValue;
    String hostURL = "", hostName = "";
    double dis = 0.0;
    private XmppService xmppService;
    private GPSTracker gps;
    public static LocationHandler locationHandler;
    private LatLng newLatLng, oldLatLng;
    Location oldLocation;
    double myMovingDistance = 0.0;

    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            xmppService = ((LocalBinder<XmppService>) service).getService();
            //    mBounded = true;
            Log.d(TAG, "-jai---onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            xmppService = null;
            //     mBounded = false;
            Log.d(TAG, "-jai---onServiceDisconnected");
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        doBindService();

        myContext = getApplicationContext();
        try {
            this.locationHandler = new LocationHandler(myContext)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(periodicTime)
                    .setFastestInterval(periodicTime)
                    .setLocationListener(this);

//            setLocationRequest();
//            buildGoogleApiClient();
        } catch (Exception e) {
        }
//        myGPSLocation = new GEOGpsLocation(myContext);
        session = new SessionManager(myContext);
        myDBHelper = new GEODBHelper(myContext);
        gps = new GPSTracker(myContext);
        session.createServiceStatus("1");
        if (session != null && session.getUserDetails() != null) {
            hostURL = session.getXmpp().get(SessionManager.KEY_HOST_URL);
            hostName = session.getXmpp().get(SessionManager.KEY_HOST_NAME);
        }
        handler = new Handler();
        mHandlerTask.run();
        System.out.println("---------jai-----service started------");
        System.out.println("---------jai-----handler starts------");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("---------jai-----destroyed------");
        handler.removeCallbacks(mHandlerTask);
        doUnbindService();
        if (locationHandler != null) {
            locationHandler.stop();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent aIntent, int aFlags, int aStartId) {
        super.onStartCommand(aIntent, aFlags, aStartId);


        System.out.println("---------jai------in start command--------------");

        return START_STICKY;
    }

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {

            myGPSLocation = new GEOGpsLocation(myContext);
            String status = myDBHelper.retriveStatus();

            if (!"2".equalsIgnoreCase(status)) {
                Calendar aCalendar = Calendar.getInstance();
                SimpleDateFormat aDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String aCurrentDate = aDateFormat.format(aCalendar.getTime());
                String travel_history = "" + currentLat + ";" + currentLong + ";" + aCurrentDate;


                if ("0".equalsIgnoreCase(status)) {
                    insertDB = 0;
                    total_amount = 0;
                    handler.postDelayed(mHandlerTask, 10 * 1000);
                    if ((currentLat == 0.0) || (currentLong == 0.0)) {
                    } else {
                        setServiceResponse(ServiceConstant.UPDATE_CURRENT_LOCATION, "" + currentLat, "" + currentLong, "");
                        myDBHelper.insertLatLongDistance("1", currentLat, currentLong, aCurrentDate);
                    }
                } else if (("1".equalsIgnoreCase(status))) {
                    double lat = currentLat;
                    double longitude = currentLong;

                    ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();
                    userlist = myDBHelper.getUserData();
                    for (int i = 0; i < userlist.size(); i++) {

                        System.out.println("-----G-----userlist i-----" + i + " btn_group " + userlist.get(i).getBtn_group());

                        if (userlist.get(i).getBtn_group().equals("4")) {
                            long currentTime = aCalendar.getTime().getTime();
                            myDBHelper.insertLatLong(userlist.get(i).getRide_id(), lat, longitude, aCurrentDate);
                            previous_time = currentTime;
                        }

                    }
                    handler.postDelayed(mHandlerTask, 10 * 1000);
                }
            } else {
                Intent serviceIntent = new Intent(myContext, GEOService1.class);
                stopService(serviceIntent);
                handler.removeCallbacks(this);
                session.createServiceStatus("0");
            }

        }
    };

    public void sendData() {

        String status = myDBHelper.retriveStatus();
        if (("1".equalsIgnoreCase(status))) {

            ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();
            userlist = myDBHelper.getUserData();
            for (int i = 0; i < userlist.size(); i++) {
                if (userlist.get(i).getBtn_group().equals("2") || userlist.get(i).getBtn_group().equals("3") || userlist.get(i).getBtn_group().equals("4")) {
                    double lat = currentLat;
                    double longitude = currentLong;
                    chatID = userlist.get(i).getUser_id() + "@" + hostName;
                    if (chatID != null) {
                        try {
                            if (job == null) {
                                job = new JSONObject();
                            }
                            job.put("action", "driver_loc");
                            job.put("latitude", lat);
                            job.put("longitude", longitude);
                            job.put("bearing", bearingValue);
                            job.put("ride_id", userlist.get(i).getRide_id());
                            job.put("user_id", userlist.get(i).getUser_id());
                            job.put("chatID", chatID);
                            if (xmppService != null) {
                                xmppService.xmpp.chat_created = false;
                                int return_status = xmppService.xmpp.sendMessage(chatID, job.toString());
                                System.out.println("------prabu job.toString()---------"+job.toString());
                                String data = URLEncoder.encode(job.toString(), "UTF-8");
                                int return_status1 = xmppService.xmpp.sendMessageServer("trackuser" + "@" + hostName, data);
                                if (return_status == 0) {
                                    xmppCount++;
                                    if (xmppCount >= 3) {
                                        setxmppServiceResponse(ServiceConstant.XmppServerUpdate, "" + currentLat, "" + currentLong, userlist.get(i).getRide_id(), bearingValue);
                                        xmppCount = 0;
                                    }
                                } else {
                                    xmppCount = 0;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

   /* Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {

            System.out.println("-----G-----mHandlerTask");
            String status = myDBHelper.retriveStatus();
            System.out.println("-----G-----retriveStatus-----"+status);

            if (!"2".equalsIgnoreCase(status)) {
                Calendar aCalendar = Calendar.getInstance();
                SimpleDateFormat aDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String aCurrentDate = aDateFormat.format(aCalendar.getTime());
                String travel_history = "" + currentLat + ";" + currentLong + ";" + aCurrentDate;
                if ("0".equalsIgnoreCase(status)) {
                    insertDB = 0;

                    total_amount = 0;
                    handler.postDelayed(mHandlerTask, 10 * 1000);
                    if ((currentLat == 0.0) || (currentLong == 0.0)) {

                    } else {

                            Toast.makeText(myContext, "GeoService Gps Enabled LatLng :"+gps.getLatitude()+","+gps.getLongitude()+"CurLatLng:"+currentLat+"," + currentLong, Toast.LENGTH_SHORT).show();
                            System.out.println("------prabu GeoService Gps Enabled LatLng :"+gps.getLatitude()+","+gps.getLongitude()+"CurLatLng:"+currentLat+"," + currentLong);

                            setServiceResponse(ServiceConstant.UPDATE_CURRENT_LOCATION, "" + currentLat, "" + currentLong, "");
                            myDBHelper.insertLatLongDistance("1", currentLat, currentLong, aCurrentDate);
                    }

                } else if (("1".equalsIgnoreCase(status))) {


                    double lat = currentLat;
                    double longitude = currentLong;


                    ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();

                    userlist = myDBHelper.getUserData();

                    System.out.println("-----G-----userlist size-----"+userlist.size());

                    for (int i = 0; i < userlist.size(); i++) {

                        System.out.println("-----G-----userlist i-----"+i+" user_id "+userlist.get(i).getUser_id()+" ride_id "+
                                userlist.get(i).getRide_id()+" btn_group "+userlist.get(i).getBtn_group());

                        if (userlist.get(i).getBtn_group().equals("2") || userlist.get(i).getBtn_group().equals("3")) {
                            System.out.println("CurrentDate " + aCurrentDate);
                            chatID = userlist.get(i).getUser_id() + "@" + hostName;
                            System.out.println("----G------chatID------" + chatID);
                            if (chatID != null) {
                                try {
                                    if (job == null) {
                                        job = new JSONObject();
                                    }
                                    job.put("action", "driver_loc");
                                    job.put("latitude", lat);
                                    job.put("longitude", longitude);
                                    job.put("bearing", bearingValue);
                                    job.put("ride_id", userlist.get(i).getRide_id());
                                    job.put("user_id", userlist.get(i).getUser_id());
                                    job.put("chatID", chatID);


                                    if (xmppService != null) {
                                        xmppService.xmpp.chat_created = false;

                                        System.out.println("-----G-2----json data to send-----"+job);

                                        int return_status = xmppService.xmpp.sendMessage(chatID, job.toString());

                                        String data = URLEncoder.encode(job.toString(), "UTF-8");
                                        System.out.println("-----G-2----json data to send URLEncoded-----" + data);
                                        int return_status1 = xmppService.xmpp.sendMessageServer("trackuser" + "@" + hostName, data);


                                        System.out.println("xmpp success status--------jai---userList--G-2----"+i+"*****" + return_status);
                                        System.out.println("xmpp success status to server --------jai1----------" + return_status1);


                                        if (return_status == 0) {
                                            xmppCount++;

                                            System.out.println("------xmpp count----------------------jaicheck-----------" + xmppCount);

                                            if (xmppCount >= 3) {
                                                System.out.println("------prem setxmppServiceResponse--------" + currentLat + "," + currentLong);

                                                setxmppServiceResponse(ServiceConstant.XmppServerUpdate, "" + currentLat, "" + currentLong, userlist.get(i).getRide_id(), bearingValue);

                                                System.out.println("------send to server----------------jaicheck-----------------" + xmppCount);
                                                xmppCount = 0;
                                            }

                                        } else {
                                            xmppCount = 0;

                                            System.out.println("------send success------------------------jaicheck---G-2------" + return_status);
                                        }
                                    } else {

                                        System.out.println("-jai---xmpp null");

                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (userlist.get(i).getBtn_group().equals("4")) {

                            float[] result = new float[10];

                            System.out.println("----status 1---jai--------------------");

                            long currentTime = aCalendar.getTime().getTime();

                            System.out.println("-----insert----jai-------------------lat-" + lat + "longitute" + longitude + "ride_id1" + ride_id + "ride_id3" + userlist.get(i).getRide_id());

                            myDBHelper.insertLatLong(userlist.get(i).getRide_id(), lat, longitude, aCurrentDate);
                            previous_time = currentTime;

                            System.out.println("bearing-----------" + bearingValue);
                            System.out.println("------Backgournd STATUS 1 Sending XMPP LatLng---------------------------------");
                            System.out.println("CurrentDate " + aCurrentDate);

                            chatID = userlist.get(i).getUser_id() + "@" + hostName;

                            System.out.println("----G-4-----chatID--------" + chatID);

                            if (chatID != null) {
                                try {
                                    if (job == null) {
                                        job = new JSONObject();
                                    }
                                    job.put("action", "driver_loc");
                                    job.put("latitude", lat);
                                    job.put("longitude", longitude);
                                    job.put("bearing", bearingValue);
                                    job.put("ride_id", userlist.get(i).getRide_id());
                                    job.put("user_id", userlist.get(i).getUser_id());
                                    job.put("chatID", chatID);


                                    if (xmppService != null) {

                                        System.out.println("----jai job----------" + job.toString());
                                        xmppService.xmpp.chattwo_created = false;
                                        int return_status = xmppService.xmpp.sendUserTwo(chatID, job.toString());

                                        String data = URLEncoder.encode(job.toString(), "UTF-8");

                                        System.out.println("----jaiURLEncoder data---G-4-------" + data);
                                        int return_status1 = xmppService.xmpp.sendMessageServer("trackuser" + "@" + hostName, data);


                                        System.out.println("xmpp success status--------jai---G-4------" + return_status);
                                        System.out.println("xmpp success status to server --------jai1----------" + return_status1);


                                        if (return_status == 0) {
                                            xmppCount++;
                                            System.out.println("------xmpp count----------------------jaicheck-----------" + xmppCount);
                                            if (xmppCount >= 3) {
                                                System.out.println("------prem setxmppServiceResponse--------" + currentLat + "," + currentLong);
                                                setxmppServiceResponse(ServiceConstant.XmppServerUpdate, "" + currentLat, "" + currentLong, userlist.get(i).getRide_id(), bearingValue);
                                                System.out.println("------send to server----------------jaicheck-----------------" + xmppCount);
                                                xmppCount = 0;
                                            }

                                        } else {
                                            xmppCount = 0;
                                            System.out.println("------send success------------------------jaicheck---------" + return_status);
                                        }
                                    } else {
                                        System.out.println("-jai---xmpp null");

                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        }

                    }

                    handler.postDelayed(mHandlerTask, 10 * 1000);
                }
            } else {
                System.out.println("---------jai------status 2--------------");
                Intent serviceIntent = new Intent(myContext, GEOService.class);
                stopService(serviceIntent);
                handler.removeCallbacks(this);
                session.createServiceStatus("0");
            }


        }
    };*/





    public void setxmppServiceResponse(String url, String lat, String lon, String id, Float bearing) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        HashMap<String, String> user = session.getUserDetails();
        String driver_id = user.get(SessionManager.KEY_DRIVERID);
        String ride_id = id;
        jsonParams.put("ride_id", ride_id);
        jsonParams.put("lat", lat);
        jsonParams.put("lon", lon);
        jsonParams.put("bearing", String.valueOf(bearingValue));
        jsonParams.put("driver_id", driver_id);
        ServiceRequest mRequest = new ServiceRequest(GEOService1.this);
        System.out.println("------XMPP TO SERVER URL------------------------jaicheck---------" + url);
        System.out.println("------XMPP TO SERVER jsonParams------------------------jaicheck---------" + jsonParams);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                // System.out.println(response);
                try {
                    JSONObject object = new JSONObject(response);
                    // JSONObject respomse = object.getJSONObject("response");
                    String status = object.getString("status");
                    String message = object.getString("response");
                    if (status.equalsIgnoreCase("1")) {
                        System.out.println("------XMPP TO SERVER response------------------------jaicheck---------" + message);
                    }

                    // Toast.makeText(myContext, message, Toast.LENGTH_LONG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {
            }
        });

    }

    public void setServiceResponse(String url, String lat, String lon, String id) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        HashMap<String, String> user = session.getUserDetails();
        String driver_id = user.get(SessionManager.KEY_DRIVERID);
        String ride_id = id;
        jsonParams.put("ride_id", ride_id);
        jsonParams.put("latitude", lat);
        jsonParams.put("longitude", lon);
        jsonParams.put("driver_id", driver_id);

        System.out.println("------GeoService jsonParams------------------jsonParams---------" + jsonParams);

        ServiceRequest mRequest = new ServiceRequest(GEOService1.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println(response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject respomse = object.getJSONObject("response");
                    String message = (String) respomse.get("message");
                    //   Toast.makeText(myContext, message, Toast.LENGTH_LONG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {
            }
        });

    }

    private void loopHandler(Runnable run, Handler handler) {
        handler.postDelayed(run, 60000);
    }

    public void setUpNextAlarm() {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* private void setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(periodicTime);
        mLocationRequest.setFastestInterval(periodicTime);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(GEOService.this)
                .addOnConnectionFailedListener(GEOService.this)
                .build();
        mGoogleApiClient.connect();
    }

   @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
     @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }*/

  /*  @Override
    public void onLocationChanged(Location location) {

        System.out.println("--------prabu GeoSerive OnlocationChaned ------");
        if (myLocation != null) {
            bearingValue = myLocation.bearingTo(location);
        }
        this.myLocation = location;
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();





        System.out.println("---------------onlocation change----------jai------" + currentLat + "ajkdhsbjahsbsa" + currentLong);


    }*/


    @Override
    public void onLocationChanged(Location location) {


        this.myLocation = location;

        if (myLocation != null) {
            try {
                currentLat = myLocation.getLatitude();
                currentLong = myLocation.getLongitude();
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                if (oldLatLng == null) {
                    System.out.println("----------inside oldLatLngnull--------");
                    oldLatLng = latLng;
                }
                newLatLng = latLng;

                oldLocation = new Location("");
                oldLocation.setLatitude(oldLatLng.latitude);
                oldLocation.setLongitude(oldLatLng.longitude);

                bearingValue = oldLocation.bearingTo(location);
                myMovingDistance = oldLocation.distanceTo(location);

                System.out.println("movingdistacn------------" + myMovingDistance);

                if (myMovingDistance > 2) {

                    sendData();

                }
                oldLatLng = newLatLng;
                //      sendLocationToUser(myLocation);
            } catch (Exception e) {
            }


        }

    }


  /*
    public double degreeToRadians(double latLong) {
        return (Math.PI * latLong / 180.0);
    }

    public double radiansToDegree(double latLong) {
        return (latLong * 180.0 / Math.PI);
    }

    public double getBearing() {


        double fLat = degreeToRadians(lat1);
        double fLong = degreeToRadians(lng1);
        double tLat = degreeToRadians(lat2);
        double tLong = degreeToRadians(lng2);

        double dLon = (tLong - fLong);

        double degree = radiansToDegree(Math.atan2(sin(dLon) * cos(tLat),
                cos(fLat) * sin(tLat) - sin(fLat) * cos(tLat) * cos(dLon)));

        if (degree >= 0) {
            return degree;
        } else {
            return 360 + degree;
        }
    }*/



    void doBindService() {
        System.out.println("---------jai-----service bind------");
        bindService(new Intent(this, XmppService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (mConnection != null) {
            unbindService(mConnection);
        }
    }


    @Override
    public boolean onUnbind(Intent intent) {

        System.out.println("---------jai-----destroyed 3------");
        return super.onUnbind(intent);


    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (locationHandler != null) {
            locationHandler.start();
        }

    }
}
