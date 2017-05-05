package com.grocus.grocustotalservice.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JoseLuis on 17/07/16.
 */
public class Preferences {

    public static final String GROCUS_PREF = "grocus_pref";

    public static void savePreference(Activity context, String key, String values){
        SharedPreferences sharedpreferences = context.getSharedPreferences(GROCUS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, values);
        editor.apply();
    }

    public static String getPreference(Activity context, String key){
        SharedPreferences sharedpreferences = context.getSharedPreferences(GROCUS_PREF, Context.MODE_PRIVATE);
        return sharedpreferences.getString(key, "");
    }

}
