package br.com.avanade.fahz.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
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
import br.com.avanade.fahz.model.life.DependentHolderBody;
import br.com.avanade.fahz.model.response.DischargesListResponse;
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

public class ListDependentsActivity extends NavDrawerBaseActivity {

    public static final int TYPE_EDIT = 0;
    public static final int TYPE_INACTIVATE = 1;
    public static final int TYPE_VIEW = 2;
    public static final int TYPE_REACTIVATE = 3;
    private RecyclerView mDependentRecyclerView;
    boolean searchModeActive;
    private ProgressDialog mProgressDialog;
    SessionManager sessionManager;
    private Dialog dialog;
    private Dialog confirmDialog;
    private Dialog deactivateDialog;
    private Button deactivateDialogConfirmButton;

    @BindView(R.id.content_dependent)
    LinearLayout mDependentContainer;
    @BindView(R.id.btnInactivate)
    Button btnInactivate;
    @BindView(R.id.btnActivate)
    Button btnActivate;
    @BindView(R.id.btnNewDependent)
    Button btnNewDependent;

    private List<Discharge> discharges = new ArrayList<>();
    private List<String> dischargesStr = new ArrayList<>();
    private InactivateDependent inactivateDependent;

    private String cpfDependent;
    private String nameDependent;
    private String kinshipValue;
    private SystemBehaviorModel systemBehavior;

    private Boolean mReintegrated = false;
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
        //loadDependentList(searchModeActive);
    }

    private void loadDependentList(final boolean active) {
        setLoading(true, getString(R.string.search_dependents));
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<DependentHolder> call = apiService.queryDependentHolder(new DependentHolderBody(cpf, active));
        call.enqueue(new Callback<DependentHolder>() {
            @Override
            public void onResponse(@NonNull Call<DependentHolder> call, @NonNull Response<DependentHolder> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        DependentHolder dependentHolder = response.body();

                        if (!mReintegrated) {
                            if (active) {
                                btnInactivate.setVisibility(View.VISIBLE);
                                btnActivate.setVisibility(View.GONE);
                            } else {
                                btnInactivate.setVisibility(View.GONE);
                                btnActivate.setVisibility(View.VISIBLE);
                            }
                        } else {
                            btnInactivate.setVisibility(View.GONE);
                            btnActivate.setVisibility(View.GONE);
                        }

                        showDependentList(dependentHolder.dependents);
                        setLoading(false, "");
                    } else {
                        int resID = getResources().getIdentifier("MSG074", "string", getPackageName());
                        String message = getResources().getString(resID);
                        showSnackBar(message, TYPE_FAILURE);
                        setLoading(false,"");
                    }
                } else {
                    showSnackBar(getString(R.string.validateSentData), TYPE_FAILURE);
                    setLoading(false,"");
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

    private void setLoading(Boolean loading, String text){
        if(loading){
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        else{
            mProgressDialog.dismiss();
        }
    }

    private void showDependentList(final List<Dependent> dependentList) {
        DependentListAdapter adapter = new DependentListAdapter(dependentList, new ClickListener() {
            @Override
            public void onPositionClicked(final int position, int typeAction) {
                switch (typeAction) {
                    case TYPE_EDIT:
                        editDependent(dependentList.get(position).cpf, false,false);
                        break;
                    case TYPE_INACTIVATE:
                        if (dependentList.get(position).processing ) {
                            int resID = getResources().getIdentifier("MSG048", "string", getPackageName());
                            String message = getResources().getString(resID);
                            showSnackBar(message, TYPE_SUCCESS);
                        } else if ( dependentList.get(position).status.id == Integer.parseInt(Constants.PENDING_STATUS))
                        {
                            int resID = getResources().getIdentifier("MSG257", "string", getPackageName());
                            String message = getResources().getString(resID);
                            showSnackBar(message, TYPE_SUCCESS);
                        }
                        else {

                            cpfDependent = dependentList.get(position).cpf;
                            nameDependent = dependentList.get(position).name;
                            kinshipValue = String.valueOf(dependentList.get(position).kinshipId);
                            systemBehavior = dependentList.get(position).systemBehavior;


                            removeDependent();
                        }
                        break;
                    case TYPE_VIEW:
                        editDependent(dependentList.get(position).cpf, true,false);
                        break;
                    case TYPE_REACTIVATE:
                        if (dependentList.get(position).processing) {
                            int resID = getResources().getIdentifier("MSG085", "string", getPackageName());
                            String message = getResources().getString(resID);
                            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, ListDependentsActivity.this, null);
                        } else {
                            int resID = getResources().getIdentifier("MSG081", "string", getPackageName());
                            String message = getResources().getString(resID);
                            cpfDependent = dependentList.get(position).cpf;

                            confirmDialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, ListDependentsActivity.this, onClickConfirm);
                        }
                        break;
                }
            }
        });
        mDependentRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_dependents);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ButterKnife.bind(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        toolbarTitle.setText(getString(R.string.label_dependents));
        searchModeActive = true;
        btnInactivate.setVisibility(View.VISIBLE);

        sessionManager = new SessionManager(getApplicationContext());
        mProgressDialog = new ProgressDialog(this);

        View mVIew = findViewById(R.id.content_dependent);
        mDependentRecyclerView = findViewById(R.id.recyclerViewDependent);
        mDependentRecyclerView.setHasFixedSize(true);
        mDependentRecyclerView.setLayoutManager(layoutManager);

        //Trata reintegrado
        if (FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Reintegrated)) {
            mReintegrated = true;
            btnNewDependent.setVisibility(View.GONE);
            btnActivate.setVisibility(View.GONE);
            btnInactivate.setVisibility(View.GONE);
        } else if (FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.INACTIVE_STATUS)
                || FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Extended)) {
            btnNewDependent.setVisibility(View.GONE);
            btnActivate.setVisibility(View.GONE);
            btnInactivate.setVisibility(View.GONE);
        }

        loadDependentList(searchModeActive);
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

    private void removeDependent() {
        //MOSTRAR MODAL PERGUNTANDO SE CONFIRMA O DESLIGAMENTO
        int resID = getResources().getIdentifier("MSG017", "string", getPackageName());
        String message = getResources().getString(resID);
        dialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, this, onClickOk);

    }

    private View.OnClickListener onClickOk = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showInactivateDialog(cpfDependent,nameDependent);
            if(dialog != null) {
                dialog.dismiss();
            }
        }
    };

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

                    Intent intent = new Intent(ListDependentsActivity.this, DocumentsActivity.class);
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

                Bundle bundle = new Bundle();
                bundle.putString(Constants.TERMS_OF_USE_SELECTED, Constants.TERMS_OF_INACTIVATE_DEPENDENTS);
                bundle.putString(Constants.TERMS_OF_USE_CPF, cpfDependent);
                Intent intent = new Intent(this, TermsOfUseActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, TERMS_OF_USE_RESULT);

            } else {
                showSnackBar(getString(R.string.problem_document), Constants.TYPE_FAILURE);
            }
        }

        if (requestCode == DEPENDENT_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                String returnMessage = data.getStringExtra(SUCCESS_MESSAGE_INSERT_DEPENDENT);
                showSnackBar(returnMessage, TYPE_SUCCESS);
                loadDependentList(searchModeActive);
            }
        }

        //CHECAR SE O TERMO SE TRATA DE ATIVAÇÃO OU INATIVAÇÃO PARA TRATAR O RETORNO.
        if (requestCode == TERMS_OF_USE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                callRemoveDependent();
            }
        }
    }

    public void callRemoveDependent()
    {
        setLoading(true,getString(R.string.inactivating_dependent));
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
                        loadDependentList(searchModeActive);
                    } else {
                        showSnackBar(response2.messageIdentifier, TYPE_FAILURE);
                    }
                }
                else
                {
                    setLoading(false,"");
                    showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }

                setLoading(false,"");
            }

            @Override
            public void onFailure(@NonNull Call<CommitResponse> call, @NonNull Throwable t) {
                if(t instanceof SocketTimeoutException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if(t instanceof UnknownHostException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
                setLoading(false,"");
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
                        setLoading(false,"");
                        deactivateDialogConfirmButton.setEnabled(true);
                    } else if (response.code() == 404) {
                        setLoading(false,"");
                        showSnackBar(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {
                        setLoading(false,"");
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
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


    @OnClick(R.id.btnNewDependent)
    public void submit(View view) {
        if(FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.PENDING_STATUS) && !FahzApplication.getInstance().getFahzClaims().getPendingApproval()) {
            int resID = getResources().getIdentifier("MSG124", "string", getPackageName());
            String message = getResources().getString(resID);
            showSnackBarDismiss(message, TYPE_FAILURE,new Snackbar.Callback() {

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    Intent intent = new Intent(ListDependentsActivity.this, ProfileActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(ListDependentsActivity.this);
                    stackBuilder.addParentStack(ProfileActivity.class);
                    stackBuilder.addNextIntent(intent);
                    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    try {
                        pendingIntent.send();
                        finish();
                        //overridePendingTransition(0, 0);
                    }
                    catch (Exception ex)
                    {
                        showSnackBar(ex.getMessage(),TYPE_FAILURE);
                    }
                    drawer.closeDrawer(GravityCompat.START);

                }
            });
        }
        else
        {
            startActivityForResult(new Intent(getApplicationContext(), DependentActivity.class), DEPENDENT_RESULT);
        }
    }

    @OnClick(R.id.btnInactivate)
    public void searchActiveDependent(View view) {
        searchModeActive = false;
        loadDependentList(searchModeActive);
    }

    @OnClick(R.id.btnActivate)
    public void searchInativateDependent(View view) {
        searchModeActive = true;
        loadDependentList(searchModeActive);
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


    //VALIDA SE O CAMPO DE DATA DESLIGAMENTO ESTA PRRENCHIDO CORRETAMENTE
    public boolean checkDate(String text) {
        return text.trim().length() != 0 && DateEditText.isValidDate(text);
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

            if(Integer.toString(dependent.status.id).equals(Constants.ACTIVE_STATUS))
            {
                holder.txtStatusDependentActive.setVisibility(View.VISIBLE);
                holder.txtStatusDependentPending.setVisibility(View.GONE);
            }
            else if(Integer.toString(dependent.status.id).equals(Constants.PENDING_STATUS))
            {
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

                if (searchModeActive) {
                    activeDependent.setVisibility(View.VISIBLE);
                    inactiveDependent.setVisibility(View.GONE);
                } else {
                    activeDependent.setVisibility(View.GONE);
                    inactiveDependent.setVisibility(View.VISIBLE);
                }

                txtDependentName = itemView.findViewById(R.id.text_dependent_name);
                txtStatusDependentActive = itemView.findViewById(R.id.active_status_dependent);
                txtStatusDependentPending = itemView.findViewById(R.id.pending_status_dependent);

                if (!mReintegrated) {
                    btnEdit = itemView.findViewById(R.id.btnEdit);
                    btnInactivate = itemView.findViewById(R.id.btnDelete);
                    btnView = itemView.findViewById(R.id.btnView);
                    btnReactivate = itemView.findViewById(R.id.btnReactivate);

                    btnEdit.setOnClickListener(this);
                    btnInactivate.setOnClickListener(this);
                    btnView.setOnClickListener(this);
                    btnReactivate.setOnClickListener(this);
                } else {
                    btnEdit.setVisibility(View.GONE);
                    btnInactivate.setVisibility(View.GONE);
                    btnView.setVisibility(View.GONE);
                    btnReactivate.setVisibility(View.GONE);
                }

                if (FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Normal) &&
                        (FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.ACTIVE_STATUS) ||
                                FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.PENDING_STATUS))) {
                    btnReactivate.setVisibility(View.VISIBLE);
                } else {
                    btnReactivate.setVisibility(View.INVISIBLE);
                }
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

    private interface ClickListener {
        void onPositionClicked(int position, int typeAction);
    }
}
