package com.cabily.cabilydriver;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.Pojo.UserPojo;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.RoundedImageView;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.subclass.SubclassActivity;
import com.cabily.cabilydriver.widgets.PkDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user88 on 10/30/2015.
 */
public class UserInfo extends SubclassActivity {

    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private SessionManager session;

    private TextView Tv_userName, Tv_username_header;
    private ImageView call_image;
    private RatingBar ratingBar;
    private RelativeLayout Rl_layout_back;
    private Button Bt_canceltrip;
    private RoundedImageView userimage;

    private String Str_username = "", Str_userrating = "", Str_usermobilno = "", Str_rideId = "" , btnGroup="";
    private String Str_user_img = "";

    public static UserInfo userInfo_class;

    final int PERMISSION_REQUEST_CODE = 111;
    final int PERMISSION_REQUEST_NAVIGATION_CODE = 222;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private Dialog cantact_dialog,sms_popup;
    private ServiceRequest mRequest;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        userInfo_class = UserInfo.this;
        initialize();
        Rl_layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        Bt_canceltrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfo.this, CancelTrip.class);
                intent.putExtra("RideId", Str_rideId);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        call_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        chooseContactOptions(Str_rideId,Str_usermobilno);



            }
        });



    }

    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(UserInfo.this);
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
    private void chooseContactOptions(final String ride_id, final String Str_user_phoneno)
    {
        cantact_dialog = new Dialog(UserInfo.this);
        cantact_dialog.getWindow();
        cantact_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cantact_dialog.setContentView(R.layout.choose_contact_popup);
        cantact_dialog.setCanceledOnTouchOutside(true);
        cantact_dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        cantact_dialog.show();
        cantact_dialog.getWindow().setGravity(Gravity.CENTER);

        RelativeLayout call = (RelativeLayout) cantact_dialog
                .findViewById(R.id.call_layout);
        RelativeLayout message = (RelativeLayout) cantact_dialog
                .findViewById(R.id.message_layout);
        RelativeLayout bottom_layout = (RelativeLayout) cantact_dialog
                .findViewById(R.id.bottom_layout);

        bottom_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantact_dialog.dismiss();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                HashMap<String, String> mask = session.getPhoneMasking();
                String mask_status = mask.get(SessionManager.KEY_PHONE_MASKING_STATUS);


                System.out.println("=========PHONEMASKINGSTATUS track ur ride get ==========>= " + mask_status);


                if (mask_status.equalsIgnoreCase("Yes")) {

                    System.out.println("=========PHONEMASKINGSTATUS track ur ride YES ==========>= " + mask_status);

                    cd = new ConnectionDetector(UserInfo.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        phonemask_Call(ServiceConstant.phoneMasking,ride_id);
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                } else {
                    if (Str_user_phoneno != null) {
                      //  Str_user_phone_no=Str_user_phoneno;
                        System.out.println("=========Str_user_phoneno ==========>= " + Str_user_phoneno);

                        if (Build.VERSION.SDK_INT >= 23) {
                            // Marshmallow+
                            if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                                requestPermission();
                            } else {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Str_user_phoneno));
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Str_user_phoneno));
                            startActivity(intent);

                        }
                    } else {
                        Alert(UserInfo.this.getResources().getString(R.string.alert_label_title), UserInfo.this.getResources().getString(R.string.arrived_alert_content1));
                    }
                }


                cantact_dialog.dismiss();


            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                cantact_dialog.dismiss();
                HashMap<String, String> mask = session.getPhoneMasking();
                String mask_status = mask.get(SessionManager.KEY_PHONE_MASKING_STATUS);


                /*System.out.println("=========PHONEMASKINGSTATUS track ur ride get ==========>= " + mask_status);

                Intent n = new Intent(Intent.ACTION_VIEW);
                n.setType("vnd.android-dir/mms-sms");
                n.putExtra("address", driverMobile);
                startActivity(n);*/

                if (mask_status.equalsIgnoreCase("Yes")) {
                    showMessagePopup(ride_id);


                }
                else {

                    try {

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("smsto:" + Uri.encode(Str_user_phoneno)));
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {

                    }


                    /*Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setType("vnd.android-dir/mms-sms");
                    i.putExtra("address", Str_user_phoneno);
                    startActivity(i);*/


                }



            }
        });
    }

    public void showMessagePopup(final String str_ride_id)
    {

        sms_popup = new Dialog(UserInfo.this);
        sms_popup.getWindow();
        sms_popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sms_popup.setContentView(R.layout.sms_popup);
        sms_popup.setCanceledOnTouchOutside(true);
        sms_popup.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        sms_popup.show();
        sms_popup.getWindow().setGravity(Gravity.CENTER);

        TextView cancel = (TextView) sms_popup
                .findViewById(R.id.cancel);
        final EditText ed_msg = (EditText) sms_popup
                .findViewById(R.id.text_editview);
        TextView send = (TextView) sms_popup
                .findViewById(R.id.send);

        //     ed_msg.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        //       ed_msg.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        // tFields.addView(inputs[i]);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sms_popup.dismiss();


            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sms=ed_msg.getText().toString();
                sms_popup.dismiss();
                if(sms.trim().length()>0) {
                    phonemask_sms(ServiceConstant.phoneMasking_sms, sms,str_ride_id);
                }
                else
                {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.sms_masking_text));
                }

            }
        });
    }
    private void phonemask_sms(String Url,String msg,final String Str_RideId) {
        dialog = new Dialog(UserInfo.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));
        System.out.println("-------------phonemask_sms----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", Str_RideId);
        jsonParams.put("user_type", "driver");
        jsonParams.put("sms_content", msg);
        System.out.println("ride_id---------" + Str_RideId);
        System.out.println("user_type---------" + "user");
        System.out.println("sms_content---------" + msg);
        mRequest = new ServiceRequest(UserInfo.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println(response);
                Log.e("phonemask_sms", response);
                String Sstatus = "", SResponse = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    SResponse = object.getString("response");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Alert(getResources().getString(R.string.action_loading_sucess), SResponse);
                    } else {
                        Alert(getResources().getString(R.string.alert_sorry_label_title), SResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }
    private void phonemask_Call(String Url,String Str_RideId) {
        dialog = new Dialog(UserInfo.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));
        System.out.println("-------------phone Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", Str_RideId);
        jsonParams.put("user_type", "driver");

        System.out.println("ride_id---------" + Str_RideId);
        System.out.println("user_type---------" + "driver");

        mRequest = new ServiceRequest(UserInfo.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println(response);
                Log.e("phonemask", response);
                String Sstatus = "", SResponse = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    SResponse = object.getString("response");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Alert(getResources().getString(R.string.action_loading_sucess), SResponse);
                    } else {
                        Alert(getResources().getString(R.string.alert_sorry_label_title), SResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }








    private void initialize() {
        cd = new ConnectionDetector(UserInfo.this);
        isInternetPresent = cd.isConnectingToInternet();
        session = new SessionManager(UserInfo.this);
        Tv_userName = (TextView) findViewById(R.id.userinfo_usernamedetail);
        call_image = (ImageView) findViewById(R.id.userinfo_user_mobileno);
        ratingBar = (RatingBar) findViewById(R.id.user_ratings);
        Tv_username_header = (TextView) findViewById(R.id.user_info_user_name_head);
        Rl_layout_back = (RelativeLayout) findViewById(R.id.layout_user_info_back);
        Bt_canceltrip = (Button) findViewById(R.id.userinfo_canceltrip);
        userimage = (RoundedImageView) findViewById(R.id.userinfo_user_image);
        Intent i = getIntent();
        Str_username = i.getStringExtra("user_name");
        Str_userrating = i.getStringExtra("user_rating");
        Str_usermobilno = i.getStringExtra("user_phoneno");
        Str_user_img = i.getStringExtra("user_image");
        Str_rideId = i.getStringExtra("RideId");
        btnGroup = i.getStringExtra("Btn_group");

        if("4".equals(btnGroup))
        {
            Bt_canceltrip.setVisibility(View.GONE);
        }
        else
        {
            Bt_canceltrip.setVisibility(View.VISIBLE);
        }

        Tv_userName.setText(Str_username);
  //      Tv_usermobile_no.setText(Str_usermobilno);
        ratingBar.setRating(Float.parseFloat(Str_userrating));
        Tv_username_header.setText(Str_username);
        Picasso.with(UserInfo.this).load(String.valueOf(Str_user_img)).placeholder(R.drawable.default_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(userimage);
    }






    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkWriteExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE, android.Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }
    private void requestNavigationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_NAVIGATION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + Str_usermobilno));
                    startActivity(callIntent);
                }
                break;
            case PERMISSION_REQUEST_NAVIGATION_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(UserInfo.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234);
                    } else {


                        //          moveNavigation();
                    }
                }
        }
    }

}
