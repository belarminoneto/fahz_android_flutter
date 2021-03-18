package br.com.avanade.fahz.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import br.com.avanade.fahz.BuildConfig;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.lgpdModel.AcceptData;
import br.com.avanade.fahz.model.lgpdModel.PoliciesAndTerm;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TERM_ACCEPT;
import static br.com.avanade.fahz.util.Constants.TERM_DECLINED;
import static br.com.avanade.fahz.util.Constants.TERM_READ;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class CheckPendingPoliciesActivity extends AppCompatActivity {

    @BindView(R.id.terms_container)
    RelativeLayout mTermsContainer;
    @BindView(R.id.txtTermText)
    TextView txtTermText;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.btnDialogSave)
    Button btnDialogSave;
    @BindView(R.id.text_qtd_policies_pending)
    TextView text_qtd_policies_pending;
    @BindView(R.id.check_accept_terms)
    CheckBox check_accept_terms;
    @BindView(R.id.scrollview)
    ScrollView scrollview;
    @BindView(R.id.btnDialogCancel)
    Button btnDialogCancel;

    private int qtdPolicyCurrent, qtdPolicyTotal;

    Context mContext;
    String mCpf;

    private ProgressDialog mProgressDialog;
    Queue<PoliciesAndTerm> panelQueue = new LinkedList<>();
    PoliciesAndTerm policiesAndTerm = new PoliciesAndTerm();
    List<AcceptData> acceptDataList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_pending_policies);
        ButterKnife.bind(this);
        mContext = this;

        mProgressDialog = new ProgressDialog(this);

        buttonEnabled(false);

        check_accept_terms.setText(getResources().getString(R.string.accept_police_lgpd));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            List<PoliciesAndTerm> policiesAndTermList = new ArrayList<>();
            policiesAndTermList.clear();
            policiesAndTermList.addAll((Collection<? extends PoliciesAndTerm>) bundle.getSerializable("policiesAndTermList"));

            if (policiesAndTermList.size() > 0) {

                qtdPolicyCurrent = 1;
                qtdPolicyTotal = policiesAndTermList.size();
                setCounter();
                panelQueue.addAll(policiesAndTermList);

                getPolicy();
            }
        }
    }

    private void setCounter() {
        StringBuilder s = new StringBuilder();
        s.append(getResources().getString(R.string.text_policy_acceptance));
        s.append(" " + qtdPolicyCurrent);
        s.append(" / ");
        s.append(qtdPolicyTotal);
        text_qtd_policies_pending.setText(s.toString());
    }

    private void getPolicy() {
        policiesAndTerm = panelQueue.peek();

        scrollview.scrollTo(0, 0);

        if (policiesAndTerm.title != null && !policiesAndTerm.title.isEmpty())
            txtTitle.setText(Html.fromHtml(policiesAndTerm.title));

        if (policiesAndTerm.text != null && !policiesAndTerm.text.isEmpty())
            txtTermText.setText(Html.fromHtml(policiesAndTerm.text));

        saveReadPolice();
    }

    @OnClick(R.id.btnDialogSave)
    public void submit(View view) {
        saveAcceptPolice();
    }

    @OnClick(R.id.btnDialogCancel)
    public void cancel(View view) {

        int resID = getResources().getIdentifier("MSG506", "string", getPackageName());
        String message = getResources().getString(resID);

        Utils.showQuestionDialog(getString(R.string.dialog_title), message, this, view1 -> saveRejectPolice());
    }

    //PARA BLOQUEAR O BACK E TER OPÇÔES APENAS NO BOTÂO
    @Override
    public void onBackPressed() {

    }

    private void saveReadPolice(){

        setLoading(true);

        AcceptData acceptData = getAccept(policiesAndTerm.versionPanelId, TERM_READ);
        acceptDataList.clear();
        acceptDataList.add(acceptData);

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = apiService.saveAcceptPanel(acceptDataList);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        if (!response.body().getAsJsonObject().has("messageIdentifier")) {
                            CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                            showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        }
                    }
                    setLoading(false);

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), TYPE_FAILURE);
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                setLoading(false);
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

    private void saveAcceptPolice(){

        setLoading(true);

        AcceptData acceptData = getAccept(policiesAndTerm.versionPanelId, TERM_ACCEPT);
        acceptDataList.clear();
        acceptDataList.add(acceptData);

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = apiService.saveAcceptPanel(acceptDataList);
        call.enqueue(new Callback<JsonElement>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        if (!response.body().getAsJsonObject().has("messageIdentifier")) {

                            CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                            showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);

                        } else {

                            if (qtdPolicyCurrent == qtdPolicyTotal) {

                                noPoliciesAndTermsPending();

                            } else {
                                panelQueue.remove();
                                qtdPolicyCurrent++;
                                check_accept_terms.setChecked(false);
                                setCounter();
                                getPolicy();
                            }
                        }
                    }
                    setLoading(false);

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), TYPE_FAILURE);
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                setLoading(false);
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

    private void saveRejectPolice(){

        setLoading(true);

        AcceptData acceptData = getAccept(policiesAndTerm.versionPanelId, TERM_DECLINED);
        acceptDataList.clear();
        acceptDataList.add(acceptData);

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = apiService.saveAcceptPanel(acceptDataList);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    if (!response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                    } else {
                        setLoading(true);
                        SessionManager sessionManager = new SessionManager(mContext);
                        sessionManager.logout();
                    }

                    setLoading(false);

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), TYPE_FAILURE);
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                setLoading(false);
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

    private AcceptData getAccept(String versionPanelId, int type){

        mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        AcceptData acceptData = new AcceptData();
        acceptData.versionPanelId = versionPanelId;
        acceptData.type = type;
        acceptData.cpf = mCpf;
        acceptData.ipAddress = Utils.getIPAddress(true);
        acceptData.operationalSystem = Utils.getAndroidVersion();
        acceptData.appVersion = BuildConfig.VERSION_NAME;
        acceptData.device = "ANDROID";

        return acceptData;
    }

    @OnCheckedChanged(R.id.check_accept_terms)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            buttonEnabled(true);
        else
            buttonEnabled(false);
    }

    private void buttonEnabled(boolean isButtonEnabled) {
        if (isButtonEnabled) {
            btnDialogSave.setEnabled(true);
            btnDialogSave.setTextColor(getResources().getColor(R.color.white_text));
            btnDialogCancel.setEnabled(true);
            btnDialogCancel.setTextColor(getResources().getColor(R.color.white_text));
        } else {
            btnDialogSave.setEnabled(false);
            btnDialogSave.setTextColor(getResources().getColor(R.color.grey_text));
            btnDialogCancel.setEnabled(false);
            btnDialogCancel.setTextColor(getResources().getColor(R.color.grey_text));
        }
        btnDialogSave.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        btnDialogCancel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mTermsContainer, message, Snackbar.LENGTH_LONG);
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

    private void noPoliciesAndTermsPending() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}