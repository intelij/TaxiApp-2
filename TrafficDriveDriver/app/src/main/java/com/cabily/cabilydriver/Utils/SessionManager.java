package com.cabily.cabilydriver.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.app.xmpp.XmppService;
import com.cabily.cabilydriver.Splash;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    static SharedPreferences pref;
    // Editor for Shared preferences
    static Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY = "key";
    public static final String KEY_DRIVER_NAME = "drivername";
    public static final String KEY_DRIVERID = "driverid";
    public static final String KEY_DRIVER_IMAGE = "driverimage";
    public static final String KEY_VEHICLENO = "vechicleno";
    public static final String KEY_VEHICLE_MODEL = "vehiclemodel";
    public static final String KEY_SEC_KEY = "seckey";
    public static final String KEY_VEHICLE_NAME = "VEHICLE_NAME";
    public static final String KEY_ONLINE = "online";
    public static final String KEY_COUNT = "keycount";
    public static final String KEY_GCM_ID = "gcmId";
    public static final String KEY_Language_code = "language_code";
    public static final String KEY_Language = "language";
    public static final String KEY_WAIT = "waittime";
    public static final String KEY_WAIT_STATUS = "wait_status";
    public static final String KEY_STATUS= "ride_status";
    public static final String KEY_id= "ride_id1";
    public static final String KEY_user_id= "key_user_id";
    public static final String KEY_SERVICE_STATUS= "service_status";
    public static final String KEY_ID_NAME = "Id_Name";
    public static final String KEY_VEHICLE_IMAGE = "Vehicle_image";
    public static final String KEY_VEHICLE_BitMap_IMAGE = "Vehicle_Bitmap";
    public static final String KEY_HOST_URL = "xmpphostUrl";
    public static final String KEY_Traffic = "traffic";
    public static final String KEY_HOST_NAME = "xmpphostName";
    public static final String KEY_APP_STATUS = "appStatus";


    public static final String GN_KEY_NAME = "name";
    public static final String GN_KEY_APP_RIDEID = "rideid";
    public static final String GN_KEY_APP_MOBILNO = "mobilno";
    public static final String GN_KEY_APP_PICKUPLATLNG = "pickuplatlng";
    public static final String GN_KEY_APP_STARTPOINT = "startpoint";
    public static final String GN_KEY_APP_USER_ID = "user_id";
    public static final String GN_KEY_APP_INTERRUPTED = "interrupted";
    public static final String GN_KEY_APP_USER_IMAGE = "user_image";



    public static final String GN_ARRIVE_KEY_ADDRESS = "address";
    public static final String GN_ARRIVE_KEY_APP_RIDEID = "rideId";
    public static final String GN_ARRIVE_KEY_APP_PICKUPLAT = "pickuplat";
    public static final String GN_ARRIVE_KEY_APP_PICKUP_LONG = "pickup_long";
    public static final String GN_ARRIVE_KEY_APP_USERNAME = "username";
    public static final String GN_ARRIVE_KEY_APP_USERRATING = "userrating";
    public static final String GN_ARRIVE_KEY_APP_PHONENO = "phoneno";
    public static final String GN_ARRIVE_KEY_APP_USERID = "UserId";
    public static final String GN_ARRIVE_KEY_APP_USERIMG = "userimg";
    public static final String KEY_ARRIVE_APP_DROP_LAT = "drop_lat";
    public static final String KEY_ARRIVE_APP_DROP_LON = "drop_lon";
    public static final String KEY_ARRIVE_APP_DROP_LOCATION = "drop_location";



    public static final String KEY_APP_CURRENT_PAGE_STATUS = "current_page";
    //Restart Xmpp Service
    public static final String KEY_XMPP_SERVICE_RESTART_STATE = "checkState";

    public static final String KEY_AD_TITLE = "ad_title";
    public static final String KEY_AD_MSG = "ad_msg";

    public static final String KEY_AD_BANNER = "ad_banner";

    private static final String IS_AD = "IsADIn";

    public static final String KEY_SITE_URL = "siteUrl";
    public static final String KEY_ABOUT_US= "about_us";


    public static final String KEY_NOTIFICATION_STATUS = "notification_status";

    public static final String KEY_ADD_TITLE = "add_title";
    public static final String KEY_ADD_MESSAGE = "add_message";
    public static final String KEY_ADD_IMAGE = "add_image";

    public static final String KEY_DRIVER_ALERT_MSG = "driver_alert_msg";

    public static final String KEY_PHONE_MASKING_STATUS= "phonemasking";

    public static final String KEY_CUS_PHO = "cus_phone";
    public static final String KEY_CUS_ADR = "cus_adr";
    public static final String KEY_IS_TRIP = "is_trip";


    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this._context = context;
        if(pref == null && editor == null) {
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }

    }

    /**
     * Create login session
     */
    public void createLoginSession(String image, String driverid, String name, String email, String vehicleno,
                                   String vechilemodel, String key, String sec_key,String gcmID) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_DRIVER_IMAGE, image);
        editor.putString(KEY_DRIVERID, driverid);
        editor.putString(KEY_DRIVER_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_VEHICLENO, vehicleno);
        editor.putString(KEY_VEHICLE_MODEL, vechilemodel);
        editor.putString(KEY_SEC_KEY, sec_key);
        editor.putString(KEY, key);
        editor.putString(KEY_GCM_ID, gcmID);
        editor.commit();
    }

    public void createSessionOnline(String online){
        editor.putString(KEY_ONLINE, online);
        editor.commit();
    }
    public void setAgent(String userId) {
        editor.putString(KEY_ID_NAME, userId);

        editor.commit();
    }

    public void setDriver_image(String img) {
        editor.putString(KEY_DRIVER_IMAGE, img);

        editor.commit();
    }
    /**
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_DRIVER_IMAGE, pref.getString(KEY_DRIVER_IMAGE, ""));
        user.put(KEY_DRIVERID, pref.getString(KEY_DRIVERID, ""));
        user.put(KEY_DRIVER_NAME, pref.getString(KEY_DRIVER_NAME, ""));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_VEHICLENO, pref.getString(KEY_VEHICLENO, ""));
        user.put(KEY_VEHICLE_MODEL, pref.getString(KEY_VEHICLE_MODEL, ""));
        user.put(KEY, pref.getString(KEY, ""));
        user.put(KEY_SITE_URL, pref.getString(KEY_SITE_URL, ""));
        user.put(KEY_ABOUT_US, pref.getString(KEY_ABOUT_US, ""));
        user.put(KEY_GCM_ID, pref.getString(KEY_GCM_ID, ""));
        user.put(KEY_STATUS, pref.getString(KEY_STATUS, ""));
        user.put(KEY_id, pref.getString(KEY_id, ""));
        user.put(KEY_ID_NAME, pref.getString(KEY_ID_NAME, ""));
        user.put(KEY_Language_code, pref.getString(KEY_Language_code, ""));
        user.put(KEY_VEHICLE_IMAGE, pref.getString(KEY_VEHICLE_IMAGE, ""));
        user.put(KEY_VEHICLE_BitMap_IMAGE, pref.getString(KEY_VEHICLE_BitMap_IMAGE, ""));
        user.put(KEY_user_id, pref.getString(KEY_user_id, ""));
        user.put(KEY_SEC_KEY, pref.getString(KEY_SEC_KEY, ""));
        return user;
    }

    public void setdriverNameUpdate(String image) {
        editor.putString(KEY_DRIVER_NAME, image);
        editor.commit();
    }

    public void setdriver_image(String image) {
        editor.putString(KEY_DRIVER_IMAGE, image);
        editor.commit();
    }
    public void setVechileNumberUpdate(String vech_no) {
        editor.putString(KEY_VEHICLENO, vech_no);
        editor.commit();
    }
    public void setVechileModelUpdate(String vech_model) {
        editor.putString(KEY_VEHICLE_MODEL, vech_model);
        editor.commit();
    }

    public HashMap<String, String> getdriver_image() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_DRIVER_IMAGE, pref.getString(KEY_DRIVER_IMAGE, ""));
        return catID;
    }

    public HashMap<String, String> getOnlineDetails() {
        HashMap<String, String> online = new HashMap<String, String>();
        online.put(KEY_ONLINE, pref.getString(KEY_ONLINE, ""));
        return online;
    }


    public void setKeyPhoneMaskingStatus(String phonemasking)
    {
        editor.putString(KEY_PHONE_MASKING_STATUS, phonemasking);
        editor.commit();
    }
    public HashMap<String, String> getPhoneMasking() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_PHONE_MASKING_STATUS, pref.getString(KEY_PHONE_MASKING_STATUS, ""));
        return catID;
    }


    public void setlamguage(String userId,String secretKey) {
        editor.putString(KEY_Language_code, userId);
        editor.putString(KEY_Language, secretKey);
        editor.commit();
    }

    public void setaboutus(String aboutus,String site_url) {
        editor.putString(KEY_ABOUT_US, aboutus);
        editor.putString(KEY_SITE_URL, site_url);
        editor.commit();
    }
    public HashMap<String, String> getLanaguage() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_Language, pref.getString(KEY_Language, ""));
        return catID;
    }
    public HashMap<String, String> getLanaguageCode() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_Language_code, pref.getString(KEY_Language_code, ""));
        return catID;
    }
    public void createStatus(String status){
        editor.putString(KEY_STATUS, status);
        editor.commit();
    }
    public void createid(String status){
        editor.putString(KEY_id, status);
        editor.commit();
    }
    public void createuserid(String status){
        editor.putString(KEY_user_id, status);
        editor.commit();
    }
    public void setRequestCount(int count){
        editor.putInt(KEY_COUNT, count);
        editor.commit();
    }
    public void setVehicleImage(String count){
        editor.putString(KEY_VEHICLE_IMAGE, count);
        editor.commit();
    }
    public void setTrafficImage(String count){
        editor.putString(KEY_Traffic, count);
        editor.commit();
    }
    public HashMap<String, String> getTrafficImage() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_Traffic, pref.getString(KEY_Traffic, ""));
        return catID;
    }
    public void setVehicle_BitmapImage(String count){
        editor.putString(KEY_VEHICLE_BitMap_IMAGE, count);
        editor.commit();
    }
    public HashMap<String, String> getBitmapCode() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_VEHICLE_BitMap_IMAGE, pref.getString(KEY_VEHICLE_BitMap_IMAGE, ""));
        return catID;
    }
    public HashMap<String, Integer> getRequestCount(){
        HashMap<String, Integer> count = new HashMap<String, Integer>();
        count.put(KEY_COUNT, pref.getInt(KEY_COUNT, 0));
        return count;
    }

    public void createServiceStatus(String status){
        editor.putString(KEY_SERVICE_STATUS, status);
        editor.commit();
    }
    public HashMap<String, String> getServiceStatus() {
        HashMap<String, String> service = new HashMap<String, String>();
        service.put(KEY_SERVICE_STATUS, pref.getString(KEY_SERVICE_STATUS, ""));
        return service;
    }
    public void setXmpp(String userId,String secretKey) {
        editor.putString(KEY_HOST_URL, userId);
        editor.putString(KEY_HOST_NAME, secretKey);
        editor.commit();
    }
    public HashMap<String, String> getXmpp() {
        HashMap<String, String> code = new HashMap<String, String>();
        code.put(KEY_HOST_URL, pref.getString(KEY_HOST_URL, ""));
        code.put(KEY_HOST_NAME, pref.getString(KEY_HOST_NAME, ""));
        return code;
    }
   /* public void setRequestCount(int count){
        editor.putInt(KEY_COUNT, count);
        editor.commit();
    }

    public HashMap<String, Integer> getRequestCount(){
        HashMap<String, Integer> count = new HashMap<String, Integer>();
        count.put(KEY_COUNT, pref.getInt(KEY_COUNT, 0));
        return count;
    }*/
   public void setWaitingStatus(String mSecs){
       editor.putString(KEY_WAIT_STATUS, mSecs);
       editor.commit();
   }

    public HashMap<String, String> getWaitingStatus(){
        HashMap<String, String> count = new HashMap<String, String>();
        count.put(KEY_WAIT_STATUS, pref.getString(KEY_WAIT_STATUS, ""));
        return count;
    }
    public void setWaitingTime(String mSecs){
        editor.putString(KEY_WAIT, mSecs);
        editor.commit();
    }

    public HashMap<String, String> getWaitingTime(){
        HashMap<String, String> count = new HashMap<String, String>();
        count.put(KEY_WAIT, pref.getString(KEY_WAIT, ""));
        return count;
    }

    public void setCount(String mSecs){
        editor.putString(KEY_WAIT, mSecs);
        editor.commit();
    }

    public HashMap<String, String> getCount() {
        HashMap<String, String> count = new HashMap<String, String>();
        count.put(KEY_WAIT, pref.getString(KEY_WAIT, ""));
        return count;
    }
    public void setUserVehicle(String name){
        editor.putString(KEY_VEHICLE_NAME, name);
        editor.commit();
    }

    //------string App Status----
    public void setAppStatus(String state) {
        editor.putString(KEY_APP_STATUS, state);
        editor.commit();
    }

    //-----------Get App Status-----
    public HashMap<String, String> getAppStatus() {
        HashMap<String, String> code = new HashMap<String, String>();
        code.put(KEY_APP_STATUS, pref.getString(KEY_APP_STATUS, ""));
        return code;
    }

      //----------GoogleNavicationService------
    public void setGoogleNavicationValue(String Str_name,String Str_rideid,String Str_mobilno,String Str_pickuplatlng,String Str_startpoint,String Str_User_Id,String Str_Interrupt,String Str_profilpic){
        editor.putString(GN_KEY_NAME,Str_name);
        editor.putString(GN_KEY_APP_RIDEID,Str_rideid);
        editor.putString(GN_KEY_APP_MOBILNO,Str_mobilno);
        editor.putString(GN_KEY_APP_PICKUPLATLNG,Str_pickuplatlng);
        editor.putString(GN_KEY_APP_STARTPOINT,Str_startpoint);
        editor.putString(GN_KEY_APP_USER_ID,Str_User_Id);
        editor.putString(GN_KEY_APP_INTERRUPTED,Str_Interrupt);
        editor.putString(GN_KEY_APP_USER_IMAGE,Str_profilpic);
        editor.commit();
    }

    //----------GoogleNavicationService------
    public  HashMap<String,String> getGoogleNavicationValue(){
        HashMap<String, String> g_values = new HashMap<String, String>();
        g_values.put(GN_KEY_NAME, pref.getString(GN_KEY_NAME, ""));
        g_values.put(GN_KEY_APP_RIDEID, pref.getString(GN_KEY_APP_RIDEID, ""));
        g_values.put(GN_KEY_APP_MOBILNO, pref.getString(GN_KEY_APP_MOBILNO, ""));
        g_values.put(GN_KEY_APP_PICKUPLATLNG, pref.getString(GN_KEY_APP_PICKUPLATLNG, ""));
        g_values.put(GN_KEY_APP_STARTPOINT, pref.getString(GN_KEY_APP_STARTPOINT, ""));
        g_values.put(GN_KEY_APP_USER_ID, pref.getString(GN_KEY_APP_USER_ID, ""));
        g_values.put(GN_KEY_APP_INTERRUPTED, pref.getString(GN_KEY_APP_INTERRUPTED, ""));
        g_values.put(GN_KEY_APP_USER_IMAGE, pref.getString(GN_KEY_APP_USER_IMAGE, ""));

        return g_values;
    }



    //----------GoogleNavicationArrivedService------
    public void setGoogleNavicationValueArrived(String Str_address,String Str_RideId,String Str_pickUp_Lat,String Str_pickUp_Long,String Str_username,String Str_user_rating,String Str_user_phoneno,String Str_user_img,
                                                String Str_user_Id,String Str_droplat,String Str_droplon,String str_drop_location){
        editor.putString(GN_ARRIVE_KEY_ADDRESS,Str_address);
        editor.putString(GN_ARRIVE_KEY_APP_RIDEID,Str_RideId);
        editor.putString(GN_ARRIVE_KEY_APP_PICKUPLAT,Str_pickUp_Lat);
        editor.putString(GN_ARRIVE_KEY_APP_PICKUP_LONG,Str_pickUp_Long);
        editor.putString(GN_ARRIVE_KEY_APP_USERNAME,Str_username);
        editor.putString(GN_ARRIVE_KEY_APP_USERRATING,Str_user_rating);
        editor.putString(GN_ARRIVE_KEY_APP_PHONENO,Str_user_phoneno);
        editor.putString(GN_ARRIVE_KEY_APP_USERIMG,Str_user_img);
        editor.putString(GN_ARRIVE_KEY_APP_USERID,Str_user_Id);
        editor.putString(KEY_ARRIVE_APP_DROP_LAT,Str_droplat);
        editor.putString(KEY_ARRIVE_APP_DROP_LON,Str_droplon);
        editor.putString(KEY_ARRIVE_APP_DROP_LOCATION,str_drop_location);
        editor.commit();
    }

    //----------GoogleNavicationService------
    public  HashMap<String,String> getGoogleNavicationValueArrived(){
        HashMap<String, String> g_values = new HashMap<String, String>();
        g_values.put(GN_ARRIVE_KEY_ADDRESS, pref.getString(GN_ARRIVE_KEY_ADDRESS, ""));
        g_values.put(GN_ARRIVE_KEY_APP_RIDEID, pref.getString(GN_ARRIVE_KEY_APP_RIDEID, ""));
        g_values.put(GN_ARRIVE_KEY_APP_PICKUPLAT, pref.getString(GN_ARRIVE_KEY_APP_PICKUPLAT, ""));
        g_values.put(GN_ARRIVE_KEY_APP_PICKUP_LONG, pref.getString(GN_ARRIVE_KEY_APP_PICKUP_LONG, ""));
        g_values.put(GN_ARRIVE_KEY_APP_USERNAME, pref.getString(GN_ARRIVE_KEY_APP_USERNAME, ""));
        g_values.put(GN_ARRIVE_KEY_APP_USERRATING, pref.getString(GN_ARRIVE_KEY_APP_USERRATING, ""));
        g_values.put(GN_ARRIVE_KEY_APP_PHONENO, pref.getString(GN_ARRIVE_KEY_APP_PHONENO, ""));
        g_values.put(GN_ARRIVE_KEY_APP_USERIMG, pref.getString(GN_ARRIVE_KEY_APP_USERIMG, ""));
        g_values.put(GN_ARRIVE_KEY_APP_USERID, pref.getString(GN_ARRIVE_KEY_APP_USERID, ""));
        g_values.put(KEY_ARRIVE_APP_DROP_LAT, pref.getString(KEY_ARRIVE_APP_DROP_LAT, ""));
        g_values.put(KEY_ARRIVE_APP_DROP_LON, pref.getString(KEY_ARRIVE_APP_DROP_LON, ""));
        g_values.put(KEY_ARRIVE_APP_DROP_LOCATION, pref.getString(KEY_ARRIVE_APP_DROP_LOCATION, ""));

        return g_values;
    }



    //------string App-onpage-Status----
    public void setApp_Current_Page_Status(String state) {
        editor.putString(KEY_APP_CURRENT_PAGE_STATUS, state);
        editor.commit();
    }

    //-----------Get App--onpage-Status-----
    public HashMap<String, String> getApp_Current_Page_Status() {
        HashMap<String, String> code = new HashMap<String, String>();
        code.put(KEY_APP_CURRENT_PAGE_STATUS, pref.getString(KEY_APP_CURRENT_PAGE_STATUS, ""));
        return code;
    }


    public void setXmppServiceState(String state) {
        editor.putString(KEY_XMPP_SERVICE_RESTART_STATE, state);
        editor.commit();
    }
    public HashMap<String, String> getXmppServiceState() {
        HashMap<String, String> xmppState = new HashMap<String, String>();
        xmppState.put(KEY_XMPP_SERVICE_RESTART_STATE, pref.getString(KEY_XMPP_SERVICE_RESTART_STATE, ""));
        return xmppState;
    }


    public String getUserVehicle(){
        return pref.getString(KEY_VEHICLE_NAME, "");
    }

    /**
     * Clear session details
     */
    public void logoutUser() {

        setXmppServiceState("");

        _context.stopService(new Intent(_context, XmppService.class));

        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, Splash.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        _context.startActivity(i);

    }

    public void setAds(String title,String msg,String banner) {
        editor.putString(KEY_AD_BANNER, banner);
        editor.putString(KEY_AD_MSG, msg);
        editor.putString(KEY_AD_TITLE, title);

        editor.commit();
    }
    public HashMap<String, String> getAds() {
        HashMap<String, String> adDetail = new HashMap<String, String>();
        adDetail.put(KEY_AD_MSG, pref.getString(KEY_AD_MSG, ""));
        adDetail.put(KEY_AD_BANNER, pref.getString(KEY_AD_BANNER, ""));
        adDetail.put(KEY_AD_TITLE, pref.getString(KEY_AD_TITLE, ""));
        return adDetail;
    }
    public void setADS(Boolean categoryID)
    {
        editor.putBoolean(IS_AD, categoryID);
        editor.commit();
    }


    public boolean isAds() {
        return pref.getBoolean(IS_AD, false);
    }

    /**
     * Quick check for login
     * *
     */
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }



    public void setcustomerdetail(String phone,String addr) {
        editor.putString(KEY_CUS_PHO, phone);
        editor.putString(KEY_CUS_ADR, addr);
        editor.commit();
    }


    public HashMap<String, String> getcustomerdetail() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_CUS_ADR, pref.getString(KEY_CUS_ADR, ""));
        catID.put(KEY_CUS_PHO, pref.getString(KEY_CUS_PHO, ""));
        return catID;
    }




    //------ Set Notification Status ----
    public void setNotificationStatus(String status) {
        editor.putString(KEY_NOTIFICATION_STATUS, status);
        editor.commit();
    }

    //----------- Get Notification Status -----
    public String getNotificationStatus() {
        return pref.getString(KEY_NOTIFICATION_STATUS, "");
    }

    //------Set Add----
    public void setAdd(String title, String message, String image) {
        editor.putString(KEY_ADD_TITLE, title);
        editor.putString(KEY_ADD_MESSAGE, message);
        editor.putString(KEY_ADD_IMAGE, image);
        editor.commit();
    }

    //-----------Get Add-----
    public HashMap<String, String> getAdd() {
        HashMap<String, String> code = new HashMap<String, String>();
        code.put(KEY_ADD_TITLE, pref.getString(KEY_ADD_TITLE, ""));
        code.put(KEY_ADD_MESSAGE, pref.getString(KEY_ADD_MESSAGE, ""));
        code.put(KEY_ADD_IMAGE, pref.getString(KEY_ADD_IMAGE, ""));
        return code;
    }

    //------Set Driver Alert Message----
    public void setDriverAlertData(String data) {
        editor.putString(KEY_DRIVER_ALERT_MSG, data);
        editor.commit();
    }

    //------Get Driver Alert Message-----
    public String getDriverAlertData() {
        return pref.getString(KEY_DRIVER_ALERT_MSG, "");
    }

    //------Set Add----
    public void setTripStatus(String status) {
        editor.putString(KEY_IS_TRIP, status);
        editor.commit();
    }

    //-----------Get Add-----
    public String getTripStatus() {
        try {
            return pref.getString(KEY_IS_TRIP, "0");
        } catch (Exception e) {
            e.printStackTrace();

            return "";
        }
    }




}