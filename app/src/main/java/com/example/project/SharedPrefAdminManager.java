package com.example.project;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefAdminManager {
    private static SharedPrefAdminManager mInstance;
    private static Context mCtx;

    private static final String SHARED_PREF_NAME = "adminsharedpref";
    private static final String KEY_ADMINNAME = "adminname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_ADMINID = "adminid";
    private static final String KEY_ADMINPASSWORD = "adminpassword";

    private SharedPrefAdminManager(Context context) {
        mCtx = context.getApplicationContext();
    }

    public static synchronized SharedPrefAdminManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefAdminManager(context);
        }
        return mInstance;
    }

    public boolean adminLogin(int id, String adminname, String email, String address, String password) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ADMINNAME, adminname);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ADDRESS, address);
        editor.putInt(KEY_ADMINID, id);
        editor.putString(KEY_ADMINPASSWORD, password);
        editor.apply();
        return true;
    }

    public int getAdminID() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ADMINID, -1); // Return -1 if no ID is found
    }

    public String getAdminName() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ADMINNAME, null);
    }

    public String getEmail() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getAddress() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ADDRESS, null);
    }

    public String getPassword() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ADMINPASSWORD, null);
    }

    public boolean isLogIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ADMINID, -1) != -1;
    }

    public boolean logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }
}

