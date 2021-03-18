package br.com.avanade.fahz.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import br.com.avanade.fahz.BuildConfig;
import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.BenefitsControlActivity;
import br.com.avanade.fahz.activities.lgpd.ControlOfPrivacyActivity;
import br.com.avanade.fahz.activities.lgpd.TermsControlActivity;
import br.com.avanade.fahz.activities.request.ListRequestActivity;
import br.com.avanade.fahz.activities.token.InsertTokenActivity;
import br.com.avanade.fahz.activities.token.RequestTokenActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.ImageObservable;
import br.com.avanade.fahz.model.ProfileImage;
import br.com.avanade.fahz.model.SchoolSuppliesCanRequest;
import br.com.avanade.fahz.model.ValidateToken;
import br.com.avanade.fahz.model.dashboard.DashboardItem;
import br.com.avanade.fahz.model.response.ValidateTokenResponse;
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

import static android.view.View.GONE;
import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;
import static br.com.avanade.fahz.util.Constants.CPF_EDIT_DEPENDENT;
import static br.com.avanade.fahz.util.Constants.ENVIRONMENTPRD;
import static br.com.avanade.fahz.util.Constants.TOKEN_NEVER_DID;
import static br.com.avanade.fahz.util.Constants.TOKEN_ON_FIVE_MINUTES;
import static br.com.avanade.fahz.util.Constants.TOKEN_SUCESS;
import static br.com.avanade.fahz.util.Constants.TOKEN_VALID_OUT_FIVE_MINUTES;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;
import static br.com.avanade.fahz.util.Constants.VIEW_EDIT_DEPENDENT;

public class MainActivity extends NavDrawerBaseActivity implements Observer {

    @BindView(R.id.btnMessage)
    ImageButton btnMessage;
    @BindView(R.id.lblMessages)
    TextView lblMessage;
    @BindView(R.id.lblQtdMessage)
    TextView lblQtdMessage;
    @BindView(R.id.imgUserDashboard)
    ImageView imgUserDashboard;
    @BindView(R.id.lbHello)
    TextView lblHello;
    @BindView(R.id.lbCPF)
    TextView lblCPF;
    @BindView(R.id.lbCompany)
    TextView lblCompany;

    @BindView(R.id.btnProfile)
    ImageButton btnProfile;
    @BindView(R.id.lblPersonal)
    TextView lblPersonal;

    @BindView(R.id.btnRequests)
    ImageButton btnRequests;
    Observable imageObservable;
    private ProgressDialog mProgressDialog;
    private View mView;
    private RecyclerView mrecyclerView;
    private boolean canLogin;
    public static boolean showLgpd;
    Context mContext;

    @Override
    protected void onResume() {
        super.onResume();

        SessionManager manager = new SessionManager(this);

        if (manager.getToken() == null || manager.getToken().isEmpty()) {
            manager.logout();
        }

        validateToken();
        loadMessageList();

        cansearch = Boolean.parseBoolean(manager.getPreference(Constants.CAN_SEARCH_PICTURE));
        //ATUALIZACAO DE IMAGEM COM A JA SALVA
        if (!cansearch) {
            String input = manager.getPreference(Constants.BITMAP_PROFILE);
            byte[] decodedByte = Base64.decode(input, 0);
            Bitmap image = BitmapFactory
                    .decodeByteArray(decodedByte, 0, decodedByte.length);

            if (image != null) {
                imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), image);
                imageDrawable.setCircular(true);
                imageDrawable.setCornerRadius(Math.min(image.getWidth(), image.getHeight()) / 2.0f);

                imgUserDashboard.invalidate();
                imgUserDashboard.setImageDrawable(null);
                imgUserDashboard.setImageURI(null);
                imgUserDashboard.setImageDrawable(imageDrawable);
            }
        }

        //Validado se dependente caso seja não mostra mensagens
        if(FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
            btnMessage.setVisibility(View.GONE);
            lblMessage.setVisibility(View.GONE);
            lblQtdMessage.setVisibility(View.GONE);

            btnProfile.setVisibility(View.VISIBLE);
            lblPersonal.setVisibility(View.VISIBLE);
        }
        else {
            btnMessage.setVisibility(View.VISIBLE);
            lblMessage.setVisibility(View.VISIBLE);
            lblQtdMessage.setVisibility(View.VISIBLE);

            btnProfile.setVisibility(View.GONE);
            lblPersonal.setVisibility(View.GONE);
        }

        //10578 - Mostrar o Warning uma vez ao dia
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date currentDate = DateEditText.parseToCalendar(df.format(Calendar.getInstance().getTime())).getTime();
        Date pastDate = null;

        if(!manager.getPreference(Constants.SHOW_WARNING_VERSION).equals(""))
            pastDate = DateEditText.parseToCalendar(manager.getPreference(Constants.SHOW_WARNING_VERSION)).getTime();

        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH && (pastDate == null || currentDate.after(pastDate))) {
            Utils.showSimpleDialog(this.getString(R.string.dialog_title), this.getString(R.string.warningversion), null, this, null);

            manager.createPreference(Constants.SHOW_WARNING_VERSION, df.format(currentDate));
        }
    }

    private void validateToken()
    {
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = apiService.validateTokenStatus(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        showSnackBar(responseCommit.messageIdentifier, Constants.TYPE_FAILURE);
                    } else {
                        ValidateTokenResponse objectResponse = new Gson().fromJson((response.body().getAsJsonObject()), ValidateTokenResponse.class);

                        ValidateToken tokenResponse = objectResponse.result;
                        boolean toValidate = false;
                        boolean toCreate = false;
                        switch(tokenResponse.getStatusTokenValidation()) {
                            case TOKEN_NEVER_DID:
                                toCreate = true;
                                break;
                            case TOKEN_SUCESS:
                                if (showLgpd && !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Reintegrated)) {
                                    if (!checkPoliciesEndTerms) {
                                        checkPoliciesEndTerms = true;
                                        checkPendingPolicies();
                                    }
                                /*} else {
                                    validateTermOfUse();*/
                                }
                                break;
                            case TOKEN_ON_FIVE_MINUTES:
                            case TOKEN_VALID_OUT_FIVE_MINUTES:
                                toValidate = true;
                                break;
                        }

                        if(toCreate)
                        {
                            Intent intent = new Intent(getBaseContext(), RequestTokenActivity.class);
                            intent.putExtra(Constants.JSON_VALIDATE_TOKEN, new Gson().toJson(tokenResponse));
                            startActivity(intent);
                            finish();
                        }
                        else if(toValidate)
                        {
                            Intent intent = new Intent(getBaseContext(), InsertTokenActivity.class);
                            intent.putExtra(Constants.JSON_VALIDATE_TOKEN, new Gson().toJson(tokenResponse));
                            startActivity(intent);
                            finish();
                        }
                    }

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    } finally {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), Constants.TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), Constants.TYPE_FAILURE);
                else
                    Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setImageHeaderVisibility(true);
        setMenuVisible(true);

        mProgressDialog = new ProgressDialog(this);
        imageObservable = ImageObservable.getInstance();
        imageObservable.addObserver(this);

        mView = findViewById(R.id.main_container);

        final LinearLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mrecyclerView = findViewById(R.id.recyclerViewMenu);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(layoutManager);

        loadMessageList();
        getFeatureFlag();
        //getAnamnesisSession();


        FahzApplication fahzApplication = FahzApplication.getInstance();
        if(!fahzApplication.getFahzClaims().getCPF().equals(""))
            FirebaseMessaging.getInstance().subscribeToTopic(fahzApplication.getFahzClaims().getCPF());

        String pushNotificationActivity = getIntent().getStringExtra(Constants.PUSH_NOTIFICATION_ACTIVITY);
        if(pushNotificationActivity != null && pushNotificationActivity.equals(Constants.LIST_MESSAGE_ACTIVITY))
        {
            seeMessages(null);
        }

        setupUi(fahzApplication);
        new GetDataSync().execute();
    }

    //METODO PARA CONFIRUAÇÂO DE TELA
    private void setupUi(FahzApplication fahzApplication) {
        String hello = "Olá, " + fahzApplication.getFahzClaims().getName().split(" ")[0];
        lblHello.setText(hello);
        String cpf = Utils.formatValueMask(fahzApplication.getFahzClaims().getCPF(), "###.###.###-##", ".-");
        lblCPF.setText(cpf);
        lblCompany.setText(FahzApplication.getInstance().getFahzClaims().getCompany());

        getProfilePictureMain(cpf);
    }

    public void getProfilePictureMain(String cpf) {
        setLoading(true, getString(R.string.loading_searching));

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<ProfileImage> call = apiService.queryProfileImage(new CPFInBody(cpf));
        call.enqueue(new Callback<ProfileImage>() {
            @Override
            public void onResponse(@NonNull Call<ProfileImage> call, @NonNull Response<ProfileImage> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                canRequestSchoolSupplies();
            }

            @Override
            public void onFailure(@NonNull Call<ProfileImage> call, @NonNull Throwable t) {
                LogUtils.error(TAG, t);
                imgUser.setImageResource(R.drawable.user);
            }
        });
    }



    private void canRequestSchoolSupplies() {

        APIService apiService = ServiceGenerator.createService(APIService.class);

        Call<JsonElement> call = apiService.checkCanRequestBenefit(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        setLoading(false, "");
                    } else {
                        SchoolSuppliesCanRequest request = new Gson().fromJson((response.body().getAsJsonObject()), SchoolSuppliesCanRequest.class);
                        boolean canViewSchoolSupplies = false;
                        if (request.getUserRequestSchool().getIsCard() || request.getUserRequestSchool().getIsRefund()) {
                            canViewSchoolSupplies = true;

                            SessionManager manager = new SessionManager(MainActivity.this);
                            manager.createPreference(Constants.IDPLAN_SCHOOL_SUPPLIES, String.valueOf(request.getUserRequestSchool().getPlanId()));
                            manager.createPreference(Constants.SCHOOL_SUPPLIES_REFUND, String.valueOf(request.getUserRequestSchool().getIsRefund()));
                            manager.createPreference(Constants.ALREADY_HAS_PLAN, String.valueOf(request.getUserRequestSchool().getAlreayHasCardBenefit()));
                            manager.createPreference(Constants.ALREADY_HAS_REFUND, String.valueOf(request.getUserRequestSchool().getAlreayHasRefundBenefit()));
                            manager.createPreference(Constants.HAS_BANK_DATA, String.valueOf(request.getUserRequestSchool().getHolderHasBankData()));
                        }

                        DashboardItem item = new DashboardItem();
                        MenuItemAdapter adapter = new MenuItemAdapter(DashboardItem.ListDashBoardItems(canViewSchoolSupplies, showLgpd));
                        mrecyclerView.setAdapter(adapter);
                    }

                    setLoading(false, "");

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    } finally {
                        setLoading(false, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
                setLoading(false, "");
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof ImageObservable) {
            ImageObservable userDataRepository = (ImageObservable) observable;
            imgUserDashboard.invalidate();
            imgUserDashboard.setImageDrawable(null);
            imgUserDashboard.setImageURI(null);
            imgUserDashboard.setImageDrawable(userDataRepository.GetProfileImageDrawable());
        }
    }

    private void loadMessageList() {
        APIService apiService = ServiceGenerator.createService(APIService.class);

        Call<JsonElement> call = apiService.getUnreadMessageCount(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    try {
                        JSONObject element = new JSONObject(response.body().toString());
                        String value = element.getString("count");
                        if (value.equals("0"))
                            lblQtdMessage.setVisibility(GONE);
                        else if(!FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role))
                            lblQtdMessage.setVisibility(View.VISIBLE);

                        lblQtdMessage.setText(value);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    }

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
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

/*
    private void getAnamnesisSession() {
        APIServiceAnamnesis apiService = createAnamnesisServiceNoToken(APIServiceAnamnesis.class);
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        userAnamnesisSession.setUserCPF(cpf);
        Call<LoginAnamnesisResponse> call = apiService.loginWithPendencies(new LoginAnamnesis(cpf, this));
        call.enqueue(new Callback<LoginAnamnesisResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginAnamnesisResponse> call, @NonNull Response<LoginAnamnesisResponse> response) {
                LoginAnamnesisResponse anamnesisResponse = response.body();
                if (response.isSuccessful()) {
                    assert anamnesisResponse != null;
                    String accessToken = anamnesisResponse.getToken();
                    int environment = anamnesisResponse.getEnvironment();
                    if (accessToken != null) {
                        userAnamnesisSession.setEnvironment(environment);
                        userAnamnesisSession.setToken(accessToken);
                        userAnamnesisSession.setPendencies(anamnesisResponse.getPendencies());
                        if (mrecyclerView.getAdapter() != null) {
                            mrecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    }
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
            }

            @Override
            public void onFailure(@NonNull Call<LoginAnamnesisResponse> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }
*/

    @Override
    protected void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void setLoading(Boolean loading, String text) {
        if (loading) {
            if (mProgressDialog != null) {
                mProgressDialog.setMessage(text);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        } else {
            try {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } catch (Exception ex) {
                LogUtils.error("MainActivity - Loading", ex);
            }
        }
    }

    private void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mView, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);

        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageObservable.deleteObserver(this);

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @OnClick(R.id.btnMessage)
    public void seeMessages(View view) {
        Intent intent = new Intent(this, BaseMessageActivity.class);
        intent.putExtra(Constants.BASE_MESSAGE_CONTROL, BaseMessageActivity.BaseMessageFragment.LISTMESSAGE);
        startActivity(intent);
    }

    @OnClick(R.id.btnProfile)
    public void seeProfile(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(CPF_EDIT_DEPENDENT, FahzApplication.getInstance().getFahzClaims().getCPF());
        bundle.putBoolean(VIEW_EDIT_DEPENDENT, true);
        bundle.putBoolean(Constants.IS_REACTIVATE, false);
        Intent intent = new Intent(this, DependentActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @OnClick(R.id.btnRequests)
    public void seeRequets(View view) {
        Intent intent = new Intent(this, ListRequestActivity.class);
        startActivity(intent);
    }

    public void buttonClick(int buttonClicked) {
        Intent intent;

        switch (buttonClicked) {
            case R.string.health_plan:
                intent = new Intent(this, BenefitsControlActivity.class);
                intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.HEALTHPLAN);
                startActivity(intent);
                break;
            case R.string.dental_plan:
                intent = new Intent(this, BenefitsControlActivity.class);
                intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.DENTALPLAN);
                startActivity(intent);
                break;
            case R.string.pharmacy:
                intent = new Intent(this, BenefitsControlActivity.class);
                intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.PHARMACY);
                startActivity(intent);
                break;
            case R.string.scholarship:
                intent = new Intent(this, BenefitsControlActivity.class);
                intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.SCHOLARSHIP);
                startActivity(intent);
                break;
            case R.string.school_supplies:
                intent = new Intent(this, BenefitsControlActivity.class);
                intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.SCHOOLSUPPLIES);
                startActivity(intent);
                break;
            case R.string.toy:
                intent = new Intent(this, BenefitsControlActivity.class);
                intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.TOY);
                startActivity(intent);
                break;
            case R.string.christmas:
                intent = new Intent(this, BenefitsControlActivity.class);
                intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.CHRISTMAS);
                startActivity(intent);
                break;
            case R.string.doubts:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.video)));
                startActivity(browserIntent);
                break;
            case R.string.questionsandanswers:
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.faq)));
                startActivity(browserIntent2);
                break;
            case R.string.title_privacy_control:
                intent = new Intent(this, ControlOfPrivacyActivity.class);
                startActivity(intent);
                break;
            case R.string.title_term_service:
                intent = new Intent(this, TermsControlActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void logoutUser() {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.logout();
    }

    public class GetDataSync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getData();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (canLogin) {

            } else {
                if(this!=null) {
                    int resID = getResources().getIdentifier("MSG413", "string", getPackageName());
                    String message = getResources().getString(resID);
                    Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), message, "OK", MainActivity.this,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    logoutUser();
                                }
                            });
                }
                else
                    logoutUser();

            }
        }

        private void getData() throws IOException, JSONException {
            if (BuildConfig.CURRENT_ENVIRONMENT.equals(ENVIRONMENTPRD)) {
                JSONObject json = readJsonFromUrl(BuildConfig.CHECK_VERSION);
                try {
                    String response = json.getString("Version");
                    Double version = new Double(response);

                    Double versionName = new Double(BuildConfig.VERSION_NAME);

                    canLogin = versionName >= version;

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            } else
                canLogin = true;
        }

        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }
    }

    private class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.DashboardItemViewHolder> {

        private List<DashboardItem> mDashboardItems;

        MenuItemAdapter(List<DashboardItem> list) {
            mDashboardItems = list;
        }

        void updateData(List<DashboardItem> dependentList) {
            mDashboardItems.clear();
            mDashboardItems.addAll(dependentList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public DashboardItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_item_layout,
                    parent, false);
            return new DashboardItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DashboardItemViewHolder holder, int position) {
            final DashboardItem item = mDashboardItems.get(position);

            holder.dashboardLayout.setVisibility(View.VISIBLE);

            holder.textAction.setText(getString(item.nameMenu));
            holder.button.setImageResource(item.drawableMenu);
            holder.button.setTag(getString(item.nameMenu));

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttonClick(item.nameMenu);
                }
            });

            if (item.nameMenu == R.string.health_plan) {
                holder.lblQtdNotification.setVisibility(userAnamnesisSession.getPendencies() > 0 ? View.VISIBLE : View.INVISIBLE);
                holder.lblQtdNotification.setText(String.valueOf(userAnamnesisSession.getPendencies()));
            }
        }

        @Override
        public int getItemCount() {
            return mDashboardItems.size();
        }

        class DashboardItemViewHolder extends RecyclerView.ViewHolder {
            private TextView textAction;
            private TextView lblQtdNotification;
            private ImageButton button;
            private ConstraintLayout dashboardLayout;

            DashboardItemViewHolder(View itemView) {
                super(itemView);

                dashboardLayout = itemView.findViewById(R.id.layout_dashboard);
                textAction = itemView.findViewById(R.id.text);
                lblQtdNotification = itemView.findViewById(R.id.lblQtdNotification);
                button = itemView.findViewById(R.id.btn);
            }
        }
    }

    private void getFeatureFlag() {

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = apiService.getFeatureFlagLgpd(Constants.FEATURE_FLAG_PLATFORM);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
                        setLoading(false, "");
                    } else {

                        showLgpd = Boolean.parseBoolean(String.valueOf(response.body().getAsJsonObject().get("enabled")));

                    }

                    setLoading(false, "");

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    } finally {
                        setLoading(false, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
                setLoading(false, "");
            }
        });
    }
}
