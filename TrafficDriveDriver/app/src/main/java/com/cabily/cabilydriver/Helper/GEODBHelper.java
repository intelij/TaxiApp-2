package com.cabily.cabilydriver.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cabily.cabilydriver.Pojo.UserPojo;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by anandan on 10-09-2016.
 */
public class GEODBHelper implements GEOCommonValues {

    private String TAG = GEODBHelper.class.getSimpleName();
    private DataBaseHelper myDBHelper;
    private SQLiteDatabase myDataBase;
    private Context myContext;
    private int DATABASE_VERSION = 1;
    private SessionManager manager;

    private static final String CREATE_TABLE_TODO = "CREATE TABLE IF NOT EXISTS " + RIDE_STATUS_TABLE + "(" + "status" + " INTEGER" + ")";
    private static final String CREATE_TABLE_LAT = "CREATE TABLE IF NOT EXISTS " + LATLONG_INFO_TABLE_NAME + "(" + "ride_id" + " INTEGER," + "geo_lat" + " TEXT," + "geo_long" + " TEXT," + "geo_time" + " TEXT" + ")";
    private static final String CREATE_TABLE_DISTANCE = "CREATE TABLE IF NOT EXISTS " + LATLONG_TABLE_DISTANCE + "(" + "status" + " INTEGER," + "geo_lat" + " TEXT," + "geo_long" + " TEXT," + "geo_time" + " TEXT" + ")";
    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "(" + "userid" + " TEXT," + "user_status" + " TEXT," + "ride_id" + " TEXT," + "share_id" + " TEXT" + ")";
    public GEODBHelper(Context context) {
        myContext = context;
        myDBHelper = new DataBaseHelper(context);
        myDataBase = myDBHelper.getWritableDatabase();
        manager = new SessionManager(context);
        open();
    }


    public class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


    /**
     * Function to make DB as readable and writable
     */
    private void open() {

        try {
            if (myDataBase == null) {
                myDataBase = myDBHelper.getWritableDatabase();
            }
            myDataBase.execSQL(CREATE_TABLE_TODO);
            myDataBase.execSQL(CREATE_TABLE_LAT);
            myDataBase.execSQL(CREATE_TABLE_DISTANCE);
            myDataBase.execSQL(CREATE_TABLE_USER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Function to Close the database
     */
    public void close() {

        try {
            Log.d(TAG, "mySQLiteDatabase Closed");

            // ---Closing the database---
            myDBHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Insert the lat and long in the table
     *
     * @param aLatitude
     * @param aLongtitude
     * @param aCurrentDate
     */
    public void insertLatLong(String rideid, double aLatitude, double aLongtitude, String aCurrentDate) {
        try {
            Log.e("Insert", "Store latlong  Info in DB");
            ContentValues values = new ContentValues();
            values.put("ride_id", rideid);
            values.put("geo_lat", aLatitude);
            values.put("geo_long", aLongtitude);
            values.put("geo_time", aCurrentDate);
            myDataBase.insert(LATLONG_INFO_TABLE_NAME
                    , null, values);
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        }
    }


    public void insertLatLongDistance(String status, double aLatitude, double aLongtitude, String aCurrentDate) {
        try {
            Log.e("Insert", "Store latlong  Info in DB");
            ContentValues values = new ContentValues();
            values.put("status", status);
            values.put("geo_lat", aLatitude);
            values.put("geo_long", aLongtitude);
            values.put("geo_time", aCurrentDate);
            myDataBase.insert(LATLONG_TABLE_DISTANCE
                    , null, values);
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        }
    }


    public void insertUserDetails(String userid, String user_status, String ride_id, String share_id) {
        try {
            Log.e("Insert", "Store latlong  Info in DB");
            ContentValues values = new ContentValues();
            values.put("userid", userid);
            values.put("user_status", user_status);
            values.put("ride_id", ride_id);
            values.put("share_id", share_id);
            System.out.println("--------prabu insert values------------"+values);

            myDataBase.insert(TABLE_USER
                    , null, values);
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUser()
    {
        SQLiteDatabase db = myDataBase;
        db.delete(TABLE_USER, null, null);
        userCount();




    }


    public void userCount()
    {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = myDataBase;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        System.out.println("---------------------jai-------------user count--------"+cnt);

    }

    /**
     * Insert the lat and long in the table
     */
    public void insertDriverStatus(String status) {
        try {
            manager.createStatus(status);
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertRide_id(String id) {
        try {
            manager.createid(id);
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertuser_id(String id) {
        try {
            manager.createuserid(id);
            System.out.println("---------G----------inserted user id----------"+id);
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        }
    }

    public String retriveStatus() {
        String status = manager.getUserDetails().get(SessionManager.KEY_STATUS);
        return status;
    }


    public String retriveid() {
        String status = manager.getUserDetails().get(SessionManager.KEY_id);
        return status;
    }
    public String retriveuserid() {
        String status = manager.getUserDetails().get(SessionManager.KEY_user_id);
        return status;
    }
    public ArrayList<String> getData() {
        String selectQuery = "SELECT  * FROM " + LATLONG_INFO_TABLE_NAME;
        SQLiteDatabase db = myDataBase;
        Cursor cursor = db.rawQuery(selectQuery, null);
        String[] data = null;
        ArrayList<String> strings = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {

                String geolong = cursor.getString(0);
                String geolat = cursor.getString(1);
                String geo_time = cursor.getString(2);
                System.out.println("Driver time Lat Long ---------------------" + geo_time + "-------------------" + geolat + "----------------" + geolong);
                strings.add(geolat + ";" + geolong + ";" + geo_time);
            } while (cursor.moveToNext());
        }
     //   db.close();
        return strings;
    }

    public ArrayList<UserPojo> getUserData() {
        String selectQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = myDataBase;
        Cursor cursor = db.rawQuery(selectQuery, null);
        String[] data = null;
        ArrayList<UserPojo> userDbData = new ArrayList<UserPojo>();
        if (cursor.moveToFirst()) {
            do {
                UserPojo pojo = new UserPojo();

                pojo.setUser_id( cursor.getString(0));
                pojo.setBtn_group( cursor.getString(1));
                pojo.setRide_id( cursor.getString(2));
                pojo.setShare_id( cursor.getString(3));
                userDbData.add(pojo);
            } while (cursor.moveToNext());
        }
        //   db.close();
        return userDbData;
    }




    public void Delete(String s)
    {
        SQLiteDatabase db = myDataBase;
        db.delete(LATLONG_TABLE_DISTANCE, null, null);
        System.out.println("jai Table deleted");
    }





    public ArrayList<LatLng> getDataDistance(String status) {
        ArrayList<LatLng> endDistanceTrips = new ArrayList<LatLng>();
        try {
            String rideIDStatic = "";
            String selectQuery = "SELECT  * FROM " + LATLONG_TABLE_DISTANCE + " WHERE status = " + status;
            System.out.println("query-----jai"+selectQuery);
            SQLiteDatabase db = myDataBase;

            if(db.isOpen()){
                Cursor cursor = db.rawQuery(selectQuery, null);
                String[] data = null;
                if (cursor.moveToFirst()) {
                    do {
                        String rideid = cursor.getString(0);
                        String geolat = cursor.getString(1);
                        String geolong = cursor.getString(2);
                        String geo_time = cursor.getString(3);
                        System.out.println("Driver time Lat Long ---------------------" + geo_time + "-------------------" + geolat + "----------------" + geolong);
                        endDistanceTrips.add(new LatLng(Double.parseDouble(geolat),Double.parseDouble(geolong)));
                    } while (cursor.moveToNext());
                }
                //     db.close();
                System.out.println("distance jai latlong "+endDistanceTrips.toString());
            }
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        }
        return endDistanceTrips;

    }

    public ArrayList<String> getDataEndTrip(String rideID) {
        String rideIDStatic = "";
        String selectQuery = "SELECT  * FROM " + LATLONG_INFO_TABLE_NAME + " WHERE ride_id = " + rideID;
        SQLiteDatabase db = myDataBase;
        ArrayList<String> endDataTrips = new ArrayList<>();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery(selectQuery, null);
            String[] data = null;
            if (cursor.moveToFirst()) {
                do {
                    String rideid = cursor.getString(0);
                    String geolat = cursor.getString(1);
                    String geolong = cursor.getString(2);
                    String geo_time = cursor.getString(3);
                    System.out.println("Driver time Lat Long ---------------------" + geo_time + "-------------------" + geolat + "----------------" + geolong);
                    endDataTrips.add(geolat + ";" + geolong + ";" + geo_time);
                } while (cursor.moveToNext());
            }
       //     db.close();
            System.out.println(endDataTrips.toString());
        }

        return endDataTrips;
    }

}
