package br.com.avanade.fahz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.Adapter.ListAnnualDocumentYearAdapter;
import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.response.AnnualDocumentsByDependentResponse;
import br.com.avanade.fahz.model.response.MajorDependentsForAnnualDocumentRenewalResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static br.com.avanade.fahz.util.Constants.CONTEXT_DOCUMENT_CPF;
import static br.com.avanade.fahz.util.Constants.HISTORY_ID;
import static br.com.avanade.fahz.util.Constants.LIFE_CPF;
import static br.com.avanade.fahz.util.Constants.LIFE_NAME;
import static br.com.avanade.fahz.util.Constants.LIFE_REFERENCE_YEAR;
import static br.com.avanade.fahz.util.Constants.REQUEST_ADD_ANNUAL_RENEWAL_DOCUMENTS_ACTIVITY;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class AnnualRenewalDocumentsActivity extends NavDrawerBaseActivity implements ListAnnualDocumentYearAdapter.ListAnnualDocumentYearAdapterButtonListener {

    public static final String TAG = "ARDocumentsActivity";
    List<AnnualDocumentsByDependentResponse.Result.Year> documentYearList;
    SessionManager sessionManager;
    @BindView(R.id.recyclerViewDocumentTypes)
    RecyclerView mDocumentTypesRecyclerView;
    @BindView(R.id.user_spinner)
    Spinner mUserSpinner;
    @BindView(R.id.progressBarDocuments)
    ProgressBar mProgressBarDocuments;
    @BindView(R.id.document_container)
    ConstraintLayout mDocumentContainer;
    @BindView(R.id.save_button)
    Button mSaveButton;
    @BindView(R.id.text_title)
    TextView txtTitle;
    @BindView(R.id.tv_empty_list)
    TextView txtEmptyList;

    private String mCpf;
    private int idContext;
    private String cpfToWork;
    private List<MajorDependentsForAnnualDocumentRenewalResponse.Dependent> dependents = new ArrayList<>();
    private List<String> dependentsStr = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private MajorDependentsForAnnualDocumentRenewalResponse.Dependent selectedDependent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_annual_renewal_documents);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        setLoading(false, "");
        sessionManager = new SessionManager(getApplicationContext());

        idContext = getIntent().getIntExtra(Constants.CONTEXT_DOCUMENT, 0);
        cpfToWork = getIntent().getStringExtra(CONTEXT_DOCUMENT_CPF);

        setupUi();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDocumentTypesRecyclerView.setHasFixedSize(true);
        mDocumentTypesRecyclerView.setLayoutManager(layoutManager);
        mDocumentTypesRecyclerView.setNestedScrollingEnabled(false);

        showEmptyListWidget();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mDocumentContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
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

    private void setupUi() {
        toolbarTitle.setText(getText(R.string.annual_documents_title));
        mSaveButton.setVisibility(GONE);
        txtTitle.setText(getText(R.string.search_for_dependents));
        mCpf = cpfToWork;
        mCpf = mCpf.replace(".", "");
        mCpf = mCpf.replace("-", "");
        if (idContext != -1)
            populateListUser();
        else {
            mUserSpinner.setVisibility(GONE);
            loadDocuments(mCpf);
        }
    }

    private void setLoading(Boolean loading, String text) {
        if (loading) {
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    //POPULA O COMBO COM A INFORMACAO DO TITULAR LOGADO E DE SEUS DEPENDENTES
    private void populateListUser() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<MajorDependentsForAnnualDocumentRenewalResponse> call = mAPIService.majorDependentsForAnnualDocumentRenewal(new CPFInBody(mCpf));
            call.enqueue(new Callback<MajorDependentsForAnnualDocumentRenewalResponse>() {
                @Override
                public void onResponse(@NonNull Call<MajorDependentsForAnnualDocumentRenewalResponse> call, @NonNull Response<MajorDependentsForAnnualDocumentRenewalResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        MajorDependentsForAnnualDocumentRenewalResponse dependentsHolder = response.body();

                        if (dependentsHolder != null && dependentsHolder.getCount() > 0) {
                            dependents.addAll(dependentsHolder.getDependents());
                        }

                        if (dependents.size() > 1) {
                            dependentsStr.add("Selecione um dependente");
                        }

                        for (MajorDependentsForAnnualDocumentRenewalResponse.Dependent dep : dependents) {
                            dependentsStr.add(dep.getName());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                                R.layout.spinner_layout, dependentsStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mUserSpinner.setAdapter(adapter);
                        setLoading(false, "");
                    } else if (response.code() == 404) {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MajorDependentsForAnnualDocumentRenewalResponse> call, @NonNull Throwable t) {
                    setLoading(false, "");
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }

            });
        }
    }

    //CASO A SELECAO NO COMBO DE USUARIO MUDE
    @OnItemSelected(R.id.user_spinner)
    void onItemSelected(int position) {
        if (dependents.size() > 1 && position == 0) {
            selectedDependent = null;
            showEmptyListWidget();
        } else {
            selectedDependent = dependents.get(position - 1);
            loadDocuments(selectedDependent.getCpf());
        }
    }

    private void showEmptyListWidget() {
        mDocumentTypesRecyclerView.setVisibility(GONE);
        txtEmptyList.setVisibility(View.VISIBLE);
        mProgressBarDocuments.setVisibility(GONE);
    }

    private void showProgessIndicatorWidget() {
        mProgressBarDocuments.setVisibility(View.VISIBLE);
        mDocumentTypesRecyclerView.setAlpha(0);
        txtEmptyList.setVisibility(GONE);
    }

    private void loadDocuments(String cpf) {
        if (sessionManager.isLoggedIn()) {
            showProgessIndicatorWidget();

            cpf = cpf.replace(".", "");
            cpf = cpf.replace("-", "");

            APIService apiService = ServiceGenerator.createService(APIService.class);

            Call<AnnualDocumentsByDependentResponse> call = apiService.annualDocumentsByDependent(new CPFInBody(cpf));
            call.enqueue(new Callback<AnnualDocumentsByDependentResponse>() {
                @Override
                public void onResponse(@NonNull Call<AnnualDocumentsByDependentResponse> call, @NonNull Response<AnnualDocumentsByDependentResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    mProgressBarDocuments.setVisibility(View.INVISIBLE);
                    AnnualDocumentsByDependentResponse documentResponse = response.body();
                    if (response.isSuccessful() && documentResponse != null && documentResponse.getCommited()) {
                        documentYearList = documentResponse.getYears();
                        showDocumentTypeData(documentYearList);
                    } else {
                        LogUtils.info(TAG, "Not found");
                        showSnackBar(getResources().getString(R.string.problemServer), Constants.TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AnnualDocumentsByDependentResponse> call, @NonNull Throwable t) {
                    showEmptyListWidget();
                    LogUtils.error(TAG, t);
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            });
        }
    }

    private void showDocumentTypeData(List<AnnualDocumentsByDependentResponse.Result.Year> documentYearList) {
        ListAnnualDocumentYearAdapter documentTypeAdapter = new ListAnnualDocumentYearAdapter(this, documentYearList, selectedDependent, this);
        mDocumentTypesRecyclerView.setAdapter(documentTypeAdapter);
        mDocumentTypesRecyclerView.setVisibility(View.VISIBLE);
        mDocumentTypesRecyclerView.setAlpha(1);
    }

    @Override
    public void onClick(View view, String selectedYear, String idHistory) {
        Intent intent = new Intent(this, AddAnnualRenewalDocumentsActivity.class);
        intent.putExtra(LIFE_CPF, selectedDependent.getCpf());
        intent.putExtra(LIFE_NAME, selectedDependent.getName());
        intent.putExtra(LIFE_REFERENCE_YEAR, selectedYear);
        intent.putExtra(HISTORY_ID, idHistory);
        startActivityForResult(intent, REQUEST_ADD_ANNUAL_RENEWAL_DOCUMENTS_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_ANNUAL_RENEWAL_DOCUMENTS_ACTIVITY && resultCode == RESULT_OK) {
            loadDocuments(selectedDependent.getCpf());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
