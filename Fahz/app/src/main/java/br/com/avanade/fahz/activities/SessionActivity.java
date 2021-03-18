package br.com.avanade.fahz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.fragments.LoginFragment;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.NavigationHost;
import br.com.avanade.fahz.util.SessionManager;

public class SessionActivity extends AppCompatActivity implements NavigationHost {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);


        sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            if (savedInstanceState == null) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new LoginFragment())
                        .commit();
            }
        }

    }

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INFORMATIVE_TOKEN_RESULT) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }

}
