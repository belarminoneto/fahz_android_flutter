package br.com.avanade.fahz.fragments.benefits.dentalplan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.BenefitsControlActivity;
import br.com.avanade.fahz.activities.benefits.dentalplan.AdhesionDentalControlActivity;
import br.com.avanade.fahz.activities.benefits.dentalplan.BaseDentalControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.dialogs.TermsOfOrientationActivity;
import br.com.avanade.fahz.dialogs.TermsOfUseActivity;
import br.com.avanade.fahz.model.Benefits;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.UserBenefits;
import br.com.avanade.fahz.model.benefits.CanInactivePlanBody;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class DentalFragment extends Fragment {
    @BindView(R.id.btnIncludePlan)
    ImageButton btnIncludePlan;
    @BindView(R.id.btnCard)
    ImageButton btnCard;
    @BindView(R.id.btnIncludeDep)
    ImageButton btnIncludeDep;
    @BindView(R.id.btnInactivateDep)
    ImageButton btnInactivateDep;
    @BindView(R.id.btnCancelPlan)
    ImageButton btnCancelPlan;
    @BindView(R.id.btnRequestCard)
    ImageButton btnRequestCard;

    @BindView(R.id.lblIncludePlan)
    TextView lblIncludePlan;
    @BindView(R.id.lblCard)
    TextView lblCard;
    @BindView(R.id.lblIncludeDep)
    TextView lblIncludeDep;
    @BindView(R.id.lblInactivateDep)
    TextView lblInactivateDep;
    @BindView(R.id.lblCancelPlan)
    TextView lblCancelPlan;
    @BindView(R.id.lblRequestCard)
    TextView lblRequestCard;

    @BindView(R.id.dental_container)
    NestedScrollView mDentalContainer;

    @BindView(R.id.secondLayoutDental)
    LinearLayout secondLayoutDental;

    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= null;

        if (!FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role))
            view = inflater.inflate(R.layout.fragment_dental, container, false);
        else
            view = inflater.inflate(R.layout.fragment_dental_dependent, container, false);

        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false);
        checkHasBenefit();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setLoading(Boolean loading){
        if(loading){
            mProgressDialog.setMessage("Aguarde um momento");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        else{
            mProgressDialog.dismiss();
        }
    }

    //VALIDA SE O USUARIO POSSUI BENEFICIOS DO TIPO ODONTO PARA CONFIGURAÇÃO DE BOTÕES
    private void checkHasBenefit()
    {
        String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<UserBenefits> call = mAPIService.queryUserBenefits(new CPFInBody(mCpf));
        call.enqueue(new Callback<UserBenefits>() {
            @Override
            public void onResponse(@NonNull Call<UserBenefits> call, @NonNull Response<UserBenefits> response) {
                if (response.isSuccessful()) {
                    UserBenefits benefits = response.body();
                    boolean userHasIt = false;
                    if(benefits != null && benefits.benefits!= null) {
                        for (Benefits benefit : benefits.benefits) {
                            if (benefit.benefit.id == Constants.BENEFITDENTAL && benefit.userHasIt)
                                userHasIt = true;

                            configureButtons(userHasIt);
                        }
                    }
                    else
                        configureButtons(false);

                } else{
                    ((BenefitsControlActivity) getActivity()).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<UserBenefits> call, Throwable t) {
                setLoading(false);
                if(getActivity()!= null) {
                    if (t instanceof SocketTimeoutException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else
                        ((BenefitsControlActivity) getActivity()).showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            }
        });
    }

    private void configureButtons(boolean userHasIt)
    {
        if (!FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
            if (userHasIt){
                if(getActivity()!= null && this.getResources() != null){
                    btnCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnIncludeDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnInactivateDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnCancelPlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnIncludePlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnRequestCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));

                    btnIncludePlan.setEnabled(false);
                    btnCard.setEnabled(true);
                    btnIncludeDep.setEnabled(true);
                    btnInactivateDep.setEnabled(true);
                    btnCancelPlan.setEnabled(true);
                    btnRequestCard.setEnabled(true);

                    lblIncludePlan.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblCard.setTextColor(this.getResources().getColor(R.color.black));
                    lblIncludeDep.setTextColor(this.getResources().getColor(R.color.black));
                    lblInactivateDep.setTextColor(this.getResources().getColor(R.color.black));
                    lblCancelPlan.setTextColor(this.getResources().getColor(R.color.black));
                    lblRequestCard.setTextColor(this.getResources().getColor(R.color.black));
                }
            } else {
                if(getActivity()!= null && this.getResources() != null) {
                    btnCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnIncludeDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnInactivateDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnCancelPlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnIncludePlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnRequestCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));

                    btnIncludePlan.setEnabled(true);
                    btnCard.setEnabled(false);
                    btnIncludeDep.setEnabled(false);
                    btnInactivateDep.setEnabled(false);
                    btnCancelPlan.setEnabled(false);
                    btnRequestCard.setEnabled(false);

                    lblIncludePlan.setTextColor(this.getResources().getColor(R.color.black));
                    lblCard.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblIncludeDep.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblInactivateDep.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblCancelPlan.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblRequestCard.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                }
            }
        }else {
            if (getActivity() != null) {

                btnIncludeDep.setVisibility(View.GONE);
                btnInactivateDep.setVisibility(View.GONE);
                btnCancelPlan.setVisibility(View.GONE);
                btnRequestCard.setVisibility(View.GONE);
                btnIncludePlan.setVisibility(View.GONE);

                lblIncludePlan.setVisibility(View.GONE);
                lblIncludeDep.setVisibility(View.GONE);
                lblInactivateDep.setVisibility(View.GONE);
                lblCancelPlan.setVisibility(View.GONE);
                lblRequestCard.setVisibility(View.GONE);

                if(getActivity()!= null && this.getResources() != null) {
                    if (userHasIt) {
                        btnCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                        lblCard.setTextColor(this.getResources().getColor(R.color.black));
                    } else {
                        btnCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                        lblCard.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    }
                }
            }
        }
    }

    //BOTÃO DE INCLUSÃO DE VIDA AO PLANO ODONTO
    public void openadhesionDental(View view) {
        if(FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.PENDING_STATUS) && !FahzApplication.getInstance().getFahzClaims().getPendingApproval()) {

            int resID = getResources().getIdentifier("MSG124", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            showSnackBarDismiss(message, TYPE_FAILURE,new Snackbar.Callback() {

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    Intent intent = null;
                    if (getContext() != null)
                        intent = new Intent(getContext(), AdhesionDentalControlActivity.class);
                    else
                        intent = new Intent(getActivity(), AdhesionDentalControlActivity.class);

                    intent.putExtra(Constants.ADHESION_DENTAL_CONTROL, AdhesionDentalControlActivity.AdhesionDentalFragment.PERSONALDATA);
                    intent.putExtra(Constants.ADHESION_HIDE_BANK, true);
                    startActivity(intent);
                }
            });
        } else {
            Intent intent = new Intent(getContext(), AdhesionDentalControlActivity.class);
            intent.putExtra(Constants.ADHESION_DENTAL_CONTROL, AdhesionDentalControlActivity.AdhesionDentalFragment.ADEHESION);
            startActivity(intent);
        }
    }

    private void openTermsOfUseActivity() {
        Intent intent = new Intent(getActivity(), TermsOfUseActivity.class);
        intent.putExtra(Constants.TERMS_OF_USE_SELECTED, Constants.TERMS_OF_USE_CODE_DENTAL);
        intent.putExtra(Constants.TERMS_OF_USE_FROM_BENEFIT, true);
        startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT);
    }

    @OnClick(R.id.btnIncludePlan)
    public void verifyCanRequestBenefit(View view)
    {
        //Verifica se pode desabilitar o beneficio
        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        Call<JsonElement> call = mAPIService.checkCanInactivePlan(new CanInactivePlanBody(FahzApplication.getInstance().getFahzClaims().getCPF(), Constants.BENEFITHEALTH, true));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        ((BenefitsControlActivity) getActivity()).showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        setLoading(false);

                    } else {
                        JsonElement value = response.body().getAsJsonObject().get("canrequest");

                        if (value.getAsBoolean()) {
                            openTermsOfUseActivity();
                        } else {
                            int resID = getResources().getIdentifier("MSG170", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            ((BenefitsControlActivity) getActivity()).showSnackBar(message,TYPE_FAILURE);
                        }
                    }
                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        ((BenefitsControlActivity) getActivity()).showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        ((BenefitsControlActivity) getActivity()).showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                setLoading(false);
                if(getActivity()!= null) {
                    if (t instanceof SocketTimeoutException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else
                        ((BenefitsControlActivity) getActivity()).showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.TERMS_OF_USE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                openadhesionDental(null);
            }
        }
    }

    //BOTÃO DE INCLUSÃO DE DEPENDENTE AO PLANO ODONTO
    @OnClick(R.id.btnIncludeDep)
    public void dependentAdehesionPlan(View view) {
        Intent intent = new Intent(getContext(), BaseDentalControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseDentalControlActivity.BaseDentalFragment.LISTDEPENDENT);
        startActivity(intent);
    }

    //BOTÃO DE CANCELAMENTO DE DEPENDENTE AO PLANO ODONTO
    @OnClick(R.id.btnInactivateDep)
    public void cancelDependentPlan(View view) {
        Intent intent = new Intent(getContext(), BaseDentalControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseDentalControlActivity.BaseDentalFragment.LISTDEPENDENTCANCEL);
        startActivity(intent);
    }

    //BOTÃO DE CANCELAMENTO DE PLANO DE SAUDE TITULAR
    @OnClick(R.id.btnCancelPlan)
    public void cancelHolderPlan(View view) {
        Intent intent = new Intent(getContext(), BaseDentalControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseDentalControlActivity.BaseDentalFragment.CANCELPLANHOLDER);
        startActivity(intent);
    }

    //BOTÃO DE CARTEIRINHAS
    @OnClick(R.id.btnCard)
    public void seeCard(View view) {
        Intent intent = new Intent(getContext(), BaseDentalControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseDentalControlActivity.BaseDentalFragment.PLANCARDS);
        startActivity(intent);
    }

    //BOTÃO DE EXTRATO DE USO
    @OnClick(R.id.btnExtractUse)
    public void ExtractOfUsage(View view) {
        Intent intent = new Intent(getContext(), BaseDentalControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseDentalControlActivity.BaseDentalFragment.EXTRACTOFUSAGE);
        startActivity(intent);
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mDentalContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    //BOTÃO SOLICITAR NOVA CARTEIRA
    @OnClick(R.id.btnRequestCard)
    public void RequestNewCard(View view) {
        Intent intent = new Intent(getActivity(), TermsOfOrientationActivity.class);
        intent.putExtra(Constants.TERMS_OF_USE_SELECTED, Constants.TERMO_ORIENTACAO_2VIA_CARTEIRA_ODONTO);
        startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT);
    }
}