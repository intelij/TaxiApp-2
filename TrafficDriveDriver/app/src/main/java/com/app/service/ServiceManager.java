package com.app.service;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.dao.ServiceResponse;
import com.app.xmpp.XmppService;
import com.cabily.cabilydriver.HomePage;
import com.cabily.cabilydriver.R;
import com.cabily.cabilydriver.Utils.NetworkChangeReceiver;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.widgets.PkDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class ServiceManager {
    private static final String TAG = "ServiceManager TAG";
    private RequestQueue mRequestQueue;
    private Context context;
    private ServiceListener mServiceListener;
    private StringRequest stringRequest;
    private ObjectManager manager;
    private SessionManager sessionManager;
    private String userID = "", gcmID = "",language_code="",Agent_Name="";

    public interface ServiceListener {
        void onCompleteListener(Object object);
        void onErrorListener(Object error);
    }

    public ServiceManager(Context context, ServiceListener listener) {
        this.context = context;
        this.mServiceListener = listener;
        init();
        sessionManager = new SessionManager(context);

        HashMap<String, String> user = sessionManager.getUserDetails();
        HashMap<String, String> language = sessionManager.getLanaguageCode();
        userID = user.get(SessionManager.KEY_DRIVERID);
        gcmID = user.get(SessionManager.KEY_GCM_ID);
        Agent_Name = user.get(SessionManager.KEY_ID_NAME);
        language_code = user.get(SessionManager.KEY_Language_code);
        if(language_code.equals(""))
        {
            language_code="en";
        }
        else
        {

        }
      /*  try {
            if (!language.get(SessionManager.KEY_Language).equals("")) {
                language_code = language.get(SessionManager.KEY_Language_code);

            } else {
                language_code = "en";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            language_code = "en";
        }*/

        System.out.println("----------language_code------------------" + language_code);
    }

    private void init() {
        manager = new ObjectManager();
    }

    public void makeServiceRequest(final String url, int method, final HashMap<String, String> param) {
        stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("----------response--------accept----------"+response);
                try {

                    JSONObject object = new JSONObject(response);
                    if (object.has("is_dead")) {
                        System.out.println("-----------is dead----------------");
                        final PkDialog mDialog = new PkDialog(context);
                        mDialog.setDialogTitle(context.getResources().getString(R.string.action_session_expired_title));
                        mDialog.setDialogMessage(context.getResources().getString(R.string.action_session_expired_message));
                        mDialog.setPositiveButton(context.getResources().getString(R.string.lbel_notification_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                sessionManager.logoutUser();


                                context.stopService(new Intent(context, XmppService.class));


                                Intent intent = new Intent(context, HomePage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        });
                        mDialog.show();
                    }

                    if (object.has("is_out")) {
                        System.out.println("-----------is out----------------");
                        sessionManager.logoutUser();
                        context.stopService(new Intent(context, XmppService.class));
                        Intent intent = new Intent(context, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
/*                        final PkDialog mDialog = new PkDialog(context);
                        mDialog.setDialogTitle(context.getResources().getString(R.string.action_session_expired_title));
                        mDialog.setDialogMessage(context.getResources().getString(R.string.action_session_expired_message));
                        mDialog.setPositiveButton(context.getResources().getString(R.string.lbel_notification_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                sessionManager.logoutUser();
                                Intent intent = new Intent(context, HomePage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        });
                        mDialog.show();*/
                    }



                    Object obj = manager.getObjectForUrl(url, response);
                        if (obj instanceof ServiceResponse) {
                            ServiceResponse mResponse = (ServiceResponse) obj;
                            if ("1".equalsIgnoreCase(mResponse.getStatus())) {
                                mServiceListener.onCompleteListener(obj);
                            } else {
                                if (obj instanceof ServiceResponse) {
                                    ServiceResponse sr = (ServiceResponse) obj;
                                }
                                mServiceListener.onErrorListener(obj);
                            }
                        } else {
                            mServiceListener.onCompleteListener(obj);
                        }





                }catch (Exception e){
                    Toast.makeText(context, "Unknown error occurred", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(context, "Network connection is slow.Please try again.", Toast.LENGTH_SHORT).show();
                        NetworkChangeReceiver.firstTime = true;
                    } else if (error instanceof AuthFailureError) {
               //         Toast.makeText(context, "AuthFailureError", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
               //         Toast.makeText(context, "ServerError", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        NetworkChangeReceiver.firstTime = true;
               //         Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
               //         Toast.makeText(context, "ParseError", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                }
                mServiceListener.onErrorListener(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authkey", Agent_Name);
                headers.put("isapplication",ServiceConstant.isapplication);
                headers.put("applanguage",language_code);
                headers.put("apptype", ServiceConstant.cabily_AppType);
                headers.put("driverid", userID);
                headers.put("apptoken", gcmID);
                System.out.println("Authkey------------" + Agent_Name);
                System.out.println("applanguage------------"+language_code);
                System.out.println("app header------------"+headers.toString());

                return headers;


                }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(stringRequest);
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
