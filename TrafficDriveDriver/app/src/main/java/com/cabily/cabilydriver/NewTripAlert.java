package com.cabily.cabilydriver;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Hockeyapp.ActionBarActivityHockeyApp;
import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.widgets.PkDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user88 on 3/8/2016.
 */
public class NewTripAlert extends ActionBarActivityHockeyApp {

    private ImageView riderlater_userimg, ridelater_call_userimg;
    private TextView Tv_ridelater_username, Tv_message, Tv_ridelater_userphoneno, Tv_ridelater_user_address, Tv_ridelater_time;
    private String SuserName = "", Suser_Mobileno = "", SUser_Message = "", SAction = "", SUser_Rating = "", SUser_image = "", SUser_pickup_location = "", Suser_Pickup_time = "", SRide_id = "";
    private RatingBar UserRating;
    private RelativeLayout layout_ok;
    public static MediaPlayer mediaPlayer1;
    final int PERMISSION_REQUEST_CODE = 111;
    private Dialog dialog;
    private ServiceRequest mRequest;
    private SessionManager session;
    private Dialog cantact_dialog,sms_popup;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ridelater_alert);
        initialize();

        layout_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer1 != null && mediaPlayer1.isPlaying()) {
                    mediaPlayer1.stop();
                }

                finish();
                Intent intent = new Intent(NewTripAlert.this, TripSummaryDetail.class);
                intent.putExtra("ride_id", SRide_id);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        ridelater_call_userimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseContactOptions();

               /* if (Suser_Mobileno != null) {
                    if (mediaPlayer1 != null && mediaPlayer1.isPlaying()) {
                        mediaPlayer1.stop();
                    }

                    if (Build.VERSION.SDK_INT >= 23) {
                        // Marshmallow+
                        if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                            requestPermission();
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + Suser_Mobileno));
                            startActivity(callIntent);
                        }
                    } else {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + Suser_Mobileno));
                        startActivity(callIntent);
                    }
                } else {
                    Alert(NewTripAlert.this.getResources().getString(R.string.alert_sorry_label_title), NewTripAlert.this.getResources().getString(R.string.arrived_alert_content1));
                }*/

            }







        });

    }
    private void chooseContactOptions() {

        if (Suser_Mobileno != null) {
            if (mediaPlayer1 != null && mediaPlayer1.isPlaying()) {
                mediaPlayer1.stop();
            }


            cantact_dialog = new Dialog(NewTripAlert.this);
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

                        cd = new ConnectionDetector(NewTripAlert.this);
                        isInternetPresent = cd.isConnectingToInternet();
                        if (isInternetPresent) {
                            phonemask_Call(ServiceConstant.phoneMasking);
                        } else {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }
                    } else {
                        if (Suser_Mobileno != null) {

                            System.out.println("=========PHONEMASKINGSTATUS NO==========>= " + mask_status);

                            if (Build.VERSION.SDK_INT >= 23) {
                                // Marshmallow+
                                if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                                    requestPermission();
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Suser_Mobileno));
                                    startActivity(intent);
                                }
                            } else {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Suser_Mobileno));
                                startActivity(intent);

                            }
                        } else {
                            Alert(NewTripAlert.this.getResources().getString(R.string.alert_label_title), NewTripAlert.this.getResources().getString(R.string.arrived_alert_content1));
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
                        showMessagePopup();


                    } else {
                        try {

                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("smsto:" + Uri.encode(Suser_Mobileno)));
                            startActivity(intent);
                        }
                        catch (Exception e)
                        {

                        }

                       /* Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setType("vnd.android-dir/mms-sms");
                        i.putExtra("address", Suser_Mobileno);
                        startActivity(i);*/


                    }


                }
            });
        }
    }

    public void showMessagePopup()
    {

        sms_popup = new Dialog(NewTripAlert.this);
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
                    phonemask_sms(ServiceConstant.phoneMasking_sms, sms);
                }
                else
                {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.sms_masking_text));
                }

            }
        });
    }
    private void phonemask_sms(String Url,String msg) {
        dialog = new Dialog(NewTripAlert.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));
        System.out.println("-------------phonemask_sms----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", SRide_id);
        jsonParams.put("user_type", "driver");
        jsonParams.put("sms_content", msg);
        System.out.println("ride_id---------" + SRide_id);
        System.out.println("user_type---------" + "user");
        System.out.println("sms_content---------" + msg);
        mRequest = new ServiceRequest(NewTripAlert.this);
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
    private void phonemask_Call(String Url) {
        dialog = new Dialog(NewTripAlert.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));
        System.out.println("-------------phone Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", SRide_id);
        jsonParams.put("user_type", "driver");

        System.out.println("ride_id---------" + SRide_id);
        System.out.println("user_type---------" + "driver");

        mRequest = new ServiceRequest(NewTripAlert.this);
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
        session = new SessionManager(NewTripAlert.this);
        mediaPlayer1 = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        riderlater_userimg = (ImageView) findViewById(R.id.ridelater_userimage);
        ridelater_call_userimg = (ImageView) findViewById(R.id.call_userimg);
        Tv_ridelater_username = (TextView) findViewById(R.id.ridelater_username);
        Tv_ridelater_user_address = (TextView) findViewById(R.id.ridelater_useraddress);
        Tv_ridelater_time = (TextView) findViewById(R.id.ridelater_user_pickptime);
        UserRating = (RatingBar) findViewById(R.id.ridelater_user_ratings);
        Tv_message = (TextView) findViewById(R.id.newtrip_header_message);
        layout_ok = (RelativeLayout) findViewById(R.id.layout_ridelater_alert_ok);


        Intent i = getIntent();
        SAction = i.getStringExtra("Action");
        SUser_Message = i.getStringExtra("Message");
        SuserName = i.getStringExtra("Username");
        Suser_Mobileno = i.getStringExtra("Mobilenumber");
        SUser_Rating = i.getStringExtra("UserRating");
        SUser_image = i.getStringExtra("UserImage");
        SUser_pickup_location = i.getStringExtra("UserPickuplocation");
        Suser_Pickup_time = i.getStringExtra("UserPickupTime");
        SRide_id = i.getStringExtra("RideId");

        System.out.println("SRide_id-----------------" + SRide_id);
        System.out.println("SUser_image-----------------" + SUser_image);
        Picasso.with(NewTripAlert.this).load(String.valueOf(SUser_image)).placeholder(R.drawable.nouserimg).memoryPolicy(MemoryPolicy.NO_CACHE).into(riderlater_userimg);
        Tv_message.setText(SUser_Message);
        Tv_ridelater_username.setText(SuserName);
        Tv_ridelater_user_address.setText(SUser_pickup_location);
        Tv_ridelater_time.setText(Suser_Pickup_time);
        UserRating.setRating(Float.parseFloat(SUser_Rating));

        if (mediaPlayer1 != null) {
            if (!mediaPlayer1.isPlaying()) {
                mediaPlayer1.start();
                mediaPlayer1.setLooping(true);

            }
        }


    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(NewTripAlert.this);
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


    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {

        if (mediaPlayer1 != null) {
            if (mediaPlayer1.isPlaying()) {
                mediaPlayer1.stop();
            }
        }


        super.onDestroy();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + Suser_Mobileno));
                    startActivity(callIntent);
                }
                break;
        }
    }
}
