/*
package com.cabily.cabilydriver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.Request;
import com.app.cardscanner.CardScanner;
import com.app.service.ServiceConstant;
import com.app.service.ServiceManager;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import java.util.HashMap;
import java.util.Locale;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

*/
/**
 *//*

public class NavigationDrawer extends BaseActivity implements View.OnClickListener {

    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemTripsummary;
    private ResideMenuItem itemBankaccount;
    private ResideMenuItem itemPaymentStatement;
    private ResideMenuItem itemChangepassword;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemLogout;
    private ResideMenuItem itemScanCard;
    private SessionManager session;
    private ActionBar actionBar;
    Locale locale = null;

    */
/**
     *//*

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.drawer_icon);
        session = new SessionManager(NavigationDrawer.this);
        setUpMenu();
        if (savedInstanceState == null)

            changeFragment(new DashBoardDriver());
    }


    public ActionBar getActionBarSupport() {
        return actionBar;
    }

    private void setUpMenu() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background_neww);
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.6f);
        resideMenu.setSwipeDirectionDisable(1);


        // create menu items;
        String trip_summary = getResources().getString(R.string.trip_summary);
        itemHome = new ResideMenuItem(this, R.drawable.icon_home, getResources().getString(R.string.navgation_drawer_home));
        itemTripsummary = new ResideMenuItem(this, R.drawable.icon_profile, "" + trip_summary);
        itemBankaccount = new ResideMenuItem(this, R.drawable.icon_calendar, getResources().getString(R.string.navgation_drawer_bank_account));
        itemPaymentStatement = new ResideMenuItem(this, R.drawable.icon_settings, getResources().getString(R.string.navgation_drawer_payment_details));
        itemChangepassword = new ResideMenuItem(this,R.drawable.password_new,getResources().getString(R.string.navgation_drawer_change_pwd));
//---------- Language Enable-------------
        itemSettings=new ResideMenuItem(this, R.drawable.change_language_ic, getResources().getString(R.string.navgation_drawer_change_language));
        itemLogout = new ResideMenuItem(this, R.drawable.logout, getResources().getString(R.string.navgation_drawer_logout));
        //itemScanCard= new ResideMenuItem(this, R.drawable.icon_settings, "Scan Card");
        itemHome.setOnClickListener(this);
        itemTripsummary.setOnClickListener(this);
        itemBankaccount.setOnClickListener(this);
        itemPaymentStatement.setOnClickListener(this);
        //---------- Language Enable-------------
        itemSettings.setOnClickListener(this);
        itemChangepassword.setOnClickListener(this);
        itemLogout.setOnClickListener(this);
        //itemScanCard.setOnClickListener(this);


        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemTripsummary, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemBankaccount, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemPaymentStatement, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemChangepassword,ResideMenu.DIRECTION_LEFT);
        //---------- Language Enable-------------
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);
        //resideMenu.addMenuItem(itemScanCard,ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemLogout, ResideMenu.DIRECTION_LEFT);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showBackPressedDialog(false);
    }

    private void showBackPressedDialog(final boolean isLogout) {
        System.gc();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_app_exiting)
                .setPositiveButton(getResources().getString(R.string.navigation_drawer_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(isLogout){
                            logout();
                        }
                        else {
                            finishAffinity();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            NavigationDrawer.this.finish();
                            System.exit(0);
                        }

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {
        if (R.id.ic_menu == view.getId()) {
            resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
        } else if (view == itemHome) {
            changeFragment(new DashBoardDriver());
        } else if (view == itemTripsummary) {
            changeFragment(new TripSummeryList());
        } else if (view == itemBankaccount) {
            changeFragment(new BankDetails());
        } else if (view == itemPaymentStatement) {
            changeFragment(new PaymentDetails());
        }else if(view == itemChangepassword) {
            changeFragment(new ChangePassWord());
        }
        //---------- Language Enable-------------
            else if(view == itemSettings){
                changeFragment(new SettingsLanguageChange());
        }

        else if (view == itemLogout) {
            showBackPressedDialog(true);
        } else if (view == itemScanCard) {
            CardScanner cardScanner = new CardScanner(this);
            cardScanner.startScanActivityResult();
        }
        resideMenu.closeMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CardScanner.MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr = "";
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }
                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }
                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }else{
                resultDisplayStr = "Scan was canceled.";
            }
        }
    }

    private void logout() {
       // session.logoutUser();
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
            session.logoutUser();
            locale = new Locale("en");
         //   session.setlamguage("en","English");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            dismissDialog();
        }

        @Override
        public void onErrorListener(Object error) {
            dismissDialog();
        }
    };

    private void changeFragment(Fragment targetFragment) {
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu() {
        return resideMenu;
    }
}


*/
