package br.com.avanade.fahz.fragments.benefits;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.activities.benefits.dentalplan.BaseDentalControlActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.activities.benefits.pharmacy.BasePharmacyControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.dialogs.TermsOfUseActivity;
import br.com.avanade.fahz.model.CancelBenefit;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.benefits.CanInactivePlanBody;
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

import static br.com.avanade.fahz.util.Constants.TERMS_OF_USE_CODE_EXCLUSION_DENTAL;
import static br.com.avanade.fahz.util.Constants.TERMS_OF_USE_CODE_EXCLUSION_HEALTH;
import static br.com.avanade.fahz.util.Constants.TERMS_OF_USE_CODE_EXCLUSION_PHARMA;
import static br.com.avanade.fahz.util.Constants.TERM_ACCEPT;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class CancelBenefitHolderFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    @BindView(R.id.content_cancel_benefit)
    LinearLayout mContentCancelBenefit;
    @BindView(R.id.name_edit_text)
    TextView holderName;
    @BindView(R.id.benefit_edit_text)
    TextView benefitDescription;
    @BindView(R.id.text_title)
    TextView mTitleText;
    @BindView(R.id.explanation_text)
    TextView mExplanationText;

    SessionManager sessionManager;

    private ProgressDialog mProgressDialog;
    private int idBenefit= 0;
    private String benefitDescriptionValue= "";
    private Dialog dialog;
    private Dialog dialogInfo;

    private CancelBenefit cancelBenefit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cancel_benefit_holder, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false,"");
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idBenefit = bundle.getInt(Constants.BENEFIT_EXTRA, 0);
            benefitDescriptionValue = bundle.getString(Constants.BENEFIT_DESCRIPTION_EXTRA);
        }

        setupUi();

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
            ((BaseHealthControlActivity)getActivity()).toolbarTitle.setText(getActivity().getString(R.string.cancel_plan));
        else if(getActivity() instanceof BaseDentalControlActivity)
            ((BaseDentalControlActivity)getActivity()).toolbarTitle.setText(getActivity().getString(R.string.cancel_plan));
        else if(getActivity() instanceof BasePharmacyControlActivity)
            ((BasePharmacyControlActivity)getActivity()).toolbarTitle.setText(getActivity().getString(R.string.cancel_plan));

        switch (idBenefit) {
            case Constants.BENEFITHEALTH:
                mTitleText.setText(getString(R.string.title_health_cancel_holder));
                mExplanationText.setText(getString(R.string.cancel_plan_text));
                break;
            case Constants.BENEFITDENTAL:
                mTitleText.setText(getString(R.string.title_dental_cancel_holder));
                mExplanationText.setText(getString(R.string.cancel_plan_text_dental));
                break;
            case Constants.BENEFITPHARMA:
                mTitleText.setText(getString(R.string.title_pharmacy_cancel_holder));
                mExplanationText.setText(getString(R.string.cancel_plan_text_pharmacy));
                break;
        }
        holderName.setText(FahzApplication.getInstance().getFahzClaims().getName());
        benefitDescription.setText(benefitDescriptionValue);


    }

    //BOTÃO PARA CHAMAR CANCELAR PLANO
    @OnClick(R.id.btnCancelPlan)
    public void submit(View view) {

        //Verifica se pode desabilitar o beneficio
        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        Call<JsonElement> call = mAPIService.checkCanInactivePlan(new CanInactivePlanBody(FahzApplication.getInstance().getFahzClaims().getCPF(), idBenefit, true));
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
                            int resID = getResources().getIdentifier("MSG095", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);

                            dialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, getActivity(), onClickOk);
                        } else {
                            int resID = getResources().getIdentifier("MSG170", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            showSnackBar(message, TYPE_FAILURE);
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

    private View.OnClickListener onClickOk = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showInactivateDialog();
            if(dialog != null) {
                dialog.dismiss();
            }
        }
    };

    //MOSTRAR MODAL TIPOS DE DESLIGAMENTO
    public void showInactivateDialog() {

        dialogInfo = new Dialog(getActivity());
        dialogInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInfo.setContentView(R.layout.dialog_inactivate_benefit);

        TextView userName = dialogInfo.findViewById(R.id.user_name);
        userName.setText(FahzApplication.getInstance().getFahzClaims().getName());
        final EditText textReasonwhy = dialogInfo.findViewById(R.id.text_reason_why);


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

        //PEGAR INFORMAÇÂO DE DATA E RAZAO DESLIGAMENTO
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();
                if(!textReasonwhy.getText().toString().equals("")) {
                    cancelBenefit = new CancelBenefit();
                    cancelBenefit.setCpf(FahzApplication.getInstance().getFahzClaims().getCPF());
                    cancelBenefit.setDescribeReasonHolder(textReasonwhy.getText().toString());
                    cancelBenefit.setIdBenefit(idBenefit);

                    if (cancelBenefit.getIdBenefit() == Constants.BENEFITHEALTH)
                        if (MainActivity.showLgpd) {
                            showTerm(mCpf, TERMS_OF_USE_CODE_EXCLUSION_HEALTH);
                            //validateTermsOfUseCancelPlan(TERMS_OF_USE_CODE_EXCLUSION_HEALTH);
                        } else {
                            callInactivate();
                        }
                    else if (cancelBenefit.getIdBenefit() == Constants.BENEFITDENTAL)
                        if (MainActivity.showLgpd) {
                            showTerm(mCpf, TERMS_OF_USE_CODE_EXCLUSION_DENTAL);
                            //validateTermsOfUseCancelPlan(TERMS_OF_USE_CODE_EXCLUSION_DENTAL);
                        } else {
                            callInactivate();
                        }
                    else if (cancelBenefit.getIdBenefit() == Constants.BENEFITPHARMA)
                        if (MainActivity.showLgpd) {
                            showTerm(mCpf, TERMS_OF_USE_CODE_EXCLUSION_PHARMA);
                            //validateTermsOfUseCancelPlan(TERMS_OF_USE_CODE_EXCLUSION_DENTAL);
                        } else {
                            callInactivate();
                        }
                }
            }
        });
        dialogInfo.show();
    }

    public void callInactivate() {
        setLoading(true, getString(R.string.inactivating_benefit));
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<CommitResponse> call = apiService.inactivateBenefit(cancelBenefit);

        call.enqueue(new Callback<CommitResponse>() {
            @Override
            public void onResponse(Call<CommitResponse> call, Response<CommitResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    CommitResponse response2 = response.body();
                    if (response2.commited) {
                        dialogInfo.cancel();
                        showSnackBarDismiss(response2.messageIdentifier.replaceAll("\\\\n", "\n"), TYPE_SUCCESS, new Snackbar.Callback() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                        });
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
            public void onFailure(Call<CommitResponse> call, Throwable t) {
                if(getActivity()!=null) {
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
                setLoading(false, "");
            }
        });
    }


    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContentCancelBenefit, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContentCancelBenefit, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }


/*
    public void validateTermsOfUseCancelPlan(final String term) {

        setLoading(true, "");

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<Object> call = mAPIService.checkUserAcceppetedTerm(new TermCheckBody(term, FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                FahzApplication.getInstance().setToken(((Headers) response.raw().headers()).get("token"));
                if (response.isSuccessful()) {
                    setLoading(false, "");

                    if (response.body() instanceof LinkedTreeMap) {

                        if (((LinkedTreeMap) response.body()).containsKey("accepted")) {
                            boolean accepted = (boolean) ((LinkedTreeMap) response.body()).get("accepted");
                            if (!accepted) {
                                Intent intent = new Intent(getActivity(), TermsOfUseActivity.class);
                                intent.putExtra(Constants.TERMS_OF_USE_SELECTED, term);
                                intent.putExtra(Constants.TERMS_OF_USE_FROM_BENEFIT, true);
                                startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT);
                            } else {
                                callInactivate();
                            }
                        } else if (((LinkedTreeMap) response.body()).containsKey("messageIdentifier")) {
                            String value = (String) ((LinkedTreeMap) response.body()).get("messageIdentifier");
                            ((BenefitsControlActivity) getActivity()).showSnackBar(value, TYPE_FAILURE);
                        }
                    }
                } else {
                    setLoading(false, "");
                    ((BenefitsControlActivity) getActivity()).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                setLoading(false, "");
                if (getActivity() != null) {
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
*/


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TERM_ACCEPT) {
            if (resultCode == Activity.RESULT_OK) {
                callInactivate();
            }
        }
    }

    private void showTerm(String cpf, String codeTerm) {
        dialogInfo.cancel();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.TERMS_OF_USE_SELECTED, codeTerm);
        bundle.putString(Constants.TERMS_OF_USE_CPF, cpf);
        Intent intent = new Intent(getContext(), TermsOfUseActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, TERM_ACCEPT);
    }
}