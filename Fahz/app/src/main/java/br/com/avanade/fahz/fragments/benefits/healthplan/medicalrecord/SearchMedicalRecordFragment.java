package br.com.avanade.fahz.fragments.benefits.healthplan.medicalrecord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.anamnesis.ViewAnswersActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.medicalrecord.ClinicalSummaryActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.medicalrecord.MedicalRecordListActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.interfaces.ISimpleDialogOkAction;
import br.com.avanade.fahz.interfaces.OnSessionListener;
import br.com.avanade.fahz.model.anamnesisModel.LifeStatusAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.LoginAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.LoginAnamnesisResponse;
import br.com.avanade.fahz.model.anamnesisModel.SearchFamilyTreeRequest;
import br.com.avanade.fahz.model.life.ListHolderAndDependentsBody;
import br.com.avanade.fahz.model.response.LifesAndDependents;
import br.com.avanade.fahz.model.response.SmallLife;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.APIServiceAnamnesis;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.AnamnesisSession;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.network.ServiceGeneratorAnamnesis.createAnamnesisService;
import static br.com.avanade.fahz.network.ServiceGeneratorAnamnesis.createAnamnesisServiceNoToken;
import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.ID_TYPE_QUEST;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.UNAUTHORIZED_ACCESS;

public class SearchMedicalRecordFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    @BindView(R.id.content_cancel_benefit)
    LinearLayout mContentCancelBenefit;
    @BindView(R.id.spBeneficiaryList)
    Spinner spBeneficiaryList;
    @BindView(R.id.spTypeOfRecordsList)
    Spinner spTypeOfRecordsList;

    SessionManager sessionManager;
    APIServiceAnamnesis mApiService;

    private ProgressDialog mProgressDialog;
    private List<SmallLife> lifesList = new ArrayList<>();
    private List<String> lifesStr = new ArrayList<>();
    private SmallLife smallLife = new SmallLife();
    List<LifeStatusAnamnesis> lifeAnamnesisList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_record, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        setupUi();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    private void setupUi() {
        ((BaseHealthControlActivity) getActivity()).toolbarTitle.setText(getActivity().getString(R.string.medical_record));
        populateListLifesAndDependents();
    }

    //POPULA O COMBO COM TITULAR E DEPENDENTES
    private void populateListLifesAndDependents() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<LifesAndDependents> call = mAPIService.listHolderAndDependents(new ListHolderAndDependentsBody(FahzApplication.getInstance().getFahzClaims().getCPF(), false));
            call.enqueue(new Callback<LifesAndDependents>() {
                @Override
                public void onResponse(@NonNull Call<LifesAndDependents> call, @NonNull Response<LifesAndDependents> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        LifesAndDependents responseLifes = response.body();
                        lifesList = new ArrayList<>();
                        lifesStr = new ArrayList<>();
                        lifesStr.add("Selecione");

                        if (responseLifes.getLifes().size() > 0) {

                            for (SmallLife life : responseLifes.getLifes()) {
                                if (life.getCpf() != null) {
                                    lifesList.add(life);
                                    lifesStr.add(life.getName());
                                }
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, lifesStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spBeneficiaryList.setAdapter(adapter);

                        if (lifesStr != null && lifesStr.size() > 0)
                            spBeneficiaryList.setSelection(lifesStr.indexOf("Selecione"));

                        if (lifesList.size() == 1) {
                            spBeneficiaryList.setSelection(1);
                            spBeneficiaryList.setEnabled(false);
                        }

                    } else if (response.code() == 404) {
                        showSnackBar(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                    setLoading(false, "");
                }

                @Override
                public void onFailure(@NonNull Call<LifesAndDependents> call, @NonNull Throwable t) {
                    setLoading(false, "");
                    if (getActivity() != null) {
                        if (t instanceof SocketTimeoutException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else
                            showSnackBar(t.getMessage(), TYPE_FAILURE);
                    }
                }
            });
        }
    }

    //BOTÃO PARA CHAMAR CANCELAR PLANO
    @OnClick(R.id.btnSearch)
    public void submit(View view) {

        if (lifesStr.indexOf(spBeneficiaryList.getSelectedItem()) == 0)
            showSnackBar("Selecione uma vida.", TYPE_FAILURE);
        else {
            smallLife = lifesList.get(lifesStr.indexOf(spBeneficiaryList.getSelectedItem()) - 1);

            if (spTypeOfRecordsList.getSelectedItemPosition() == 1) {

                try {

                    String dt = DateEditText.parseToDateRequest(smallLife.getDateOfBirth());
                    Integer age = DateEditText.getAge(dt);

                    userAnamnesisSession.setLifeCPF(smallLife.getCpf());
                    userAnamnesisSession.setSituation(FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution());
                    userAnamnesisSession.setAge(age);

                    LifeStatusAnamnesis lifeStatus = new LifeStatusAnamnesis();
                    lifeStatus.setAge(age);
                    lifeStatus.setStatus(FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution());
                    lifeStatus.setCpf(smallLife.getCpf());
                    AnamnesisSession.lifeSession = lifeStatus;

                    getFamilyTree(FahzApplication.getInstance().getFahzClaims().getCPF());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (spTypeOfRecordsList.getSelectedItemPosition() == 2) {
                Intent intent = new Intent(getContext(), MedicalRecordListActivity.class);
                intent.putExtra("smallLife", smallLife);
                startActivity(intent);
            } else if (spTypeOfRecordsList.getSelectedItemPosition() == 3) {
                Intent intent = new Intent(getContext(), ClinicalSummaryActivity.class);
                intent.putExtra("smallLife", smallLife);
                startActivity(intent);
            } else {
                showSnackBar("Selecione um Tipo de Utilização.", TYPE_FAILURE);
            }
        }
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContentCancelBenefit, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContentCancelBenefit, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    void getSession(final OnSessionListener callback) {
        setLoading(true, getString(R.string.loading_searching));
        mApiService = createAnamnesisServiceNoToken(APIServiceAnamnesis.class);
        Call<LoginAnamnesisResponse> call = mApiService.loginWithoutPendencies(new LoginAnamnesis(FahzApplication.getInstance().getFahzClaims().getCPF(), this.getContext()));
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
                        mApiService = createAnamnesisService(APIServiceAnamnesis.class);
                        callback.onSessionSuccess();
                    } else {
                        showAlert(getString(R.string.problemServer), () -> getActivity().finish());
                    }
                } else {
                    showAlert(getString(R.string.problemServer), () -> getActivity().finish());
                }
                setLoading(false, "");
            }

            @Override
            public void onFailure(@Nullable Call<LoginAnamnesisResponse> call, @NonNull Throwable t) {
                setLoading(false, "");
                if (t instanceof SocketTimeoutException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), () -> getActivity().finish());
                else if (t instanceof UnknownHostException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), () -> getActivity().finish());
                else {
                    LogUtils.error(getActivity().getLocalClassName(), t);
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), () -> getActivity().finish());
                }
            }
        });
    }

    void showAlert(String msg) {
        Utils.showSimpleDialog(getString(R.string.dialog_title), msg, getString(R.string.close), this.getContext(), null);
    }

    void showAlert(String msg, @Nullable ISimpleDialogOkAction simpleDialogOkAction) {
        Utils.showSimpleDialog(getString(R.string.dialog_title), msg, getString(R.string.close), getContext(), simpleDialogOkAction);
    }

    void getFamilyTree(final String cpf) {
        setLoading(true, getString(R.string.loading_searching));
        mApiService = createAnamnesisService(APIServiceAnamnesis.class);
        Call<List<LifeStatusAnamnesis>> call = mApiService.searchForFamilyTreeByCPF(new SearchFamilyTreeRequest(cpf));
        call.enqueue(new Callback<List<LifeStatusAnamnesis>>() {
            @Override
            public void onResponse(@NonNull Call<List<LifeStatusAnamnesis>> call, @NonNull Response<List<LifeStatusAnamnesis>> response) {
                if (response.raw().code() == UNAUTHORIZED_ACCESS) {
                    getSession(() -> getFamilyTree(cpf));
                } else {
                    List<LifeStatusAnamnesis> body = response.body();
                    if (body == null) {
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            String error = jObjError.getString("Message");
                            showAlert(error);
                        } catch (Exception e) {
                            showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), () -> getActivity().finish());
                            LogUtils.error(getActivity().getLocalClassName(), e);
                        }
                    } else {
                        lifeAnamnesisList = body;

                        if (lifeAnamnesisList.size()  == 0) {
                            Utils.showSimpleDialog(getString(R.string.dialog_title), getString(R.string.no_found_anamnese), getString(R.string.close), getActivity(), null);
                            return;
                        }

                        boolean hasAnamnese = false;
                        for (int i = 0; i < lifeAnamnesisList.size(); i++) {

                            if(lifeAnamnesisList.get(i).getPercentage() == 100.0 &&
                                    lifeAnamnesisList.get(i).getIdTypeQuestionnaire() == 1 &&
                                    lifeAnamnesisList.get(i).getCpf().equals(smallLife.getCpf()) ) {

                                hasAnamnese = true;
                            }
                        }

                        if(!hasAnamnese)
                            Utils.showSimpleDialog(getString(R.string.dialog_title), getString(R.string.no_found_anamnese), getString(R.string.close), getActivity(), null);
                        else{
                            Intent intent = new Intent(getContext(), ViewAnswersActivity.class);
                            intent.putExtra(ID_TYPE_QUEST, 1);
                            startActivity(intent);
                        }
                    }
                }
                setLoading(false, "");
            }

            @Override
            public void onFailure(@NonNull Call<List<LifeStatusAnamnesis>> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), () -> getActivity().finish());
                else if (t instanceof UnknownHostException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), () -> getActivity().finish());
                else {
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), () -> getActivity().finish());
                    LogUtils.error(getActivity().getLocalClassName(), t);
                }

                setLoading(false, "");
            }
        });
    }
}