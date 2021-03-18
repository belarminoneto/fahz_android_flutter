package br.com.avanade.fahz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.ChangePasswordRequest;
import br.com.avanade.fahz.model.ChangePasswordResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ChangePasswordActivity extends NavDrawerBaseActivity {

    private ProgressDialog mProgressDialog;
    private SessionManager mSessionManager;
    public static final String CPF_BUNDLE_KEY = "CPF";
    public static final String PASSWORD_BUNDLE_KEY = "PASSWORD";
    boolean hasChangePassword = false;

    @BindView(R.id.changePasswordContainer)
    ScrollView mChangeContainer;

    @BindView(R.id.currentPassword_textInput)
    TextInputLayout currentPasswordTextInput;
    @BindView(R.id.currentPassword_editText)
    TextInputEditText currentPasswordEditText;
    @BindView(R.id.newPassword_textInput)
    TextInputLayout newPasswordTextInput;
    @BindView(R.id.newPassword_editText)
    TextInputEditText newPasswordEditText;
    @BindView(R.id.confirmNewPassword_textInput)
    TextInputLayout confirmNewPasswordTextInput;
    @BindView(R.id.confirmNewPassword_editText)
    TextInputEditText confirmNewPasswordEditText;
    @BindView(R.id.save_button)
    Button saveButton;
    SessionManager sessionManager;

    String cpf = "";


    @Override
    protected void onResume() {
        super.onResume();

        SessionManager sessionManager = new SessionManager(this);

        if(sessionManager.getToken() == null || sessionManager.getToken().isEmpty()){
            sessionManager.logout();
        }
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
        hasChangePassword = getIntent().getBooleanExtra(Constants.HAS_CHANGE_PASSWORD, false);

        setContentView(R.layout.activity_change_password,!hasChangePassword);
        ButterKnife.bind(this);

        toolbarTitle.setText(getString(R.string.initialPage));
        sessionManager = new SessionManager(getApplicationContext());


        cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        mProgressDialog = new ProgressDialog(this);

        buttonEnabled(false);

        //Verifica se a informação de senha atual está preenchida para comportamento de tela
        currentPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (currentPasswordTextInput.isErrorEnabled()) {
                    currentPasswordTextInput.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 8) {
                    buttonEnabled(false);
                } else {
                    if (!TextUtils.isEmpty(newPasswordEditText.getText().toString()) &&
                            !TextUtils.isEmpty(confirmNewPasswordEditText.getText().toString())) {
                        buttonEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Verifica se a informação da nova senha está preenchida para comportamento de tela
        newPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (newPasswordTextInput.isErrorEnabled()) {
                    newPasswordTextInput.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 8) {
                    buttonEnabled(false);
                } else if (!TextUtils.isEmpty(currentPasswordEditText.getText().toString()) &&
                        !TextUtils.isEmpty(confirmNewPasswordEditText.getText().toString())) {
                    buttonEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Verifica se a informação da confirmação nova senha está preenchida para comportamento de tela
        confirmNewPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (confirmNewPasswordTextInput.isErrorEnabled()) {
                    confirmNewPasswordTextInput.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 8) {
                    buttonEnabled(false);
                } else if (!TextUtils.isEmpty(currentPasswordEditText.getText().toString()) &&
                        !TextUtils.isEmpty(newPasswordEditText.getText().toString())) {
                    buttonEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void buttonEnabled(boolean isButtonEnabled) {
        if(isButtonEnabled) {
            saveButton.setEnabled(true);
            saveButton.setTextColor(getResources().getColor(R.color.white_text));
        } else {
            saveButton.setEnabled(false);
            saveButton.setTextColor(getResources().getColor(R.color.grey_text));
        }
    }

    //Clique do Botão Salvar (Usando ButterKnife)
    @OnClick(R.id.save_button)
    public void submit(View view) {
        String currentPassword = currentPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmNewPassword = confirmNewPasswordEditText.getText().toString();
        if (newPassword.equals(confirmNewPassword)) {
            postChangePassword(cpf, currentPassword, newPassword);
        } else if (currentPassword.equals(newPassword)) {
            newPasswordTextInput.setError("A nova senha não pode ser igual a senha atual");
        } else {
            confirmNewPasswordTextInput.setError("As senhas não conferem");
        }
    }

    //Chamada para o endpoint de Troca de Denha
    private void postChangePassword(String cpf, String currentPassword, String newPassword) {
        if (sessionManager.isLoggedIn()) {
            setLoading(true);

            final ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
            changePasswordRequest.cpf = cpf;
            changePasswordRequest.currentPassword = currentPassword;
            changePasswordRequest.newPassword = newPassword;

            //Criação do Serviço Rest para chamada de Endpoint
            APIService apiService = ServiceGenerator.createService(APIService.class);
            Call<ChangePasswordResponse> call = apiService.changePassword(changePasswordRequest);
            call.enqueue(new Callback<ChangePasswordResponse>() {
                @Override
                public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                    FahzApplication.getInstance().setToken((response.raw().headers()).get("token"));
                    if (response.isSuccessful()) {
                        ChangePasswordResponse response2 = response.body();

                        //Verifica se A informação foi comitada com sucesso
                        if (response2!= null && response2.commited) {
                            int resID = getResources().getIdentifier("MSG044", "string", getPackageName());
                            String message = getResources().getString(resID);
                            showSnackBarDismiss(message, TYPE_SUCCESS, new Snackbar.Callback(){
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if (hasChangePassword) {
                                        startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                                        setLoading(false);
                                        finish();
                                    }
                                    else
                                    {
                                        startActivity(new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class));
                                        setLoading(false);
                                        finish();
                                    }
                                }
                            });

                        } else {
                            showSnackBar(response2.message.replaceAll("<BR/>",""), TYPE_FAILURE);
                        }
                    }
                    setLoading(false);
                }

                @Override
                public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                    setLoading(false);

                    if(t instanceof SocketTimeoutException)
                        showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                    else if(t instanceof UnknownHostException)
                        showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            });
        }
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
}
