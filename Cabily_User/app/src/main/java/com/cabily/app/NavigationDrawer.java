package com.cabily.app;

/**
 * Created by Prem Kumar on 10/1/2015.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cabily.HockeyApp.ActionBarActivityHockeyApp;
import com.cabily.adapter.HomeMenuListAdapter;
import com.cabily.fragment.Fragment_HomePage;
import com.cabily.utils.AppInfoSessionManager;
import com.cabily.utils.ConnectionDetector;
import com.cabily.utils.SessionManager;
import com.casperon.app.cabily.R;
import com.mylibrary.dialog.PkDialog;

import java.util.HashMap;
import java.util.Locale;


public class NavigationDrawer extends ActionBarActivityHockeyApp implements DrawerLayout.DrawerListener {
    ActionBarDrawerToggle actionBarDrawerToggle;
    static DrawerLayout drawerLayout;
    private static RelativeLayout mDrawer,version_no_layout;
    private Context context;
    private ListView mDrawerList;

    private static HomeMenuListAdapter mMenuAdapter;
    private String[] title;
    private int[] icon;

    private SessionManager session;

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;

    Fragment bookmyride = new Fragment_HomePage();

    private AppInfoSessionManager SappInfo_Session;
    private TextView tv_version;
    String currentVersion;
    private String lang = "";

    public static boolean drawerState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        context = getApplicationContext();
        session  = new SessionManager(NavigationDrawer.this);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mDrawer = (RelativeLayout) findViewById(R.id.drawer);
        version_no_layout = (RelativeLayout) findViewById(R.id.version_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_listview);
        tv_version= (TextView) findViewById(R.id.verion_no);
        version_no_layout.setEnabled(false);
        version_no_layout.setClickable(false);

        HashMap<String, String> language = session.getLanaguage();
        lang = language.get(SessionManager.KEY_Language);

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);




        try {
            currentVersion = NavigationDrawer.this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tv_version.setText(getResources().getString(R.string.aboutus_lable_version_textview)+currentVersion);

        /*MaterialRippleLayout.on(mDrawerList)
                .rippleColor(R.color.ripple_blue_color)
                .create();*/


        if (savedInstanceState == null) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content_frame, new Fragment_HomePage());
            tx.commit();
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        title = new String[]{"username", getResources().getString(R.string.navigation_label_bookmyride),
                getResources().getString(R.string.navigation_label_myride),
                getResources().getString(R.string.navigation_label_money),
                getResources().getString(R.string.navigation_label_invite),
                getResources().getString(R.string.navigation_label_ratecard),
                getResources().getString(R.string.navigation_label_emergency),
                getResources().getString(R.string.navigation_label_feedback),
                getResources().getString(R.string.navigation_label_aboutus),

        };

        icon = new int[]{R.drawable.no_profile_image_avatar_icon, R.drawable.book_my_ride,
                R.drawable.myride, R.drawable.cabily_money,
                R.drawable.invite_and_earn, R.drawable.rate_card, R.drawable.emergency_contact,R.drawable.report_issue_icon, R.drawable.aboutus_icon};

        mMenuAdapter = new HomeMenuListAdapter(context, title, icon);
        mDrawerList.setAdapter(mMenuAdapter);
//        mDrawerList.setItemChecked(1, true);
        mMenuAdapter.notifyDataSetChanged();

        drawerLayout.setDrawerListener(NavigationDrawer.this);

        version_no_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("do nothing");

            }
        });
        tv_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("do nothing");

            }
        });

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                cd = new ConnectionDetector(NavigationDrawer.this);
                isInternetPresent = cd.isConnectingToInternet();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                switch (position) {

                    case 0:
                        Intent intent = new Intent(NavigationDrawer.this, ProfilePage.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;
                    case 1:
                        ft.replace(R.id.content_frame, bookmyride);
                        break;
                    case 2:
                        if(isInternetPresent)
                        {
                            Intent myRide_intent = new Intent(NavigationDrawer.this, MyRides.class);
                            startActivity(myRide_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                        else
                        {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }
                        break;
                    case 3:
                        if(isInternetPresent)
                        {
                            Intent emergency_intent = new Intent(NavigationDrawer.this, CabilyMoney.class);
                            startActivity(emergency_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                        else
                        {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }
                        break;
                    case 4:
                        if(isInternetPresent)
                        {
                            Intent invite_intent = new Intent(NavigationDrawer.this, InviteAndEarn.class);
                            startActivity(invite_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                        else
                        {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }
                        break;
                    case 5:
                        if(isInternetPresent)
                        {
                            Intent emergency_intent = new Intent(NavigationDrawer.this, RateCard.class);
                            startActivity(emergency_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                        else
                        {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }
                        break;
                    case 6:
                        if(isInternetPresent)
                        {
                            Intent emergency_intent = new Intent(NavigationDrawer.this, EmergencyContact.class);
                            startActivity(emergency_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                        else
                        {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }
                        break;
                    case 7:
            ///            sendEmail();

                        Intent feedback_intent = new Intent(NavigationDrawer.this, FeedBackPage.class);
                        startActivity(feedback_intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                        break;
                    case 8:
                        Intent about_intent = new Intent(NavigationDrawer.this, AboutUs.class);
                        startActivity(about_intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;
                }
                ft.commit();
                mDrawerList.setItemChecked(position, false);
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

        final PkDialog mDialog = new PkDialog(NavigationDrawer.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();

    }

    //----------Method to Send Email--------
    protected void sendEmail()
    {
        SappInfo_Session = new AppInfoSessionManager(NavigationDrawer.this);
        HashMap<String, String> appInfo = SappInfo_Session.getAppInfo();
        String toAddress = appInfo.get(AppInfoSessionManager.KEY_CONTACT_EMAIL);

        String[] TO = {toAddress};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Message");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(NavigationDrawer.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    public static void navigationNotifyChange() {
        try {
            mMenuAdapter.notifyDataSetChanged();
        }
        catch (Exception e)
        {
            e.printStackTrace();
          //  Toast.makeText(getApplicationContext(),"Something happened , try again",Toast.LENGTH_LONG).show();
        }
    }

   /* public void shareToGMail()
    {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"info@zoplay.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        final PackageManager pm = NavigationDrawer.this.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for(final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        NavigationDrawer.this.startActivity(emailIntent);
    }*/
/*
*/

    private void showBackPressedDialog(final boolean isLogout) {
        System.gc();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_app_exiting)
                .setPositiveButton(getResources().getString(R.string.navigation_drawer_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                            finishAffinity();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            NavigationDrawer.this.finish();
                            System.exit(0);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                })
                .setNegativeButton(getResources().getString(R.string.navigation_drawer_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        drawerState = true;
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        drawerState = false;
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }
}
