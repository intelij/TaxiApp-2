package com.cabily.cabilydriver.Utils;

import android.content.Context;

import com.android.volley.Request;
import com.app.service.ServiceConstant;
import com.app.service.ServiceRequest;

import java.util.HashMap;


public class ChatAvailabilityCheck {
    private Context context;
    private ServiceRequest mAvailabilityRequest;
    private SessionManager sessionManager;
    private String sMode = "";
    private String sUserID = "", gcmID = "", sTimeZone = "";

    public ChatAvailabilityCheck(Context mContext, String mode) {
        this.context = mContext;
        this.sMode = mode;
        sessionManager = new SessionManager(mContext);
        mAvailabilityRequest = new ServiceRequest(mContext);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_DRIVERID);
        gcmID = user.get(SessionManager.KEY_GCM_ID);
    }

    public void postChatRequest() {

        System.out.println("-----------app_availability url---------------" + ServiceConstant.updateAppStatus_url);
        System.out.println("-----------id---------------" + sUserID);
        System.out.println("-----------mode---------------" + sMode);

        if (mAvailabilityRequest != null) {
            mAvailabilityRequest.cancelRequest();
        }

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "driver");
        jsonParams.put("id", sUserID);
        jsonParams.put("mode", sMode);

        mAvailabilityRequest.makeServiceRequest(ServiceConstant.updateAppStatus_url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("---------app_availability sMode-----------------" + sMode);
                System.out.println("---------app_availability response-----------------" + response);
            }
            @Override
            public void onErrorListener() {
            }
        });
    }
}
