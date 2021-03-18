package br.com.avanade.fahz.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.token.InformativeTokenActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.fragments.AddressFragment;
import br.com.avanade.fahz.fragments.BankDataFragment;
import br.com.avanade.fahz.fragments.PersonalDataFragment;
import br.com.avanade.fahz.model.BankResponse;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.TitularResponse;
import br.com.avanade.fahz.model.ValidateTokenRequest;
import br.com.avanade.fahz.model.lgpdModel.EntitieVisible;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SystemBehavior;
import br.com.avanade.fahz.util.Utils;
import br.com.avanade.fahz.util.ValidateBankDataErrorException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.DOCUMENT_FOR_RESULT;
import static br.com.avanade.fahz.util.Constants.INFORMATIVE_TOKEN_RESULT;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ProfileActivity extends NavDrawerBaseActivity {
    private APIService mAPIService;
    private ProgressDialog mProgressDialog;
    private final String LOG_KEY = ProfileActivity.class.getSimpleName();
    private PersonalDataFragment mDataFragment;
    private AddressFragment mAddressFragment;
    private BankDataFragment mBankDataFragment;
    private TitularResponse mResponse;
    private String previousEmail;
    private String previousPhone;
    private boolean mFromRefund;
    private boolean mFromCard;
    private boolean mCanViewPersonalData;
    private boolean mCanViewAddress;
    private boolean mCanViewBankData;

    private ValidateTokenRequest.SendMethod methodToken =null;

    private int idBehavior;

    private Dialog dialog;

    @BindView(R.id.save_button)
    Button mButton;
    @BindView(R.id.profile_container)
    ConstraintLayout mProfileContainer;
    @BindView(R.id.txtProfileDescription)
    TextView mProfileDescription;

    private String messageIdentifier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);

        mFromRefund = getIntent().getBooleanExtra(Constants.BANK_FOR_REFUND, false);
        mFromCard = getIntent().getBooleanExtra(Constants.FROM_CARD, false);

        ViewPager viewPager = findViewById(R.id.viewPager);
        if (viewPager != null) {

            String entities =  FahzApplication.getInstance().getFahzClaims().getEntitiesVisible();

            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(entities, JsonElement.class);

            for (int i = 0; i < ((JsonArray) jsonElement).size(); i++) {

                EntitieVisible entitieVisible = gson.fromJson(((JsonArray) jsonElement).get(i), EntitieVisible.class);

                if(entitieVisible.idMenu == Constants.MENU_GESTAO_FAHZ_MENU_PERFIL_ENDERECO && (entitieVisible.canView == 1)) {
                    mCanViewAddress = true;
                } else if(entitieVisible.idMenu == Constants.MENU_GESTAO_FAHZ_MENU_PERFIL_BANCARIOS && (entitieVisible.canView == 1)) {
                    mCanViewBankData = true;
                } else if(entitieVisible.idMenu == Constants.MENU_GESTAO_FAHZ_MENU_PERFIL_DADOS && (entitieVisible.canView == 1)) {
                    mCanViewPersonalData = true;
                }
            }

            if (!mCanViewAddress && !mCanViewBankData && !mCanViewPersonalData) {
                mButton.setVisibility(View.GONE);
            }

            loadTitularData(viewPager);
        }

        toolbarTitle.setText(getString(R.string.label_personal_data));
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mProfileContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mProfileContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    @OnClick(R.id.save_button)
    public void submit(View view)
    {
        SystemBehavior.BehaviorEnum id = SystemBehavior.BehaviorEnum.EDIT_LIFE;
        idBehavior = id.id();
        mResponse = mDataFragment.getPersonalData();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mButton.getWindowToken(), 0);


        if(mResponse != null && mResponse.email != null && !mResponse.email.equals("") && !Utils.isEmailValid(mResponse.email))
        {
            showSnackBar(getString(R.string.not_valid_email), TYPE_FAILURE);
        }
        else if(mResponse != null && mResponse.phone != null && !mResponse.phone.equals("") && !Utils.validatePhone(mResponse.phone))
        {
            showSnackBar(getString(R.string.not_valid_phone), TYPE_FAILURE);
        }
        else if(mResponse != null && mResponse.cellphone != null && !mResponse.cellphone.equals("") && !Utils.validatePhone(mResponse.cellphone))
        {
            showSnackBar(getString(R.string.not_valid_cellphone), TYPE_FAILURE);
        }
        else if(mResponse != null && mResponse.cellphone != null && !mResponse.cellphone.equals("") && !Utils.validateCellPhoneNumber(mResponse.cellphone))
        {
            int resID = getResources().getIdentifier("MSG371", "string", getPackageName());
            String message = getResources().getString(resID);

            showSnackBar(message, TYPE_FAILURE);
        }
        else
        {

            if (mResponse.email.equals("") && mResponse.cellphone.equals(""))
            {
                int resID = getResources().getIdentifier("MSG206", "string", getPackageName());
                String message = getResources().getString(resID);
                showSnackBar(message, TYPE_FAILURE);
            }
            else
            {
                if(mDataFragment.validateRequiredData() && mAddressFragment.validateRequiredData())
                {
                    int resID = getResources().getIdentifier("MSG102", "string", getPackageName());
                    String message = getResources().getString(resID);

                    dialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, this, onClickOk);
                }
            }
        }
    }

    private View.OnClickListener onClickOk = new View.OnClickListener() {
        @SuppressWarnings("UnusedAssignment")
        @Override
        public void onClick(View view) {
            //Validar se mudou email e ou telefone, caso mudou os dois chamar modal e depois validate documet.

            boolean hasChangedEmail = ((previousEmail!= null && !previousEmail.isEmpty())  && !previousEmail.equals(mResponse.email))
                    ||  ((previousEmail== null || previousEmail.isEmpty()) && !mResponse.email.isEmpty());
            boolean hasChangePhone = (previousPhone!= null && !previousPhone.isEmpty() &&!previousPhone.equals(mResponse.cellphone))
                    ||((previousPhone == null || previousPhone.isEmpty())  && !mResponse.cellphone.isEmpty());

            boolean emailIsEmpty = (mResponse.email== null || mResponse.email.isEmpty());
            boolean phoneIsEmpty = (mResponse.cellphone == null || mResponse.cellphone.isEmpty());

            if (hasChangedEmail && hasChangePhone && !emailIsEmpty && !phoneIsEmpty){

                String[] items = new String[2];
                items[0] = getString(R.string.hint_email);
                items[1] =  getString(R.string.hint_cellphone);

                ShowQuestionDiolog(getString(R.string.edit_values_token),items,null,ProfileActivity.this);
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
                    else if(phoneIsEmpty && !emailIsEmpty)
                        methodToken = ValidateTokenRequest.SendMethod.EMAIL;
                }

                validateCallDocuments();
            }

            if (dialog != null) {
                dialog.dismiss();
            }
        }
    };

    private void ShowQuestionDiolog(String title, String[] options, @Nullable String buttonMessage,
                                  Context context) {

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
                validateCallDocuments();
            }
        });

        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodToken = ValidateTokenRequest.SendMethod.SMS;
                dialog.cancel();
                validateCallDocuments();
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

    public void validateCallDocuments(){
        try {
            setLoading(true);
            if (dialog != null) {
                dialog.dismiss();
            }

            Intent intent = new Intent(ProfileActivity.this, DocumentsActivity.class);
            intent.putExtra(Constants.ORIGIN_CALL_DOCUMENT, DocumentsActivity.OriginCall.FROMPROFILE);
            intent.putExtra(Constants.CONTEXT_DOCUMENT, idBehavior);
            intent.putExtra(Constants.CONTEXT_DOCUMENT_CPF, FahzApplication.getInstance().getFahzClaims().getCPF());

            BankResponse validatingBank = new BankResponse();
            validatingBank = mResponse.bank;

            //VALIDA SE O TITULAR TEM DADOS BANCARIOS, CASO SIM PRECISAMOS DO DOCUMENTO Comprovante de Conta Corrente
            mResponse.bank = mBankDataFragment.getBankData();
            if ((mResponse.bank != null && mResponse.bank.bank != 0 && mResponse.bank.account != null && !mResponse.bank.account.equals(""))
                //||
                //(validatingBank != null && validatingBank.bank != 0 && validatingBank.account != null && !validatingBank.account.equals(""))
            ) {
                intent.putExtra(Constants.REQUEST_ACCOUNT_DOCUMENT, Constants.Comprovante_de_Conta_Corrente);
            }
            //Valida se veio de reembolso e obriga a preencher os dados bancarios
            if (mFromRefund &&
                    (!(mResponse.bank != null && mResponse.bank.bank != 0 && mResponse.bank.account != null && !mResponse.bank.account.equals("")) &&
                            !(validatingBank != null && validatingBank.bank != 0 && validatingBank.account != null && !validatingBank.account.equals("")))) {
                Utils.showSimpleDialog(getString(R.string.dialog_title), "Favor preencher os dados bancários para prosseguir com o reembolso",
                        null, ProfileActivity.this, null);
            } else {

                startActivityForResult(intent, DOCUMENT_FOR_RESULT);
            }
            setLoading(false);
        } catch (ValidateBankDataErrorException ex) {
            setLoading(false);
            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, ProfileActivity.this, null);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Resources resources = getResources();
        TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());

        if(mCanViewPersonalData) {
            mDataFragment = PersonalDataFragment.newInstance(mResponse);
            adapter.add(mDataFragment, resources.getString(R.string.fragment_personal_data));
        }

        if(mCanViewAddress) {
            mAddressFragment = AddressFragment.newInstance(mResponse.address);
            adapter.add(mAddressFragment, resources.getString(R.string.fragment_address));
        }

        if(mCanViewBankData) {
            mBankDataFragment = BankDataFragment.newInstance(mResponse.bank);
            adapter.add(mBankDataFragment, resources.getString(R.string.fragment_bank_data));
        }

        viewPager.setAdapter(adapter);
    }

    static class TabsAdapter extends FragmentPagerAdapter {
        private List<Fragment> listFragments = new ArrayList<>();
        private List<String> listFragmentsTitle =  new ArrayList<>();

        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        public void add(Fragment fragment, String title) {
            this.listFragments.add(fragment);
            this.listFragmentsTitle.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return listFragments.get(position);
        }

        @Override
        public int getCount() {
            return listFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return listFragmentsTitle.get(position);
        }
    }

    private void loadTitularData(final ViewPager viewPager){
        setLoading(true);
        mAPIService = ServiceGenerator.createService(APIService.class);
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        Call<TitularResponse> call = mAPIService.getDataTitular(new CPFInBody(cpf));
        call.enqueue(new Callback<TitularResponse>() {
            @Override
            public void onResponse(@NonNull Call<TitularResponse> call, @NonNull Response<TitularResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                mResponse = response.body();
                previousEmail = response.body().email;
                previousPhone = response.body().cellphone;

                setupViewPager(viewPager);
                TabLayout tabLayout = findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager);

                if(mCanViewPersonalData) {
                    tabLayout.getTabAt(0).setIcon(R.drawable.ic_personal);
                }

                if(mCanViewAddress) {
                    tabLayout.getTabAt(1).setIcon(R.drawable.ic_address);
                }

                if(mCanViewBankData) {
                    tabLayout.getTabAt(2).setIcon(R.drawable.ic_bank);
                }



                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<TitularResponse> call, @NonNull Throwable t) {
                if(t instanceof SocketTimeoutException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if(t instanceof UnknownHostException)
                    showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
                setLoading(false);
            }
        });
    }

    private void setLoading(boolean loading){
        if(loading){
            mProgressDialog.setMessage("Carregando dados");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        else
            mProgressDialog.dismiss();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.DOCUMENT_FOR_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                savePersonalData();
            }
            else
            {
                showSnackBar("Os Documentos não foram salvos com sucesso.", Constants.TYPE_FAILURE);
            }
        }else if (requestCode == Constants.INFORMATIVE_TOKEN_RESULT) {
            showSnackBarDismiss(messageIdentifier, TYPE_SUCCESS, new Snackbar.Callback() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onDismissed(Snackbar snackbar, int event) {

                    if (mFromRefund || mFromCard) {
                        Intent returnA = new Intent();
                        setResult(RESULT_OK, returnA);
                        finish();
                    } else {
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void savePersonalData() {
        try {
            setLoading(true);
            mAPIService = ServiceGenerator.createService(APIService.class);
            mResponse = mDataFragment.getPersonalData();
            mResponse.address = mAddressFragment.getAddressData();
            mResponse.bank = mBankDataFragment.getBankData();

            final boolean isSendToken = methodToken != null;
            String methodTokenString = methodToken != null ?  methodToken.toString() : null;

            Call<CommitResponse> call = mAPIService.editTitular(mResponse, false,isSendToken, methodTokenString);
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    CommitResponse response2 = response.body();
                    if (response2.commited) {

                        messageIdentifier = response2.messageIdentifier;

                        if (isSendToken) {
                            Intent intentInformative = new Intent(ProfileActivity.this, InformativeTokenActivity.class);
                            startActivityForResult(intentInformative, INFORMATIVE_TOKEN_RESULT);
                        } else {
                            showSnackBarDismiss(response2.messageIdentifier, TYPE_SUCCESS, new Snackbar.Callback() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {

                                    if (mFromRefund || mFromCard) {
                                        Intent returnA = new Intent();
                                        setResult(RESULT_OK, returnA);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }

                    } else {
                        showSnackBar(response2.messageIdentifier, TYPE_FAILURE);
                    }

                    setLoading(false);
                }

                @Override
                public void onFailure(@NonNull Call<CommitResponse> call, @NonNull Throwable t) {
                    if(t instanceof SocketTimeoutException)
                        showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                    else if(t instanceof UnknownHostException)
                        showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                    setLoading(false);
                }
            });

        } catch (ValidateBankDataErrorException ex) {
            setLoading(false);
            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, this, null);

        } catch (Exception ex) {
            setLoading(false);
            showSnackBar(ex.getMessage(), TYPE_FAILURE);
        }

    }
    public void changeHeaderTitle(String text)
    {
        mProfileDescription.setText(text);
    }
}
