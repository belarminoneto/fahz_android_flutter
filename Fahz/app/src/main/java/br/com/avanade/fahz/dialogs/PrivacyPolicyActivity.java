package br.com.avanade.fahz.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.fragments.FirstAccessFragment;
import br.com.avanade.fahz.model.lgpdModel.PolicyResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @BindView(R.id.terms_container)
    ConstraintLayout mTermsContainer;
    @BindView(R.id.txtTermText)
    TextView txtTermText;
    @BindView(R.id.btnDialogSave)
    Button btnDialogSave;
    @BindView(R.id.text_title)
    TextView text_title;

    Context mContext;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_terms_privacy_policy);
        ButterKnife.bind(this);
        mContext = this;

        mProgressDialog = new ProgressDialog(this);

        buttonEnabled(false);
        getTerm();
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mTermsContainer, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
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

    @OnClick(R.id.btnDialogSave)
    public void submit() {
        saveTerm();
    }

    @OnClick(R.id.btnDialogCancel)
    public void cancel(View view) {


        int resID;
        resID = getResources().getIdentifier("MSG503", "string", getPackageName());
        String message = getResources().getString(resID);
        Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), message, "OK", this,
                view1 -> {
                    Intent intent = new Intent(this, FirstAccessFragment.class);
                    intent.putExtra("resultCode", 1);
                    setResult(RESULT_CANCELED, intent);
                    finish();
                });
    }

    //PARA BLOQUEAR O BACK E TER OPÇÔES APENAS NO BOTÂO
    @Override
    public void onBackPressed() {

    }

    private void getTerm() {

        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<PolicyResponse> call = mAPIService.getPrivacyPolicy();

        call.enqueue(new Callback<PolicyResponse>() {
            @Override
            public void onResponse(@NonNull Call<PolicyResponse> call, @NonNull Response<PolicyResponse> response) {

                if (response.isSuccessful()) {

                    PolicyResponse policyResponse = response.body();

                    if (policyResponse != null && policyResponse.hasMessage()) {
                        showSnackBar(policyResponse.getMessage(), TYPE_FAILURE);
                    } else {
                        Spanned sp = Html.fromHtml(policyResponse.getPolicy().text == null ? "" : policyResponse.getPolicy().text);
                        txtTermText.setText(sp);
                        FirstAccessFragment.idTermOfAcceptancePrivacyPolicy = policyResponse.getPolicy().versionPanelId;
                    }

                } else {

                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), TYPE_FAILURE);
                    }
                }

                setLoading(false);
            }

            @Override
            public void onFailure(Call<PolicyResponse> call, Throwable t) {
                setLoading(false);
                Utils.showSimpleDialog(getString(R.string.dialog_title), t.getMessage(), null, getBaseContext(), null);
            }
        });
    }

    private void saveTerm() {
        Intent intent = new Intent(this, FirstAccessFragment.class);
        intent.putExtra("resultCode", 1);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnCheckedChanged(R.id.check_accept_terms)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            buttonEnabled(true);
        else
            buttonEnabled(false);
    }

    private void buttonEnabled(boolean isButtonEnabled) {
        if (isButtonEnabled) {
            btnDialogSave.setEnabled(true);
            btnDialogSave.setTextColor(getResources().getColor(R.color.white_text));
        } else {
            btnDialogSave.setEnabled(false);
            btnDialogSave.setTextColor(getResources().getColor(R.color.grey_text));
        }
        btnDialogSave.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }
}
