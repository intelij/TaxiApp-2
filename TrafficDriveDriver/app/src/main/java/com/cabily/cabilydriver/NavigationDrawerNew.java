package com.cabily.cabilydriver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Hockeyapp.ActionBarActivityHockeyApp;
import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceManager;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.adapter.HomeMenuListAdapter;
import com.cabily.cabilydriver.widgets.PkDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Prem Kumar and Anitha on 11/17/2016.
 */

public class NavigationDrawerNew extends ActionBarActivityHockeyApp {
    ActionBarDrawerToggle actionBarDrawerToggle;
    static DrawerLayout drawerLayout;
    private static RelativeLayout mDrawer;
    private Context context;
    private ListView mDrawerList;

    private static HomeMenuListAdapter mMenuAdapter;
    private String[] title;
    private int[] icon;
    private String Language_code="";
    private boolean isAppInfoAvailable = false;
    private Dialog dialog;
    private SessionManager session;
    private ServiceRequest mRequest;

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private TextView tv_version;
    String currentVersion;
    private String sType = "";
    public static boolean sPushType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        context = getApplicationContext();
        session  = new SessionManager(NavigationDrawerNew.this);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mDrawer = (RelativeLayout) findViewById(R.id.drawer);
        mDrawerList = (ListView) findViewById(R.id.drawer_listview);

        tv_version= (TextView) findViewById(R.id.verion_no);
        try {
            currentVersion = NavigationDrawerNew.this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tv_version.setText("v"+currentVersion);
        /*MaterialRippleLayout.on(mDrawerList)
                .rippleColor(R.color.ripple_blue_color)
                .create();*/


        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("type")){
                sType = intent.getStringExtra("type");
                if ("push".equalsIgnoreCase(sType))
                {
                    sPushType = true;
                }else{
                    sPushType = false;
                }
            }
        }

        if (savedInstanceState == null) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content_frame, new DashBoardDriver());
            tx.commit();
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();



        title = new String[]{"username", getResources().getString(R.string.navigationDrawer_label_home),
                getResources().getString(R.string.navigationDrawer_label_tripSummary),
                getResources().getString(R.string.navigationDrawer_label_backAccount),
                getResources().getString(R.string.navigationDrawer_label_paymentDetail),
                getResources().getString(R.string.navigationDrawer_label_changePassword),
                getResources().getString(R.string.navgation_drawer_change_language),
                getResources().getString(R.string.navigation_label_Feedback),
                getResources().getString(R.string.navigation_label_aboutus),
                getResources().getString(R.string.navigationDrawer_label_logout),
        };

        icon = new int[]{R.drawable.placeholder_icon, R.drawable.home,
                R.drawable.profile, R.drawable.card,
                R.drawable.payment, R.drawable.pass_new,R.drawable.changelanguage,R.drawable.report,R.drawable.about_us, R.drawable.logout_new};

        mMenuAdapter = new HomeMenuListAdapter(context, title, icon);
        mDrawerList.setAdapter(mMenuAdapter);
        mMenuAdapter.notifyDataSetChanged();


        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                cd = new ConnectionDetector(NavigationDrawerNew.this);
                isInternetPresent = cd.isConnectingToInternet();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                switch (position) {

                    case 0:
                         break;
                    case 1:
                        ft.replace(R.id.content_frame, new DashBoardDriver());
                        break;
                    case 2:
                        ft.replace(R.id.content_frame, new TripSummeryList());
                        break;
                    case 3:
                        ft.replace(R.id.content_frame, new BankDetails());
                        break;
                    case 4:
                        ft.replace(R.id.content_frame, new PaymentDetails());
                        break;
                    case 5:
                        ft.replace(R.id.content_frame, new ChangePassWord());
                        break;
                    case 6:
                        ft.replace(R.id.content_frame, new SettingsLanguageChange());
                        break;
                    case 7:
                        Intent feedback_intent = new Intent(NavigationDrawerNew.this, FeedBackPage.class);
                        startActivity(feedback_intent);

                        break;
                    case 8:
                        Intent about_intent = new Intent(NavigationDrawerNew.this, AboutUs.class);
                        startActivity(about_intent);

                        break;
                    case 9:

                        showBackPressedDialog(true);
                        break;

                }
                ft.commit();
                mDrawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(mDrawer);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
            /*int id = item.getItemId();

	        //noinspection SimplifiableIfStatement
	        if (id == R.id.action_settings) {
	            return true;
	        }*/

        return super.onOptionsItemSelected(item);
    }

    public static void openDrawer() {

        drawerLayout.openDrawer(mDrawer);
    }

    public static void disableSwipeDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public static void enableSwipeDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(NavigationDrawerNew.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showBackPressedDialog(false);
    }

    private void changeFragment(Fragment targetFragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }


    private void showBackPressedDialog(final boolean isLogout) {

        System.gc();
        if(isLogout){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_app_exit)
                    .setPositiveButton(getResources().getString(R.string.alert_label_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(isLogout){
                                logout();
                            }


                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.navigation_label_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            Dialog dialog = builder.create();
            dialog.show();



        }
        else

        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_app_exiting)
                    .setPositiveButton(getResources().getString(R.string.alert_label_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.navigation_label_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            Dialog dialog = builder.create();
            dialog.show();
        }





    }




    private void logout() {
        showDialog("Logout");
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        HashMap<String, String> userDetails = session.getUserDetails();
        String driverId = userDetails.get("driverid");
        jsonParams.put("driver_id", "" + driverId);
        jsonParams.put("device", "" + "ANDROID");
        ServiceManager manager = new ServiceManager(this, updateAvailablityServiceListener);
        manager.makeServiceRequest(ServiceConstant.LOGOUT_REQUEST, Request.Method.POST, jsonParams);
    }

    private ServiceManager.ServiceListener updateAvailablityServiceListener = new ServiceManager.ServiceListener() {
        @Override
        public void onCompleteListener(Object object) {
            dismissDialog();
            postRequest_applaunch(ServiceConstant.app_launching_url);
        }

        @Override
        public void onErrorListener(Object error) {
            dismissDialog();
        }
    };

    private void postRequest_applaunch(String Url) {

        System.out.println("-------------Splash App Information Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "driver");
        jsonParams.put("id", "");
        mRequest = new ServiceRequest(NavigationDrawerNew.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------Splash App Information Response----------------" + response);
                String Str_status = "", sContact_mail = "", sCustomerServiceNumber = "", sSiteUrl = "", sXmppHostUrl = "", sHostName = "", sFacebookId = "", sGooglePlusId = "", sPhoneMasking = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            JSONObject info_object = response_object.getJSONObject("info");
                            if (info_object.length() > 0) {
                                sContact_mail = info_object.getString("site_contact_mail");
                                sCustomerServiceNumber = info_object.getString("customer_service_number");
                                sSiteUrl = info_object.getString("site_url");
                                sXmppHostUrl = info_object.getString("xmpp_host_url");
                                sHostName = info_object.getString("xmpp_host_name");
                              /*  sFacebookId = info_object.getString("facebook_app_id");
                                sGooglePlusId = info_object.getString("google_plus_app_id");
                                sPhoneMasking = info_object.getString("phone_masking_status");*/

                                /*server_mode = info_object.getString("server_mode");
                                site_mode = info_object.getString("site_mode");
                                site_string = info_object.getString("site_mode_string");
                                site_url = info_object.getString("site_url");*/
                                Language_code= info_object.getString("lang_code");
                                isAppInfoAvailable = true;
                            } else {
                                isAppInfoAvailable = false;
                            }
                           /* sPendingRideId= response_object.getString("pending_rideid");
                            sRatingStatus= response_object.getString("ride_status");*/
                        } else {
                            isAppInfoAvailable = false;
                        }
                    } else {
                        isAppInfoAvailable = false;
                    }
                    if (Str_status.equalsIgnoreCase("1") && isAppInfoAvailable) {
                        Locale locale = null;
                        switch (Language_code) {
                            case "en":
                                locale = new Locale("en");
                                session.setlamguage("en", "en");
                                //  System.out.println("========English Language========"+language_change.getSelectedItem().toString()+"\t\ten");
                                //  Intent in=new Intent(ProfilePage.this,NavigationDrawer.class);
                                //   finish();
                                //  startActivity(in);

//                        Intent bi = new Intent();
//                        bi.setAction("homepage");
//                        sendBroadcast(bi);
//                        finish();
                                break;
                            case "es":
                                locale = new Locale("es");
                                session.setlamguage("es", "es");
                                //     System.out.println("========Arabic Language========"+language_change.getSelectedItem().toString()+"\t\tar");
                                //     Intent i=new Intent(ProfilePage.this,NavigationDrawer.class);
                                //     finish();
                                //     startActivity(i);

//                        Intent bii = new Intent();
//                        bii.setAction("homepage");
//                        sendBroadcast(bii);
//                        finish();
                                break;

                            default:
                                locale = new Locale("en");
                                session.setlamguage("en", "en");
                                break;
                        }

                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());


                        finish();

                        session.logoutUser();

                       /* if(site_mode.equalsIgnoreCase("dev"))
                        {
                            mInfoDialog = new PkDialogWithoutButton(Splash.this);
                            mInfoDialog.setDialogTitle("ALERT");
                            mInfoDialog.setDialogMessage(site_string);
                            mInfoDialog.show();
                        }
                        else
                        {
                            Intent intent = new Intent(Splash.this,HomePage.class);
                            startActivity(intent);
                            finish();
                        }
                        if(server_mode.equalsIgnoreCase("0"))
                        {
                            Toast.makeText(context, site_url, Toast.LENGTH_SHORT).show();
                        }*/
                    } else {
                        Toast.makeText(NavigationDrawerNew.this, "BAD URL", Toast.LENGTH_SHORT).show();

                        /*mInfoDialog = new PkDialogWithoutButton(Splash.this);
                        mInfoDialog.setDialogTitle("");
                        mInfoDialog.setDialogMessage("");
                        mInfoDialog.show();*/
                    }
/*                    if (Str_status.equalsIgnoreCase("1") && isAppInfoAvailable) {

                        session = new SessionManager(Splash.this);
                        if (session.isLoggedIn()) {
                            Intent i = new Intent(Splash.this, DashBoardDriver.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            Intent i = new Intent(Splash.this, HomePage.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    } else {

                        Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.fetchdatatoast));
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onErrorListener() {
                Toast.makeText(NavigationDrawerNew.this, ServiceConstant.baseurl, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void showDialog(String message) {
        dialog = new Dialog(this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void dismissDialog() {
        try {
            if (dialog != null)
                dialog.dismiss();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

    }

    public static void navigationNotifyChange() {
        mMenuAdapter.notifyDataSetChanged();
    }


}

