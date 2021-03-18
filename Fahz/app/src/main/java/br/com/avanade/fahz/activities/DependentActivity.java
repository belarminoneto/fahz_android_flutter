package br.com.avanade.fahz.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.token.InformativeTokenActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.controls.PhoneEditText;
import br.com.avanade.fahz.dialogs.CheckPendingTermsActivity;
import br.com.avanade.fahz.dialogs.TermsOfUseActivity;
import br.com.avanade.fahz.model.AccountHolder;
import br.com.avanade.fahz.model.AddDependentRequest;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CivilState;
import br.com.avanade.fahz.model.CivilStatusRequest;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.Dependent;
import br.com.avanade.fahz.model.DependentHolder;
import br.com.avanade.fahz.model.DependentLife;
import br.com.avanade.fahz.model.DependentRelationship;
import br.com.avanade.fahz.model.DependentRequest;
import br.com.avanade.fahz.model.DependentResponse;
import br.com.avanade.fahz.model.Kinship;
import br.com.avanade.fahz.model.LifeBenefits;
import br.com.avanade.fahz.model.SavedSerproResponse;
import br.com.avanade.fahz.model.SerproResponse;
import br.com.avanade.fahz.model.Status;
import br.com.avanade.fahz.model.SystemBehaviorModel;
import br.com.avanade.fahz.model.ValidateTokenRequest;
import br.com.avanade.fahz.model.lgpdModel.GetListOfTerms;
import br.com.avanade.fahz.model.lgpdModel.PoliciesAndTerm;
import br.com.avanade.fahz.model.life.DependentHolderBody;
import br.com.avanade.fahz.model.response.CivilStateListResponse;
import br.com.avanade.fahz.model.response.KinshipListResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.SystemBehavior;
import br.com.avanade.fahz.util.Utils;
import br.com.avanade.fahz.util.ValidateCPF;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.COMPANION;
import static br.com.avanade.fahz.util.Constants.CPF_EDIT_DEPENDENT;
import static br.com.avanade.fahz.util.Constants.DOCUMENT_FOR_RESULT;
import static br.com.avanade.fahz.util.Constants.FATHER;
import static br.com.avanade.fahz.util.Constants.INCLUSAO_FILHO_GUARDA_COMPARTILHADA;
import static br.com.avanade.fahz.util.Constants.INCLUSAO_FILHO_MAIOR_SEM_CART_TRAB;
import static br.com.avanade.fahz.util.Constants.INFORMATIVE_TOKEN_RESULT;
import static br.com.avanade.fahz.util.Constants.MASK_CPF;
import static br.com.avanade.fahz.util.Constants.MASK_DATE;
import static br.com.avanade.fahz.util.Constants.MOTHER;
import static br.com.avanade.fahz.util.Constants.SINGLE;
import static br.com.avanade.fahz.util.Constants.SON;
import static br.com.avanade.fahz.util.Constants.SPOUSE;
import static br.com.avanade.fahz.util.Constants.TERM_ACCEPT;
import static br.com.avanade.fahz.util.Constants.TUTOR;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;
import static br.com.avanade.fahz.util.Constants.VIEW_EDIT_DEPENDENT;

public class DependentActivity extends NavDrawerBaseActivity {

    @BindView(R.id.cpf_edit_text)
    TextInputEditText cpfEditText;
    @BindView(R.id.full_name_edit_text)
    TextInputEditText fullNameEditText;
    @BindView(R.id.datebirth_edit_text)
    TextInputEditText birthDateEditText;
    @BindView(R.id.name_mother_edit_text)
    TextInputEditText fullNameMotherEditText;
    @BindView(R.id.marriage_edit_text)
    TextInputEditText marriageEditText;
    @BindView(R.id.cpf_text_input)
    TextInputLayout cpfTextInput;
    @BindView(R.id.full_name_text_input)
    TextInputLayout fullNameTextInput;
    @BindView(R.id.cellphone_text_edit)
    PhoneEditText cellphoneText;
    @BindView(R.id.email_text_edit)
    TextInputEditText emailEditText;

    @BindView(R.id.sex_spinner)
    Spinner sexSpinner;
    @BindView(R.id.civil_state_spinner)
    Spinner civilStateSpinner;
    @BindView(R.id.degree_kinship_spinner)
    Spinner degreeKinshipSpinner;

    @BindView(R.id.marriagedate_text_input)
    TextInputLayout marriageTextInput;
    @BindView(R.id.nationality_spinner)
    Spinner nationalitySpinner;
    private String cpfDependent = "";

    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.imgBtn_refresh)
    ImageButton btnRefresh;

    @BindView(R.id.containerDisabled)
    LinearLayout mContainerDisabled;
    @BindView(R.id.containerStudent)
    LinearLayout mContainerStudent;
    @BindView(R.id.containerWork)
    LinearLayout mContainerWork;
    @BindView(R.id.containerFatherDeclared)
    LinearLayout mContainerFatherDeclared;
    @BindView(R.id.containerShared)
    LinearLayout mContainerShared;
    @BindView(R.id.containerAdopted)
    LinearLayout mContainerAdopted;
    @BindView(R.id.containerPreviousMarried)
    LinearLayout mContainerPreviousMarried;
    @BindView(R.id.containerHolderPrevious)
    LinearLayout mContainerHolderPrevious;


    //Switchs para Validação
    @BindView(R.id.switch_disabled)
    Switch mSwitchDisabled;
    @BindView(R.id.switch_student)
    Switch mSwitchStudent;
    @BindView(R.id.switch_work)
    Switch mSwitchWork;
    @BindView(R.id.switch_declared)
    Switch mSwitchDeclared;
    @BindView(R.id.switch_shared)
    Switch mSwitchShared;
    @BindView(R.id.switch_adopted)
    Switch mSwitchAdopted;
    @BindView(R.id.switch_previous_married)
    Switch mSwitchPreviousMarried;
    @BindView(R.id.switch_holder_previous_married)
    Switch mSwitchHolderPreviousMarried;

    private View mView;
    private ProgressDialog mProgressDialog;
    private SessionManager sessionManager;
    private Context mContext;

    private List<CivilState> civilStates = new ArrayList<>();
    private List<String> civilStatesStr = new ArrayList<>();
    private List<Kinship> kinship = new ArrayList<>();
    private List<String> kinshipStr = new ArrayList<>();
    private SystemBehavior.BehaviorEnum idBehavior;
    private DependentLife mDependentLife;
    private String previousEmail = "";
    private String previousPhone = "";
    private ValidateTokenRequest.SendMethod methodToken = null;
    private boolean isNewDependent;
    private boolean onlyView;
    private boolean isReactivate;
    private boolean mIsChangedBehavior = false;
    private Dialog dialog;
    private Dialog dialogSerpro;

    String messageIdentifier;
    AddDependentRequest addDependentRequest;
    public static GetListOfTerms getListOfTerms = new GetListOfTerms();
    public String codeTerm = null;
    private DependentRequest dependent;

    private View.OnClickListener onClickOk = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            DependentRequest dependent = getDependentInformation();
            //Validar se mudou email e ou telefone, caso mudou os dois chamar modal e depois validate documet.

            boolean hasChangedEmail = ((previousEmail != null && !previousEmail.isEmpty()) && !previousEmail.equals(dependent.email))
                    || ((previousEmail == null || previousEmail.isEmpty()) && !dependent.email.isEmpty());
            boolean hasChangePhone = (previousPhone != null && !previousPhone.isEmpty() && !previousPhone.equals(dependent.cellPhone))
                    || ((previousPhone == null || previousPhone.isEmpty()) && !dependent.cellPhone.isEmpty());

            boolean emailIsEmpty = (dependent.email == null || dependent.email.isEmpty());
            boolean phoneIsEmpty = (dependent.cellPhone == null || dependent.cellPhone.isEmpty());

            if (hasChangedEmail && hasChangePhone && !isMinor() && !emailIsEmpty && !phoneIsEmpty) {

                String[] items = new String[2];
                items[0] = getString(R.string.hint_email);
                items[1] = getString(R.string.hint_cellphone);

                ShowQuestionDiolog(getString(R.string.edit_values_token), items, null, DependentActivity.this);
            } else {
                if (hasChangedEmail) {

                    if (!emailIsEmpty)
                        methodToken = ValidateTokenRequest.SendMethod.EMAIL;
                    else if (emailIsEmpty && !phoneIsEmpty)
                        methodToken = ValidateTokenRequest.SendMethod.SMS;
                } else if (hasChangePhone) {
                    if (!phoneIsEmpty)
                        methodToken = ValidateTokenRequest.SendMethod.SMS;
                    else if (phoneIsEmpty && !emailIsEmpty)
                        methodToken = ValidateTokenRequest.SendMethod.EMAIL;
                }

                getDocuments();
            }

            if (dialog != null) {
                dialog.dismiss();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private View.OnClickListener validateSerpro = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            searchSerpro(cpfEditText.getText().toString());
            if (dialogSerpro != null) {
                dialogSerpro.dismiss();
            }
        }
    };

    private static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_dependent);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(getApplicationContext());
        mContext = this;
        toolbarTitle.setText(getText(R.string.label_dependents));

        mView = findViewById(R.id.new_depedent);
        mProgressDialog = new ProgressDialog(this);

        onlyView = getIntent().getBooleanExtra(VIEW_EDIT_DEPENDENT, false);
        cpfDependent = getIntent().getStringExtra(CPF_EDIT_DEPENDENT);
        isReactivate = getIntent().getBooleanExtra(Constants.IS_REACTIVATE, false);

        isNewDependent = cpfDependent == null || cpfDependent.equals("");

        btnSave.setVisibility(View.VISIBLE);
        if (onlyView) {
            cpfEditText.setEnabled(false);
            fullNameEditText.setEnabled(false);
            birthDateEditText.setEnabled(false);
        }

        if (isNewDependent) {
            cellphoneText.setEnabled(false);
            emailEditText.setEnabled(false);
        } else {
            cellphoneText.setEnabled(true);
            emailEditText.setEnabled(true);
        }

        populateListCivilState();
        populateListKinship();
        birthDateField();
        cpfField();
    }

    @OnClick(R.id.btnSave)
    public void submit(View view) {
        if (checkName() && checkCPF() && checkMother() && checkMarriageDate() && checkkinship()) {

            int resID = getResources().getIdentifier("MSG102", "string", getPackageName());
            String message = getResources().getString(resID);
            if (cellphoneText.getText().toString() != null && !cellphoneText.getText().toString().equals("")
                    && !Utils.validatePhone(cellphoneText.getText().toString())) {
                showSnackBar(getString(R.string.not_valid_cellphone), TYPE_FAILURE);
            }

            dialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, this, onClickOk);

        } else
            showSnackBar(getString(R.string.validate_fields_dependents), Constants.TYPE_FAILURE);
    }

    public void getDocuments() {
        try {
            setLoading(true, getString(R.string.loading_searching));

            idBehavior = verifySystemBehaviorDependent(kinship.get(kinshipStr.indexOf(degreeKinshipSpinner.getSelectedItem()) - 1).getId().toString());
            DependentRequest dependent = getDependentInformation();
            validateNewBehavior(idBehavior.id());

            if (idBehavior != null || isReactivate) {
                int index;
                if (mIsChangedBehavior || !isReactivate)
                    index = idBehavior.id();
                else
                    index = SystemBehavior.BehaviorEnum.REACT_DEPENDENT.id();

                Intent intent = new Intent(DependentActivity.this, DocumentsActivity.class);
                intent.putExtra(Constants.ORIGIN_CALL_DOCUMENT, DocumentsActivity.OriginCall.FROMDEPENDENT);
                intent.putExtra(Constants.CONTEXT_DOCUMENT, index);
                intent.putExtra(Constants.CONTEXT_DOCUMENT_CPF, cpfDependent);
                intent.putExtra(VIEW_EDIT_DEPENDENT, onlyView);
                startActivityForResult(intent, DOCUMENT_FOR_RESULT);
            }
        } catch (Exception ex) {
            setLoading(false, "");
            showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TERM_ACCEPT) {
            if (resultCode == Activity.RESULT_OK) {

                if (checkCPF() && checkMother() && checkMarriageDate() && checkkinship())
                    if (isNewDependent)
                        addDependent();
                    else
                        editDependent();
                else
                    showSnackBar(getString(R.string.validate_fields_dependents), Constants.TYPE_FAILURE);

            } else {
                int resID = getResources().getIdentifier("MSG509", "string", mContext.getPackageName());
                String message = getResources().getString(resID);
                showSnackBar(message, Constants.TYPE_FAILURE);
            }
        }

        if (requestCode == Constants.DOCUMENT_FOR_RESULT) {
            if (resultCode == Activity.RESULT_OK) {

                if (getListOfTerms.getCodes().size() >= 1) {

                    getListOfTerms.setCpf(cpfDependent);
                    getListOfTerms.setPlatform(Constants.FEATURE_FLAG_PLATFORM);
                    showListOfTerms(getListOfTerms);

                } else if (codeTerm != null) {

                    showTerm(cpfDependent, codeTerm);

                } else {
                    if (checkCPF() && checkMother() && checkMarriageDate() && checkkinship())
                        if (isNewDependent)
                            addDependent();
                        else
                            editDependent();
                    else
                        showSnackBar(getString(R.string.validate_fields_dependents), Constants.TYPE_FAILURE);
                }
            } else {
                showSnackBar("Os Documentos não foram salvos com sucesso.", Constants.TYPE_FAILURE);
            }

        } else if (requestCode == Constants.INFORMATIVE_TOKEN_RESULT) {
            Intent returnA = new Intent();
            returnA.putExtra(Constants.SUCCESS_MESSAGE_INSERT_DEPENDENT, messageIdentifier);
            if (addDependentRequest != null)
                returnA.putExtra(Constants.INSERT_DEPENDENT_OBJECT, addDependentRequest.dependentRelationship.dependent.cpf);
            setResult(RESULT_OK, returnA);
            finish();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void cpfField() {
        cpfEditText.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;
            String oldString = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String str = unmask(s.toString());
                StringBuilder mask = new StringBuilder();
                if (isUpdating) {
                    oldString = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (final char m : MASK_CPF.toCharArray()) {
                    if (m != '#' && str.length() > oldString.length()) {
                        mask.append(m);
                        continue;
                    }
                    try {
                        mask.append(str.charAt(i));
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                cpfEditText.setText(mask.toString());
                cpfEditText.setSelection(mask.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    //POPULA O COMBO COM A INFORMACAO DOS ESTADOS CIVIS
    private void populateListCivilState() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<CivilStateListResponse> call = mAPIService.getCivilStates();
            call.enqueue(new Callback<CivilStateListResponse>() {
                @Override
                public void onResponse(@NonNull Call<CivilStateListResponse> call, @NonNull Response<CivilStateListResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful() && response.body() != null) {

                        CivilStateListResponse civilStatesResponse = response.body();
                        civilStates = new ArrayList<>();
                        civilStatesStr = new ArrayList<>();

                        if (civilStatesResponse.getCount() > 0) {
                            civilStates.addAll(civilStatesResponse.getCivilstates());
                        }

                        for (CivilState state : civilStates) {
                            civilStatesStr.add(state.getDescription());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                                R.layout.spinner_layout, civilStatesStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        civilStateSpinner.setAdapter(adapter);
                        setLoading(false, "");

                    } else if (response.code() == 404) {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(Call<CivilStateListResponse> call, Throwable t) {
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
    }

    @OnFocusChange(R.id.cpf_edit_text)
    void onFocusChange(View v, boolean hasFocus) {
        String cpf = cpfEditText.getText().toString();
        if (!hasFocus && !cpf.equals("")) {
            searchSerpro(cpf);
        }
    }

    @OnClick(R.id.imgBtn_refresh)
    public void searchSerpro(View view) {
        if (sessionManager.isLoggedIn()) {
            int resID = getResources().getIdentifier("MSG213", "string", getPackageName());
            String message = getResources().getString(resID);
            dialogSerpro = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, this, validateSerpro);
        }
    }

    //BUSCA NO ENDPOINT DA SERPRO
    public void searchSerpro(String cpf) {
        if (sessionManager.isLoggedIn() && !isReactivate) {
            setLoading(true, getString(R.string.loading_searching));

            if (isNewDependent) {
                fullNameEditText.setText("");
                birthDateEditText.setText("");
            }

            cpf = cpf.replace(".", "");
            cpf = cpf.replace("-", "");

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(cpfEditText.getWindowToken(), 0);

            APIService mAPIService = ServiceGenerator.createService(APIService.class);
            Call<JsonElement> call = mAPIService.serpro(new CPFInBody(cpf));
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        SerproResponse user = null;
                        SavedSerproResponse user2 = null;

                        if (response.body().getAsJsonObject().toString().contains("situacao"))
                            user2 = new Gson().fromJson((response.body().getAsJsonObject()), SavedSerproResponse.class);
                        else if (response.body().getAsJsonObject().toString().contains("codigo"))
                            user = new Gson().fromJson((response.body().getAsJsonObject()), SerproResponse.class);

                        if (user != null || user2 != null) {

                            setLoading(false, "");

                            if ((user != null && user.statusResponse.code != null && user.statusResponse.code.equals(Constants.REGULAR))
                                    || (user2 != null && user2.statusSavedResponse.code != null && user2.statusSavedResponse.code.equals(Constants.REGULAR))) {
                                fullNameEditText.setText(user == null ? user2.name : user.name);
                                birthDateEditText.setText(DateEditText.parseTODate(user == null ? user2.birthdate : user.birthdate));

                                if (!isNewDependent)
                                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG368", "string", getPackageName())), TYPE_SUCCESS);

                            } else if (isNewDependent) {
                                fullNameEditText.setText("");
                                birthDateEditText.setText("");
                                showSnackBar(getResources().getString(R.string.dialog_error5), Constants.TYPE_FAILURE);
                            }
                        }

                        if (isNewDependent)
                            degreeKinshipSpinner.setSelection(0);

                    } else if (response.code() == 400) {
                        setLoading(false, "");
                        try {
                            String data = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(data);
                            String message = jObjError.getString("Message");
                            showSnackBar(message, Constants.TYPE_FAILURE);
                        } catch (Exception ex) {
                            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, mContext, null);
                        }
                    } else {
                        setLoading(false, "");
                        showSnackBar(getResources().getString(R.string.problemServer), Constants.TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, Throwable t) {
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
    }

    //POPULA O COMBO COM A INFORMACAO DOS GRAUS DE PARENTESCO
    private void populateListKinship() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.getKinships();
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful() && response.body() != null) {
                        KinshipListResponse responseKinship = new Gson().fromJson(response.body().getAsJsonObject(), KinshipListResponse.class);
                        kinship = new ArrayList<>();
                        kinshipStr = new ArrayList<>();

                        if (responseKinship.getKinship().size() > 0) {
                            kinship.addAll(responseKinship.getKinship());
                        }

                        for (Kinship k : kinship) {
                            kinshipStr.add(k.getDescription());
                        }
                        kinshipStr.add(0, "Selecione");

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                                R.layout.spinner_layout, kinshipStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        degreeKinshipSpinner.setAdapter(adapter);
                        setLoading(false, "");

                        if (isNewDependent) {
                            btnRefresh.setVisibility(View.GONE);
                            ((LinearLayout.LayoutParams) fullNameTextInput.getLayoutParams()).weight = 7;
                        } else if (onlyView) {
                            queryDependentDetails(cpfDependent);
                            btnRefresh.setVisibility(View.GONE);
                            ((LinearLayout.LayoutParams) fullNameTextInput.getLayoutParams()).weight = 7;
                        } else {
                            queryDependentDetails(cpfDependent);
                            btnRefresh.setVisibility(View.VISIBLE);
                            ((LinearLayout.LayoutParams) fullNameTextInput.getLayoutParams()).weight = 6;
                        }

                    } else if (response.code() == 404) {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
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
    }

    //CASO A SELECAO DE PARENTESCO MUDE
    @OnItemSelected(R.id.degree_kinship_spinner)
    void onItemSelected(int position) {
        if (kinship.size() > 0 && !birthDateEditText.getText().toString().equals("") && kinshipStr.indexOf(degreeKinshipSpinner.getSelectedItem()) != 0) {

            //VERIFICA SE O NOVO GRAU È CONJUGE OU COMPANHEIRO SE FOR VALIDAR SE NO BANCO JÁ EXISTE ALGUM
            int selectedId = kinship.get(kinshipStr.indexOf(degreeKinshipSpinner.getSelectedItem()) - 1).getId();
            String selectedValueK = String.valueOf(selectedId);

            int selectedCivilId = 0;
            if (civilStates != null && civilStates.size() > 0) {
                selectedCivilId = civilStates.get(civilStatesStr.indexOf(civilStateSpinner.getSelectedItem())).getId();
            }

            if (mDependentLife != null) {
                if (selectedId != mDependentLife.kinshipId) {
                    mSwitchDisabled.setChecked(false);
                    mSwitchStudent.setChecked(false);
                    mSwitchWork.setChecked(false);
                    mSwitchDeclared.setChecked(false);
                    mSwitchShared.setChecked(false);
                    mSwitchAdopted.setChecked(false);
                    mSwitchPreviousMarried.setChecked(false);
                    mSwitchHolderPreviousMarried.setChecked(false);
                } else {
                    selectSwitchDependent(mDependentLife.systemBehavior.ID);
                }
            }

            if (selectedCivilId != SINGLE && (selectedValueK.equals(Constants.SON) || selectedValueK.equals(Constants.TUTOR) ||
                    selectedValueK.equals(Constants.CURATOR) || selectedValueK.equals(Constants.GUARD) || selectedValueK.equals(Constants.STEPSON)) && getAge() < 18
                    && !onlyView) {
                int resID = getResources().getIdentifier("MSG336", "string", getPackageName());
                String message = getResources().getString(resID);
                showSnackBar(message, TYPE_FAILURE);
                degreeKinshipSpinner.setSelection(0);
            } else {

                if (selectedValueK.equals(Constants.COMPANION) || selectedValueK.equals(Constants.SPOUSE))
                    loadDependentList(true);
                else {
                    defineSwitchBehavior();
                }
            }
        }
    }

    public void validateNewBehavior(int newBehavior) {
        if (mDependentLife != null) {
            mIsChangedBehavior = newBehavior != mDependentLife.systemBehavior.ID;
        }
    }

    //CASO A SELECAO DE Estado Civil
    @OnItemSelected(R.id.civil_state_spinner)
    void onItemCivilStatedSelected(int position) {

        if (kinship.size() > 0 && !birthDateEditText.getText().toString().equals("") && kinshipStr.indexOf(degreeKinshipSpinner.getSelectedItem()) != 0) {

            //VERIFICA SE O NOVO GRAU È CONJUGE OU COMPANHEIRO SE FOR VALIDAR SE NO BANCO JÁ EXISTE ALGUM
            int selectedId = kinship.get(kinshipStr.indexOf(degreeKinshipSpinner.getSelectedItem()) - 1).getId();
            String selectedValueK = String.valueOf(selectedId);

            int selectedCivilId = civilStates.get(civilStatesStr.indexOf(civilStateSpinner.getSelectedItem())).getId();

            if (selectedCivilId != SINGLE && (selectedValueK.equals(Constants.SON) || selectedValueK.equals(Constants.TUTOR) ||
                    selectedValueK.equals(Constants.CURATOR) || selectedValueK.equals(Constants.GUARD) || selectedValueK.equals(Constants.STEPSON)) && getAge() < 18
                    && !onlyView) {
                int resID = getResources().getIdentifier("MSG336", "string", getPackageName());
                String message = getResources().getString(resID);
                showSnackBar(message, TYPE_FAILURE);
                degreeKinshipSpinner.setSelection(0);
            }
        }
    }

    private void loadDependentList(final boolean active) {
        setLoading(true, getString(R.string.validating_kinship));
        final String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();

        final String cpfDependent = cpfEditText.getText().toString().replace(".", "").replace("-", "");

        final APIService apiService = ServiceGenerator.createService(APIService.class);

        Call<DependentHolder> call = apiService.queryDependentHolder(new DependentHolderBody(cpf, active));
        call.enqueue(new Callback<DependentHolder>() {
            @Override
            public void onResponse(@NonNull Call<DependentHolder> call, @NonNull Response<DependentHolder> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        DependentHolder dependentHolder = response.body();
                        boolean hasfound = false;

                        for (Dependent dep : dependentHolder.dependents) {
                            if ((String.valueOf(dep.kinshipId).equals(COMPANION) || String.valueOf(dep.kinshipId).equals(SPOUSE)) &&
                                    !dep.cpf.equals(cpfDependent)) {
                                hasfound = true;
                                int resID = getResources().getIdentifier("MSG134", "string", getPackageName());
                                String message = getResources().getString(resID);
                                showSnackBar(message, TYPE_FAILURE);
                                degreeKinshipSpinner.setSelection(0);

                                mContainerDisabled.setVisibility(View.GONE);
                                mContainerStudent.setVisibility(View.GONE);
                                mContainerWork.setVisibility(View.GONE);
                                mContainerFatherDeclared.setVisibility(View.GONE);
                                mContainerShared.setVisibility(View.GONE);
                                mContainerAdopted.setVisibility(View.GONE);
                                mContainerPreviousMarried.setVisibility(View.GONE);
                                mContainerHolderPrevious.setVisibility(View.GONE);
                                marriageTextInput.setVisibility(View.GONE);

                                break;
                            }
                        }
                        if (!hasfound) {
                            defineSwitchBehavior();

                        }
                        setLoading(false, "");


                    } else {
                        int resID = getResources().getIdentifier("MSG074", "string", getPackageName());
                        String message = getResources().getString(resID);
                        showSnackBar(message, TYPE_FAILURE);
                        setLoading(false, "");
                    }
                } else {
                    showSnackBar(getString(R.string.validateSentData), TYPE_FAILURE);
                    setLoading(false, "");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DependentHolder> call, @NonNull Throwable t) {
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

    private void defineSwitchBehavior() {
        try {
            String selectedValueK = String.valueOf(kinship.get(kinshipStr.indexOf(degreeKinshipSpinner.getSelectedItem()) - 1).getId());
            int age = DateEditText.getAge(birthDateEditText.getText().toString());

            switch (selectedValueK) {
                case SPOUSE:
                    mContainerDisabled.setVisibility(View.GONE);
                    mContainerStudent.setVisibility(View.GONE);
                    mContainerWork.setVisibility(View.GONE);
                    mContainerFatherDeclared.setVisibility(View.GONE);
                    mContainerShared.setVisibility(View.GONE);
                    mContainerAdopted.setVisibility(View.GONE);
                    mContainerPreviousMarried.setVisibility(View.GONE);
                    mContainerHolderPrevious.setVisibility(View.GONE);
                    marriageTextInput.setVisibility(View.VISIBLE);
                    break;
                case COMPANION:
                    mContainerDisabled.setVisibility(View.GONE);
                    mContainerStudent.setVisibility(View.GONE);
                    mContainerWork.setVisibility(View.GONE);
                    mContainerFatherDeclared.setVisibility(View.GONE);
                    mContainerShared.setVisibility(View.GONE);
                    mContainerAdopted.setVisibility(View.GONE);
                    mContainerPreviousMarried.setVisibility(View.VISIBLE);
                    mContainerHolderPrevious.setVisibility(View.VISIBLE);
                    marriageTextInput.setVisibility(View.GONE);
                    break;
                case SON:
                    mContainerDisabled.setVisibility(View.VISIBLE);
                    if (age >= 18 && age < 25 && DateEditText.canShowStudentAndWork() && !mSwitchDisabled.isChecked()) {
                        mContainerStudent.setVisibility(View.VISIBLE);
                    } else if (mSwitchDisabled.isChecked())
                        mContainerStudent.setVisibility(View.GONE);
                    if (age >= 18 && age < 25 && DateEditText.canShowStudentAndWork() && !mSwitchDisabled.isChecked())
                        mContainerWork.setVisibility(View.VISIBLE);
                    else if (mSwitchDisabled.isChecked())
                        mContainerWork.setVisibility(View.GONE);
                    mContainerFatherDeclared.setVisibility(View.GONE);
                    mContainerShared.setVisibility(View.GONE);
                    mContainerAdopted.setVisibility(View.VISIBLE);
                    mContainerPreviousMarried.setVisibility(View.GONE);
                    mContainerHolderPrevious.setVisibility(View.GONE);
                    marriageTextInput.setVisibility(View.GONE);
                    break;
                case TUTOR:
                    mContainerDisabled.setVisibility(View.VISIBLE);
                    if (age > 18 && age < 25 && DateEditText.canShowStudentAndWork() && !mSwitchDisabled.isChecked())
                        mContainerStudent.setVisibility(View.VISIBLE);
                    else if (mSwitchDisabled.isChecked())
                        mContainerStudent.setVisibility(View.GONE);
                    if (age > 18 && age < 25 && DateEditText.canShowStudentAndWork() && !mSwitchDisabled.isChecked())
                        mContainerWork.setVisibility(View.VISIBLE);
                    else if (mSwitchDisabled.isChecked())
                        mContainerWork.setVisibility(View.GONE);
                    mContainerFatherDeclared.setVisibility(View.GONE);
                    mContainerShared.setVisibility(View.GONE);
                    mContainerAdopted.setVisibility(View.GONE);
                    mContainerPreviousMarried.setVisibility(View.GONE);
                    mContainerHolderPrevious.setVisibility(View.GONE);
                    marriageTextInput.setVisibility(View.GONE);
                    break;
                case Constants.CURATOR:
                    mContainerDisabled.setVisibility(View.VISIBLE);
                    if (age > 18 && age < 25 && DateEditText.canShowStudentAndWork() && !mSwitchDisabled.isChecked())
                        mContainerStudent.setVisibility(View.VISIBLE);
                    else if (mSwitchDisabled.isChecked())
                        mContainerStudent.setVisibility(View.GONE);
                    if (age > 18 && age < 25 && DateEditText.canShowStudentAndWork() && !mSwitchDisabled.isChecked())
                        mContainerWork.setVisibility(View.VISIBLE);
                    else if (mSwitchDisabled.isChecked())
                        mContainerWork.setVisibility(View.GONE);
                    mContainerFatherDeclared.setVisibility(View.GONE);
                    mContainerShared.setVisibility(View.GONE);
                    mContainerAdopted.setVisibility(View.GONE);
                    mContainerPreviousMarried.setVisibility(View.GONE);
                    mContainerHolderPrevious.setVisibility(View.GONE);
                    marriageTextInput.setVisibility(View.GONE);
                    break;
                case Constants.GUARD:
                    mContainerDisabled.setVisibility(View.VISIBLE);
                    if (age > 18 && age < 25 && DateEditText.canShowStudentAndWork() && !mSwitchDisabled.isChecked())
                        mContainerStudent.setVisibility(View.VISIBLE);
                    else if (mSwitchDisabled.isChecked())
                        mContainerStudent.setVisibility(View.GONE);
                    if (age > 18 && age < 25 && DateEditText.canShowStudentAndWork() && !mSwitchDisabled.isChecked())
                        mContainerWork.setVisibility(View.VISIBLE);
                    else if (mSwitchDisabled.isChecked())
                        mContainerWork.setVisibility(View.GONE);
                    mContainerFatherDeclared.setVisibility(View.GONE);
                    mContainerShared.setVisibility(View.GONE);
                    mContainerAdopted.setVisibility(View.GONE);
                    mContainerPreviousMarried.setVisibility(View.GONE);
                    mContainerHolderPrevious.setVisibility(View.GONE);
                    marriageTextInput.setVisibility(View.GONE);
                    break;
                case Constants.STEPSON:
                    mContainerDisabled.setVisibility(View.VISIBLE);
                    if (age > 18 && age < 25 && DateEditText.canShowStudentAndWork() && !mSwitchDisabled.isChecked())
                        mContainerStudent.setVisibility(View.VISIBLE);
                    else if (mSwitchDisabled.isChecked())
                        mContainerStudent.setVisibility(View.GONE);
                    if (age > 18 && age < 25 && DateEditText.canShowStudentAndWork() && !mSwitchDisabled.isChecked())
                        mContainerWork.setVisibility(View.VISIBLE);
                    else if (mSwitchDisabled.isChecked())
                        mContainerWork.setVisibility(View.GONE);
                    mContainerFatherDeclared.setVisibility(View.VISIBLE);
                    mContainerShared.setVisibility(View.GONE);
                    mContainerAdopted.setVisibility(View.GONE);
                    mContainerPreviousMarried.setVisibility(View.GONE);
                    mContainerHolderPrevious.setVisibility(View.GONE);
                    marriageTextInput.setVisibility(View.GONE);
                    if (mSwitchDeclared.isChecked())
                        mContainerShared.setVisibility(View.VISIBLE);
                    break;
                case FATHER:
                case MOTHER:
                    mContainerDisabled.setVisibility(View.GONE);
                    mContainerStudent.setVisibility(View.GONE);
                    mContainerWork.setVisibility(View.GONE);
                    mContainerFatherDeclared.setVisibility(View.GONE);
                    mContainerShared.setVisibility(View.GONE);
                    mContainerAdopted.setVisibility(View.GONE);
                    mContainerPreviousMarried.setVisibility(View.GONE);
                    mContainerHolderPrevious.setVisibility(View.GONE);
                    marriageTextInput.setVisibility(View.GONE);
                    break;
            }
        } catch (Exception ex) {
            showSnackBar(ex.getMessage(), TYPE_FAILURE);
        }
    }

    @OnCheckedChanged(R.id.switch_disabled)
    public void onCheckedChangedDisabled(CompoundButton buttonView, boolean isChecked) {
        try {
            mContainerStudent.setVisibility(View.GONE);
            mContainerWork.setVisibility(View.GONE);
            if (!isChecked && getAge() >= 18 && getAge() < 25 && DateEditText.canShowStudentAndWork()) {
                mContainerStudent.setVisibility(View.VISIBLE);
                mContainerWork.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, this, null);
        }
    }

    public int getAge() {
        int age = 0;
        try {
            age = DateEditText.getAge(birthDateEditText.getText().toString());
        } catch (Exception ex) {
            showSnackBar(ex.getMessage(), TYPE_FAILURE);
        }

        return age;
    }

    @OnCheckedChanged(R.id.switch_declared)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            mContainerShared.setVisibility(View.VISIBLE);
        else
            mContainerShared.setVisibility(View.GONE);
    }

    private void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mView, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);

        snackbar.show();
    }

    private void setLoading(boolean loading, String text) {
        try {
            if (loading) {
                mProgressDialog.setMessage(text);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            } else
                mProgressDialog.dismiss();
        } catch (Exception ex) {
            LogUtils.error("DependentActivity", ex);
        }
    }

    //----------------------------------------------
    //VALIDA SE O CAMPO DE CPF OBRIGATORIO ESTA PREENCHIDO
    public boolean checkCPF() {
        if (cpfEditText.getText().toString().trim().length() < 11)
            return false;
        else {
            cpfDependent = cpfEditText.getText().toString();
            return true;
        }
    }

    //----------------------------------------------
    //VALIDA SE O CAMPO DE NOME OBRIGATORIO ESTA PREENCHIDO
    public boolean checkName() {
        return fullNameEditText.getText().toString().trim().length() >= 11;
    }

    //VALIDA SE O CAMPO OBRIGATORIO NOME DA MAE ESTA PREENCHIDO
    public boolean checkMother() {
        return fullNameMotherEditText.getText().toString().trim().length() != 0;
    }

    //VALIDA SE O CAMPO OBRIGATORIO DE DATA DE CASAMENTO ESTA PREENCHIDO
    public boolean checkMarriageDate() {
        if (degreeKinshipSpinner.getSelectedItemPosition() != 0) {
            int selectedValueK = kinship.get(kinshipStr.indexOf(degreeKinshipSpinner.getSelectedItem()) - 1).getId();
            String marriageDate = marriageEditText.getText().toString();

            return !String.valueOf(selectedValueK).equals(SPOUSE) || (marriageDate.trim().length() != 0 && DateEditText.isValidDate(marriageDate));
        } else {
            showSnackBar(getString(R.string.choose_kinship), TYPE_FAILURE);
        }

        return false;
    }

    //VALIDA SE O CAMPO OBRIGATORIO DE GRAU DE PARENTESCO ESTA PREENCHIDO
    public boolean checkkinship() {
        return degreeKinshipSpinner.getSelectedItemPosition() != 0;
    }

    private void birthDateField() {
        marriageEditText.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String str = unmask(s.toString());
                StringBuilder mask = new StringBuilder();
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (final char m : MASK_DATE.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mask.append(m);
                        continue;
                    }
                    try {
                        mask.append(str.charAt(i));
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                marriageEditText.setText(mask.toString());
                marriageEditText.setSelection(mask.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        birthDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
    }

    //PEGA OS VALORES DOS CAMPOS NA TELA PARA PODER INSERIR OU EDITAR
    private DependentRequest getDependentInformation() {
        DependentRequest dependentRequest = new DependentRequest();
        dependentRequest.cpf = ValidateCPF.removeSpecialCharacters(cpfEditText.getText().toString());
        dependentRequest.name = fullNameEditText.getText().toString();
        dependentRequest.mothersName = fullNameMotherEditText.getText().toString();
        dependentRequest.birthDate = DateEditText.parseToShortDate(birthDateEditText.getText().toString());
        dependentRequest.sex = sexSpinner.getSelectedItem().toString().equalsIgnoreCase("Masculino") ? "M" : "F";
        CivilStatusRequest civilStatusRequest = new CivilStatusRequest();
        civilStatusRequest.id = civilStates.get(civilStatesStr.indexOf(civilStateSpinner.getSelectedItem())).getId();
        dependentRequest.civilState = civilStatusRequest;
        dependentRequest.nationality = nationalitySpinner.getSelectedItem().toString();
        dependentRequest.student = mSwitchStudent.isChecked();
        dependentRequest.invalid = mSwitchDisabled.isChecked();
        dependentRequest.outOfPolicy = false;
        dependentRequest.marriageDate = DateEditText.parseToShortDate(marriageEditText.getText().toString());
        dependentRequest.status = mDependentLife != null ? mDependentLife.status : new Status((0));
        dependentRequest.email = emailEditText.getText().toString();
        dependentRequest.cellPhone = PhoneEditText.unmaskPhone(cellphoneText.getText().toString());

        try {
            dependentRequest.isMinor = DateEditText.getAge(birthDateEditText.getText().toString()) < 18;
        } catch (Exception ex) {
            showSnackBar(ex.getMessage(), TYPE_FAILURE);
        }
        return dependentRequest;
    }

    //METODO QUE DEFINE O SISTEMA COMPORTAMENTO A PARTIR DAS ESCOLHAS FEITAS EM TELA
    private SystemBehavior.BehaviorEnum verifySystemBehaviorDependent(String kinshipId) {

        SystemBehavior.BehaviorEnum SystemBehaviorId = null;
        DependentRequest dependent = getDependentInformation();

        boolean AmbevSource = FahzApplication.getInstance().getFahzClaims().getSource() != null &&
                !FahzApplication.getInstance().getFahzClaims().getSource().equals(Integer.toString(Constants.Fahz));

        getListOfTerms = new GetListOfTerms();
        String[] initList;

        switch (kinshipId) {
            case Constants.SPOUSE:
                SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_CONJ;
                break;
            case Constants.COMPANION:
                if (mSwitchHolderPreviousMarried.isChecked() && mSwitchPreviousMarried.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_COMP_BOTH_MARRIGE;
                } else if (!mSwitchHolderPreviousMarried.isChecked() && mSwitchPreviousMarried.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_COMP_HOLDER_MARRIGE;
                } else if (mSwitchHolderPreviousMarried.isChecked() && !mSwitchPreviousMarried.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_COMP_DEP_MARRIGE;
                } else if (!mSwitchHolderPreviousMarried.isChecked() && !mSwitchPreviousMarried.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_COMP;
                }
                break;
            case Constants.SON:
                if (mSwitchAdopted.isChecked()) {
                    if (mSwitchDisabled.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_ADOP_INVALID;
                    } else if (dependent.isMinor) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_ADOP_MINOR;
                    } else if (mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_ADOP_MAJOR_STU;
                    } else if (!mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_ADOP_MAJOR_WORK;
                    } else if (mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_ADOP_MAJOR_WORK_STU;
                    } else if (!mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                        if (AmbevSource)
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_ADOP_MAJOR;
                        else
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_ADOP_MAJOR_FAHZ;
                    }
                } else {
                    if (mSwitchDisabled.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_INVALID;
                    } else if (dependent.isMinor) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_MINOR; // TA CAINDO AQUI
                    } else if (mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_MAJOR_STU;
                        codeTerm = INCLUSAO_FILHO_MAIOR_SEM_CART_TRAB;
                    } else if (!mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_MAJOR_WORK;
                    } else if (mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_MAJOR_WORK_STU;
                    } else if (!mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                        if (AmbevSource)
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_MAJOR; // É PARA FICAR AQUI
                        else
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_SON_MAJOR_FAHZ;
                    }
                }
                break;
            case Constants.CURATOR:
                if (mSwitchDisabled.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_CUR_INVALID;
                } else if (dependent.isMinor) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_CUR_MINOR;
                } else if (mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_CUR_MAJOR_STU;
                    codeTerm = INCLUSAO_FILHO_MAIOR_SEM_CART_TRAB;
                } else if (!mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_CUR_MAJOR_WORK;
                } else if (mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_CUR_MAJOR_WORK_STU;
                } else if (!mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                    if (AmbevSource)
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_CUR_MAJOR;
                    else
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_CUR_MAJOR_FAHZ;
                }
                break;
            case Constants.GUARD:
                if (mSwitchDisabled.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_GUARD_INVALID;
                } else if (dependent.isMinor) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_GUARD_MINOR;
                } else if (mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_GUARD_MAJOR_STU;
                    codeTerm = INCLUSAO_FILHO_MAIOR_SEM_CART_TRAB;
                } else if (!mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_GUARD_MAJOR_WORK;
                } else if (mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_GUARD_MAJOR_WORK_STU;
                } else if (!mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                    if (AmbevSource)
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_GUARD_MAJOR;
                    else
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_GUARD_MAJOR_FAHZ;
                }
                break;
            case Constants.STEPSON:
                if (mSwitchDeclared.isChecked()) {

                    if (mSwitchShared.isChecked()) {

                        if (mSwitchDisabled.isChecked()) {
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_SHARED_INVALID;
                        } else if (dependent.isMinor) {
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_SHARED_MINOR;
                            codeTerm = INCLUSAO_FILHO_GUARDA_COMPARTILHADA;
                        } else if (mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_SHARED_MAJOR_STU;
                            initList = new String[]{Constants.INCLUSAO_FILHO_GUARDA_COMPARTILHADA, Constants.INCLUSAO_FILHO_MAIOR_SEM_CART_TRAB};
                            getListOfTerms.setCodes(Arrays.asList(initList));
                        } else if (!mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_SHARED_MAJOR_WORK;
                        } else if (mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_SHARED_MAJOR_WORK_STU;
                        } else if (!mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                            if (AmbevSource)
                                SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_SHARED_MAJOR;
                            else
                                SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_SHARED_MAJOR_FAHZ;
                        }
                    } else {

                        if (mSwitchDisabled.isChecked()) {
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_NO_SHARED_INVALID;
                        } else if (dependent.isMinor) {
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_NO_SHARED_MINOR;
                        } else if (mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_NO_SHARED_MAJOR_STU;
                        } else if (!mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_NO_SHARED_MAJOR_WORK;
                        } else if (mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_NO_SHARED_MAJOR_WORK_STU;
                        } else if (!mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                            if (AmbevSource)
                                SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_NO_SHARED_MAJOR;
                            else
                                SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_NO_SHARED_MAJOR_FAHZ;
                        }
                    }
                } else {
                    if (mSwitchDisabled.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_INVALID;
                    } else if (dependent.isMinor) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_MINOR;
                    } else if (mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_MAJOR_STU;
                    } else if (!mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_MAJOR_WORK;
                    } else if (mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_MAJOR_WORK_STU;
                    } else if (!mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                        if (AmbevSource)
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_MAJOR;
                        else
                            SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_ENT_MAJOR_FAHZ;
                    }
                }
                break;
            case Constants.FATHER:
                SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_DAD;
                break;
            case Constants.MOTHER:
                SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_MOM;
                break;
            case Constants.TUTOR:
                if (mSwitchDisabled.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_TUT_INVALID;
                } else if (dependent.isMinor) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_TUT_MINOR;
                } else if (mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_TUT_MAJOR_STU;
                    codeTerm = INCLUSAO_FILHO_MAIOR_SEM_CART_TRAB;
                } else if (!mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_TUT_MAJOR_WORK;
                } else if (mSwitchStudent.isChecked() && mSwitchWork.isChecked()) {
                    SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_TUT_MAJOR_WORK_STU;
                } else if (!mSwitchStudent.isChecked() && !mSwitchWork.isChecked()) {
                    if (AmbevSource)
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_TUT_MAJOR;
                    else
                        SystemBehaviorId = SystemBehavior.BehaviorEnum.INCL_DEP_TUT_MAJOR_FAHZ;
                }
                break;
        }

        return SystemBehaviorId;
    }

    //METODO QUE DEFINE AS ESCOLHAS FEITAS EM TELA A PARTIR DA ESCOLHA DE COMPORTAMENTO
    private void selectSwitchDependent(int behaviorId) {

        SystemBehavior.BehaviorEnum behavior = SystemBehavior.BehaviorEnum.fromId(behaviorId);

        switch (behavior) {
            case INCL_DEP_COMP_BOTH_MARRIGE:
                mSwitchHolderPreviousMarried.setChecked(true);
                mSwitchPreviousMarried.setChecked(true);
                break;
            case INCL_DEP_COMP_DEP_MARRIGE:
                mSwitchHolderPreviousMarried.setChecked(false);
                mSwitchPreviousMarried.setChecked(true);
                break;
            case INCL_DEP_COMP_HOLDER_MARRIGE:
                mSwitchHolderPreviousMarried.setChecked(true);
                mSwitchPreviousMarried.setChecked(false);
                break;
            case INCL_DEP_COMP:
                mSwitchHolderPreviousMarried.setChecked(false);
                mSwitchPreviousMarried.setChecked(false);
                break;
            case INCL_DEP_SON_ADOP_INVALID:
                mSwitchAdopted.setChecked(true);
                mSwitchDisabled.setChecked(true);
                break;
            case INCL_DEP_SON_ADOP_MINOR:
                mSwitchAdopted.setChecked(true);
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK:
                mSwitchAdopted.setChecked(true);
                mSwitchWork.setChecked(true);
                break;
            case INCL_DEP_SON_ADOP_MAJOR:
                mSwitchAdopted.setChecked(true);
                mSwitchWork.setChecked(false);
                break;
            case INCL_DEP_SON_INVALID:
                mSwitchAdopted.setChecked(false);
                mSwitchDisabled.setChecked(true);
                break;
            case INCL_DEP_SON_MINOR:
                mSwitchAdopted.setChecked(false);
                break;
            case INCL_DEP_SON_MAJOR_WORK:
                mSwitchAdopted.setChecked(false);
                mSwitchWork.setChecked(true);
                break;
            case INCL_DEP_SON_MAJOR:
                mSwitchAdopted.setChecked(false);
                mSwitchWork.setChecked(false);
                break;
            case INCL_DEP_SON_ADOP_MAJOR_STU:
                mSwitchAdopted.setChecked(true);
                mSwitchDisabled.setChecked(false);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_SON_MAJOR_STU:
                mSwitchAdopted.setChecked(false);
                mSwitchDisabled.setChecked(false);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK_STU:
                mSwitchAdopted.setChecked(true);
                mSwitchWork.setChecked(true);
                mSwitchDisabled.setChecked(false);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_SON_MAJOR_WORK_STU:
                mSwitchAdopted.setChecked(false);
                mSwitchWork.setChecked(false);
                mSwitchDisabled.setChecked(false);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_CUR_INVALID:
                mSwitchDisabled.setChecked(true);
                break;
            case INCL_DEP_CUR_MAJOR_WORK:
                mSwitchWork.setChecked(true);
                break;
            case INCL_DEP_CUR_MAJOR:
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(false);
                break;
            case INCL_DEP_CUR_MAJOR_STU:
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_CUR_MAJOR_WORK_STU:
                mSwitchWork.setChecked(true);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_GUARD_INVALID:
                mSwitchDisabled.setChecked(true);
                break;
            case INCL_DEP_GUARD_MAJOR_WORK:
                mSwitchWork.setChecked(true);
                break;
            case INCL_DEP_GUARD_MAJOR:
                mSwitchWork.setChecked(false);
                break;
            case INCL_DEP_GUARD_MAJOR_STU:
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_GUARD_MAJOR_WORK_STU:
                mSwitchWork.setChecked(true);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_ENT_SHARED_INVALID:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(true);
                mSwitchDisabled.setChecked(true);
                break;
            case INCL_DEP_ENT_SHARED_MINOR:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(true);
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(true);
                mSwitchWork.setChecked(true);
                break;
            case INCL_DEP_ENT_SHARED_MAJOR:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(true);
                mSwitchWork.setChecked(false);
                break;
            case INCL_DEP_ENT_NO_SHARED_INVALID:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(false);
                mSwitchDisabled.setChecked(true);
                break;
            case INCL_DEP_ENT_NO_SHARED_MINOR:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(false);
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(false);
                mSwitchWork.setChecked(true);
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(false);
                mSwitchWork.setChecked(false);
                break;
            case INCL_DEP_ENT_INVALID:
                mSwitchDeclared.setChecked(false);
                mSwitchDisabled.setChecked(true);
                break;
            case INCL_DEP_ENT_MINOR:
                mSwitchDeclared.setChecked(false);
                break;
            case INCL_DEP_ENT_MAJOR_WORK:
                mSwitchDeclared.setChecked(false);
                mSwitchWork.setChecked(true);
                break;
            case INCL_DEP_ENT_MAJOR:
                mSwitchDeclared.setChecked(false);
                mSwitchWork.setChecked(false);
                break;
            case INCL_DEP_ENT_MAJOR_STU:
                mSwitchDeclared.setChecked(false);
                mSwitchShared.setChecked(false);
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_ENT_MAJOR_WORK_STU:
                mSwitchDeclared.setChecked(false);
                mSwitchShared.setChecked(false);
                mSwitchWork.setChecked(true);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_STU:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(true);
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK_STU:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(true);
                mSwitchWork.setChecked(true);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_STU:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(false);
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK_STU:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(false);
                mSwitchWork.setChecked(true);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_TUT_INVALID:
                mSwitchDisabled.setChecked(true);
                break;
            case INCL_DEP_TUT_MAJOR_WORK:
                mSwitchWork.setChecked(true);
                break;
            case INCL_DEP_TUT_MAJOR:
                mSwitchWork.setChecked(false);
                break;
            case INCL_DEP_TUT_MAJOR_WORK_STU:
                mSwitchWork.setChecked(true);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_TUT_MAJOR_STU:
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(true);
                break;
            case INCL_DEP_SON_ADOP_MAJOR_FAHZ:
                mSwitchAdopted.setChecked(true);
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(false);
                break;
            case INCL_DEP_SON_MAJOR_FAHZ:
                mSwitchAdopted.setChecked(false);
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(false);
                break;
            case INCL_DEP_CUR_MAJOR_FAHZ:
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(false);
                break;
            case INCL_DEP_GUARD_MAJOR_FAHZ:
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(false);
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_FAHZ:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(true);
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(false);
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_FAHZ:
                mSwitchDeclared.setChecked(true);
                mSwitchShared.setChecked(false);
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(false);
                break;
            case INCL_DEP_ENT_MAJOR_FAHZ:
                mSwitchDeclared.setChecked(false);
                mSwitchShared.setChecked(false);
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(false);
                break;
            case INCL_DEP_TUT_MAJOR_FAHZ:
                mSwitchWork.setChecked(false);
                mSwitchStudent.setChecked(false);
                break;
        }
    }

    //METODO PARA ADIçÂO DE DEPENDENTE
    private void addDependent() {
        setLoading(true, getString(R.string.savingdependent));

        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();

        AccountHolder accountHolder = new AccountHolder();
        accountHolder.cpf = ValidateCPF.removeSpecialCharacters(cpf);

        DependentRequest dependent = getDependentInformation();
        dependent.systemBehavior = new SystemBehaviorModel();
        dependent.systemBehavior.ID = idBehavior.id();

        DependentRelationship dependentRelationship = new DependentRelationship(accountHolder, dependent, kinship.get(kinshipStr.indexOf(degreeKinshipSpinner.getSelectedItem()) - 1).getId());
        List<LifeBenefits> lifeBenefits = new ArrayList<>();
        addDependentRequest = new AddDependentRequest(dependentRelationship, lifeBenefits);

        final boolean isSendToken = methodToken != null;
        String methodTokenString = methodToken != null ? methodToken.toString() : null;

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<DependentResponse> call = apiService.addDependent(addDependentRequest, isSendToken, methodTokenString);
        call.enqueue(new Callback<DependentResponse>() {
            @Override
            public void onResponse(Call<DependentResponse> call, Response<DependentResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                DependentResponse response2 = response.body();
                if (response.isSuccessful()) {
                    if (response2.commited) {

                        if (isSendToken && !isMinor()) {

                            messageIdentifier = response2.messageIdentifier;

                            Intent intent = new Intent(DependentActivity.this, InformativeTokenActivity.class);
                            startActivityForResult(intent, INFORMATIVE_TOKEN_RESULT);
                        } else {
                            Intent returnA = new Intent();
                            returnA.putExtra(Constants.SUCCESS_MESSAGE_INSERT_DEPENDENT, response2.messageIdentifier);
                            returnA.putExtra(Constants.INSERT_DEPENDENT_OBJECT, addDependentRequest.dependentRelationship.dependent.cpf);
                            setResult(RESULT_OK, returnA);
                            finish();
                        }

                    } else {
                        showSnackBar(response2.messageIdentifier, TYPE_FAILURE);
                    }
                } else {
                    showSnackBar(getResources().getString(R.string.problemServer), Constants.TYPE_FAILURE);
                }
                setLoading(false, "");
            }

            @Override
            public void onFailure(Call<DependentResponse> call, Throwable t) {
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

    //---------------------------------------------------EDICAO

    //BUSCA DE DEPENDENTE
    private void queryDependentDetails(String cpfDependent) {
        setLoading(true, getString(R.string.search_dependent_detail));
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<DependentLife> call = apiService.queryCPFDependent(new CPFInBody(cpfDependent));
        call.enqueue(new Callback<DependentLife>() {
            @Override
            public void onResponse(@NonNull Call<DependentLife> call, @NonNull Response<DependentLife> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful() && response.code() == 200) {
                    mDependentLife = response.body();
                    previousEmail = mDependentLife.email;
                    previousPhone = mDependentLife.cellPhone;

                    if (mDependentLife != null) {
                        populateDependentInformation();
                    }
                } else {
                    int resID = getResources().getIdentifier("MSG027", "string", getPackageName());
                    String message = getResources().getString(resID);
                    showSnackBar(message, TYPE_FAILURE);
                }
                setLoading(false, "");
            }

            @Override
            public void onFailure(@NonNull Call<DependentLife> call, @NonNull Throwable t) {
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

    //POPULA AS INFORMACOES DE DEPENDENTE NA TELA
    private void populateDependentInformation() {
        if (mDependentLife != null) {
            setLoading(true, getString(R.string.search_dependent_detail));
            cpfEditText.setText(mDependentLife.cpf);
            fullNameEditText.setText(mDependentLife.name);
            fullNameMotherEditText.setText(mDependentLife.mothersName);
            birthDateEditText.setText(DateEditText.parseTODate(mDependentLife.birthDate));
            marriageEditText.setText(mDependentLife.marriageDate != null ? DateEditText.parseTODate(mDependentLife.marriageDate) : "");
            cellphoneText.setText(mDependentLife.cellPhone);
            emailEditText.setText(mDependentLife.email);

            String kinshipDescription = "";
            for (Kinship k : kinship) {
                if (k.getId() == mDependentLife.kinshipId)
                    kinshipDescription = k.getDescription();
            }

            degreeKinshipSpinner.setSelection(kinshipStr.indexOf(kinshipDescription));
            degreeKinshipSpinner.setEnabled(false);
            degreeKinshipSpinner.setFocusable(false);
            degreeKinshipSpinner.setAlpha(0.5f);

            sexSpinner.setSelection(Arrays.asList(getResources().getStringArray(R.array.sex_array)).indexOf(mDependentLife.sex.equals("M") ? "Masculino" : "Feminino"));
            civilStateSpinner.setSelection(civilStatesStr.indexOf(mDependentLife.civilStatusRequest.description));
            nationalitySpinner.setPrompt(mDependentLife.nationality.equalsIgnoreCase("BRA") ? "Brasileiro" : "Estrageiro");
            selectSwitchDependent(mDependentLife.systemBehavior.ID);

            mSwitchDisabled.setEnabled(false);
            mSwitchDisabled.setAlpha(0.5f);

            mSwitchAdopted.setEnabled(false);
            mSwitchAdopted.setAlpha(0.5f);

            mSwitchDeclared.setEnabled(false);
            mSwitchDeclared.setAlpha(0.5f);

            mSwitchDeclared.setEnabled(false);
            mSwitchDeclared.setAlpha(0.5f);

            mSwitchHolderPreviousMarried.setEnabled(false);
            mSwitchHolderPreviousMarried.setAlpha(0.5f);

            mSwitchPreviousMarried.setEnabled(false);
            mSwitchPreviousMarried.setAlpha(0.5f);

            mSwitchShared.setEnabled(false);
            mSwitchShared.setAlpha(0.5f);

            mSwitchStudent.setEnabled(false);
            mSwitchStudent.setAlpha(0.5f);

            mSwitchWork.setEnabled(false);
            mSwitchWork.setAlpha(0.5f);

            cpfEditText.setClickable(true);
            cpfEditText.setFocusable(false);
            cpfEditText.setInputType(InputType.TYPE_NULL);

            cpfEditText.setTextColor(getResources().getColor(R.color.grey_light_text));
            cpfTextInput.setHintTextAppearance(R.style.DesactivateHint);

            cpfEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG214", "string", getPackageName())), TYPE_FAILURE);
                }
            });

            blockFields();

            setLoading(false, "");
        }
    }

    private boolean isMinor() {

        boolean isMinor = false;
        try {
            isMinor = DateEditText.getAge(birthDateEditText.getText().toString()) < 18;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isMinor;
    }

    private void editDependent() {
        setLoading(true, getString(R.string.editing_dependent));

        DependentRequest dependent = getDependentInformation();
        dependent.systemBehavior = new SystemBehaviorModel();
        dependent.systemBehavior.ID = idBehavior.id();

        mDependentLife.populateWithInformation(dependent, kinship.get(kinshipStr.indexOf(degreeKinshipSpinner.getSelectedItem()) - 1).getId());

        final boolean isSendToken = methodToken != null;
        String methodTokenString = methodToken != null ? methodToken.toString() : null;

        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<CommitResponse> call = apiService.editDependent(isReactivate, mDependentLife, isSendToken, methodTokenString);
        call.enqueue(new Callback<CommitResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful() && response.code() == 200) {
                    CommitResponse response2 = response.body();
                    if (response2.commited) {

                        if (isSendToken && !isMinor()) {

                            messageIdentifier = response2.messageIdentifier;

                            Intent intent = new Intent(DependentActivity.this, InformativeTokenActivity.class);
                            startActivityForResult(intent, INFORMATIVE_TOKEN_RESULT);
                        } else {
                            Intent returnA = new Intent();
                            returnA.putExtra(Constants.SUCCESS_MESSAGE_INSERT_DEPENDENT, response2.messageIdentifier);
                            setResult(RESULT_OK, returnA);
                            finish();
                        }
                    } else {
                        showSnackBar(response2.messageIdentifier, TYPE_FAILURE);
                    }
                } else {
                    showSnackBar(getResources().getString(R.string.problemServer), Constants.TYPE_FAILURE);
                }
                setLoading(false, "");
            }

            @Override
            public void onFailure(@NonNull Call<CommitResponse> call, @NonNull Throwable t) {
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

    private void ShowQuestionDiolog(String title, String[] options, @Nullable String buttonMessage, Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_question_token);

        Button emailButton = dialog.findViewById(R.id.emailButton);
        Button smsButton = dialog.findViewById(R.id.smsButton);
        Button backButton = dialog.findViewById(R.id.backButton);

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodToken = ValidateTokenRequest.SendMethod.EMAIL;
                dialog.cancel();
                getDocuments();
            }
        });

        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodToken = ValidateTokenRequest.SendMethod.SMS;
                dialog.cancel();
                //callSaveDependent();
                getDocuments();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void showTerm(String cpf, String codeTerm) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TERMS_OF_USE_SELECTED, codeTerm);
        bundle.putString(Constants.TERMS_OF_USE_CPF, cpf);
        Intent intent = new Intent(mContext, TermsOfUseActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, TERM_ACCEPT);
    }


    // Método que verifica se o perfil de quem está acessando essa página é dependente
    // Se sim: Bloqueia os campos de grau de parentesco, e todos os switches
    private void blockFields() {
        // Condição abaixo verifica se o perfil de quem está acessando a tela é de dependente.
        if (FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
            degreeKinshipSpinner.setEnabled(false);
            degreeKinshipSpinner.setFocusable(false);
            degreeKinshipSpinner.setAlpha(0.5f);

            mSwitchDisabled.setEnabled(false);
            mSwitchDisabled.setAlpha(0.5f);

            mSwitchAdopted.setEnabled(false);
            mSwitchAdopted.setAlpha(0.5f);

            mSwitchDeclared.setEnabled(false);
            mSwitchDeclared.setAlpha(0.5f);

            mSwitchDeclared.setEnabled(false);
            mSwitchDeclared.setAlpha(0.5f);

            mSwitchHolderPreviousMarried.setEnabled(false);
            mSwitchHolderPreviousMarried.setAlpha(0.5f);

            mSwitchPreviousMarried.setEnabled(false);
            mSwitchPreviousMarried.setAlpha(0.5f);

            mSwitchShared.setEnabled(false);
            mSwitchShared.setAlpha(0.5f);

            mSwitchStudent.setEnabled(false);
            mSwitchStudent.setAlpha(0.5f);

            mSwitchWork.setEnabled(false);
            mSwitchWork.setAlpha(0.5f);
        }
    }

    //Metodo que verifica se ha politicas pendentes para o usuário aceitar
    public void showListOfTerms(GetListOfTerms getListOfTerms) {

        setLoading(true, "");

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = mAPIService.getListOfTerms(getListOfTerms);
        call.enqueue(new Callback<JsonElement>() {
            @SuppressLint("LongLogTag")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    if (((JsonArray) response.body()).size() > 0) {

                        List<PoliciesAndTerm> termsList = new ArrayList<>();
                        for (int i = 0; i < ((JsonArray) response.body()).size(); i++) {
                            PoliciesAndTerm policiesAndTerm = new Gson().fromJson(((JsonArray) response.body()).get(i).toString(), PoliciesAndTerm.class);
                            if (policiesAndTerm.documentType == 1) {
                                termsList.add(policiesAndTerm);
                            }
                        }

                        Intent intent = new Intent(mContext, CheckPendingTermsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("policiesAndTermList", (Serializable) termsList);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, Constants.TERM_ACCEPT);
                    }

                    setLoading(false, "");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                setLoading(false, "");
                if (t instanceof SocketTimeoutException)
                    Toast.makeText(mContext, getResources().getString(getResources().getIdentifier("MSG362", "string", mContext.getPackageName())), Toast.LENGTH_LONG).show();
                else if (t instanceof UnknownHostException)
                    Toast.makeText(mContext, getResources().getString(getResources().getIdentifier("MSG361", "string", mContext.getPackageName())), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}