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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.poovam.pinedittextfield.SquarePinField;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.ValidateToken;
import br.com.avanade.fahz.model.ValidateTokenRequest;
import br.com.avanade.fahz.model.response.GenericResponse;
import br.com.avanade.fahz.model.response.ValidateTokenResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class InsertTokenActivity extends NavDrawerBaseActivity {

    private ProgressDialog mProgressDialog;

    @BindView(R.id.changePasswordContainer)
    ScrollView mChangeContainer;

    @BindView(R.id.description)
    TextView descriptionText;
    @BindView(R.id.resentCLick)
    TextView resentCLick;

    @BindView(R.id.logout_button)
    Button logoutButton;

    @BindView(R.id.pinField)
    SquarePinField pinView;

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

        setContentView(R.layout.activity_insert_token, false);
        ButterKnife.bind(this);

        String json = getIntent().getStringExtra(Constants.JSON_VALIDATE_TOKEN);
        tokenResponse = new Gson().fromJson(json, ValidateToken.class);

        mProgressDialog = new ProgressDialog(this);


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    //Clique do Botão Salvar (Usando ButterKnife)
    @OnClick(R.id.logout_button)
    public void logout(View view) {

        SessionManager sessionManager = new SessionManager(this);
        sessionManager.logout();
    }

    //Clique do Botão Salvar (Usando ButterKnife)
    @OnClick(R.id.resentCLick)
    public void resent(View view) {

        validateToken();
    }

    @OnTextChanged(R.id.pinField)
    void onTokenFilled(CharSequence text) {

        if (text.length() == 6) {
            sendRequestToken(text.toString());
        }
    }

    private void sendRequestToken(String token)
    {
        setLoading(true, getString(R.string.validating_token));
        APIService apiService = ServiceGenerator.createService(APIService.class);

        Call<JsonElement> call = apiService.validateToken(token);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                    } else {
                        GenericResponse objectResponse = new Gson().fromJson((response.body().getAsJsonObject()), GenericResponse.class);
                        if(objectResponse.commited && objectResponse.result) {
                            showSnackBarDismiss(getString(R.string.validation_success), TYPE_SUCCESS, new Snackbar.Callback() {

                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    Intent intent = new Intent(InsertTokenActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                        else
                        {
                            showSnackBar(getString(R.string.validation_fail), TYPE_FAILURE);
                        }
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

    private void validateToken() {
        APIService apiService = ServiceGenerator.createService(APIService.class);

        Call<JsonElement> call = apiService.validateTokenStatus(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                    } else {
                        ValidateTokenResponse objectResponse = new Gson().fromJson((response.body().getAsJsonObject()), ValidateTokenResponse.class);

                        ValidateToken tokenResponse = objectResponse.result;

                        Intent intent = new Intent(InsertTokenActivity.this, RequestTokenActivity.class);
                        intent.putExtra(Constants.JSON_VALIDATE_TOKEN, new Gson().toJson(tokenResponse));
                        startActivity(intent);
                        finish();

                    }

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
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

}
