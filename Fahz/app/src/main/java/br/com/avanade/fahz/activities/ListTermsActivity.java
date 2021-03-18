package br.com.avanade.fahz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.TermsByUser;
import br.com.avanade.fahz.model.response.TermsByUserResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ListTermsActivity extends NavDrawerBaseActivity {

    private ProgressDialog mProgressDialog;
    SessionManager sessionManager;

    @BindView(R.id.content_terms)
    LinearLayout mContainer;
    @BindView(R.id.term_spinner)
    Spinner mTermSpinner;
    @BindView(R.id.scrollViewTerm)
    ScrollView scrollViewTerm;
    @BindView(R.id.imageTerm)
    ImageView imageTerm;
    @BindView(R.id.txtSelectTerm)
    TextView txtSelectTerm;
    @BindView(R.id.txtTermText)
    TextView txtTermText;

    private List<TermsByUser> terms = new ArrayList<>();
    private List<String> termsStr = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_list_terms);
        ButterKnife.bind(this);

        toolbarTitle.setText(getString(R.string.label_terms));

        sessionManager = new SessionManager(getApplicationContext());
        mProgressDialog = new ProgressDialog(this);

        loadTerms();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void loadTerms() {
        setLoading(true, getString(R.string.search_dependents));
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = apiService.getAccepedtedTerms(new CPFInBody(cpf));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        if (response.body().getAsJsonObject().has("messageIdentifier")) {
                            CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                            showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                            setLoading(false, "");
                        } else {
                            TermsByUserResponse information = new Gson().fromJson((response.body().getAsJsonObject()), TermsByUserResponse.class);

                            terms = new ArrayList<>();
                            termsStr = new ArrayList<>();

                            if (information.getRegisters().size() > 0) {
                                terms.addAll(information.getRegisters());
                            }

                            termsStr.add("Selecione");
                            for (TermsByUser termByUser : terms) {
                                termsStr.add(termByUser.getTermOfUse().code);
                            }


                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                                    R.layout.spinner_layout, termsStr);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mTermSpinner.setAdapter(adapter);

                        }
                    }

                } else {
                    try {
                        String data = response.errorBody() != null ? response.errorBody().string() : "";
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
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                setLoading(false, "");
                if(t instanceof SocketTimeoutException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if(t instanceof UnknownHostException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

    private void setLoading(Boolean loading, String text) {
        if (loading) {
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);

        snackbar.show();
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    //CASO A SELECAO NO COMBO DE TERMOS MUDE
    @OnItemSelected(R.id.term_spinner)
    void onItemSelected(int position) {
        if(termsStr.size()>0) {
            String value = termsStr.get(position);

            if (!value.equals("Selecione")) {
                for (TermsByUser term : terms) {
                    if (term.getTermOfUse().code.equals(value)) {
                        scrollViewTerm.setVisibility(View.VISIBLE);
                        imageTerm.setVisibility(View.GONE);
                        txtSelectTerm.setVisibility(View.GONE);

                        Spanned sp = Html.fromHtml(term.getTermOfUse().Text);
                        txtTermText.setText(sp);
                    }
                }
            } else {
                scrollViewTerm.setVisibility(View.GONE);
                imageTerm.setVisibility(View.VISIBLE);
                txtSelectTerm.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.btnDownload)
    public void onclick() {
        String value = mTermSpinner.getSelectedItem().toString();

        for (TermsByUser term : terms) {
            if (term.getTermOfUse().code.equals(value)) {
                if(term.getTermOfUse().filePath!=null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(term.getTermOfUse().filePath));
                    startActivity(intent);
                }
                else
                {
                    showSnackBar(getString(R.string.no_path), TYPE_FAILURE);
                }
            }
        }
    }

}
