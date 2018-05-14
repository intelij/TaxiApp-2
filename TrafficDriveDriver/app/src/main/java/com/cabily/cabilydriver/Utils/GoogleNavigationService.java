package com.cabily.cabilydriver.Utils;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cabily.cabilydriver.ArrivedTrip;
import com.cabily.cabilydriver.EndTrip;
import com.cabily.cabilydriver.R;
import com.cabily.cabilydriver.TripPage;

import java.util.HashMap;

/**
 * Created by Prem Kumar and Anitha on 12/28/2016.
 */

public class GoogleNavigationService extends Service {

    private WindowManager mWindowManager;
    private ImageView mImgFloatingView;
    private boolean mIsFloatingViewAttached = false;
    private GestureDetector gestureDetector;
    private SessionManager sessionManager;
    private static final int MAX_CLICK_DURATION = 200;


    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;



  private String PageStatus="";

    private String Str_name = "",Str_rideid="",Str_mobilno="",Str_address,Str_beginAddress="",Str_User_Id="",Str_Interrupt="",
            Str_profilpic="",Str_arr_Str_address="",Str_arr_Str_pickUp_Lat="",Str_arr_Str_pickUp_Long="",Str_arr_Str_user_rating="",Str_Str_droplat="",
    Str_arr_Str_droplon="",Str_arr_str_drop_location="";


    @Override
    public IBinder onBind(Intent intent) {
        //Not use this method
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!mIsFloatingViewAttached){
            mWindowManager.addView(mImgFloatingView, mImgFloatingView.getLayoutParams());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        sessionManager=new SessionManager(getApplicationContext());


        mImgFloatingView = new ImageView(this);
        mImgFloatingView.setImageResource(R.drawable.click_back_two_new);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 600;

        mWindowManager.addView(mImgFloatingView, params);

        mImgFloatingView.setOnTouchListener(new View.OnTouchListener() {

            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        initialX = params.x;
//                        initialY = params.y;
//                        initialTouchX = event.getRawX();
//                        initialTouchY = event.getRawY();
//                        Log.d("TAG", "Action was DOWN");
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        Log.d("TAG", "Action was MOVE");
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
//                        mWindowManager.updateViewLayout(mImgFloatingView, params);
//                        return true;
//
//                }

              if (gestureDetector.onTouchEvent(event)) {


                    HashMap<String, String> onPage=sessionManager.getApp_Current_Page_Status();

                    PageStatus=onPage.get(SessionManager.KEY_APP_CURRENT_PAGE_STATUS);


                  cd = new ConnectionDetector(getApplicationContext());
                  isInternetPresent = cd.isConnectingToInternet();

                  if (isInternetPresent) {

                      Intent finish_TripPage = new Intent();
                      finish_TripPage.setAction("com.finish.tripPage");

                      sendBroadcast(finish_TripPage);

                      stopSelf();

                      Intent intent = new Intent(getApplicationContext(), TripPage.class);
                      intent.addCategory(Intent.CATEGORY_HOME);
                      intent.addCategory(Intent.CATEGORY_DEFAULT);
                      intent.addCategory(Intent.CATEGORY_MONKEY);
                      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                      intent.putExtra("interrupted","Yes");
                      startActivity(intent);
                  }

/*                    if(PageStatus.equals("ArrivedTrip")) {


                        System.out.println("*****************************************Arrived Trip**********************************");

                        HashMap<String, String> dataarr = sessionManager.getGoogleNavicationValueArrived();

                        Str_arr_Str_address=dataarr.get(SessionManager.GN_ARRIVE_KEY_ADDRESS);
                        Str_rideid = dataarr.get(SessionManager.GN_ARRIVE_KEY_APP_RIDEID);
                        Str_arr_Str_pickUp_Lat=dataarr.get(SessionManager.GN_ARRIVE_KEY_APP_PICKUPLAT);
                        Str_arr_Str_pickUp_Long=dataarr.get(SessionManager.GN_ARRIVE_KEY_APP_PICKUP_LONG);
                        Str_name = dataarr.get(SessionManager.GN_ARRIVE_KEY_APP_USERNAME);
                        Str_arr_Str_user_rating=dataarr.get(SessionManager.GN_ARRIVE_KEY_APP_USERRATING);
                        Str_mobilno = dataarr.get(SessionManager.GN_ARRIVE_KEY_APP_PHONENO);
                        Str_User_Id = dataarr.get(SessionManager.GN_ARRIVE_KEY_APP_USERID);
                        Str_profilpic = dataarr.get(SessionManager.GN_ARRIVE_KEY_APP_USERIMG);
                        Str_Str_droplat=dataarr.get(SessionManager.KEY_ARRIVE_APP_DROP_LAT);
                        Str_arr_Str_droplon=dataarr.get(SessionManager.KEY_ARRIVE_APP_DROP_LON);
                        Str_arr_str_drop_location=dataarr.get(SessionManager.KEY_ARRIVE_APP_DROP_LOCATION);

                        Intent finish_TripPages = new Intent();
                        finish_TripPages.setAction("com.app.finish.ArrivedTrip");
                        sendBroadcast(finish_TripPages);

                        stopSelf();

                        Intent in = new Intent(getApplicationContext(), ArrivedTrip.class);
                        in.addCategory(Intent.CATEGORY_HOME);
                        in.addCategory(Intent.CATEGORY_DEFAULT);
                        in.addCategory(Intent.CATEGORY_MONKEY);
                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        in.putExtra("address", Str_arr_Str_address);
                        in.putExtra("rideId", Str_rideid);
                        in.putExtra("pickuplat", Str_arr_Str_pickUp_Lat);
                        in.putExtra("pickup_long", Str_arr_Str_pickUp_Long);
                        in.putExtra("username", Str_name);
                        in.putExtra("userrating", Str_arr_Str_user_rating);
                        in.putExtra("phoneno", Str_mobilno);
                        in.putExtra("userimg", Str_profilpic);
                        in.putExtra("UserId", Str_User_Id);
                        in.putExtra("drop_lat", Str_Str_droplat);
                        in.putExtra("drop_lon", Str_arr_Str_droplon);
                        in.putExtra("drop_location", Str_arr_str_drop_location);
                        startActivity(in);


                    }else if(PageStatus.equals("EndTrip")){

                        System.out.println("*****************************************EripTrip**********************************");

                        HashMap<String, String> data = sessionManager.getGoogleNavicationValue();
                        Str_name = data.get(SessionManager.GN_KEY_NAME);
                        Str_rideid = data.get(SessionManager.GN_KEY_APP_RIDEID);
                        Str_mobilno = data.get(SessionManager.GN_KEY_APP_MOBILNO);
                        Str_address = data.get(SessionManager.GN_KEY_APP_PICKUPLATLNG);
                        Str_beginAddress = data.get(SessionManager.GN_KEY_APP_STARTPOINT);
                        Str_User_Id = data.get(SessionManager.GN_KEY_APP_USER_ID);
                        Str_Interrupt = data.get(SessionManager.GN_KEY_APP_INTERRUPTED);
                        Str_profilpic = data.get(SessionManager.GN_KEY_APP_USER_IMAGE);

                        Intent finish_TripPage = new Intent();
                        finish_TripPage.setAction("com.app.finish.EndTrip");
                        sendBroadcast(finish_TripPage);

                        stopSelf();

                        Intent intent = new Intent(getApplicationContext(), EndTrip.class);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.addCategory(Intent.CATEGORY_MONKEY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("name", Str_name);
                        intent.putExtra("rideid", Str_rideid);
                        intent.putExtra("mobilno", Str_mobilno);
                        intent.putExtra("pickuplatlng", Str_address);
                        intent.putExtra("startpoint", Str_beginAddress);
                        intent.putExtra("user_id", Str_User_Id);
                        intent.putExtra("interrupted",Str_Interrupt);
                        intent.putExtra("user_image",Str_profilpic );
                        startActivity(intent);

                    }*/

                    return true;
                }


                return false;
            }
        });

        mIsFloatingViewAttached = true;
    }


    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }


    public void removeView() {
        if (mImgFloatingView != null){
            mWindowManager.removeView(mImgFloatingView);
            mIsFloatingViewAttached = false;
        }
    }

    @Override
    public void onDestroy() {
        System.out.println("-----------google Navigation service onDestroy----------------");
        super.onDestroy();

        removeView();
    }
}
