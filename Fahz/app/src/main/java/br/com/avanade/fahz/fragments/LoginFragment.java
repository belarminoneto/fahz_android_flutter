package br.com.avanade.fahz.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

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
import java.util.Objects;

import br.com.avanade.fahz.BuildConfig;
import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.ChangePasswordActivity;
import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.LoginResponse;
import br.com.avanade.fahz.model.ResetPasswordRequest;
import br.com.avanade.fahz.model.ResetPasswordResponse;
import br.com.avanade.fahz.model.servicemodels.LoginRequest;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.NavigationHost;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import br.com.avanade.fahz.util.ValidateCPF;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.ENVIRONMENTPRD;
import static br.com.avanade.fahz.util.Constants.MASK_CPF;
import static com.google.firebase.messaging.Constants.MessageNotificationKeys.CHANNEL;

public class LoginFragment extends Fragment {
    private APIService mAPIService;
    private Button enterButton;
    private Button supportButton;
    private View view;

    private TextInputLayout CPFTextInput;
    private EditText CPFEditText;
    private TextInputLayout passwordTextInput;
    private EditText passwordEditText;
    private ProgressDialog mProgressDialog;
    private TextView tvVersao;

    private SessionManager sessionManager;
    private LoginRequest loginRequest;
    private boolean canLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        CPFTextInput = view.findViewById(R.id.cpf_text_input);
        CPFEditText = view.findViewById(R.id.cpf_edit_text);
        passwordTextInput = view.findViewById(R.id.password_text_input);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        TextView clickHereText = view.findViewById(R.id.text_click_here);
        TextView resetPassword = view.findViewById(R.id.text_reset_password);
        enterButton = view.findViewById(R.id.button_enter);
        tvVersao = view.findViewById(R.id.tv_versao);
        supportButton = view.findViewById(R.id.button_support);

        sessionManager = new SessionManager(getContext());
        FahzApplication.getInstance().onCreate();

        tvVersao.setText(getString(R.string.accenture, BuildConfig.VERSION_NAME, BuildConfig.ENVIRONMENT_DESCRIPTION));

        cpfField();
        passwordField();

        buttonEnabled(false);
        enterButton.setOnClickListener(v -> {
            String cpf = ValidateCPF.removeSpecialCharacters(CPFEditText.getText().toString());
            String password = passwordEditText.getText().toString();
            sendPost(cpf, password);
        });

        supportButton.setOnClickListener(v -> {
            FahzApplication.getFlutterEngine().getNavigationChannel().pushRoute("support");

            MethodChannel mc = new MethodChannel(FahzApplication.getFlutterEngine()
                    .getDartExecutor().getBinaryMessenger(),"FLUTTER_TESTE");

            mc.invokeMethod("CHAMANDO_METODO_FLUTTER", "MARCOS");

            mc.setMethodCallHandler((methodCall, result) ->
                    {
                        if(methodCall.method.equals("CHAMANDO_METODO_ANDROID"))
                        {
                            result.success("NOME ENVIADO DO FLUTTER:  "+ methodCall.argument("data"));
                        }
                        else
                        {
                            System.out.println("new method came "+methodCall.method);
                        }

                    }
            );


            startActivity(
                  FlutterActivity
                          .withCachedEngine("fahzv2_engine")
                          .build(LoginFragment.this.getActivity())
            );

        });

        clickHereText.setOnClickListener(v -> {
            String CPF = CPFEditText.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString("CPF", CPF);
            //SignupFragment fragment = new SignupFragment();
            FirstAccessFragment fragment = new FirstAccessFragment();
            fragment.setArguments(bundle);
            ((NavigationHost) Objects.requireNonNull(getActivity())).navigateTo(fragment, true);
        });

        resetPassword.setOnClickListener(v -> {
            if (TextUtils.isEmpty(CPFEditText.getText().toString())) {
                CPFTextInput.setError(getString(R.string.error_cpf_empty));
                CPFEditText.requestFocus();
            } else {
                resetPassword(CPFEditText.getText().toString(), "");
            }
        });

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void cpfField() {
        CPFEditText.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;
            String oldString = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (CPFTextInput.isErrorEnabled()) {
                    CPFTextInput.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String str = unmask(s.toString());
                StringBuilder mask = new StringBuilder();
                if (isUpdating){
                    oldString = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (final char m : MASK_CPF.toCharArray()){
                    if (m != '#' && str.length() > oldString.length()){
                        mask.append(m);
                        continue;
                    }
                    try {
                        mask.append(str.charAt(i));
                    }catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                CPFEditText.setText(mask.toString());
                CPFEditText.setSelection(mask.length());

                if (CPFEditText.getText().length() < MASK_CPF.length()) {
                    buttonEnabled(false);
                } else {
                    String cpf = ValidateCPF.removeSpecialCharacters(CPFEditText.getText().toString());
                    if (ValidateCPF.isCPF(cpf)) {
                        if (TextUtils.isEmpty(passwordEditText.getText().toString()) || passwordEditText.getText().length() < 8) {
                            buttonEnabled(false);
                        } else {
                            buttonEnabled(true);
                        }
                    } else {
                        CPFTextInput.setError(getString(R.string.error_cpf_invalid));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        CPFEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String CPF = ValidateCPF.removeSpecialCharacters(CPFEditText.getText().toString());
                if (TextUtils.isEmpty(CPF)) {
                    CPFTextInput.setError(getString(R.string.error_cpf_empty));
                } else if (!ValidateCPF.isCPF(CPF)) {
                    CPFTextInput.setError(getString(R.string.error_cpf_invalid));
                }
            } else {
                if (CPFTextInput.isErrorEnabled()) {
                    CPFTextInput.setError(null);
                }
            }
        });
        CPFEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (CPFEditText.getRight() - CPFEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    CPFEditText.getText().clear();
                    CPFEditText.requestFocus();
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
                final String str = s.toString();
                String CPF = ValidateCPF.removeSpecialCharacters(CPFEditText.getText().toString());
                if (str.length() >= 8 && ValidateCPF.isCPF(CPF) ){
                    buttonEnabled(true);
                } else {
                    buttonEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
                    passwordTextInput.setError(getString(R.string.error_password_empty));
                } else if (!isPasswordValid(passwordEditText.getText())) {
                    passwordTextInput.setError(getString(R.string.error_password_short));
                }
            } else {
                if (passwordTextInput.isErrorEnabled()) {
                    passwordTextInput.setError(null);
                }
            }
        });
    }

    private void buttonEnabled(boolean isButtonEnabled) {
        if(isButtonEnabled) {
            enterButton.setEnabled(true);
            enterButton.setTextColor(getResources().getColor(R.color.white_text));
            Drawable buttonIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_right_branco, null);
            enterButton.setCompoundDrawablesWithIntrinsicBounds(null, null, buttonIcon, null);
        } else {
            enterButton.setEnabled(false);
            enterButton.setTextColor(getResources().getColor(R.color.grey_text));
            Drawable buttonIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_right_cinza, null);
            enterButton.setCompoundDrawablesWithIntrinsicBounds(null, null, buttonIcon, null);
        }
    }

    private static String unmask(String s) {
        return s.replaceAll("[^0-9]*","");
    }

    private boolean isPasswordValid(@Nullable Editable text){
        return text != null && text.length() >= 8;
    }

    private void sendPost(final String cpf, String password) {
        setLoading(true);

        loginRequest = new LoginRequest();
        loginRequest.CPF = cpf;
        loginRequest.Password = password;
        loginRequest.Platform = Constants.FEATURE_FLAG_PLATFORM;
        new GetDataSync().execute();

    }

    private void resetPassword(String cpf, String method) {
        setLoading(true);
        final ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.cpf = cpf;
        resetPasswordRequest.method = method;

        mAPIService = ServiceGenerator.createServiceNoToken(APIService.class);
        Call<ResetPasswordResponse> call = mAPIService.resetPassword(resetPasswordRequest);
        call.enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(@NonNull Call<ResetPasswordResponse> call, @NonNull Response<ResetPasswordResponse> response) {
                if (response.isSuccessful()) {
                    setLoading(false);
                    ResetPasswordResponse resetPasswordResponse = response.body();
                    if (resetPasswordResponse != null && resetPasswordResponse.committed) {
                        Utils.showSimpleDialog(getString(R.string.dialog_title_upload), resetPasswordResponse.message, null, getActivity(), null);
                    } else {
                        assert resetPasswordResponse != null;
                        if (resetPasswordResponse.email.equals("") && resetPasswordResponse.cellphone.equals("")) {
                            showSnackBar(resetPasswordResponse.message);
                        } else {
                            choiceMethodDialog(resetPasswordResponse);
                        }
                    }
                } else {
                    showSnackBar(getString(R.string.problemServer));
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<ResetPasswordResponse> call, @NonNull Throwable t) {
                setLoading(false);
                if (getActivity() != null) {
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())));
                    else if (t instanceof UnknownHostException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())));
                    else
                        showSnackBar(t.getMessage());
                }
            }
        });
    }

    private void choiceMethodDialog(ResetPasswordResponse response) {
        String[] items = new String[2];
        items[0] = response.email;
        items[1] = response.cellphone;

        showSimpleDialog(getString(R.string.dialog_question), items, getContext());
    }

    private void setLoading(Boolean loading) {
        if (loading) {
            mProgressDialog.setMessage("Aguarde um momento");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    private void showSimpleDialog(String title, String[] options,
                                  Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert_list);

        Button chatButton = dialog.findViewById(R.id.dialog_confirm_button);
        Button cancel = dialog.findViewById(R.id.dialog_cancel_button);

        TextView txtTitle = dialog.findViewById(R.id.dialog_alert_title);

        final RadioGroup radioChoice = dialog.findViewById(R.id.radioChoice);
        RadioButton radioEmail = dialog.findViewById(R.id.radioEmail);
        radioEmail.setText(String.format("Email: %s", options[0]));
        RadioButton radioSMS = dialog.findViewById(R.id.radioSMS);
        radioSMS.setText(String.format("Celular: %s", options[1]));

        txtTitle.setText(title);

        cancel.setOnClickListener(view -> dialog.cancel());

        chatButton.setOnClickListener(view -> {
            int selectedId = radioChoice.getCheckedRadioButtonId();
            if (selectedId != -1) {
                resetPassword(CPFEditText.getText().toString(), selectedId == R.id.radioSMS ? "SMS" : "Email");
                dialog.cancel();
            }
        });

        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    public class GetDataSync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getData();
            } catch (IOException | JSONException e) {
                LogUtils.error(getClass().getSimpleName(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (canLogin) {
                mAPIService = ServiceGenerator.createServiceNoToken(APIService.class);
                Call<JsonElement> call = mAPIService.login(loginRequest);
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            LoginResponse loginResponse = new Gson().fromJson((response.body().getAsJsonObject()), LoginResponse.class);

                            if (loginResponse != null) {
                                if (loginResponse.authorized) {
                                    sessionManager.createLoginSession(loginResponse.token);
                                    //Caso o Usuário seja obrigado a trocar a senha, ele já é encaminhado para a Tela de Troca
                                    if (loginResponse.changePassword) {
                                        Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                                        intent.putExtra(Constants.HAS_CHANGE_PASSWORD, true);
                                        startActivity(intent);
                                    } else {
                                        startActivity(new Intent(getContext(), MainActivity.class));
                                    }
                                    setLoading(false);
                                    Objects.requireNonNull(getActivity()).finish();

                                    SessionManager manager = new SessionManager(getActivity());
                                    manager.createPreference(Constants.CAN_SEARCH_PICTURE, "true");

                                } else {
                                    setLoading(false);
                                    showSnackBar(loginResponse.messageIdentifier);
                                }
                            }
                        } else {
                            setLoading(false);
                            try {
                                assert response.errorBody() != null;
                                String data = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(data);
                                String message = jObjError.getString("Message");
                                showSnackBar(message);
                            } catch (Exception ex) {
                                Utils.showSimpleDialog(getString(R.string.dialog_title), "O CONECTA FAHZ está temporariamente indisponível. Por favor tente acessar mais tarde.", null, getActivity(), null);
                            }
                        }
                    }







                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        setLoading(false);
                        if (getActivity() != null) {
                            if (t instanceof SocketTimeoutException)
                                showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())));
                            else if (t instanceof UnknownHostException)
                                showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())));
                            else
                                showSnackBar(t.getMessage());
                        }
                    }
                });
            } else {
                setLoading(false);
                showSnackBar(getResources().getString(getResources().getIdentifier("MSG413", "string", Objects.requireNonNull(getActivity()).getPackageName())));

            }
        }

        private void getData() throws IOException, JSONException {
            //noinspection ConstantConditions
            if (BuildConfig.CURRENT_ENVIRONMENT.equals(ENVIRONMENTPRD)) {
                JSONObject json = readJsonFromUrl(BuildConfig.CHECK_VERSION);
                try {
                    String response = json.getString("Version");
                    double version = Double.parseDouble(response);
                    double versionName = Double.parseDouble(BuildConfig.VERSION_NAME);
                    canLogin = versionName >= version;
                } catch (JSONException e) {
                    LogUtils.error(getClass().getSimpleName(), e);
                }
            } else {
                canLogin = true;
            }
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
            try (InputStream is = new URL(url).openStream()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String jsonText = readAll(rd);
                return new JSONObject(jsonText);
            }
        }
    }
}

