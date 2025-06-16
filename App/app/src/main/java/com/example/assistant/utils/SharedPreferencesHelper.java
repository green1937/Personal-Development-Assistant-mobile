package com.example.assistant.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedPreferencesHelper {

    private static final String PREF_FILE = "com.example.assistant.repository.PREFERENCE_FILE";
    private static final String TOKEN_KEY = "auth_token";

    // Метод для сохранения токена
    public static void saveToken(Context context, String token) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply(); // или editor.commit()
    }

    // Метод для получения токена
    public static String getToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(TOKEN_KEY, ""); // значение по умолчанию ""
    }
}
