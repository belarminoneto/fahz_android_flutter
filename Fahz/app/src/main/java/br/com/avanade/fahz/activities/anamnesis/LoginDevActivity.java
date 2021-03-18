package br.com.avanade.fahz.activities.anamnesis;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.interfaces.OnSessionListener;

import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;

public class LoginDevActivity extends BaseAnamnesisActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        if (userAnamnesisSession.getToken() == null) {
            getSession(new OnSessionListener() {
                @Override
                public void onSessionSuccess() {
                    goToSearchActivity();
                }
            });
        } else {
            goToSearchActivity();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login_dev;
    }

    void goToSearchActivity() {
        Intent intent = new Intent(this, SearchLifeActivity.class);
        startActivity(intent);
        finish();
    }
}
