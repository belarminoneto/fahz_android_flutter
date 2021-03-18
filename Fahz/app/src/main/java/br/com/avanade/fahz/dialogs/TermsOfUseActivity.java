package br.com.avanade.fahz.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.InsertTermOfUseResquest;
import br.com.avanade.fahz.model.response.TermOfUseResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class TermsOfUseActivity extends AppCompatActivity {

    @BindView(R.id.terms_container)
    RelativeLayout mTermsContainer;
    @BindView(R.id.txtTermText)
    TextView txtTermText;
    @BindView(R.id.btnDialogSave)
    Button btnDialogSave;
    SessionManager sessionManager;
    String termOfUseTowork = "";
    Context mContext;
    boolean fromBenefit;
    boolean fromRegister;
    boolean fromFeature;
    String mCPF;
    TermOfUseResponse responseTerm = new TermOfUseResponse();

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.dialog_terms_of_use);
        ButterKnife.bind(this);

        termOfUseTowork = getIntent().getStringExtra(Constants.TERMS_OF_USE_SELECTED);
        mCPF = getIntent().getStringExtra(Constants.TERMS_OF_USE_CPF);

        if (mCPF == null || mCPF.equals(""))
            mCPF = FahzApplication.getInstance().getFahzClaims().getCPF();
        fromBenefit = getIntent().getBooleanExtra(Constants.TERMS_OF_USE_FROM_BENEFIT, false);
        fromRegister = getIntent().getBooleanExtra(Constants.TERMS_OF_USE_FROM_REGISTER, false);
        fromFeature = getIntent().getBooleanExtra(Constants.TERMS_OF_USE_FROM_FEATURE, false);
        sessionManager = new SessionManager(getApplicationContext());
        mContext = this;

        mProgressDialog = new ProgressDialog(this);
        Spanned sp = Html.fromHtml("");
        txtTermText.setText(sp);
        buttonEnabled(false);
        getTermOfUse();
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mTermsContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void setLoading(Boolean loading) {
        if (loading) {
            mProgressDialog.setMessage("Aguarde um momento");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    @OnClick(R.id.btnDialogSave)
    public void submit(View view) {

        saveTermOfUse(Constants.TERM_ACCEPT);

        Intent intent = new Intent();
        intent.putExtra(Constants.TERMS_OF_USE_SELECTED, termOfUseTowork);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.btnDialogCancel)
    public void cancel(View view) {

        saveTermOfUse(Constants.TERM_DECLINED);
        Intent intent = new Intent();
        intent.putExtra(Constants.TERMS_OF_USE_SELECTED, termOfUseTowork);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    //PARA BLOQUEAR O BACK E TER OPÇÔES APENAS NO BOTÂO
    @Override
    public void onBackPressed() {
    }

    private void getTermOfUse() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true);

            APIService mAPIService = ServiceGenerator.createService(APIService.class);
            Call<JsonElement> call = mAPIService.getTermByCode(termOfUseTowork);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful() && !response.body().toString().contains("messageIdentifier")) {

                        try {
                            responseTerm = new Gson().fromJson((response.body().getAsJsonObject()), TermOfUseResponse.class);

                            if (responseTerm.FinalDate.equals("")) {
                                Spanned sp = Html.fromHtml(responseTerm.Text);
                                txtTermText.setText(sp);
                                saveTermOfUse(Constants.TERM_READ);
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra(Constants.TERMS_OF_USE_SELECTED, termOfUseTowork);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        } catch (IllegalStateException ex) {
                            LogUtils.error(getLocalClassName(), ex);
                        }
                    } else if(response.isSuccessful() && response.body().toString().contains("messageIdentifier"))
                    {
                        try {
                            String data = response.body().toString();
                            JSONObject jObjError = new JSONObject(data);
                            String message = jObjError.getString("messageIdentifier");
                            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, mContext, null);
                            setLoading(false);
                        }
                        catch (Exception ex){
                            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, mContext, null);
                        }
                    }
                    else {
                        String message;
                        String code;
                        try {
                            String data =response.errorBody().toString();
                            JSONObject jObjError = new JSONObject(data);
                            message = jObjError.getString("Message");
                            code = jObjError.getString("Code");
                            if (code.equals(Constants.ERROR_UNAUTHORIZED) &&
                                    message.equals(Constants.ERROR_DESCRIPTION_UNAUTHORIZED)) {
                                logoutUser();
                            } else {
                                setLoading(false);
                                CommitResponse commitResponse = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                                Utils.showSimpleDialog(getString(R.string.dialog_title), commitResponse.messageIdentifier, null, mContext, null);
                            }
                        }
                        catch (Exception ex){
                            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, mContext, null);
                        }
                    }

                    setLoading(false);
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    setLoading(false);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), t.getMessage(), null, getBaseContext(), null);
                }
            });
        }
    }

    private void logoutUser() {
        setLoading(true);
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.logout();
    }

    private void saveTermOfUse(int typeAcceptedTerm) {
        if (sessionManager.isLoggedIn()) {
            setLoading(true);

            InsertTermOfUseResquest request = getInsertTermOfUseResquest(typeAcceptedTerm);

            APIService mAPIService = ServiceGenerator.createService(APIService.class);
            Call<JsonElement> call = mAPIService.accepetTerm(request);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {
                        setLoading(false);

                        CommitResponse commitResponse = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        if (!commitResponse.commited) {
                            if (mContext != null) {
                                Utils.showSimpleDialog(getString(R.string.dialog_title), commitResponse.messageIdentifier, null, mContext, null);
                            }
                        }
                    } else {
                        String message;
                        String code;
                        try {
                            String data = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(data);
                            message = jObjError.getString("Message");
                            code = jObjError.getString("Code");
                            if (code.equals(Constants.ERROR_UNAUTHORIZED) &&
                                    message.equals(Constants.ERROR_DESCRIPTION_UNAUTHORIZED)) {
                                logoutUser();
                            } else {
                                setLoading(false);
                                CommitResponse commitResponse = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                                Utils.showSimpleDialog(getString(R.string.dialog_title), commitResponse.messageIdentifier, null, mContext, null);
                            }
                        } catch (Exception ex) {
                            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, mContext, null);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    setLoading(false);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), t.getMessage(), null, mContext, null);
                }
            });
        }
    }

    @NotNull
    private InsertTermOfUseResquest getInsertTermOfUseResquest(int typeAcceptedTerm) {
        InsertTermOfUseResquest request = new InsertTermOfUseResquest();
        request.code = termOfUseTowork;
        request.cpf = mCPF.replace(".", "").replace("-", "");
        request.version = responseTerm.lastVersion;
        request.typeAcceptedTerm = typeAcceptedTerm;
        return request;
    }

    @OnCheckedChanged(R.id.check_accept_terms)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            buttonEnabled(true);
        else
            buttonEnabled(false);
    }

    private void buttonEnabled(boolean isButtonEnabled) {
        if(isButtonEnabled) {
            btnDialogSave.setEnabled(true);
            btnDialogSave.setTextColor(getResources().getColor(R.color.white_text));
            btnDialogSave.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            btnDialogSave.setEnabled(false);
            btnDialogSave.setTextColor(getResources().getColor(R.color.grey_text));
            btnDialogSave.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }
}
