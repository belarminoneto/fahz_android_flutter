package br.com.avanade.fahz.activities.benefits.scholarship;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.DocumentsActivity;
import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.fragments.benefits.scholarship.InitialDataFragment;
import br.com.avanade.fahz.fragments.benefits.scholarship.ScholarshipPersonalDataFragment;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.HistoryModel;
import br.com.avanade.fahz.model.ResponseHistory;
import br.com.avanade.fahz.model.ScholarshipRequest;
import br.com.avanade.fahz.model.TitularResponse;
import br.com.avanade.fahz.model.response.ScholarshipLifeResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.SystemBehavior;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.DOCUMENT_FOR_RESULT;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class RequestScholarshipControlActivity extends NavDrawerBaseActivity {

    public enum ScholarshipFragment {
        SCHOLARSHIPDATA,
        PERSONALDATA
    }

    boolean isFirstFragment;

    @BindView(R.id.scholarship_container)
    RelativeLayout mManagerContainer;
    @BindView(R.id.scholarshipContainer)
    LinearLayout mContainer;
    @BindView(R.id.btnContinue)
    Button mBtnContinue;

    private br.com.avanade.fahz.model.ScholarshipLifeResponse mData;

    private final String LOG_KEY = RequestScholarshipControlActivity.class.getSimpleName();
    private TitularResponse mResponse;
    private APIService mAPIService;
    private ProgressDialog mProgressDialog;
    ScholarshipFragment fragment;
    private SessionManager sessionManager;

    InitialDataFragment initialDataFragment;
    ScholarshipPersonalDataFragment personalDataFragment;

    HistoryModel requestHistory;

    @Override
    protected void onResume() {
        super.onResume();

        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.getToken() == null || sessionManager.getToken().isEmpty()) {
            sessionManager.logout();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholarship);
        ButterKnife.bind(this);

        setImageHeaderVisibility(true);
        setMenuVisible(false);
        mProgressDialog = new ProgressDialog(this);
        sessionManager = new SessionManager(getApplicationContext());

        requestHistory = new HistoryModel();
        requestHistory.setDocumentsId(new ArrayList<String>());

        isFirstFragment = true;
        fragment = (ScholarshipFragment) getIntent().getSerializableExtra(Constants.SCHOLARSHIP_CONTROL);
        loadScholarship();


        setupUi();
    }

    //METODO PARA CONFIRUAÇÂO DE TELA
    private void setupUi() {

        toolbarTitle.setText(getString(R.string.start_scholarship));
        setImageHeaderVisibility(false);
        setCancelOperation(true);
    }

    private void loadScholarship() {
        setLoading(true, getString(R.string.loading_searching));
        mAPIService = ServiceGenerator.createService(APIService.class);
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        Call<JsonElement> call = mAPIService.getScholarshipData(new CPFInBody(cpf));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    ScholarshipLifeResponse responseValue = new Gson().fromJson((response.body().getAsJsonObject()), ScholarshipLifeResponse.class);
                    mData = responseValue.getScholarshipLifeResponse();
                    setFragment(fragment);

                    setLoading(false,"");

                } else if (response.code() == 404) {
                    setLoading(false,"");
                    showSnackBar(getString(R.string.validateSentData), TYPE_FAILURE);
                } else {
                    setLoading(false,"");
                    showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                setLoading(false,"");
                if(t instanceof SocketTimeoutException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if(t instanceof UnknownHostException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

    public br.com.avanade.fahz.model.ScholarshipLifeResponse getDataInformation()
    {
        return mData;
    }


    public void setDataInformation(br.com.avanade.fahz.model.ScholarshipLifeResponse info)
    {
        mData = info;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        switch (fragment) {
            case SCHOLARSHIPDATA:
                break;
            case PERSONALDATA:
                toolbarTitle.setText(getString(R.string.start_scholarship));
                fragment = ScholarshipFragment.SCHOLARSHIPDATA;
                break;
        }
    }

    private void setLoading(boolean loading, String text) {
        if (loading) {
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else
            mProgressDialog.dismiss();
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mManagerContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mManagerContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void setFragment(ScholarshipFragment type) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (type) {
            case SCHOLARSHIPDATA:
                toolbarTitle.setText(getString(R.string.start_scholarship));
                initialDataFragment = new InitialDataFragment();
                ft.replace(R.id.scholarshipContainer, initialDataFragment, ScholarshipFragment.SCHOLARSHIPDATA.toString());
                break;
            case PERSONALDATA:
                mBtnContinue.setText(R.string.button_save);
                toolbarTitle.setText(getString(R.string.data_scholarship));
                personalDataFragment = new ScholarshipPersonalDataFragment();
                ft.replace(R.id.scholarshipContainer, personalDataFragment, ScholarshipFragment.PERSONALDATA.toString());

                break;
            default:
                showSnackBar(getString(R.string.NoFragmentFoud), TYPE_FAILURE);
        }

        if (isFirstFragment) {
            isFirstFragment = false;
        } else {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    //CHAMADA DO BOTÃO DE PROSSEGUIR EXISTENTE NO LAYOUT, ELE QUE DITA O FLUXO DA TELA
    @OnClick(R.id.btnContinue)
    public void continueNavigation(View view) {
        switch (fragment) {
            case SCHOLARSHIPDATA:
                Pair<br.com.avanade.fahz.model.ScholarshipLifeResponse,Boolean> info = initialDataFragment.getData();
                if(info.second) {
                    fragment = ScholarshipFragment.PERSONALDATA;
                    setFragment(ScholarshipFragment.PERSONALDATA);
                }
                break;
            case PERSONALDATA:
                info = personalDataFragment.getData();
                if(info.second) {
                    validateShowDocuments();
                }
                break;
        }
    }

    private void includeBenefit(ScholarshipRequest request) {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.saving_information));

            request.setDocuments(requestHistory.getDocumentsId());

            Gson gson = new Gson();
            String jsonInString = gson.toJson(request);

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.includeScholarshipBenefit(request);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        if (response.body().getAsJsonObject().has("messageIdentifier")) {
                            CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                            showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                            setLoading(false, "");
                        } else {

                            ResponseHistory responseHistory = new Gson().fromJson((response.body().getAsJsonObject()), ResponseHistory.class);
                            if (responseHistory.commited) {
                                int resID = getResources().getIdentifier("MSG366", "string", getPackageName());
                                String message = getResources().getString(resID);
                                Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), message, null, RequestScholarshipControlActivity.this, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(RequestScholarshipControlActivity.this, MainActivity.class));
                                        setLoading(false, "");
                                        finish();
                                    }
                                });
                            }
                        }

                    } else {
                        setLoading(false, "");
                        if( response.errorBody()!=null) {
                            try {
                                String data = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(data);
                                String message = jObjError.getString("Message");
                                showSnackBar(message, Constants.TYPE_FAILURE);
                            } catch (Exception ex) {
                                showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                            }
                        }
                        else {
                            if (!TextUtils.isEmpty(response.raw().message()))
                                showSnackBar(response.raw().message(), TYPE_FAILURE);
                            else
                                showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    setLoading(false, "");
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

    //INCLUSAO DE ADESAO AO PLANO MEDICO
    private void includeHistory() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.saving_information));

            if(requestHistory.getDocumentsId()!= null && requestHistory.getDocumentsId().size()>0) {
                APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
                Call<CommitResponse> call = mAPIService.generateHistoryDocument(requestHistory);
                call.enqueue(new Callback<CommitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                        FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                        if (response.isSuccessful()) {

                            CommitResponse commitResponse = response.body();
                            if (commitResponse.commited) {
                                int resID = getResources().getIdentifier("MSG366", "string", getPackageName());
                                String message = getResources().getString(resID);
                                Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), message, null, RequestScholarshipControlActivity.this, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(RequestScholarshipControlActivity.this, MainActivity.class));
                                        setLoading(false, "");
                                        finish();
                                    }
                                });
                            } else
                                showSnackBar(commitResponse.messageIdentifier, TYPE_FAILURE);

                            setLoading(false, "");

                        } else {
                            if (!TextUtils.isEmpty(response.raw().message()))
                                showSnackBar(response.raw().message(), TYPE_FAILURE);
                            else
                                showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                            setLoading(false, "");
                        }
                    }

                    @Override
                    public void onFailure(Call<CommitResponse> call, Throwable t) {
                        setLoading(false, "");
                        if(t instanceof SocketTimeoutException)
                            showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                        else if(t instanceof UnknownHostException)
                            showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                        else
                            showSnackBar(t.getMessage(), TYPE_FAILURE);
                    }

                });
            }
            else
            {
                int resID = getResources().getIdentifier("MSG013", "string", getPackageName());
                String message = getResources().getString(resID);
                Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), message, null, RequestScholarshipControlActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(RequestScholarshipControlActivity.this, MainActivity.class));
                        setLoading(false, "");
                        finish();
                    }
                });
            }
        }
    }

    //CHAMADA PARA FINALIZAR O FLUXO. MOSTRANDO OS DOCUMENTOS APóS ISSO
    private void validateShowDocuments() {
        SystemBehavior.BehaviorEnum id = SystemBehavior.BehaviorEnum.fromId(mData.getSystemBehavior().ID);
        final int index = id.id();
        final Intent intent = new Intent(RequestScholarshipControlActivity.this, DocumentsActivity.class);


        intent.putExtra(Constants.ORIGIN_CALL_DOCUMENT, DocumentsActivity.OriginCall.FROMADHESION);
        intent.putExtra(Constants.CONTEXT_DOCUMENT, index);
        intent.putExtra(Constants.CONTEXT_DOCUMENT_CPF, FahzApplication.getInstance().getFahzClaims().getCPF());
        intent.putExtra(Constants.CONTEXT_DOCUMENT_PLAN, mData.getPlan().getId());


        startActivityForResult(intent, DOCUMENT_FOR_RESULT);
    }

    //RESULTADO DA VOLTA DA CHAMADA DA TELA DE DOCUMENTO
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.DOCUMENT_FOR_RESULT) {
            if (resultCode == Activity.RESULT_OK) {

                List<String> idDocuments = new ArrayList<String>(Arrays.asList(data.getStringArrayExtra(Constants.DOCUMENTS_SAVED)));
                requestHistory.setDocumentsId( idDocuments);

                ScholarshipRequest request = new ScholarshipRequest();
                request.setScholarshipLife(mData.getScholarshipLife());
                mData.getBankData().cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
                request.setBankData(mData.getBankData());
                request.setEmail(mData.getEmail());
                request.setPhone(mData.getPhone());
                request.setCPF(FahzApplication.getInstance().getFahzClaims().getCPF());

                //CHAMADA AOS ENDPOINTS RELACIONADOS NO FLUXO
                includeBenefit(request);

            }
            else
            {
                showSnackBar("Os Documentos não foram salvos com sucesso.", Constants.TYPE_FAILURE);
            }
        }
    }


}
