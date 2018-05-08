package com.cabily.app;

/**
 * Created by Prem Kumar on 10/1/2015.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Request;
import com.cabily.HockeyApp.FragmentActivityHockeyApp;
import com.cabily.iconstant.Iconstant;
import com.cabily.utils.AppInfoSessionManager;
import com.cabily.utils.ConnectionDetector;
import com.cabily.utils.CountryDialCode;
import com.cabily.utils.CurrencySymbolConverter;
import com.cabily.utils.EmojiExcludeFilter;
import com.cabily.utils.SessionManager;
import com.casperon.app.cabily.R;
import com.countrycodepicker.CountryPicker;
import com.countrycodepicker.CountryPickerListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.facebook.AsyncFacebookRunner;
import com.mylibrary.facebook.DialogError;
import com.mylibrary.facebook.Facebook;
import com.mylibrary.facebook.FacebookError;
import com.mylibrary.facebook.Util;
import com.mylibrary.gps.GPSTracker;
import com.mylibrary.pushnotification.GCMInitializer;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;


public class RegisterPage extends FragmentActivityHockeyApp  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {
    private RelativeLayout back;
    private EditText Eusername, Epassword, Eemail, EphoneNo, Ereferalcode,Econfirm_pss;
    private TextView submit,tv_signin;
    private ImageView help;
    private TextInputLayout Rl_countryCode;
    private TextView Tv_countryCode;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private Context context;
    RelativeLayout facebooklayout;
    private ServiceRequest mRequest;
    Dialog dialog;
    Handler mHandler;
    //------------------GCM Initialization------------------
    private GoogleCloudMessaging gcm;
    private String GCM_Id = "";
    GPSTracker gps;
    CountryPicker picker;
    private GPSTracker gpsTracker;

    private TextView Tv_or,Tv_privacy_policy;
    AppInfoSessionManager appInfo_Session;
    private String Str_FacebookId = "";

    private Facebook facebook;
    AsyncFacebookRunner mAsyncRunner;
    private SharedPreferences mPrefs;
    private String sMediaId = "";
    private String email = "", profile_image = "", username1 = "", userid = "", Language_code = "";
    private String sCurrencySymbol = "";
    private SessionManager session;
    final static int REQUEST_LOCATION = 199;
    public static RegisterPage registerPageClass;
    BroadcastReceiver logoutReciver;
    private String userID = "", sLatitude = "", sLongitude = "";
    private TextView tv_privacy_policy,tv_terms_and_conditions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_new);
        context = getApplicationContext();
        registerPageClass = RegisterPage.this;


        IntentFilter filter = new IntentFilter();
        filter.addAction("com.app.register.finish");
        logoutReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();

            }
        };
        registerReceiver(logoutReciver, filter);

        mGoogleApiClient = new GoogleApiClient.Builder(RegisterPage.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();


        initialize();

        /*help.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Referral_information();
            }
        });*/

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        Rl_countryCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        Tv_countryCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        tv_signin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterPage.this, LoginPage.class);
                startActivity(i);
                finish();

               // picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                Tv_countryCode.setText(dialCode);

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Rl_countryCode.getWindowToken(), 0);
            }
        });

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Epassword.setText(Epassword.getText().toString().trim());

                if (Eemail.getText().toString().trim().length() == 0) {
                    erroredit(Eemail, getResources().getString(R.string.register_label_alert_email_register));
                } else if (!isValidEmail(Eemail.getText().toString().replace(" ", ""))) {
                    erroredit(Eemail, getResources().getString(R.string.register_label_alert_email));
                }
                else  if (Epassword.getText().toString().length() == 0) {
                    erroredit(Epassword, getResources().getString(R.string.register_label_alertpassword_register));
                }else if (!isValidPassword(Epassword.getText().toString())) {
                    erroredit(Epassword, getResources().getString(R.string.register_label_alert_password));
                } else if (Eusername.getText().toString().trim().length() == 0) {
                    erroredit(Eusername, getResources().getString(R.string.register_label_alert_username));
                } else if (!isValidPhoneNumber(EphoneNo.getText().toString())) {
                    erroredit(EphoneNo, getResources().getString(R.string.register_label_alert_phoneNo));
                } else if (Tv_countryCode.getText().toString().equalsIgnoreCase("code")) {
                    erroredit(EphoneNo, getResources().getString(R.string.register_label_alert_country_code));
                }
                else if (!Epassword.getText().toString().equals(Econfirm_pss.getText().toString())) {
                    erroredit(Econfirm_pss, getResources().getString(R.string.register_label_alert_pass_not_match));
                }

                else {

                    cd = new ConnectionDetector(RegisterPage.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {

                        mHandler.post(dialogRunnable);

                        //---------Getting GCM Id----------
                        GCMInitializer initializer = new GCMInitializer(RegisterPage.this, new GCMInitializer.CallBack() {
                            @Override
                            public void onRegisterComplete(String registrationId) {

                                GCM_Id = registrationId;

                                if (gps.isgpsenabled() && gps.canGetLocation()) {

                                    sLatitude = String.valueOf(gps.getLatitude());
                                    sLongitude = String.valueOf(gps.getLongitude());
                                    PostRequest(Iconstant.register_url);

                                }
                                else
                                {
                                    Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.alert_gpsEnable));
                                }



                            }

                            @Override
                            public void onError(String errorMsg) {
                                PostRequest(Iconstant.register_url);
                            }
                        });
                        initializer.init();

                    } else {
                        Alert(getResources().getString(R.string.alert_nointernet), getResources().getString(R.string.alert_nointernet_message));
                    }
                }
            }
        });


        Eusername.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Eusername);
                }
                return false;
            }
        });


        Epassword.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Epassword);
                }
                return false;
            }
        });

        Eemail.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Eemail);
                }
                return false;
            }
        });


        EphoneNo.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(EphoneNo);
                }
                return false;
            }
        });
        Ereferalcode.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Ereferalcode);
                }
                return false;
            }
        });
      /*  tv_terms_and_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Iconstant.privacy_policy_url));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);



            }
        });

        tv_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Iconstant.privacy_policy_url));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);



            }
        });*/
        facebooklayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutFromFacebook();
                loginToFacebook();
            }
        });

    }

    private void initialize() {
        cd = new ConnectionDetector(RegisterPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        mHandler = new Handler();
        picker = CountryPicker.newInstance("Select Country");
        session = new SessionManager(RegisterPage.this);
        Tv_or = (TextView) findViewById(R.id.login_or_label);
        appInfo_Session = new AppInfoSessionManager(RegisterPage.this);
        HashMap<String, String> user = appInfo_Session.getAppInfo();
        Str_FacebookId = user.get(AppInfoSessionManager.KEY_FACEBOOK_ID);
        facebook = new Facebook(Str_FacebookId);
        facebooklayout = (RelativeLayout) findViewById(R.id.login_facebook_button);
        gps = new GPSTracker(getApplicationContext());
        System.out.println("Str_FacebookId--jai----------" + Str_FacebookId);


        back = (RelativeLayout) findViewById(R.id.register_header_back_layout);
        Eusername = (EditText) findViewById(R.id.register_username_editText);
        Epassword = (EditText) findViewById(R.id.register_password_editText);
        Eemail = (EditText) findViewById(R.id.register_email_editText);
        EphoneNo = (EditText) findViewById(R.id.register_phone_editText);
        Ereferalcode = (EditText) findViewById(R.id.register_referalcode_editText);
        Econfirm_pss = (EditText) findViewById(R.id.register_con_password_editText);
      ///  help = (ImageView) findViewById(R.id.register_referalcode_help_image);
        Rl_countryCode = (TextInputLayout) findViewById(R.id.signup_input_layout_code);
        Tv_countryCode = (TextView) findViewById(R.id.register_country_code_textview);
        submit = (TextView) findViewById(R.id.register_submit_button);
        tv_signin= (TextView) findViewById(R.id.login_label);
//        tv_privacy_policy = (TextView) findViewById(R.id.policy);
        tv_terms_and_conditions = (TextView) findViewById(R.id.terms);
        submit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));

        String first_text="<font color='#5b5b5b'>"+getResources().getString(R.string.login_label_privacypolicy)+" "+"</font>";
        String second_text="<font color='#e84c3d'>"+getResources().getString(R.string.login_label_Terms_of_service)+" "+"</font>";
        String tired_text="<font color='#5b5b5b'>"+getResources().getString(R.string.login_label_and)+" "+"</font>";
        String fourth_text="<font color='#e84c3d'>"+getResources().getString(R.string.login_label_Privacy_Policy)+"</font>";
        tv_terms_and_conditions.setText(Html.fromHtml(first_text+second_text+tired_text+fourth_text));

        ClickableSpan termsOfServicesClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Iconstant.privacy_policy_url));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

        ClickableSpan privacyPolicyClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Iconstant.privacy_policy_url));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

        makeLinks(tv_terms_and_conditions, new String[] { getResources().getString(R.string.login_label_Terms_of_service), getResources().getString(R.string.login_label_Privacy_Policy) }, new ClickableSpan[] {
                termsOfServicesClick, privacyPolicyClick
        });

        Eusername.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Epassword.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Eemail.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        EphoneNo.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Ereferalcode.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Econfirm_pss.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        //code to make password editText as dot
        Epassword.setTransformationMethod(new PasswordTransformationMethod());



        Econfirm_pss.setTransformationMethod(new PasswordTransformationMethod());

        Eusername.addTextChangedListener(loginEditorWatcher);
        Epassword.addTextChangedListener(loginEditorWatcher);





        if (gps.isgpsenabled() && gps.canGetLocation()) {

            sLatitude = String.valueOf(gps.getLatitude());
            sLongitude = String.valueOf(gps.getLongitude());

        }
        else
        {
            Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.alert_gpsEnable));
        }
        if (Str_FacebookId != null && Str_FacebookId.length() > 0) {
            facebooklayout.setVisibility(View.VISIBLE);
            Tv_or.setVisibility(View.VISIBLE);

        } else {
            facebooklayout.setVisibility(View.GONE);
            Tv_or.setVisibility(View.GONE);


        }


        gpsTracker = new GPSTracker(RegisterPage.this);
        if (gpsTracker.canGetLocation() && gpsTracker.isgpsenabled()) {

            double MyCurrent_lat = gpsTracker.getLatitude();
            double MyCurrent_long = gpsTracker.getLongitude();

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(MyCurrent_lat, MyCurrent_long, 1);
                if (addresses != null && !addresses.isEmpty()) {

                    String Str_getCountryCode = addresses.get(0).getCountryCode();
                    if (Str_getCountryCode.length() > 0 && !Str_getCountryCode.equals(null) && !Str_getCountryCode.equals("null")) {
                        String Str_countyCode = CountryDialCode.getCountryCode(Str_getCountryCode);
                        Tv_countryCode.setText(Str_countyCode);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void Referral_information() {
        final MaterialDialog dialog = new MaterialDialog(RegisterPage.this);
        View view = LayoutInflater.from(this).inflate(R.layout.register_referalcode_dialog, null);

        TextView tv_ok = (TextView) view.findViewById(R.id.referral_code_popup_text_ok);
        tv_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.setView(view).show();
    }

    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(RegisterPage.this);
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

    //----------------------Code for TextWatcher-------------------------
    private final TextWatcher loginEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //clear error symbol after entering text
            if (Eusername.getText().length() > 0) {
                Eusername.setError(null);
            }
            if (Epassword.getText().length() > 0) {
                Epassword.setError(null);
            }
            if (Eemail.getText().length() > 0) {
                Eemail.setError(null);
            }
            if (EphoneNo.getText().length() > 0) {
                EphoneNo.setError(null);
            }
           /* if (Econfirm_pss.getText().length() > 0) {
                Econfirm_pss.setError(null);
            }*/


        }
    };


    //-------------------------code to Check Email Validation-----------------------
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //--------------------Code to set error for EditText-----------------------
    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(RegisterPage.this, R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass.length() < 6) {
            return false;
        }
            /*
             * else if(!pass.matches("(.*[A-Z].*)")) { return false; }
			 */
        /*else if (!pass.matches("(.*[a-z].*)")) {
            return false;
        } else if (!pass.matches("(.*[0-9].*)")) {
            return false;
        }*/
            /*
             * else if(!pass.matches(
			 * "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)")) {
			 * return false; }
			 */
        else {
            return true;
        }

    }

    // validating Phone Number
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 5 || target.length() >= 16) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    //--------Handler Method------------
    Runnable dialogRunnable = new Runnable() {
        @Override
        public void run() {
            dialog = new Dialog(RegisterPage.this);
            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
            dialog_title.setText(getResources().getString(R.string.action_verifying));
        }
    };


    // -------------------------code for Login Post Request----------------------------------
    private void PostRequest(String Url) {

        System.out.println("--------------register url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_name", Eusername.getText().toString());
        jsonParams.put("email", Eemail.getText().toString().replace(" ", "").trim());
        jsonParams.put("password", Epassword.getText().toString());
        jsonParams.put("phone_number", EphoneNo.getText().toString());
        jsonParams.put("country_code", Tv_countryCode.getText().toString());
        jsonParams.put("referal_code", Ereferalcode.getText().toString());
        jsonParams.put("gcm_id", GCM_Id);
        jsonParams.put("lat", sLatitude);
        jsonParams.put("lon", sLongitude);
        System.out.println("--------------register jsonParams-------------------" + jsonParams);

        mRequest = new ServiceRequest(RegisterPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                Log.e("registr", response);

                System.out.println("--------------register reponse-------------------" + response);

                String Sstatus = "", Smessage = "", Sotp_status = "", Sotp = "";

                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Sotp_status = object.getString("otp_status");
                        Sotp = object.getString("otp");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                if (Sstatus.equalsIgnoreCase("1")) {
                    Intent intent = new Intent(context, OtpPage.class);
                    intent.putExtra("sLatitude", sLatitude);
                    intent.putExtra("sLongitude", sLongitude);

                    intent.putExtra("Otp_Status", Sotp_status);
                    intent.putExtra("Otp", Sotp);
                    intent.putExtra("UserName", Eusername.getText().toString());
                    intent.putExtra("Email", Eemail.getText().toString().replace(" ", "").trim());
                    intent.putExtra("Password", Epassword.getText().toString());
                    intent.putExtra("Phone", EphoneNo.getText().toString());
                    intent.putExtra("CountryCode", Tv_countryCode.getText().toString());
                    intent.putExtra("ReferalCode", Ereferalcode.getText().toString());
                    intent.putExtra("GcmID", GCM_Id);

                    System.out.println("gcm---------" + GCM_Id);

                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                } else {
                    Alert(getResources().getString(R.string.login_label_alert_register_failed), Smessage);
                }

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Eusername.getWindowToken(), 0);

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    public void logoutFromFacebook() {
        Util.clearCookies(RegisterPage.this);
        // your sharedPrefrence
        SharedPreferences.Editor editor = context.getSharedPreferences("CASPreferences", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    Runnable dialogFacebookRunnable = new Runnable() {
        @Override
        public void run() {
            dialog = new Dialog(RegisterPage.this);
            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
            dialog_title.setText(getResources().getString(R.string.action_loading));
        }
    };

    public void loginToFacebook() {

        System.out.println("---------------facebook login1-----------------------");
        mPrefs = context.getSharedPreferences("CASPreferences", Context.MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);

        if (access_token != null) {
            facebook.setAccessToken(access_token);
        }


        System.out.println("---------------facebook expires-----------------------" + expires);

        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }

        System.out.println("---------------facebook isSessionValid-----------------------" + facebook.isSessionValid());
        if (!facebook.isSessionValid()) {
            facebook.authorize(RegisterPage.this,
                    new String[]{"email"},
                    new Facebook.DialogListener() {

                        @Override
                        public void onCancel() {
                            // Function to handle cancel event
                        }

                        @Override
                        public void onComplete(Bundle values) {
                            // Function to handle complete event
                            // Edit Preferences and update facebook acess_token
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("access_token",
                                    facebook.getAccessToken());
                            editor.putLong("access_expires", facebook.getAccessExpires());
                            editor.commit();
                            String accessToken = facebook.getAccessToken();

                            mHandler.post(dialogFacebookRunnable);
                            //---------Getting GCM Id----------
                            GCMInitializer initializer = new GCMInitializer(RegisterPage.this, new GCMInitializer.CallBack() {
                                @Override
                                public void onRegisterComplete(String registrationId) {

                                    GCM_Id = registrationId;

                                    //getProfileInformation();

                                    String accessToken1 = facebook.getAccessToken();

                                    System.out.println("----------------------jai----------------------"+"https://graph.facebook.com/me?fields=id,name,picture,email&access_token=" + accessToken1);

                                    JsonRequest("https://graph.facebook.com/me?fields=id,name,picture,email&access_token=" + accessToken1);

                                }

                                @Override
                                public void onError(String errorMsg) {
                                    //getProfileInformation();

                                    String accessToken1 = facebook.getAccessToken();
                                    JsonRequest("https://graph.facebook.com/me?fields=id,name,picture,email&access_token=" + accessToken1);
                                }
                            });
                            initializer.init();
                        }

                        @Override
                        public void onError(DialogError error) {
                            // Function to handle error

                        }

                        @Override
                        public void onFacebookError(FacebookError fberror) {
                            // Function to handle Facebook errors

                        }
                    });
        }
    }

    private void JsonRequest(final String Url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        mRequest = new ServiceRequest(RegisterPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.GET, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------access token reponse-------------------" + response);

                try {

                    JSONObject object = new JSONObject(response);
                    System.out.println("---------facebook profile------------" + response);


                    sMediaId = object.getString("id");
                    userid = object.getString("id");
                    profile_image = "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
                    username1 = object.getString("name");
                    username1 = username1.replaceAll("\\s+", "");


                    if (object.has("email")) {
                        email = object.getString("email");
                    } else {
                        email = "";
                    }
                    System.out.println("-------sMediaId------------------" + sMediaId);
                    System.out.println("-------email------------------" + email);
                    System.out.println("-----------------userid-------------------------------" + userid);
                    System.out.println("----------------profile_image-----------------" + profile_image);
                    System.out.println("-----------username----------" + username1);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //post execute
                dialog.dismiss();

                PostRequest_facebook(Iconstant.social_check_url);
            }

            @Override
            public void onErrorListener() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void PostRequest_facebook(final String Url) {


        final ProgressDialog progress;
        progress = new ProgressDialog(RegisterPage.this);
        progress.setMessage(getResources().getString(R.string.action_pleasewait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(false);
        progress.show();

        System.out.println("-----------media_id 1------------" + sMediaId);
        System.out.println("-----------deviceToken 1------------" + "");
        System.out.println("-----------gcm_id 1------------" + GCM_Id);
        System.out.println("-----------email 1------------" + email);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("media_id", sMediaId);
        jsonParams.put("deviceToken", "");
        jsonParams.put("gcm_id", GCM_Id);
        jsonParams.put("email", email);

        mRequest = new ServiceRequest(RegisterPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Login reponse-------------------" + response);

                String Sstatus = "", Smessage = "", Suser_image = "", Suser_id = "", Suser_name = "",
                        Semail = "", Scountry_code = "", SphoneNo = "", Sreferal_code = "", Scategory = "", SsecretKey = "", SwalletAmount = "", ScurrencyCode = "";

                String gcmId = "";
                String is_alive_other = "";

                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");
                    System.out.println("---------Sstatus--------" + Sstatus);
                    if (Sstatus.equalsIgnoreCase("1")) {
                        Suser_image = object.getString("user_image");
                        Suser_id = object.getString("user_id");
                        Suser_name = object.getString("user_name");
                        Semail = object.getString("email");
                        Scountry_code = object.getString("country_code");
                        SphoneNo = object.getString("phone_number");
                        Sreferal_code = object.getString("referal_code");
                        Scategory = object.getString("category");
                        SsecretKey = object.getString("sec_key");
                        SwalletAmount = object.getString("wallet_amount");

                        gcmId = object.getString("key");
                        is_alive_other = object.getString("is_alive_other");

                        ScurrencyCode = object.getString("currency");
                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (Sstatus.equalsIgnoreCase("1")) {
                    session.createLoginSession(Semail, Suser_id, Suser_name, Suser_image, Scountry_code, SphoneNo, Sreferal_code, Scategory, gcmId);
                    session.createWalletAmount(sCurrencySymbol + SwalletAmount);
                    session.setXmppKey(Suser_id, SsecretKey);

                    //starting XMPP service
                    //     postRequest_AppInformation(Iconstant.app_info_url);
                    //     ChatService.startUserAction(LoginPage.this);
                    SingUpAndSignIn.activty.finish();
                    Intent intent = new Intent(context, UpdateUserLocation.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (Sstatus.equalsIgnoreCase("2")) {

                    Intent intent = new Intent(RegisterPage.this, RegisterFacebook.class);
                    intent.putExtra("userId", userid);
                    intent.putExtra("userName", username1);
                    intent.putExtra("userEmail", email);
                    intent.putExtra("media", sMediaId);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(Eusername.getWindowToken(), 0);

                } else {
                    Alert(getResources().getString(R.string.login_label_alert_signIn_failed), Smessage);
                }

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Eusername.getWindowToken(), 0);
                progress.dismiss();
            }

            @Override
            public void onErrorListener() {
                if (progress != null) {
                    progress.dismiss();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("--------------jai---------------"+requestCode+resultCode+data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(logoutReciver);
    }


    //Enabling Gps Service
    /*private void enableGpsService() {
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
                            status.startResolutionForResult(RegisterPage.this, REQUEST_LOCATION);
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
                        Toast.makeText(SplashPage.this, getResources().getString(R.string.splash_toast_loaction_enable), Toast.LENGTH_LONG).show();
                        if (session.isLoggedIn()) {
                            //starting XMPP service
                            //                         ChatService.startUserAction(SplashPage.this);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    session = new SessionManager(getApplicationContext());
                                    gps = new GPSTracker(SplashPage.this);

                                    HashMap<String, String> user = session.getUserDetails();
                                    userID = user.get(SessionManager.KEY_USERID);
                                    sLatitude = String.valueOf(gps.getLatitude());
                                    sLongitude = String.valueOf(gps.getLongitude());

                                    postRequest_AppInformation(Iconstant.app_info_url);

                                }
                            }, 2000);

                        } else {
                            postRequest_AppInformation(Iconstant.app_info_url);
                        }
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        finish();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }*/


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            RegisterPage.this.finish();
            RegisterPage.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
