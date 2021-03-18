package br.com.avanade.fahz.fragments.benefits.scholarship;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import br.com.avanade.fahz.Adapter.ListDocumentTypeAdapter;
import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.dialogs.TermsOfUseActivity;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CalculateRefundRequest;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.Document;
import br.com.avanade.fahz.model.DocumentByPerson;
import br.com.avanade.fahz.model.DocumentDeleteRequest;
import br.com.avanade.fahz.model.DocumentType;
import br.com.avanade.fahz.model.Documents;
import br.com.avanade.fahz.model.FinancialPlanList;
import br.com.avanade.fahz.model.FinancialPlanOperation;
import br.com.avanade.fahz.model.FinancialPlanUpdate;
import br.com.avanade.fahz.model.HistoryModel;
import br.com.avanade.fahz.model.UploadResponse;
import br.com.avanade.fahz.model.document.QueryDocumentTypeBody;
import br.com.avanade.fahz.model.response.CalculateRefundResponse;
import br.com.avanade.fahz.model.response.FinancialPlanResponse;
import br.com.avanade.fahz.model.response.ScholarshipLifeResponse;
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
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class SendRefundFragment extends Fragment implements ListDocumentTypeAdapter.DeleteOnClickHandler {

    @BindView(R.id.content_request_refund)
    LinearLayout mContentRequestRefund;
    SessionManager sessionManager;
    @BindView(R.id.recyclerViewPeople)
    RecyclerView mPeopleRecyclerView;
    @BindView(R.id.lblPlanName)
    TextView lblPlanName;

    RecyclerView mFinancialItemRecyclerView;

    final List<DocumentByPerson> documentByPersonArrayList = new ArrayList<>();
    private br.com.avanade.fahz.model.ScholarshipLifeResponse mData;

    private ProgressDialog mProgressDialog;
    private int idPlan;
    private int systemBehavior;

    private List<FinancialPlanOperation> requests;
    private List<FinancialPlanOperation> requestsThatHaveBeenValidated;

    HistoryModel requestHistory;

    HashMap<String, Double> percentRefundList =new HashMap<String, Double>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_scholarship_refund, container, false);
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
            systemBehavior = bundle.getInt(Constants.SCHOLARSHIP_REFUND_DOCUMENT, 0);
        }

        requests = new ArrayList<>();

        getScholarship();
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
            LogUtils.error("SendRefundFragment", ex);
        }
    }

    private void getScholarship() {
        setLoading(true, getString(R.string.loading_searching));
        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        Call<JsonElement> call = mAPIService.getScholarshipData(new CPFInBody(cpf));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    ScholarshipLifeResponse responseValue = new Gson().fromJson((response.body().getAsJsonObject()), ScholarshipLifeResponse.class);
                    mData = responseValue.getScholarshipLifeResponse();
                    if(mData.getPlan()!=null)
                        lblPlanName.setText(mData.getPlan().getDescription());
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

    //BUSCA DEPENDENTES QUE ESTAO NO BENEFICIO SELECIONADO
    private void getPeople() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.GetFinancialsPlanItens(mData.getScholarshipLife().getId());
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
                            Calendar today = Calendar.getInstance();
                            FinancialPlanResponse responseFinancial = new Gson().fromJson((response.body().getAsJsonObject()), FinancialPlanResponse.class);

                            List<List<FinancialPlanList>> newListItem = new ArrayList<>();

                            for (List<FinancialPlanList> itemList : responseFinancial.getFinancialPlanList()) {

                                List<FinancialPlanList> smallList = new ArrayList<>();
                                for (FinancialPlanList item : itemList) {
                                    if (item.getDateTocheck().before(today.getTime()) || (
                                            item.getDateTocheck().getMonth() == today.getTime().getMonth() &&
                                                    item.getDateTocheck().getYear() == today.getTime().getYear()
                                            )) {
                                        if (item.getStatus() == Constants.PENDING_REFUND) {
                                            smallList.add(item);
                                        }
                                    }
                                }
                                if(smallList.size()>0)
                                    newListItem.add(smallList);
                            }

                            ScholarshipPeopleRefundAdapter adapter = new ScholarshipPeopleRefundAdapter(newListItem);
                            mPeopleRecyclerView.setAdapter(adapter);
                        }

                    } else {
                        try {
                            ScholarshipPeopleRefundAdapter adapter = new ScholarshipPeopleRefundAdapter(null);
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

    //BOTÃO PARA SALVAR AS INFORMACOES
    @OnClick(R.id.btnSave)
    public void submit(View view) {
        if (requests != null && requests.size() > 0) {
            boolean canContinue = true;

            for (FinancialPlanOperation request : requests) {
                View v = mPeopleRecyclerView.getChildAt(request.indexSelected);

                if (request.getAmountPaid() == null || request.getAmountPaid().equals("")) {
                    canContinue = false;
                    int resID = getResources().getIdentifier("MSG324", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), message + " " + request.period, null, getActivity(), null);
                } else {

                    if (canContinue && !hasAddedDocument(request.cpf)) {
                        canContinue = false;

                        int resID = getResources().getIdentifier("MSG325", "string", getActivity().getPackageName());
                        String message = getResources().getString(resID);
                        Utils.showSimpleDialog(getString(R.string.dialog_title), message + " " + request.period, null, getActivity(), null);
                    }

                    if (!canContinue)
                        break;
                }
            }

            if (canContinue) {
                requestsThatHaveBeenValidated = new ArrayList<>();
                requestsThatHaveBeenValidated.add(requests.get(0));

                int resID = getResources().getIdentifier("MSG323", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                Utils.showQuestionDialog(getString(R.string.dialog_title), message, getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Exibir o termo aqui, se aceito chama sendRequest()
                        openTermsOfUseActivity();
                    }
                });
            }
        } else {
            setLoading(false, "");
            int resID = getResources().getIdentifier("MSG183", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
        }

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
                else {
                    boolean hasNew = false;
                    for (DocumentType document : documentByPerson.getDocumentsType()) {
                        if (document.documents.size() == 0) {
                            hasNew = false;
                        } else {
                            for (Documents doc : document.documents) {
                                if (doc.newAdition)
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

    public void sendRequest() {

        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            FinancialPlanUpdate planUpdate = new FinancialPlanUpdate();
            planUpdate.setFinancialPlanOperation(requests);
            planUpdate.setDocuments(requestHistory.getDocumentsId());

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.updateFinancialPlan(planUpdate);
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
                    }
                }

            });
        }
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContentRequestRefund, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContentRequestRefund, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public boolean ListContainId(String idFinancialItem)
    {
        for (FinancialPlanOperation request: requests) {
            if(request.getIdFinancialPlan().equals(idFinancialItem))
                return true;
        }
        return false;
    }

    public boolean RemoveFinancialitemIfExist(String idFinancialItem)
    {
        for (FinancialPlanOperation request: requests) {
            if(request.getIdFinancialPlan().equals(idFinancialItem)) {
                requests.remove(request);
                return true;
            }
        }
        return false;
    }

    public boolean addValueIfExist(String idFinancialItem, String valueToRefund) {

        for (FinancialPlanOperation request : requests) {
            if (request.getIdFinancialPlan().equals(idFinancialItem)) {
                if (valueToRefund != null && !valueToRefund.equals("")) {
                    try {
                        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                        Number number = format.parse(valueToRefund.replace("R$",""));
                        request.setAmountPaid(number.doubleValue());
                        return true;
                    } catch (Exception ex) {

                        int resID = getResources().getIdentifier("MSG221", "string", getActivity().getPackageName());
                        String message = getResources().getString(resID);
                        Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
                    }
                } else
                    return false;
            }
        }
        return false;

    }


    public void showDocumentTypeData(ArrayList<DocumentType> documentTypesArrayList, RecyclerView mDocumentTypesRecyclerView, String mCpf) {
        ListDocumentTypeAdapter documentTypeAdapter = new ListDocumentTypeAdapter(getActivity(), documentTypesArrayList, mCpf,
                getActivity().getSupportFragmentManager(), this, null);
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
                if(!s.toString().matches("^R\\$(\\d{1,3}(\\.\\d{3})*|(\\d+))(\\,\\d{2})?$"))
                {
                    String userInput= ""+s.toString().replaceAll("[^\\d]", "");
                    StringBuilder cashAmountBuilder = new StringBuilder(userInput);

                    while (cashAmountBuilder.length() > 3 && cashAmountBuilder.charAt(0) == '0') {
                        cashAmountBuilder.deleteCharAt(0);
                    }
                    while (cashAmountBuilder.length() < 3) {
                        cashAmountBuilder.insert(0, '0');
                    }
                    cashAmountBuilder.insert(cashAmountBuilder.length()-2, ',');
                    cashAmountBuilder.insert(0, "R$");

                    editText.setText(cashAmountBuilder.toString());
                    // keeps the cursor always to the right
                    Selection.setSelection(editText.getText(), cashAmountBuilder.toString().length());

                }
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

    private class ScholarshipPeopleRefundAdapter extends RecyclerView.Adapter<ScholarshipPeopleRefundAdapter.ScholarshipPeopleRefundViewHolder> {

        private List<List<FinancialPlanList>> mList;

        ScholarshipPeopleRefundAdapter(List<List<FinancialPlanList>> list) {
            mList = list;
        }

        void updateData(List<List<FinancialPlanList>> dependentList) {
            mList.clear();
            mList.addAll(dependentList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ScholarshipPeopleRefundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_scholarship_refund_item,
                    parent, false);
            return new ScholarshipPeopleRefundViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ScholarshipPeopleRefundViewHolder holder, final int position) {
            final List<FinancialPlanList> person = mList.get(position);
            if(person != null && person.size() >0)
            {
                holder.nameLabel.setText(person.get(0).getScholarshipLife().getName());

                if (person.get(0).getScholarshipLife().getName() == null) {
                    holder.nameLabel.setText(FahzApplication.getInstance().getFahzClaims().getName());
                    holder.typeName.setText(getString(R.string.type_holder));
                } else {
                    holder.typeName.setText(getString(R.string.type_dependent));
                }


                holder.courseDesc.setText(person.get(0).getScholarshipLife().getCourse());
                holder.typeScholarshipDesc.setText(person.get(0).getScholarshipLife().getTypeMonitoringScholarship().description);
                holder.schoolDesc.setText(person.get(0).getScholarshipLife().getInstitution());
                holder.startDesc.setText(DateEditText.parseTODate(person.get(0).getScholarshipLife().getStartCourse()));
                holder.endDesc.setText(DateEditText.parseTODate(person.get(0).getScholarshipLife().getEndCourse()));

                holder.separator.setVisibility(View.INVISIBLE);
                holder.courseDesc.setVisibility(GONE);
                holder.typeScholarshipDesc.setVisibility(GONE);
                holder.schoolDesc.setVisibility(View.GONE);
                holder.startDesc.setVisibility(View.GONE);
                holder.endDesc.setVisibility(View.GONE);
                holder.financialItemRecycler.setVisibility(View.GONE);
                holder.lblCourse.setVisibility(GONE);
                holder.lblTypeScholarship.setVisibility(GONE);
                holder.lblSchool.setVisibility(View.GONE);
                holder.lblStart.setVisibility(View.GONE);
                holder.lblEnd.setVisibility(View.GONE);
                holder.lblDocuments.setVisibility(GONE);
                holder.documentRecycler.setVisibility(GONE);
                holder.lblStartInformation.setVisibility(GONE);

                holder.choosePerson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            holder.read.setText(getString(R.string.read_less));
                            holder.separator.setVisibility(VISIBLE);
                            holder.courseDesc.setVisibility(VISIBLE);
                            holder.typeScholarshipDesc.setVisibility(VISIBLE);
                            holder.schoolDesc.setVisibility(VISIBLE);
                            holder.startDesc.setVisibility(VISIBLE);
                            holder.endDesc.setVisibility(VISIBLE);
                            holder.financialItemRecycler.setVisibility(VISIBLE);
                            holder.documentRecycler.setVisibility(VISIBLE);

                            holder.lblCourse.setVisibility(VISIBLE);
                            holder.lblTypeScholarship.setVisibility(VISIBLE);
                            holder.lblSchool.setVisibility(VISIBLE);
                            holder.lblStart.setVisibility(VISIBLE);
                            holder.lblEnd.setVisibility(VISIBLE);
                            holder.lblDocuments.setVisibility(VISIBLE);
                            holder.lblStartInformation.setVisibility(VISIBLE);
                        } else {
                            holder.read.setText(getString(R.string.read_more));
                            holder.separator.setVisibility(View.INVISIBLE);
                            holder.courseDesc.setVisibility(View.GONE);
                            holder.typeScholarshipDesc.setVisibility(View.GONE);
                            holder.schoolDesc.setVisibility(View.GONE);
                            holder.startDesc.setVisibility(View.GONE);
                            holder.endDesc.setVisibility(View.GONE);
                            holder.financialItemRecycler.setVisibility(View.GONE);
                            holder.documentRecycler.setVisibility(GONE);

                            holder.lblCourse.setVisibility(GONE);
                            holder.lblTypeScholarship.setVisibility(GONE);
                            holder.lblSchool.setVisibility(View.GONE);
                            holder.lblStart.setVisibility(View.GONE);
                            holder.lblEnd.setVisibility(View.GONE);
                            holder.lblDocuments.setVisibility(GONE);
                            holder.lblStartInformation.setVisibility(GONE);
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

                ScholarshipFinancialItemAdapter adapterFinancial = new ScholarshipFinancialItemAdapter(person, holder);
                LinearLayoutManager layoutManagerFinancial = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                holder.financialItemRecycler.setHasFixedSize(true);
                holder.financialItemRecycler.setLayoutManager(layoutManagerFinancial);

                holder.financialItemRecycler.setAdapter(adapterFinancial);


                //CHAMADA DE ENDPOINT QUE DEFINE OS DOCUMENTOS A SEREM MOSTRADOS
                getDocuments(person.get(0).getScholarshipLife().getCPF(), holder);

                getPercent(person.get(0).getScholarshipLife().getCPF(), person.get(0));
            }
            else
            {
                holder.separator.setVisibility(View.INVISIBLE);
                holder.courseDesc.setVisibility(GONE);
                holder.typeScholarshipDesc.setVisibility(GONE);
                holder.schoolDesc.setVisibility(View.GONE);
                holder.startDesc.setVisibility(View.GONE);
                holder.endDesc.setVisibility(View.GONE);
                holder.financialItemRecycler.setVisibility(View.GONE);
                holder.lblCourse.setVisibility(GONE);
                holder.lblTypeScholarship.setVisibility(GONE);
                holder.lblSchool.setVisibility(View.GONE);
                holder.lblStart.setVisibility(View.GONE);
                holder.lblEnd.setVisibility(View.GONE);
                holder.lblDocuments.setVisibility(GONE);
                holder.documentRecycler.setVisibility(GONE);
                holder.lblStartInformation.setVisibility(GONE);
            }
        }

        public void controlVisibilityInformation(ScholarshipPeopleRefundViewHolder holder)
        {
            if(holder.read.getText().equals(getString(R.string.read_more)))
            {
                holder.read.setText(getString(R.string.read_less));
                holder.separator.setVisibility(VISIBLE);
                holder.courseDesc.setVisibility(VISIBLE);
                holder.typeScholarshipDesc.setVisibility(VISIBLE);
                holder.schoolDesc.setVisibility(VISIBLE);
                holder.startDesc.setVisibility(VISIBLE);
                holder.endDesc.setVisibility(VISIBLE);
                holder.financialItemRecycler.setVisibility(VISIBLE);
                holder.documentRecycler.setVisibility(VISIBLE);

                holder.lblCourse.setVisibility(VISIBLE);
                holder.lblTypeScholarship.setVisibility(VISIBLE);
                holder.lblSchool.setVisibility(VISIBLE);
                holder.lblStart.setVisibility(VISIBLE);
                holder.lblEnd.setVisibility(VISIBLE);
                holder.lblDocuments.setVisibility(VISIBLE);
                holder.lblStartInformation.setVisibility(VISIBLE);
            }
            else
            {
                holder.choosePerson.setChecked(false);
                holder.separator.setVisibility(View.INVISIBLE);
                holder.courseDesc.setVisibility(View.GONE);
                holder.typeScholarshipDesc.setVisibility(View.GONE);
                holder.schoolDesc.setVisibility(View.GONE);
                holder.startDesc.setVisibility(View.GONE);
                holder.endDesc.setVisibility(View.GONE);
                holder.financialItemRecycler.setVisibility(View.GONE);
                holder.documentRecycler.setVisibility(GONE);

                holder.lblCourse.setVisibility(GONE);
                holder.lblTypeScholarship.setVisibility(GONE);
                holder.lblSchool.setVisibility(View.GONE);
                holder.lblStart.setVisibility(View.GONE);
                holder.lblEnd.setVisibility(View.GONE);
                holder.lblDocuments.setVisibility(GONE);
                holder.lblStartInformation.setVisibility(GONE);
                holder.read.setText(getString(R.string.read_more));
            }
        }
        private void getDocuments(final String cpf, final ScholarshipPeopleRefundViewHolder holder) {
            if (sessionManager.isLoggedIn()) {
                setLoading(true, getString(R.string.loading_searching));

                APIService apiService = ServiceGenerator.createService(APIService.class);
                Call<Document> call = apiService.queryDocumentType(new QueryDocumentTypeBody(cpf, systemBehavior, false, 0));
                call.enqueue(new Callback<Document>() {
                    @Override
                    public void onResponse(@NonNull Call<Document> call, @NonNull Response<Document> response) {
                        FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                        if (response.isSuccessful()) {
                            Document documentResponse = response.body();
                            assert documentResponse != null;
                            if (documentResponse.documentTypes.size() > 0) {

                                DocumentByPerson documentByPerson = new DocumentByPerson((ArrayList)documentResponse.documentTypes, cpf,holder.documentRecycler);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                holder.documentRecycler.setHasFixedSize(true);
                                holder.documentRecycler.setLayoutManager(layoutManager);
                                holder.documentRecycler.setNestedScrollingEnabled(false);

                                for (DocumentType docType : documentByPerson.getDocumentsType()) {
                                    docType.documents = new ArrayList<>();
                                    docType.userHasIt = false;
                                }

                                documentByPersonArrayList.add(documentByPerson);
                                showDocumentTypeData((ArrayList)documentResponse.documentTypes, holder.documentRecycler, cpf);
                            }


                        } else {
                            try {
                                ListDocumentTypeAdapter documentTypeAdapter = new ListDocumentTypeAdapter(null, null, null, null, null,null);
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
                    public void onFailure(Call<Document> call, Throwable t) {
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

        private void getPercent(final String cpf, FinancialPlanList financialPlan) {
            if (sessionManager.isLoggedIn()) {
                setLoading(true, getString(R.string.loading_searching));

                CalculateRefundRequest request = new CalculateRefundRequest();
                request.IdFinancialPlan = financialPlan.getId();
                request.AmountPaid = 100.00;

                APIService apiService = ServiceGenerator.createService(APIService.class);
                Call<CalculateRefundResponse> call = apiService.calculateRefund(request);
                call.enqueue(new Callback<CalculateRefundResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CalculateRefundResponse> call, @NonNull Response<CalculateRefundResponse> response) {
                        FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                        if (response.isSuccessful()) {

                            CalculateRefundResponse responseBody = response.body();

                            if(responseBody.commited)
                                percentRefundList.put(cpf, responseBody.refund/100);

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
                    public void onFailure(Call<CalculateRefundResponse> call, Throwable t) {
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
            return mList.size();
        }

        class ScholarshipPeopleRefundViewHolder extends RecyclerView.ViewHolder {
            private CheckBox choosePerson;
            private TextView typeName;
            private TextView nameLabel;
            private View separator;
            private TextView read;
            private TextView courseDesc;
            private TextView typeScholarshipDesc;
            private TextView schoolDesc;
            private TextView startDesc;
            private TextView endDesc;

            private TextView lblCourse;
            private TextView lblTypeScholarship;
            private TextView lblSchool;
            private TextView lblStart;
            private TextView lblEnd;
            private TextView lblDocuments;
            private TextView lblStartInformation;


            private RecyclerView documentRecycler;
            private RecyclerView financialItemRecycler;

            ScholarshipPeopleRefundViewHolder(View itemView) {
                super(itemView);
                read= itemView.findViewById(R.id.read);
                choosePerson = itemView.findViewById(R.id.checkBox_choose);
                typeName = itemView.findViewById(R.id.type_name);
                nameLabel = itemView.findViewById(R.id.name_label);
                separator = itemView.findViewById(R.id.view_separator);
                courseDesc = itemView.findViewById(R.id.course_desc);
                typeScholarshipDesc = itemView.findViewById(R.id.type_scholarship_desc);
                schoolDesc = itemView.findViewById(R.id.school_desc);
                startDesc = itemView.findViewById(R.id.start_desc);
                endDesc = itemView.findViewById(R.id.end_desc);
                financialItemRecycler = itemView.findViewById(R.id.recyclerViewFinancialItem);
                documentRecycler = itemView.findViewById(R.id.documentRecycler);
                lblStartInformation = itemView.findViewById(R.id.lblStartInformation);

                lblCourse = itemView.findViewById(R.id.lblCourse);
                lblTypeScholarship = itemView.findViewById(R.id.lblTypeScholarship);
                lblSchool = itemView.findViewById(R.id.lblSchool);
                lblStart = itemView.findViewById(R.id.lblStart);
                lblEnd = itemView.findViewById(R.id.lblEnd);
                lblDocuments = itemView.findViewById(R.id.lblDocuments);
            }
        }
    }

    private class ScholarshipFinancialItemAdapter extends RecyclerView.Adapter<ScholarshipFinancialItemAdapter.ScholarshipFinancialItemViewHolder> {

        private List<FinancialPlanList> mList;
        private RadioButton lastChecked = null;
        private int lastCheckedPos = 0;
        private ScholarshipPeopleRefundAdapter.ScholarshipPeopleRefundViewHolder mHolder;

        ScholarshipFinancialItemAdapter(List<FinancialPlanList> list, ScholarshipPeopleRefundAdapter.ScholarshipPeopleRefundViewHolder holder) {
            mList = list;
            mHolder = holder;
        }

        void updateData(List<FinancialPlanList> dependentList) {
            mList.clear();
            mList.addAll(dependentList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ScholarshipFinancialItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_scholarship_financial_item,
                    parent, false);
            return new ScholarshipFinancialItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ScholarshipFinancialItemViewHolder holder, final int position) {
            final FinancialPlanList item = mList.get(position);
            holder.radioSelectedParcel.setText(getString(R.string.hint_select_parcel));
            holder.radioSelectedParcel.setTag(new Integer(position));
            holder.value_refund_edit_text.setTag(new Integer(position));

            holder.monthDesc.setText(item.getPeriod());
            if(item.getAmountRefundPeriod()!= null)
                holder.refund_value.setText("R$" + String.format("%.2f", item.getAmountRefundPeriod()));
            else
                holder.refund_value.setText("R$ 0,00");
            if(position == 0 && mList.get(0).isSelected() && holder.radioSelectedParcel.isChecked())
            {
                lastChecked = holder.radioSelectedParcel;
                lastCheckedPos = 0;
            }

            holder.radioSelectedParcel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    RadioButton cb = (RadioButton)v;
                    final int clickedPos = ((Integer)cb.getTag()).intValue();

                    if(cb.isChecked())
                    {
                        if(lastChecked != null && lastChecked != cb)
                        {
                            RemoveFinancialitemIfExist(mList.get(lastCheckedPos).getId());
                            lastChecked.setChecked(false);
                            mList.get(lastCheckedPos).setSelected(false);
                        }

                        lastChecked = cb;
                        lastCheckedPos = clickedPos;
                    }
                    else
                        lastChecked = null;

                    if (!ListContainId(mList.get(clickedPos).getId())) {
                        FinancialPlanOperation request = new FinancialPlanOperation();
                        request.setIdFinancialPlan(mList.get(clickedPos).getId());
                        request.period = mList.get(position).getPeriod();
                        request.cpf = mList.get(position).getScholarshipLife().getCPF();
                        request.indexSelected = position;
                        requests.add(request);

                        addValueIfExist(mList.get(clickedPos).getId(), holder.value_refund_edit_text.getText().toString());
                    }

                    mList.get(clickedPos).setSelected(cb.isChecked());
                }
            });

            localeDecimalInput(holder.value_refund_edit_text);
            holder.value_refund_edit_text.setText( String.format("%.2f",item.getAmountPeriod()));

            holder.value_refund_edit_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    final int clickedPos = ((Integer)holder.value_refund_edit_text.getTag()).intValue();
                    boolean hasToAdd = addValueIfExist(mList.get(clickedPos).getId(), holder.value_refund_edit_text.getText().toString());

                    holder.radioSelectedParcel.setChecked(true);
                    lastChecked =  holder.radioSelectedParcel;
                    mHolder.choosePerson.setChecked(true);
                    if(!hasToAdd)
                    {
                        if (!ListContainId(mList.get(clickedPos).getId())) {
                            FinancialPlanOperation request = new FinancialPlanOperation();
                            request.setIdFinancialPlan(mList.get(clickedPos).getId());
                            request.period = mList.get(position).getPeriod();
                            request.indexSelected = position;
                            request.cpf = mList.get(clickedPos).getScholarshipLife().getCPF();
                            requests.add(request);

                            addValueIfExist(mList.get(clickedPos).getId(), holder.value_refund_edit_text.getText().toString());
                        }
                    }

                    try {
                        //CALCULAR O VALOR DO REEMBOLSO
                        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                        Number number = format.parse(holder.value_refund_edit_text.getText().toString().replace("R$", ""));

                        Double newValue = number.doubleValue()* percentRefundList.get(mList.get(clickedPos).getScholarshipLife().getCPF());
                        holder.refund_value.setText("R$" + String.format("%.2f", newValue));
                    }
                    catch (Exception ex)
                    {
                        int resID = getResources().getIdentifier("MSG221", "string", getActivity().getPackageName());
                        String message = getResources().getString(resID);
                        Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ScholarshipFinancialItemViewHolder extends RecyclerView.ViewHolder {
            private RadioButton radioSelectedParcel;
            private TextView monthDesc;
            private TextView refund_value;
            private EditText value_refund_edit_text;


            ScholarshipFinancialItemViewHolder(View itemView) {
                super(itemView);
                radioSelectedParcel= itemView.findViewById(R.id.radioSelectedParcel);
                monthDesc = itemView.findViewById(R.id.monthDesc);
                refund_value = itemView.findViewById(R.id.refund_value);
                value_refund_edit_text = itemView.findViewById(R.id.value_refund_edit_text);
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