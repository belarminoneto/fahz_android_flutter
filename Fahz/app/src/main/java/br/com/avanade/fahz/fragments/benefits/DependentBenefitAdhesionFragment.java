package br.com.avanade.fahz.fragments.benefits;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
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
import br.com.avanade.fahz.dialogs.CheckPendingTermsActivity;
import br.com.avanade.fahz.dialogs.TermsOfUseActivity;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.Dependent;
import br.com.avanade.fahz.model.DependentHolder;
import br.com.avanade.fahz.model.DependentLife;
import br.com.avanade.fahz.model.SystemBehaviorModel;
import br.com.avanade.fahz.model.benefits.CanInactivePlanBody;
import br.com.avanade.fahz.model.lgpdModel.GetListOfTerms;
import br.com.avanade.fahz.model.lgpdModel.PoliciesAndTerm;
import br.com.avanade.fahz.model.life.DependentsOutOfBenefitBody;
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

import static android.app.Activity.RESULT_OK;
import static br.com.avanade.fahz.util.Constants.DEPENDENT_RESULT;
import static br.com.avanade.fahz.util.Constants.DOCUMENT_FOR_RESULT;
import static br.com.avanade.fahz.util.Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED;
import static br.com.avanade.fahz.util.Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_SON_SHARED;
import static br.com.avanade.fahz.util.Constants.TERMS_OF_USE_RESULT;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class DependentBenefitAdhesionFragment extends Fragment {

    @BindView(R.id.content_dependent)
    LinearLayout mContentDependent;
    SessionManager sessionManager;
    @BindView(R.id.recyclerViewDependent)
    RecyclerView mDependentRecyclerView;

    @BindView(R.id.text_title)
    TextView mTitleText;

    private DependentLife mDependentLifeSelected;
    private ProgressDialog mProgressDialog;
    private int idBenefit = 0;
    private String actualDependentCpf;
    private String mConstant;
    private Dialog dialog;
    private DependentHolder dependents;
    public static String codeTerm = null;
    public static GetListOfTerms getListOfTerms = new GetListOfTerms();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_dependent_benefit_adhesion, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false, "");
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
                mTitleText.setText(getString(R.string.title_health_adhesion_dependent));
                break;
            case Constants.BENEFITDENTAL:
                mTitleText.setText(getString(R.string.title_dental_adhesion_dependent));
                break;
            case Constants.BENEFITPHARMA:
                mTitleText.setText(getString(R.string.title_pharmacy_adhesion_dependent));
                break;
        }
    }

    //BUSCA DEPENDENTES QUE NAO ESTAO NO BENEFICIO SELECIONADO
    private void getDependentes()
    {
        if (sessionManager.isLoggedIn()) {
            setLoading(true,getString(R.string.loading_searching));

            String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.getDependentesOutOfBenefit(new DependentsOutOfBenefitBody(mCpf, idBenefit));
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
                            dependents = new Gson().fromJson((response.body().getAsJsonObject()), DependentHolder.class);
                            if(dependents.dependents.size()>0) {
                                DependentListNotOnAdapter adapter = new DependentListNotOnAdapter(dependents.dependents);
                                mDependentRecyclerView.setAdapter(adapter);
                            }
                            else
                            {
                                DependentListNotOnAdapter adapter = new DependentListNotOnAdapter(new ArrayList<Dependent>());
                                mDependentRecyclerView.setAdapter(adapter);

                                int resID = getResources().getIdentifier("MSG150", "string", getActivity().getPackageName());
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
                    setLoading(false,"");                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
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

                    Intent intent = null;
                    if(getActivity()!=null)
                        intent = new Intent(getActivity(), ProfileActivity.class);
                    else
                        intent = new Intent(getContext(), ProfileActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
                    stackBuilder.addParentStack(ProfileActivity.class);
                    stackBuilder.addNextIntent(intent);
                    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    try {
                        pendingIntent.send();
                        getActivity().finish();
                        //overridePendingTransition(0, 0);
                    }
                    catch (Exception ex)
                    {
                        ((BenefitsControlActivity) getActivity()).showSnackBar(ex.getMessage(), TYPE_FAILURE);
                    }
                }
            });

        }
        else
        {
            startActivityForResult(new Intent(getActivity().getApplicationContext(), DependentActivity.class), DEPENDENT_RESULT);
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

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContentDependent, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    //METODO CHAMADO NO CLIQUE DO BOTAO DE INATIVAR DEPENDENTE NO BENEFICIO
    private void CallDocuments(Dependent dependent) {
        SystemBehaviorModel behavior = dependent.systemBehavior;
        if(behavior.ID != 0) {
            SystemBehavior.BehaviorEnum behaviorToCheck = SystemBehavior.BehaviorEnum.fromId(behavior.ID);
            int value = 0;

            switch (idBenefit) {
                case Constants.BENEFITHEALTH:
                    value = SystemBehavior.verifyHealthBehavior(behaviorToCheck).id();
                    break;
                case Constants.BENEFITDENTAL:
                    value = SystemBehavior.verifyDentalBehavior(behaviorToCheck).id();
                    break;
                case Constants.BENEFITPHARMA:
                    value = SystemBehavior.verifyPharmacyBehavior(behaviorToCheck).id();
                    setTermsAdhesionPharmacy(mDependentLifeSelected.systemBehavior.ID);

                    break;
            }

            actualDependentCpf = dependent.cpf;

            if (idBenefit != Constants.BENEFITPHARMA) {
                Intent intent = new Intent(getActivity(), DocumentsActivity.class);
                intent.putExtra(Constants.ORIGIN_CALL_DOCUMENT, DocumentsActivity.OriginCall.FROMADHESION);
                intent.putExtra(Constants.CONTEXT_DOCUMENT, value);
                intent.putExtra(Constants.CONTEXT_DOCUMENT_CPF, dependent.cpf);
                startActivityForResult(intent, DOCUMENT_FOR_RESULT);
            } else {
                Dependent dep = new Dependent();

                if (mDependentLifeSelected != null) {
                    dep.cpf = mDependentLifeSelected.cpf;
                    dep.systemBehavior = mDependentLifeSelected.systemBehavior;
                }

                queryDependentDetails(dep);
            }
        }
        else
        {
            int resID = getResources().getIdentifier("MSG027", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            showSnackBar(message, TYPE_FAILURE);
        }
    }

    public void verifyCanRequestBenefit(final Dependent dependent,final View button )
    {
        //Verifica se pode desabilitar o beneficio
        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        Call<JsonElement> call = mAPIService.checkCanInactivePlan(new CanInactivePlanBody(dependent.cpf, idBenefit, false));
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
                            CallDocuments(dependent);
                        } else {
                            int resID = getResources().getIdentifier("MSG170", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            showSnackBar(message,TYPE_FAILURE);
                        }
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
                button.setEnabled(true);

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

    //RESULTADO DA VOLTA DA CHAMADA DA TELA DE DOCUMENTO
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.DOCUMENT_FOR_RESULT) {
            if (resultCode == RESULT_OK) {
                Dependent dep = new Dependent();

                if (mDependentLifeSelected != null) {
                    dep.cpf = mDependentLifeSelected.cpf;
                    dep.systemBehavior = mDependentLifeSelected.systemBehavior;

                    //CHECAR O TIPO DE PLANO DE ADESÃO
                    if (idBenefit == Constants.BENEFITHEALTH) {
                        setTermsAdhesionHealth(dep.systemBehavior.ID);
                    } else if (idBenefit == Constants.BENEFITDENTAL) {
                        setTermsAdhesionDental(dep.systemBehavior.ID);
                    } else if (idBenefit == Constants.BENEFITPHARMA) {
                        setTermsAdhesionPharmacy(dep.systemBehavior.ID);
                    }
                }

                queryDependentDetails(dep);
            } else {
                showSnackBar(getString(R.string.no_saved_document), Constants.TYPE_FAILURE);
            }
        } else if (requestCode == Constants.DEPENDENT_RESULT) {
            if (resultCode == RESULT_OK) {

                String dependentCPF = data.getStringExtra(Constants.INSERT_DEPENDENT_OBJECT);
                setLoading(true, getString(R.string.search_dependent_detail));
                APIService apiService = ServiceGenerator.createService(APIService.class);
                Call<DependentLife> call = apiService.queryCPFDependent(new CPFInBody(dependentCPF));
                call.enqueue(new Callback<DependentLife>() {
                    @Override
                    public void onResponse(@NonNull Call<DependentLife> call, @NonNull Response<DependentLife> response) {
                        FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                        if (response.isSuccessful() && response.code() == 200) {
                            DependentLife mDependentLife =  response.body();
                            mDependentLifeSelected = mDependentLife;
                            CallDocuments(Dependent.createDependent(mDependentLife));
                        } else {
                            int resID = getResources().getIdentifier("MSG027", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            showSnackBar(message, TYPE_FAILURE);
                        }
                        setLoading(false,"");
                    }

                    @Override
                    public void onFailure(@NonNull Call<DependentLife> call, @NonNull Throwable t) {
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

        //CHECAR O RESULTADO DO ACEITE DO TERMO
        if (requestCode == TERMS_OF_USE_RESULT) {
            if (resultCode == RESULT_OK) {

                //CHECAR O TIPO DE PLANO DE ADESÃO
                if (idBenefit == Constants.BENEFITHEALTH) {
                    adhesionHealth(actualDependentCpf);
                } else if (idBenefit == Constants.BENEFITDENTAL) {
                    adhesionDental(actualDependentCpf);
                } else if (idBenefit == Constants.BENEFITPHARMA) {
                    adhesionPharmacy(actualDependentCpf);
                }

            } else {
                int resID = getResources().getIdentifier("MSG510", "string", getContext().getPackageName());
                String message = getResources().getString(resID);
                showSnackBar(message, Constants.TYPE_FAILURE);

                DependentListNotOnAdapter adapter = new DependentListNotOnAdapter(dependents.dependents);
                mDependentRecyclerView.setAdapter(adapter);
            }
        }
    }

    private void adhesionHealth(String cpf) {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<CommitResponse> call = mAPIService.adhesionHealthPlanDependent(new CPFInBody(cpf));
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        CommitResponse commitResponse = response.body();
                        if(commitResponse.commited) {
                            int resID = getResources().getIdentifier("MSG092", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            dialog  = Utils.showSimpleDialogReturnDialog(getString(R.string.dialog_title), message, getActivity(),finishClick);

                        }
                        else
                            try {
                                Utils.showSimpleDialog(getString(R.string.dialog_title), commitResponse.messageIdentifier, null, getActivity(), null);
                            } catch (Exception ex) {
                                showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
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
                public void onFailure(Call<CommitResponse> call, Throwable t) {
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

    private View.OnClickListener finishClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(dialog != null) {
                dialog.dismiss();
            }
            getDependentes();
        }
    };

    private void adhesionDental(String cpf){
        if (sessionManager.isLoggedIn()) {
            setLoading(true,getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<CommitResponse> call = mAPIService.adhesionDentalPlanDependent(new CPFInBody(cpf));
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        CommitResponse commitResponse = response.body();
                        if(commitResponse.commited) {
                            int resID = getResources().getIdentifier("MSG092", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            dialog  = Utils.showSimpleDialogReturnDialog(getString(R.string.dialog_title), message, getActivity(),finishClick);
                        }
                        else
                            try {
                                Utils.showSimpleDialog(getString(R.string.dialog_title), commitResponse.messageIdentifier, null, getActivity(), null);
                            } catch (Exception ex) {
                                showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                            }

                    } else {
                        setLoading(false,"");
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }

                    setLoading(false, "");
                }

                @Override
                public void onFailure(Call<CommitResponse> call, Throwable t) {
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

    private void adhesionPharmacy(String cpf){
        if (sessionManager.isLoggedIn()) {
            setLoading(true,getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<CommitResponse> call = mAPIService.adhesionPharmacyPlanDependent(new CPFInBody(cpf));
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        CommitResponse commitResponse = response.body();
                        if(commitResponse.commited) {
                            int resID = getResources().getIdentifier("MSG092", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            dialog  = Utils.showSimpleDialogReturnDialog(getString(R.string.dialog_title), message, getActivity(),finishClick);
                        }
                        else
                            try {
                                Utils.showSimpleDialog(getString(R.string.dialog_title), commitResponse.messageIdentifier, null, getActivity(), null);
                            } catch (Exception ex) {
                                showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                            }

                    } else {
                        setLoading(false,"");
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }

                    setLoading(false, "");
                }

                @Override
                public void onFailure(Call<CommitResponse> call, Throwable t) {
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

    private void queryDependentDetails(Dependent dependent) {

        setLoading(true, getString(R.string.search_dependent_detail));

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<DependentLife> call = apiService.queryCPFDependent(new CPFInBody(dependent.cpf));
        call.enqueue(new Callback<DependentLife>() {
            @Override
            public void onResponse(@NonNull Call<DependentLife> call, @NonNull Response<DependentLife> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful() && response.code() == 200) {
                    mDependentLifeSelected = response.body();

                    if (getListOfTerms.getCodes().size() >= 1) {

                        getListOfTerms.setCpf(dependent.cpf);
                        getListOfTerms.setPlatform(Constants.FEATURE_FLAG_PLATFORM);
                        showListOfTerms(getListOfTerms);

                    } else if (codeTerm != null) {

                        showTerm(dependent.cpf, codeTerm);

                    } else {

                        switch (idBenefit) {
                            case Constants.BENEFITHEALTH:
                                adhesionHealth(actualDependentCpf);
                                break;
                            case Constants.BENEFITDENTAL:
                                adhesionDental(actualDependentCpf);
                                break;
                            case Constants.BENEFITPHARMA:
                                adhesionPharmacy(actualDependentCpf);
                                break;
                        }
                    }
                } else {
                    int resID = getResources().getIdentifier("MSG027", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    showSnackBar(message, TYPE_FAILURE);
                }

                setLoading(false, "");

            }

            @Override
            public void onFailure(@NonNull Call<DependentLife> call, @NonNull Throwable t) {
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

    private void showTerm(String cpf, String codeTerm) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TERMS_OF_USE_SELECTED, codeTerm);
        bundle.putString(Constants.TERMS_OF_USE_CPF, cpf);
        Intent intent = new Intent(getContext(), TermsOfUseActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, TERMS_OF_USE_RESULT);
    }

    //Metido que verifica se ha politicas pendentes para o usuário aceitar
    public void showListOfTerms(GetListOfTerms getListOfTerms) {

        setLoading(true, "");

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        Call<JsonElement> call = mAPIService.getListOfTerms(getListOfTerms);
        call.enqueue(new Callback<JsonElement>() {
            @SuppressLint("LongLogTag")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    if (((JsonArray) response.body()).size() > 0) {

                        List<PoliciesAndTerm> termsList = new ArrayList<>();
                        for (int i = 0; i < ((JsonArray) response.body()).size(); i++) {
                            PoliciesAndTerm policiesAndTerm = new Gson().fromJson(((JsonArray) response.body()).get(i).toString(), PoliciesAndTerm.class);
                            if (policiesAndTerm.documentType == 1) {
                                termsList.add(policiesAndTerm);
                            }
                        }

                        Intent intent = new Intent(getContext(), CheckPendingTermsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("policiesAndTermList", (Serializable) termsList);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT);
                    }

                    setLoading(false, "");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                setLoading(false, "");
                if (t instanceof SocketTimeoutException)
                    Toast.makeText(getContext(), getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), Toast.LENGTH_LONG).show();
                else if (t instanceof UnknownHostException)
                    Toast.makeText(getContext(), getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dependent_adhesion_benefit_item,
                    parent, false);
            return new DependentViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DependentViewHolder holder, int position) {
            final Dependent dependent = mDependentList.get(position);
            holder.txtDependentName.setText(dependent.name);

            holder.btnAdhesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DependentLife life = new DependentLife();
                    view.setEnabled(false);
                    setLoading(true, getString(R.string.loading_searching));
                    life.populateDependent(dependent);
                    mDependentLifeSelected = life;
                    verifyCanRequestBenefit(dependent, view);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDependentList.size();
        }

        class DependentViewHolder extends RecyclerView.ViewHolder {
            private TextView txtDependentName;
            private Button btnAdhesion;

            DependentViewHolder(View itemView) {
                super(itemView);
                txtDependentName = itemView.findViewById(R.id.text_dependent_name);
                btnAdhesion = itemView.findViewById(R.id.btnInclude);
            }
        }
    }

    //METODO QUE DEFINE AS ESCOLHAS FEITAS EM TELA A PARTIR DA ESCOLHA DE COMPORTAMENTO
    private void setTermsAdhesionHealth(int behaviorId) {

        SystemBehavior.BehaviorEnum behavior = SystemBehavior.BehaviorEnum.fromId(behaviorId);
        getListOfTerms = new GetListOfTerms();
        String[] initList;

        switch (behavior) {
            case INCL_DEP_COMP_BOTH_MARRIGE:
                break;
            case INCL_DEP_COMP_DEP_MARRIGE:
                break;
            case INCL_DEP_COMP_HOLDER_MARRIGE:
                break;
            case INCL_DEP_COMP:
                break;
            case INCL_DEP_SON_ADOP_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_SON_ADOP_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_SON_ADOP_MAJOR:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                //break;
            case INCL_DEP_SON_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_SON_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_SON_MAJOR_WORK:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_SON_MAJOR:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_STU:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_SON_MAJOR_STU:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK_STU:
                //codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_SON_MAJOR_WORK_STU:
                break;
            case INCL_DEP_CUR_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_CUR_MAJOR_WORK:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_CUR_MAJOR:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_CUR_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MAJOR;
//                initList = new String[]{Constants.TERMS_OF_USE_CODE_HEALTH, Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MAJOR};
//                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_CUR_MAJOR_WORK_STU:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_GUARD_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_GUARD_MAJOR_WORK:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_GUARD_MAJOR:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_GUARD_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MAJOR;
//                initList = new String[]{Constants.TERMS_OF_USE_CODE_HEALTH, Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MAJOR};
//                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_GUARD_MAJOR_WORK_STU:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_ENT_SHARED_INVALID:
                initList = new String[]{Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR, TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_SHARED_MINOR:
                initList = new String[]{Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR, TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED;

//                initList = new String[]{Constants.TERMS_OF_USE_CODE_HEALTH, TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED};
//                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_SHARED_MAJOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED;

//                initList = new String[]{Constants.TERMS_OF_USE_CODE_HEALTH, TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED};
//                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_NO_SHARED_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_ENT_NO_SHARED_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_ENT_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_ENT_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_ENT_MAJOR_WORK:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_ENT_MAJOR:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_ENT_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MAJOR;

//                initList = new String[]{Constants.TERMS_OF_USE_CODE_HEALTH, Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MAJOR};
//                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_MAJOR_WORK_STU:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_STU:
                initList = new String[]{Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MAJOR, Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED};
                //initList = new String[]{Constants.TERMS_OF_USE_CODE_HEALTH, Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MAJOR, Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK_STU:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED;

//                initList = new String[]{Constants.TERMS_OF_USE_CODE_HEALTH, Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED};
//                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_STU:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK_STU:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_TUT_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_TUT_MAJOR_WORK:
                break;
            case INCL_DEP_TUT_MAJOR:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_TUT_MAJOR_WORK_STU:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_TUT_MAJOR_STU:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_FAHZ:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_SON_MAJOR_FAHZ:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_CUR_MAJOR_FAHZ:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_GUARD_MAJOR_FAHZ:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED;
//                initList = new String[]{Constants.TERMS_OF_USE_CODE_HEALTH, Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED};
//                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_FAHZ:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_ENT_MAJOR_FAHZ:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
            case INCL_DEP_TUT_MAJOR_FAHZ:
                //codeTerm = Constants.TERMS_OF_USE_CODE_HEALTH;
                break;
        }
    }

    //METODO QUE DEFINE AS ESCOLHAS FEITAS EM TELA A PARTIR DA ESCOLHA DE COMPORTAMENTO
    private void setTermsAdhesionDental(int behaviorId) {

        SystemBehavior.BehaviorEnum behavior = SystemBehavior.BehaviorEnum.fromId(behaviorId);
        getListOfTerms = new GetListOfTerms();
        String[] initList;

        switch (behavior) {
            case INCL_DEP_COMP_BOTH_MARRIGE:
                break;
            case INCL_DEP_COMP_DEP_MARRIGE:
                break;
            case INCL_DEP_COMP_HOLDER_MARRIGE:
                break;
            case INCL_DEP_COMP:
                break;
            case INCL_DEP_SON_ADOP_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR;
                break;
            case INCL_DEP_SON_ADOP_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_SON_ADOP_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_SON_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR;
                break;
            case INCL_DEP_SON_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR;
                break;
            case INCL_DEP_SON_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_SON_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_SON_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK_STU:
                //codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MAJOR;
                break;
            case INCL_DEP_SON_MAJOR_WORK_STU:
                //codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MAJOR;
                break;
            case INCL_DEP_CUR_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR;
                break;
            case INCL_DEP_CUR_MAJOR_WORK:
                //codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_CUR_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_CUR_MAJOR_STU:
                initList = new String[]{Constants.TERMS_OF_USE_CODE_DENTAL, Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MAJOR};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_CUR_MAJOR_WORK_STU:
                //codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_GUARD_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR;
                break;
            case INCL_DEP_GUARD_MAJOR_WORK:
                //codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_GUARD_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_GUARD_MAJOR_STU:
                initList = new String[]{Constants.TERMS_OF_USE_CODE_DENTAL, Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MAJOR};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_GUARD_MAJOR_WORK_STU:
                //codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_ENT_SHARED_INVALID:
                initList = new String[]{Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR, TERMS_OF_ACTIVATION_PLAN_ODONTO_SON_SHARED};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_SHARED_MINOR:
                initList = new String[]{Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR, TERMS_OF_ACTIVATION_PLAN_ODONTO_SON_SHARED};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK:
                initList = new String[]{Constants.TERMS_OF_USE_CODE_DENTAL, TERMS_OF_ACTIVATION_PLAN_ODONTO_SON_SHARED};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_SHARED_MAJOR:
                initList = new String[]{Constants.TERMS_OF_USE_CODE_DENTAL, TERMS_OF_ACTIVATION_PLAN_ODONTO_SON_SHARED};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_NO_SHARED_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR;
                break;
            case INCL_DEP_ENT_NO_SHARED_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_ENT_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR;
                break;
            case INCL_DEP_ENT_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR;
                break;
            case INCL_DEP_ENT_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_ENT_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_ENT_MAJOR_STU:
                initList = new String[]{Constants.TERMS_OF_USE_CODE_DENTAL, Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MAJOR};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_MAJOR_WORK_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_STU:
                initList = new String[]{Constants.TERMS_OF_USE_CODE_DENTAL, Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MAJOR, TERMS_OF_ACTIVATION_PLAN_ODONTO_SON_SHARED};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK_STU:
                initList = new String[]{Constants.TERMS_OF_USE_CODE_DENTAL, TERMS_OF_ACTIVATION_PLAN_ODONTO_SON_SHARED};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_TUT_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR;
                break;
            case INCL_DEP_TUT_MAJOR_WORK:
                break;
            case INCL_DEP_TUT_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_TUT_MAJOR_WORK_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_TUT_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_SON_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_CUR_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_GUARD_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_FAHZ:
                initList = new String[]{Constants.TERMS_OF_USE_CODE_DENTAL, TERMS_OF_ACTIVATION_PLAN_ODONTO_SON_SHARED};
                getListOfTerms.setCodes(Arrays.asList(initList));
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_ENT_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
            case INCL_DEP_TUT_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_DENTAL;
                break;
        }
    }

    //METODO QUE DEFINE AS ESCOLHAS FEITAS EM TELA A PARTIR DA ESCOLHA DE COMPORTAMENTO
    private void setTermsAdhesionPharmacy(int behaviorId) {

        SystemBehavior.BehaviorEnum behavior = SystemBehavior.BehaviorEnum.fromId(behaviorId);
        getListOfTerms = new GetListOfTerms();
        String[] initList;

        switch (behavior) {
            case INCL_DEP_COMP_BOTH_MARRIGE:
                break;
            case INCL_DEP_COMP_DEP_MARRIGE:
                break;
            case INCL_DEP_COMP_HOLDER_MARRIGE:
                break;
            case INCL_DEP_COMP:
                break;
            case INCL_DEP_SON_ADOP_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_SON_ADOP_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_SON_ADOP_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_SON_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_SON_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_SON_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_SON_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_SON_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK_STU:
                break;
            case INCL_DEP_SON_MAJOR_WORK_STU:
                break;
            case INCL_DEP_CUR_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_CUR_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_CUR_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_CUR_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_CUR_MAJOR_WORK_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_GUARD_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_GUARD_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_GUARD_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_GUARD_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_GUARD_MAJOR_WORK_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_SHARED_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_ENT_SHARED_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_NO_SHARED_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_ENT_NO_SHARED_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_INVALID:
                break;
            case INCL_DEP_ENT_MINOR:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_ENT_MAJOR_WORK:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_MAJOR_WORK_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_TUT_INVALID:
                codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR;
                break;
            case INCL_DEP_TUT_MAJOR_WORK:
                break;
            case INCL_DEP_TUT_MAJOR:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_TUT_MAJOR_WORK_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_TUT_MAJOR_STU:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_SON_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_CUR_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_GUARD_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_ENT_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
            case INCL_DEP_TUT_MAJOR_FAHZ:
                codeTerm = Constants.TERMS_OF_USE_CODE_PHARMACY;
                break;
        }
    }
}