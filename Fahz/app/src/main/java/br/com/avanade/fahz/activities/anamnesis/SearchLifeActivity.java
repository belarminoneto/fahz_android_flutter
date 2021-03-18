package br.com.avanade.fahz.activities.anamnesis;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.avanade.fahz.Adapter.LifeAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.interfaces.OnSessionListener;
import br.com.avanade.fahz.model.anamnesisModel.LifeAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.SearchLifeRequest;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.UNAUTHORIZED_ACCESS;

public class SearchLifeActivity extends BaseAnamnesisActivity implements View.OnClickListener {

    @BindView(R.id.edtFilter)
    EditText mEdtFilter;

    @BindView(R.id.rvLife)
    RecyclerView mRvLife;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    LifeAdapter mAdapter;
    List<LifeAnamnesis> lifeAnamnesisList;
    private Timer searchTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setupUi();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_search_life;
    }

    private void setupUi() {
        mAdapter = new LifeAdapter(this);
        mRvLife.setAdapter(mAdapter);
        mRvLife.setLayoutManager(new LinearLayoutManager(this));

        mEdtFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.length() > 3) {
                    restartTimer(s.toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    void changeProgressBarVisibility(final boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(visible ? VISIBLE : INVISIBLE);
            }
        });
    }

    @OnClick(R.id.imgBack)
    public void onBack() {
        onBackPressed();
    }

    void restartTimer(final String searchedText) {
        if (searchTimer != null) {
            searchTimer.cancel();
            searchTimer.purge();
        }
        searchTimer = new Timer();
        searchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                searchLife(searchedText);
            }
        }, 500);
    }

    void searchLife(final String searchedText) {
        changeProgressBarVisibility(true);
        Call<List<LifeAnamnesis>> call = mApiService.searchLifeByFilter(new SearchLifeRequest(searchedText));
        call.enqueue(new Callback<List<LifeAnamnesis>>() {
            @Override
            public void onResponse(@NonNull Call<List<LifeAnamnesis>> call, @NonNull Response<List<LifeAnamnesis>> response) {
                if (response.raw().code() == UNAUTHORIZED_ACCESS) {
                    getSession(new OnSessionListener() {
                        @Override
                        public void onSessionSuccess() {
                            searchLife(searchedText);
                        }
                    });
                } else {
                    List<LifeAnamnesis> list = response.body();
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    lifeAnamnesisList = list;
                    mAdapter.setItems(list);
                }
                changeProgressBarVisibility(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<LifeAnamnesis>> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())));
                else if (t instanceof UnknownHostException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())));
                else
                    showAlert(t.getMessage());

                changeProgressBarVisibility(false);
            }
        });
    }

    @Override
    public void onClick(@NonNull View v) {
        int itemPosition = mRvLife.getChildLayoutPosition(v);
        String cpf = lifeAnamnesisList.get(itemPosition).getCpf();
        userAnamnesisSession.setUserCPF(cpf);
        Intent intent = new Intent(this, FamilyTreeAnamnesisActivity.class);
        startActivity(intent);
    }
}
