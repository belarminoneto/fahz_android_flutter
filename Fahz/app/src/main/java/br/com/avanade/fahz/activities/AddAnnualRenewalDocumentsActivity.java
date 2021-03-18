package br.com.avanade.fahz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

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
import br.com.avanade.fahz.dialogs.TermsOfUseActivity;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.DocumentDeleteRequest;
import br.com.avanade.fahz.model.DocumentType;
import br.com.avanade.fahz.model.Documents;
import br.com.avanade.fahz.model.SendAnnualRenewalDocumentsForApprovalRequest;
import br.com.avanade.fahz.model.UploadResponse;
import br.com.avanade.fahz.model.document.TypesWithoutDocumentsBody;
import br.com.avanade.fahz.model.response.SendAnnualRenewalDocumentsForApprovalResponse;
import br.com.avanade.fahz.model.response.TypesWithoutDocumentsResponse;
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

import static br.com.avanade.fahz.util.Constants.INCLUSAO_FILHO_MAIOR_SEM_CART_TRAB;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class AddAnnualRenewalDocumentsActivity extends NavDrawerBaseActivity implements ListDocumentTypeAdapter.DeleteOnClickHandler {

    public static final String TAG = "AddARDActivity";

    @BindView(R.id.recyclerViewDocumentTypes)
    RecyclerView mDocumentTypesRecyclerView;
    @BindView(R.id.document_container)
    ConstraintLayout documentContainer;
    @BindView(R.id.save_button)
    Button mSaveButton;
    @BindView(R.id.cancel_button)
    Button mCancelButton;
    @BindView(R.id.tv_life_name)
    TextView tvLifeName;

    SessionManager sessionManager;

    private String mCpf;
    private String mName;
    private String mYear;
    private String mIdHistory;

    private String validationMessage;

    private ProgressDialog mProgressDialog;
    private ArrayList<DocumentType> documentTypeArrayList;
    private List<String> idDocuments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_add_annual_renewal_documents);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        setLoading(false);
        sessionManager = new SessionManager(getApplicationContext());

        mCpf = getIntent().getStringExtra(Constants.LIFE_CPF);
        mName = getIntent().getStringExtra(Constants.LIFE_NAME);
        mYear = getIntent().getStringExtra(Constants.LIFE_REFERENCE_YEAR);
        mIdHistory = getIntent().getStringExtra(Constants.HISTORY_ID);

        idDocuments = new ArrayList<>();

        setupUi();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDocumentTypesRecyclerView.setHasFixedSize(true);
        mDocumentTypesRecyclerView.setLayoutManager(layoutManager);
        mDocumentTypesRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(documentContainer, message, Snackbar.LENGTH_LONG).setDuration(5000);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    private void setupUi() {
        toolbarTitle.setText(getText(R.string.annual_documents_title));
        if (mCpf != null) {
            tvLifeName.setText(mName);
            loadTypeDocuments(mCpf);
            if (mIdHistory != null) {
                loadDocuments(mIdHistory);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void setLoading(Boolean loading) {
        setLoading(loading, getText(R.string.loading).toString());
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

    private void loadDocuments(String HistoryId) {
        setLoading(true);
        mDocumentTypesRecyclerView.setAlpha(0);

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<TypesWithoutDocumentsResponse> call = apiService.getDocumentsByHistoryId(HistoryId);
        call.enqueue(new Callback<TypesWithoutDocumentsResponse>() {
            @Override
            public void onResponse(@NonNull Call<TypesWithoutDocumentsResponse> call, @NonNull Response<TypesWithoutDocumentsResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                setLoading(false, "");
                if (response.isSuccessful()) {
                    TypesWithoutDocumentsResponse data = response.body();
                    assert data != null;
                    if (data.getCount() > 0) {
                        for (final DocumentType type : documentTypeArrayList) {

                            for (final TypesWithoutDocumentsResponse.DocumentType documentType : data.getDocumentTypes()) {
                                if (documentType.getId() == type.id) {
                                    addDocuments(documentType.getDocuments(), type);
                                    break;
                                }
                            }
                        }

                        refreshAdapter(documentTypeArrayList);
                    }
                } else {
                    LogUtils.info(TAG, "Not found");
                    showSnackBar(getResources().getString(R.string.problemServer), Constants.TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TypesWithoutDocumentsResponse> call, @NonNull Throwable t) {
                setLoading(false, "");
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

    private void addDocuments(List<Documents> documents, DocumentType type) {
        for (final Documents document : documents) {
            type.documents.add(document);
            idDocuments.add(document.Id);
        }
    }

    private void loadTypeDocuments(String cpf) {
        if (sessionManager.isLoggedIn()) {
            setLoading(true);
            mDocumentTypesRecyclerView.setAlpha(0);

            cpf = cpf.replace(".", "");
            cpf = cpf.replace("-", "");

            APIService apiService = ServiceGenerator.createService(APIService.class);

            Integer behaviorId = 362;
            Call<TypesWithoutDocumentsResponse> call = apiService.typesWithoutDocuments(new TypesWithoutDocumentsBody(cpf, behaviorId, false));
            call.enqueue(new Callback<TypesWithoutDocumentsResponse>() {
                @Override
                public void onResponse(@NonNull Call<TypesWithoutDocumentsResponse> call, @NonNull Response<TypesWithoutDocumentsResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    setLoading(false, "");
                    if (response.isSuccessful()) {
                        TypesWithoutDocumentsResponse data = response.body();
                        assert data != null;
                        if (data.getCount() > 0) {
                            documentTypeArrayList = new ArrayList<>();
                            for (TypesWithoutDocumentsResponse.DocumentType type : data.getDocumentTypes()) {
                                documentTypeArrayList.add(new DocumentType(type.getId(), type.getDescription(), type.getDocuments(), type.getUserHasIt()));
                            }

                            refreshAdapter(documentTypeArrayList);
                        }
                    } else {
                        LogUtils.info(TAG, "Not found");
                        showSnackBar(getResources().getString(R.string.problemServer), Constants.TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TypesWithoutDocumentsResponse> call, @NonNull Throwable t) {
                    setLoading(false, "");
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

    private void refreshAdapter(ArrayList<DocumentType> types) {
        ListDocumentTypeAdapter adapter = new ListDocumentTypeAdapter(getBaseContext(), types, mCpf, getSupportFragmentManager(), this,null);
        mDocumentTypesRecyclerView.setAdapter(adapter);
        mDocumentTypesRecyclerView.setAlpha(1);
        mDocumentTypesRecyclerView.setVisibility(View.VISIBLE);
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
                type.documents.add(new Documents(resultUpload.path, currentDateandTime, name, true));

                if (!documentTypeArrayList.get(indexFound).userHasIt)
                    documentTypeArrayList.get(indexFound).userHasIt = true;
                documentTypeArrayList.get(indexFound).documents = type.documents;
            } else {

                type.documents = new ArrayList<>();
                type.documents.add(new Documents(resultUpload.path, currentDateandTime, name, true));

                if (!type.userHasIt)
                    type.userHasIt = true;
                documentTypeArrayList.add(type);
            }

            idDocuments.add(idDocument);

            refreshAdapter(documentTypeArrayList);
        } else {
            showSnackBar(resultUpload.messageIdentifier, TYPE_FAILURE);
        }
    }

    @Override
    public void onDelete(final String mPath, String mCpf) {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));
            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            DocumentDeleteRequest request = new DocumentDeleteRequest(mCpf, mPath);
            Call<CommitResponse> call = mAPIService.deleteDocument(request);
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    setLoading(false);
                    if (response.isSuccessful()) {
                        removeDocumentFromView(mPath);
                    } else {
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CommitResponse> call, @NonNull Throwable t) {
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
        refreshAdapter(documentTypeArrayList);
    }

    @OnClick(R.id.save_button)
    public void submit(View v) {
        if (!isValid()) {
            showSnackBar(validationMessage, TYPE_FAILURE);
        } else if (!hasNotSendWorkCard()) {
            openTermsOfUseActivity();
            //checkTermsOfUse();
        } else {
            sendDocumentsForApi();
        }
    }

/*    private void checkTermsOfUse() {
        setLoading(true, "Checando termos de uso...");
        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<Object> call = mAPIService.checkUserAcceppetedTerm(new TermCheckBody(INCLUSAO_FILHO_MAIOR_SEM_CART_TRAB, mCpf));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    setLoading(false, "");

                    if (response.body() instanceof LinkedTreeMap) {

                        if (((LinkedTreeMap) response.body()).containsKey("accepted")) {
                            boolean accepted = (boolean) ((LinkedTreeMap) response.body()).get("accepted");
                            if (!accepted) {
                                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(AddAnnualRenewalDocumentsActivity.this);
                                builder.setTitle("Sem carteira de trabalho");
                                builder.setMessage("Se não possuir carteira de trabalho deve aceitar o termo de uso. Deseja prosseguir?");
                                builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        openTermsOfUseActivity();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {
                                sendDocumentsForApi();
                            }
                        } else if (((LinkedTreeMap) response.body()).containsKey("messageIdentifier")) {
                            String value = (String) ((LinkedTreeMap) response.body()).get("messageIdentifier");
                            showSnackBar(value, TYPE_FAILURE);
                        }
                    }
                } else {
                    setLoading(false, "");
                    showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                setLoading(false, "");
                showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
            }
        });
    }*/

    private void openTermsOfUseActivity() {
        Intent intent = new Intent(this, TermsOfUseActivity.class);
        intent.putExtra(Constants.TERMS_OF_USE_SELECTED, INCLUSAO_FILHO_MAIOR_SEM_CART_TRAB);
        intent.putExtra(Constants.TERMS_OF_USE_CPF, mCpf);
        intent.putExtra(Constants.TERMS_OF_USE_FROM_REGISTER, true);
        startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT_SHARED);
    }

    private void sendDocumentsForApi() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));
            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            SendAnnualRenewalDocumentsForApprovalRequest body = new SendAnnualRenewalDocumentsForApprovalRequest(mCpf, mYear, idDocuments);
            Call<SendAnnualRenewalDocumentsForApprovalResponse> call = mAPIService.sendAnnualRenewalDocumentsForApproval(body);
            call.enqueue(new Callback<SendAnnualRenewalDocumentsForApprovalResponse>() {
                @Override
                public void onResponse(@NonNull Call<SendAnnualRenewalDocumentsForApprovalResponse> call, @NonNull Response<SendAnnualRenewalDocumentsForApprovalResponse> response) {
                    setLoading(false);
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {
                        SendAnnualRenewalDocumentsForApprovalResponse response2 = response.body();
                        if (response2 != null) {
                            if (response2.getCommited()) {
                                setResult(RESULT_OK);
                                showSnackBar(getResources().getString(getResources().getIdentifier("MSG016", "string", getPackageName())), TYPE_FAILURE);
                                try {
                                    wait(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                returnBackToMain();
                            } else {
                                showSnackBar(response2.getMessage(), TYPE_FAILURE);
                            }
                        }
                    } else {
                        setLoading(false);
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SendAnnualRenewalDocumentsForApprovalResponse> call, @NonNull Throwable t) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.TERMS_OF_USE_RESULT_SHARED && resultCode == RESULT_OK) {
            sendDocumentsForApi();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean isValid() {
        if (idDocuments == null || idDocuments.isEmpty()) {
            setValidationMessage("Nenhum documento foi enviado", null);
            return false;
        }
        for (DocumentType type : documentTypeArrayList) {
            if (type.id == Constants.Comprovante_Escolar && type.documents != null && type.documents.size() <= 0) {
                setValidationMessage(null, type);
                return false;
            }
        }
        return !hasNotSendWorkCard() || hasSendAllWorkCardDocuments();
    }

    private boolean hasNotSendWorkCard() {
        for (DocumentType type : documentTypeArrayList) {
            if (type.id != Constants.Comprovante_Escolar && type.documents.size() > 0)
                return true;
        }
        setValidationMessage("Não foi enviado nenhum documento de carteira de trabalho.", null);
        return false;
    }

    private void returnBackToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private boolean hasSendAllWorkCardDocuments() {
        for (DocumentType type : documentTypeArrayList) {
            if (type.id != Constants.Comprovante_Escolar && type.documents.size() <= 0) {
                setValidationMessage(null, type);
                return false;
            }
        }

        return true;
    }

    private void setValidationMessage(String message, DocumentType type) {
        if (type == null) {
            validationMessage = message;
        } else {
            if (type.description != null && !type.description.isEmpty())
                validationMessage = "Documento " + type.description + " é obrigatório.";
            else
                validationMessage = "Faltam documentos necessários.";
        }
    }

    @OnClick(R.id.cancel_button)
    public void cancel(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
