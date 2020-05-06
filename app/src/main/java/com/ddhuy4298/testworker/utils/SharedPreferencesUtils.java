package com.ddhuy4298.testworker.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    private SharedPreferences preferences;

    public SharedPreferencesUtils(Context context) {
        preferences = context.getSharedPreferences(
                getClass().getSimpleName(),
                Context.MODE_PRIVATE
        );
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getValue(String key) {
        return preferences.getString(key, null);
    }

    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }
}
