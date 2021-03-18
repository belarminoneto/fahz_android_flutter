package br.com.avanade.fahz.activities.lgpd;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.Adapter.lgpd.PanelTabAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.fragments.lpgd.TransparencyFragment;
import br.com.avanade.fahz.model.lgpdModel.TransparencyPanel;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class PanelTransparencyActivity extends NavDrawerBaseActivity {
    private ProgressDialog mProgressDialog;
    private TransparencyFragment mTransparencyFragment;

    private int idBehavior;
    private Dialog dialog;

    @BindView(R.id.ConstraintLayoutTransparency)
    ConstraintLayout mConstraintLayoutTransparency;

    private String messageIdentifier;

    public List<TransparencyPanel> transparencyPanelList = new ArrayList<>();

    public PanelTransparencyActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparency);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        toolbarTitle.setText(R.string.title_transparency);

        ViewPager viewPager = findViewById(R.id.viewPager);
        if (viewPager != null) {
            loadTransparency(viewPager);
        }
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mConstraintLayoutTransparency, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    private void setupViewPager(ViewPager viewPager) {

        PanelTabAdapter adapter = new PanelTabAdapter(getSupportFragmentManager());

        for (int i = 0; i < transparencyPanelList.size(); i++) {
            mTransparencyFragment = TransparencyFragment.newInstance(transparencyPanelList.get(i));
            adapter.add(mTransparencyFragment, transparencyPanelList.get(i).title);
        }

        viewPager.setAdapter(adapter);
    }

    private void setLoading(boolean loading) {
        if (loading) {
            mProgressDialog.setMessage("Carregando dados");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else
            mProgressDialog.dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }


    private void loadTransparency(final ViewPager viewPager) {

        setLoading(true);
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = apiService.getTransparencyPanel(cpf);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));

                if (response.isSuccessful()) {

                    JsonArray jsonArray = ((JsonObject) response.body()).getAsJsonArray("TransparencyPanel");

                    for (int i = 0; i < jsonArray.size(); i++) {
                        TransparencyPanel transparencyPanel = new Gson().fromJson(jsonArray.get(i).toString(), TransparencyPanel.class);
                        transparencyPanelList.add(transparencyPanel);
                    }

                    setupViewPager(viewPager);
                    TabLayout tabLayout = findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(viewPager);


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
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
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






