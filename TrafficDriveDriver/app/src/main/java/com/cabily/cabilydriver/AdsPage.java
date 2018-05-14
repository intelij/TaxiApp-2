package com.cabily.cabilydriver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cabily.cabilydriver.Utils.SessionManager;
import com.squareup.picasso.Picasso;

/**
 */
public class AdsPage extends Activity {
    private ImageView  Iv_banner;
    Button Iv_close;
    private TextView Tv_title, Tv_message;
    private View Vi_space;
    private String sTitle = "", sMessage = "", mBanner = "";

    private static SessionManager session;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ads_page);
        initialize();

        Iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void initialize() {
        Iv_close = (Button) findViewById(R.id.ads_page_close_imageView);
        Iv_banner = (ImageView) findViewById(R.id.ads_page_banner_image);
        Tv_title = (TextView) findViewById(R.id.ads_page_title);
        Tv_message = (TextView) findViewById(R.id.ads_page_message);

        session = new SessionManager(AdsPage.this);
        Intent intent = getIntent();

        if (intent.hasExtra("AdsTitle")) {
            sTitle = intent.getStringExtra("AdsTitle");
        }

        if (intent.hasExtra("AdsMessage")) {
            sMessage = intent.getStringExtra("AdsMessage");
        }

        if (intent.hasExtra("AdsBanner")) {
            mBanner = intent.getStringExtra("AdsBanner");
        }

        if (mBanner.length() > 0) {
            Iv_banner.setVisibility(View.VISIBLE);


            Picasso.with(AdsPage.this).load(String.valueOf(mBanner)).into(Iv_banner);

        } else {
            Iv_banner.setVisibility(View.GONE);

        }

        Tv_title.setText(sTitle);
        Tv_message.setText(sMessage);
        session.setADS(false);
    }


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
        return false;
    }
}