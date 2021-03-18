package br.com.avanade.fahz.fragments.benefits.pharmacy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import br.com.avanade.fahz.activities.benefits.pharmacy.AdhesionPharmacyControlActivity;
import br.com.avanade.fahz.activities.benefits.pharmacy.BasePharmacyControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
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

public class PharmacyFragment extends Fragment {

    @BindView(R.id.btnIncludePlan)
    ImageButton btnIncludePlan;
//    @BindView(R.id.btnCard)
//    ImageButton btnCard;
    @BindView(R.id.btnIncludeDep)
    ImageButton btnIncludeDep;
    @BindView(R.id.btnInactivateDep)
    ImageButton btnInactivateDep;
    @BindView(R.id.btnCancelPlan)
    ImageButton btnCancelPlan;

    @BindView(R.id.lblIncludePlan)
    TextView lblIncludePlan;
//    @BindView(R.id.lblCard)
//    TextView lblCard;
    @BindView(R.id.lblIncludeDep)
    TextView lblIncludeDep;
    @BindView(R.id.lblInactivateDep)
    TextView lblInactivateDep;
    @BindView(R.id.lblCancelPlan)
    TextView lblCancelPlan;

    @BindView(R.id.pharmacy_container)
    NestedScrollView mPharmacyContainer;

    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pharmacy_no_card, container, false);
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

    //VALIDA SE O USUARIO POSSUI BENEFICIOS DO TIPO FARMACIA PARA CONFIGURAÇÃO DE BOTÕES
    private void checkHasBenefit()
    {
        setLoading(true);
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
                            if (benefit.benefit.id == Constants.BENEFITPHARMA && benefit.userHasIt)
                                userHasIt = true;

                            configureButtons(userHasIt);
                        }
                    }
                    else
                        configureButtons(false);
                } else{
                    ((BenefitsControlActivity) getActivity()).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
                setLoading(false);
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
            if (userHasIt) {
                //btnCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                btnIncludeDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                btnInactivateDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                btnCancelPlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                btnIncludePlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));

                btnIncludePlan.setEnabled(false);
                //btnCard.setEnabled(true);
                btnIncludeDep.setEnabled(true);
                btnInactivateDep.setEnabled(true);
                btnCancelPlan.setEnabled(true);

                lblIncludePlan.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                //lblCard.setTextColor(this.getResources().getColor(R.color.black));
                lblIncludeDep.setTextColor(this.getResources().getColor(R.color.black));
                lblInactivateDep.setTextColor(this.getResources().getColor(R.color.black));
                lblCancelPlan.setTextColor(this.getResources().getColor(R.color.black));

            } else {
                //btnCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                btnIncludeDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                btnInactivateDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                btnCancelPlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                btnIncludePlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));

                btnIncludePlan.setEnabled(true);
                //btnCard.setEnabled(false);
                btnIncludeDep.setEnabled(false);
                btnInactivateDep.setEnabled(false);
                btnCancelPlan.setEnabled(false);

                lblIncludePlan.setTextColor(this.getResources().getColor(R.color.black));
                //lblCard.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                lblIncludeDep.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                lblInactivateDep.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                lblCancelPlan.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
            }
        }
    }


    //BOTÃO DE INCLUSÃO DE VIDA AO PLANO FARMACIA
    public void openadhesionPharmacy(View view) {
        if(FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.PENDING_STATUS) && !FahzApplication.getInstance().getFahzClaims().getPendingApproval()) {

            int resID = getResources().getIdentifier("MSG124", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            showSnackBarDismiss(message, TYPE_FAILURE,new Snackbar.Callback() {

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    Intent intent = new Intent(getContext(), AdhesionPharmacyControlActivity.class);
                    intent.putExtra(Constants.ADHESION_PHARMACY_CONTROL, AdhesionPharmacyControlActivity.AdhesionPharmazyFragment.PERSONALDATA);
                    intent.putExtra(Constants.ADHESION_HIDE_BANK, true);
                    startActivity(intent);
                }
            });
        } else {
            Intent intent = new Intent(getContext(), AdhesionPharmacyControlActivity.class);
            intent.putExtra(Constants.ADHESION_PHARMACY_CONTROL, AdhesionPharmacyControlActivity.AdhesionPharmazyFragment.ADEHESION);
            startActivity(intent);
        }
    }


    public void openTermsOfUseActivity() {
        Intent intent = new Intent(getActivity(), TermsOfUseActivity.class);
        intent.putExtra(Constants.TERMS_OF_USE_SELECTED, Constants.TERMS_OF_USE_CODE_PHARMACY);
        intent.putExtra(Constants.TERMS_OF_USE_FROM_BENEFIT, true);
        startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT);
    }

/*
    //MÈTODO QUE VERIFICA SE O USUARIO JÀ ACEITOU O TERMO DE USO PADRÃO
    //BOTÃO DE INCLUSÃO DE VIDA AO PLANO DE SAÚDE
    public void validateTermOfUse() {

        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<Object> call = mAPIService.checkUserAcceppetedTerm(new TermCheckBody(Constants.TERMS_OF_USE_CODE_PHARMACY, FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    setLoading(false);

                    if (response.body() instanceof LinkedTreeMap) {

                        if (((LinkedTreeMap) response.body()).containsKey("accepted")) {
                            boolean accepted = (boolean) ((LinkedTreeMap) response.body()).get("accepted");
                            if (!accepted) {
                                Intent intent = new Intent(getActivity(), TermsOfUseActivity.class);
                                intent.putExtra(Constants.TERMS_OF_USE_SELECTED, Constants.TERMS_OF_USE_CODE_PHARMACY);
                                intent.putExtra(Constants.TERMS_OF_USE_FROM_BENEFIT, true);
                                startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT);
                            } else {
                                openadhesionPharmacy(null);
                            }
                        } else if (((LinkedTreeMap) response.body()).containsKey("messageIdentifier")) {
                            String value = (String) ((LinkedTreeMap) response.body()).get("messageIdentifier");
                            ((BenefitsControlActivity) getActivity()).showSnackBar(value, TYPE_FAILURE);
                        }
                    }
                } else {
                    setLoading(false);
                    ((BenefitsControlActivity) getActivity()).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
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
    }*/

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
                            //validateTermOfUse();
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
                openadhesionPharmacy(null);
                //validateTermOfUse();
            }
        }
    }

    //BOTÃO DE INCLUSÃO DE DEPENDENTE AO PLANO FARMACIA
    @OnClick(R.id.btnIncludeDep)
    public void dependentAdehesionPlan(View view) {
        Intent intent = new Intent(getContext(), BasePharmacyControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BasePharmacyControlActivity.BasePharmacyFragment.LISTDEPENDENT);
        startActivity(intent);

    }

    //BOTÃO DE CANCELAMENTO DE DEPENDENTE AO PLANO FARMACIA
    @OnClick(R.id.btnInactivateDep)
    public void cancelDependentPlan(View view) {
        Intent intent = new Intent(getContext(), BasePharmacyControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BasePharmacyControlActivity.BasePharmacyFragment.LISTDEPENDENTCANCEL);
        startActivity(intent);
    }

    //BOTÃO DE CANCELAMENTO DE PLANO DE FARMACIA TITULAR
    @OnClick(R.id.btnCancelPlan)
    public void cancelHolderPlan(View view) {
        Intent intent = new Intent(getContext(), BasePharmacyControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BasePharmacyControlActivity.BasePharmacyFragment.CANCELPLANHOLDER);
        startActivity(intent);
    }
//    //BOTÃO DE CARTEIRINHAS
//    @OnClick(R.id.btnCard)
//    public void seeCard(View view) {
//        Intent intent = new Intent(getContext(), BasePharmacyControlActivity.class);
//        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BasePharmacyControlActivity.BasePharmacyFragment.PLANCARDS);
//        startActivity(intent);
//    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mPharmacyContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

}