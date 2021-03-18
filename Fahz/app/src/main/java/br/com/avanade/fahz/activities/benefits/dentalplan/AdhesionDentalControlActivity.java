package br.com.avanade.fahz.activities.benefits.dentalplan;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.DocumentsActivity;
import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.activities.benefits.BenefitsControlActivity;
import br.com.avanade.fahz.activities.token.InformativeTokenActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.fragments.AddressFragment;
import br.com.avanade.fahz.fragments.BankDataFragment;
import br.com.avanade.fahz.fragments.PersonalDataFragment;
import br.com.avanade.fahz.fragments.benefits.healthplan.ChooseOperatorFragment;
import br.com.avanade.fahz.model.AdhesionPlanRequest;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.TitularResponse;
import br.com.avanade.fahz.model.ValidateTokenRequest;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.SystemBehavior;
import br.com.avanade.fahz.util.Utils;
import br.com.avanade.fahz.util.ValidateBankDataErrorException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.activities.benefits.dentalplan.AdhesionDentalControlActivity.AdhesionDentalFragment.ADEHESION;
import static br.com.avanade.fahz.activities.benefits.dentalplan.AdhesionDentalControlActivity.AdhesionDentalFragment.PERSONALDATA;
import static br.com.avanade.fahz.util.Constants.DOCUMENT_FOR_RESULT;
import static br.com.avanade.fahz.util.Constants.INFORMATIVE_TOKEN_RESULT;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class AdhesionDentalControlActivity extends NavDrawerBaseActivity {

    public enum AdhesionDentalFragment {
        PERSONALDATA,
        ADDRESSDATA,
        BANKDATA,
        ADEHESION
    }

    boolean isFirstFragment;

    @BindView(R.id.adhesion_dental_manager_container)
    RelativeLayout mAdhesionManagerContainer;
    @BindView(R.id.adhesionDentalContainer)
    LinearLayout mAdhesionDentalContainer;
    @BindView(R.id.txtProfileDescription)
    TextView mProfileDescription;
    @BindView(R.id.btnContinue)
    Button mBtnContinue;

    private ValidateTokenRequest.SendMethod methodToken =null;

    private final String LOG_KEY = AdhesionDentalControlActivity.class.getSimpleName();
    private TitularResponse mResponse;
    private APIService mAPIService;
    private ProgressDialog mProgressDialog;
    AdhesionDentalFragment adhesionDentalFragment;
    private SessionManager sessionManager;
    private String previousEmail;
    private String previousPhone;

    private boolean hideBankData;

    private PersonalDataFragment mDataFragment;
    private AddressFragment mAddressFragment;
    private BankDataFragment mBankDataFragment;

    private int exceptionOperatorCode;
    private boolean canCallValidate = false;

    private boolean editLife;
    private String messageIdentifier;

    @Override
    protected void onResume() {
        super.onResume();

        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.getToken() == null || sessionManager.getToken().isEmpty()) {
            sessionManager.logout();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhesion_dental_control);
        ButterKnife.bind(this);

        setImageHeaderVisibility(true);
        setMenuVisible(false);
        mProgressDialog = new ProgressDialog(this);
        sessionManager = new SessionManager(getApplicationContext());

        isFirstFragment = true;
        adhesionDentalFragment = (AdhesionDentalFragment) getIntent().getSerializableExtra(Constants.ADHESION_DENTAL_CONTROL);
        hideBankData = getIntent().getBooleanExtra(Constants.ADHESION_HIDE_BANK, false);

        if (adhesionDentalFragment == PERSONALDATA) {
            editLife = true;
            loadTitularData();
        } else if (adhesionDentalFragment == ADEHESION) {
            editLife = false;
            setFragment(adhesionDentalFragment);
        }

        setupUi();
    }

    //METODO PARA CONFIRUAÇÂO DE TELA
    private void setupUi() {
        toolbarTitle.setText(getString(R.string.dental_plan));
        setImageHeaderVisibility(false);
        setCancelOperation(true);
    }

    private void loadTitularData() {
        setLoading(true, getString(R.string.loading_searching));
        mAPIService = ServiceGenerator.createService(APIService.class);
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        Call<TitularResponse> call = mAPIService.getDataTitular(new CPFInBody(cpf));
        call.enqueue(new Callback<TitularResponse>() {
            @Override
            public void onResponse(Call<TitularResponse> call, Response<TitularResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                mResponse = response.body();
                previousEmail = mResponse.email;
                previousPhone = mResponse.cellphone;

                setFragment(adhesionDentalFragment);
                setLoading(false, "");
            }

            @Override
            public void onFailure(Call<TitularResponse> call, Throwable t) {
                if(t instanceof SocketTimeoutException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if(t instanceof UnknownHostException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
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
        super.onBackPressed();

        switch (adhesionDentalFragment) {
            case PERSONALDATA:
                break;
            case ADDRESSDATA:
                changeHeaderTitle(getString(R.string.text_change_personal_data));
                adhesionDentalFragment = PERSONALDATA;
                break;
            case BANKDATA:
                changeHeaderTitle(getString(R.string.text_change_address));
                adhesionDentalFragment = AdhesionDentalFragment.ADDRESSDATA;
                break;
            case ADEHESION:
                changeHeaderTitle(getString(R.string.text_change_bank_data));
                adhesionDentalFragment = AdhesionDentalFragment.BANKDATA;
                Intent intent = new Intent(this, BenefitsControlActivity.class);
                intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.DENTALPLAN);
                startActivity(intent);
                this.finish();
                break;
        }
    }

    private void setLoading(boolean loading, String text) {
        if (loading) {
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else
            mProgressDialog.dismiss();
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mAdhesionManagerContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mAdhesionManagerContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void setFragment(AdhesionDentalFragment type) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (type) {
            case PERSONALDATA:
                changeHeaderTitle(getString(R.string.text_change_personal_data));
                mDataFragment = PersonalDataFragment.newInstance(mResponse);
                ft.replace(R.id.adhesionDentalContainer, mDataFragment, PERSONALDATA.toString());
                break;
            case ADDRESSDATA:
                changeHeaderTitle(getString(R.string.text_change_address));
                mAddressFragment = AddressFragment.newInstance(mResponse.address);
                ft.replace(R.id.adhesionDentalContainer, mAddressFragment, AdhesionDentalFragment.ADDRESSDATA.toString());

                break;
            case BANKDATA:
                mBtnContinue.setText(R.string.continue_button);
                changeHeaderTitle(getString(R.string.text_change_bank_data));
                mBankDataFragment = BankDataFragment.newInstance(mResponse.bank);
                ft.replace(R.id.adhesionDentalContainer, mBankDataFragment, AdhesionDentalFragment.BANKDATA.toString());

                break;
            case ADEHESION:
                mBtnContinue.setText(R.string.button_save);
                changeHeaderTitle(getString(R.string.choose_operator_dental));
                ChooseOperatorFragment sFragment = ChooseOperatorFragment.newInstance(false, Constants.BENEFITDENTAL);
                ft.replace(R.id.adhesionDentalContainer, sFragment, ADEHESION.toString());

                break;
            default:
                showSnackBar(getString(R.string.NoFragmentFoud), TYPE_FAILURE);
        }

        if (isFirstFragment) {
            isFirstFragment = false;
        } else {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    //CHAMADA DO BOTÃO DE PROSSEGUIR EXISTENTE NO LAYOUT, ELE QUE DITA O FLUXO DA TELA
    @OnClick(R.id.btnContinue)
    public void continueNavigation(View view)
    {
        try {
            switch (adhesionDentalFragment)
            {
                case PERSONALDATA:
                    if(mDataFragment.validateRequiredData())
                    {
                        adhesionDentalFragment = AdhesionDentalFragment.ADDRESSDATA;
                        setFragment(AdhesionDentalFragment.ADDRESSDATA);
                    }
                    break;
                case ADDRESSDATA:
                    if(mAddressFragment.validateRequiredData())
                    {
                        if (hideBankData)
                        {
                            validateCheckToken(true);
                        }
                        else
                        {
                            adhesionDentalFragment = AdhesionDentalFragment.BANKDATA;
                            setFragment(AdhesionDentalFragment.BANKDATA);
                        }
                    }
                    break;
                case BANKDATA:
                    mResponse.bank = mBankDataFragment.getBankData();
                    validateCheckToken(true);
                    break;
                case ADEHESION:
                    validateCheckToken(false);
                    break;
            }
        } catch (ValidateBankDataErrorException ex) {
            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, this, null);
        }
    }

    //METODO QUE TROCA O TEXTO INDICADOR DE HEADER, PARA SABER EM QUE PARTE DO FLUXO A PESSOA SE ENCONTRA
    public void changeHeaderTitle(String text) {
        mProfileDescription.setText(text);
    }

    //ENDPOINT DE SALVAR DADOS PESSOAIS, CASO TENHA HAVIDO ALGUMA ALTERAÇÃO
    private void savePersonalData() {
        try {
            final String identifier = "Dados Pessoais";

            setLoading(true, getString(R.string.saving_information));
            mAPIService = ServiceGenerator.createService(APIService.class);
            mResponse = mDataFragment.getPersonalData();

            mResponse.address = mAddressFragment.getAddressData();
            if (mBankDataFragment != null && mBankDataFragment.getBankData() != null)
                mResponse.bank = mBankDataFragment.getBankData();

            final boolean isSendToken = methodToken != null;
            String methodTokenString = methodToken != null ?  methodToken.toString() : null;

            Call<CommitResponse> call = mAPIService.editTitular(mResponse, false,isSendToken, methodTokenString);
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(Call<CommitResponse> call, Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    CommitResponse response2 = response.body();

                    if (response.isSuccessful()) {
                        if (response2.commited) {
                            if(isSendToken){
                                messageIdentifier = response2.messageIdentifier;
                                Intent intent = new Intent(AdhesionDentalControlActivity.this, InformativeTokenActivity.class);
                                startActivityForResult(intent,INFORMATIVE_TOKEN_RESULT);
                            }
                            else {
                                showSnackBarDismiss(response2.messageIdentifier, TYPE_SUCCESS, new Snackbar.Callback() {

                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        adhesionDentalFragment = ADEHESION;
                                        setFragment(ADEHESION);
                                    }
                                });
                            }

                        } else {
                            editLife = true;
                            showSnackBar(response2.messageIdentifier, TYPE_FAILURE);
                        }
                    } else {
                        if (!TextUtils.isEmpty(response.raw().message()))
                            showSnackBar(identifier + " - " + response.raw().message(), TYPE_FAILURE);
                        else
                            showSnackBar(identifier + " - " + getString(R.string.problemServer), TYPE_FAILURE);
                        setLoading(false, "");
                    }

                    setLoading(false, "");
                }

                @Override
                public void onFailure(Call<CommitResponse> call, Throwable t) {
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
        catch (ValidateBankDataErrorException ex)
        {
            setLoading(false, "");
            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, this, null);
        }

        catch (Exception ex)
        {
            setLoading(false,"");
            showSnackBar(ex.getMessage(),TYPE_FAILURE);
        }
    }

    //INCLUSAO DE ADESAO AO PLANO DENTAL
    private void includeDentalPlan(AdhesionPlanRequest request) {
        final String identifier = "Adesão ao Plano";

        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.saving_information));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<CommitResponse> call = mAPIService.adhesionDentalPlan(request);
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        CommitResponse commitResponse = response.body();
                        if (commitResponse.commited) {
                            int resID = getResources().getIdentifier("MSG184", "string", getPackageName());
                            String message = getResources().getString(resID);
                            showSnackBarDismiss(commitResponse.messageIdentifier, TYPE_SUCCESS,new Snackbar.Callback() {

                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    Intent intent = new Intent(AdhesionDentalControlActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    //overridePendingTransition(0, 0);
                                }
                            });
                        } else {
                            int resID = getResources().getIdentifier("MSG166", "string", getPackageName());
                            String message = getResources().getString(resID);

                            if(commitResponse.messageIdentifier.equals(message))
                                showSnackBarDismiss(message, TYPE_SUCCESS,new Snackbar.Callback() {

                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        Intent intent = new Intent(AdhesionDentalControlActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        //overridePendingTransition(0, 0);
                                    }
                                });
                            else
                                showSnackBar(commitResponse.messageIdentifier, TYPE_FAILURE);
                        }

                        setLoading(false, "");

                    } else {
                        if(!TextUtils.isEmpty(response.raw().message()))
                            showSnackBar(identifier +" - " +response.raw().message(), TYPE_FAILURE);
                        else
                            showSnackBar(identifier +" - " +getString(R.string.problemServer), TYPE_FAILURE);
                        setLoading(false, "");
                    }
                }

                @Override
                public void onFailure(Call<CommitResponse> call, Throwable t) {
                    setLoading(false, "");
                    if(t instanceof SocketTimeoutException)
                        showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                    else if(t instanceof UnknownHostException)
                        showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }

            });
        }
    }

    //METODO CHAMADO PELO FRAGMENT AdhesionHealthFragment SEMPRE QUE DA LISTA DE OPERADORAS DE EXCEÇÂO A SELEÇÂO MUDA, SALVANDO
    //ASIM QUAL O ID DA OPERADORA DE EXCEÇÂO.
    public void registerExceptionOperator(int operatorCode)
    {
        exceptionOperatorCode = operatorCode;
    }

    private void validateCheckToken(boolean fromBank) {
        if(editLife) {
            boolean hasChangedEmail = ((previousEmail!= null && !previousEmail.isEmpty())  && !previousEmail.equals(mResponse.email))
                    ||  ((previousEmail== null || previousEmail.isEmpty()) && !mResponse.email.isEmpty());
            boolean hasChangePhone = (previousPhone!= null && !previousPhone.isEmpty() &&!previousPhone.equals(mResponse.cellphone))
                    ||((previousPhone == null || previousPhone.isEmpty())  && !mResponse.cellphone.isEmpty());

            boolean emailIsEmpty = (mResponse.email== null || mResponse.email.isEmpty());
            boolean phoneIsEmpty = (mResponse.cellphone == null || mResponse.cellphone.isEmpty());

            //Validar se mudou email e ou telefone, caso mudou os dois chamar modal e depois validate documet.
            if (hasChangedEmail && hasChangePhone && !emailIsEmpty && !phoneIsEmpty){

                String[] items = new String[2];
                items[0] = getString(R.string.hint_email);
                items[1] = getString(R.string.hint_cellphone);

                ShowQuestionDiolog(getString(R.string.edit_values_token), items, null, AdhesionDentalControlActivity.this, fromBank);
            } else {
                if (hasChangedEmail) {
                    if (!emailIsEmpty)
                        methodToken = ValidateTokenRequest.SendMethod.EMAIL;
                    else if(emailIsEmpty && !phoneIsEmpty)
                        methodToken = ValidateTokenRequest.SendMethod.SMS;
                }
                else if (hasChangePhone) {
                    if (!phoneIsEmpty)
                        methodToken = ValidateTokenRequest.SendMethod.SMS;
                    else if (phoneIsEmpty && !emailIsEmpty)
                        methodToken = ValidateTokenRequest.SendMethod.EMAIL;
                }

                validateShowDocuments(fromBank);
            }
        }
        else
            validateShowDocuments(fromBank);
    }

    private void ShowQuestionDiolog(String title, String[] options, @Nullable String buttonMessage,
                                    Context context, final boolean fromBank) {

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
                validateShowDocuments(fromBank);
            }
        });

        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodToken = ValidateTokenRequest.SendMethod.SMS;
                dialog.cancel();
                validateShowDocuments(fromBank);
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

    //CHAMADA PARA FINALIZAR O FLUXO. VALIDA SE UMA OPERADORA DIFERENTE FOI SELECIONADA. E MOSTRA OS DOCUMENTOS APóS ISSO
    private void validateShowDocuments(boolean fromBank) {
        try {
            SystemBehavior.BehaviorEnum id;

            if (fromBank)
                id = SystemBehavior.BehaviorEnum.EDIT_LIFE;
            else
                id = SystemBehavior.BehaviorEnum.INCL_LIFE_DENTAL;

            final int index = id.id();
            final Intent intent = new Intent(AdhesionDentalControlActivity.this, DocumentsActivity.class);

            if (exceptionOperatorCode != 0) {
                int resID = getResources().getIdentifier("MSG097", "string", getPackageName());
                String message = getResources().getString(resID);

                Utils.showQuestionDialog(getString(R.string.dialog_title), message, this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra(Constants.ORIGIN_CALL_DOCUMENT, DocumentsActivity.OriginCall.FROMADHESION);
                        intent.putExtra(Constants.CONTEXT_DOCUMENT, index);
                        intent.putExtra(Constants.CONTEXT_DOCUMENT_CPF, FahzApplication.getInstance().getFahzClaims().getCPF());

                    }
                });
            } else {
                intent.putExtra(Constants.ORIGIN_CALL_DOCUMENT, DocumentsActivity.OriginCall.FROMADHESION);
                intent.putExtra(Constants.CONTEXT_DOCUMENT, index);
                intent.putExtra(Constants.CONTEXT_DOCUMENT_CPF, FahzApplication.getInstance().getFahzClaims().getCPF());
            }

            if (fromBank && !hideBankData) {
                mResponse.bank = mBankDataFragment.getBankData();
                if (mResponse.bank != null && mResponse.bank.bank != 0 && !mResponse.bank.account.equals("")) {
                    intent.putExtra(Constants.REQUEST_ACCOUNT_DOCUMENT, Constants.Comprovante_de_Conta_Corrente);
                }
            }

            startActivityForResult(intent, DOCUMENT_FOR_RESULT);
        } catch (ValidateBankDataErrorException ex) {
            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, this, null);
        }
    }

    //RESULTADO DA VOLTA DA CHAMADA DA TELA DE DOCUMENTO
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DOCUMENT_FOR_RESULT) {
            if (resultCode == Activity.RESULT_OK) {

                if (editLife) {
                    editLife = false;

                    //CHAMADA AOS ENDPOINTS RELACIONADOS NO FLUXO
                    savePersonalData();

                } else {
                    AdhesionPlanRequest request = new AdhesionPlanRequest();
                    request.setCpf(FahzApplication.getInstance().getFahzClaims().getCPF());
                    request.setOperatorCode(exceptionOperatorCode);
                    if (exceptionOperatorCode != 0) {
                        request.setException(true);
                        request.setMessage("");
                    }

                    includeDentalPlan(request);
                }
            } else {
                showSnackBar("Os Documentos não foram salvos com sucesso.", TYPE_FAILURE);
            }
        } else if (requestCode == INFORMATIVE_TOKEN_RESULT) {
            showSnackBarDismiss(messageIdentifier, TYPE_SUCCESS, new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    Intent intent = new Intent(AdhesionDentalControlActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
    }


}
