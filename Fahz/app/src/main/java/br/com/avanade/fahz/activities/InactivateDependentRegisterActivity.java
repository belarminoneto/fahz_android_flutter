package br.com.avanade.fahz.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.dialogs.TermsOfUseActivity;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.Dependent;
import br.com.avanade.fahz.model.DependentHolder;
import br.com.avanade.fahz.model.Discharge;
import br.com.avanade.fahz.model.InactivateDependent;
import br.com.avanade.fahz.model.SystemBehaviorModel;
import br.com.avanade.fahz.model.life.DependentFilterBody;
import br.com.avanade.fahz.model.response.DischargesListResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.SystemBehavior;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.COMPANION;
import static br.com.avanade.fahz.util.Constants.CPF_EDIT_DEPENDENT;
import static br.com.avanade.fahz.util.Constants.CURATOR;
import static br.com.avanade.fahz.util.Constants.DEPENDENT_RESULT;
import static br.com.avanade.fahz.util.Constants.DOCUMENT_FOR_RESULT;
import static br.com.avanade.fahz.util.Constants.Execício_de_atividade_remunerada;
import static br.com.avanade.fahz.util.Constants.FATHER;
import static br.com.avanade.fahz.util.Constants.GUARD;
import static br.com.avanade.fahz.util.Constants.MOTHER;
import static br.com.avanade.fahz.util.Constants.Matrimonio_do_dependente;
import static br.com.avanade.fahz.util.Constants.Nao_e_Estudante;
import static br.com.avanade.fahz.util.Constants.Nao_reconhecimento_de_paternidade;
import static br.com.avanade.fahz.util.Constants.Obito;
import static br.com.avanade.fahz.util.Constants.SON;
import static br.com.avanade.fahz.util.Constants.STEPSON;
import static br.com.avanade.fahz.util.Constants.SUCCESS_MESSAGE_INSERT_DEPENDENT;
import static br.com.avanade.fahz.util.Constants.Separacao_Companheiro;
import static br.com.avanade.fahz.util.Constants.Separacao_da_Mae_do_Enteado;
import static br.com.avanade.fahz.util.Constants.Separacao_judicial_ou_Divorcio;
import static br.com.avanade.fahz.util.Constants.TERMS_OF_USE_RESULT;
import static br.com.avanade.fahz.util.Constants.TUTOR;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;
import static br.com.avanade.fahz.util.Constants.VIEW_EDIT_DEPENDENT;

public class InactivateDependentRegisterActivity extends NavDrawerBaseActivity {

    public static final int TYPE_EDIT = 0;
    public static final int TYPE_INACTIVATE = 1;
    public static final int TYPE_VIEW = 2;
    public static final int TYPE_REACTIVATE = 3;
    SessionManager sessionManager;
    @BindView(R.id.content_dependent)
    LinearLayout mDependentContainer;
    private RecyclerView mDependentRecyclerView;
    private ProgressDialog mProgressDialog;
    private Dialog dialog;
    private Dialog confirmDialog;
    private Dialog deactivateDialog;
    private Button deactivateDialogConfirmButton;
    private List<Discharge> discharges = new ArrayList<>();
    private List<String> dischargesStr = new ArrayList<>();
    private InactivateDependent inactivateDependent;

    private String cpfDependent;
    private String nameDependent;
    private String kinshipValue;
    private SystemBehaviorModel systemBehavior;
    private Context mContext;

    private View.OnClickListener onClickConfirm = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editDependent(cpfDependent, false, true);
            if (confirmDialog != null) {
                confirmDialog.cancel();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadDependentList() {
        setLoading(true, getString(R.string.search_dependents));
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<DependentHolder> call = apiService.getfilterdependents(new DependentFilterBody(cpf, -1));
        call.enqueue(new Callback<DependentHolder>() {
            @Override
            public void onResponse(@NonNull Call<DependentHolder> call, @NonNull Response<DependentHolder> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        DependentHolder dependentHolder = response.body();
                        showDependentList(dependentHolder.dependents);
                    } else {
                        int resID = getResources().getIdentifier("MSG074", "string", getPackageName());
                        String message = getResources().getString(resID);
                        showSnackBar(message, TYPE_FAILURE);
                    }
                    setLoading(false, "");
                } else {
                    showSnackBar(getString(R.string.validateSentData), TYPE_FAILURE);
                    setLoading(false, "");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DependentHolder> call, @NonNull Throwable t) {
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

    private void setLoading(Boolean loading, String text) {
        if (loading) {
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    private void showDependentList(final List<Dependent> dependentList) {
        DependentListAdapter adapter = new DependentListAdapter(dependentList, (position, typeAction) -> {
            switch (typeAction) {
                case TYPE_EDIT:
                    editDependent(dependentList.get(position).cpf, false, false);
                    break;
                case TYPE_INACTIVATE:
                    if (dependentList.get(position).processing) {
                        int resID = getResources().getIdentifier("MSG048", "string", getPackageName());
                        String message = getResources().getString(resID);
                        showSnackBar(message, TYPE_SUCCESS);
                    } else if (dependentList.get(position).status.id == Integer.parseInt(Constants.PENDING_STATUS)) {
                        int resID = getResources().getIdentifier("MSG257", "string", getPackageName());
                        String message = getResources().getString(resID);
                        showSnackBar(message, TYPE_SUCCESS);
                    } else {

                        cpfDependent = dependentList.get(position).cpf;
                        nameDependent = dependentList.get(position).name;
                        kinshipValue = String.valueOf(dependentList.get(position).kinshipId);
                        systemBehavior = dependentList.get(position).systemBehavior;

                        showInactivateDialog(cpfDependent, nameDependent);
                        //removeDependent();
                    }
                    break;
                case TYPE_VIEW:
                    editDependent(dependentList.get(position).cpf, true, false);
                    break;
                case TYPE_REACTIVATE:
                    if (dependentList.get(position).processing) {
                        int resID = getResources().getIdentifier("MSG085", "string", getPackageName());
                        String message = getResources().getString(resID);
                        Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, InactivateDependentRegisterActivity.this, null);
                    } else {
                        int resID = getResources().getIdentifier("MSG081", "string", getPackageName());
                        String message = getResources().getString(resID);
                        cpfDependent = dependentList.get(position).cpf;

                        confirmDialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, InactivateDependentRegisterActivity.this, onClickConfirm);
                    }
                    break;
            }
        });
        mDependentRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inactivate_dependent_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ButterKnife.bind(this);

        mContext = this;

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        toolbarTitle.setText(getString(R.string.label_dependents));

        sessionManager = new SessionManager(getApplicationContext());
        mProgressDialog = new ProgressDialog(this);

        mDependentRecyclerView = findViewById(R.id.recyclerViewDependent);
        mDependentRecyclerView.setHasFixedSize(true);
        mDependentRecyclerView.setLayoutManager(layoutManager);

        loadDependentList();
    }

    private void editDependent(String cpfDependent, boolean onlyView, boolean reactivate) {
        Bundle bundle = new Bundle();
        bundle.putString(CPF_EDIT_DEPENDENT, cpfDependent);
        bundle.putBoolean(VIEW_EDIT_DEPENDENT, onlyView);
        bundle.putBoolean(Constants.IS_REACTIVATE, reactivate);
        Intent intent = new Intent(this, DependentActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, DEPENDENT_RESULT);
    }

    //MOSTRAR MODAL COM DATA E TIPOS DE DESLIGAMENTO
    public void showInactivateDialog(final String cpfDependent, String nameDependent) {

        deactivateDialog = new Dialog(this);
        deactivateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deactivateDialog.setContentView(R.layout.dialog_reason_inactivate_dependent);

        TextView userName = deactivateDialog.findViewById(R.id.user_name);
        userName.setText(nameDependent);

        deactivateDialogConfirmButton = deactivateDialog.findViewById(R.id.btnDialogSave);
        deactivateDialogConfirmButton.setEnabled(false);
        Button cancelButton = deactivateDialog.findViewById(R.id.btnDialogCancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactivateDialog.cancel();
            }
        });

        //POPULAR RAZOES INAcTIVAR
        final Spinner dischargeReason = deactivateDialog.findViewById(R.id.discharge_reason_spinner);
        populateDischargeReason(dischargeReason);

        //PEGAR INFORMAÇÂO DE DATA E RAZAO DESLIGAMENTO
        deactivateDialogConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inactivateDependent = new InactivateDependent();
                inactivateDependent.setCpf(cpfDependent);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
                inactivateDependent.setDate(DateEditText.parseToShortDate(mdformat.format(calendar.getTime())));
                inactivateDependent.setReasonCode(discharges.get(dischargesStr.indexOf(dischargeReason.getSelectedItem())).getId());

                //ABRE TELA DE DOCUMENTOS PASSANDO O ID
                SystemBehavior.BehaviorEnum id = SystemBehavior.BehaviorEnum.EXCL_DEPENDENT;
                if (id != null) {
                    int index = id.id();

                    Intent intent = new Intent(InactivateDependentRegisterActivity.this, DocumentsActivity.class);
                    intent.putExtra(Constants.ORIGIN_CALL_DOCUMENT, DocumentsActivity.OriginCall.FROMDEPENDENT);
                    intent.putExtra(Constants.CONTEXT_DOCUMENT, index);
                    intent.putExtra(Constants.CONTEXT_DOCUMENT_CPF, cpfDependent);
                    intent.putExtra(Constants.CONTEXT_REASON_CANCEL_BENEFIT, inactivateDependent.getReasonCode());
                    startActivityForResult(intent, DOCUMENT_FOR_RESULT);
                    deactivateDialog.cancel();
                }
            }
        });
        deactivateDialog.show();
    }

    //RESULTADO DO DIALOG CHAMADA DE DOCUMENTOS PARA INATIVAçÂO E CRIAçÂO DE NOVO DEPENDENTE
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.DOCUMENT_FOR_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                showTerm(inactivateDependent.getCpf(), Constants.TERMS_OF_INACTIVATE_DEPENDENTS);
                //callRemoveDependent();
            } else {
                showSnackBar(getString(R.string.problem_document), Constants.TYPE_FAILURE);
            }
        }

        if (requestCode == DEPENDENT_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                String returnMessage = data.getStringExtra(SUCCESS_MESSAGE_INSERT_DEPENDENT);
                showSnackBar(returnMessage, TYPE_SUCCESS);
                loadDependentList();
            }
        }

        if (requestCode == TERMS_OF_USE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                callRemoveDependent();
            } else {
                int resID = getResources().getIdentifier("MSG509", "string", mContext.getPackageName());
                String message = getResources().getString(resID);
                showSnackBar(message, Constants.TYPE_FAILURE);
            }
        }
    }

    public void callRemoveDependent() {
        setLoading(true, getString(R.string.inactivating_dependent));
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<CommitResponse> call = apiService.inactiveDependent(inactivateDependent);

        call.enqueue(new Callback<CommitResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    CommitResponse response2 = response.body();
                    if (response2.commited) {
                        showSnackBar(response2.messageIdentifier, TYPE_SUCCESS);
                        loadDependentList();
                    } else {
                        showSnackBar(response2.messageIdentifier, TYPE_FAILURE);
                    }
                } else {
                    setLoading(false, "");
                    showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }

                setLoading(false, "");
            }

            @Override
            public void onFailure(@NonNull Call<CommitResponse> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
                setLoading(false, "");
            }
        });
    }

    //POPULA O COMBO COM AS RAZOES CADASTRADAS PARA DESATIVAR
    private void populateDischargeReason(final Spinner dischargeReason) {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class);
            Call<JsonElement> call = mAPIService.getDischarges();
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token") != null ? response.raw().headers().get("token") : FahzApplication.getInstance().getToken());
                    if (response.isSuccessful()) {

                        DischargesListResponse responseDischarge = new Gson().fromJson((response.body().getAsJsonObject()), DischargesListResponse.class);
                        discharges = new ArrayList<>();
                        dischargesStr = new ArrayList<>();

                        if (responseDischarge.getDischarges().size() > 0) {
                            discharges = getDischargeByKinship(responseDischarge.getDischarges());
                        }

                        for (Discharge k : discharges) {
                            dischargesStr.add(k.getDescription());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                                R.layout.spinner_layout, dischargesStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dischargeReason.setAdapter(adapter);
                        setLoading(false, "");
                        deactivateDialogConfirmButton.setEnabled(true);
                    } else if (response.code() == 404) {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
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

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mDependentContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);

        snackbar.show();
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mDependentContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public List<Discharge> getDischargeByKinship(List<Discharge> discharges) {
        List<Discharge> dischargesReturn = new ArrayList<>();
        switch (kinshipValue) {
            case Constants.SPOUSE:
                dischargesReturn.add(discharges.get(Obito - 1));
                dischargesReturn.add(discharges.get(Separacao_judicial_ou_Divorcio - 1));
                break;
            case COMPANION:
                dischargesReturn.add(discharges.get(Obito - 1));
                dischargesReturn.add(discharges.get(Separacao_Companheiro - 1));
                break;
            case SON:
            case TUTOR:
            case CURATOR:
            case GUARD:
            case STEPSON:
                if (SystemBehavior.validateMinor(systemBehavior.ID)) {
                    dischargesReturn.add(discharges.get(Obito - 1));
                    dischargesReturn.add(discharges.get(Nao_reconhecimento_de_paternidade - 1));
                    dischargesReturn.add(discharges.get(Matrimonio_do_dependente - 1));
                    dischargesReturn.add(discharges.get(Separacao_da_Mae_do_Enteado - 1));
                } else {
                    dischargesReturn.add(discharges.get(Obito - 1));
                    dischargesReturn.add(discharges.get(Matrimonio_do_dependente - 1));
                    dischargesReturn.add(discharges.get(Execício_de_atividade_remunerada - 1));
                    dischargesReturn.add(discharges.get(Nao_e_Estudante - 1));
                }
                break;
            case FATHER:
            case MOTHER:
                dischargesReturn.add(discharges.get(Obito - 1));
                break;
        }

        return dischargesReturn;
    }

    @Override
    public void onBackPressed() {
    }

    private interface ClickListener {
        void onPositionClicked(int position, int typeAction);
    }

    private class DependentListAdapter extends RecyclerView.Adapter<DependentListAdapter.DependentViewHolder> {

        private ClickListener mListener;
        private List<Dependent> mDependentList;

        DependentListAdapter(List<Dependent> list, ClickListener listener) {
            mDependentList = list;
            mListener = listener;
        }

        void updateData(List<Dependent> dependentList) {
            mDependentList.clear();
            mDependentList.addAll(dependentList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public DependentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dependent_item,
                    parent, false);
            return new DependentViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DependentViewHolder holder, int position) {
            Dependent dependent = mDependentList.get(position);
            holder.txtDependentName.setText(dependent.name);

            if (Integer.toString(dependent.status.id).equals(Constants.ACTIVE_STATUS)) {
                holder.txtStatusDependentActive.setVisibility(View.VISIBLE);
                holder.txtStatusDependentPending.setVisibility(View.GONE);
            } else if (Integer.toString(dependent.status.id).equals(Constants.PENDING_STATUS)) {
                holder.txtStatusDependentActive.setVisibility(View.GONE);
                holder.txtStatusDependentPending.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mDependentList == null ? 0 : mDependentList.size();
        }

        class DependentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView txtDependentName;
            private ImageButton btnEdit;
            private ImageButton btnInactivate;
            private ImageButton btnView;
            private Button btnReactivate;
            private WeakReference<ClickListener> listenerWeakReference;
            private LinearLayout activeDependent;
            private LinearLayout inactiveDependent;
            private TextView txtStatusDependentActive;
            private TextView txtStatusDependentPending;

            DependentViewHolder(View itemView) {
                super(itemView);
                listenerWeakReference = new WeakReference<>(mListener);

                activeDependent = itemView.findViewById(R.id.activeDependentes);
                inactiveDependent = itemView.findViewById(R.id.inactiveDependentes);

                activeDependent.setVisibility(View.VISIBLE);
                inactiveDependent.setVisibility(View.GONE);

                txtDependentName = itemView.findViewById(R.id.text_dependent_name);
                txtStatusDependentActive = itemView.findViewById(R.id.active_status_dependent);
                txtStatusDependentPending = itemView.findViewById(R.id.pending_status_dependent);

                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnInactivate = itemView.findViewById(R.id.btnDelete);
                btnView = itemView.findViewById(R.id.btnView);
                btnReactivate = itemView.findViewById(R.id.btnReactivate);

                btnEdit.setOnClickListener(this);
                btnInactivate.setOnClickListener(this);
                btnView.setOnClickListener(this);
                btnReactivate.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                LogUtils.debug("ListDependentsActivity", "onClick: getAdapterPosition: " + getAdapterPosition());
                if (v.getId() == btnEdit.getId()) {
                    listenerWeakReference.get().onPositionClicked(getAdapterPosition(), TYPE_EDIT);
                } else if (v.getId() == btnInactivate.getId()) {
                    listenerWeakReference.get().onPositionClicked(getAdapterPosition(), TYPE_INACTIVATE);
                } else if (v.getId() == btnView.getId()) {
                    listenerWeakReference.get().onPositionClicked(getAdapterPosition(), TYPE_VIEW);
                } else if (v.getId() == btnReactivate.getId()) {
                    listenerWeakReference.get().onPositionClicked(getAdapterPosition(), TYPE_REACTIVATE);
                }
            }
        }
    }

    private void showTerm(String cpf, String codeTerm) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TERMS_OF_USE_SELECTED, codeTerm);
        bundle.putString(Constants.TERMS_OF_USE_CPF, cpf);
        Intent intent = new Intent(mContext, TermsOfUseActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, TERMS_OF_USE_RESULT);
    }
}
