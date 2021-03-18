package br.com.avanade.fahz.fragments.benefits.schoolsupplies;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
import br.com.avanade.fahz.model.SendReceiptRequest;
import br.com.avanade.fahz.model.UploadResponse;
import br.com.avanade.fahz.model.document.DocumentsMaterialSchoolBody;
import br.com.avanade.fahz.model.response.SchoolBenefitRequest;
import br.com.avanade.fahz.model.schoolsupplies.PlanInformationBody;
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

public class SendReceiptFragment extends Fragment implements ListDocumentTypeAdapter.DeleteOnClickHandler {

    @BindView(R.id.content_receipt)
    LinearLayout mContentReceipt;
    SessionManager sessionManager;
    @BindView(R.id.recyclerViewPeople)
    RecyclerView mPeopleRecyclerView;

    @BindView(R.id.btnSave)
    Button mBtnSave;

    private ProgressDialog mProgressDialog;
    private int idPlan;

    private List<BenefitSchoolMaterial> requests;
    private List<BenefitSchoolMaterial> requestsThatHaveBeenValidated;
    final List<DocumentByPerson> documentByPersonArrayList = new ArrayList<>();
    private Dialog dialogQuestion;

    private List<BenefitSchoolMaterial> okListed;

    HistoryModel requestHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_school_receipt_refund, container, false);
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
        }

        requests = new ArrayList<>();
        okListed = new ArrayList<>();

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
            LogUtils.error("SendReceiptFragment", ex);
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
            Call<JsonElement> call = mAPIService.whoCanSendRecipt(new CPFInBody(mCpf));
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
                            SchoolBenefitRequest dependents = new Gson().fromJson((response.body().getAsJsonObject()), SchoolBenefitRequest.class);
                            if (dependents.getRegisters().size() > 0) {
                                SchooolBenfitPeopleRefundAdapter adapter = new SchooolBenfitPeopleRefundAdapter(dependents.getRegisters());
                                mPeopleRecyclerView.setAdapter(adapter);
                            }
                        }

                    } else {
                        try {
                            SchooolBenfitPeopleRefundAdapter adapter = new SchooolBenfitPeopleRefundAdapter(new ArrayList<SchoolBenefitPeople>());
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

    //BOTÃO QUE RETORNA AS INFORMACOES DO PLANO
    @OnClick(R.id.btnInfo)
    public void getInformation(View view) {
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

    //BOTÃO PARA SALVAR AS INFORMACOES
    @OnClick(R.id.btnSave)
    public void submit(View view) {
        if (requests != null && requests.size() > 0) {
            boolean canContinue = true;

            for (BenefitSchoolMaterial request : requests) {
                View v = mPeopleRecyclerView.getChildAt(request.indexSelected);
                if (canContinue && !hasAddedDocument(request.getCPF())) {
                    canContinue = false;
                    int resID = getResources().getIdentifier("MSG231", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), message + " " + request.Name, null, getActivity(), null);
                }

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


                int resID = getResources().getIdentifier("MSG286", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                dialogQuestion = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, getActivity(),onClickAccept);
            }
        } else {
            setLoading(false, "");
            int resID = getResources().getIdentifier("MSG345", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
        }
    }

    private View.OnClickListener onClickAccept = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(dialogQuestion!= null)
                dialogQuestion.dismiss();
            sendRequest();
        }
    };

    //ENVIA A SOLICITAÇÃO DE REEMBOLSO PARA MATERIAL ESCOLAR
    public void sendRequest() {

        mBtnSave.setEnabled(false);
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));


            if (requestHistory.getDocumentsId().size() > 0) {

                SendReceiptRequest request = new SendReceiptRequest();
                request.setBenefitSchoolMaterial(requests);
                request.setDocuments(requestHistory.getDocumentsId());

                Gson gson = new Gson();
                String jsonInString = gson.toJson(request);

                APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
                Call<JsonElement> call = mAPIService.includebenefitrefundandreceips(request, false);
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

                                mBtnSave.setEnabled(true);

                                String message = getString(R.string.check_send_refund);
                                Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), message, null, getActivity(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(getContext(), MainActivity.class));
                                        setLoading(false, "");
                                        getActivity().finish();
                                    }
                                });

                            }

                        } else {
                            mBtnSave.setEnabled(true);
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

                        setLoading(false, "");
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        setLoading(false, "");
                        if (getActivity() != null) {
                            if (t instanceof SocketTimeoutException)
                                showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                            else if (t instanceof UnknownHostException)
                                showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                            else
                                showSnackBar(t.getMessage(), TYPE_FAILURE);

                            mBtnSave.setEnabled(true);
                        }
                    }

                });
            }
            else
            {

            }
        }
    }

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
        for (BenefitSchoolMaterial request : requests) {
            if (request.getCPF().equals(cpf))
                return true;
        }
        return false;
    }

    //CASO O CHECKBOX SEJA TIRADA A SELEÇÃO REMOVE O CPF DA LISTA
    public boolean RemoveCPFIfExist(String cpf) {
        for (BenefitSchoolMaterial request : requests) {
            if (request.getCPF().equals(cpf)) {
                requests.remove(request);
                return true;
            }
        }
        return false;
    }

    //METODO DE RETORNO DO UPLOADDIALOGFRAGMENT, APÓS CHAMADA ENDPOINT DE UPLOAD
    public void uploadDocumentUpdate(UploadResponse resultUpload, int idType, DocumentType type, final String name, String mCpf,String idDocument) {
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

    public boolean hasAddedDocument(String cpf) {
        for (DocumentByPerson documentByPerson : documentByPersonArrayList) {
            if (documentByPerson.getCPF().equals(cpf)) {
                if (documentByPerson.getDocumentsType().get(0).documents.size() == 0)
                    return false;
            }
        }

        return requestHistory.getDocumentsId().size() != 0;
    }

    public void showDocumentTypeData(ArrayList<DocumentType> documentTypesArrayList, RecyclerView mDocumentTypesRecyclerView, String mCpf) {
        ListDocumentTypeAdapter documentTypeAdapter = new ListDocumentTypeAdapter(getActivity(), documentTypesArrayList, mCpf,
                getActivity().getSupportFragmentManager(), this, getString(R.string.check_send_document));
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
                    }
                    showDocumentTypeData(array.getDocumentsType(), array.getmDocumentTypesRecyclerView(), array.getCPF());
                    break;
                }
            }
        }
    }


    private void localeDecimalInput(final EditText editText) {

        DecimalFormat decFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols symbols = decFormat.getDecimalFormatSymbols();
        final String defaultSeperator = Character.toString(symbols.getDecimalSeparator());

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().contains(defaultSeperator))
                    editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                else
                    editText.setKeyListener(DigitsKeyListener.getInstance("0123456789" + defaultSeperator));
            }
        });
    }

    private class SchooolBenfitPeopleRefundAdapter extends RecyclerView.Adapter<SchooolBenfitPeopleRefundAdapter.SchoolBenefitPeopleViewHolder> {

        private List<SchoolBenefitPeople> mPeopleList;

        SchooolBenfitPeopleRefundAdapter(List<SchoolBenefitPeople> list) {
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_school_supplies_receipt_item,
                    parent, false);
            return new SchoolBenefitPeopleViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final SchoolBenefitPeopleViewHolder holder, final int position) {
            final SchoolBenefitPeople person = mPeopleList.get(position);
            holder.nameLabel.setText(person.getName());

            if (person.getCanRequest()) {
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

            } else {
                holder.statusEligibleText.setVisibility(View.VISIBLE);
                holder.choosePerson.setEnabled(false);
                holder.nameLabel.setTextColor(getResources().getColor(R.color.grey_light_text));
            }

            ArrayAdapter<String> adapter;

            if (person.getIsHolder())
                holder.typeName.setText(getString(R.string.type_holder));
            else
                holder.typeName.setText(getString(R.string.type_dependent));


            //CHAMADA DE ENDPOINT QUE DEFINE OS DOCUMENTOS A SEREM MOSTRADOS
            getDocuments(person.getCPF(),holder);

            holder.separator2.setVisibility(View.INVISIBLE);
            holder.documentRecycler.setVisibility(View.GONE);

            holder.choosePerson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        if (!ListContainCPF(person.getCPF())) {
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
                    } else {
                        RemoveCPFIfExist(person.getCPF());
                        if(!holder.read.getText().equals(getString(R.string.read_more)))
                            controlVisibilityInformation(holder);
                    }
                }
            });

            holder.read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controlVisibilityInformation(holder);

                }
            });

            holder.nameLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controlVisibilityInformation(holder);

                }
            });

        }

        public void controlVisibilityInformation(SchoolBenefitPeopleViewHolder holder)
        {
            if(holder.read.getText().equals(getString(R.string.read_more)))
            {
                holder.read.setText(getString(R.string.read_less));
                holder.separator2.setVisibility(View.VISIBLE);
                holder.documentRecycler.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.separator2.setVisibility(View.INVISIBLE);
                holder.documentRecycler.setVisibility(View.GONE);
                holder.read.setText(getString(R.string.read_more));
            }
        }

        public void onClickShow(View view, SchoolBenefitPeopleViewHolder holder) {
            if(holder.read.getText().equals(getString(R.string.read_more)))
            {
                holder.read.setText(getString(R.string.read_less));
                holder.separator2.setVisibility(View.VISIBLE);
                holder.documentRecycler.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.choosePerson.setChecked(false);
                holder.separator2.setVisibility(View.INVISIBLE);
                holder.documentRecycler.setVisibility(View.GONE);
                holder.read.setText(getString(R.string.read_more));
            }
        }

        private void getDocuments(final String cpf, final SchoolBenefitPeopleViewHolder holder) {
            if (sessionManager.isLoggedIn()) {
                setLoading(true, getString(R.string.loading_searching));

                APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
                Call<JsonElement> call = mAPIService.getDocumentsMaterialSchool(new DocumentsMaterialSchoolBody(cpf, idPlan, SystemBehavior.BehaviorEnum.INCL_RECEIPT_CARD_MATERIAL_SCHOOL.id()));
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
        }

        @Override
        public int getItemCount() {
            return mPeopleList.size();
        }

        class SchoolBenefitPeopleViewHolder extends RecyclerView.ViewHolder {
            private CheckBox choosePerson;
            private TextView typeName;
            private TextView nameLabel;
            private TextView statusEligibleText;
            private View separator2;
            private RecyclerView documentRecycler;
            private TextView read;

            SchoolBenefitPeopleViewHolder(View itemView) {
                super(itemView);
                statusEligibleText = itemView.findViewById(R.id.status_eligible);
                choosePerson = itemView.findViewById(R.id.checkBox_choose);
                typeName = itemView.findViewById(R.id.type_name);
                nameLabel = itemView.findViewById(R.id.name_label);
                separator2 = itemView.findViewById(R.id.view_separator_2);
                documentRecycler = itemView.findViewById(R.id.recyclerViewDocumentTypes);
                read = itemView.findViewById(R.id.read);
            }
        }
    }

}