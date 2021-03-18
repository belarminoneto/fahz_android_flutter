package br.com.avanade.fahz.fragments.benefits.schoolsupplies;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import br.com.avanade.fahz.Adapter.ListDocumentTypeAdapter;
import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.activities.benefits.schoolsupplies.BaseSchoolSuppliesControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.dialogs.TermsOfUseActivity;
import br.com.avanade.fahz.model.BenefitSchoolMaterial;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.Document;
import br.com.avanade.fahz.model.DocumentByPerson;
import br.com.avanade.fahz.model.DocumentDeleteRequest;
import br.com.avanade.fahz.model.DocumentType;
import br.com.avanade.fahz.model.Documents;
import br.com.avanade.fahz.model.HistoryModel;
import br.com.avanade.fahz.model.Plan;
import br.com.avanade.fahz.model.PlanMaterialSchoolInformation;
import br.com.avanade.fahz.model.SchoolBenefitPeople;
import br.com.avanade.fahz.model.Schooling;
import br.com.avanade.fahz.model.SendReceiptRequest;
import br.com.avanade.fahz.model.TitularResponse;
import br.com.avanade.fahz.model.UploadResponse;
import br.com.avanade.fahz.model.document.DocumentsMaterialSchoolBody;
import br.com.avanade.fahz.model.response.SchoolBenefitRequest;
import br.com.avanade.fahz.model.response.SchoolingReponse;
import br.com.avanade.fahz.model.schoolsupplies.PlanInformationBody;
import br.com.avanade.fahz.model.schoolsupplies.SchoolBenefitStartBody;
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

import static android.view.View.GONE;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class RequestCardFragment extends Fragment implements ListDocumentTypeAdapter.DeleteOnClickHandler {

    @BindView(R.id.content_request_card)
    LinearLayout mContentRequestCard;
    SessionManager sessionManager;
    @BindView(R.id.recyclerViewPeople)
    RecyclerView mPeopleRecyclerView;

    final List<DocumentByPerson> documentByPersonArrayList = new ArrayList<>();
    private List<SchoolBenefitPeople> mPeopleList;

    private ProgressDialog mProgressDialog;
    private int idPlan;

    private List<Schooling> schooling = new ArrayList<>();
    private List<String> schoolingHolderStr = new ArrayList<>();
    private List<String> schoolingDependentStr = new ArrayList<>();

    private List<BenefitSchoolMaterial> requests;
    private List<BenefitSchoolMaterial> requestsThatHaveBeenValidated;

    private List<BenefitSchoolMaterial> okListed;

    HistoryModel requestHistory;

    //List<String> idDocuments = new ArrayList<>();
    List<SchoolBenefitPeople> people;
    Dialog dialogConfirm1;
    Dialog dialogConfirm2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_school_request_card, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false,"");
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        requestHistory = new HistoryModel();
        requestHistory.setDocumentsId(new ArrayList<String>());

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mPeopleRecyclerView = view.findViewById(R.id.recyclerViewPeople);
        mPeopleRecyclerView.setHasFixedSize(true);
        mPeopleRecyclerView.setLayoutManager(layoutManager);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idPlan = bundle.getInt(Constants.PLAN_ID, 0);
        }

        requests = new ArrayList<>();
        okListed = new ArrayList<>();

        setupUi();
        populateSchoolings();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setLoading(Boolean loading, String text){
        try {
            if (loading) {
                mProgressDialog.setMessage(text);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            } else {
                mProgressDialog.dismiss();
            }
        }
        catch (Exception ex)
        {
            LogUtils.error("RequestCardFragment", ex);
        }
    }

    private void setupUi()
    {
        if(getActivity() instanceof BaseSchoolSuppliesControlActivity)
            ((BaseSchoolSuppliesControlActivity)getActivity()).toolbarTitle.setText(getActivity().getString(R.string.label_request_card));
   }

    //BUSCA DEPENDENTES QUE ESTAO NO BENEFICIO SELECIONADO
    private void getPeople()
    {
        if (sessionManager.isLoggedIn()) {
            setLoading(true,getString(R.string.loading_searching));

            String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.getSchoolBenefitStart(new SchoolBenefitStartBody(mCpf, idPlan));
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        if(response.body().getAsJsonObject().has("messageIdentifier"))
                        {
                            CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                            showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        }
                        else
                        {
                            SchoolBenefitRequest dependents = new Gson().fromJson((response.body().getAsJsonObject()), SchoolBenefitRequest.class);
                            if(dependents.getRegisters().size()>0) {
                                people = dependents.getRegisters();
                                SchoolBenfitPeopleCardAdapter adapter = new SchoolBenfitPeopleCardAdapter(dependents.getRegisters());
                                mPeopleRecyclerView.setAdapter(adapter);
                            }
                        }

                    } else {
                        try {
                            SchoolBenfitPeopleCardAdapter adapter = new SchoolBenfitPeopleCardAdapter(new ArrayList<SchoolBenefitPeople>());
                            mPeopleRecyclerView.setAdapter(adapter);

                            String data = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(data);
                            String message = jObjError.getString("Message");
                            showSnackBar(message, Constants.TYPE_FAILURE);
                        } catch (Exception ex) {
                            showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                        }
                    }
                    setLoading(false,"");                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    setLoading(false,"");
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

    //BOTÃO PARA SALVAR AS INFORMACOES
    @OnClick(R.id.btnSave)
    public void submit(View view) {
        if (requests != null && requests.size() > 0) {

            boolean canContinue = true;
            String names = "";

            for (BenefitSchoolMaterial request : requests) {
                View v = mPeopleRecyclerView.getChildAt(request.indexSelected);
                if (canContinue && !hasAddedDocument(request.getCPF())) {
                    canContinue = false;
                    int resID = getResources().getIdentifier("MSG232", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), message + " " + request.Name, null, getActivity(), null);
                } else if (request.getSchooling() == null) {
                    canContinue = false;
                    int resID = getResources().getIdentifier("MSG222", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), message + " " + request.Name, null, getActivity(), null);
                }

                names += "\n"+ request.Name + ", ";

                if (!canContinue)
                    break;
            }

            for (BenefitSchoolMaterial listed : okListed) {
                View v = mPeopleRecyclerView.getChildAt(listed.indexSelected);

                //Verifica se tem documentos mas nao esta adicionado
                if (hasAddedDocument(listed.getCPF()) &&  !ListContainCPF(listed.getCPF())) {
                    canContinue = false;
                    int resID = getResources().getIdentifier("MSG443", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), message + " " + listed.Name, null, getActivity(), null);
                }
                if (!canContinue)
                    break;
            }


            if (canContinue) {


                requestsThatHaveBeenValidated = new ArrayList<>();
                requestsThatHaveBeenValidated.add(requests.get(0));

                int resID = getResources().getIdentifier("MSG224", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);

                names = names.substring(0, names.length() - 2);
                names += "?";

                message += " " + names;
                dialogConfirm1 = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, getActivity(), onClickOk);
            }
        } else {
            setLoading(false, "");
            int resID = getResources().getIdentifier("MSG223", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
        }
    }

    private View.OnClickListener onClickOk = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int resID = getResources().getIdentifier("MSG401", "string", getActivity().getPackageName());
            String messageNotSelected = getResources().getString(resID);
            boolean none = true;

            for (SchoolBenefitPeople person: people) {
                if (person.getCanRequest()) {

                    boolean hasRequested = false;
                    for (BenefitSchoolMaterial request : requests) {
                        if (person.getCPF().equals(request.getCPF())) {
                            hasRequested = true;
                            break;
                        }
                    }
                    if (!hasRequested) {
                        none = false;
                        messageNotSelected += "\n" + person.getName() + ", ";
                    }
                }
            }

            if(none)
            {
                //Exibir o termo
                //sendRequest();
                openTermsOfUseActivity();
            }
            else {
                messageNotSelected = messageNotSelected.substring(0, messageNotSelected.length() - 2);
                int resID2 = getResources().getIdentifier("MSG402", "string", getActivity().getPackageName());
                messageNotSelected += "\n" + getResources().getString(resID2);

                dialogConfirm2 = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), messageNotSelected, getActivity(), onClickOk2);

                if (dialogConfirm1 != null) {
                    dialogConfirm1.dismiss();
                }
            }
        }
    };

    private View.OnClickListener onClickOk2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Exibir o termo
            //sendRequest();
            openTermsOfUseActivity();

            if(dialogConfirm2 != null) {
                dialogConfirm2.dismiss();
            }
        }
    };

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


                        if (indexFound != -1) {

                            type = array.getDocumentsType().get(indexFound);
                            type.documents.add(new Documents(resultUpload.path, currentDateandTime, name, true));

                            if (!array.getDocumentsType().get(indexFound).userHasIt)
                                array.getDocumentsType().get(indexFound).userHasIt = true;
                            array.getDocumentsType().get(indexFound).documents = type.documents;
                        } else {

                            type.documents = new ArrayList<>();
                            type.documents.add(new Documents(resultUpload.path, currentDateandTime, name, true));

                            if (!type.userHasIt)
                                type.userHasIt = true;
                            array.getDocumentsType().add(type);
                        }
                        requestHistory.getDocumentsId().add(idDocument);

                        showDocumentTypeData(array.getDocumentsType(), array.getmDocumentTypesRecyclerView(), array.getCPF());
                        break;
                    }
                }
            }

        } else

        {
            showSnackBar(resultUpload.messageIdentifier, TYPE_FAILURE);
        }
    }

    public boolean hasAddedDocument(String cpf)
    {
        for (DocumentByPerson documentByPerson: documentByPersonArrayList) {
            if(documentByPerson.getCPF().equals(cpf))
            {
                if (documentByPerson.getDocumentsType().get(0).documents.size() == 0)
                    return false;
                for (Documents document : documentByPerson.getDocumentsType().get(0).documents) {
                    if (document.newAdition) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    //BOTÃO QUE RETORNA AS INFORMACOES DO PLANO
    @OnClick(R.id.btnInfo)
    public void getInformation(View view) {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.getPlanInformation(new PlanInformationBody(FahzApplication.getInstance().getFahzClaims().getCPF(), idPlan));
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
                            PlanMaterialSchoolInformation information = new Gson().fromJson((response.body().getAsJsonObject()), PlanMaterialSchoolInformation.class);
                            Utils.showSimpleDialog(getString(R.string.information), information.getPlanMaterialSchool().getRules(), null, getActivity(), null);
                        }

                    } else {
                        try {

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

    public void sendRequest() {

        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            SendReceiptRequest request = new SendReceiptRequest();
            request.setBenefitSchoolMaterial(requests);
            request.setDocuments(requestHistory.getDocumentsId());

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<CommitResponse> call = mAPIService.includeBenefitCard(request);
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        CommitResponse commitResponse = response.body();
                        if (commitResponse.commited) {
                            int resID = getResources().getIdentifier("MSG184", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), message, null, getActivity(), view -> {
                                setLoading(false, "");
                                if (getActivity() != null) {
                                    getActivity().finish();
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                }
                            });
                        } else
                            showSnackBar(commitResponse.messageIdentifier, TYPE_FAILURE);

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
                        //showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
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

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContentRequestCard, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContentRequestCard, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    //POPULA O COMBO COM AS ESCOLARIDADES POSSIVEIS
    private void populateSchoolings() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class);
            Call<JsonElement> call = mAPIService.getSchoolings(-1);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token") != null ? response.raw().headers().get("token") : FahzApplication.getInstance().getToken());
                    if (response.isSuccessful()) {

                        SchoolingReponse responseSchooling = new Gson().fromJson((response.body().getAsJsonObject()), SchoolingReponse.class);
                        schooling = new ArrayList<>();
                        schoolingHolderStr = new ArrayList<>();
                        schoolingDependentStr = new ArrayList<>();

                        Schooling s = new Schooling();
                        s.setDescription("Selecione");
                        s.setID(-1);
                        s.setValidateDependent(true);
                        s.setValidateHolder(true);
                        schooling.add(s);

                        if (responseSchooling.getRegisters().size() > 0) {
                            schooling.addAll(responseSchooling.getRegisters());
                        }

                        for (Schooling k : schooling) {
                            if(k.getValidateHolder())
                                schoolingHolderStr.add(k.getDescription());
                            if(k.getValidateDependent())
                                schoolingDependentStr.add(k.getDescription());
                        }

                        getPeople();

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

    public boolean ListContainCPF(String cpf)
    {
        for (BenefitSchoolMaterial request: requests) {
            if(request.getCPF().equals(cpf))
                return true;
        }
        return false;
    }

    public boolean RemoveCPFIfExist(String cpf)
    {
        for (BenefitSchoolMaterial request: requests) {
            if(request.getCPF().equals(cpf)) {
                requests.remove(request);
                return true;
            }
        }
        return false;
    }

    public void InsertIdSchooling(String cpf, String nameSschooling)
    {
        for (BenefitSchoolMaterial request: requests) {
            if(request.getCPF().equals(cpf)) {
                if(nameSschooling.equals("Selecione"))
                    request.setSchooling(null);
                else {
                    for (Schooling s : schooling) {
                        if (s.getDescription().equals(nameSschooling)) {
                            request.setSchooling(s);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void showDocumentTypeData(ArrayList<DocumentType> documentTypesArrayList, RecyclerView mDocumentTypesRecyclerView, String mCpf) {
        if(getActivity()!=null) {
            ListDocumentTypeAdapter documentTypeAdapter = new ListDocumentTypeAdapter(getActivity(), documentTypesArrayList, mCpf,
                    getActivity().getSupportFragmentManager(), this,null);
            mDocumentTypesRecyclerView.setAdapter(documentTypeAdapter);
            mDocumentTypesRecyclerView.setAlpha(1);
        }
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
                    if(getActivity()!= null)
                    {
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
                    }
                    showDocumentTypeData(array.getDocumentsType(), array.getmDocumentTypesRecyclerView(), array.getCPF());
                    break;
                }
            }
        }
    }

    private class SchoolBenfitPeopleCardAdapter extends RecyclerView.Adapter<SchoolBenfitPeopleCardAdapter.SchoolBenefitPeopleViewHolder> {

        SchoolBenfitPeopleCardAdapter(List<SchoolBenefitPeople> list) {
            mPeopleList = list;
        }

        void updateData(List<SchoolBenefitPeople> dependentList) {
            mPeopleList.clear();
            mPeopleList.addAll(dependentList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SchoolBenefitPeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_school_supplies_card_item,
                    parent, false);
            return new SchoolBenefitPeopleViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final SchoolBenefitPeopleViewHolder holder, final int position) {
            final SchoolBenefitPeople person = mPeopleList.get(position);
            holder.nameLabel.setText(person.getName());

            if(person.getCanRequest()) {
                holder.statusEligibleText.setVisibility(GONE);
                holder.choosePerson.setEnabled(true);
                holder.nameLabel.setTextColor(getResources().getColor(R.color.blueHeader));

                BenefitSchoolMaterial request = new BenefitSchoolMaterial();
                request.setCPF(person.getCPF());
                request.Name = person.getName();
                Plan plan = new Plan();
                plan.setId(idPlan);
                request.setPlan(plan);
                request.indexSelected = position;
                okListed.add(request);
            }
            else
            {
                holder.statusEligibleText.setVisibility(View.VISIBLE);
                holder.statusEligibleText.setText(person.getMessage());
                holder.choosePerson.setEnabled(false);
                holder.nameLabel.setTextColor(getResources().getColor(R.color.grey_light_text));
            }

            ArrayAdapter<String> adapter;

            if(person.getIsHolder())
            {
                holder.typeName.setText(getString(R.string.type_holder));
                adapter = new ArrayAdapter<>(getActivity(),
                        R.layout.spinner_layout, schoolingHolderStr);
            }
            else
            {
                holder.typeName.setText(getString(R.string.type_dependent));
                 adapter = new ArrayAdapter<>(getActivity(),
                        R.layout.spinner_layout, schoolingDependentStr);
            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.schoolingSpinner.setAdapter(adapter);

            //CHAMADA DE ENDPOINT QUE DEFINE OS DOCUMENTOS A SEREM MOSTRADOS
            getDocuments(person.getCPF(), person.getIsHolder(), holder);

            holder.separator.setVisibility(View.INVISIBLE);
            holder.chooseSchooling.setVisibility(GONE);
            holder.schoolingSpinner.setVisibility(GONE);
            holder.separator2.setVisibility(View.GONE);
            holder.documentRecycler.setVisibility(View.GONE);

            holder.choosePerson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked)
                    {
                        if(!ListContainCPF(person.getCPF()))
                        {
                            BenefitSchoolMaterial request = new BenefitSchoolMaterial();
                            request.setCPF(person.getCPF());
                            request.Name = person.getName();
                            Plan plan = new Plan();
                            plan.setId(idPlan);
                            request.setPlan(plan);
                            request.indexSelected = position;
                            requests.add(request);
                        }

                        if(holder.read.getText().equals(getString(R.string.read_more)))
                            controlVisibilityInformation(holder);
                    }
                    else
                    {
                        RemoveCPFIfExist(person.getCPF());
                        if(!holder.read.getText().equals(getString(R.string.read_more)))
                            controlVisibilityInformation(holder);
                    }
                }
            });

            holder.read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.choosePerson.isEnabled())
                        controlVisibilityInformation(holder);
                }
            });

            holder.nameLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.choosePerson.isEnabled())
                        controlVisibilityInformation(holder);
                }
            });

            holder.schoolingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {
                    Object item = adapterView.getItemAtPosition(position);
                    if (item != null) {
                        InsertIdSchooling(person.getCPF(),item.toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // TODO Auto-generated method stub

                }
            });
        }

        public void controlVisibilityInformation(SchoolBenefitPeopleViewHolder holder)
        {
            if(holder.read.getText().equals(getString(R.string.read_more)))
            {
                holder.read.setText(getString(R.string.read_less));
                holder.separator.setVisibility(View.VISIBLE);
                holder.chooseSchooling.setVisibility(View.VISIBLE);
                holder.schoolingSpinner.setVisibility(View.VISIBLE);
                holder.separator2.setVisibility(View.VISIBLE);
                holder.documentRecycler.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.read.setText(getString(R.string.read_more));
                holder.separator.setVisibility(View.INVISIBLE);
                holder.chooseSchooling.setVisibility(GONE);
                holder.schoolingSpinner.setVisibility(GONE);
                holder.separator2.setVisibility(View.GONE);
                holder.documentRecycler.setVisibility(View.GONE);
            }
        }


        private void getDocuments(final String cpf, final boolean isHolder, SchoolBenefitPeopleViewHolder holder) {
            if (sessionManager.isLoggedIn()) {
                setLoading(true, getString(R.string.loading_searching));

                if(isHolder) {
                    getBehaviorForSchoolsuplies(cpf, holder);
                }
                else {
                    getDocumentsByBehavior(cpf, holder, SystemBehavior.BehaviorEnum.INCL_CARD_MATERIAL_SCHOOL.id());
                }
            }
        }

        private void getBehaviorForSchoolsuplies(final String cpf, SchoolBenefitPeopleViewHolder holder){
            setLoading(true, "Buscando documentos");

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<TitularResponse> call = mAPIService.getDataTitular(new CPFInBody(cpf));

            call.enqueue(new Callback<TitularResponse>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(@NonNull Call<TitularResponse> call, @NonNull Response<TitularResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    // Aplicando a regra de verificação da idade do titular.
                    TitularResponse holderData = response.body();

                    String date = DateEditText.formatDate(holderData.birthdate, "yyyyMMdd", "dd/MM/yyyy");
                    Integer age = 0;
                    try {
                        age = DateEditText.getAge(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    int behavior = age >= 18 ? SystemBehavior.BehaviorEnum.INCL_CARD_MATERIAL_SCHOOL.id()
                            : SystemBehavior.BehaviorEnum.INCL_CARD_MATERIAL_SCHOOL_MINOR_HOLDER.id();

                    getDocumentsByBehavior(cpf, holder, behavior);
                    setLoading(false, "");
                }

                @Override
                public void onFailure(Call<TitularResponse> call, Throwable t) {
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
        @Override
        public int getItemCount() {
            return mPeopleList.size();
        }

        private void getDocumentsByBehavior(final String cpf, SchoolBenefitPeopleViewHolder holder, int behavior) {
            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.getDocumentsMaterialSchool(new DocumentsMaterialSchoolBody(cpf, idPlan, behavior));
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
                            Document dependents = new Gson().fromJson((response.body().getAsJsonObject()), Document.class);
                            if (dependents.documentTypes.size() > 0) {

                                DocumentByPerson documentByPerson = new DocumentByPerson((ArrayList)dependents.documentTypes, cpf,holder.documentRecycler);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                holder.documentRecycler.setHasFixedSize(true);
                                holder.documentRecycler.setLayoutManager(layoutManager);
                                holder.documentRecycler.setNestedScrollingEnabled(false);

                                for (DocumentType docType:documentByPerson.getDocumentsType()) {
                                    for (Documents doc: docType.documents) {
                                        requestHistory.getDocumentsId().add(doc.Id);
                                    }
                                }

                                documentByPersonArrayList.add(documentByPerson);
                                showDocumentTypeData((ArrayList)dependents.documentTypes, holder.documentRecycler, cpf);
                            }
                        }

                    } else {
                        try {
                            ListDocumentTypeAdapter documentTypeAdapter = new ListDocumentTypeAdapter(null,null,null,null,null,null);
                            holder.documentRecycler.setAdapter(documentTypeAdapter);

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

        class SchoolBenefitPeopleViewHolder extends RecyclerView.ViewHolder {
            private CheckBox choosePerson;
            private TextView typeName;
            private TextView nameLabel;
            private View separator;
            private TextView statusEligibleText;
            private TextView chooseSchooling;
            private Spinner schoolingSpinner;
            private View separator2;
            private RecyclerView documentRecycler;
            private TextView read;

            SchoolBenefitPeopleViewHolder(View itemView) {
                super(itemView);
                statusEligibleText= itemView.findViewById(R.id.status_eligible);
                choosePerson = itemView.findViewById(R.id.checkBox_choose);
                typeName = itemView.findViewById(R.id.type_name);
                nameLabel = itemView.findViewById(R.id.name_label);
                separator = itemView.findViewById(R.id.view_separator);
                chooseSchooling = itemView.findViewById(R.id.choose_schooling);
                schoolingSpinner = itemView.findViewById(R.id.schooling_spinner);
                separator2 = itemView.findViewById(R.id.view_separator_2);
                documentRecycler = itemView.findViewById(R.id.recyclerViewDocumentTypes);
                read = itemView.findViewById(R.id.read);

            }
        }
    }


    public void openTermsOfUseActivity() {
        Intent intent = new Intent(getActivity(), TermsOfUseActivity.class);
        intent.putExtra(Constants.TERMS_OF_USE_SELECTED, Constants.ADESAO_PLANO_MATERIAL_ESCOLAR);
        intent.putExtra(Constants.TERMS_OF_USE_FROM_BENEFIT, true);
        startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT_CARD);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.TERMS_OF_USE_RESULT_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                sendRequest();
            }
        }
    }

}