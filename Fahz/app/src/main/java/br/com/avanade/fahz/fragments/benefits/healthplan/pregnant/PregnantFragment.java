package br.com.avanade.fahz.fragments.benefits.healthplan.pregnant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.BenefitsControlActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.dialogs.TermsOfUseActivity;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.ExternalAccess;
import br.com.avanade.fahz.model.externalaccess.ExternalAccessUrlBody;
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

public class PregnantFragment extends Fragment {

    private ProgressDialog mProgressDialog;

    @BindView(R.id.container_pregnant)
    LinearLayout mContent;

    @BindView(R.id.btnSubscriptionPregnant)
    ImageButton btnSubscriptionPregnant;

    @BindView(R.id.btnCommunicationPregnant)
    ImageButton btnCommunicationPregnant;

    @BindView(R.id.lblCommunicationPregnant)
    TextView lblCommunicationPregnant;


    private ExternalAccess mExternalAccess;
    private ExternalAccess mExternalAccessWhats;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pregnant, container, false);
        ButterKnife.bind(this, view);

        setupUi();

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false);

        GetURL();
        GetURLWhatsApp();

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //BOTÃO DE PROGRAMA GESTANTE
    @OnClick(R.id.btnSubscriptionPregnant)
    public void CallPregnantProgram(View view) {
        if (mExternalAccess != null) {
            String url = mExternalAccess.URL;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        else{
            ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
        }
    }

    public void CallCommunicationPregnantProgram() {

        if (mExternalAccessWhats != null) {
            String urlWhats = mExternalAccessWhats.URL;
            Intent whats = new Intent(Intent.ACTION_VIEW);
            whats.setData(Uri.parse(urlWhats));
            startActivity(whats);
        } else {
            ((BenefitsControlActivity) getActivity()).showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
        }
    }

    @OnClick(R.id.btnCommunicationPregnant)
    public void openTermsOfUseActivity() {
        Intent intent = new Intent(getActivity(), TermsOfUseActivity.class);
        intent.putExtra(Constants.TERMS_OF_USE_SELECTED, Constants.TERMS_OF_USE_CODE_WHATSAPP);
        intent.putExtra(Constants.TERMS_OF_USE_FROM_BENEFIT, true);
        startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.TERMS_OF_USE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                CallCommunicationPregnantProgram();
            }
        }
    }

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

    private void GetURLWhatsApp() {
        setLoading(true);
        String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = mAPIService.getURL(new ExternalAccessUrlBody(mCpf, 0, 0, 8));
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
                        mExternalAccessWhats = new Gson().fromJson((response.body().getAsJsonObject()), ExternalAccess.class);
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

    private void setupUi()
    {
        ((BaseHealthControlActivity)getActivity()).toolbarTitle.setText(getActivity().getString(R.string.pregnant_program));
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

}