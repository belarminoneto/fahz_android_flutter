package br.com.avanade.fahz.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.response.TermOfUseResponse;
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

import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class TermsOfOrientationActivity extends AppCompatActivity {


    @BindView(R.id.terms_container)
    RelativeLayout mTermsContainer;
    @BindView(R.id.txtTermText)
    TextView txtTermText;
    @BindView(R.id.btnDialogSave)
    Button btnDialogSave;
    SessionManager sessionManager;
    String termOfUseTowork = "";
    Context mContext;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.dialog_terms_of_orientation);
        ButterKnife.bind(this);

        termOfUseTowork= getIntent().getStringExtra(Constants.TERMS_OF_USE_SELECTED);

        sessionManager = new SessionManager(getApplicationContext());
        mContext = this;

        mProgressDialog = new ProgressDialog(this);
        Spanned sp = Html.fromHtml("");
        txtTermText.setText(sp);

        getTermOfUse();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mTermsContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
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
    public void submit(View view) {
        this.finish();
    }


    //PARA BLOQUEAR O BACK E TER OPÇÔES APENAS NO BOTÂO
    @Override
    public void onBackPressed() {

    }

    private void getTermOfUse() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true);

            APIService mAPIService = ServiceGenerator.createService(APIService.class);
            Call<JsonElement> call = mAPIService.getTermByCode(termOfUseTowork);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful() && !response.body().toString().contains("messageIdentifier")) {


                        TermOfUseResponse responseTerm = new Gson().fromJson((response.body().getAsJsonObject()), TermOfUseResponse.class);
                        Spanned sp = Html.fromHtml(responseTerm.Text);
                        txtTermText.setText(sp);

                    } else if(response.isSuccessful() && response.body().toString().contains("messageIdentifier"))
                    {
                        try {
                            String data = response.body().toString();
                            JSONObject jObjError = new JSONObject(data);
                            String message = jObjError.getString("messageIdentifier");
                            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, mContext, null);
                            setLoading(false);
                        }
                        catch (Exception ex){
                            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, mContext, null);
                        }

                    }
                    else {
                        String message;
                        String code;
                        try {
                            String data =response.errorBody().toString();
                            JSONObject jObjError = new JSONObject(data);
                            message = jObjError.getString("Message");
                            code = jObjError.getString("Code");
                            if (code.equals(Constants.ERROR_UNAUTHORIZED) &&
                                    message.equals(Constants.ERROR_DESCRIPTION_UNAUTHORIZED)) {
                                logoutUser();
                            } else {
                                setLoading(false);
                                CommitResponse commitResponse = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                                Utils.showSimpleDialog(getString(R.string.dialog_title), commitResponse.messageIdentifier, null, mContext, null);
                            }
                        }
                        catch (Exception ex){
                            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, mContext, null);
                        }
                    }

                    setLoading(false);
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    setLoading(false);
                    Utils.showSimpleDialog(getString(R.string.dialog_title), t.getMessage(), null, getBaseContext(), null);
                }
            });
        }
    }

    private void logoutUser() {
        setLoading(true);
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.logout();
    }

}
