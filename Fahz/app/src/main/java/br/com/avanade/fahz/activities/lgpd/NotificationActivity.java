package br.com.avanade.fahz.activities.lgpd;

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
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.Adapter.lgpd.CardViewNotificationAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.lgpdModel.Authorization;
import br.com.avanade.fahz.model.lgpdModel.NotificationAnswer;
import br.com.avanade.fahz.model.lgpdModel.NotificationAnswerResponse;
import br.com.avanade.fahz.model.lgpdModel.NotificationResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class NotificationActivity extends NavDrawerBaseActivity {

    private ProgressDialog mProgressDialog;

    @BindView(R.id.ConstraintLayoutNotification)
    ConstraintLayout mConstraintLayoutNotification;

    @BindView(R.id.textViewNotification)
    TextView mTextViewNotification;

    @BindView(R.id.recyclerViewNotification)
    RecyclerView mRecyclerViewNotification;

    CardViewNotificationAdapter mCardViewNotificationAdapter;

    private List<Authorization> mAuthorizationList = new ArrayList<>();
    private final List<NotificationAnswer> notificationAnswer = new ArrayList<>();

    private String cpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        toolbarTitle.setText(R.string.title_notification);

        ButterKnife.bind(this);

        mRecyclerViewNotification.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        mProgressDialog = new ProgressDialog(this);

        loadNotification();
    }

    private void loadNotification() {

        setLoading(true);

        cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<NotificationResponse> call = apiService.GetPermitions(new CPFInBody(cpf));
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    NotificationResponse notification = response.body();

                    if (notification != null && notification.hasMessage()) {

                        showSnackBar(notification.getMessage(), TYPE_FAILURE);

                    } else {

                        if (notification != null) {
                            Spanned sp = Html.fromHtml(notification.getNotification().text == null ? "" : notification.getNotification().text);
                            mTextViewNotification.setText(sp);

                            mAuthorizationList = notification.getNotification().authorizations;

                            if (mAuthorizationList.size() > 0) {
                                mCardViewNotificationAdapter = new CardViewNotificationAdapter(NotificationActivity.this, mAuthorizationList);
                                mRecyclerViewNotification.setAdapter(mCardViewNotificationAdapter);
                            }
                        }
                    }

                    setLoading(false);

                } else {
                    try {
                        if (response.errorBody() != null) {
                            String data = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(data);
                            String message = jObjError.getString("Message");
                            showSnackBar(message, TYPE_FAILURE);
                        }
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), TYPE_FAILURE);
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
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
        Snackbar snackbar = Snackbar.make(mConstraintLayoutNotification, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    @OnClick(R.id.save_button)
    public void sendMyNotifications(View view) {

        if (mCardViewNotificationAdapter != null && mCardViewNotificationAdapter.getItemCount() > 0) {

            notificationAnswer.clear();

            for (int i = 0; i < mCardViewNotificationAdapter.permitionList.size(); i++) {

                for (int j = 0; j < mCardViewNotificationAdapter.permitionList.get(i).options.size(); j++) {
                    NotificationAnswer answers = new NotificationAnswer();
                    answers.cpf = cpf;
                    answers.idAutorization = mCardViewNotificationAdapter.permitionList.get(i).id;
                    answers.idOption = mCardViewNotificationAdapter.permitionList.get(i).options.get(j).id;
                    answers.answer = (mCardViewNotificationAdapter.permitionList.get(i).options.get(j).answerUser) ? 1 : 0;
                    notificationAnswer.add(answers);
                }
            }

            saveNotification(notificationAnswer);

        } else {
            showSnackBar(getResources().getString(getResources().getIdentifier("MSG505", "string", getPackageName())), TYPE_FAILURE);
        }

    }

    private void saveNotification(List<NotificationAnswer> notificationAnswer) {

        if (notificationAnswer.size() > 0) {

            setLoading(true);

            NotificationAnswerResponse notificationAnswerResponse = new NotificationAnswerResponse();
            notificationAnswerResponse.notificationAnswer = notificationAnswer;

            APIService apiService = ServiceGenerator.createService(APIService.class);
            Call<JsonElement> call = apiService.savePermitions(notificationAnswerResponse);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        if (!response.body().getAsJsonObject().has("messageIdentifier")) {

                            CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                            showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);

                        } else {

                            CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                            showSnackBar(responseCommit.messageIdentifier, TYPE_SUCCESS);

                        }

                        setLoading(false);

                    } else {
                        try {
                            if (response.errorBody() != null) {
                                String data = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(data);
                                String message = jObjError.getString("Message");
                                showSnackBar(message, TYPE_FAILURE);
                            }
                        } catch (Exception ex) {
                            showSnackBar(ex.getMessage(), TYPE_FAILURE);
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
        else{
            showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
        }
    }
}