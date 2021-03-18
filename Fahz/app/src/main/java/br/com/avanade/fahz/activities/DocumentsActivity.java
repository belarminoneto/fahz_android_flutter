package br.com.avanade.fahz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import br.com.avanade.fahz.Adapter.ListDocumentTypeAdapter;
import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.fragments.UploadDialogFragment;
import br.com.avanade.fahz.model.Benefit;
import br.com.avanade.fahz.model.BenefitList;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.Dependent;
import br.com.avanade.fahz.model.DependentHolder;
import br.com.avanade.fahz.model.Document;
import br.com.avanade.fahz.model.DocumentDeleteRequest;
import br.com.avanade.fahz.model.DocumentType;
import br.com.avanade.fahz.model.Documents;
import br.com.avanade.fahz.model.UploadResponse;
import br.com.avanade.fahz.model.document.QueryDocumentTypeGenericBody;
import br.com.avanade.fahz.model.life.DependentHolderBody;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static br.com.avanade.fahz.util.Constants.DOCUMENTS_SAVED;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class DocumentsActivity extends NavDrawerBaseActivity implements ListDocumentTypeAdapter.DeleteOnClickHandler {

    public static final String TAG = "DocumentsActivity";
    ArrayList<DocumentType> documentTypeArrayList = new ArrayList<>();
    SessionManager sessionManager;
    @BindView(R.id.recyclerViewDocumentTypes)
    RecyclerView mDocumentTypesRecyclerView;
    @BindView(R.id.lbName)
    TextView mlbName;
    @BindView(R.id.lbBenefit)
    TextView mlbBenefit;
    @BindView(R.id.benefit_spinner)
    Spinner mBenefitSpinner;
    @BindView(R.id.user_spinner)
    Spinner mUserSpinner;
    @BindView(R.id.progressBarDocuments)
    ProgressBar mProgressBarDocuments;
    @BindView(R.id.document_container)
    RelativeLayout mDocumentContainer;
    @BindView(R.id.save_button)
    Button mSaveButton;
    @BindView(R.id.addDocument)
    ImageButton addDocument;
    @BindView(R.id.text_title)
    TextView txtTitle;
    List<String> idDocuments = new ArrayList<>();
    private String mCpf;
    private OriginCall originCall;
    private int idContext;
    private int idDocumentReceipt;
    private int idBenefitSearch;
    private Boolean isOnlyView;
    private String cpfToWork;
    private int planId;
    private List<Dependent> dependents = new ArrayList<>();
    private List<String> dependentsStr = new ArrayList<>();
    private List<Benefit> benefits = new ArrayList<>();
    private List<String> benefitsStr = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_documents);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        setLoading(false, "");
        sessionManager = new SessionManager(getApplicationContext());

        originCall = (OriginCall) getIntent().getSerializableExtra(Constants.ORIGIN_CALL_DOCUMENT);
        idContext = getIntent().getIntExtra(Constants.CONTEXT_DOCUMENT, 0);
        cpfToWork = getIntent().getStringExtra(Constants.CONTEXT_DOCUMENT_CPF);

        planId = getIntent().getIntExtra(Constants.CONTEXT_DOCUMENT_PLAN, 0);
        idDocumentReceipt = getIntent().getIntExtra(Constants.REQUEST_ACCOUNT_DOCUMENT, 0);
        idBenefitSearch = getIntent().getIntExtra(Constants.CONTEXT_REASON_CANCEL_BENEFIT, 0);
        isOnlyView = getIntent().getBooleanExtra(Constants.VIEW_EDIT_DEPENDENT, false);

        setupUi();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDocumentTypesRecyclerView.setHasFixedSize(true);
        mDocumentTypesRecyclerView.setLayoutManager(layoutManager);
        mDocumentTypesRecyclerView.setNestedScrollingEnabled(false);

        populateListBenefit();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void setupUi() {
        toolbarTitle.setText(getText(R.string.label_documents));
        switch (originCall) {
            case FROMMENU:
                mlbBenefit.setVisibility(GONE);
                mBenefitSpinner.setVisibility(GONE);
                mSaveButton.setVisibility(GONE);
                txtTitle.setText(getString(R.string.text_filter_document));
                mCpf = cpfToWork;
                if (idContext != -1)
                    populateListUser();
                else {
                    mlbName.setVisibility(GONE);
                    mUserSpinner.setVisibility(GONE);
                    loadDocumentType(mCpf, idContext);
                }

                break;
            case FROMPROFILE:
                addDocument.setVisibility(GONE);
                mlbName.setVisibility(GONE);
                mUserSpinner.setVisibility(GONE);
                mlbBenefit.setVisibility(GONE);
                mBenefitSpinner.setVisibility(GONE);
                mSaveButton.setVisibility(View.VISIBLE);
                txtTitle.setText(getString(R.string.text_data_document));
                mCpf = cpfToWork;

                //CASO VENHA DE UM CONTEXTO O CPF È O DO USUÁRIO e O CONTEXTO VEIO DA CLASSE QUE CHAMOU
                loadDocumentType(mCpf, idContext);
                break;
            case FROMDEPENDENT:
            case FROMADHESION:
                addDocument.setVisibility(GONE);
                mlbName.setVisibility(GONE);
                mUserSpinner.setVisibility(GONE);
                mlbBenefit.setVisibility(GONE);
                mBenefitSpinner.setVisibility(GONE);
                mSaveButton.setVisibility(View.VISIBLE);
                txtTitle.setText(getString(R.string.text_data_document));
                mCpf = cpfToWork;

                //CASO VENHA DE UM CONTEXTO O CPF È O DO USUÁRIO e O CONTEXTO VEIO DA CLASSE QUE CHAMOU
                loadDocumentType(mCpf, idContext);
                break;
            case FROMSCHOOLSUPPLIES:
                addDocument.setVisibility(GONE);
                mlbName.setVisibility(GONE);
                mUserSpinner.setVisibility(GONE);
                mlbBenefit.setVisibility(GONE);
                mBenefitSpinner.setVisibility(GONE);
                mSaveButton.setVisibility(View.VISIBLE);
                txtTitle.setText(getString(R.string.text_data_document_person) + " " + getIntent().getStringExtra(Constants.NAME_TO_DOCUMENTO_SCHOOL_SUPPLIES));
                mCpf = cpfToWork;

                //CASO VENHA DE UM CONTEXTO O CPF È O DO USUÁRIO e O CONTEXTO VEIO DA CLASSE QUE CHAMOU
                loadDocumentType(mCpf, idContext);
                break;

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
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

    //CLIQUE DO BOTAO SALVAR VINDO DO CONTEXTO DE DADOS PESSOAIS E DEPENDENTES
    @OnClick(R.id.save_button)
    public void finishActivityResult() {
        String nameDocument = "";
        for (DocumentType type : documentTypeArrayList) {
            if (!type.userHasIt) {
                nameDocument = type.description;
            }
        }

        if (nameDocument.equals("")) {
            Intent returnA = new Intent();

            String[] stringArray = idDocuments.toArray(new String[0]);
            returnA.putExtra(DOCUMENTS_SAVED, stringArray);
            setResult(RESULT_OK, returnA);
            finish();
        } else {
            int resID = getResources().getIdentifier("MSG061", "string", getPackageName());
            String message = getResources().getString(resID);
            showSnackBar(message + nameDocument + ".", TYPE_FAILURE);
        }
    }

    @OnClick(R.id.addDocument)
    public void submit(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("cpf", mCpf);
        bundle.putString("title", "Upload de documento");
        bundle.putInt("id", 0);
        bundle.putBoolean("isNewType", true);

        UploadDialogFragment uploadDialogFragment = new UploadDialogFragment();
        uploadDialogFragment.setArguments(bundle);
        uploadDialogFragment.show(getSupportFragmentManager(), "uploadFile");
    }

    //POPULA O COMBO COM A INFORMACAO DO TITULAR LOGADO E DE SEUS DEPENDENTES
    private void populateListUser() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true,getString(R.string.loading_searching));

            mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<DependentHolder> call = mAPIService.queryDependentHolder(new DependentHolderBody(mCpf, true));
            call.enqueue(new Callback<DependentHolder>() {
                @Override
                public void onResponse(@NonNull Call<DependentHolder> call, @NonNull Response<DependentHolder> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        DependentHolder dependentsHolder = response.body();

                        Dependent holderDependent = new Dependent();
                        holderDependent.cpf = mCpf;
                        holderDependent.name = FahzApplication.getInstance().getFahzClaims().getName();
                        dependents.add(holderDependent);

                        if (dependentsHolder!= null && dependentsHolder.count > 0) {
                            dependents.addAll(dependentsHolder.dependents);
                        }

                        for (Dependent dep : dependents) {
                            dependentsStr.add(dep.name);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                                R.layout.spinner_layout, dependentsStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mUserSpinner.setAdapter(adapter);
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
                public void onFailure(@NonNull Call<DependentHolder> call, @NonNull Throwable t) {
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
    }

    //CASO A SELECAO NO COMBO DE USUARIO MUDE
    @OnItemSelected(R.id.user_spinner)
    void onItemSelected(int position) {
        if(originCall == OriginCall.FROMMENU) {
            loadDocumentType(dependents.get(position).cpf,-1);
        }
    }

    private void populateListBenefit() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true,getString(R.string.loading_searching));
            APIService service = ServiceGenerator.createService(APIService.class);
            Call<BenefitList> call = service.getBenefitList();
            call.enqueue(new Callback<BenefitList>() {
                @Override
                public void onResponse(@NonNull Call<BenefitList> call, @NonNull Response<BenefitList> response) {
                    BenefitList responseBenefitList = response.body();
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful() && responseBenefitList != null) {
                        for (Benefit benefit : responseBenefitList.benefits) {
                            benefitsStr.add(benefit.description);
                        }

                        benefits = responseBenefitList.benefits;

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                                R.layout.spinner_layout, benefitsStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mBenefitSpinner.setAdapter(adapter);
                    }

                    setLoading(false,"");
                }

                @Override
                public void onFailure(@NonNull Call<BenefitList> call, @NonNull Throwable t) {
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
    }

    @OnItemSelected(R.id.benefit_spinner)
    void onItemBenefitSelected(int position) {

        //Vindo do Menu não existe consulta com Beneficio
//        if(!fromMenu) {
//             //TODO: Caso Valido para Quando tiver beneficio, de comportamento tem que ser feito
//            loadDocumentType(dependents.get(mUserSpinner.getSelectedItemPosition()).cpf,benefits.get(position).id);
//        }
    }

    private void loadDocumentType(String cpf, int benefit) {
        if (sessionManager.isLoggedIn()) {
            mProgressBarDocuments.setVisibility(View.VISIBLE);
            mDocumentTypesRecyclerView.setAlpha(0);

            cpf = cpf.replace(".", "");
            cpf = cpf.replace("-", "");

            APIService apiService = ServiceGenerator.createService(APIService.class);

            boolean genericSearch = benefit == -1;
            int planIdValue = planId == 0 ? -1 : planId;

            Call<Document> call = apiService.queryDocumentTypeGeneric(new QueryDocumentTypeGenericBody(cpf, benefit, idDocumentReceipt != 0, idBenefitSearch, genericSearch, planIdValue));
            call.enqueue(new Callback<Document>() {
                @Override
                public void onResponse(@NonNull Call<Document> call, @NonNull Response<Document> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    Document documentResponse = response.body();
                    if (response.isSuccessful() && documentResponse != null) {
                        mProgressBarDocuments.setVisibility(View.INVISIBLE);
                        List<DocumentType> listDocumentTypes = documentResponse.documentTypes;

                        documentTypeArrayList = new ArrayList<>();

                        if(listDocumentTypes.size()>0) {
                            for (DocumentType documentType : listDocumentTypes) {

                                for (Documents doc : documentType.documents) {
                                    doc.newAdition = false;
                                    idDocuments.add(doc.Id);
                                }

                                if (originCall.equals(OriginCall.FROMDEPENDENT) && isOnlyView){
                                    documentType.userCanUpload = false;
                                }
                                documentTypeArrayList.add(new DocumentType(documentType.id,
                                        documentType.description,
                                        documentType.documents,
                                        documentType.userHasIt,
                                        documentType.userCanUpload));
                            }
                        }

                        showDocumentTypeData(documentTypeArrayList);
                    } else {
                        mProgressBarDocuments.setVisibility(View.INVISIBLE);
                        LogUtils.info("DocumentsActivity", "Not found");
                        showSnackBar(getResources().getString(R.string.problemServer), Constants.TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Document> call, @NonNull Throwable t) {
                    mProgressBarDocuments.setVisibility(View.INVISIBLE);
                    LogUtils.error("DocumentsActivity", t);
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

    private void showDocumentTypeData(ArrayList<DocumentType> documentTypesArrayList) {
        mCpf = mCpf.replace(".", "");
        mCpf = mCpf.replace("-", "");
        ListDocumentTypeAdapter documentTypeAdapter = new ListDocumentTypeAdapter(this, documentTypesArrayList, mCpf, getSupportFragmentManager(), this,null);
        mDocumentTypesRecyclerView.setAdapter(documentTypeAdapter);
        mDocumentTypesRecyclerView.setAlpha(1);
    }

    //METODO DE RETORNO DO UPLOADDIALOGFRAGMENT, APÓS CHAMADA ENDPOINT DE UPLOAD
    public void uploadDocumentUpdate(UploadResponse resultUpload, int idType, DocumentType type, final String name, String idDocument) {
        if (resultUpload.commited) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentDateandTime = formatter.format(new Date());
            int indexFound = -1;

            for (int i = 0; i < documentTypeArrayList.size(); i++) {
                if (documentTypeArrayList.get(i).id == idType)
                    indexFound = i;
            }

            if (indexFound != -1) {

                type = documentTypeArrayList.get(indexFound);
                type.documents.add(new Documents(resultUpload.path, currentDateandTime, name,true));

                if (!documentTypeArrayList.get(indexFound).userHasIt)
                    documentTypeArrayList.get(indexFound).userHasIt = true;
                documentTypeArrayList.get(indexFound).documents = type.documents;
            } else {

                type.documents = new ArrayList<>();
                type.documents.add(new Documents(resultUpload.path, currentDateandTime, name,true));

                if (!type.userHasIt)
                    type.userHasIt = true;
                documentTypeArrayList.add(type);
            }

            idDocuments.add(idDocument);

            showDocumentTypeData(documentTypeArrayList);
        } else {
            showSnackBar(resultUpload.messageIdentifier, TYPE_FAILURE);
        }
    }

    public void onDelete(final String mPath, String mCpf) {
        if (sessionManager.isLoggedIn()) {
            setLoading(true,getString(R.string.loading_searching));
            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            DocumentDeleteRequest request = new DocumentDeleteRequest(mCpf, mPath);
            Call<CommitResponse> call = mAPIService.deleteDocument(request);
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    setLoading(false, "");
                    if (response.isSuccessful()) {
                        removeDocumentFromView(mPath);
                    } else {
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CommitResponse> call, @NonNull Throwable t) {
                    setLoading(false,"");
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

    private void removeDocumentFromView(String mPath) {
        Documents docToRemove = null;
        for (DocumentType type : documentTypeArrayList) {
            for (Documents doc : type.documents) {
                if (doc.path.equals(mPath))
                    docToRemove = doc;
            }

            if (docToRemove != null) {
                type.documents.remove(docToRemove);
                if (type.documents.size() == 0)
                    type.userHasIt = false;
                break;
            }
        }
        showDocumentTypeData(documentTypeArrayList);
    }

    public enum OriginCall {
        FROMMENU,
        FROMPROFILE,
        FROMDEPENDENT,
        FROMADHESION,
        FROMSCHOOLSUPPLIES
    }
}

