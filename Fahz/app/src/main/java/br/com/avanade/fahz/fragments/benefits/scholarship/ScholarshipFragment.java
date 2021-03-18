package br.com.avanade.fahz.fragments.benefits.scholarship;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.ProfileActivity;
import br.com.avanade.fahz.activities.benefits.BenefitsControlActivity;
import br.com.avanade.fahz.activities.benefits.scholarship.BaseScholarshipControlActivity;
import br.com.avanade.fahz.activities.benefits.scholarship.RequestScholarshipControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.SystemBehaviorModel;
import br.com.avanade.fahz.model.response.CanRequestScholarshipResponse;
import br.com.avanade.fahz.model.response.RequestCourseFollowupResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ScholarshipFragment extends Fragment {

    @BindView(R.id.container_scholarship)
    LinearLayout mContent;

    @BindView(R.id.btnRequest)
    ImageButton btnRequest;
    @BindView(R.id.btnRefund)
    ImageButton btnRefund;
    @BindView(R.id.btnCourseFollowup)
    ImageButton btnCourseFollowup;

    @BindView(R.id.lblRequest)
    TextView lblRequest;
    @BindView(R.id.lblRefund)
    TextView lblRefund;
    @BindView(R.id.lblCourseFollowup)
    TextView lblCourseFollowup;

    @BindView(R.id.layout_request)
    LinearLayout layoutRequest;
    @BindView(R.id.layout_refund)
    LinearLayout layoutRefund;
    @BindView(R.id.layout_followup)
    LinearLayout layoutFollowup;


    private ProgressDialog mProgressDialog;
    int idPlan = 0;
    boolean canRequest;
    boolean canRequestRefund;
    boolean canStartFollowup;
    boolean canSendDocument;
    SystemBehaviorModel systemBehavior;
    SystemBehaviorModel systemBehaviorFollowup;
    String idScholarship;
    String followupMensage;

    private Dialog dialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scholarship, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false);
        validateCanRequest();

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

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContent, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void validateCanRequest() {
        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = mAPIService.getCanRequestScholarship(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        setLoading(false);
                    } else {
                        CanRequestScholarshipResponse canRequestReturn = new Gson().fromJson((response.body().getAsJsonObject()), CanRequestScholarshipResponse.class);
                        canRequest = canRequestReturn.getCanRequest();
                        idPlan = canRequestReturn.getPlanId();


                        if (!canRequest) {
                            btnRequest.setBackgroundDrawable(FahzApplication.getAppContext().getResources().getDrawable(R.drawable.circle_action_disable));
                            lblRequest.setTextColor(FahzApplication.getAppContext().getResources().getColor(R.color.grey_light_text_disable));
                        } else {
                            btnRequest.setBackgroundDrawable(FahzApplication.getAppContext().getResources().getDrawable(R.drawable.circle_action_enable));
                            lblRequest.setTextColor(FahzApplication.getAppContext().getResources().getColor(R.color.black));
                        }
                    }

                    validateCanRequestRefund();
                } else {
                    setLoading(false);
                    ((BenefitsControlActivity) getActivity()).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
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

    public void validateCanRequestRefund() {
        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = mAPIService.getCanRequestRefund(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        setLoading(false);
                    } else {
                        CanRequestScholarshipResponse canRequestReturn = new Gson().fromJson((response.body().getAsJsonObject()), CanRequestScholarshipResponse.class);
                        canRequestRefund = canRequestReturn.getCanRequest();
                        systemBehavior = canRequestReturn.getSystemBehavior();


                        if (!canRequestRefund) {
                            btnRefund.setEnabled(false);
                            btnRefund.setBackgroundDrawable(FahzApplication.getAppContext().getResources().getDrawable(R.drawable.circle_action_disable));
                            lblRefund.setTextColor(FahzApplication.getAppContext().getResources().getColor(R.color.grey_light_text_disable));
                        } else {
                            btnRefund.setBackgroundDrawable(FahzApplication.getAppContext().getResources().getDrawable(R.drawable.circle_action_enable));
                            lblRefund.setTextColor(FahzApplication.getAppContext().getResources().getColor(R.color.black));
                        }
                    }

                    validateCanStartFollowup();
                } else {
                    setLoading(false);
                    ((BenefitsControlActivity) getActivity()).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
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

    public void validateCanStartFollowup() {
        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = mAPIService.canStartFollowUp(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        setLoading(false);
                    } else {
                        RequestCourseFollowupResponse canRequestReturn = new Gson().fromJson((response.body().getAsJsonObject()), RequestCourseFollowupResponse.class);
                        canStartFollowup = canRequestReturn.getRequestCourseFollowup().getCanAccess();
                        canSendDocument = canRequestReturn.getRequestCourseFollowup().getCanSendNewDocument();
                        systemBehaviorFollowup = canRequestReturn.getRequestCourseFollowup().getSystemBehavior();
                        idScholarship = canRequestReturn.getRequestCourseFollowup().getIdScholarship();
                        followupMensage = canRequestReturn.getRequestCourseFollowup().getMessage();


                        if (!canStartFollowup) {
                            btnCourseFollowup.setBackgroundDrawable(FahzApplication.getAppContext().getResources().getDrawable(R.drawable.circle_action_disable));
                            lblCourseFollowup.setTextColor(FahzApplication.getAppContext().getResources().getColor(R.color.grey_light_text_disable));
                        } else {
                            btnCourseFollowup.setBackgroundDrawable(FahzApplication.getAppContext().getResources().getDrawable(R.drawable.circle_action_enable));
                            lblCourseFollowup.setTextColor(FahzApplication.getAppContext().getResources().getColor(R.color.black));
                        }
                    }

                    setLoading(false);
                } else {
                    setLoading(false);
                    ((BenefitsControlActivity) getActivity()).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
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

    //BOTÃO DE SOLICITAR BENEFICIO
    @OnClick(R.id.btnRequest)
    public void requestCard(View view) {
        if (!canRequest && !canRequestRefund) {
            int resID = getResources().getIdentifier("MSG300", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
        } else if (!canRequest && canRequestRefund) {

            int resID = getResources().getIdentifier("MSG299", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
        }
        else
        {
            Intent intent = new Intent(getContext(), RequestScholarshipControlActivity.class);
            intent.putExtra(Constants.SCHOLARSHIP_CONTROL, RequestScholarshipControlActivity.ScholarshipFragment.SCHOLARSHIPDATA);
            startActivity(intent);
        }
    }


    //BOTÃO DE SOLICITAR REEMBOLSO
    @OnClick(R.id.btnRefund)
    public void requestRefund(View view) {
        setLoading(true);
        APIService apiService = ServiceGenerator.createService(APIService.class);

        Call<JsonElement> call = apiService.getCanRequestRefund(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        setLoading(false);
                    } else {
                        CanRequestScholarshipResponse request = new Gson().fromJson((response.body().getAsJsonObject()), CanRequestScholarshipResponse.class);


//                        SessionManager manager = new SessionManager(getActivity());
//                        manager.createPreference(Constants.IDPLAN_SCHOOL_SUPPLIES, String.valueOf(request.getUserRequestSchool().getPlanId()));
//                        manager.createPreference(Constants.SCHOOL_SUPPLIES_REFUND, String.valueOf(request.getUserRequestSchool().getIsRefund()));
//                        manager.createPreference(Constants.ALREADY_HAS_PLAN, String.valueOf(request.getUserRequestSchool().getAlreayHasCardBenefit()));
//                        manager.createPreference(Constants.ALREADY_HAS_REFUND, String.valueOf(request.getUserRequestSchool().getAlreayHasRefundBenefit()));
//                        manager.createPreference(Constants.HAS_BANK_DATA, String.valueOf(request.getUserRequestSchool().getHolderHasBankData()));

                        if (request.isHolderHasBankData()) {
                            Intent intent = new Intent(getContext(), BaseScholarshipControlActivity.class);
                            intent.putExtra(Constants.BASE_SCHOOL_CONTROL, BaseScholarshipControlActivity.BaseSchoolFragment.REQUESTREFUND);
                            intent.putExtra(Constants.SCHOLARSHIP_REFUND_DOCUMENT, systemBehavior.ID);
                            startActivity(intent);
                        } else {
                            int resID = getResources().getIdentifier("MSG273", "string", getActivity().getPackageName());

                            String message = getResources().getString(resID);
                            dialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, getActivity(), onClickOk);
                        }
                    }
                    setLoading(false);

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    } finally {
                        setLoading(false);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                if(getActivity()!= null) {
                    if (t instanceof SocketTimeoutException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else
                        ((BenefitsControlActivity) getActivity()).showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
                setLoading(false);
            }
        });

    }


    private View.OnClickListener onClickOk = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            intent.putExtra(Constants.BANK_FOR_REFUND,true);
            startActivityForResult (intent, Constants.SCHOOL_SUPPLIES_VALIDADE_PROFILE_REFUND);
            if(dialog != null) {
                dialog.dismiss();
            }
        }
    };
    //BOTÃO DE ENVIAR ACOMPANHAMENTO
    @OnClick(R.id.btnCourseFollowup)
    public void followUp(View view) {
        if (!canStartFollowup) {
            Utils.showSimpleDialog(getString(R.string.dialog_title), followupMensage, null, getActivity(), null);
        }
        else
        {
            Intent intent = new Intent(getContext(), BaseScholarshipControlActivity.class);
            intent.putExtra(Constants.BASE_SCHOOL_CONTROL, BaseScholarshipControlActivity.BaseSchoolFragment.FOLLOWUP);
            intent.putExtra(Constants.SCHOLARSHIP_REFUND_DOCUMENT, systemBehaviorFollowup.ID);
            intent.putExtra(Constants.SCHOLARSHIP_ID, idScholarship);
            intent.putExtra(Constants.CAN_SEND_DOCUMENT, canSendDocument);
            startActivity(intent);
        }
    }



}