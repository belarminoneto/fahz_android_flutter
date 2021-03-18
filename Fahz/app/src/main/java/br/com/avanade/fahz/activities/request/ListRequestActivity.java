package br.com.avanade.fahz.activities.request;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.fragments.requests.RequestDetailsFragment;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.Request;
import br.com.avanade.fahz.model.history.GetRequestBody;
import br.com.avanade.fahz.model.response.RequestResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.NUMBER_OF_SHOWN_REQUESTS;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ListRequestActivity extends NavDrawerBaseActivity {


    private static final String REQUEST_DETAILS_FRAGMENT_TAG = "requestDetailsFragment";
    @BindView(R.id.recyclerViewRequest)
    RecyclerView mRequestRecyclerView;
    SessionManager sessionManager;
    @BindView(R.id.content_request)
    LinearLayout mRequestContainer;
    @BindView(R.id.status_spinner)
    Spinner mStatusSpinner;
    RequestListAdapter adapter;
    private ProgressDialog mProgressDialog;
    private int skip = 1;
    private YoYo.AnimatorCallback mFragmentAnimationCloseListener = new YoYo.AnimatorCallback() {
        @Override
        public void call(Animator animator) {
            final FragmentManager manager = getSupportFragmentManager();
            final RequestDetailsFragment fragment = (RequestDetailsFragment) manager.findFragmentByTag(REQUEST_DETAILS_FRAGMENT_TAG);
            if (fragment != null) {
                manager.beginTransaction().remove(fragment).commit();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_request);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ButterKnife.bind(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        toolbarTitle.setText(getString(R.string.label_request));

        sessionManager = new SessionManager(getApplicationContext());
        mProgressDialog = new ProgressDialog(this);

        View mVIew = findViewById(R.id.content_request);
        mRequestRecyclerView.setHasFixedSize(true);
        mRequestRecyclerView.setLayoutManager(layoutManager);

        setImageHeaderVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    //CASO A SELECAO NO COMBO DE STATUS MUDE
    @OnItemSelected(R.id.status_spinner)
    void onItemSelected(int position) {
        skip = 1;
        adapter = null;
        loadRequests(position, skip);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //loadDependentList(searchModeActive);
    }

    private void loadRequests(int status, int skip) {
        setLoading(true, getString(R.string.search_requests));

        APIService apiService = ServiceGenerator.createService(APIService.class);
        String mCPF = FahzApplication.getInstance().getFahzClaims().getCPF();

        Call<JsonElement> call = apiService.getRequests(new GetRequestBody(mCPF, status, NUMBER_OF_SHOWN_REQUESTS, skip));
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
                            RequestResponse requests = new Gson().fromJson((response.body().getAsJsonObject()), RequestResponse.class);
                            showRequestList(requests.getRegisters());
                        }
                    }

                } else {
                    try {
                        assert response.errorBody() != null;
                        String data = response.errorBody().string();
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

                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
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

    private void showRequestList(final List<Request> list) {
        if (adapter == null) {
            adapter = new RequestListAdapter(list, this, mStatusSpinner.getSelectedItem().toString());
            adapter.setRowListener(new RequestListAdapter.OnRowClickListener() {
                @Override
                public void onRowClicked(Request request, int position) {
                    RequestDetailsFragment fragment = RequestDetailsFragment.newInstance(request);
                    fragment.setOnFinishAnimationListener(mFragmentAnimationCloseListener);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_fragment, fragment, REQUEST_DETAILS_FRAGMENT_TAG)
                            .commit();
                }
            });
            mRequestRecyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(list);
        }

        adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                if (list.size() > 0) {
                    skip++;
                    loadRequests(mStatusSpinner.getSelectedItemPosition(), skip);
                }
            }
        });
    }


    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mRequestContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);

        snackbar.show();
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mRequestContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpened()) {
            super.onBackPressed();
        } else {
            final FragmentManager manager = getSupportFragmentManager();
            final RequestDetailsFragment fragment = (RequestDetailsFragment) manager.findFragmentByTag(REQUEST_DETAILS_FRAGMENT_TAG);
            if (fragment != null) {
                fragment.startCloseAnimation();
            } else {
                super.onBackPressed();
            }
        }
    }

    public interface OnBottomReachedListener {

        void onBottomReached(int position);

    }
}
