package br.com.avanade.fahz.fragments.benefits.healthplan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.activities.benefits.schoolsupplies.BaseSchoolSuppliesControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.ReasonRequestPersonalCard;
import br.com.avanade.fahz.model.RequestNewPersonalCard;
import br.com.avanade.fahz.model.response.ReasonRequestPersonalCardResponse;
import br.com.avanade.fahz.model.response.RequestNewPersonalCardResponse;
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
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class RequestNewCardFragment extends Fragment {

    @BindView(R.id.content_card)
    LinearLayout mConten;
    SessionManager sessionManager;
    @BindView(R.id.recyclerViewPeople)
    RecyclerView mPeopleRecyclerView;

    @BindView(R.id.btnSave)
    public Button enterButton;

    private ProgressDialog mProgressDialog;
    private int idPlan;

    private List<ReasonRequestPersonalCard> reasons = new ArrayList<>();
    private List<String> reasonsStr = new ArrayList<>();

    private List<RequestNewPersonalCard> requests;
    private List<RequestNewPersonalCard> requestsThatHaveBeenValidated;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_request_new_card, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false,"");
        sessionManager = new SessionManager(getActivity().getApplicationContext());


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mPeopleRecyclerView = view.findViewById(R.id.recyclerViewPeople);
        mPeopleRecyclerView.setHasFixedSize(true);
        mPeopleRecyclerView.setLayoutManager(layoutManager);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idPlan = bundle.getInt(Constants.PLAN_ID, 0);
        }

        requests = new ArrayList<>();

        setupUi();
        populateReasonsRequest();

        buttonEnabled(false);
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
            ((BaseHealthControlActivity)getActivity()).toolbarTitle.setText(getActivity().getString(R.string.new_card_label));
   }

    //BUSCA AS PESSOAS POSSIVEIS DE SOLCIITAR CARTAO
    private void getRequestCards()
    {
        if (sessionManager.isLoggedIn()) {
            setLoading(true,getString(R.string.loading_searching));

            String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.StartRequestNewCard(new CPFInBody(mCpf));
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
                            RequestNewPersonalCardResponse requests = new Gson().fromJson((response.body().getAsJsonObject()), RequestNewPersonalCardResponse.class);
                            if(requests.getRegisters().size()>0) {
                                RequestNewCardAdapter adapter = new RequestNewCardAdapter(requests.getRegisters());
                                mPeopleRecyclerView.setAdapter(adapter);
                            }
                        }

                    } else {
                        try {
                            RequestNewCardAdapter adapter = new RequestNewCardAdapter(new ArrayList<RequestNewPersonalCard>());
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

            for (RequestNewPersonalCard request : requests) {
                View v = mPeopleRecyclerView.getChildAt(request.indexSelected);
                if (request.getReasonRequestPersonalCard() == null) {
                    canContinue = false;
                    int resID = getResources().getIdentifier("MSG347", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), message + " " + request.getName(), null, getActivity(), null);
                }

                if (!canContinue)
                    break;
            }

            if (canContinue) {
                requestsThatHaveBeenValidated = new ArrayList<>();
                requestsThatHaveBeenValidated.add(requests.get(0));
                sendRequest();
            }
        } else {
            setLoading(false, "");
            int resID = getResources().getIdentifier("MSG345", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
        }
    }

    public void sendRequest() {

        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<CommitResponse> call = mAPIService.generateRequestNewCard(requests);
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        CommitResponse commitResponse = response.body();
                        if (commitResponse.commited) {
                            Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), commitResponse.messageIdentifier, null, getActivity(), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                    setLoading(false, "");
                                    getActivity().finish();
                                }
                            });
                        } else
                            showSnackBar(commitResponse.messageIdentifier, TYPE_FAILURE);

                    } else {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }

                    setLoading(false, "");
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
        Snackbar snackbar = Snackbar.make(mConten, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mConten, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    //POPULA O COMBO COM AS RAZOES POSSIVEIS
    private void populateReasonsRequest() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class);
            Call<JsonElement> call = mAPIService.GetReasonRequestPersonalCard();
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token") != null ? response.raw().headers().get("token") : FahzApplication.getInstance().getToken());
                    if (response.isSuccessful()) {

                        ReasonRequestPersonalCardResponse responseReasons = new Gson().fromJson((response.body().getAsJsonObject()), ReasonRequestPersonalCardResponse.class);
                        reasons = new ArrayList<>();
                        reasonsStr = new ArrayList<>();

                        ReasonRequestPersonalCard s = new ReasonRequestPersonalCard();
                        s.setDescription("Selecione");
                        s.setId(-1);
                        reasons.add(s);

                        if (responseReasons.getReasons().size() > 0) {
                            reasons.addAll(responseReasons.getReasons());
                        }

                        for ( ReasonRequestPersonalCard k : reasons) {
                                reasonsStr.add(k.getDescription());
                        }

                        getRequestCards();

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
        for (RequestNewPersonalCard request: requests) {
            if(request.getCPF().equals(cpf))
                return true;
        }
        return false;
    }

    public boolean RemoveCPFIfExist(String cpf) {
        for (RequestNewPersonalCard request : requests) {
            if (request.getCPF().equals(cpf)) {
                requests.remove(request);

                if (requests.size() == 0)
                    buttonEnabled(false);
                else
                    buttonEnabled(true);

                return true;
            }
        }
        return false;
    }

    public void InsertIdRequest(String cpf, String reasonDescription)
    {
        for (RequestNewPersonalCard request: requests) {
            if(request.getCPF().equals(cpf)) {
                if(reasonDescription.equals("Selecione"))
                    request.setReasonRequestPersonalCard(null);
                else {
                    for (ReasonRequestPersonalCard s : reasons) {
                        if (s.getDescription().equals(reasonDescription)) {
                            request.setReasonRequestPersonalCard(s);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void buttonEnabled(boolean isButtonEnabled) {
        if(isButtonEnabled) {
            enterButton.setEnabled(true);
            enterButton.setTextColor(getResources().getColor(R.color.white_text));
            enterButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            enterButton.setEnabled(false);
            enterButton.setTextColor(getResources().getColor(R.color.grey_text));
            enterButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    private class RequestNewCardAdapter extends RecyclerView.Adapter<RequestNewCardAdapter.RequestCardViewHolder> {

        private List<RequestNewPersonalCard> mRequests;

        RequestNewCardAdapter(List<RequestNewPersonalCard> list) {
            mRequests = list;
        }

        void updateData(List<RequestNewPersonalCard> requests) {
            mRequests.clear();
            mRequests.addAll(requests);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RequestCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_request_new_card_item,
                    parent, false);
            return new RequestCardViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final RequestCardViewHolder holder, final int position) {
            final RequestNewPersonalCard request = mRequests.get(position);
            holder.nameLabel.setText(request.getName());

            int resID = getResources().getIdentifier("MSG359", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            holder.canRequestText.setText(message);

            if(!request.getRequestDate().equals("") && !request.getRequestDate().equals("00010101"))
                holder.lastDateRequested.setText(Utils.formatDateString(request.getRequestDate()));
            else
                holder.lastDateRequested.setText("");

            if(request.getCanRequest()) {
                holder.reasonSpinner.setEnabled(true);
                holder.choosePerson.setEnabled(true);
                holder.reasonLabel.setTextColor(getResources().getColor(R.color.grey_dark));
                holder.nameLabel.setTextColor(getResources().getColor(R.color.blueHeader));
            }
            else
            {
                holder.reasonSpinner.setEnabled(false);
                holder.choosePerson.setEnabled(false);
                holder.reasonLabel.setTextColor(getResources().getColor(R.color.grey_light_text));
                holder.nameLabel.setTextColor(getResources().getColor(R.color.grey_light_text));
            }

            ArrayAdapter<String> adapter;

            if(request.getIsHolder())
            {
                holder.typeName.setText(getString(R.string.type_holder));
                adapter = new ArrayAdapter<>(getActivity(),
                        R.layout.spinner_layout, reasonsStr);
            }
            else
            {
                holder.typeName.setText(getString(R.string.type_dependent));
                 adapter = new ArrayAdapter<>(getActivity(),
                        R.layout.spinner_layout, reasonsStr);
            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.reasonSpinner.setAdapter(adapter);

            if(request.getReasonRequestPersonalCard() != null && !holder.reasonSpinner.isEnabled()) {
                if (request.getReasonRequestPersonalCard().getDescription() != null)
                    holder.reasonSpinner.setSelection(reasonsStr.indexOf(request.getReasonRequestPersonalCard().getDescription()));
                else if (request.getReasonRequestPersonalCard().getId() != 0)
                    holder.reasonSpinner.setSelection(request.getReasonRequestPersonalCard().getId());
            }

            holder.separator.setVisibility(View.INVISIBLE);
            holder.reasonLabel.setVisibility(GONE);
            holder.reasonSpinner.setVisibility(GONE);
            holder.lastDateRequested.setVisibility(View.GONE);
            holder.lastDateView.setVisibility(View.GONE);
            holder.canRequestText.setVisibility(GONE);

            holder.choosePerson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked)
                    {
                        if(!ListContainCPF(request.getCPF()) && request.getCanRequest())
                        {
                            requests.add(request);

                            if (requests.size() == 0)
                                buttonEnabled(false);
                            else
                                buttonEnabled(true);
                        }

                        holder.read.setText(getString(R.string.read_less));
                        holder.separator.setVisibility(View.VISIBLE);
                        holder.reasonLabel.setVisibility(View.VISIBLE);
                        holder.reasonSpinner.setVisibility(View.VISIBLE);
                        holder.lastDateRequested.setVisibility(View.VISIBLE);
                        holder.lastDateView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        RemoveCPFIfExist(request.getCPF());
                        holder.read.setText(getString(R.string.read_more));
                        holder.separator.setVisibility(View.INVISIBLE);
                        holder.reasonLabel.setVisibility(GONE);
                        holder.reasonSpinner.setVisibility(GONE);
                        holder.lastDateRequested.setVisibility(View.GONE);
                        holder.lastDateView.setVisibility(View.GONE);
                    }
                }
            });

            holder.read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.read.getText().equals(getString(R.string.read_more)))
                    {
                        holder.read.setText(getString(R.string.read_less));
                        holder.separator.setVisibility(View.VISIBLE);
                        holder.reasonLabel.setVisibility(View.VISIBLE);
                        holder.reasonSpinner.setVisibility(View.VISIBLE);
                        holder.lastDateRequested.setVisibility(View.VISIBLE);
                        holder.lastDateView.setVisibility(View.VISIBLE);
                        if(!request.getCanRequest()) {
                            holder.canRequestText.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        RemoveCPFIfExist(request.getCPF());
                        holder.read.setText(getString(R.string.read_more));
                        holder.separator.setVisibility(View.INVISIBLE);
                        holder.reasonLabel.setVisibility(GONE);
                        holder.reasonSpinner.setVisibility(GONE);
                        holder.lastDateRequested.setVisibility(View.GONE);
                        holder.lastDateView.setVisibility(View.GONE);
                        holder.canRequestText.setVisibility(View.GONE);
                    }
                }
            });

            holder.reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {
                    Object item = adapterView.getItemAtPosition(position);
                    if (item != null) {
                        InsertIdRequest(request.getCPF(),item.toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // TODO Auto-generated method stub

                }
            });

        }

        @Override
        public int getItemCount() {
            return mRequests.size();
        }

        class RequestCardViewHolder extends RecyclerView.ViewHolder {
            private CheckBox choosePerson;
            private TextView typeName;
            private TextView nameLabel;
            private View separator;
            private TextView read;

            private TextView canRequestText;
            private TextView reasonLabel;
            private Spinner reasonSpinner;
            private TextInputEditText lastDateRequested;
            private TextInputLayout lastDateView;

            RequestCardViewHolder(View itemView) {
                super(itemView);
                canRequestText= itemView.findViewById(R.id.can_request_text);
                reasonLabel= itemView.findViewById(R.id.reason_id);
                choosePerson = itemView.findViewById(R.id.checkBox_choose);
                typeName = itemView.findViewById(R.id.type_name);
                nameLabel = itemView.findViewById(R.id.name_label);
                separator = itemView.findViewById(R.id.view_separator_2);
                lastDateRequested = itemView.findViewById(R.id.last_date_edit_text);
                reasonSpinner = itemView.findViewById(R.id.reason_sppiner);
                read = itemView.findViewById(R.id.read);
                lastDateView = itemView.findViewById(R.id.last_date_view);

            }
        }
    }

}