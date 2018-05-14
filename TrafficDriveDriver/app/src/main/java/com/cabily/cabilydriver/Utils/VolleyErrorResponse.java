package com.cabily.cabilydriver.Utils;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.cabily.cabilydriver.R;

/**
 * Created by user88 on 10/15/2015.
 */
public class VolleyErrorResponse {

    public static void VolleyError(Context context, VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(context, context.getResources().getString(R.string.service_request_net_slow_alert), Toast.LENGTH_SHORT).show();
            NetworkChangeReceiver.firstTime = true;
        } else if (error instanceof AuthFailureError) {
//            Toast.makeText(context, "AuthFailureError", Toast.LENGTH_LONG).show();
        } else if (error instanceof ServerError) {
//            Toast.makeText(context, "ServerError", Toast.LENGTH_LONG).show();
        } else if (error instanceof NetworkError) {
//            Toast.makeText(context, "NetworkError", Toast.LENGTH_LONG).show();
            NetworkChangeReceiver.firstTime = true;
        } else if (error instanceof ParseError) {
//            Toast.makeText(context, "ParseError", Toast.LENGTH_LONG).show();
        }
    }
}