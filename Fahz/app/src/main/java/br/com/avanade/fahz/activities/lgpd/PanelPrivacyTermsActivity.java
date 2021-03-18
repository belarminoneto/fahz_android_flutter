package br.com.avanade.fahz.activities.lgpd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.avanade.fahz.Adapter.lgpd.PanelTabAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.dialogs.PrivacyTermsNewsActivity;
import br.com.avanade.fahz.fragments.lpgd.PrivacyTermsFragment;
import br.com.avanade.fahz.fragments.lpgd.PrivacyTermsOverViewFragment;
import br.com.avanade.fahz.model.lgpdModel.NewsPlainText;
import br.com.avanade.fahz.model.lgpdModel.PrivacyTerms;
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

public class PanelPrivacyTermsActivity extends NavDrawerBaseActivity {
    private ProgressDialog mProgressDialog;

    @BindView(R.id.ConstraintLayoutTerm)
    ConstraintLayout mConstraintLayout;

    private PrivacyTerms mPrivacyTerms = new PrivacyTerms();
    public List<NewsPlainText> newsPlainTextList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_terms);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);

        toolbarTitle.setText(R.string.label_privacy_terms);

        ViewPager viewPager = findViewById(R.id.viewPager);
        if (viewPager != null) {
            loadPanels(viewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        PanelTabAdapter adapter = new PanelTabAdapter(getSupportFragmentManager());

        for (int i = 0; i < mPrivacyTerms.section.get(0).panel.size(); i++) {

            if (mPrivacyTerms.section.get(0).panel.get(i).id == 1) {
                PrivacyTermsOverViewFragment mPrivacyTermsOverViewFragment = PrivacyTermsOverViewFragment.newInstance(mPrivacyTerms);
                adapter.add(mPrivacyTermsOverViewFragment, mPrivacyTerms.section.get(0).panel.get(i).title);
            } else {
                PrivacyTermsFragment mPrivacyTermsFragment = PrivacyTermsFragment.newInstance(mPrivacyTerms.section.get(0).panel.get(i));
                adapter.add(mPrivacyTermsFragment, mPrivacyTerms.section.get(0).panel.get(i).title);
            }
        }

        viewPager.setAdapter(adapter);
    }


    private void loadPanels(final ViewPager viewPager) {

        setLoading(true);

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<PrivacyTerms> call = apiService.getPrivacyGeneral();
        call.enqueue(new Callback<PrivacyTerms>() {
            @Override
            public void onResponse(@NonNull Call<PrivacyTerms> call, @NonNull Response<PrivacyTerms> response) {

                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));

                if (response.isSuccessful()) {

                    PrivacyTerms privacyTerms = response.body();

                    if (privacyTerms != null && privacyTerms.hasMessage()) {
                        showSnackBar(privacyTerms.getMessage(), TYPE_FAILURE);
                    } else {
                        mPrivacyTerms = privacyTerms;
                        newsPlainTextList.addAll(privacyTerms.newsPlainText);

                        setupViewPager(viewPager);
                        TabLayout tabLayout = findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(viewPager);
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

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mConstraintLayout, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
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

    @Override
    public boolean onCreateOptionsMenu(@Nullable Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lgpd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ((Objects.requireNonNull(item).getItemId())) {
            case R.id.menu_news:
                Intent intent = new Intent(this, PrivacyTermsNewsActivity.class);
                intent.putParcelableArrayListExtra("News", (ArrayList<? extends Parcelable>) newsPlainTextList);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
