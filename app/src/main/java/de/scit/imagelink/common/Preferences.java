package de.scit.imagelink.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Preferences {

    private static final String SCIT_PREFERENCES = "scit_pref";

    public static final String PREFERENCE_SERVER_IP = "server_ip";

    private static Context ctx;

    public static void setPreference(Context ctx, String key, String value) {
        updateContext(ctx);
        SharedPreferences sharedPref = Preferences.ctx.getSharedPreferences(SCIT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringPreference(Context ctx, String key, String defaultValue) {
        Log.i("Pref", "Read from preferences: " + key);
        updateContext(ctx);
        SharedPreferences sharedPref = Preferences.ctx.getSharedPreferences(SCIT_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultValue);
    }

    private static void updateContext(Context ctx) {
        if (ctx != null) {
            Preferences.ctx = ctx;
        }

        if (ctx == null && Preferences.ctx == null) {
            throw new IllegalStateException("Context was never initialized");
        }
    }
}
