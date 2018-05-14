package com.cabily.cabilydriver;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.CurrencySymbolConverter;
import com.cabily.cabilydriver.Utils.EmojiExcludeFilter;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.subclass.SubclassActivity;
import com.cabily.cabilydriver.widgets.PkDialog;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by user88 on 11/5/2015.
 */
public class OtpPage extends SubclassActivity {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
          EditText Et_otp;
         Button BT_otp_confirm;

      Dialog dialog;
    private ServiceRequest mRequest;

    StringRequest postrequest;

    String Str_otp="",Str_amount="";
    private  String Str_rideId="",driver_id="";

    private String sCurrencySymbol="";
    CheckBox chkbox;
         ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_page);
        initialize();

        //Starting Xmpp service
        //FEB 1
                        /*if (!ChatingService.isConnected) {
                            ChatingService.startDriverAction(getActivity());
                        }*/
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        BT_otp_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!chkbox.isChecked()) {


                    if ((Et_otp.getText().toString().trim().length() > 0)&&(Et_otp.getText().toString().trim().equals(Str_otp))) {
                        Intent intent = new Intent(OtpPage.this, PaymentPage.class);
                        intent.putExtra("amount", Str_amount);
                        intent.putExtra("rideid", Str_rideId);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        Toast.makeText(OtpPage.this, getResources().getString(R.string.Enter_OTP), Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Intent intent = new Intent(OtpPage.this, PaymentPage.class);
                    intent.putExtra("amount", Str_amount);
                    intent.putExtra("rideid", Str_rideId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

            }
        });
        Et_otp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_otp);
                }
                return false;
            }
        });

    }
    @Override
    public void onBackPressed() {
           super.onBackPressed();
      //  showBackPressedDialog();
    }
    private void initialize() {
        session = new SessionManager(OtpPage.this);

        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);
        Et_otp = (EditText)findViewById(R.id.otp_enter_code);
        BT_otp_confirm = (Button)findViewById(R.id.otp_request_btn);
        chkbox = (CheckBox) findViewById(R.id.otp_receive_status);
        back = (ImageView) findViewById(R.id.payment_otp_back_img);
        Intent i = getIntent();
        Str_rideId = i.getStringExtra("rideid");

        System.out.println("inside-------------"+Str_rideId);

       // Et_otp.setSelection(Et_otp.getText().length());

        Et_otp.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_otp.addTextChangedListener(loginEditorWatcher);

        Et_otp.setFocusable(true);

        cd = new ConnectionDetector(OtpPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent){

                PostRequest(ServiceConstant.receivecash_url);

            System.out.println("end------------------" +ServiceConstant.receivecash_url);
        }else {

            Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
        }

    }
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
            if (Et_otp.getText().length() > 0) {
                Et_otp.setError(null);
            }


        }
    };



    //--------------Alert Method------------------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(OtpPage.this);
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


    //-----------------------Code for arrived post request-----------------
    private void PostRequest(String Url) {
        dialog = new Dialog(OtpPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------otp----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id",driver_id);
        jsonParams.put("ride_id",Str_rideId);

        System.out.println("otp-------driver_id---------"+driver_id);

        System.out.println("otp-------ride_id---------"+Str_rideId);
        mRequest = new ServiceRequest(OtpPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                Log.e("otp", response);

                System.out.println("otp---------"+response);

                String Str_status = "",Str_response="",Str_otp_status="",Str_ride_id="",Str_currency="";

                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")){

                        Str_response = object.getString("response");
                        Str_currency = object.getString("currency");
                        //    Currency currencycode = Currency.getInstance(getLocale(Str_currency));

                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Str_currency);

                        Str_otp_status = object.getString("otp_status");
                        Str_otp  = object.getString("otp");
                        Str_ride_id = object.getString("ride_id");
                        Str_amount = sCurrencySymbol + object.getString("amount");

                        System.out.println("otp--------"+Str_otp);

                        System.out.println("Str_otp_status--------"+Str_otp_status);

                    }else{

                        Str_response = object.getString("response");
                    }
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();


                if (Str_status.equalsIgnoreCase("1")){

                    if (Str_otp_status.equalsIgnoreCase("development")){
                        Et_otp.setText(Str_otp);
                    }
                    else
                    {
                        Et_otp.setFocusable(true);
                    }

                }else{
                    Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);  }
            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();
            }
        });

    }


/*
    private void PostRequest1(String Url) {
        dialog = new Dialog(OtpPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        System.out.println("loadin-----------");
        TextView dialog_title=(TextView)dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));


        postrequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("otp", response);

                        System.out.println("otp---------"+response);

                        String Str_status = "",Str_response="",Str_otp_status="",Str_ride_id="",Str_currency="";

                        try {
                            JSONObject object = new JSONObject(response);
                            Str_status = object.getString("status");

                            if (Str_status.equalsIgnoreCase("1")){

                                Str_response = object.getString("response");
                                Str_currency = object.getString("currency");
                            //    Currency currencycode = Currency.getInstance(getLocale(Str_currency));

                                sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Str_currency);

                                Str_otp_status = object.getString("otp_status");
                                Str_otp  = object.getString("otp");
                                Str_ride_id = object.getString("ride_id");
                                Str_amount = sCurrencySymbol + object.getString("amount");

                                System.out.println("otp--------"+Str_otp);

                                System.out.println("Str_otp_status--------"+Str_otp_status);

                            }else{

                                Str_response = object.getString("response");

                            }



                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        dialog.dismiss();


                        if (Str_status.equalsIgnoreCase("1")){

                            if (Str_otp_status.equalsIgnoreCase("development")){
                                Et_otp.setText(Str_otp);
                            }

                        }else{
                            Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);  }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorResponse.VolleyError(OtpPage.this, error);
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-agent", ServiceConstant.useragent);
                headers.put("isapplication",ServiceConstant.isapplication);
                headers.put("applanguage",ServiceConstant.applanguage);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("driver_id",driver_id);
                jsonParams.put("ride_id",Str_rideId);

                System.out.println("otp-------driver_id---------"+driver_id);

                System.out.println("otp-------ride_id---------"+Str_rideId);



                return jsonParams;
            }
        };
        postrequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        postrequest.setShouldCache(false);

        AppController.getInstance().addToRequestQueue(postrequest);
    }
*/



    //method to convert currency code to currency symbol
    private static Locale getLocale(String strCode) {

        for (Locale locale : NumberFormat.getAvailableLocales()) {
            String code = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
            if (strCode.equals(code)) {
                return locale;
            }
        }
        return null;
    }




}
