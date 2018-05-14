package com.cabily.cabilydriver;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.EmojiExcludeFilter;
import com.cabily.cabilydriver.widgets.PkDialog;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user88 on 12/21/2015.
 */
public class ForgotPassword extends ActionBarActivity {

    private EditText Et_email;
    private RelativeLayout Rl_layout_email_send, layout_forgot_pwd_back;
    private Dialog dialog;
    private StringRequest postrequest;
    private ServiceRequest mRequest;

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        initilize();

        Rl_layout_email_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(ForgotPassword.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    if(Et_email.getText().toString().replace(" ","").trim().length()>0) {
                        if(isValidEmail(Et_email.getText().toString())) {
                            forgotpassword_PostRequest(ServiceConstant.forgotpassword);
                            System.out.println("forgotpwd-----------" + ServiceConstant.forgotpassword);
                        }
                        else
                        {
                            Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.action_alert_invalid_email));
                        }
                    }
                    else {
                        Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.forgotpassword));
                    }
                } else {

                    Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });


        layout_forgot_pwd_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }


    private void initilize() {

        Et_email = (EditText) findViewById(R.id.editText_email_forgotpwd);
        Rl_layout_email_send = (RelativeLayout) findViewById(R.id.settings_forgotpwd_button);
        layout_forgot_pwd_back = (RelativeLayout) findViewById(R.id.layout_forgotpwd_back);

        Et_email.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }


    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(ForgotPassword.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
               // onBackPressed();
            }
        });
        mDialog.show();
    }

    public  boolean isValidEmail(CharSequence target) {
        return  android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    private void alertBack(String title, String message) {
        final PkDialog mDialog = new PkDialog(ForgotPassword.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                onBackPressed();
            }
        });
        mDialog.show();
    }



    //--------------------------code for post forgot password-----------------------
    private void forgotpassword_PostRequest(String Url) {
        dialog = new Dialog(ForgotPassword.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------forgotpwd----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email", Et_email.getText().toString().replace(" ","").trim());

        System.out.println("--------------email-------------------" + Et_email.getText().toString());

        mRequest = new ServiceRequest(ForgotPassword.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                Log.e("forgotpwd", response);

                System.out.println("forgotpwdresponse---------" + response);

                String Str_status = "", Str_response = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    Str_response = object.getString("response");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Str_status.equalsIgnoreCase("1")) {
                    Alert(getResources().getString(R.string.label_pushnotification_cashreceived), Str_response);

                } else {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });

    }

/*

    private void forgotpassword_PostRequest1(String Url) {
        dialog = new Dialog(ForgotPassword.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        System.out.println("loadin-----------");
        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));


        postrequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("forgotpwd", response);

                        System.out.println("forgotpwdresponse---------" + response);

                        String Str_status = "", Str_response = "";

                        try {
                            JSONObject object = new JSONObject(response);
                            Str_status = object.getString("status");
                            Str_response = object.getString("response");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        if (Str_status.equalsIgnoreCase("1")) {
                            Alert(getResources().getString(R.string.label_pushnotification_cashreceived), Str_response);

                        } else {
                            Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);
                        }

                        dialog.dismiss();


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorResponse.VolleyError(ForgotPassword.this, error);
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
                jsonParams.put("email", Et_email.getText().toString());

                System.out.println("--------------email-------------------" + Et_email.getText().toString());

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


}
