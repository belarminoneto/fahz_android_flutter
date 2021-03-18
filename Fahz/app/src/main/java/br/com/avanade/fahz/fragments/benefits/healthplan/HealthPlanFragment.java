package br.com.avanade.fahz.fragments.benefits.healthplan;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.DependentActivity;
import br.com.avanade.fahz.activities.ProfileActivity;
import br.com.avanade.fahz.activities.anamnesis.FamilyTreeAnamnesisActivity;
import br.com.avanade.fahz.activities.anamnesis.ViewAnswersActivity;
import br.com.avanade.fahz.activities.benefits.BenefitsControlActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.AdhesionHealthControlActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.dialogs.TermsOfOrientationActivity;
import br.com.avanade.fahz.model.Benefits;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.ExternalAccess;
import br.com.avanade.fahz.model.UrlTeleHealthRequest;
import br.com.avanade.fahz.model.UrlTeleHealthResponse;
import br.com.avanade.fahz.model.UserBenefits;
import br.com.avanade.fahz.model.benefits.CanInactivePlanBody;
import br.com.avanade.fahz.model.externalaccess.ExternalAccessUrlBody;
import br.com.avanade.fahz.model.response.FamilyGroupItemResponse;
import br.com.avanade.fahz.model.response.FamilyGroupListResponse;
import br.com.avanade.fahz.model.response.OperatorResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;
import static br.com.avanade.fahz.util.Constants.BENEFITHEALTH;
import static br.com.avanade.fahz.util.Constants.CPF_EDIT_DEPENDENT;
import static br.com.avanade.fahz.util.Constants.REQUEST_CAMERA_PERMISSION;
import static br.com.avanade.fahz.util.Constants.REQUEST_MODIFY_AUDIO_SETTINGS_PERMISSION;
import static br.com.avanade.fahz.util.Constants.REQUEST_RECORD_AUDIO_PERMISSION;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;
import static br.com.avanade.fahz.util.Constants.VIEW_EDIT_DEPENDENT;


public class HealthPlanFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private List<FamilyGroupItemResponse> beneficiaryList;
    private FamilyGroupItemResponse selectedBeneficiary;
    private Dialog confirmationDialog;
    Dialog dialog;

    @BindView(R.id.btnIncludePlan)
    ImageButton btnIncludePlan;
    @BindView(R.id.btnCard)
    ImageButton btnCard;
    @BindView(R.id.btnIncludeDep)
    ImageButton btnIncludeDep;
    @BindView(R.id.btnInactivateDep)
    ImageButton btnInactivateDep;
    @BindView(R.id.btnAlterOperator)
    ImageButton btnAlterOperator;
    @BindView(R.id.btnCancelPlan)
    ImageButton btnCancelPlan;
    @BindView(R.id.btnRequestCard)
    ImageButton btnRequestCard;
    @BindView(R.id.btnExtractUse)
    ImageButton btnExtractUse;
    @BindView(R.id.btnSchedule)
    ImageButton btnSchedule;
    @BindView(R.id.btnTeleHealth)
    ImageButton btnTeleHealth;
    @BindView(R.id.btnPregnantProgram)
    ImageButton btnPregnantProgram;
    @BindView(R.id.btnAnamnesis)
    ImageButton btnAnamnesis;
    @BindView(R.id.btnMedicalRecord)
    ImageButton btnMedicalRecord;

    @BindView(R.id.lblIncludePlan)
    TextView lblIncludePlan;
    @BindView(R.id.lblCard)
    TextView lblCard;
    @BindView(R.id.lblIncludeDep)
    TextView lblIncludeDep;
    @BindView(R.id.lblInactivateDep)
    TextView lblInactivateDep;
    @BindView(R.id.lblAlterOperator)
    TextView lblAlterOperator;
    @BindView(R.id.lblCancelPlan)
    TextView lblCancelPlan;
    @BindView(R.id.lblRequestCard)
    TextView lblRequestCard;
    @BindView(R.id.lblExtractUse)
    TextView lblExtractUse;
    @BindView(R.id.lblSchedule)
    TextView lblSchedule;
    @BindView(R.id.lblTeleHealth)
    TextView lblTeleHealth;
    @BindView(R.id.lblMedicalRecord)
    TextView lblMedicalRecord;

    @BindView(R.id.lblPregnant)
    TextView lblPregnant;
    @BindView(R.id.lblQtdNotification)
    TextView lblQtdNotification;
    @BindView(R.id.lblAnamnesis)
    TextView lblAnamnesis;

    boolean userIsPregnant = false;

    @BindView(R.id.health_plan_container)
    NestedScrollView mHealthPlanContainer;

    private ProgressDialog mProgressDialog;
    private ExternalAccess mExternalAccess;
    private static final String DIALOG_FRAGMENT = "dialog";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_plan, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());

        configureAnamnesisButton();
        checkHasBenefit();
        loadFamilyGroup();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setLoading(Boolean loading) {
        if (loading) {
            mProgressDialog.setMessage("Aguarde um momento");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    public void setLoading(boolean loading, String text) {
        if (loading) {
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else
            mProgressDialog.dismiss();
    }

    //VALIDA SE O USUARIO POSSUI BENEFICIOS DO TIPO SAUDE PARA CONFIGURAÇÃO DE BOTÕES
    private void checkHasBenefit() {
        setLoading(true);
        String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<UserBenefits> call = mAPIService.queryUserBenefits(new CPFInBody(mCpf));
        call.enqueue(new Callback<UserBenefits>() {
            @Override
            public void onResponse(@NonNull Call<UserBenefits> call, @NonNull Response<UserBenefits> response) {
                try {
                    if (response.isSuccessful() && response.body().benefits != null) {
                        UserBenefits benefits = response.body();
                        boolean userHasIt = false;
                        boolean userCanSchedule = false;
                        boolean userCanSeePregnantProgram = false;
                        boolean userCanSeeHealthProfile = false;
                        boolean userCanChangeBenefit = false;
                        boolean userCanSeeTeleHealth = false;
                        boolean userCanSeeMedicalRecord = false;

                        userIsPregnant = false;

                        if (benefits != null && benefits.benefits != null) {
                            for (Benefits benefit : benefits.benefits) {

                                if (benefit.benefit.id == BENEFITHEALTH && benefit.userHasIt)
                                    userHasIt = true;
                                if (benefit.benefit.id == BENEFITHEALTH && benefit.userCanSchedule)
                                    userCanSchedule = true;
                                if (benefit.benefit.id == BENEFITHEALTH && benefit.userCanSeePregnantProgram)
                                    userCanSeePregnantProgram = true;

                                if (benefit.benefit.id == BENEFITHEALTH && benefit.userIsPregnant)
                                    userIsPregnant = true;
                                if (benefit.benefit.id == BENEFITHEALTH && benefit.userCanSeeHealthProfile) {
                                    userCanSeeHealthProfile = true;
                                    //userCanSeeMedicalRecord = true;
                                }
                                if (benefit.benefit.id == BENEFITHEALTH && benefit.userCanChangeBenefit)
                                    userCanChangeBenefit = true;
                                if (benefit.benefit.id == BENEFITHEALTH && benefit.userCanMakeAttendance)
                                    userCanSeeTeleHealth = true;
                            }

                            configureButtons(userHasIt, userCanSchedule, userCanSeePregnantProgram, userCanSeeHealthProfile, userCanChangeBenefit, userCanSeeTeleHealth, userIsPregnant, userCanSeeMedicalRecord);
                        }
                        else
                            configureButtons(false, false, false, false, false, false, false, false);
                    } else {
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.error("HealthPlanFragment", e);
                }
                setLoading(false);
            }

            @Override
            public void onFailure(Call<UserBenefits> call, Throwable t) {
                setLoading(false);
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



    private void configureAnamnesisButton() {
        lblQtdNotification.setVisibility(userAnamnesisSession.getPendencies() > 0 ? View.VISIBLE : View.INVISIBLE);
        lblQtdNotification.setText(String.valueOf(userAnamnesisSession.getPendencies()));
    }


    private void configureButtons(boolean userHasIt, boolean userCanSchedule, boolean userCanSeePregnantProgram, boolean userCanSeeHealthProfile, boolean userCanChangeBenefit, boolean userCanSeeTeleHealth, boolean userIsPregnant, boolean userCanSeeMedicalRecord) {
        if (!FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
            if (getActivity() != null) {
                if (userHasIt) {
                    btnCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnIncludeDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnInactivateDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnAlterOperator.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnCancelPlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnRequestCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnIncludePlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnIncludePlan.setEnabled(false);

                    lblIncludePlan.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblCard.setTextColor(this.getResources().getColor(R.color.black));
                    lblIncludeDep.setTextColor(this.getResources().getColor(R.color.black));
                    lblInactivateDep.setTextColor(this.getResources().getColor(R.color.black));
                    lblAlterOperator.setTextColor(this.getResources().getColor(R.color.black));
                    lblCancelPlan.setTextColor(this.getResources().getColor(R.color.black));
                    lblRequestCard.setTextColor(this.getResources().getColor(R.color.black));

                    if (userCanSchedule) {
                        btnSchedule.setEnabled(true);
                        lblSchedule.setTextColor(this.getResources().getColor(R.color.black));
                        btnSchedule.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    } else {
                        btnSchedule.setEnabled(false);
                        btnSchedule.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                        lblSchedule.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    }

                    if (userCanSeeHealthProfile) {
                        btnAnamnesis.setEnabled(true);
                        lblAnamnesis.setTextColor(this.getResources().getColor(R.color.black));
                        btnAnamnesis.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    } else {
                        btnAnamnesis.setEnabled(false);
                        btnAnamnesis.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                        lblAnamnesis.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    }

                    if (userCanSeePregnantProgram) {

                        GetURL();

                        btnPregnantProgram.setVisibility(View.VISIBLE);
                        lblPregnant.setVisibility(View.VISIBLE);
                    } else {
                        btnPregnantProgram.setVisibility(View.GONE);
                        lblPregnant.setVisibility(View.GONE);
                    }

                    if (userCanSeeTeleHealth) {
                        btnTeleHealth.setVisibility(View.VISIBLE);
                        lblTeleHealth.setVisibility(View.VISIBLE);
                    } else {
                        btnTeleHealth.setVisibility(View.GONE);
                        lblTeleHealth.setVisibility(View.GONE);
                    }


                    btnMedicalRecord.setVisibility(View.GONE);
                    lblMedicalRecord.setVisibility(View.GONE);


                } else {
                    btnCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnIncludeDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnInactivateDep.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnAlterOperator.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnCancelPlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnRequestCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnSchedule.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnIncludePlan.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    btnAnamnesis.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));

                    btnCard.setEnabled(false);
                    btnIncludeDep.setEnabled(false);
                    btnInactivateDep.setEnabled(false);
                    btnAlterOperator.setEnabled(false);
                    btnCancelPlan.setEnabled(false);
                    btnRequestCard.setEnabled(false);
                    btnSchedule.setEnabled(false);
                    btnAnamnesis.setEnabled(false);

                    lblIncludePlan.setTextColor(this.getResources().getColor(R.color.black));
                    lblCard.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblIncludeDep.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblInactivateDep.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblAlterOperator.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblCancelPlan.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblRequestCard.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblSchedule.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblAnamnesis.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));

                    btnTeleHealth.setVisibility(View.GONE);
                    lblTeleHealth.setVisibility(View.GONE);

                    btnMedicalRecord.setVisibility(View.GONE);
                    lblMedicalRecord.setVisibility(View.GONE);
                }

                if (!userCanChangeBenefit) {
                    btnAlterOperator.setEnabled(false);
                    btnAlterOperator.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    lblAlterOperator.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                }
            }
        } else {
            if (getActivity() != null) {
                btnIncludeDep.setVisibility(View.GONE);
                btnInactivateDep.setVisibility(View.GONE);
                btnAlterOperator.setVisibility(View.GONE);
                btnCancelPlan.setVisibility(View.GONE);
                btnRequestCard.setVisibility(View.GONE);
                btnIncludePlan.setVisibility(View.GONE);

                lblIncludePlan.setVisibility(View.GONE);
                lblIncludeDep.setVisibility(View.GONE);
                lblInactivateDep.setVisibility(View.GONE);
                lblAlterOperator.setVisibility(View.GONE);
                lblCancelPlan.setVisibility(View.GONE);
                lblRequestCard.setVisibility(View.GONE);

                if (userCanSeePregnantProgram){
//                    GetURL();
                    btnPregnantProgram.setVisibility(View.VISIBLE);
                    lblPregnant.setVisibility(View.VISIBLE);
                }

                if (userHasIt) {
                    btnCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    lblCard.setTextColor(this.getResources().getColor(R.color.black));

                    if (userCanSeeHealthProfile) {
                        btnAnamnesis.setEnabled(true);
                        lblAnamnesis.setTextColor(this.getResources().getColor(R.color.black));
                        btnAnamnesis.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                    } else {
                        btnAnamnesis.setEnabled(false);
                        btnAnamnesis.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                        lblAnamnesis.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    }

                    if (userCanSchedule) {
                        btnSchedule.setEnabled(true);
                        btnSchedule.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
                        lblSchedule.setTextColor(this.getResources().getColor(R.color.black));
                    } else {
                        btnSchedule.setEnabled(true);
                        btnSchedule.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                        lblSchedule.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    }

                    if (userCanSeeTeleHealth) {
                        btnTeleHealth.setVisibility(View.VISIBLE);
                        lblTeleHealth.setVisibility(View.VISIBLE);
                    } else {
                        btnTeleHealth.setVisibility(View.GONE);
                        lblTeleHealth.setVisibility(View.GONE);
                    }

                } else {
                    btnCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnRequestCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnSchedule.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
                    btnAnamnesis.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));

                    btnCard.setEnabled(false);
                    btnSchedule.setEnabled(false);
                    btnAnamnesis.setEnabled(false);

                    lblCard.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblSchedule.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
                    lblAnamnesis.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));

                    btnTeleHealth.setVisibility(View.GONE);
                    lblTeleHealth.setVisibility(View.GONE);

                    btnMedicalRecord.setVisibility(View.GONE);
                    lblMedicalRecord.setVisibility(View.GONE);
                }
            }
        }
    }

    public void openadhesionHealth(View view) {
        if (FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.PENDING_STATUS) && !FahzApplication.getInstance().getFahzClaims().getPendingApproval()) {

            if (getActivity() != null) {
                int resID = getResources().getIdentifier("MSG124", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                showSnackBarDismiss(message, TYPE_FAILURE, new Snackbar.Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (getContext() != null) {
                            Intent intent = new Intent(getContext(), AdhesionHealthControlActivity.class);
                            intent.putExtra(Constants.ADHESION_HEALTH_CONTROL, AdhesionHealthControlActivity.AdhesionHealthFragment.PERSONALDATA);
                            intent.putExtra(Constants.ADHESION_HIDE_BANK, true);
                            startActivity(intent);
                        }
                    }
                });
            }
        } else {
            Intent intent = new Intent(getContext(), AdhesionHealthControlActivity.class);
            intent.putExtra(Constants.ADHESION_HEALTH_CONTROL, AdhesionHealthControlActivity.AdhesionHealthFragment.ADEHESION);
            startActivity(intent);
        }
    }


    @OnClick(R.id.btnIncludePlan)
    public void verifyCanRequestBenefit(View view) {
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
                            openadhesionHealth(null);
                            //validateTermOfUse();
                        } else {
                            int resID = getResources().getIdentifier("MSG170", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            ((BenefitsControlActivity) getActivity()).showSnackBar(message, TYPE_FAILURE);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.TERMS_OF_USE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                openadhesionHealth(null);
                //validateTermOfUse();
            }
        }
    }

    //BOTÃO DE INCLUSÃO DE DEPENDENTE AO PLANO DE SAUR
    @OnClick(R.id.btnIncludeDep)
    public void dependentAdehesionPlan(View view) {
        Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.LISTDEPENDENT);
        startActivity(intent);
    }

    //BOTÃO DE CANCELAMENTO DE PLANO DE SAUDE TITULAR
    @OnClick(R.id.btnCancelPlan)
    public void cancelHolderPlan(View view) {
        Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.CANCELPLANHOLDER);
        startActivity(intent);
    }

    //BOTÃO DE AGENDAMENTO
    @OnClick(R.id.btnSchedule)
    public void openScheduleView(View view) {
        Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.MYSCHEDULE);
        startActivity(intent);
    }


    //BOTÃO DE CANCELAMENTO DE DEPENDENTE AO PLANO DE SAUDe
    @OnClick(R.id.btnInactivateDep)
    public void cancelDependentPlan(View view) {
        Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.LISTDEPENDENTCANCEL);
        startActivity(intent);
    }

    //BOTÃO DE CARTEIRINHAS
    @OnClick(R.id.btnCard)
    public void seeCard(View view) {
        Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.PLANCARDS);
        startActivity(intent);
    }


    //BOTÃO DE PROGRAMA GESTANTE
    @OnClick(R.id.btnPregnantProgram)
    public void CallPregnantProgram(View view) {

        if(!userIsPregnant) {

            if (mExternalAccess != null) {
                String url = mExternalAccess.URL;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
        } else {
                ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
            }

        }else{
            Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
            intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.PROGRAMPREGNANT);
            startActivity(intent);
        }
    }



    //BOTÃO DE CARTEIRINHAS
    @OnClick(R.id.btnAlterOperator)
    public void AlterOperator(View view) {
        Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.CHANGEOPERATOR);
        startActivity(intent);
    }

    //BOTÃO DE EXTRATO DE USO
    @OnClick(R.id.btnExtractUse)
    public void ExtractOfUsage(View view) {
        Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.EXTRACTOFUSAGE);
        startActivity(intent);
    }

    @OnClick(R.id.btnAnamnesis)
    public void startAnamnesis(View view) {
        //Intent intent = new Intent(getContext(), LoginDevActivity.class);
        Intent intent = new Intent(getContext(), FamilyTreeAnamnesisActivity.class);
        startActivity(intent);
    }

    //BOTÃO DE CANCELAMENTO DE PLANO DE SAUDE TITULAR
    @OnClick(R.id.btnMedicalRecord)
    public void seeMedicalRecord(View view) {
        Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.MEDICALRECORD);
        startActivity(intent);
    }


    //BOTÃO SOLICITAR NOVA CARTEIRA
    @OnClick(R.id.btnRequestCard)
    public void RequestNewCard(View view) {
        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<OperatorResponse> callOperator = mAPIService.getCurrentOperator(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
        callOperator.enqueue(new Callback<OperatorResponse>() {
            @Override
            public void onResponse(@NonNull Call<OperatorResponse> callOperator, @NonNull Response<OperatorResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    OperatorResponse operatorResponse = response.body();
                    if(operatorResponse.getCommited()) {
                        if (operatorResponse.getOperator().getId().equals(Constants.CNU)) {
                            Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
                            intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.REQUESTNEWCARD);
                            startActivity(intent);
                        } else if (operatorResponse.getOperator().getId().equals(Constants.SUL)) {
                            Intent intent = new Intent(getActivity(), TermsOfOrientationActivity.class);
                            intent.putExtra(Constants.TERMS_OF_USE_SELECTED, Constants.TERMO_ORIENTACAO_2VIA_CARTEIRA_SUL);
                            startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT);
                        } else if (operatorResponse.getOperator().getId().equals(Constants.BRADESCO)) {
                            Intent intent = new Intent(getActivity(), TermsOfOrientationActivity.class);
                            intent.putExtra(Constants.TERMS_OF_USE_SELECTED, Constants.TERMO_ORIENTACAO_2VIA_CARTEIRA_BRA);
                            startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT);
                        }
                    }else {
                        ((BenefitsControlActivity) getActivity()).showSnackBar(operatorResponse.getMessageIdentifier(), TYPE_FAILURE);
                    }
                    setLoading(false);
                } else {
                    setLoading(false);
                    ((BenefitsControlActivity) getActivity()).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<OperatorResponse> call, Throwable t) {
                setLoading(false);
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


    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mHealthPlanContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    //BOTÃO DE TELEMEDICINA
    @OnClick(R.id.btnTeleHealth)
    public void callTeleHealth(View view) {

        Context context = FahzApplication.getAppContext();
        if (!isDeviceSupportCamera()) {
            Toast.makeText(context, getString(R.string.device_not_support_camera), Toast.LENGTH_LONG).show();
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionType(Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION);
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionType(Manifest.permission.RECORD_AUDIO, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            callModalTeleHealth();
        }
    }

    public void callModalTeleHealth() {

        //CHECAR QTD DE VIDAS PARA ACIONAR O ABRIR DIALOG COM VIDAS
        if (beneficiaryList.size() == 0 || beneficiaryList.size() == 1) {

            String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();
            boolean mHolder = FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role);

            UrlTeleHealthRequest UrlTeleHealthRequest = new UrlTeleHealthRequest();
            UrlTeleHealthRequest.setCpf(mCpf);
            UrlTeleHealthRequest.setHolder(!mHolder);

            getUrlTeleHealth(UrlTeleHealthRequest);

        } else {

            callDialogChooseBeneficiary();
        }
    }

    public void callDialogChooseBeneficiary() {

        confirmationDialog = new Dialog(getContext());
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmationDialog.setContentView(R.layout.dialog_telehealth_choose_beneficiary);
        confirmationDialog.setCanceledOnTouchOutside(false);

        final Button confirmButton = confirmationDialog.findViewById(R.id.bt_confirm);
        Button cancelButton = confirmationDialog.findViewById(R.id.bt_cancel);

        Spinner spBeneficiaryList = confirmationDialog.findViewById(R.id.sp_select_beneficiary);

        final List<String> listOfNames = new ArrayList<>();
        for (FamilyGroupItemResponse item : beneficiaryList) {
            listOfNames.add(item.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_layout, listOfNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBeneficiaryList.setAdapter(adapter);
        spBeneficiaryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBeneficiary = beneficiaryList.get(position);
                confirmButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedBeneficiary = null;
                confirmButton.setEnabled(false);
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.cancel();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.dismiss();

                if (selectedBeneficiary != null) {

                    UrlTeleHealthRequest UrlTeleHealthRequest = new UrlTeleHealthRequest();
                    UrlTeleHealthRequest.setCpf(selectedBeneficiary.getCpf());
                    UrlTeleHealthRequest.setHolder(selectedBeneficiary.isHolder());

                    getUrlTeleHealth(UrlTeleHealthRequest);

                } else {
                    ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                }
            }
        });

        confirmationDialog.show();
    }


    private void loadFamilyGroup() {

        setLoading(true, getContext().getString(R.string.loading_data));

        String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<FamilyGroupListResponse> call = mAPIService.getFamilyGroup(new CPFInBody(mCpf));
        call.enqueue(new Callback<FamilyGroupListResponse>() {
            @Override
            public void onResponse(@NonNull Call<FamilyGroupListResponse> call, @NonNull Response<FamilyGroupListResponse> response) {

                selectedBeneficiary = null;

                setLoading(false, "");

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    beneficiaryList = response.body().getLives();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FamilyGroupListResponse> call, @NonNull Throwable t) {

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


    private void getUrlTeleHealth(final UrlTeleHealthRequest UrlTeleHealthRequest) {

        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class);

        Call<JsonElement> call = mAPIService.CreateDriving(UrlTeleHealthRequest);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                if (response.isSuccessful()) {
                    final UrlTeleHealthResponse urlTeleHealthResponse = new Gson().fromJson((response.body().getAsJsonObject()), UrlTeleHealthResponse.class);

                    if (!urlTeleHealthResponse.commit) {

                        showSnackBarDismiss(urlTeleHealthResponse.messageIdentifier, TYPE_FAILURE, new Snackbar.Callback() {

                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                if (getContext() != null) {

                                    String mCPF = FahzApplication.getInstance().getFahzClaims().getCPF();
                                    if (urlTeleHealthResponse.redirectAlterEmail) {
                                        if (FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {

                                            Intent intent = new Intent(getContext(), DependentActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString(CPF_EDIT_DEPENDENT, mCPF);
                                            bundle.putBoolean(VIEW_EDIT_DEPENDENT, true);
                                            bundle.putBoolean(Constants.IS_REACTIVATE, false);
                                            intent.putExtras(bundle);
                                            startActivity(intent);

                                        } else {

                                            Intent intent = new Intent(getContext(), ProfileActivity.class);
                                            startActivity(intent);

                                        }
                                    }
                                }
                            }
                        });


                        setLoading(false);
                        //dialog = Utils.showSimpleDialogReturnDialog(getString(R.string.dialog_title), urlTeleHealthResponse.messageIdentifier, getContext(), onClickCompany);


                    } else {
                        setLoading(false);

                        Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
                        intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.TELEHEALTH);
                        intent.putExtra("url", urlTeleHealthResponse.url);
                        startActivity(intent);
                    }
                } else {
                    setLoading(false);
                    ((BenefitsControlActivity) getActivity()).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                setLoading(false);
                if (getActivity() != null)
                    if (t instanceof SocketTimeoutException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else
                        ((BenefitsControlActivity) getActivity()).showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermissionType(String permission, int request_code) {
        if (permission.equals(Manifest.permission.CAMERA)) {
            if (shouldShowRequestPermissionRationale(permission)) {
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        } else if (permission.equals(Manifest.permission.RECORD_AUDIO)) {
            if (shouldShowRequestPermissionRationale(permission)) {
            } else {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            }
        } else if (permission.equals(Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
            if (shouldShowRequestPermissionRationale(permission)) {
            } else {
                requestPermissions(new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS}, REQUEST_MODIFY_AUDIO_SETTINGS_PERMISSION);
            }
        }

        if (shouldShowRequestPermissionRationale(permission)) {
            requestPermissions(new String[]{permission}, request_code);
        }
    }

    private boolean isDeviceSupportCamera() {
        return getContext() != null && getContext().getPackageManager() != null &&
                getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION || requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                if (requestCode == REQUEST_CAMERA_PERMISSION)
                    ErrorDialog.newInstance(getString(R.string.request_permission))
                            .show(getChildFragmentManager(), DIALOG_FRAGMENT);
                else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION)
                    ErrorDialog.newInstance(getString(R.string.request_permission_record_audio))
                            .show(getChildFragmentManager(), DIALOG_FRAGMENT);

            } else {
                callPermissions();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void callPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionType(Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION);
        } else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionType(Manifest.permission.RECORD_AUDIO, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            callModalTeleHealth();
        }
    }


    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //activity.finish();
                        }
                    })
                    .create();
        }
    }

    private View.OnClickListener onClickCompany = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setLoading(false, "");
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    };


    private void GetURL() {
        setLoading(true);
        String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = mAPIService.getURL(new ExternalAccessUrlBody(mCpf, 0, 0, 5));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);

                    if (!responseCommit.commited) {
                        ((BenefitsControlActivity) getActivity()).showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        setLoading(false);
                    } else {
                        mExternalAccess = new Gson().fromJson((response.body().getAsJsonObject()), ExternalAccess.class);
                        setLoading(false);
                    }
                } else {
                    setLoading(false);
                    ((BenefitsControlActivity) getActivity()).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }
            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                setLoading(false);
                if(getActivity()!= null)
                    if (t instanceof SocketTimeoutException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else
                        ((BenefitsControlActivity) getActivity()).showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

}

