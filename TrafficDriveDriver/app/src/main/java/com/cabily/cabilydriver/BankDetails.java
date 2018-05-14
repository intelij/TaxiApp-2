package com.cabily.cabilydriver;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Hockeyapp.FragmentHockeyApp;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.EmojiExcludeFilter;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.widgets.PkDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 */
public class BankDetails extends FragmentHockeyApp {
    private StringRequest postrequest;
    private SessionManager session;
    private String driver_id = "";
    private View parentView;
  ///  private ResideMenu resideMenu;
    private EditText holder_name, holder_address, account_no, bankname, branchname, branchaddress, ifsccode, routingno;
    private Button save_btn;
    private static View rootview;
    Context context;
    Dialog dialog;
    private ServiceRequest mRequest;

    private ActionBar actionBar;
    private View mCustomView;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private ColorDrawable colorDrawable = new ColorDrawable();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview != null) {
            ViewGroup parent = (ViewGroup) rootview.getParent();
            if (parent != null)
                parent.removeView(rootview);
        }
        try {
            rootview = inflater.inflate(R.layout.bank_account, container, false);
        } catch (InflateException e) {
        }
       /* ActionBarActivity actionBarActivity = (ActionBarActivity) getActivity();
        actionBar = actionBarActivity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.hide();*/
        initialize(rootview);
        NavigationDrawerNew.disableSwipeDrawer();

        rootview.findViewById(R.id.ham_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                NavigationDrawerNew.openDrawer();




                    if(holder_name!=null)
                    {
                        holder_name.setError(null);
                    }
                    if(holder_address!=null)
                    {
                        holder_address.setError(null);
                    }
                    if(account_no!=null)
                    {
                        account_no.setError(null);
                    }
                    if(bankname!=null)
                    {
                        bankname.setError(null);
                    } if(branchname!=null)
                    {
                        branchname.setError(null);
                    }
                    if(holder_name!=null)
                    {
                        holder_name.setError(null);
                    }
                    if(branchaddress!=null)
                    {
                        branchaddress.setError(null);
                    }

                    if(ifsccode!=null)
                    {
                        ifsccode.setError(null);
                    }
                    if(routingno!=null)
                    {
                        routingno.setError(null);
                    }


                }

        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder_name.setText(Html.fromHtml(holder_name.getText().toString()).toString());
                holder_address.setText(Html.fromHtml(holder_address.getText().toString()).toString());
                account_no.setText(Html.fromHtml(account_no.getText().toString()).toString());
                bankname.setText(Html.fromHtml(bankname.getText().toString()).toString());
                branchname.setText(Html.fromHtml(branchname.getText().toString()).toString());
                branchaddress.setText(Html.fromHtml(branchaddress.getText().toString()).toString());
                ifsccode.setText(Html.fromHtml(ifsccode.getText().toString()).toString());
                routingno.setText(Html.fromHtml(routingno.getText().toString()).toString());


                if (holder_name.length() == 0) {
                    erroredit(holder_name,getResources().getString(R.string.action_alert_bank_Username));
                } else if (holder_address.length() == 0) {
                    erroredit(holder_address, getResources().getString(R.string.action_alert_bank_address));
                } else if (account_no.length() == 0) {
                    erroredit(account_no, getResources().getString(R.string.action_alert_bank_accountno));
                } else if (bankname.length() == 0) {
                    erroredit(bankname,getResources().getString(R.string.action_alert_bank_name));
                } else if (branchname.length() == 0) {
                    erroredit(branchname, getResources().getString(R.string.action_alert_branch_name));
                } else if (branchaddress.length() == 0) {
                    erroredit(branchaddress,getResources().getString(R.string.action_alert_branch_address));
                } /*else if (ifsccode.length() == 0) {
                    erroredit(ifsccode, getResources().getString(R.string.action_alert_bank_ifs_code));
                } else if (routingno.length() == 0) {
                    erroredit(routingno, getResources().getString(R.string.action_alert_bank_routingno));
                }*/ else {
                    cd = new ConnectionDetector(getActivity());
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        postRequest_savebank(ServiceConstant.saveBankDetails);
                        System.out.println("bank------------------" + ServiceConstant.saveBankDetails);
                    } else {
                        Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }

            }
        });

      //  setUpViews();
        return rootview;
    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(getActivity());
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


    private void initialize(View rootview) {
        session = new SessionManager(getActivity());

        holder_name = (EditText) rootview.findViewById(R.id.bank_ac_holder_name);
        holder_address = (EditText) rootview.findViewById(R.id.bank_ac_holder_address);
        account_no = (EditText) rootview.findViewById(R.id.bank_ac_account_number);
        bankname = (EditText) rootview.findViewById(R.id.bank_ac_bank_name);
        branchname = (EditText) rootview.findViewById(R.id.bank_ac_branch_name);
        branchaddress = (EditText) rootview.findViewById(R.id.bank_ac_branch_address);
        ifsccode = (EditText) rootview.findViewById(R.id.bank_ac_ifsc_code);
        routingno = (EditText) rootview.findViewById(R.id.bank_ac_routing_number);
        save_btn = (Button) rootview.findViewById(R.id.bank_ac_save_button);

        holder_name.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        holder_address.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        account_no.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        bankname.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        branchname.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        branchaddress.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        ifsccode.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        routingno.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        HashMap<String, String> user = session.getUserDetails();
        driver_id = user.get(SessionManager.KEY_DRIVERID);

        cd = new ConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            postRequest_getbank(ServiceConstant.getBankDetails);
            System.out.println("bankget------------------" + ServiceConstant.getBankDetails);
        } else {
            Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
        }

    }




    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        editname.startAnimation(shake);
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }

    //-----------------------Post Request-----------------
    private void postRequest_savebank(String Url) {
        dialog = new Dialog(getActivity());
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------savebank----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);
        jsonParams.put("acc_holder_name", holder_name.getText().toString());
        jsonParams.put("acc_holder_address", holder_address.getText().toString());
        jsonParams.put("acc_number", account_no.getText().toString());
        jsonParams.put("bank_name", bankname.getText().toString());
        jsonParams.put("branch_name", branchname.getText().toString());
        jsonParams.put("branch_address", branchaddress.getText().toString());
        jsonParams.put("swift_code", ifsccode.getText().toString());
        jsonParams.put("routing_number", routingno.getText().toString());
        System.out.println("-------------savebank---jsonParams-------------" + jsonParams);
        System.out.println("driver_id----------" + driver_id);

        System.out.println("acc_holder_name----------" + holder_name.getText().toString());

        System.out.println("acc_holder_address----------" + holder_address.getText().toString());

        System.out.println("acc_number----------" + account_no.getText().toString());

        System.out.println("bank_name----------" + bankname.getText().toString());

        System.out.println("branch_address----------" + branchaddress.getText().toString());

        System.out.println("swift_code----------" + ifsccode.getText().toString());

        System.out.println("routing_number----------" + routingno.getText().toString());

        mRequest = new ServiceRequest(getActivity());
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                String Strstatus = "", Sresponse = "", Str_accountholder_name = "", Str_accountholder_address = "", Str_account_number = "", Str_bank_name = "",
                        Str_branch_name = "", Str_branch_address = "", Str_swift_code = "", Str_routing_number = "";

                System.out.println("bank-----------------" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    Strstatus = object.getString("status");

                    if (Strstatus.equalsIgnoreCase("1")){

                        JSONObject jsonObject = object.getJSONObject("response");
                        JSONObject jobjct = jsonObject.getJSONObject("banking");

                        Str_accountholder_name = jobjct.getString("acc_holder_name");
                        Str_accountholder_address = jobjct.getString("acc_holder_address");
                        Str_account_number = jobjct.getString("acc_number");
                        Str_bank_name = jobjct.getString("bank_name");
                        Str_branch_name = jobjct.getString("branch_name");
                        Str_branch_address = jobjct.getString("branch_address");
                        Str_swift_code = jobjct.getString("swift_code");
                        Str_routing_number = jobjct.getString("routing_number");
                    }else{
                        Sresponse = object.getString("response");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Strstatus.equalsIgnoreCase("1")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.alertsaved_label_title), Toast.LENGTH_LONG).show();
                    holder_name.setText(Str_accountholder_name);
                    holder_address.setText(Str_accountholder_address);
                    account_no.setText(Str_account_number);
                    bankname.setText(Str_bank_name);
                    branchname.setText(Str_branch_name);
                    ifsccode.setText(Str_swift_code);
                    routingno.setText(Str_routing_number);

                } else {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), Sresponse);
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();

            }

        });


    }


    //-----------------------Post Request-----------------
    private void postRequest_getbank(String Url) {
        dialog = new Dialog(getActivity());
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------dashboard----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("driver_id", driver_id);
        System.out.println("driver_id----------" + driver_id);

        mRequest = new ServiceRequest(getActivity());
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                String Strstatus = "", Smessage = "", Str_accountholder_name = "", Str_accountholder_address = "", Str_account_number = "", Str_bank_name = "",
                        Str_branch_name = "", Str_branch_address = "", Str_swift_code = "", Str_routing_number = "";
                System.out.println("bank-----------------" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    Strstatus = object.getString("status");

                    if (Strstatus.equalsIgnoreCase("1")){
                        JSONObject jsonObject = object.getJSONObject("response");
                        JSONObject jobjct = jsonObject.getJSONObject("banking");

                        Str_accountholder_name = jobjct.getString("acc_holder_name");
                        Str_accountholder_address = jobjct.getString("acc_holder_address");
                        Str_account_number = jobjct.getString("acc_number");
                        Str_bank_name = jobjct.getString("bank_name");
                        Str_branch_name = jobjct.getString("branch_name");
                        Str_branch_address = jobjct.getString("branch_address");
                        Str_swift_code = jobjct.getString("swift_code");
                        Str_routing_number = jobjct.getString("routing_number");

                    }else{
                        Smessage = object.getString("response");
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    dialog.dismiss();
                }

                if (Strstatus.equalsIgnoreCase("1")) {
                    holder_name.setText(Str_accountholder_name);
                    holder_address.setText(Str_accountholder_address);
                    account_no.setText(Str_account_number);
                    bankname.setText(Str_bank_name);
                    branchname.setText(Str_branch_name);
                    ifsccode.setText(Str_swift_code);
                    routingno.setText(Str_routing_number);
                    branchaddress.setText(Str_branch_address);

                } else {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), Smessage);
                }

                dialog.dismiss();


            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();

            }

        });
    }

}
