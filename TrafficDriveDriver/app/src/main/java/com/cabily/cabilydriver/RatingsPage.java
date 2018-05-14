package com.cabily.cabilydriver;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Hockeyapp.ActivityHockeyApp;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.Pojo.Reviwes_Pojo;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.EmojiExcludeFilter;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.adapter.Reviwes_adapter;
import com.cabily.cabilydriver.widgets.PkDialog;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user88 on 11/4/2015.
 */
public class RatingsPage extends ActivityHockeyApp {
    private String driver_id = "";
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    StringRequest postrequest;
    private TextView empty_Tv,rating_lable;
    Reviwes_adapter adapter;
    private ArrayList<Reviwes_Pojo> reivweslist;
    private boolean show_progress_status = false;
    Dialog dialog;
    private ServiceRequest mRequest;

    private TextView Tv_skip;
    ExpandableHeightListView listview;
    private String Str_rideid = "";

    Button Bt_rate_rider;

    private RelativeLayout Rl_layout_rating;

    private EditText Et_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviwes_list);
        initialize();

        Tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isInternetPresent) {
                    HashMap<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("skip_by", "driver");
                    jsonParams.put("ride_id", Str_rideid);
                    PostRequest_skipReviwes(ServiceConstant.skip_reviwes_url, jsonParams);
                } else {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                }


            }
        });
        Et_comment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_comment);
                }
                return false;
            }
        });
        Bt_rate_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isRatingEmpty = false;
                if (show_progress_status) {
                    if (reivweslist != null) {
                        for (int i = 0; i < reivweslist.size(); i++) {
                            if (reivweslist.get(i).getRatings_count().length() == 0 || reivweslist.get(i).getRatings_count().equalsIgnoreCase("0.0")) {
                                isRatingEmpty = true;
                            }
                        }
                        if (!isRatingEmpty) {
                            if (isInternetPresent) {
                                HashMap<String, String> jsonParams = new HashMap<String, String>();
                                jsonParams.put("ratingsFor", "rider");
                                jsonParams.put("ride_id", Str_rideid);
                                jsonParams.put("comments", Et_comment.getText().toString());
                                for (int i = 0; i < reivweslist.size(); i++) {
                                    jsonParams.put("ratings[" + i + "][option_id]", reivweslist.get(i).getOptions_id());
                                    jsonParams.put("ratings[" + i + "][option_title]", reivweslist.get(i).getOptions_title());
                                    jsonParams.put("ratings[" + i + "][rating]", reivweslist.get(i).getRatings_count());
                                }
                                System.out.println("------------jsonParams-------------" + jsonParams);
                                Post_RequestReviwes(ServiceConstant.submit_reviwes_url, jsonParams);
                            } else {
                                Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                            }
                        } else {
                            Alert(getResources().getString(R.string.lbel_notification), getResources().getString(R.string.lbel_notification_selectrating));
                        }
                    }
                }else{
                    if (isInternetPresent) {
                        HashMap<String, String> jsonParams = new HashMap<String, String>();
                        jsonParams.put("ratingsFor", "rider");
                        jsonParams.put("ride_id", Str_rideid);
                        jsonParams.put("comments", Et_comment.getText().toString());
                        System.out.println("------------jsonParams-------------" + jsonParams);
                        Post_RequestReviwes(ServiceConstant.submit_reviwes_url, jsonParams);
                    } else {
                        Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }


            }
        });

    }


    private void initialize() {
        session = new SessionManager(RatingsPage.this);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);
        reivweslist = new ArrayList<Reviwes_Pojo>();

        listview = (ExpandableHeightListView) findViewById(R.id.listView_rating);
        empty_Tv = (TextView) findViewById(R.id.reviwes_no_textview);
        Bt_rate_rider = (Button) findViewById(R.id.btn_submit_reviwes);
        Rl_layout_rating = (RelativeLayout) findViewById(R.id.layout_reviwesubmit_btn);
        rating_lable = (TextView) findViewById(R.id.rating_page_lable);


        Et_comment = (EditText) findViewById(R.id.my_rides_rating_comment_edittext);
        Et_comment.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Et_comment.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        Intent i = getIntent();
        Str_rideid = i.getStringExtra("rideid");

        Tv_skip = (TextView) findViewById(R.id.review_skip);

        cd = new ConnectionDetector(RatingsPage.this);
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            PostRequest(ServiceConstant.reviwes_options_list_url);
            System.out.println("raatingslist------------------" + ServiceConstant.reviwes_options_list_url);
        } else {
            Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));

        }
    }

    //--------------Close KeyBoard Method-----------
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(RatingsPage.this);
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

    private void PostRequest_skipReviwes(String Url, final HashMap<String, String> jsonParams) {
        dialog = new Dialog(RatingsPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rating_loading_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview_rating);
        dialog_title.setText(getResources().getString(R.string.action_loading_rating));
        System.out.println("----------skipReviwes- Url-----------" + Url);
        System.out.println("----------skipReviwes- jsonParams-----------" + jsonParams);


        mRequest = new ServiceRequest(RatingsPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                Log.e("reviwes", response);

                System.out.println("----------skipReviwes- response-----------" + response);

                String status = "", Str_response = "";

                try {
                    JSONObject object = new JSONObject(response);
                    status = object.getString("status");
                    Str_response = object.getString("response");

                    System.out.println("status------" + status);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (status.equalsIgnoreCase("1")) {

                    Intent broadcastIntent_tripdetail = new Intent();
                    broadcastIntent_tripdetail.setAction("com.finish.endtripenterdetail");
                    sendBroadcast(broadcastIntent_tripdetail);

                    Intent broadcastIntent_begintrip = new Intent();
                    broadcastIntent_begintrip.setAction("com.finish.com.finish.BeginTrip");
                    sendBroadcast(broadcastIntent_begintrip);

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.finish.EndTrip");
                    sendBroadcast(broadcastIntent);
                    finish();

                    Intent broadcastIntent_drivermap = new Intent();
                    broadcastIntent_drivermap.setAction("com.finish.canceltrip.DriverMapActivity");
                    sendBroadcast(broadcastIntent_drivermap);
                    finish();


                    Intent broadcastIntent_loading = new Intent();
                    broadcastIntent_loading.setAction("com.finish.loadingpage");
                    sendBroadcast(broadcastIntent_loading);
                    finish();

                    Intent i = new Intent(RatingsPage.this, DriverMapActivity.class);
                    i.putExtra("availability", "Yes");
                    startActivity(i);


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


    //-----------------------Code for reviwes options list post request-----------------
    private void PostRequest(String Url) {
        dialog = new Dialog(RatingsPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------dashboard----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("optionsFor", "rider");
        jsonParams.put("ride_id", Str_rideid);


        mRequest = new ServiceRequest(RatingsPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                Log.e("reviwes", response);

                String ride_ratting_status = "";

                String status = "", Str_total = "", Str_Rating = "";

                try {
                    JSONObject object = new JSONObject(response);
                    status = object.getString("status");
                    if (status.equals("1")) {
                        Str_total = object.getString("total");
                        ride_ratting_status = object.getString("ride_ratting_status");

                        if (ride_ratting_status.equalsIgnoreCase("1")) {
                            Intent i = new Intent(RatingsPage.this, HomePage.class);
                            startActivity(i);
                            finish();


                        }

                        System.out.println("status---------" + object.getString("status"));

                        JSONArray jarry = object.getJSONArray("review_options");

                        if (jarry.length() > 0) {

                            for (int i = 0; i < jarry.length(); i++) {

                                JSONObject jobject = jarry.getJSONObject(i);

                                Reviwes_Pojo item = new Reviwes_Pojo();

                                item.setOptions_title(jobject.getString("option_title"));
                                item.setRatings_count("");
                                item.setOptions_id(jobject.getString("option_id"));

                                reivweslist.add(item);

                            }
                            show_progress_status = true;
                        }
                    } else {
                        show_progress_status = false;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (status.equalsIgnoreCase("1")) {
                    dialog.dismiss();

                    if (show_progress_status) {
                        empty_Tv.setVisibility(View.GONE);
                        listview.setVisibility(View.VISIBLE);
                        rating_lable.setVisibility(View.VISIBLE);
                        adapter = new Reviwes_adapter(RatingsPage.this, reivweslist);
                        listview.setAdapter(adapter);
                        listview.setExpanded(true);
                        dialog.dismiss();
                    }else{
                        empty_Tv.setVisibility(View.GONE);
                        listview.setVisibility(View.GONE);
                        rating_lable.setVisibility(View.GONE);
                    }
                } else if (status.equalsIgnoreCase("0")) {
                    Intent broadcastIntent_tripdetail = new Intent();
                    broadcastIntent_tripdetail.setAction("com.finish.endtripenterdetail");
                    sendBroadcast(broadcastIntent_tripdetail);

                    Intent broadcastIntent_begintrip = new Intent();
                    broadcastIntent_begintrip.setAction("com.finish.com.finish.BeginTrip");
                    sendBroadcast(broadcastIntent_begintrip);

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.finish.EndTrip");
                    sendBroadcast(broadcastIntent);
                    finish();

                    Intent broadcastIntent_drivermap = new Intent();
                    broadcastIntent_drivermap.setAction("com.finish.canceltrip.DriverMapActivity");
                    sendBroadcast(broadcastIntent_drivermap);
                    finish();


                    Intent broadcastIntent_loading = new Intent();
                    broadcastIntent_loading.setAction("com.finish.loadingpage");
                    sendBroadcast(broadcastIntent_loading);
                    finish();

                    Intent i = new Intent(RatingsPage.this, DriverMapActivity.class);
                    i.putExtra("availability", "Yes");
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fetchdatatoast), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();
            }

        });
    }



 /*           private void PostRequest1(String Url) {
            dialog = new Dialog(RatingsPage.this);
           dialog.getWindow();
             dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title=(TextView)dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));


        postrequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("reviwes", response);

                        String status = "",Str_total="",Str_Rating="";

                        try {
                            JSONObject object = new JSONObject(response);
                            status = object.getString("status");
                            Str_total = object.getString("total");

                            System.out.println("status---------"+object.getString("status"));

                            JSONArray jarry = object.getJSONArray("review_options");

                            if (jarry.length()>0){

                                for (int i =0;i<jarry.length();i++){

                                    JSONObject jobject =jarry.getJSONObject(i);

                                    Reviwes_Pojo item = new Reviwes_Pojo();

                                    item.setOptions_title(jobject.getString("option_title"));
                                    item.setRatings_count("");
                                    item.setOptions_id(jobject.getString("option_id"));

                                    reivweslist.add(item);

                                }
                                show_progress_status=true;

                            }else{
                                show_progress_status=false;
                            }
                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        if (status.equalsIgnoreCase("1")){
                            adapter = new Reviwes_adapter(RatingsPage.this,reivweslist);
                            listview.setAdapter(adapter);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.fetchdatatoast), Toast.LENGTH_SHORT).show();
                        }
                        if(show_progress_status)
                        {
                            empty_Tv.setVisibility(View.GONE);
                        }
                        else
                        {
                            empty_Tv.setVisibility(View.VISIBLE);
                            listview.setEmptyView(empty_Tv);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorResponse.VolleyError(RatingsPage.this,error);
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
                jsonParams.put("optionsFor", "rider");

                return jsonParams;
            }
        };
        AppController.getInstance().addToRequestQueue(postrequest);
    }*/


    private void Post_RequestReviwes(String Url, final HashMap<String, String> jsonParams) {
        dialog = new Dialog(RatingsPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rating_loading_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview_rating);
        dialog_title.setText(getResources().getString(R.string.action_loading_rating));


        mRequest = new ServiceRequest(RatingsPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                Log.e("reviwes", response);

                System.out.println("reviwes------------" + response);

                String status = "", Str_response = "";

                try {
                    JSONObject object = new JSONObject(response);
                    status = object.getString("status");
                    Str_response = object.getString("response");

                    System.out.println("status------" + status);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (status.equalsIgnoreCase("1")) {
                    final PkDialog mdialog = new PkDialog(RatingsPage.this);
                    mdialog.setDialogTitle(getResources().getString(R.string.action_loading_sucess));
                    mdialog.setDialogMessage(Str_response);
                    mdialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mdialog.dismiss();

                                    Intent broadcastIntent_tripdetail = new Intent();
                                    broadcastIntent_tripdetail.setAction("com.finish.endtripenterdetail");
                                    sendBroadcast(broadcastIntent_tripdetail);

                                    Intent broadcastIntent_begintrip = new Intent();
                                    broadcastIntent_begintrip.setAction("com.finish.com.finish.BeginTrip");
                                    sendBroadcast(broadcastIntent_begintrip);

                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction("com.finish.EndTrip");
                                    sendBroadcast(broadcastIntent);
                                    finish();

                                    Intent broadcastIntent_drivermap = new Intent();
                                    broadcastIntent_drivermap.setAction("com.finish.canceltrip.DriverMapActivity");
                                    sendBroadcast(broadcastIntent_drivermap);
                                    finish();


                                    Intent broadcastIntent_loading = new Intent();
                                    broadcastIntent_loading.setAction("com.finish.loadingpage");
                                    sendBroadcast(broadcastIntent_loading);
                                    finish();

                                    Intent i = new Intent(RatingsPage.this, DriverMapActivity.class);
                                    i.putExtra("availability", "Yes");
                                    startActivity(i);


                                }
                            }
                    );
                    mdialog.show();

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
/*        postrequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("reviwes", response);

                        System.out.println("reviwes------------" + response);

                        String status = "",Str_response="";

                      try {
                            JSONObject object = new JSONObject(response);
                            status = object.getString("status");
                            Str_response = object.getString("response");

                          System.out.println("status------"+status);

                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        if (status.equalsIgnoreCase("1")) {
                            final PkDialog mdialog = new PkDialog(RatingsPage.this);
                            mdialog.setDialogTitle(getResources().getString(R.string.action_loading_sucess));
                            mdialog.setDialogMessage(Str_response);
                            mdialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mdialog.dismiss();

                                            Intent broadcastIntent_tripdetail = new Intent();
                                            broadcastIntent_tripdetail.setAction("com.finish.endtripenterdetail");
                                            sendBroadcast(broadcastIntent_tripdetail);

                                            Intent broadcastIntent_drivermap = new Intent();
                                            broadcastIntent_drivermap.setAction("com.finish.canceltrip.DriverMapActivity");
                                            sendBroadcast(broadcastIntent_drivermap);
                                            finish();

                                            Intent broadcastIntent_loading= new Intent();
                                            broadcastIntent_loading.setAction("com.finish.loadingpage");
                                            sendBroadcast(broadcastIntent_loading);
                                            finish();



                                        }
                                    }
                            );
                            mdialog.show();

                        } else {
                            Alert(getResources().getString(R.string.alert_sorry_label_title), Str_response);
                        }
                        dialog.dismiss();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorResponse.VolleyError(RatingsPage.this,error);
                dialog.dismiss();
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
                return jsonParams;
            }
        };
        AppController.getInstance().addToRequestQueue(postrequest);*/
    }


    //-----------------Move Back on  phone pressed  back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            // nothing

            return true;
        }
        return false;
    }


}
