package br.com.avanade.fahz.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.BuildConfig;
import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.token.InformativeTokenActivity;
import br.com.avanade.fahz.controls.PhoneEditText;
import br.com.avanade.fahz.dialogs.PolicyOfDataUseActivity;
import br.com.avanade.fahz.dialogs.PrivacyPolicyActivity;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.SignupRequest;
import br.com.avanade.fahz.model.SignupResponse;
import br.com.avanade.fahz.model.ValidateTokenRequest;
import br.com.avanade.fahz.model.lgpdModel.AcceptData;
import br.com.avanade.fahz.model.lgpdModel.CheckPlatform;
import br.com.avanade.fahz.model.lgpdModel.NotificationAnswer;
import br.com.avanade.fahz.model.response.FirstAccessResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.NavigationHost;
import br.com.avanade.fahz.util.Utils;
import br.com.avanade.fahz.util.ValidateCPF;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static br.com.avanade.fahz.util.Constants.INFORMATIVE_TOKEN_RESULT;
import static br.com.avanade.fahz.util.Constants.MASK_CPF;
import static br.com.avanade.fahz.util.Constants.MASK_DATE;
import static br.com.avanade.fahz.util.Constants.TERM_ACCEPT;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class FirstAccessFragment extends Fragment {
    private APIService mAPIService;

    private EditText cpfEditText, birthDateEditText, passwordEditText, passwordConfirmEditText, mNameEditText, mEmail;
    private CheckBox policyPrivacyCheckbox, policyDataUseCheckbox, healthInformationCheckbox, offerInformationCheckbox;
    private TableLayout tablelayoutLgpd;
    private PhoneEditText mCellPhone;
    private Button enterButton;
    private View mView;
    private ProgressDialog mProgressDialog;
    private ValidateTokenRequest.SendMethod methodToken = null;

    public static String idTermOfAcceptancePrivacyPolicy;
    public static String idTermOfAcceptanceDataUse;
    public boolean showCommunicationsAndPolicies = true;
    private final List<NotificationAnswer> notificationAnswerList = new ArrayList<>();
    private final List<AcceptData> acceptDataList = new ArrayList<>();
    private TextInputLayout cpfTextInput, birthDateTextInput, passwordTextInput, passwordConfirmTextInput;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_signup_lgpd, container, false);

        mNameEditText = mView.findViewById(R.id.full_name_edit_text);
        cpfTextInput = mView.findViewById(R.id.cpf_text_input);
        cpfEditText = mView.findViewById(R.id.cpf_edit_text);
        birthDateTextInput = mView.findViewById(R.id.birthdate_text_input);
        birthDateEditText = mView.findViewById(R.id.birthdate_edit_text);
        mEmail = mView.findViewById(R.id.email_edit_text);
        mCellPhone = mView.findViewById(R.id.cellphone_edit_text);
        passwordTextInput = mView.findViewById(R.id.password_text_input);
        passwordEditText = mView.findViewById(R.id.password_edit_text);
        passwordConfirmTextInput = mView.findViewById(R.id.password_confirm_text_input);
        passwordConfirmEditText = mView.findViewById(R.id.password_confirm_edit_text);
        enterButton = mView.findViewById(R.id.button_enter);
        TextView clickHereText = mView.findViewById(R.id.text_click_here);

        tablelayoutLgpd = mView.findViewById(R.id.tablelayout_lgpd);

        healthInformationCheckbox = mView.findViewById(R.id.health_information_checkbox);
        offerInformationCheckbox = mView.findViewById(R.id.offer_information_checkbox);
        policyPrivacyCheckbox = mView.findViewById(R.id.policy_privacy_checkbox);
        policyDataUseCheckbox = mView.findViewById(R.id.policy_data_use_checkbox);

        TextView policyPrivacyTextview = mView.findViewById(R.id.policy_privacy_textview);
        TextView policyDataUseTextview = mView.findViewById(R.id.policy_data_use_textview);


        mProgressDialog = new ProgressDialog(getActivity());

        final Bundle bundle = this.getArguments();
        boolean hasEntered = false;
        if (bundle != null) {
            cpfEditText.setText(bundle.getString("CPF"));
            if (checkFieldCPF()) {
                queryCPF(ValidateCPF.removeSpecialCharacters(cpfEditText.getText().toString()));
                birthDateEditText.requestFocus();
                birthDateTextInput.requestFocus();
                hasEntered = true;
            }
        }

        cpfField(hasEntered);
        birthDateField();
        passwordField();
        passwordConfirmField();
        enterButton();

        clickHereText.setOnClickListener(v -> ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false));

        policyPrivacyTextview.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_PRIVACY_POLICE);
        });

        policyDataUseTextview.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PolicyOfDataUseActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_DATE_USE);
        });

        return mView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_PRIVACY_POLICE) {
                policyPrivacyCheckbox.setEnabled(true);
                policyPrivacyCheckbox.setChecked(true);
            } else if (requestCode == Constants.REQUEST_CODE_DATE_USE) {
                policyDataUseCheckbox.setEnabled(true);
                policyDataUseCheckbox.setChecked(true);
            }

        } else {

            if (requestCode == Constants.REQUEST_CODE_PRIVACY_POLICE) {
                policyPrivacyCheckbox.setEnabled(false);
                policyPrivacyCheckbox.setChecked(false);
            } else if (requestCode == Constants.REQUEST_CODE_DATE_USE) {
                policyDataUseCheckbox.setEnabled(false);
                policyDataUseCheckbox.setChecked(false);
            }
        }

        if (checkFieldCPF() &&
                checkFieldBirthDate() &&
                checkFieldPasswordConfirm() &&
                (!showCommunicationsAndPolicies ||
                        policyPrivacyCheckbox.isChecked() &&
                                policyDataUseCheckbox.isChecked())) {

            buttonEnabled(true);

        } else {

            buttonEnabled(false);

        }

    }

    private String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    private void buttonEnabled(boolean isButtonEnabled) {
        if (isButtonEnabled) {
            enterButton.setEnabled(true);
            enterButton.setTextColor(getResources().getColor(R.color.white_text));
            Drawable buttonIcon = getResources().getDrawable(R.drawable.ic_arrow_right_branco);
            enterButton.setCompoundDrawablesWithIntrinsicBounds(null, null, buttonIcon, null);
        } else {
            enterButton.setEnabled(false);
            enterButton.setTextColor(getResources().getColor(R.color.grey_text));
            Drawable buttonIcon = getResources().getDrawable(R.drawable.ic_arrow_right_cinza);
            enterButton.setCompoundDrawablesWithIntrinsicBounds(null, null, buttonIcon, null);
        }
    }

    private boolean checkFieldCPF() {
        String cpf = ValidateCPF.removeSpecialCharacters(cpfEditText.getText().toString());

        tablelayoutLgpd.setVisibility(View.GONE);


        if (TextUtils.isEmpty(cpf)) {
            cpfTextInput.setError(getString(R.string.error_cpf_empty));
        } else if (!ValidateCPF.isCPF(cpf)) {
            cpfTextInput.setError(getString(R.string.error_cpf_invalid));
        } else {
            if (showCommunicationsAndPolicies)
                tablelayoutLgpd.setVisibility(View.VISIBLE);

            return true;
        }
        return false;
    }

    private boolean checkFieldBirthDate() {
        String birthDate = birthDateEditText.getText().toString();
        if (TextUtils.isEmpty(birthDate)) {
            birthDateTextInput.setError(getString(R.string.error_birthdate_empty));
        } else if (!Utils.isBirthDateValid(birthDate)) {
            birthDateTextInput.setError(getString(R.string.error_birthdate_invalid));
        } else {
            return true;
        }
        return false;
    }

    private boolean checkFieldPassword() {
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordTextInput.setError(getString(R.string.error_password_empty));
        } else if (!Utils.isPasswordValid(password)) {
            passwordTextInput.setError(getString(R.string.error_password_short));
        } else {
            return true;
        }
        return false;
    }

    private boolean checkFieldPasswordConfirm() {
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();
        if (TextUtils.isEmpty(passwordConfirm)) {
            passwordConfirmTextInput.setError(getString(R.string.error_password_empty));
        } else if (!password.equals(passwordConfirm)) {
            passwordConfirmTextInput.setError(getString(R.string.error_password_not_equals));
        } else if (!Utils.isPasswordValid(passwordConfirm)) {
            passwordConfirmTextInput.setError(getString(R.string.error_password_short));
        } else {
            if (passwordConfirmTextInput.isErrorEnabled()) {
                passwordConfirmTextInput.setError(null);
            }
            return true;
        }
        return false;
    }

    private void queryCPF(String cpf) {
        try {
            setLoading(true);

            cpf = cpf.replace(".", "");
            cpf = cpf.replace("-", "");

            //Sempre ao consultar novo CPF limpar campos de aceites e desabilitar
            tablelayoutLgpd.setVisibility(View.GONE);

            policyPrivacyCheckbox.setChecked(false);
            policyDataUseCheckbox.setChecked(false);

            policyPrivacyCheckbox.setEnabled(false);
            policyDataUseCheckbox.setEnabled(false);


            mAPIService = ServiceGenerator.createServiceNoToken(APIService.class);

            Call<JsonElement> call = mAPIService.getLifeBasicInformation(new CheckPlatform(cpf, Constants.FEATURE_FLAG_PLATFORM));
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                    mNameEditText.setText("");
                    mEmail.setText("");
                    mCellPhone.setText("");

                    if (response.isSuccessful()) {
                        if (response.body().getAsJsonObject().has("messageIdentifier")) {
                            CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                            showSnackBar(responseCommit.messageIdentifier, TYPE_SUCCESS);
                            setLoading(false);
                        } else {
                            FirstAccessResponse user = new Gson().fromJson((response.body().getAsJsonObject()), FirstAccessResponse.class);
                            if (user != null && user.getName() != null) {
                                setLoading(false);

                                showCommunicationsAndPolicies = user.getShowCommunicationsAndPolicies();

                                if (showCommunicationsAndPolicies) {
                                    tablelayoutLgpd.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else if (response.code() == 404) {
                        setLoading(false);
                        try {
                            int resID = getResources().getIdentifier("MSG001", "string", getActivity().getPackageName());
                            String message = getResources().getString(resID);
                            showSnackBar(message, TYPE_FAILURE);
                        } catch (Exception ex) {
                            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, getActivity(), null);
                        }
                    } else {
                        setLoading(false);
                        showSnackBar(getResources().getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    setLoading(false);
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
        } catch (Exception ex) {
            LogUtils.info("FirtAccess", ex.getMessage());
        }
    }

    private void setLoading(Boolean loading) {
        try {
            if (loading) {
                mProgressDialog.setMessage(getString(R.string.loading_checking_cpf));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            } else {
                mProgressDialog.dismiss();
            }
        } catch (Exception ex) {
            LogUtils.info("FirtAccess", ex.getMessage());
        }
    }


    //METODO PARA REALIZAR O PRIMERIO ACESSO E CRIACAO DE USUARIO
    private void signUp(String cpf, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading_signing_up));
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (showCommunicationsAndPolicies) {
            setNotification(cpf);
            setAcceptData(cpf);
        }

        SignupRequest signupRequest = new SignupRequest(cpf,
                password,
                mEmail.getText().toString(),
                mCellPhone.getText().toString(),
                methodToken,
                policyPrivacyCheckbox.isChecked(),
                policyDataUseCheckbox.isChecked(),
                notificationAnswerList,
                acceptDataList,
                birthDateEditText.getText().toString()
        );


        Call<SignupResponse> call = mAPIService.signup(signupRequest);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(@NonNull Call<SignupResponse> call, @NonNull Response<SignupResponse> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    SignupResponse signupResponse = response.body();
                    if (signupResponse != null && signupResponse.authorized) {
                        Intent intent = new Intent(getActivity(), InformativeTokenActivity.class);
                        getActivity().startActivityForResult(intent, INFORMATIVE_TOKEN_RESULT);
                    } else {
                        showSnackBar(signupResponse.messageIdentifier, TYPE_FAILURE);
                        passwordConfirmEditText.requestFocus();
                    }
                } else if (response.code() == 404) {
                    progressDialog.dismiss();
                    int resID = getResources().getIdentifier("MSG005", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    showSnackBar(message, TYPE_FAILURE);

                } else {
                    progressDialog.dismiss();
                    showSnackBar(getResources().getString(R.string.problemServer), TYPE_FAILURE);
                }

                passwordConfirmEditText.requestFocus();
            }

            @Override
            public void onFailure(@NonNull Call<SignupResponse> call, @NonNull Throwable t) {
                progressDialog.dismiss();
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

    private void setAcceptData(String cpf) {
        //Envio dos dados do dispositivo
        AcceptData acceptData = new AcceptData();
        acceptData.cpf = cpf;
        acceptData.type = TERM_ACCEPT;
        acceptData.ipAddress = Utils.getIPAddress(true);
        acceptData.operationalSystem = Utils.getAndroidVersion();
        acceptData.appVersion = BuildConfig.VERSION_NAME;
        acceptData.device = "ANDROID";
        acceptData.versionPanelId = idTermOfAcceptancePrivacyPolicy;
        acceptDataList.add(acceptData);

        acceptData = new AcceptData();
        acceptData.cpf = cpf;
        acceptData.type = TERM_ACCEPT;
        acceptData.ipAddress = Utils.getIPAddress(true);
        acceptData.operationalSystem = Utils.getAndroidVersion();
        acceptData.appVersion = BuildConfig.VERSION_NAME;
        acceptData.device = "ANDROID";
        acceptData.versionPanelId = idTermOfAcceptanceDataUse;
        acceptDataList.add(acceptData);
    }

    private void setNotification(String cpf) {

        //Envio de notificações E-mail
        NotificationAnswer notificationAnswer = new NotificationAnswer();
        notificationAnswer.idAutorization = 1;
        notificationAnswer.idOption = 1;
        notificationAnswer.answer = (healthInformationCheckbox.isChecked()) ? 1 : 0;
        notificationAnswer.cpf = cpf;
        notificationAnswerList.add(notificationAnswer);

        notificationAnswer = new NotificationAnswer();
        notificationAnswer.idAutorization = 2;
        notificationAnswer.idOption = 1;
        notificationAnswer.answer = (offerInformationCheckbox.isChecked()) ? 1 : 0 ;
        notificationAnswer.cpf = cpf;
        notificationAnswerList.add(notificationAnswer);

        //SMS
        notificationAnswer = new NotificationAnswer();
        notificationAnswer.idAutorization = 1;
        notificationAnswer.idOption = 2;
        notificationAnswer.answer = (healthInformationCheckbox.isChecked()) ? 1 : 0;
        notificationAnswer.cpf = cpf;
        notificationAnswerList.add(notificationAnswer);

        notificationAnswer = new NotificationAnswer();
        notificationAnswer.idAutorization = 2;
        notificationAnswer.idOption = 2;
        notificationAnswer.answer = (offerInformationCheckbox.isChecked()) ? 1 : 0 ;
        notificationAnswer.cpf = cpf;
        notificationAnswerList.add(notificationAnswer);

        //Push Notification
        notificationAnswer = new NotificationAnswer();
        notificationAnswer.idAutorization = 1;
        notificationAnswer.idOption = 3;
        notificationAnswer.answer = (healthInformationCheckbox.isChecked()) ? 1 : 0;
        notificationAnswer.cpf = cpf;
        notificationAnswerList.add(notificationAnswer);

        notificationAnswer = new NotificationAnswer();
        notificationAnswer.idAutorization = 2;
        notificationAnswer.idOption = 3;
        notificationAnswer.answer = (offerInformationCheckbox.isChecked()) ? 1 : 0 ;
        notificationAnswer.cpf = cpf;
        notificationAnswerList.add(notificationAnswer);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void cpfField(boolean hasEntered) {
        if (!hasEntered)
            cpfEditText.requestFocus();
        cpfEditText.addTextChangedListener(new TextWatcher() {
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
                for (final char m : MASK_CPF.toCharArray()) {
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
                cpfEditText.setText(mask.toString());
                cpfEditText.setSelection(mask.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cpfEditText.setOnKeyListener((view, i, keyEvent) -> {

            if (i == EditorInfo.IME_ACTION_SEARCH ||
                    i == EditorInfo.IME_ACTION_DONE ||
                    keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                            keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                cpfEditText.clearFocus();
                return true;
            }
            return false;
        });

        cpfEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (checkFieldCPF()) {
                    queryCPF(ValidateCPF.removeSpecialCharacters(cpfEditText.getText().toString()));
                }
            } else {
                if (cpfTextInput.isErrorEnabled()) {
                    cpfTextInput.setError(null);
                }
            }
        });

        cpfEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (cpfEditText.getRight() - cpfEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    cpfEditText.getText().clear();
                    cpfEditText.requestFocus();
                    return true;
                }
            }
            return false;
        });
    }

    private void passwordField() {
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 8) {
                    if (!TextUtils.isEmpty(passwordConfirmEditText.getText().toString())) {
                        if (checkFieldCPF() &&
                                checkFieldBirthDate() &&
                                checkFieldPasswordConfirm() &&
                                policyPrivacyCheckbox.isChecked() &&
                                policyDataUseCheckbox.isChecked()) {
                            buttonEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                checkFieldPassword();
            } else {
                if (passwordTextInput.isErrorEnabled()) {
                    passwordTextInput.setError(null);
                }
            }
        });
    }

    private void passwordConfirmField() {
        passwordConfirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 8 && checkFieldPasswordConfirm()) {
                    if (checkFieldCPF() &&
                            checkFieldBirthDate() &&
                            checkFieldPassword() &&
                            (!showCommunicationsAndPolicies ||
                                    policyPrivacyCheckbox.isChecked() &&
                                            policyDataUseCheckbox.isChecked())) {
                        buttonEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordConfirmEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                checkFieldPasswordConfirm();
            } else {
                if (passwordConfirmTextInput.isErrorEnabled()) {
                    passwordConfirmTextInput.setError(null);
                }
            }
        });
    }

    private void birthDateField() {
        birthDateEditText.addTextChangedListener(new TextWatcher() {
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
                birthDateEditText.setText(mask.toString());
                birthDateEditText.setSelection(mask.length());

                if (birthDateEditText.getText().toString().matches(MASK_CPF)) {
                    if (checkFieldCPF() &&
                            checkFieldPassword() &&
                            checkFieldPasswordConfirm() &&
                            (!showCommunicationsAndPolicies ||
                                    policyPrivacyCheckbox.isChecked() &&
                                            policyDataUseCheckbox.isChecked())) {
                        buttonEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        birthDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                checkFieldBirthDate();
            } else {
                if (birthDateTextInput.isErrorEnabled()) {
                    birthDateTextInput.setError(null);
                }
            }
        });
    }

    private void enterButton() {
        buttonEnabled(false);
        enterButton.setOnClickListener(v -> {
            enterButton.requestFocus();
            //Valida a data de Nascimento se igual a digitada
            //String passedBirthDate = birthDateEditText.getText().toString();
            String cellphone = mCellPhone.getText().toString();
            String email = mEmail.getText().toString();

            //Valida termos de privacidade e uso de dados
            if (showCommunicationsAndPolicies && !policyPrivacyCheckbox.isChecked()) {
                int resID = getResources().getIdentifier("MSG503", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                showSnackBar(message, TYPE_FAILURE);
            } else if (showCommunicationsAndPolicies && !policyDataUseCheckbox.isChecked()) {
                int resID = getResources().getIdentifier("MSG504", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                showSnackBar(message, TYPE_FAILURE);
            } else {
                if (ValidateEmailAndCellphone(cellphone, email)) {
                    SendTokenToSmsOrEmail(cellphone, email);
                }
            }
        });
    }

    private boolean ValidateEmailAndCellphone(String cellphone, String email) {
        boolean EmailAndCellphoneIsValid = true;

        if (!email.equals("") && !Utils.isEmailValid(email)) {
            showSnackBar(getString(R.string.not_valid_email), TYPE_FAILURE);
            EmailAndCellphoneIsValid = false;
        }

        if (!cellphone.equals("") && !Utils.validateCellPhoneNumber(cellphone)) {
            int resID = getResources().getIdentifier("MSG371", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            showSnackBar(message, TYPE_FAILURE);
            EmailAndCellphoneIsValid = false;
        }

        return EmailAndCellphoneIsValid;
    }

    private void SendTokenToSmsOrEmail(String cellphone, String email) {
        if (email.equals("")) {
            int resID = getResources().getIdentifier("MSG507", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            showSnackBar(message, TYPE_FAILURE);
        } else if (cellphone.equals("")) {
            int resID = getResources().getIdentifier("MSG508", "string", getActivity().getPackageName());
            String message = getResources().getString(resID);
            showSnackBar(message, TYPE_FAILURE);
        } else {
            //Validar se mudou email e ou telefone, caso mudou os dois chamar modal e depois validate documet.
            if (!cellphone.equals("") && !email.equals("")) {
                ShowQuestionDiolog(getActivity());
            } else {
                if (!email.equals("")) {
                    methodToken = ValidateTokenRequest.SendMethod.EMAIL;
                } else {
                    methodToken = ValidateTokenRequest.SendMethod.SMS;
                }

                String cpf = ValidateCPF.removeSpecialCharacters(cpfEditText.getText().toString());
                String password = passwordConfirmEditText.getText().toString();
                signUp(cpf, password);
            }
        }
    }

    private void ShowQuestionDiolog(Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_question_token);

        Button emailButton = dialog.findViewById(R.id.emailButton);
        Button smsButton = dialog.findViewById(R.id.smsButton);
        Button backButton = dialog.findViewById(R.id.backButton);

        emailButton.setOnClickListener(view -> {
            methodToken = ValidateTokenRequest.SendMethod.EMAIL;
            dialog.cancel();
            String cpf = ValidateCPF.removeSpecialCharacters(cpfEditText.getText().toString());
            String password = passwordConfirmEditText.getText().toString();
            signUp(cpf, password);
        });

        smsButton.setOnClickListener(view -> {
            methodToken = ValidateTokenRequest.SendMethod.SMS;
            dialog.cancel();
            String cpf = ValidateCPF.removeSpecialCharacters(cpfEditText.getText().toString());
            String password = passwordConfirmEditText.getText().toString();
            signUp(cpf, password);
        });

        backButton.setOnClickListener(view -> dialog.cancel());

        dialog.show();
    }

    private void showSnackBar(String message, int typeFailure) {
        Snackbar snackbar = Snackbar.make(mView, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeFailure == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }
}
