package br.com.avanade.fahz.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.messaging.FirebaseMessaging;

import br.com.avanade.fahz.util.SessionManager;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;

public class FahzApplication extends Application {
    private static FahzApplication instance;
    private static Context appContext;
    private static FahzClaims mClaims;
    private static String mToken;
    private static FlutterEngine flutterEngine;


    public static FahzApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context appContext){
        FahzApplication.appContext = appContext;
    }

    public FahzClaims getFahzClaims(){
        return mClaims;
    }

    public void setAppClaims(FahzClaims value){
        mClaims = value;
    }

    public static FlutterEngine getFlutterEngine(){return flutterEngine;}

    public String getToken(){ return mToken; }
    public void setToken(String value) {
        if (value != null) {
            FahzApplication.getInstance().setAppClaims(new FahzClaims(value));
            mToken = value;
        }
    }

    public void createApplication()
    {
        onCreate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        this.setAppContext(getApplicationContext());

        SharedPreferences preferences = getBaseContext().getSharedPreferences(SessionManager.PREF_NAME, Context.MODE_PRIVATE);
        setAppClaims(new FahzClaims(preferences.getString(SessionManager.KEY_TOKEN, "")));
        setToken(preferences.getString(SessionManager.KEY_TOKEN, ""));

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("font/opensans_semibold.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build());

        FirebaseMessaging.getInstance().subscribeToTopic("allfahz");

        flutterEngine = new FlutterEngine(this);
        flutterEngine.getDartExecutor().executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault());
        FlutterEngineCache
                .getInstance()
                .put("fahzv2_engine", flutterEngine);
    }

    public void clearAll()
    {
        mClaims = new FahzClaims("");
        mToken = "";

    }
}