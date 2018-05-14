package com.cabily.cabilydriver.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by user88 on 4/26/2016.
 */
public class SessionManager_Applaunch {
    // Shared Preferences
    static SharedPreferences pref;
    // Editor for Shared preferences
    static SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "Ride247";

    public static final String KEY_PHONE_MASK = "phonemask";


    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager_Applaunch(Context context) {
        this._context = context;
        if(pref == null && editor == null) {
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }

    }

    public void createSessionPhoneMask(String phonemask){
        editor.putString(KEY_PHONE_MASK, phonemask);
        editor.commit();
    }


    public HashMap<String, String> getphonemaskDetails() {
        HashMap<String, String> online = new HashMap<String, String>();
        online.put(KEY_PHONE_MASK, pref.getString(KEY_PHONE_MASK, ""));
        return online;
    }

}
