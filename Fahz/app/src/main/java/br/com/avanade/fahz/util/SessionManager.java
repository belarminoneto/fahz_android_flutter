package br.com.avanade.fahz.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import br.com.avanade.fahz.activities.SessionActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.application.FahzClaims;

public class SessionManager {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static final String PREF_NAME = "br.com.avanade.fahz";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_CPF = "cpf";
    public static final String KEY_TOKEN = "token";
    private final boolean FIRST_ACCESS_TOKEN = false;

    public SessionManager(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();
    }

    public void createLoginSession(String token) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_TOKEN, token);
        editor.commit();

        FahzApplication.getInstance().setAppClaims(new FahzClaims(token));
        FahzApplication.getInstance().setToken(token);
    }

    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent intent = new Intent(context, SessionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public void logout() {
        editor.clear();
        editor.commit();
        //FahzApplication.getInstance().clearAll();
        FahzApplication.getInstance().onCreate();

        Intent intent = new Intent(context, SessionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public String getUserDefault() {
//        HashMap<String, String> user = new HashMap<String, String>();
//        user.put(KEY_CPF, preferences.getString(KEY_CPF, null));
//        user.put(KEY_TOKEN, preferences.getString(KEY_TOKEN, null));
//
//        return user;
        return preferences.getString(KEY_CPF, null);
    }

    public String getToken(){
        return preferences.getString(KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGIN, false);
    }

    public void createPreference(String name, String value) {
        editor.putString(name, value);
        editor.commit();
    }

    public String getPreference(String name) {
        return preferences.getString(name, "");
    }
}
