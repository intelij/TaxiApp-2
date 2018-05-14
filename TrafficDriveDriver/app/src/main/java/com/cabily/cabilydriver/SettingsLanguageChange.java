package com.cabily.cabilydriver;

/**
 * Created by user88 on 10/20/2016.
 */

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.widgets.PkDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by user129 on 10/13/2016.
 */
public class SettingsLanguageChange extends Fragment {


    private static View rootview;
    private Dialog dialog;
    SessionManager session;
    private ServiceRequest mRequest;

    Spinner language_spinner;
    private String driver_id = "";
    RelativeLayout change_language;

    ArrayList<String> languages_spn = new ArrayList<String>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootview != null) {
            ViewGroup parent = (ViewGroup) rootview.getParent();
            if (parent != null)
                parent.removeView(rootview);
        }
        try {
            rootview = inflater.inflate(R.layout.setting_language_change, container, false);
        } catch (InflateException e) {

        }
        init(rootview);


        rootview.findViewById(R.id.ham_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationDrawerNew.openDrawer();
               /* if (resideMenu != null) {
                    resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                }*/
            }
        });


        change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Locale locale = null;

                switch (language_spinner.getSelectedItemPosition()) {

                    case 0:
                        locale = new Locale("en");
                        session.setlamguage("en", "en");
                        System.out.println("========English Language========" + language_spinner.getSelectedItem().toString() + "\t\ten");
                        changeLanguage_PostRequest(ServiceConstant.change_Language, "en");
                      /* Intent i=new Intent(getActivity(),NavigationDrawer.class);
                        getActivity().finish();
                        startActivity(i);*/
                        break;

                    case 1:
                        locale = new Locale("es");
                        session.setlamguage("es", "es");
                        System.out.println("========Arabic Language========" + language_spinner.getSelectedItem().toString() + "\t\tes");
                        changeLanguage_PostRequest(ServiceConstant.change_Language, "es");
                       /*Intent in=new Intent(getActivity(),NavigationDrawer.class);
                        getActivity().finish();
                        startActivity(in);*/
                        break;

                    case 2:
                        locale = new Locale("ta");
                        session.setlamguage("ta", "ta");
                        System.out.println("========Arabic Language========" + language_spinner.getSelectedItem().toString() + "\t\tta");
                        changeLanguage_PostRequest(ServiceConstant.change_Language, "ta");
                       /*Intent in=new Intent(getActivity(),NavigationDrawer.class);
                        getActivity().finish();
                        startActivity(in);*/
                        break;

                    default:
                        locale = new Locale("en");
                        session.setlamguage("en", "en");
                        changeLanguage_PostRequest(ServiceConstant.change_Language, "en");


                        break;

                }

                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

            }
        });

        try {
            session = new SessionManager(getActivity());

            HashMap<String, String> language = session.getLanaguage();

            if (!language.get(SessionManager.KEY_Language).equals("")) {


                if (language.get(SessionManager.KEY_Language).equals("en")) {

                    language_spinner.setSelection(0);

                } else if (language.get(SessionManager.KEY_Language).equals("es")) {

                    language_spinner.setSelection(1);
                } else {
                    language_spinner.setSelection(2);

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  setUpViews();
        return rootview;

    }


    private void changeLanguage_PostRequest(String Url, String type) {
        dialog = new Dialog(getActivity());
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------password----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("id", driver_id);
        jsonParams.put("lang_code", type);
        jsonParams.put("user_type", "driver");

        System.out.println("--------------driver_id-------------------" + driver_id);
        System.out.println("--------------type-------------------" + type);


        mRequest = new ServiceRequest(getActivity());
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                Log.e("changelanguage", response);

                System.out.println("changelngresponse---------" + response);

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
                    Alert(getResources().getString(R.string.action_loading_sucess), Str_response);
                    //  Alert(getResources().getString(R.string.label_pushnotification_cashreceived),Str_response);
                } else {

                    Alert_sorry(getResources().getString(R.string.alert_sorry_label_title), Str_response);


                }

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();

            }

        });

    }

    private void init(View rootview) {

        session = new SessionManager(getActivity());

        language_spinner = (Spinner) rootview.findViewById(R.id.language_spinner);
        change_language = (RelativeLayout) rootview.findViewById(R.id.change_layout);

        try {
            languages_spn.clear();
            languages_spn.add("English");

//        languages_spn.add("Spanish");
//        languages_spn.add("தமிழ்");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, languages_spn);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            language_spinner.setAdapter(dataAdapter);

            HashMap<String, String> user = session.getUserDetails();
            driver_id = user.get(SessionManager.KEY_DRIVERID);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(getActivity());
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent i = new Intent(getActivity(), NavigationDrawerNew.class);
                getActivity().finish();
                startActivity(i);
                // finish();
            }
        });
        mDialog.show();
    }

    private void Alert_sorry(String title, String message) {
        final PkDialog mDialog = new PkDialog(getActivity());
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                // finish();
            }
        });
        mDialog.show();
    }
}
