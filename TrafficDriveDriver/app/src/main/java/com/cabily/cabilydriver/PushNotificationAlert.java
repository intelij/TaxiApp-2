package com.cabily.cabilydriver;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cabily.cabilydriver.Utils.CurrencySymbolConverter;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by user88 on 11/6/2015.
 */
public class PushNotificationAlert extends BaseActivity {
    private TextView Message_Tv,Textview_Ok,Textview_alert_header;
    private  String message="",action="",amount="",rideid="",currency_code="",str_amount="",ride_id;
    private RelativeLayout Rl_layout_alert_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pushnotification);
        initialize();

        Rl_layout_alert_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("action-----------" + action);

                if (action.equalsIgnoreCase("ride_cancelled")) {

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.finish.ArrivedTrip");
                    sendBroadcast(broadcastIntent);

                    Intent broadcastIntent1 = new Intent();
                    broadcastIntent1.setAction("com.finish.BeginTrip");
                    sendBroadcast(broadcastIntent1);


                    Intent broadcastIntent_userinfo = new Intent();
                    broadcastIntent_userinfo.setAction("com.finish.UserInfo");
                    sendBroadcast(broadcastIntent_userinfo);

                    Intent broadcastIntent_tripdetail = new Intent();
                    broadcastIntent_tripdetail.setAction("com.finish.tripsummerydetail");
                    sendBroadcast(broadcastIntent_tripdetail);

                    Intent broadcastIntent_drivermap = new Intent();
                    broadcastIntent_drivermap.setAction("com.finish.canceltrip.DriverMapActivity");
                    sendBroadcast(broadcastIntent_drivermap);

                    Intent broadcastIntent_drivermap1 = new Intent();
                    broadcastIntent_drivermap1.setAction("com.finish.canceltrip.DriverMapActivity.finish");
                    sendBroadcast(broadcastIntent_drivermap1);


                    Intent broadcastIntent_trip = new Intent();
                    broadcastIntent_trip.setAction("com.finish.tripPage");
                    sendBroadcast(broadcastIntent_trip);


                    Intent i = new Intent(PushNotificationAlert.this, DriverMapActivity.class);
                    i.putExtra("availability","Yes");
                    startActivity(i);


                    finish();


                //    onBackPressed();

                } else if (action.equalsIgnoreCase("receive_cash")) {

                    System.out.println("inside receive_cash-----------" + str_amount);
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.finish.OtpPage");
                    sendBroadcast(broadcastIntent);

                    Intent intent = new Intent(PushNotificationAlert.this, PaymentPage.class);
                    intent.putExtra("amount", str_amount);
                    intent.putExtra("rideid", rideid);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();

                } else if (action.equalsIgnoreCase("payment_paid")) {


                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.finish.OtpPage");
                    sendOrderedBroadcast(broadcastIntent,null);

                    Intent broadcastIntent_payment = new Intent();
                    broadcastIntent_payment.setAction("com.finish.PaymentPage");
                    sendOrderedBroadcast(broadcastIntent_payment,null);

                    Intent broadcastIntent_paymenttrip = new Intent();
                    broadcastIntent_paymenttrip.setAction("com.finish.tripsummerydetail");
                    sendOrderedBroadcast(broadcastIntent_paymenttrip,null);


                    Intent intent = new Intent(PushNotificationAlert.this, RatingsPage.class);
                    intent.putExtra("rideid", rideid);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
                else if (action.equalsIgnoreCase("ride_completed")) {



                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.finish.ArrivedTrip");
                    sendBroadcast(broadcastIntent);

                    Intent broadcastIntent1 = new Intent();
                    broadcastIntent1.setAction("com.finish.BeginTrip");
                    sendBroadcast(broadcastIntent1);

                    Intent broadcastIntent2 = new Intent();
                    broadcastIntent2.setAction("com.finish.EndTrip");
                    sendBroadcast(broadcastIntent2);

                    Intent broadcastIntent_userinfo = new Intent();
                    broadcastIntent_userinfo.setAction("com.finish.UserInfo");
                    sendBroadcast(broadcastIntent_userinfo);


                    Intent broadcastIntent_trip = new Intent();
                    broadcastIntent_trip.setAction("com.finish.tripPage");
                    sendBroadcast(broadcastIntent_trip);



                    Intent intent = new Intent(PushNotificationAlert.this, TripSummaryDetail.class);
                    intent.putExtra("ride_id", ride_id);
                    intent.putExtra("type", "noti");
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            }
        });
    }


    private void initialize() {
        Intent i = getIntent();
        message = i.getStringExtra("Message");
        action = i.getStringExtra("Action");
        amount = i.getStringExtra("amount");
        rideid = i.getStringExtra("RideId");
        ride_id= i.getStringExtra("ride");
        currency_code = i.getStringExtra("Currencycode");


        System.out.println("----------jai-----ride_id"+ride_id);

        Textview_Ok = (TextView)findViewById(R.id.pushnotification_alert_ok_textview);
        Message_Tv = (TextView)findViewById(R.id.pushnotification_alert_messge_textview);
        Textview_alert_header = (TextView)findViewById(R.id.pushnotification_alert_messge_label);
        Rl_layout_alert_ok = (RelativeLayout)findViewById(R.id.pushnotification_alert_ok);
        Message_Tv.setText(message);

          if (action.equalsIgnoreCase("ride_cancelled")){
              Textview_alert_header.setText(getResources().getString(R.string.label_pushnotification_canceled));
          }else if(action.equalsIgnoreCase("receive_cash")){
              Textview_alert_header.setText(getResources().getString(R.string.label_pushnotification_cashreceived));
          }else if(action.equalsIgnoreCase("payment_paid")){
              Textview_alert_header.setText(getResources().getString(R.string.label_pushnotification_ride_completed));
          }
          else if(action.equalsIgnoreCase("ride_completed")){
              Textview_alert_header.setText(getResources().getString(R.string.label_pushnotification_ride_completed));
          }
        if(currency_code!=null)
        {
          //  Currency currencycode= Currency.getInstance(getLocale(currency_code));
            String  sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(currency_code);
            str_amount=sCurrencySymbol+amount;
        }
    }




    //method to convert currency code to currency symbol
    private static Locale getLocale(String strCode) {

        for (Locale locale : NumberFormat.getAvailableLocales()) {
            String code = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
            if (strCode!=null&&strCode.equals(code)) {
                return locale;
            }
        }
        return null;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            // nothing
            return true;
        }
        return false;
    }
}
