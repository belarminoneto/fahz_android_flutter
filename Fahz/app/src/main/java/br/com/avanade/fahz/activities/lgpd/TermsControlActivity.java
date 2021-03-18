package br.com.avanade.fahz.activities.lgpd;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.Adapter.lgpd.CardViewTermsControlAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.lgpdModel.PrivacyTerms;
import br.com.avanade.fahz.model.lgpdModel.Register;
import br.com.avanade.fahz.model.lgpdModel.TermOfService;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class TermsControlActivity extends NavDrawerBaseActivity {

    private ProgressDialog mProgressDialog;

    private int idBehavior;
    private Dialog dialog;

    @BindView(R.id.ConstraintLayoutTermControl)
    ConstraintLayout mConstraintLayoutNotification;

    @BindView(R.id.textViewTermControl)
    TextView mTextViewTermControl;

    @BindView(R.id.recyclerTermControl)
    RecyclerView recyclerView;

    CardViewTermsControlAdapter mCardViewTermsControlAdapter;

    private List<Register> mTermOfUseList = new ArrayList<>();

    private String messageIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_control);

        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        mProgressDialog = new ProgressDialog(this);
        toolbarTitle.setText(R.string.title_control_term_service);

        loadPanels();
        loadTerms();
    }

    private void setLoading(boolean loading) {
        if (loading) {
            mProgressDialog.setMessage("Carregando dados");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else
            mProgressDialog.dismiss();
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mConstraintLayoutNotification, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    private void loadPanels() {
        setLoading(true);

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<PrivacyTerms> call = apiService.getPrivacyGeneral();
        call.enqueue(new Callback<PrivacyTerms>() {
            @Override
            public void onResponse(@NonNull Call<PrivacyTerms> call, @NonNull Response<PrivacyTerms> response) {
                FahzApplication.getInstance().setToken(((Headers) response.raw().headers()).get("token"));
                if (response.isSuccessful()) {

                    PrivacyTerms privacyTerms = response.body();

                    if (privacyTerms.hasMessage()) {
                        showSnackBar(privacyTerms.getMessage(), TYPE_FAILURE);
                    } else {
                        Spanned sp = Html.fromHtml(privacyTerms.section.get(0).panel.get(3).obs);
                        mTextViewTermControl.setText(sp);
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
                    }
                }

                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<PrivacyTerms> call, @NonNull Throwable t) {
                setLoading(false);
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }


    private void loadTerms() {
        setLoading(true);

        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<TermOfService> call = apiService.getallbyuser(new CPFInBody(cpf));

        call.enqueue(new Callback<TermOfService>() {
            @Override
            public void onResponse(@NonNull Call<TermOfService> call, @NonNull Response<TermOfService> response) {
                FahzApplication.getInstance().setToken(((Headers) response.raw().headers()).get("token"));
                if (response.isSuccessful()) {

                    TermOfService termOfService = response.body();

                    if (termOfService.hasMessage()) {

                        showSnackBar(termOfService.getMessage(), TYPE_FAILURE);

                    } else {

                        mTermOfUseList = termOfService.registers;

                        if (mTermOfUseList.size() > 0) {
                            mCardViewTermsControlAdapter = new CardViewTermsControlAdapter(TermsControlActivity.this, mTermOfUseList);
                            recyclerView.setAdapter(mCardViewTermsControlAdapter);
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
                    }
                }

                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<TermOfService> call, @NonNull Throwable t) {
                setLoading(false);
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }
}