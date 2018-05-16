package com.cabily.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.cabily.HockeyApp.ActivityHockeyApp;
import com.cabily.adapter.CustomPagerAdapter;
import com.casperon.app.cabily.R;
import com.mylibrary.widgets.CustomTextView;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import library.SliderLayout;
import library.SliderTypes.BaseSliderView;
import library.Tricks.ViewPagerEx;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by user14 on 11/17/2016.
 */

public class SignUpBannerPage extends ActivityHockeyApp implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {


    private SliderLayout mSlider;
    private CircleIndicator pagerIndicator;
    private RelativeLayout Rl_DriveMe;
    private CustomTextView Tv_WhisesLable;
    private CustomTextView Tv_Identy;
    private CustomPagerAdapter myAdapter;
    private AutoScrollViewPager myViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_banner_layout);

        initialize();

        Rl_DriveMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignUpBannerPage.this, SingUpAndSignIn.class);
                startActivity(intent);
                finish();

            }
        });

    }

    private void initialize() {

        pagerIndicator = (CircleIndicator) findViewById(R.id.main_page_VWPGR_indicator);

        Rl_DriveMe = (RelativeLayout) findViewById(R.id.signup_page_drive_me_layout);
        myViewPager = (AutoScrollViewPager) findViewById(R.id.main_page_VWPGR);

        int[] myImageInt = {
                R.drawable.banner1,
                //TSVETAN remove banner for refer frend and book ride for later PICTURES
               // R.drawable.banner2,
                //R.drawable.banner3,
                R.drawable.banner4,
                R.drawable.banner5};


        //TSVETAN remove banner for refer frend and book ride for later TEXTS
        String[] myText = getResources().getStringArray(R.array.titles);
        String[] myTextTsvetan = new String[3];
        myTextTsvetan[0] = myText[0];
        myTextTsvetan[1] = myText[3];
        myTextTsvetan[2] = myText[4];

        myAdapter = new CustomPagerAdapter(SignUpBannerPage.this, myImageInt, myTextTsvetan);
        myViewPager.setAdapter(myAdapter);
        pagerIndicator.setViewPager(myViewPager);
        myViewPager.startAutoScroll();
        myViewPager.setInterval(5000);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

