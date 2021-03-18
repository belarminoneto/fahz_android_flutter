package br.com.avanade.fahz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import br.com.avanade.fahz.R;

public class SplashActivity extends AppCompatActivity {

    private static boolean isActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            /*
             * Exibindo splash com um timer.
             */
            @Override
            public void run() {
                // Esse método será executado sempre que o timer acabar
                // E inicia a activity principal
                if(isActive()) {
                    Intent i = new Intent(SplashActivity.this, SessionActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 3000);

    }

    public static boolean isActive() {
        return isActive;
    }

    public static void setIsActive(boolean isActive) {
        SplashActivity.isActive = isActive;
    }

}
