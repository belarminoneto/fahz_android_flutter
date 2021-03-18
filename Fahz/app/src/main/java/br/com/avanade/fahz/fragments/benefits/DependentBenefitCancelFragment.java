package br.com.avanade.fahz.fragments.benefits;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.DependentActivity;
import br.com.avanade.fahz.activities.DocumentsActivity;
import br.com.avanade.fahz.activities.ProfileActivity;
import br.com.avanade.fahz.activities.benefits.BenefitsControlActivity;
import br.com.avanade.fahz.activities.benefits.dentalplan.BaseDentalControlActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.activities.benefits.pharmacy.BasePharmacyControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.dialogs.TermsOfUseActivity;
import br.com.avanade.fahz.model.CancelBenefit;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.Dependent;
import br.com.avanade.fahz.model.DependentHolder;
import br.com.avanade.fahz.model.Discharge;
import br.com.avanade.fahz.model.benefits.CanInactivePlanBody;
import br.com.avanade.fahz.model.life.DependentsInBenefitBody;
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
import static br.com.avanade.fahz.util.Constants.Separacao_Companheiro;
import static br.com.avanade.fahz.util.Constants.Separacao_da_Mae_do_Enteado;
import static br.com.avanade.fahz.util.Constants.Separacao_judicial_ou_Divorcio;
import static br.com.avanade.fahz.util.Constants.TERMS_OF_USE_RESULT;
import static br.com.avanade.fahz.util.Constants.TUTOR;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class DependentBenefitCancelFragment extends Fragment {

    @BindView(R.id.content_dependent)
    LinearLayout mContentDependent;
    SessionManager sessionManager;
    @BindView(R.id.recyclerViewDependent)
    RecyclerView mDependentRecyclerView;
    @BindView(R.id.text_title)
    TextView mTitleText;

    private ProgressDialog mProgressDialog;
    private int idBenefit= 0;

    private List<Discharge> discharges = new ArrayList<>();
    private List<String> dischargesStr = new ArrayList<>();

    private Dialog dialog;
    private Dialog dialogfinish;
    private Dialog dialogInfo;

    private Dependent mChooseDependent;
    private int mChooseInactivateReason;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_dependent_benefit_cancel, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false,"");
        sessionManager = new SessionManager(getActivity().getApplicationContext());


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mDependentRecyclerView = view.findViewById(R.id.recyclerViewDependent);
        mDependentRecyclerView.setHasFixedSize(true);
        mDependentRecyclerView.setLayoutManager(layoutManager);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idBenefit = bundle.getInt(Constants.BENEFIT_EXTRA, 0);
        }

        setupUi();
        getDependentes();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    private void setupUi()
    {
        if(getActivity() instanceof BaseHealthControlActivity)
            ((BaseHealthControlActivity)getActivity()).toolbarTitle.setText(getActivity().getString(R.string.label_dependents));
        else if(getActivity() instanceof BaseDentalControlActivity)
            ((BaseDentalControlActivity)getActivity()).toolbarTitle.setText(getActivity().getString(R.string.label_dependents));
        else if(getActivity() instanceof BasePharmacyControlActivity)
            ((BasePharmacyControlActivity)getActivity()).toolbarTitle.setText(getActivity().getString(R.string.label_dependents));

        switch (idBenefit) {
            case Constants.BENEFITHEALTH:
                mTitleText.setText(getString(R.string.title_benefit_cancel_dependent));
                break;
            case Constants.BENEFITDENTAL:
                mTitleText.setText(getString(R.string.title_benefit_dental_cancel_dependent));
                break;
            case Constants.BENEFITPHARMA:
                mTitleText.setText(getString(R.string.title_benefit_pharmacy_cancel_dependent));
                break;
        }
    }

    private View.OnClickListener onClickOk = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Verifica se pode desabilitar o beneficio
            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.checkCanInactivePlan(new CanInactivePlanBody(mChooseDependent.cpf, idBenefit, false));
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
                            JsonElement value = response.body().getAsJsonObject().get("canrequest");

                            if (value.getAsBoolean()) {
                                showInactivateDialog();

                            } else {
                                int resID = getResources().getIdentifier("MSG170", "string", getActivity().getPackageName());
                                String message = getResources().getString(resID);
                                showSnackBar(message, TYPE_FAILURE);
                            }
                        }

                        if (dialog != null) {
                            dialog.dismiss();
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
    };

    //BOTÃO PARA CHAMAR INCLUSAO DE DEPENDENTE
    @OnClick(R.id.btnNewDependent)
    public void submit(View view) {
        if(FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.PENDING_STATUS)&& !FahzApplication.getInstance().getFahzClaims().getPendingApproval()) {

            int resID = getResources().getIdentifier("MSG124", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            showSnackBarDismiss(message, TYPE_FAILURE,new Snackbar.Callback() {

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
                    stackBuilder.addParentStack(ProfileActivity.class);
                    stackBuilder.addNextIntent(intent);
                    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    try {
                        pendingIntent.send();
                        getActivity().finish();
                        //overridePendingTransition(0, 0);
                    } catch (Exception ex) {
                        ((BenefitsControlActivity) getActivity()).showSnackBar(ex.getMessage(), TYPE_FAILURE);
                    }
                }
            });

        } else {
            startActivityForResult(new Intent(getActivity().getApplicationContext(), DependentActivity.class), DEPENDENT_RESULT);
        }
    }

    //BUSCA DEPENDENTES QUE ESTAO NO BENEFICIO SELECIONADO
    private void getDependentes() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.getDependentesInBenefit(new DependentsInBenefitBody(mCpf, idBenefit));
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        if (response.body().getAsJsonObject().has("messageIdentifier")) {
                            CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                            showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        } else {
                            DependentHolder dependents = new Gson().fromJson((response.body().getAsJsonObject()), DependentHolder.class);
                            if (dependents.dependents.size() > 0) {
                                DependentListNotOnAdapter adapter = new DependentListNotOnAdapter(dependents.dependents);
                                mDependentRecyclerView.setAdapter(adapter);
                            } else {
                                DependentListNotOnAdapter adapter = new DependentListNotOnAdapter(new ArrayList<Dependent>());
                                mDependentRecyclerView.setAdapter(adapter);

                                int resID = getResources().getIdentifier("MSG156", "string", getActivity().getPackageName());
                                String message = getResources().getString(resID);
                                Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
                            }
                        }

                    } else {
                        try {
                            DependentListNotOnAdapter adapter = new DependentListNotOnAdapter(new ArrayList<Dependent>());
                            mDependentRecyclerView.setAdapter(adapter);

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

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContentDependent, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    //MODAL PARA PERGUNTAR SE O USUARIO DESEJA SEGUIR COM O CANCELAMENTO
    private void CallModal(Dependent dependent) {
        mChooseDependent = dependent;
        int resID = getResources().getIdentifier("MSG091", "string", getActivity().getPackageName());
        String message = getResources().getString(resID);

        dialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, getActivity(), onClickOk);

    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContentDependent, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    //MOSTRAR MODAL TIPOS DE DESLIGAMENTO
    public void showInactivateDialog() {

        dialogInfo = new Dialog(getActivity());
        dialogInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInfo.setContentView(R.layout.dialog_inactivate_benefit_dependent);

        TextView userName = dialogInfo.findViewById(R.id.user_name);
        userName.setText(mChooseDependent.name);

        TextView titleExplanation = dialogInfo.findViewById(R.id.dialog_alert_text);
        switch (idBenefit) {
            case Constants.BENEFITHEALTH:
                titleExplanation.setText(getString(R.string.text_modal_inactivate_benefit_health));
                break;
            case Constants.BENEFITDENTAL:
                titleExplanation.setText(getString(R.string.text_modal_inactivate_benefit_dental));
                break;
            case Constants.BENEFITPHARMA:
                titleExplanation.setText(getString(R.string.text_modal_inactivate_benefit_pharmacy));
                break;
        }

        Button confirmButton = dialogInfo.findViewById(R.id.btnDialogSave);
        Button cancelButton = dialogInfo.findViewById(R.id.btnDialogCancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInfo.cancel();
            }
        });


        //POPULAR RAZOES INATIVAR
        final Spinner dischargeReason = dialogInfo.findViewById(R.id.discharge_reason_spinner);
        populateDischargeReason(dischargeReason);

        //PEGAR INFORMAÇÂO DE DATA E RAZAO DESLIGAMENTO
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChooseInactivateReason = discharges.get(dischargesStr.indexOf(dischargeReason.getSelectedItem())).getId();
                CallDocuments();
            }
        });
        dialogInfo.show();
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

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                R.layout.spinner_layout, dischargesStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dischargeReason.setAdapter(adapter);
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

    //METODO CHAMADO NO CLIQUE DO BOTAO DE INCLUIR DEPENDENTE NO BENEFICIO
    private void CallDocuments()
    {
        int value = 0;

        switch (idBenefit) {
            case Constants.BENEFITHEALTH:
                value = SystemBehavior.BehaviorEnum.EXCL_DEP_HEALTH.id();
                break;
            case Constants.BENEFITDENTAL:
                value = SystemBehavior.BehaviorEnum.EXCL_DEP_DENTAL.id();
                break;
            case Constants.BENEFITPHARMA:
                value = SystemBehavior.BehaviorEnum.EXCL_DEP_PHARMACY.id();
                break;
        }

        dialogInfo.cancel();

        Intent intent = new Intent(getActivity(), DocumentsActivity.class);
        intent.putExtra(Constants.ORIGIN_CALL_DOCUMENT, DocumentsActivity.OriginCall.FROMADHESION);
        intent.putExtra(Constants.CONTEXT_DOCUMENT, value);
        intent.putExtra(Constants.CONTEXT_DOCUMENT_CPF, mChooseDependent.cpf);
        intent.putExtra(Constants.CONTEXT_REASON_CANCEL_BENEFIT, mChooseInactivateReason);
        startActivityForResult(intent,DOCUMENT_FOR_RESULT);
    }

    //RESULTADO DA VOLTA DA CHAMADA DA TELA DE DOCUMENTO
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.DOCUMENT_FOR_RESULT) {
            if (resultCode == Activity.RESULT_OK) {

                //CHAMA O ENDPOINT DE ADESAO AO BENEFICIO PARA O USUARIO
                switch (idBenefit) {
                    case Constants.BENEFITHEALTH:
                        showTerm(mChooseDependent.cpf, Constants.TERMS_OF_USE_CODE_EXCLUSION_HEALTH);
                        //cancel(mChooseDependent.cpf);
                        break;
                    case Constants.BENEFITDENTAL:
                        showTerm(mChooseDependent.cpf, Constants.TERMS_OF_USE_CODE_EXCLUSION_DENTAL);
                        //cancel(mChooseDependent.cpf);
                        break;
                    case Constants.BENEFITPHARMA:
                        showTerm(mChooseDependent.cpf, Constants.TERMS_OF_USE_CODE_EXCLUSION_PHARMA);
                        //cancel(mChooseDependent.cpf);
                        break;
                }

            } else {
                showSnackBar(getString(R.string.no_saved_document), Constants.TYPE_FAILURE);
            }
        }

        if (requestCode == TERMS_OF_USE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                cancel(mChooseDependent.cpf);
            } else {
                int resID = getResources().getIdentifier("MSG510", "string", this.getContext().getPackageName());
                String message = getResources().getString(resID);
                showSnackBar(message, Constants.TYPE_FAILURE);
            }
        }

    }

    private void cancel(String cpf)
    {
        if (sessionManager.isLoggedIn()) {
            setLoading(true,getString(R.string.loading_searching));

            CancelBenefit cancelBenefit = new CancelBenefit();
            cancelBenefit.setCpf(mChooseDependent.cpf);
            cancelBenefit.setReasonid(mChooseInactivateReason);
            cancelBenefit.setIdBenefit(idBenefit);

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<CommitResponse> call = mAPIService.inactivateBenefit(cancelBenefit);
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        CommitResponse commitResponse = response.body();
                        if(commitResponse.commited) {
                            int resID = getResources().getIdentifier("MSG093", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            dialogfinish  = Utils.showSimpleDialogReturnDialog(getString(R.string.dialog_title), message, getActivity(),finishClick);


                        }
                        else
                            showSnackBar(commitResponse.messageIdentifier,TYPE_FAILURE);

                    } else {
                        setLoading(false,"");
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }

                    setLoading(false, "");
                }

                @Override
                public void onFailure(Call<CommitResponse> call, Throwable t) {
                    setLoading(false,"");
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

    private View.OnClickListener finishClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(dialogfinish != null) {
                dialogfinish.dismiss();
            }
            getDependentes();
        }
    };

    public List<Discharge> getDischargeByKinship(List<Discharge> discharges) {
        List<Discharge> dischargesReturn = new ArrayList<>();
        switch (String.valueOf(mChooseDependent.kinshipId)) {
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
                if (SystemBehavior.validateMinor(mChooseDependent.systemBehavior.ID)) {
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

    private class DependentListNotOnAdapter extends RecyclerView.Adapter<DependentListNotOnAdapter.DependentViewHolder> {

        private List<Dependent> mDependentList;

        DependentListNotOnAdapter(List<Dependent> list) {
            mDependentList = list;
        }

        void updateData(List<Dependent> dependentList) {
            mDependentList.clear();
            mDependentList.addAll(dependentList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public DependentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dependent_cancel_benefit_item,
                    parent, false);
            return new DependentViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DependentViewHolder holder, int position) {
            final Dependent dependent = mDependentList.get(position);
            holder.txtDependentName.setText(dependent.name);

            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CallModal(dependent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDependentList.size();
        }

        class DependentViewHolder extends RecyclerView.ViewHolder {
            private TextView txtDependentName;
            private Button btnCancel;


            DependentViewHolder(View itemView) {
                super(itemView);
                txtDependentName = itemView.findViewById(R.id.text_dependent_name);
                btnCancel = itemView.findViewById(R.id.btnCancel);

            }
        }
    }


    private void showTerm(String cpf, String codeTerm) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TERMS_OF_USE_SELECTED, codeTerm);
        bundle.putString(Constants.TERMS_OF_USE_CPF, cpf);
        Intent intent = new Intent(this.getContext(), TermsOfUseActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, TERMS_OF_USE_RESULT);
    }

}