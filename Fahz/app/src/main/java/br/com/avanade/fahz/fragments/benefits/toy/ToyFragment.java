package br.com.avanade.fahz.fragments.benefits.toy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.BenefitsControlActivity;
import br.com.avanade.fahz.activities.benefits.toy.RequestNewToyActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.response.CanRequestToyResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;

public class ToyFragment extends Fragment {

    @BindView(R.id.btnRequestToy)
    ImageButton btnRequest;

    @BindView(R.id.btnRequestToyLabel)
    TextView lblRequest;

    private ProgressDialog mProgressDialog;
    private APIService mApiService;
    private String mCpf;
    private String observation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toy, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        mApiService = ServiceGenerator.createService(APIService.class);
        mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        setLoading(false);
        canRequestToy();

        return view;
    }

    private void canRequestToy() {
        setLoading(true);
        Call<CanRequestToyResponse> call = mApiService.canRequestToy(new CPFInBody(mCpf));
        call.enqueue(new Callback<CanRequestToyResponse>() {
            @Override
            public void onResponse(@NonNull Call<CanRequestToyResponse> call, @NonNull Response<CanRequestToyResponse> response) {
                CanRequestToyResponse body = response.body();
                if (body != null) {
                    Activity activity = getActivity();
                    if (activity != null) {
                        Boolean committed = body.getCommited();
                        if (committed != null && !committed) {
                            disabledButton(true);
                            showAlert(body.getMessageIdentifier());
                        } else {
                            CanRequestToyResponse.DataResponse data = body.getHoldertoycampaign();
                            if (data.getCanRequest()) {
                                disabledButton(false);
                                observation = body.getHoldertoycampaign().getObservation();
                            } else {
                                disabledButton(true);
                                showAlert(data.getMessage());
                            }
                        }
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<CanRequestToyResponse> call, @NonNull Throwable t) {
                BenefitsControlActivity benefitsControlActivity = (BenefitsControlActivity) getActivity();
                if (benefitsControlActivity != null) {
                    if (t instanceof SocketTimeoutException)
                        benefitsControlActivity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", benefitsControlActivity.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        benefitsControlActivity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", benefitsControlActivity.getPackageName())), TYPE_FAILURE);
                    else
                        benefitsControlActivity.showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
                setLoading(false);
            }
        });

    }

    private void disabledButton(boolean disable) {
        Activity activity = getActivity();
        if (activity != null) {
            Resources resources = activity.getResources();
            if (disable) {
                btnRequest.setBackgroundDrawable(resources.getDrawable(R.drawable.circle_action_disable));
                lblRequest.setTextColor(getActivity().getResources().getColor(R.color.grey_light_text_disable));
            } else {
                btnRequest.setBackgroundDrawable(resources.getDrawable(R.drawable.circle_action_enable));
                lblRequest.setTextColor(getActivity().getResources().getColor(R.color.black));
            }
            btnRequest.setClickable(!disable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @OnClick(R.id.btnRequestToy)
    void start(View view) {

        Intent intent = new Intent(getContext(), RequestNewToyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("observation", observation);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showAlert(String msg) {
        Utils.showSimpleDialog(getString(R.string.dialog_title), msg, getString(R.string.close), getActivity(), null);
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

}