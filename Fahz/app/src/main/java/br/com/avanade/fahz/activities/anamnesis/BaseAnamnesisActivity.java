package br.com.avanade.fahz.activities.anamnesis;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.interfaces.ISimpleDialogOkAction;
import br.com.avanade.fahz.interfaces.OnSessionListener;
import br.com.avanade.fahz.model.anamnesisModel.LoginAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.LoginAnamnesisResponse;
import br.com.avanade.fahz.network.APIServiceAnamnesis;
import br.com.avanade.fahz.util.Utils;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.network.ServiceGeneratorAnamnesis.createAnamnesisService;
import static br.com.avanade.fahz.network.ServiceGeneratorAnamnesis.createAnamnesisServiceNoToken;
import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;

public abstract class BaseAnamnesisActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    Dialog dialog;
    APIServiceAnamnesis mApiService;
    public ISimpleDialogOkAction simpleDialogOkAction = this::finish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(getLayoutResourceId());
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        mApiService = createAnamnesisService(APIServiceAnamnesis.class);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected abstract int getLayoutResourceId();

    void setLoading(Boolean loading) {
        if (loading) {
            mProgressDialog.setMessage(getResources().getString(R.string.wait_moment));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    void showAlert(String msg) {
        Utils.showSimpleDialog(getString(R.string.dialog_title), msg, getString(R.string.close), this, null);
    }

    void showAlert(String msg, @Nullable ISimpleDialogOkAction simpleDialogOkAction) {
        Utils.showSimpleDialog(getString(R.string.dialog_title), msg, getString(R.string.close), this, simpleDialogOkAction);
    }

    void getSession(final OnSessionListener callback) {
        setLoading(true);
        mApiService = createAnamnesisServiceNoToken(APIServiceAnamnesis.class);
        Call<LoginAnamnesisResponse> call = mApiService.loginWithoutPendencies(new LoginAnamnesis(userAnamnesisSession.getUserCPF(), this));
        call.enqueue(new Callback<LoginAnamnesisResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginAnamnesisResponse> call, @NonNull Response<LoginAnamnesisResponse> response) {
                LoginAnamnesisResponse anamnesisResponse = response.body();
                if (response.isSuccessful()) {
                    assert anamnesisResponse != null;
                    String accessToken = anamnesisResponse.getToken();
                    int environment = anamnesisResponse.getEnvironment();
                    if (accessToken != null) {
                        userAnamnesisSession.setEnvironment(environment);
                        userAnamnesisSession.setToken(accessToken);
                        mApiService = createAnamnesisService(APIServiceAnamnesis.class);
                        callback.onSessionSuccess();
                    } else {
                        showAlert(getString(R.string.problemServer), () -> finish());
                    }
                } else {
                    showAlert(getString(R.string.problemServer), () -> finish());
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@Nullable Call<LoginAnamnesisResponse> call, @NonNull Throwable t) {
                setLoading(false);
                if (t instanceof SocketTimeoutException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), () -> finish());
                else if (t instanceof UnknownHostException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), () -> finish());
                else {
                    LogUtils.error(getLocalClassName(), t);
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), () -> finish());
                }
            }
        });
    }

}
