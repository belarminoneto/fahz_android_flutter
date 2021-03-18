package br.com.avanade.fahz.fragments.benefits.scholarship;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import br.com.avanade.fahz.Adapter.ListDocumentTypeAdapter;
import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.activities.benefits.schoolsupplies.BaseSchoolSuppliesControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.DocumentByPerson;
import br.com.avanade.fahz.model.DocumentDeleteRequest;
import br.com.avanade.fahz.model.DocumentType;
import br.com.avanade.fahz.model.Documents;
import br.com.avanade.fahz.model.FollowupScholarship;
import br.com.avanade.fahz.model.FollowupUpdate;
import br.com.avanade.fahz.model.HistoryModel;
import br.com.avanade.fahz.model.StartFollowup;
import br.com.avanade.fahz.model.SystemBehaviorModel;
import br.com.avanade.fahz.model.UploadResponse;
import br.com.avanade.fahz.model.response.StartFollowupResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static br.com.avanade.fahz.util.Constants.MASK_DATE;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ScholarshipFollowupFragment extends Fragment implements ListDocumentTypeAdapter.DeleteOnClickHandler {

    @BindView(R.id.content_receipt)
    LinearLayout mContentReceipt;
    SessionManager sessionManager;
    @BindView(R.id.recyclerViewPeople)
    RecyclerView mPeopleRecyclerView;

    private ProgressDialog mProgressDialog;
    private int idPlan;
    private String scholarshipId;

    private List<FollowupScholarship> requests;
    private List<FollowupScholarship> requestsThatHaveBeenValidated;
    final List<DocumentByPerson> documentByPersonArrayList = new ArrayList<>();

    List<Documents> documentsSaved;

    HistoryModel requestHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_scholarship_followup, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false, "");
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        requestHistory = new HistoryModel();
        requestHistory.setDocumentsId(new ArrayList<String>());

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mPeopleRecyclerView = view.findViewById(R.id.recyclerViewPeople);
        mPeopleRecyclerView.setHasFixedSize(true);
        mPeopleRecyclerView.setLayoutManager(layoutManager);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idPlan = bundle.getInt(Constants.PLAN_ID, 0);
            scholarshipId = bundle.getString(Constants.SCHOLARSHIP_ID);
        }

        String listDocuments  = sessionManager.getPreference(Constants.DOCUMENT_REFUND_SAVED_LOCAL);
        if(!listDocuments.equals("")) {
            Type typeClass = new TypeToken<List<Documents>>() {
            }.getType();
            documentsSaved = new Gson().fromJson(listDocuments, typeClass);
        }

        requests = new ArrayList<>();

        setupUi();
        getPeople();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setLoading(Boolean loading, String text) {
        try {
            if (loading) {
                mProgressDialog.setMessage(text);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            } else {
                mProgressDialog.dismiss();
            }
        } catch (Exception ex) {
            LogUtils.error("FollowupFragment", ex);
        }
    }

    private void setupUi() {
        if (getActivity() instanceof BaseSchoolSuppliesControlActivity)
            ((BaseSchoolSuppliesControlActivity) getActivity()).toolbarTitle.setText(getActivity().getString(R.string.send_receipt));
    }

    //BUSCA DEPENDENTES QUE ESTAO NO BENEFICIO SELECIONADO
    private void getPeople() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.startFollowup(scholarshipId);
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
                            StartFollowupResponse dependents = new Gson().fromJson((response.body().getAsJsonObject()), StartFollowupResponse.class);
                            if (dependents.getRegisters().size() > 0) {
                                ScholarshipFollowupPeopleAdapter adapter = new ScholarshipFollowupPeopleAdapter(dependents.getRegisters());
                                mPeopleRecyclerView.setAdapter(adapter);
                            }
                        }

                    } else {
                        try {
                            ScholarshipFollowupPeopleAdapter adapter = new ScholarshipFollowupPeopleAdapter(new ArrayList<StartFollowup>());
                            mPeopleRecyclerView.setAdapter(adapter);

                            String data = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(data);
                            String message = jObjError.getString("Message");
                            showSnackBar(message, Constants.TYPE_FAILURE);
                        } catch (Exception ex) {
                            showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                        }
                    }
                    setLoading(false, "");
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    setLoading(false, "");
                    if(getActivity() != null) {
                        if (t instanceof SocketTimeoutException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else
                            showSnackBar(t.getMessage(), TYPE_FAILURE);
                    }
                }

            });
        }
    }

    private boolean isDateValid(String date) {
        String regex = "^(?:(?:31(\\/)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/)" +
                "(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?" +
                "\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?" +
                "(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|" +
                "[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:" +
                "(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
        return date != null && date.matches(regex);
    }

    //BOTÃO PARA SALVAR AS INFORMACOES
    @OnClick(R.id.btnSave)
    public void submit(View view) {
        if (requests != null && requests.size() > 0) {
            boolean canContinue = true;

            for (FollowupScholarship request : requests) {
                View v = mPeopleRecyclerView.getChildAt(request.indexSelected);
                String date = ((EditText) v.findViewById(R.id.date_edit_text)).getText().toString();
                if ((date.equals("") || date == null) && request.receiptDateNeeded) {
                    canContinue = false;
                    int resID = getResources().getIdentifier("MSG349", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), message + " " + request.getScholarshipLife().getName(), null, getActivity(), null);
                }
                else if(!date.equals("") && !isDateValid(date))
                {
                    canContinue = false;
                    int resID = getResources().getIdentifier("MSG350", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), message + " " + request.getScholarshipLife().getName(), null, getActivity(), null);
                }
                else if (canContinue && !hasAddedDocument(request.cpf)) {

                    canContinue = false;
                    int resID = getResources().getIdentifier("MSG342", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), message + " " + request.getScholarshipLife().getName(), null, getActivity(), null);
                }

                if (!canContinue)
                    break;
                else
                    request.setReceiptDate(DateEditText.parseToShortDate(date));
            }

            if (canContinue) {

                requestsThatHaveBeenValidated = new ArrayList<>();
                requestsThatHaveBeenValidated.add(requests.get(0));


                int resID = getResources().getIdentifier("MSG341", "string", Objects.requireNonNull(getActivity()).getPackageName());
                String message = getResources().getString(resID);
                Utils.showQuestionDialog(getString(R.string.dialog_title), message, getActivity(), view1 -> sendRequest());

            }
        } else {
            setLoading(false, "");
            int resID = getResources().getIdentifier("MSG223", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
        }
    }

    //ENVIA A SOLICITAÇÃO DE REEMBOLSO PARA MATERIAL ESCOLAR
    public void sendRequest() {

        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            FollowupUpdate planUpdate = new FollowupUpdate();
            planUpdate.setFollowupScholarships(requests);
            planUpdate.setDocuments(requestHistory.getDocumentsId());

            Gson gson = new Gson();
            gson.toJson(planUpdate);

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.GenerateFolowupDocuments(planUpdate);
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

                            int resID = getResources().getIdentifier("MSG366", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), message, null, getActivity(), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                    setLoading(false, "");
                                    getActivity().finish();
                                }
                            });

                            sessionManager.createPreference(Constants.DOCUMENT_REFUND_SAVED_LOCAL,"");
                        }

                    } else {
                        setLoading(false, "");
                        try {
                            String data = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(data);
                            String message = jObjError.getString("Message");
                            showSnackBar(message, Constants.TYPE_FAILURE);
                        } catch (Exception ex) {
                            showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                        }
                }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    setLoading(false, "");
                    if(getActivity()!=null) {
                        if (t instanceof SocketTimeoutException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else
                            showSnackBar(t.getMessage(), TYPE_FAILURE);
                    }
                }

            });
        }
    }

//    public void sendRequestHistory() {
//
//        if (sessionManager.isLoggedIn()) {
//            setLoading(true, getString(R.string.loading_searching));
//
//            Gson gson = new Gson();
//            String jsonInString = gson.toJson(requests);
//
//            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
//            Call<CommitResponse> call = mAPIService.generateHistoryDocument(requestHistory);
//            call.enqueue(new Callback<CommitResponse>() {
//                @Override
//                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
//                    FahzApplication.getInstance().setToken(((Headers) response.raw().headers()).get("token"));
//                    if (response.isSuccessful()) {
//                        CommitResponse commitResponse = (CommitResponse) response.body();
//                        if (commitResponse.commited) {
//                            int resID = getResources().getIdentifier("MSG366", "string", getActivity().getPackageName());
//                            String message = getResources().getString(resID);
//                            Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), message, null, getActivity(), new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    startActivity(new Intent(getContext(), MainActivity.class));
//                                    setLoading(false, "");
//                                    getActivity().finish();
//                                }
//                            });
//
//                            sessionManager.createPreference(Constants.DOCUMENT_REFUND_SAVED_LOCAL,"");
//                        }else
//                            showSnackBar(commitResponse.messageIdentifier, TYPE_FAILURE);
//
//
//                    } else {
//                        setLoading(false, "");
//                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
//                    }
//
//                    setLoading(false, "");
//                }
//
//                @Override
//                public void onFailure(Call<CommitResponse> call, Throwable t) {
//                    setLoading(false, "");
//                    if(getActivity()!= null) {
//                        if (t instanceof SocketTimeoutException)
//                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
//                        else if (t instanceof UnknownHostException)
//                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
//                        else
//                            showSnackBar(t.getMessage(), TYPE_FAILURE);
//                    }
//                }
//
//            });
//        }
//    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContentReceipt, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContentReceipt, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    //VALIDA SE O CPF SELECIONADO JÁ EXISTE NA LISTA QUE SERÁ ENVIAR PARA SOLICITAÇÃO DE REEMBOLSO
    public boolean ListContainCPF(String cpf) {
        for (FollowupScholarship request : requests) {
            if (request.getScholarshipLife().getCPF().equals(cpf))
                return true;
        }
        return false;
    }

    //CASO O CHECKBOX SEJA TIRADA A SELEÇÃO REMOVE O CPF DA LISTA
    public boolean RemoveCPFIfExist(String cpf) {
        for (FollowupScholarship request : requests) {
            if (request.getScholarshipLife().getCPF().equals(cpf)) {
                requests.remove(request);
                return true;
            }
        }
        return false;
    }

    //METODO DE RETORNO DO UPLOADDIALOGFRAGMENT, APÓS CHAMADA ENDPOINT DE UPLOAD
    public void uploadDocumentUpdate(UploadResponse resultUpload, int idType, DocumentType type, final String name, String mCpf, String idDocument) {
        if (resultUpload.commited) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentDateandTime = formatter.format(new Date());
            int indexFound = -1;

            if (documentByPersonArrayList.size() > 0) {
                for (DocumentByPerson array : documentByPersonArrayList) {
                    if (array.getCPF().equals(mCpf)) {
                        for (int i = 0; i < array.getDocumentsType().size(); i++) {
                            if (array.getDocumentsType().get(i).id == idType)
                                indexFound = i;
                        }

                        Documents toSave;

                        if (indexFound != -1) {

                            toSave = new Documents(resultUpload.path, currentDateandTime, name, true);
                            type = array.getDocumentsType().get(indexFound);
                            type.documents.add(toSave);

                            if (!array.getDocumentsType().get(indexFound).userHasIt)
                                array.getDocumentsType().get(indexFound).userHasIt = true;
                            array.getDocumentsType().get(indexFound).documents = type.documents;
                        } else {

                            toSave = new Documents(resultUpload.path, currentDateandTime, name, true);
                            type.documents = new ArrayList<>();
                            type.documents.add(toSave);

                            if (!type.userHasIt)
                                type.userHasIt = true;
                            array.getDocumentsType().add(type);
                        }

                        requestHistory.getDocumentsId().add(idDocument);

                        for (DocumentType docType : array.getDocumentsType()) {
                            docType.userCanUpload = true;
                        }

                        showDocumentTypeData(array.getDocumentsType(), array.getmDocumentTypesRecyclerView(), array.getCPF(), true);

                        if(toSave!= null)
                        {
                            String listDocuments  = sessionManager.getPreference(Constants.DOCUMENT_REFUND_SAVED_LOCAL);
                            if(!listDocuments.equals(""))
                            {
                                Type typeClass = new TypeToken<List<Documents>>() {}.getType();
                                List<Documents>  documentList = new Gson().fromJson(listDocuments, typeClass);
                                toSave.cpf = mCpf;
                                toSave.type = type.id;
                                toSave.id = idDocument;
                                documentList.add(toSave);

                                Gson gson = new Gson();
                                String jsonInString = gson.toJson(documentList);

                                sessionManager.createPreference(Constants.DOCUMENT_REFUND_SAVED_LOCAL,jsonInString);
                            }
                            else
                            {
                                List<Documents> documentList = new ArrayList<>();
                                toSave.cpf = mCpf;
                                toSave.type = type.id;
                                toSave.id = idDocument;
                                documentList.add(toSave);
                                Gson gson = new Gson();
                                String jsonInString = gson.toJson(documentList);

                                sessionManager.createPreference(Constants.DOCUMENT_REFUND_SAVED_LOCAL,jsonInString);
                            }
                        }

                        break;
                    }
                }
            }

        } else

        {
            showSnackBar(resultUpload.messageIdentifier, TYPE_FAILURE);
        }
    }

    public boolean hasAddedDocument(String cpf) {
        for (DocumentByPerson documentByPerson : documentByPersonArrayList) {
            if (documentByPerson.getCPF().equals(cpf)) {
                if (documentByPerson.getDocumentsType().get(0).documents.size() == 0)
                    return false;
                else {
                    boolean hasNew = false;
                    for (DocumentType document : documentByPerson.getDocumentsType()) {
                        if(document.documents.size()==0)
                        {
                            hasNew = false;
                        }
                        else {
                            for (Documents doc : document.documents) {
                                if (doc.newAdition || (documentsSaved!= null && documentsSaved.size() >0))
                                    hasNew = true;
                            }
                        }

                        if (hasNew == false)
                            break;
                    }

                    return hasNew;
                }
            }
        }

        return true;
    }

    public void showDocumentTypeData(ArrayList<DocumentType> documentTypesArrayList, RecyclerView mDocumentTypesRecyclerView, String mCpf, boolean canSendDocument) {
        ListDocumentTypeAdapter documentTypeAdapter = new ListDocumentTypeAdapter(Objects.requireNonNull(getActivity()), documentTypesArrayList, mCpf, getActivity().getSupportFragmentManager(),
                this, canSendDocument);
        mDocumentTypesRecyclerView.setAdapter(documentTypeAdapter);
        mDocumentTypesRecyclerView.setAlpha(1);
    }

    @Override
    public void onDelete(final String mPath, final String mCpf) {

        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));
            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            DocumentDeleteRequest request = new DocumentDeleteRequest(mCpf, mPath);
            Call<CommitResponse> call = mAPIService.deleteDocument(request);
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    setLoading(false, "");
                    if (response.isSuccessful()) {
                        removeDocumentFromView(mCpf, mPath);
                    } else {
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(Call<CommitResponse> call, Throwable t) {
                    setLoading(false, "");
                    if(getActivity()!= null) {
                        if (t instanceof SocketTimeoutException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else
                            showSnackBar(t.getMessage(), TYPE_FAILURE);
                    }
                }

            });
        }
    }

    private void removeDocumentFromView(String mCpf, String mPath) {
        if (documentByPersonArrayList.size() > 0) {
            for (DocumentByPerson array : documentByPersonArrayList) {
                if (array.getCPF().equals(mCpf)) {
                    Documents docToRemove = null;
                    for (DocumentType type : array.getDocumentsType()) {
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

                        type.userCanUpload = true;
                    }
                    showDocumentTypeData(array.getDocumentsType(), array.getmDocumentTypesRecyclerView(), array.getCPF(), true);
                    break;
                }
            }
        }
    }

    private void birthDateField(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String str = unmask(s.toString());
                StringBuilder mask = new StringBuilder();
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (final char m : MASK_DATE.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mask.append(m);
                        continue;
                    }
                    try {
                        mask.append(str.charAt(i));
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mask.toString());
                editText.setSelection(mask.length());

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
    }

    private String unmask(String s) {
        return s.replaceAll("[^0-9]*","");
    }

    private class ScholarshipFollowupPeopleAdapter extends RecyclerView.Adapter<ScholarshipFollowupPeopleAdapter.ScholarshipFollowupPeopleViewHolder> {

        private List<StartFollowup> mList;

        ScholarshipFollowupPeopleAdapter(List<StartFollowup> list) {
            mList = list;
        }

        void updateData(List<StartFollowup> dependentList) {
            mList.clear();
            mList.addAll(dependentList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ScholarshipFollowupPeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_scholarship_followup_item,
                    parent, false);
            return new ScholarshipFollowupPeopleViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ScholarshipFollowupPeopleViewHolder holder, final int position) {
            final StartFollowup person = mList.get(position);
            holder.nameLabel.setText(person.getScholarshipLife().getName());

            if (person.getScholarshipLife().getName().equals(FahzApplication.getInstance().getFahzClaims().getName())) {
                holder.nameLabel.setText(FahzApplication.getInstance().getFahzClaims().getName());
                holder.typeName.setText(getString(R.string.type_holder));
            } else {
                holder.typeName.setText(getString(R.string.type_dependent));
            }

            if(person.isCanSendNewDocument()) {
                holder.dateLabel.setVisibility(VISIBLE);
                holder.dateText.setVisibility(VISIBLE);
                holder.choosePerson.setEnabled(true);
                holder.nameLabel.setTextColor(getResources().getColor(R.color.blueHeader));
            }
            else
            {
                holder.dateLabel.setVisibility(GONE);
                holder.dateText.setVisibility(GONE);
                holder.choosePerson.setEnabled(false);
                holder.nameLabel.setTextColor(getResources().getColor(R.color.grey_light_text));
            }

            if(person.isReceiptDateNeeded())
                holder.dateLabel.setHint(getString(R.string.hint_date)+"*");
            else
                holder.dateLabel.setHint(getString(R.string.hint_date));

            holder.courseDesc.setText(person.getScholarshipLife().getCourse());
            holder.typeScholarshipDesc.setText(person.getScholarshipLife().getTypeMonitoringScholarship().description);
            holder.schoolDesc.setText(person.getScholarshipLife().getInstitution());

            holder.courseDesc.setVisibility(GONE);
            holder.typeScholarshipDesc.setVisibility(GONE);
            holder.schoolDesc.setVisibility(View.GONE);
            holder.lblCourse.setVisibility(GONE);
            holder.lblTypeScholarship.setVisibility(GONE);
            holder.lblSchool.setVisibility(View.GONE);
            holder.documentRecycler.setVisibility(GONE);
            holder.lblInfo.setVisibility(GONE);
            holder.dateLabel.setVisibility(GONE);
            holder.dateText.setVisibility(GONE);

            holder.choosePerson.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked) {
                    if (!ListContainCPF(person.getScholarshipLife().getCPF())) {
                        FollowupScholarship request = new FollowupScholarship();
                        request.setScholarshipLife(person.getScholarshipLife());
                        SystemBehaviorModel model = new SystemBehaviorModel();
                        model.ID = person.getSystemBehavior().ID;

                        request.receiptDateNeeded = person.isReceiptDateNeeded();
                        request.setSystemBehavior(model);
                        request.cpf = mList.get(position).getScholarshipLife().getCPF();
                        request.indexSelected = position;
                        requests.add(request);

                    }

                    holder.read.setText(getString(R.string.read_less));
                    holder.courseDesc.setVisibility(VISIBLE);
                    holder.typeScholarshipDesc.setVisibility(VISIBLE);
                    holder.schoolDesc.setVisibility(VISIBLE);
                    holder.documentRecycler.setVisibility(VISIBLE);

                    holder.lblCourse.setVisibility(VISIBLE);
                    holder.lblTypeScholarship.setVisibility(VISIBLE);
                    holder.lblSchool.setVisibility(VISIBLE);
                    holder.lblInfo.setVisibility(VISIBLE);
                    if (!person.isCanSendNewDocument()) {
                        holder.dateLabel.setVisibility(GONE);
                        holder.dateText.setVisibility(GONE);
                    } else {
                        holder.dateLabel.setVisibility(VISIBLE);
                        holder.dateText.setVisibility(VISIBLE);
                    }
                } else {
                    RemoveCPFIfExist(person.getScholarshipLife().getCPF());
                    holder.read.setText(getString(R.string.read_more));
                    holder.courseDesc.setVisibility(View.GONE);
                    holder.typeScholarshipDesc.setVisibility(View.GONE);
                    holder.schoolDesc.setVisibility(View.GONE);
                    holder.documentRecycler.setVisibility(GONE);

                    holder.lblCourse.setVisibility(GONE);
                    holder.lblTypeScholarship.setVisibility(GONE);
                    holder.lblSchool.setVisibility(View.GONE);
                    holder.lblInfo.setVisibility(GONE);
                    holder.dateLabel.setVisibility(GONE);
                    holder.dateText.setVisibility(GONE);
                }
            });

            holder.read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controlVisibilityInformation(holder,person);
                }
            });

            holder.nameLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controlVisibilityInformation(holder,person);
                }
            });

            birthDateField(holder.dateText);

            //CHAMADA DE ENDPOINT QUE DEFINE OS DOCUMENTOS A SEREM MOSTRADOS
            getDocuments(person.getScholarshipLife().getCPF(),holder, person.getDocumentsType(), person.isCanSendNewDocument());
        }

        public void controlVisibilityInformation(ScholarshipFollowupPeopleViewHolder holder, StartFollowup value)
        {
            if(holder.read.getText().equals(getString(R.string.read_more)))
            {

                holder.read.setText(getString(R.string.read_less));
                holder.courseDesc.setVisibility(VISIBLE);
                holder.typeScholarshipDesc.setVisibility(VISIBLE);
                holder.schoolDesc.setVisibility(VISIBLE);
                holder.documentRecycler.setVisibility(VISIBLE);

                holder.lblCourse.setVisibility(VISIBLE);
                holder.lblTypeScholarship.setVisibility(VISIBLE);
                holder.lblSchool.setVisibility(VISIBLE);
                holder.lblInfo.setVisibility(VISIBLE);
                if(!value.isCanSendNewDocument()) {
                    holder.dateLabel.setVisibility(GONE);
                    holder.dateText.setVisibility(GONE);
                }
                else {
                    holder.dateLabel.setVisibility(VISIBLE);
                    holder.dateText.setVisibility(VISIBLE);
                }
            }
            else
            {
                holder.choosePerson.setChecked(false);
                RemoveCPFIfExist(value.getScholarshipLife().getCPF());
                holder.read.setText(getString(R.string.read_more));
                holder.courseDesc.setVisibility(View.GONE);
                holder.typeScholarshipDesc.setVisibility(View.GONE);
                holder.schoolDesc.setVisibility(View.GONE);
                holder.documentRecycler.setVisibility(GONE);

                holder.lblCourse.setVisibility(GONE);
                holder.lblTypeScholarship.setVisibility(GONE);
                holder.lblSchool.setVisibility(View.GONE);
                holder.lblInfo.setVisibility(GONE);
                holder.dateLabel.setVisibility(GONE);
                holder.dateText.setVisibility(GONE);
            }
        }

        private void getDocuments(final String cpf, final ScholarshipFollowupPeopleViewHolder holder, List<DocumentType> documents, boolean canSendDocument) {
            if (sessionManager.isLoggedIn()) {
                setLoading(true, getString(R.string.loading_searching));


                if (documents.size() > 0) {

                    DocumentByPerson documentByPerson = new DocumentByPerson((ArrayList) documents, cpf, holder.documentRecycler);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    holder.documentRecycler.setHasFixedSize(true);
                    holder.documentRecycler.setLayoutManager(layoutManager);
                    holder.documentRecycler.setNestedScrollingEnabled(false);
                    for (DocumentType type : documentByPerson.getDocumentsType()) {

                        //Valida se existem documentos salvos no preference
                        if (documentsSaved != null && documentsSaved.size() > 0) {
                            for (Documents doc : documentsSaved) {
                                if (doc.type == type.id && doc.cpf.equals(cpf)) {
                                    doc.newAdition = false;
                                    type.documents.add(doc);
                                    requestHistory.getDocumentsId().add(doc.id);
                                }
                            }
                        }

                        if (type.documents.size() > 0) {
                            type.userHasIt = true;
                        }
                    }

                    for (DocumentType docType : documentByPerson.getDocumentsType()) {
                        for (Documents doc : docType.documents) {
                            requestHistory.getDocumentsId().add(doc.Id);
                        }
                    }

                    for (DocumentType docType : documents) {
                        docType.userCanUpload = true;
                    }

                    documentByPersonArrayList.add(documentByPerson);
                    showDocumentTypeData((ArrayList) documents, holder.documentRecycler, cpf, canSendDocument);
                }

                setLoading(false, "");
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ScholarshipFollowupPeopleViewHolder extends RecyclerView.ViewHolder {
            private CheckBox choosePerson;
            private TextView typeName;
            private TextView nameLabel;
            private TextView read;

            private TextView lblCourse;
            private TextView lblTypeScholarship;
            private TextView lblSchool;

            private TextView courseDesc;
            private TextView typeScholarshipDesc;
            private TextView schoolDesc;
            private RecyclerView documentRecycler;
            private TextView lblInfo;
            private TextInputLayout dateLabel;
                    private TextInputEditText dateText;

            ScholarshipFollowupPeopleViewHolder(View itemView) {
                super(itemView);
                read = itemView.findViewById(R.id.read);
                choosePerson = itemView.findViewById(R.id.checkBox_choose);
                typeName = itemView.findViewById(R.id.type_name);
                nameLabel = itemView.findViewById(R.id.name_label);
                lblCourse = itemView.findViewById(R.id.lblCourse);
                lblTypeScholarship = itemView.findViewById(R.id.lblTypeScholarship);
                lblSchool = itemView.findViewById(R.id.lblSchool);
                courseDesc = itemView.findViewById(R.id.course_desc);
                typeScholarshipDesc = itemView.findViewById(R.id.type_scholarship_desc);
                schoolDesc = itemView.findViewById(R.id.school_desc);
                documentRecycler = itemView.findViewById(R.id.recyclerViewDocument);
                lblInfo = itemView.findViewById(R.id.lblInfo);
                dateLabel = itemView.findViewById(R.id.date_text_input);
                dateText = itemView.findViewById(R.id.date_edit_text);
            }
        }
    }

}