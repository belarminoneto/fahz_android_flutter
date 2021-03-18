package br.com.avanade.fahz.activities.lgpd;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
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

import br.com.avanade.fahz.Adapter.lgpd.CardViewControlPrivacyAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.lgpdModel.Control;
import br.com.avanade.fahz.model.lgpdModel.PrivacyControlResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ControlOfPrivacyActivity extends NavDrawerBaseActivity {

    private ProgressDialog mProgressDialog;

    private int idBehavior;
    private Dialog dialog;

    @BindView(R.id.constraint_layout_control_privacy)
    ConstraintLayout mConstraintLayout;

    @BindView(R.id.recycler_view_control_privacy)
    RecyclerView recyclerView;

    CardViewControlPrivacyAdapter mCardViewControlPrivacyAdapter;

    private List<Control> mControlList = new ArrayList<>();

    private String messageIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_privacy);

        ButterKnife.bind(this);
        toolbarTitle.setText(R.string.title_privacy_control);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        mProgressDialog = new ProgressDialog(this);

        loadControl();
    }

    private void loadControl() {

        setLoading(true);

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<PrivacyControlResponse> call = apiService.getPrivacyControlText();

        call.enqueue(new Callback<PrivacyControlResponse>() {
            @Override
            public void onResponse(@NonNull Call<PrivacyControlResponse> call, @NonNull Response<PrivacyControlResponse> response) {
                FahzApplication.getInstance().setToken(((Headers) response.raw().headers()).get("token"));
                if (response.isSuccessful()) {

                    PrivacyControlResponse privacyControlResponse = response.body();

                    assert privacyControlResponse != null;
                    if (privacyControlResponse.hasMessage()) {

                        showSnackBar(privacyControlResponse.getMessage(), TYPE_FAILURE);

                    } else {

                        mControlList = privacyControlResponse.getPrivacyControl().controls;

                        if (mControlList.size() > 0) {

                            Control control = new Control();
                            control.description = privacyControlResponse.getPrivacyControl().text;
                            mControlList.add(0, control);

                            mCardViewControlPrivacyAdapter = new CardViewControlPrivacyAdapter(ControlOfPrivacyActivity.this, mControlList);
                            recyclerView.setAdapter(mCardViewControlPrivacyAdapter);
                        }
                    }
                    setLoading(false);
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
            public void onFailure(@NonNull Call<PrivacyControlResponse> call, @NonNull Throwable t) {
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

    private void setLoading(boolean loading) {
        if (loading) {
            mProgressDialog.setMessage("Carregando dados");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else
            mProgressDialog.dismiss();
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mConstraintLayout, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }
}