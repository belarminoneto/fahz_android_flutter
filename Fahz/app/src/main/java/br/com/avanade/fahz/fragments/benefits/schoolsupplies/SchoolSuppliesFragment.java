package br.com.avanade.fahz.fragments.benefits.schoolsupplies;

import android.app.Activity;
import android.app.Dialog;
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
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.ProfileActivity;
import br.com.avanade.fahz.activities.benefits.BenefitsControlActivity;
import br.com.avanade.fahz.activities.benefits.schoolsupplies.BaseSchoolSuppliesControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.SchoolSuppliesCanRequest;
import br.com.avanade.fahz.model.response.CanSendReceiptResponse;
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

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class SchoolSuppliesFragment extends Fragment {

    @BindView(R.id.container_school_supplies)
    LinearLayout mContentSchoolSupplies;

    @BindView(R.id.btnRequestCard)
    ImageButton btnRequestCard;
    @BindView(R.id.btnRequestRefund)
    ImageButton btnRequestRefund;
    @BindView(R.id.btnSendReceipt)
    ImageButton btnSendReceipt;

    @BindView(R.id.lblRequestCard)
    TextView lblRequestCard;
    @BindView(R.id.lblRequestRefund)
    TextView lblRequestRefund;
    @BindView(R.id.lblSendReceipt)
    TextView lblSendReceipt;

    @BindView(R.id.layout_request)
    LinearLayout layoutRequest;
    @BindView(R.id.layout_refund)
    LinearLayout layoutRefund;
    @BindView(R.id.layout_receipts)
    LinearLayout layoutReceipts;

    private ProgressDialog mProgressDialog;
    String idPlan;
    boolean alreadyHasPlan;
    boolean alreadyHasRefund;
    SessionManager manager;
    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_supplies, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false);

        manager = new SessionManager(getContext());
        idPlan = manager.getPreference(Constants.IDPLAN_SCHOOL_SUPPLIES);
        boolean isRefund = Boolean.valueOf(manager.getPreference(Constants.SCHOOL_SUPPLIES_REFUND));
        alreadyHasPlan = Boolean.valueOf(manager.getPreference(Constants.ALREADY_HAS_PLAN));
        alreadyHasRefund = Boolean.valueOf(manager.getPreference(Constants.ALREADY_HAS_REFUND));
        setupUI(isRefund);

        return view;
    }

    public void setupUI(boolean isRefund) {
        if (isRefund) {
            layoutRequest.setVisibility(View.GONE);
            layoutRefund.setVisibility(View.VISIBLE);
        } else {
            layoutRequest.setVisibility(View.VISIBLE);
            layoutRefund.setVisibility(View.GONE);
        }

        if (alreadyHasPlan) {
            btnRequestCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
            lblRequestCard.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
        } else {
            btnRequestCard.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
            lblRequestCard.setTextColor(this.getResources().getColor(R.color.black));
        }

        if (alreadyHasRefund) {
            btnRequestRefund.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_disable));
            lblRequestRefund.setTextColor(this.getResources().getColor(R.color.grey_light_text_disable));
        } else {
            btnRequestRefund.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.circle_action_enable));
            lblRequestRefund.setTextColor(this.getResources().getColor(R.color.black));
        }

        canSendReceipt();
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

    //BOTÃO DE SOLICITAR BENEFICIO CARTÃO, VALIDANDO PRIMEIRO O TERMO DE USO
    @OnClick(R.id.btnRequestCard)
    public void requestCard(View view) {
        if (!alreadyHasPlan) {
            if(FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.PENDING_STATUS) && !FahzApplication.getInstance().getFahzClaims().getPendingApproval()) {

                int resID = getResources().getIdentifier("MSG124", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                showSnackBarDismiss(message, TYPE_FAILURE,new Snackbar.Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        intent.putExtra(Constants.FROM_CARD,true);
                        startActivityForResult (intent, Constants.SCHOOL_SUPPLIES_VALIDADE_PROFILE_CARD);
                    }
                });
            }
            else {
                openRequestCard();
            }
        } else {
            int resID = getResources().getIdentifier("MSG225", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
        }
    }


    //BOTÃO DE SOLICITAR BENEFICIO REEMBOLSO, VALIDANDO PRIMEIRO O TERMO DE USO
    @OnClick(R.id.btnRequestRefund)
    public void adehesionTermRefund(View view) {
        if (!alreadyHasRefund) {
            if(FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.PENDING_STATUS) && !FahzApplication.getInstance().getFahzClaims().getPendingApproval()) {
                requestRefund(true);
            } else {
                requestRefund(false);
            }
        } else {
            int resID = getResources().getIdentifier("MSG285", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.TERMS_OF_USE_RESULT_REFUND) {
            if (resultCode == Activity.RESULT_OK) {
                adehesionTermRefund(null);
            }
        }
        else  if (requestCode == Constants.SCHOOL_SUPPLIES_VALIDADE_PROFILE_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                requestCard(null);
            }
        }
        else  if (requestCode == Constants.SCHOOL_SUPPLIES_VALIDADE_PROFILE_REFUND) {
            if (resultCode == Activity.RESULT_OK) {
                adehesionTermRefund(null);
            }
        }
    }

    //PROXIMO PASSO PARA SOLICITAR REEMBOLSO< AGORA VALIDA SE POSSUI DADOS BANCARIOS
    public void requestRefund(final boolean isPending) {
        setLoading(true);
        APIService apiService = ServiceGenerator.createService(APIService.class);

        Call<JsonElement> call = apiService.checkCanRequestBenefit(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
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
                        SchoolSuppliesCanRequest request = new Gson().fromJson((response.body().getAsJsonObject()), SchoolSuppliesCanRequest.class);
                        boolean canViewSchoolSupplies = false;
                        if (request.getUserRequestSchool().getIsCard() || request.getUserRequestSchool().getIsRefund()) {
                            canViewSchoolSupplies = true;

                            SessionManager manager = new SessionManager(getActivity());
                            manager.createPreference(Constants.IDPLAN_SCHOOL_SUPPLIES, String.valueOf(request.getUserRequestSchool().getPlanId()));
                            manager.createPreference(Constants.SCHOOL_SUPPLIES_REFUND, String.valueOf(request.getUserRequestSchool().getIsRefund()));
                            manager.createPreference(Constants.ALREADY_HAS_PLAN, String.valueOf(request.getUserRequestSchool().getAlreayHasCardBenefit()));
                            manager.createPreference(Constants.ALREADY_HAS_REFUND, String.valueOf(request.getUserRequestSchool().getAlreayHasRefundBenefit()));
                            manager.createPreference(Constants.HAS_BANK_DATA, String.valueOf(request.getUserRequestSchool().getHolderHasBankData()));

                            if(request.getUserRequestSchool().getHolderHasBankData() && !isPending) {
                                openRequestRefund();
                            }
                            else
                            {
                                int resID = 0;
                                if(isPending)
                                    resID = getResources().getIdentifier("MSG124", "string", getActivity().getPackageName());
                                else
                                    resID = getResources().getIdentifier("MSG273", "string", getActivity().getPackageName());

                                String message = getResources().getString(resID);
                                dialog = Utils.showQuestionReturnDialog (getString(R.string.dialog_title), message, getActivity(), onClickOk);
                            }
                        }
                    }
                    setLoading(false, "");

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    } finally {
                        setLoading(false, "");
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
                setLoading(false, "");
            }
        });

    }

    private void openRequestRefund()
    {
        Intent intent = new Intent(getContext(), BaseSchoolSuppliesControlActivity.class);
        intent.putExtra(Constants.BASE_SCHOOL_CONTROL, BaseSchoolSuppliesControlActivity.BaseSchoolFragment.REQUESTREFUND);
        intent.putExtra(Constants.IDPLAN_SCHOOL_SUPPLIES, idPlan);
        startActivity(intent);
    }

    private void openRequestCard()
    {
        Intent intent = new Intent(getContext(), BaseSchoolSuppliesControlActivity.class);
        intent.putExtra(Constants.BASE_SCHOOL_CONTROL, BaseSchoolSuppliesControlActivity.BaseSchoolFragment.REQUESTCARD);
        intent.putExtra(Constants.IDPLAN_SCHOOL_SUPPLIES, idPlan);
        startActivity(intent);
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

    //ENVIO DE RECIPE
    @OnClick(R.id.btnSendReceipt)
    public void sendReceipt(View view) {
        Intent intent = new Intent(getContext(), BaseSchoolSuppliesControlActivity.class);
        intent.putExtra(Constants.BASE_SCHOOL_CONTROL, BaseSchoolSuppliesControlActivity.BaseSchoolFragment.SENDRECEIPT);
        intent.putExtra(Constants.IDPLAN_SCHOOL_SUPPLIES, idPlan);
        startActivity(intent);
    }

    private void canSendReceipt() {
        setLoading(true, getString(R.string.loading_searching));
        APIService apiService = ServiceGenerator.createService(APIService.class);

        Call<CanSendReceiptResponse> call = apiService.canSendReceipts(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<CanSendReceiptResponse>() {
            @Override
            public void onResponse(@NonNull Call<CanSendReceiptResponse> call, @NonNull Response<CanSendReceiptResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    CanSendReceiptResponse commitResponse = response.body();
                    if(commitResponse.getNeedToSendReceipt()) {
                        layoutReceipts.setVisibility(View.VISIBLE);
                        if (commitResponse.getCanSendReceipt())
                        {
                            btnSendReceipt.setEnabled(true);
                            btnSendReceipt.setBackgroundDrawable(FahzApplication.getAppContext().getResources().getDrawable(R.drawable.circle_action_enable));
                            lblSendReceipt.setTextColor(FahzApplication.getAppContext().getResources().getColor(R.color.black));
                        }
                        else
                        {
                            btnSendReceipt.setEnabled(false);
                            btnSendReceipt.setBackgroundDrawable(FahzApplication.getAppContext().getResources().getDrawable(R.drawable.circle_action_disable));
                            lblSendReceipt.setTextColor(FahzApplication.getAppContext().getResources().getColor(R.color.grey_light_text_disable));
                        }
                    }
                    else
                        layoutReceipts.setVisibility(View.GONE);


                    setLoading(false, "");

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    } finally {
                        setLoading(false, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CanSendReceiptResponse> call, @NonNull Throwable t) {
                if(getActivity()!= null) {
                    if (t instanceof SocketTimeoutException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else
                        ((BenefitsControlActivity) getActivity()).showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
                setLoading(false, "");
            }
        });
    }

    private void setLoading(Boolean loading, String text) {
        if (loading) {
            if(mProgressDialog!=null) {
                mProgressDialog.setMessage(text);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        } else {
            try {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
            catch (Exception ex)
            {
                LogUtils.error("MainActivity - Loading", ex);
            }
        }
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContentSchoolSupplies, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContentSchoolSupplies, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }
}