package com.cabily.cabilydriver;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.dao.LoginDetails;
import com.app.gcm.GCMIntializer;
import com.app.service.ServiceConstant;
import com.app.service.ServiceManager;
import com.app.service.ServiceRequest;
import com.app.xmpp.XmppService;
import com.cabily.cabilydriver.Utils.ChatAvailabilityCheck;
import com.cabily.cabilydriver.Utils.EmojiExcludeFilter;
import com.cabily.cabilydriver.Utils.GPSTracker;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.widgets.PkDialog;
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

import java.util.HashMap;
import java.util.Locale;

public class LoginPage extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private String android_id;
    private SessionManager session;
    private RelativeLayout email_layout, password_layout;
    private EditText emailid, password;
    private Button signin;
    private String GCM_Id;
    private ActionBar actionBar;
    public static LoginDetails details;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    GPSTracker gps;

    private Animation slideUp;
    private Animation slideLeft;

    private TextView layout_forgotpassword,register;

    private ServiceRequest mRequest;

    private boolean isAppInfoAvailable = false;

    String driver_id="",sLatitude="",sLongitude="",app_identity_name="",Language_code="",driver_image = "",driverName ="";

    private TextView tv_privacy_policy,tv_terms_and_conditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);


        mGoogleApiClient = new GoogleApiClient.Builder(LoginPage.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        initialize();

        layout_forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginPage.this, ForgotPassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginPage.this, RegisterPageWebview.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

       /* tv_terms_and_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServiceConstant.privacy_policy_URL));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);



            }
        });

        tv_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServiceConstant.privacy_policy_URL));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);



            }
        });*/




    }

    private void initialize() {
        android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        session = new SessionManager(this);
        gps = new GPSTracker(LoginPage.this);
        email_layout = (RelativeLayout) findViewById(R.id.email_layout);
        password_layout = (RelativeLayout) findViewById(R.id.password_layout);
        emailid = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signin = (Button) findViewById(R.id.signin_main_button);
        layout_forgotpassword = (TextView) findViewById(R.id.forgot_passwordTv);
        register = (TextView) findViewById(R.id.register_label);

//        tv_privacy_policy = (TextView) findViewById(R.id.policy);
        tv_terms_and_conditions = (TextView) findViewById(R.id.terms);

        String first_text="<font color='#0e0e0e'>"+getResources().getString(R.string.login_label_privacypolicy)+" "+"</font>";
        String second_text="<font color='#e84c3d'>"+getResources().getString(R.string.login_label_Terms_of_service)+" "+"</font>";
        String tired_text="<font color='#0e0e0e'>"+getResources().getString(R.string.login_label_and)+" "+"</font>";
        String fourth_text="<font color='#e84c3d'>"+getResources().getString(R.string.login_label_Privacy_Policy)+"</font>";
        tv_terms_and_conditions.setText(Html.fromHtml(first_text+second_text+tired_text+fourth_text));

        ClickableSpan termsOfServicesClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServiceConstant.privacy_policy_URL));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

        ClickableSpan privacyPolicyClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServiceConstant.privacy_policy_URL));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

        makeLinks(tv_terms_and_conditions, new String[] { getResources().getString(R.string.login_label_Terms_of_service), getResources().getString(R.string.login_label_Privacy_Policy) }, new ClickableSpan[] {
                termsOfServicesClick, privacyPolicyClick
        });


        emailid.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        password.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        slideUp = AnimationUtils.loadAnimation(LoginPage.this, R.anim.slide_up);
        slideLeft = AnimationUtils.loadAnimation(LoginPage.this, R.anim.slide_left);

        signin.setOnClickListener(this);
        showDialog(getResources().getString(R.string.lablesigningin_Textview));

        GCMIntializer initializer = new GCMIntializer(LoginPage.this, new GCMIntializer.CallBack() {
            @Override
            public void onRegisterComplete(String id) {
                GCM_Id = id;
                dismissDialog();
            }

            @Override
            public void onError(String errorMsg) {
                dismissDialog();
            }
        });
        initializer.init();

        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);
        if (gps.isgpsenabled() && gps.canGetLocation()) {
            //do nothing

            sLatitude = String.valueOf(gps.getLatitude());
            sLongitude = String.valueOf(gps.getLongitude());
        } else {
            enableGpsService();
        }


    }


    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);

            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }


    private void slideLeft() {
        signin.startAnimation(slideLeft);
        signin.setVisibility(View.INVISIBLE);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(LoginPage.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    //Enabling Gps Service
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
                            status.startResolutionForResult(LoginPage.this, REQUEST_LOCATION);
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
    public void onClick(View v) {
        if (v == signin) {
            final String pass = password.getText().toString().trim();
            final String email = emailid.getText().toString().replace(" ","").trim();

            emailid.setText(email);
            password.setText(pass);

            if (email.trim().length()==00) {
                emailid.setError(getResources().getString(R.string.action_alert_empty_email));
            }
            else if (!isValidEmail(email)) {
                emailid.setError(getResources().getString(R.string.action_alert_invalid_email));
            } else if (pass.length() == 0) {
                password.setError(getResources().getString(R.string.action_alert_invalid_password));
            } else {
                gps = new GPSTracker(LoginPage.this);
                if (gps.canGetLocation() && gps.isgpsenabled()) {

                    showDialog(getResources().getString(R.string.lablesigningin_Textview));

                 //   postRequest_applaunch(ServiceConstant.app_launching_url);

                    postRequest(LOGIN_URL);



                } else {
                    enableGpsService();
                }

            }
        }
    }
    public  boolean isValidEmail(CharSequence target) {
        return  android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

   /* private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z ]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }*/

    // validating password with retype password
    public boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 5) {
            return true;
        }
        return false;
    }

    private void postRequest(final String url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email", emailid.getText().toString().replace(" ","").trim());
        jsonParams.put("password", password.getText().toString());
        jsonParams.put("gcm_id", GCM_Id);
        jsonParams.put("latitude", sLatitude);
        jsonParams.put("longitude", sLongitude);

        System.out.println("-------------LoginPage  jsonParams----------------" + jsonParams);


        ServiceManager manager = new ServiceManager(this, mServiceListener);
        manager.makeServiceRequest(url, Request.Method.POST, jsonParams);
    }

    private ServiceManager.ServiceListener mServiceListener = new ServiceManager.ServiceListener() {
        @Override
        public void onCompleteListener(Object res) {

            System.out.println("loginresponse-------------------" + res);
            dismissDialog();
            String status = "", driver_img = "", driver_id = "", driver_name = "", email = "", vehicle_number = "", vehicle_model = "", key = "", isalive = "",gcmid="";
            String sec_key = "",lang_code="";
            if (res instanceof LoginDetails) {
                LoginDetails details = (LoginDetails) res;
                LoginPage.details = details;
                status = details.getStatus();
                driver_img = details.getDriverImage();
                driver_id = details.getDriverId();
                driver_name = details.getDriverName();
                email = details.getEmail();
                vehicle_number = details.getVehicleNumber();
                vehicle_model = details.getVehicleModel();
                sec_key = details.getSec_key();
                key = details.getKey();
             //   lang_code= details.getlang_key();
                isalive = details.getIs_alive_other();

                System.out.println("key--------------" + sec_key);

                System.out.println("driverid--------------" + driver_id);

                System.out.println("--------gcm id-------------"+key);

            }
            if (status.equalsIgnoreCase("1")) {



                session.createLoginSession(driver_img, driver_id, driver_name, email, vehicle_number, vehicle_model, key, sec_key,key);
                session.setUserVehicle(vehicle_model);

                if (isalive.equalsIgnoreCase("Yes")) {

                    HomePage.homePage.finish();

                    final PkDialog mDialog = new PkDialog(LoginPage.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.app_name));
                    mDialog.setDialogMessage(getResources().getString(R.string.alert_multiple_login));
                    mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            postRequest_applaunch(ServiceConstant.app_launching_url);
                           /* ChatingService.startDriverAction(LoginPage.this);
                            Intent i = new Intent(LoginPage.this, NavigationDrawer.class);
                            slideLeft();
                            startActivity(i);
                            finish();*/

                        }
                    });
                    mDialog.show();
                }else
                {
                    postRequest_applaunch(ServiceConstant.app_launching_url);
                    /*ChatingService.startDriverAction(LoginPage.this);
                    Intent i = new Intent(LoginPage.this, NavigationDrawer.class);
                    slideLeft();
                    startActivity(i);
                    finish();*/
                }
            }
        }

        @Override
        public void onErrorListener(Object obj) {
            dismissDialog();
            if (obj instanceof LoginDetails) {
                LoginDetails details = (LoginDetails) obj;
                String status = details.getStatus();
                String response = details.getResponse();

                if (status.equalsIgnoreCase("0")) {
                    Alert(getResources().getString(R.string.action_alert_SigninFaild), response);
                }
            }
        }
    };

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private void postRequest_applaunch(String Url) {

        System.out.println("-------------Splash App Information Url----------------" + Url);
        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "driver");
        jsonParams.put("id", driver_id);
        jsonParams.put("latitude", sLatitude);
        jsonParams.put("longitude", sLongitude);

        System.out.println("-------------LoginPage App Information jsonParams----------------" + jsonParams);


        mRequest = new ServiceRequest(LoginPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Splash App Information Response----------------" + response);



                String Str_status = "", sContact_mail = "",About_Content="" ,customer_service_number="",customer_service_address="", sCustomerServiceNumber = "", sSiteUrl = "", sXmppHostUrl = "", sHostName = "", sFacebookId = "", sGooglePlusId = "", sPhoneMasking = "";
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
                                app_identity_name= info_object.getString("app_identity_name");
                                About_Content= info_object.getString("about_content");
                              /*  sFacebookId = info_object.getString("facebook_app_id");
                                sGooglePlusId = info_object.getString("google_plus_app_id");
                                sPhoneMasking = info_object.getString("phone_masking_status");*/
                                driver_image = info_object.getString("driver_image");
                                driverName = info_object.getString("driver_name");
                                Language_code= info_object.getString("lang_code");

                                if(info_object.has("customer_service_number")) {
                                    customer_service_number = info_object.getString("customer_service_number");
                                }
                                if(info_object.has("site_contact_address")) {
                                    customer_service_address = info_object.getString("site_contact_address");
                                }


                                /*server_mode = info_object.getString("server_mode");
                                site_mode = info_object.getString("site_mode");
                                site_string = info_object.getString("site_mode_string");
                                site_url = info_object.getString("site_url");*/
                                if (info_object.has("phone_masking_status")) {
                                    String phoneMaskingStatus = info_object.getString("phone_masking_status");
                                 //   PHONEMASKINGSTATUS = phoneMaskingStatus;
                                    // set phone masking
                                    session.setKeyPhoneMaskingStatus(phoneMaskingStatus);
                                    System.out.println("=====>>>===PHONEMASKINGSTATUS ==========>>>>> " + phoneMaskingStatus);
                                }
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
                        HashMap<String, String> language = session.getLanaguage();
                        Locale locale = null;

                        switch (Language_code){

                            case "en":
                                locale = new Locale("en");
                                session.setlamguage("en","en");
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
                                session.setlamguage("es","es");
                                //     System.out.println("========Arabic Language========"+language_change.getSelectedItem().toString()+"\t\tar");
                                //     Intent i=new Intent(ProfilePage.this,NavigationDrawer.class);
                                //     finish();
                                //     startActivity(i);

//                        Intent bii = new Intent();
//                        bii.setAction("homepage");
//                        sendBroadcast(bii);
//                        finish();
                                break;
                            case "ta":
                                locale = new Locale("ta");
                                session.setlamguage("ta","ta");
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
                                session.setlamguage("en","en");
                                break;
                        }

                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());
                        session.setcustomerdetail(customer_service_number,customer_service_address);
                        session.setXmpp(sXmppHostUrl,sHostName);
                        session.setAgent(app_identity_name);

                        session.setaboutus(About_Content,sSiteUrl);
                        session.setAgent(app_identity_name);
                        session.setdriver_image(driver_image);
                        session.setdriverNameUpdate(driverName);

                        postRequest_SetUserLocation(ServiceConstant.UPDATE_CURRENT_LOCATION);
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
                        Toast.makeText(LoginPage.this, "BAD URL", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(LoginPage.this, ServiceConstant.baseurl, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postRequest_SetUserLocation(String Url) {

        System.out.println("----------sLatitude----------" + sLatitude);
        System.out.println("----------sLongitude----------" + sLongitude);
        System.out.println("----------driver_id----------" + driver_id);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);
        jsonParams.put("latitude", sLatitude);
        jsonParams.put("longitude", sLongitude);

        System.out.println("-------------Splash UserLocation Url----------------" + Url);
        mRequest = new ServiceRequest(LoginPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Splash UserLocation Response----------------" + response);

                String Str_status = "", sCategoryID = "", sTripProgress = "", sRideId = "", sRideStage = "";
                try {
                    JSONObject object = new JSONObject(response);
                 /*   Str_status = object.getString("status");
                    sCategoryID = object.getString("category_id");
                    sTripProgress = object.getString("trip_in_progress");
                    sRideId = object.getString("ride_id");
                    sRideStage = object.getString("ride_stage");*/

                   /* if (Str_status.equalsIgnoreCase("1")) {
                        session.setCategoryID(sCategoryID);
                        session.setRidePendingStatus(sTripProgress,sRideId,sRideStage);
                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HomePage.homePage.finish();

                ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(LoginPage.this, "available");
                chatAvailability.postChatRequest();

                //FEB 1
                        /*if (!ChatingService.isConnected) {
                            ChatingService.startDriverAction(getActivity());
                        }*/

                if (!isMyServiceRunning(XmppService.class)) {
                    startService(new Intent(LoginPage.this, XmppService.class));
                }

                session.setXmppServiceState("online");
                Intent i = new Intent(getApplicationContext(), NavigationDrawerNew.class);
                slideLeft();
                startActivity(i);
                finish();
               /* Intent i = new Intent(HomePage.this, NavigationDrawer.class);
                startActivity(i);
                finish();*/
                // overridePendingTransition(R.anim.enter, R.anim.exit);

            }

            @Override
            public void onErrorListener() {

                HomePage.homePage.finish();
                //FEB 1
                        /*if (!ChatingService.isConnected) {
                            ChatingService.startDriverAction(getActivity());
                        }*/
                if (!isMyServiceRunning(XmppService.class)) {
                    startService(new Intent(LoginPage.this, XmppService.class));
                }

                session.setXmppServiceState("online");

                Intent i = new Intent(getApplicationContext(), NavigationDrawerNew.class);
                slideLeft();
                startActivity(i);
                finish();
                /*Intent i = new Intent(HomePage.this, NavigationDrawer.class);
                startActivity(i);
                finish();*/
                //   overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        boolean b = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                System.out.println("1 already running");
                b = true;
                break;
            } else {
                System.out.println("2 not running");
                b = false;
            }
        }
        System.out.println("3 not running");
        return b;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                session = new SessionManager(getApplicationContext());
                                gps = new GPSTracker(LoginPage.this);

                                HashMap<String, String> user = session.getUserDetails();
                                driver_id = user.get(SessionManager.KEY_DRIVERID);

                                sLatitude = String.valueOf(gps.getLatitude());
                                sLongitude = String.valueOf(gps.getLongitude());
                              //  postRequest_applaunch(ServiceConstant.app_launching_url);
                                //  postRequest_AppInformation(Iconstant.app_info_url);
                                //   postRequest_SetUserLocation(Iconstant.setUserLocation);

                            }
                        }, 2000);
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
