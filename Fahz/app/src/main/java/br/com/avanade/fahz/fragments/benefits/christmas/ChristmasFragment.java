package br.com.avanade.fahz.fragments.benefits.christmas;

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

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.christmas.SelectAddressActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.response.ChristmasStartResponse;
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
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ChristmasFragment extends Fragment {

    private ProgressDialog mProgressDialog;

    @BindView(R.id.container_christmas)
    LinearLayout mContent;

    @BindView(R.id.btnRequestAddress)
    ImageButton btnRequest;
    @BindView(R.id.lblRequestAddress)
    TextView lblRequest;

    ChristmasStartResponse christmasResponse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_christmas, container, false);
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
        Call<JsonElement> call = mAPIService.startChooseAddress(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
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
                        ChristmasStartResponse canRequestReturn = new Gson().fromJson((response.body().getAsJsonObject()), ChristmasStartResponse.class);
                        christmasResponse = canRequestReturn;

                        if (!christmasResponse.getXmasStartChoose().getHasAvailablecampaign()) {
                            btnRequest.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.circle_action_disable));
                            lblRequest.setTextColor(getActivity().getResources().getColor(R.color.grey_light_text_disable));
                        } else {

                        }
                    }

                    setLoading(false);

                } else {
                    setLoading(false);
                    showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                setLoading(false);
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

    //BOTÃO DE ENVIAR ACOMPANHAMENTO
    @OnClick(R.id.btnRequestAddress)
    public void start(View view) {
        if (christmasResponse.getXmasStartChoose().getHasAvailablecampaign()) {
            Intent intent = new Intent(getContext(), SelectAddressActivity.class);
            Gson gson = new Gson();
            String myJson = gson.toJson(christmasResponse);
            intent.putExtra("ChristmasResponse", myJson);
            startActivity(intent);
        }
        else
        {
            Utils.showSimpleDialog(getString(R.string.dialog_title), christmasResponse.getXmasStartChoose().getReasonNote(), null, getActivity(), null);
        }
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