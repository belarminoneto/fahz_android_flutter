package br.com.avanade.fahz.activities.token;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.controls.PhoneEditText;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.ValidateToken;
import br.com.avanade.fahz.model.ValidateTokenRequest;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TOKEN_NEVER_DID;
import static br.com.avanade.fahz.util.Constants.TOKEN_ON_FIVE_MINUTES;
import static br.com.avanade.fahz.util.Constants.TOKEN_VALID_OUT_FIVE_MINUTES;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class RequestTokenActivity extends NavDrawerBaseActivity {

    private ProgressDialog mProgressDialog;

    @BindView(R.id.changePasswordContainer)
    ScrollView mChangeContainer;

    @BindView(R.id.description)
    TextView descriptionText;
    @BindView(R.id.phone_edittext)
    TextInputEditText phoneEditText;
    @BindView(R.id.email_edittext)
    TextInputEditText emailEditText;
    @BindView(R.id.email_button)
    Button emailButton;
    @BindView(R.id.logout_button)
    Button logoutButton;
    @BindView(R.id.sms_button)
    Button smsButton;
    @BindView(R.id.insertTokenButton)
    Button insertTokenButton;


    SessionManager sessionManager;

    private ValidateToken tokenResponse = null;
    private ValidateTokenRequest request = new ValidateTokenRequest();

    @Override
    protected void onResume() {
        super.onResume();
    }

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

        setContentView(R.layout.activity_request_token, false);
        ButterKnife.bind(this);

        toolbarTitle.setText(getString(R.string.validade_acess));
        sessionManager = new SessionManager(getApplicationContext());

        mProgressDialog = new ProgressDialog(this);

        String json = getIntent().getStringExtra(Constants.JSON_VALIDATE_TOKEN);
        tokenResponse = new Gson().fromJson(json, ValidateToken.class);
        emailEditText.setText(tokenResponse.getEmail());
        phoneEditText.setText(tokenResponse.getCellphone());

        smsButton.setFocusableInTouchMode(true);
        smsButton.requestFocus();
        int status = tokenResponse.getStatusTokenValidation();

        if (status == TOKEN_ON_FIVE_MINUTES || status == TOKEN_VALID_OUT_FIVE_MINUTES) {
            String expirationDate = DateEditText.parseCompleteToCalendar(tokenResponse.getExpireDate(), "");
            descriptionText.setText(String.format(getString(R.string.validation_token_desc_validate), expirationDate));
            if (status == TOKEN_ON_FIVE_MINUTES) {
                buttonEnabledEmail(false);
                buttonEnabledPhone(false);
            } else {
                buttonEnabledEmail(true);
                buttonEnabledPhone(true);
            }
        } else if (status == TOKEN_NEVER_DID) {
            descriptionText.setText(getString(R.string.validation_token_desc));
            buttonEnabledEmail(true);
            buttonEnabledPhone(true);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void buttonEnabledEmail(boolean isButtonEnabled) {
        if(isButtonEnabled) {
            emailButton.setEnabled(true);
            emailButton.setTextColor(getResources().getColor(R.color.white_text));

            insertTokenButton.setTextColor(getResources().getColor(R.color.grey_text));
            insertTokenButton.setEnabled(false);
        } else {
            emailButton.setEnabled(false);
            emailButton.setTextColor(getResources().getColor(R.color.grey_text));

            insertTokenButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            insertTokenButton.setEnabled(true);
        }
    }

    private void buttonEnabledPhone(boolean isButtonEnabled) {
        if(isButtonEnabled) {
            smsButton.setEnabled(true);
            smsButton.setTextColor(getResources().getColor(R.color.white_text));

            insertTokenButton.setTextColor(getResources().getColor(R.color.grey_text));
            insertTokenButton.setEnabled(false);
        } else {
            smsButton.setEnabled(false);
            smsButton.setTextColor(getResources().getColor(R.color.grey_text));
            insertTokenButton.setTextColor(getResources().getColor(R.color.grey_text));

            insertTokenButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            insertTokenButton.setEnabled(true);
        }
    }

    //Clique do Botão Salvar (Usando ButterKnife)
    @OnClick(R.id.insertTokenButton)
    public void insertToken(View view) {
        Intent intent = new Intent(RequestTokenActivity.this, InsertTokenActivity.class);
        intent.putExtra(Constants.JSON_VALIDATE_TOKEN, new Gson().toJson(tokenResponse));
        startActivity(intent);
        finish();

    }

    //Clique do Botão Salvar (Usando ButterKnife)
    @OnClick(R.id.sms_button)
    public void smsRequest(View view)
    {
        if(!phoneEditText.getText().toString().equals("") && !Utils.validatePhone(phoneEditText.getText().toString()))
        {
            showSnackBar(getString(R.string.not_valid_cellphone), TYPE_FAILURE);
        }
        else if(!phoneEditText.getText().toString().equals("") && !Utils.validateCellPhoneNumber(phoneEditText.getText().toString()))
        {
            int resID = getResources().getIdentifier("MSG371", "string", getPackageName());
            String message = getResources().getString(resID);
            showSnackBar(message, TYPE_FAILURE);
        }
        else if(!phoneEditText.getText().toString().equals(""))
        {
            sendRequestToken(ValidateTokenRequest.SendMethod.SMS);
        }
        else
        {
            showSnackBar(getString(R.string.fill_sms), TYPE_FAILURE);
        }
    }

    @OnClick(R.id.email_button)
    public void emailRequest(View view) {
        if(!emailEditText.getText().toString().equals("") && isEmailValid(emailEditText.getText().toString()))
        {
            sendRequestToken(ValidateTokenRequest.SendMethod.EMAIL);
        }
        else if(emailEditText.getText().toString().equals(""))
        {
            showSnackBar(getString(R.string.fill_email), TYPE_FAILURE);
        }
        else if(!isEmailValid(emailEditText.getText().toString()))
        {
            showSnackBar(getString(R.string.not_valid_email), TYPE_FAILURE);
        }

    }

    //Clique do Botão Salvar (Usando ButterKnife)
    @OnClick(R.id.logout_button)
    public void logout(View view) {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.logout();
    }

    @OnTextChanged(R.id.phone_edittext)
    void onTextChangedPhone(CharSequence text) {

        if (tokenResponse.getCellphone() != null) {
            if (!tokenResponse.getCellphone().equals(phoneEditText.getText().toString()))
                buttonEnabledPhone(true);
            else
                buttonEnabledPhone(false);
        } else if (!phoneEditText.getText().toString().equals("")) {
            buttonEnabledPhone(true);
        } else {
            buttonEnabledPhone(false);
        }
    }

    @OnTextChanged(R.id.email_edittext)
    void onTextChanged(CharSequence text) {
        if (tokenResponse.getEmail() != null) {
            if (!tokenResponse.getEmail().equals(emailEditText.getText().toString()))
                buttonEnabledEmail(true);
            else
                buttonEnabledEmail(false);
        } else if (!emailEditText.getText().toString().equals("")) {
            buttonEnabledEmail(true);
        } else {
            buttonEnabledEmail(false);
        }
    }

    public void ValidateEmail()
    {
        if(!emailEditText.getText().toString().equals("") && isEmailValid(emailEditText.getText().toString()))
        {
            request.setEmail(emailEditText.getText().toString());
        }
    }
    public void ValidatePhone()
    {
        String cellPhoneNumber = phoneEditText.getText().toString();

        if(!cellPhoneNumber.equals("") && Utils.validateCellPhoneNumber(cellPhoneNumber))
        {
            request.setCellPhone(PhoneEditText.unmaskPhone(cellPhoneNumber));
        }
    }

    private void sendRequestToken(ValidateTokenRequest.SendMethod method)
    {
        request.setSendMethod(method);
        ValidateEmail();
        ValidatePhone();

        setLoading(true, getString(R.string.saving_information));
        APIService apiService = ServiceGenerator.createService(APIService.class);

        Call<JsonElement> call = apiService.createTokenRequest(request);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);

                        if(responseCommit.commited) {
                            showSnackBarDismiss(responseCommit.messageIdentifier, TYPE_SUCCESS, new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    buttonEnabledEmail(false);
                                    buttonEnabledPhone(false);

                                    Intent intent = new Intent(RequestTokenActivity.this, InsertTokenActivity.class);
                                    intent.putExtra(Constants.JSON_VALIDATE_TOKEN, new Gson().toJson(tokenResponse));
                                    startActivity(intent);
                                    finish();
                                }
                            });

                        }
                        else
                            showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                    }

                    setLoading(false, "");

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), TYPE_FAILURE);
                    } finally {
                        setLoading(false, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
                setLoading(false, "");
            }
        });
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

    private void setLoading(Boolean loading){
        if(loading){
            mProgressDialog.setMessage("Aguarde um momento");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        else{
            mProgressDialog.dismiss();
        }
    }

    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

    private void setLoading(Boolean loading, String text) {
        if (loading) {
            if (mProgressDialog != null) {
                mProgressDialog.setMessage(text);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        } else {
            try {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } catch (Exception ex) {
                LogUtils.error("MainActivity - Loading", ex);
            }
        }
    }

    @OnFocusChange(R.id.phone_edittext)
    void onFocusChanged(boolean focused) {
        if (focused) {
            phoneEditText.setHint(getString(R.string.placeholder_cellphone_token));
        } else {
            phoneEditText.setHint("");
        }
    }

    @OnFocusChange(R.id.email_edittext)
    void onFocusChangedEmail(boolean focused) {
        if (focused) {
            emailEditText.setHint(getString(R.string.placeholder_email_token));
        } else {
            emailEditText.setHint("");
        }
    }

}
