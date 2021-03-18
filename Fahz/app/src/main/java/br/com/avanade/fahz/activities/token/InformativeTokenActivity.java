package br.com.avanade.fahz.activities.token;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.model.ValidateToken;
import br.com.avanade.fahz.model.ValidateTokenRequest;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static br.com.avanade.fahz.util.Constants.FIRST_ACCESS_TOKEN;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class InformativeTokenActivity extends NavDrawerBaseActivity {

    private ProgressDialog mProgressDialog;

    @BindView(R.id.informativeToken)
    ScrollView mChangeContainer;

    @BindView(R.id.initial_description)
    TextView initialDescription;
    @BindView(R.id.problem_desc)
    TextView problemDesc;

    @BindView(R.id.okButton)
    Button okButton;
    SessionManager sessionManager;

    private ValidateToken tokenResponse = null;
    private ValidateTokenRequest request = new ValidateTokenRequest();

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_token_informative, false);
        ButterKnife.bind(this);

        toolbarTitle.setText(getString(R.string.validade_acess));
        sessionManager = new SessionManager(getApplicationContext());

        mProgressDialog = new ProgressDialog(this);

        String text = " Para continuar com a validação de seu cadastro, em alguns instantes, você vai receber por e-mail ou por SMS o seu token de acesso, que será\n" +
                "        enviado pelo endereço <font color='#005F99'>sistemas.token1@fahz.com.br</font> ou\n" +
                "        <font color='#005F99'>sistemas.token2@fahz.com.br</font>. Fique de olho na sua caixa de Spam.";


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initialDescription.setText(Html.fromHtml(text,  Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
        } else {
            initialDescription.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        }

        problemDesc.setText(Html.fromHtml("<b>" +getString(R.string.step_problem) + "</b>" + " " + getString(R.string.step_descrition_problem)));

        String json = getIntent().getStringExtra(Constants.JSON_VALIDATE_TOKEN);
        tokenResponse = new Gson().fromJson(json, ValidateToken.class);
        sessionManager.createPreference(FIRST_ACCESS_TOKEN,"true");
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    //Clique do botão
    @OnClick(R.id.okButton)
    public void okButton(View view) {
        this.finish();
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mChangeContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mChangeContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    @OnClick(R.id.okButton)
    public void okClick(View view) {
        finish();
        sessionManager.createPreference(FIRST_ACCESS_TOKEN,"false");
        onResume();
    }
}
