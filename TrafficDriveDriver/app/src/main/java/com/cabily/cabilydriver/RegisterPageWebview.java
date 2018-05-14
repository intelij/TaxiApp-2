package com.cabily.cabilydriver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.Hockeyapp.ActionBarActivityHockeyApp;
import com.app.service.ServiceConstant;
import com.cabily.cabilydriver.Utils.ConnectionDetector;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.widgets.PkDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 */
public class RegisterPageWebview extends ActionBarActivityHockeyApp {

    SessionManager session;
    private WebView mWebView;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String weburl = ServiceConstant.Register_URL;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=1;
    private RelativeLayout back;
    private ProgressBar progressBar;
    private String sLang = "";

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_webview);
        initilize();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    onBackPressed();
                }
            }
        });
    }

    private void initilize() {
        session = new SessionManager(RegisterPageWebview.this);
        mWebView = (WebView) findViewById(R.id.register_webView);
        progressBar = (ProgressBar) findViewById(R.id.registration_payment_webview_progressbar);
        back = (RelativeLayout) findViewById(R.id.registration_webview_header_back_layout);
        WebSettings webSettings = mWebView.getSettings();
        mWebView.getSettings().setJavaScriptEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);

        mWebView.getSettings().setLoadWithOverviewMode(true);

        // Other webview settings
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setSupportZoom(true);

        if(Build.VERSION.SDK_INT >= 21){
            webSettings.setMixedContentMode(0);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT >= 19){
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT >=11 && Build.VERSION.SDK_INT < 19){
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mWebView.setWebViewClient(new Callback());

        HashMap<String, String> language = session.getLanaguage();
        sLang = language.get(SessionManager.KEY_Language);
        System.out.println("--------sLang------"+sLang);

        if ("".equalsIgnoreCase(sLang))
        {
            sLang = "en";
        }


        cd = new ConnectionDetector(RegisterPageWebview.this);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            System.out.println("weburl============="+weburl+"?lang="+sLang);
            mWebView.loadUrl(weburl+"?lang="+sLang);
            mWebView.setWebChromeClient(new WebChromeClient(){

                @Override
                public void onProgressChanged(WebView view, int progress) {
                    if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                    }
                    progressBar.setProgress(progress);

                    if (progress == 100) {
                        progressBar.setVisibility(ProgressBar.GONE);
                    }
                }


                //For Android 3.0+
                public void openFileChooser(ValueCallback<Uri> uploadMsg){
                    mUM = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    RegisterPageWebview.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FCR);
                }
                // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
                public void openFileChooser(ValueCallback uploadMsg, String acceptType){
                    mUM = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    RegisterPageWebview.this.startActivityForResult(
                            Intent.createChooser(i, "File Browser"),
                            FCR);
                }
                //For Android 4.1+
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                    mUM = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    RegisterPageWebview.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), RegisterPageWebview.FCR);
                }
                //For Android 5.0+
                public boolean onShowFileChooser(
                        WebView webView, ValueCallback<Uri[]> filePathCallback,
                        WebChromeClient.FileChooserParams fileChooserParams){
                    if(mUMA != null){
                        mUMA.onReceiveValue(null);
                    }
                    System.out.println("1");
                    mUMA = filePathCallback;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(takePictureIntent.resolveActivity(RegisterPageWebview.this.getPackageManager()) != null){
                        System.out.println("2");
                        File photoFile = null;
                        try{
                            System.out.println("3");
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCM);
                        }catch(IOException ex){
                            Log.e("dgd", "Image file creation failed", ex);
                        }
                        System.out.println("4");
                        if(photoFile != null){
                            System.out.println("5");
                            mCM = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        }else{
                            System.out.println("6");
                            takePictureIntent = null;
                        }
                    }
                    System.out.println("7");
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("*/*");
                    Intent[] intentArray;
                    if(takePictureIntent != null){
                        System.out.println("8");
                        intentArray = new Intent[]{takePictureIntent};
                    }else{
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, FCR);
                    return true;
                }
            });

        } else {
            Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }



    // Create an image file
    private File createImageFile() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }


    private class Callback extends WebViewClient {  //HERE IS THE MAIN CHANGE.

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (false);
        }

        //Show loader on url load
        @Override
        public void onLoadResource(WebView view, String url) {


        }

        @Override
        public void onPageFinished(WebView view, String url) {
            System.out.println("-------jai   url-------------"+url);
            try {

                if (url.contains("app/driver/signup/success")) {
                    mWebView.clearHistory();

                    Alert_finish(getResources().getString(R.string.lbel_alert_thanks), getResources().getString(R.string.lbel_alert_register_success));
                    System.out.println(" jai page redirected");
                }
                else
                {
                    System.out.println("jai page  not redirected");
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    private void Alert_finish(String title, String message) {
        final PkDialog mDialog = new PkDialog(RegisterPageWebview.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
            }
        });
        mDialog.show();
    }


    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(RegisterPageWebview.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if(Build.VERSION.SDK_INT >= 21){
            Uri[] results = null;
            //Check if response is positive
            if(resultCode== Activity.RESULT_OK){
                if(requestCode == FCR){
                    if(null == mUMA){
                        return;
                    }
                    if(intent == null || intent.getDataString() == null){
                        //Capture Photo if no image available
                        if(mCM != null){
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    }else{
                        String dataString = intent.getDataString();
                        if(dataString != null){
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        }else{
            if(requestCode == FCR){
                if(null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }


}
