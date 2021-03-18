package br.com.avanade.fahz.activities.benefits.christmas;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.ZipEditText;
import br.com.avanade.fahz.model.AddressResponse;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.Company;
import br.com.avanade.fahz.model.CompanyResponse;
import br.com.avanade.fahz.model.SendAddressChristmas;
import br.com.avanade.fahz.model.Subsidiary;
import br.com.avanade.fahz.model.WorkspaceResponse;
import br.com.avanade.fahz.model.ZipResponse;
import br.com.avanade.fahz.model.response.ChristmasStartResponse;
import br.com.avanade.fahz.model.response.SubsidiaryXmasResponse;
import br.com.avanade.fahz.model.response.WorksapceXmasResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;
import static br.com.avanade.fahz.util.Constants.typeAddress;
import static br.com.avanade.fahz.util.Constants.typeWorkspace;

public class SelectAddressActivity extends NavDrawerBaseActivity {

    @BindView(R.id.content_christmas_address)
    LinearLayout mContentChristmasAddress;

    @BindView(R.id.btnSave)
    Button btnSave;

    @BindView(R.id.layoutInitialInfo)
    LinearLayout mLayoutInitialInfo;

    @BindView(R.id.layoutNoInfo)
    LinearLayout mLayoutNoInfo;
    @BindView(R.id.lblNoInfo)
    TextView mlblNoInfo;


    @BindView(R.id.layoutUnitInfo)
    LinearLayout mUnitInfo;
    @BindView(R.id.lblUnitName)
    TextView mlblUnitName;
    @BindView(R.id.lblAddress)
    TextView mlblAddress;

    @BindView(R.id.layoutAddressInfo)
    LinearLayout mAddressInfo;
    @BindView(R.id.lblCEP)
    TextView mlblCEP;
    @BindView(R.id.lblStreetName)
    TextView mlblStreeName;
    @BindView(R.id.lblNumber)
    TextView mlblNumbers;
    @BindView(R.id.lblComplement)
    TextView mlblComplement;
    @BindView(R.id.lblNeighborhood)
    TextView mlblNeighborhood;
    @BindView(R.id.lblCity)
    TextView mlblCity;
    @BindView(R.id.lblUf)
    TextView mlblUf;

    @BindView(R.id.layoutInformation)
    LinearLayout mLayoutInformation;
    @BindView(R.id.alterAddress)
    LinearLayout mAlterAddress;
    @BindView(R.id.alterUnit)
    LinearLayout mAlterUnit;



    //RADIOS
    @BindView(R.id.radion_same_unit)
    RadioButton mRadioSameUnit;
    @BindView(R.id.radio_new_unit)
    RadioButton mRadioNewUnit;
    @BindView(R.id.radio_new_address)
    RadioButton mRadioNewAddress;

    //INFO UNIDADE
    @BindView(R.id.view_separator)
    View mUnitSeparator;
    @BindView(R.id.lblDescriptionUnit)
    TextView mDescriptionUnit;
    @BindView(R.id.read)
    TextView mReadUnit;

    @BindView(R.id.layoutUnit)
    LinearLayout layoutUnit;
    @BindView(R.id.layoutSubsidiary)
    LinearLayout layoutSubsidiary;
    @BindView(R.id.layoutCompany)
    LinearLayout layoutCompany;

    @BindView(R.id.spinnerUnit)
    Spinner mSpinnerUnit;
    @BindView(R.id.spinnerSubsidiary)
    Spinner mSpinnerSubsidiary;
    @BindView(R.id.spinnerCompany)
    Spinner mSpinnerCompany;



    //INFO ADDRESS
    @BindView(R.id.view_separator_address)
    View mAddressSeparator;
    @BindView(R.id.lblDescriptionAddress)
    TextView mDescriptionAddress;
    @BindView(R.id.AddressLayout)
    LinearLayout mAddressLayout;
    @BindView(R.id.readAddress)
    TextView mReadAddress;

    @BindView(R.id.zip_edit_Text)
    ZipEditText mZipText;
    @BindView(R.id.street_edit_text)
    EditText mStreetText;
    @BindView(R.id.uf_edit_text)
    EditText mUfText;
    @BindView(R.id.city_edit_text)
    EditText mCityText;
    @BindView(R.id.district_edit_text)
    EditText mDistrictText;
    @BindView(R.id.number_edit_text)
    EditText mNumberText;
    @BindView(R.id.complement_edit_text)
    EditText mComplementText;
    @BindView(R.id.country_edit_text)
    EditText mCountryText;


    SessionManager sessionManager;
    ChristmasStartResponse christmasResponse;
    List<WorkspaceResponse> workspaces;
    List<CompanyResponse> subsidiaries;
    List<CompanyResponse> companies;

    private List<CompanyResponse> companyList = new ArrayList<>();
    private List<String> companyListStr = new ArrayList<>();

    private List<CompanyResponse> subList = new ArrayList<>();
    private List<String> subListStr = new ArrayList<>();

    private List<WorkspaceResponse> workspaceList = new ArrayList<>();
    private List<String> workspaceStr = new ArrayList<>();

    private ProgressDialog mProgressDialog;
    Dialog dialog;

    private RadioButton lastChecked = null;

    @Nullable
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_christmas_select_address);
        ButterKnife.bind(this);

        setImageHeaderVisibility(false);
        setMenuVisible(false);
        toolbarTitle.setText(getString(R.string.header_christmas));

        Gson gson = new Gson();
        christmasResponse = gson.fromJson(getIntent().getStringExtra("ChristmasResponse"), ChristmasStartResponse.class);

        mProgressDialog = new ProgressDialog(this);
        setLoading(false, "");
        sessionManager = new SessionManager(getApplicationContext());

        mUnitSeparator.setVisibility(View.INVISIBLE);
        mDescriptionUnit.setVisibility(GONE);
        layoutUnit.setVisibility(GONE);
        layoutSubsidiary.setVisibility(GONE);
        layoutCompany.setVisibility(GONE);

        mAddressSeparator.setVisibility(View.INVISIBLE);
        mDescriptionAddress.setVisibility(GONE);
        mAddressLayout.setVisibility(GONE);

        mReadUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUnit(null);
            }
        });

        mReadAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAddress(null);
            }
        });

        validateScreen();
    }

    private void setLoading(Boolean loading, String text) {
        try {
            if (loading) {
                mProgressDialog.setMessage(text);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            } else {
                mProgressDialog.dismiss();
            }
        } catch (Exception ex) {
            LogUtils.error("SendRefundFragment", ex);
        }
    }

    @OnClick(R.id.radion_same_unit)
    public void checkSame(View view) {

        if (lastChecked != null && lastChecked != mRadioSameUnit)
            lastChecked.setChecked(false);

        btnSave.setEnabled(true);
        setVisibilityAddress(false);
        setVisibilityUnit(false);
        lastChecked = mRadioSameUnit;
    }

    @OnClick(R.id.radio_new_unit)
    public void checkUnit(View view) {

        if (lastChecked != null && lastChecked != mRadioNewUnit)
            lastChecked.setChecked(false);

        btnSave.setEnabled(true);
        setVisibilityAddress(false);
        setVisibilityUnit(true);
        lastChecked = mRadioNewUnit;
    }

    @OnClick(R.id.radio_new_address)
    public void checkAddress(View view) {

        if (lastChecked != null && lastChecked != mRadioNewAddress)
            lastChecked.setChecked(false);

        btnSave.setEnabled(true);
        setVisibilityAddress(true);
        setVisibilityUnit(false);
        lastChecked = mRadioNewAddress;
    }

    public void setVisibilityUnit(boolean visible) {
        if (!visible) {
            mUnitSeparator.setVisibility(View.INVISIBLE);
            mDescriptionUnit.setVisibility(GONE);
            layoutUnit.setVisibility(GONE);
            layoutSubsidiary.setVisibility(GONE);
            layoutCompany.setVisibility(GONE);
        } else {
            mUnitSeparator.setVisibility(View.VISIBLE);
            mDescriptionUnit.setVisibility(VISIBLE);
            layoutUnit.setVisibility(VISIBLE);
            layoutSubsidiary.setVisibility(VISIBLE);
            layoutCompany.setVisibility(VISIBLE);
        }
    }

    public void setVisibilityAddress(boolean visible) {
        if (!visible) {
            mAddressSeparator.setVisibility(View.INVISIBLE);
            mDescriptionAddress.setVisibility(GONE);
            mAddressLayout.setVisibility(GONE);
        } else {
            mAddressSeparator.setVisibility(View.VISIBLE);
            mDescriptionAddress.setVisibility(VISIBLE);
            mAddressLayout.setVisibility(VISIBLE);
        }
    }

    public void validateScreen() {
        setLoading(true, getString(R.string.loading_searching));


        if (christmasResponse.getXmasStartChoose().getHasAvailablecampaign() &&
                (christmasResponse.getXmasStartChoose().getReasonNote()!= null && !christmasResponse.getXmasStartChoose().getReasonNote().equals("")) ) {
            mLayoutInitialInfo.setVisibility(GONE);
            mUnitInfo.setVisibility(GONE);
            mAddressInfo.setVisibility(GONE);
            mLayoutInformation.setVisibility(GONE);

            mLayoutNoInfo.setVisibility(VISIBLE);
            mlblNoInfo.setText(christmasResponse.getXmasStartChoose().getReasonNote());
        } else {
            mLayoutInitialInfo.setVisibility(VISIBLE);
            mUnitInfo.setVisibility(GONE);
            mAddressInfo.setVisibility(GONE);

            // Caso o endpoint retornou que o tipo de endereço a ser exibido é da T_VIDA ou último cadastrado na campanha
            if (christmasResponse.getXmasStartChoose().getType() == typeAddress &&
                    christmasResponse.getXmasStartChoose().getAddress() != null) {

                mAddressInfo.setVisibility(VISIBLE);
                mlblCEP.setText(christmasResponse.getXmasStartChoose().getAddress().zip);
                mlblStreeName.setText(christmasResponse.getXmasStartChoose().getAddress().street);
                mlblNumbers.setText(String.valueOf(christmasResponse.getXmasStartChoose().getAddress().number));
                mlblComplement.setText(christmasResponse.getXmasStartChoose().getAddress().complement);
                mlblNeighborhood.setText(christmasResponse.getXmasStartChoose().getAddress().district);
                mlblCity.setText(christmasResponse.getXmasStartChoose().getAddress().city);
                mlblUf.setText(christmasResponse.getXmasStartChoose().getAddress().state);

            }
            // Caso o endpoint retornou que o tipo de endereço a ser exibido é da última lotação cadastrada ou atual do usuário
            else if (christmasResponse.getXmasStartChoose().getType() == typeWorkspace &&
                    christmasResponse.getXmasStartChoose().getWorkspace() != null) {
                mUnitInfo.setVisibility(VISIBLE);
                mlblAddress.setText(christmasResponse.getXmasStartChoose().getWorkspace().address);
                mlblUnitName.setText(christmasResponse.getXmasStartChoose().getWorkspace().description);
            }

            if (christmasResponse.getXmasStartChoose().getEditable()) {
                mLayoutInformation.setVisibility(VISIBLE);
                if(christmasResponse.getXmasStartChoose().getCanChooseWorkspace())
                    mAlterUnit.setVisibility(VISIBLE);
                else
                    mAlterUnit.setVisibility(GONE);

                if(christmasResponse.getXmasStartChoose().getCanChooseAddress())
                    mAlterAddress.setVisibility(VISIBLE);
                else
                    mAlterAddress.setVisibility(GONE);
            }
        }

        if(christmasResponse.getXmasStartChoose().getCanChooseWorkspace())
            getXmasWorkspace(true);
        else
            setLoading(false, "");

    }

    @OnFocusChange(R.id.zip_edit_Text)
    void onFocusChanged(boolean focused) {
        if (!focused) {
            setLoading(true, getString(R.string.loading_searching));
            APIService mAPIService = ServiceGenerator.createService(APIService.class);
            final String zipCode = ZipEditText.unmaskZip(mZipText.getText().toString());
            Call<ZipResponse> call = mAPIService.cep(zipCode);

            call.enqueue(new Callback<ZipResponse>() {
                @Override
                public void onResponse(@NonNull Call<ZipResponse> call, @NonNull Response<ZipResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ZipResponse zipData = response.body();


                        mStreetText.setText(zipData.street);
                        mDistrictText.setText(zipData.district);
                        mUfText.setText(zipData.state);
                        mCityText.setText(zipData.city);
                        mCountryText.setText("BR");

                    } else if (response.code() == 404) {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }

                    setLoading(false, "");
                }

                @Override
                public void onFailure(@NonNull Call<ZipResponse> call, @NonNull Throwable t) {
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

    //BOTÃO QUE RETORNA AS INFORMACOES DO PLANO
    @OnClick(R.id.btnInfo)
    public void getInformation(View view) {
        Utils.showSimpleDialog(getString(R.string.information), christmasResponse.getXmasStartChoose().getRulesCampaign(), null, this, null);
    }

    public void getXmasWorkspace(final boolean starting) {
        setLoading(true, getString(R.string.loading_searching));

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = mAPIService.getXmasWorkspace();
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
                        workspaces = new Gson().fromJson((response.body().getAsJsonObject()), WorksapceXmasResponse.class).getRegisters();

                        workspaceList = new ArrayList<>();
                        workspaceStr = new ArrayList<>();

                        if (workspaces.size() > 0) {
                            workspaceList.addAll(workspaces);
                        }

                        workspaceStr.add("Selecione");
                        for (WorkspaceResponse w : workspaceList) {
                            workspaceStr.add(w.id + " - " + w.description);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectAddressActivity.this,
                                R.layout.spinner_layout, workspaceStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinnerUnit.setAdapter(adapter);

                        if (starting && christmasResponse.getXmasStartChoose().getWorkspace() != null)
                            mSpinnerUnit.setSelection(workspaceStr.indexOf(christmasResponse.getXmasStartChoose().getWorkspace().description));

                    }

                    if (christmasResponse.getXmasStartChoose().getWorkspace() != null)
                        getXmasSubsidiary(true);
                    else
                        setLoading(false, "");


                } else {
                    setLoading(false, "");
                    showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
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

    @OnItemSelected(R.id.spinnerUnit)
    void onUnitSelected(int position) {

        if ( !mSpinnerUnit.getSelectedItem().equals("Selecione") && (christmasResponse.getXmasStartChoose().getWorkspace() != null &&
                workspaceList.get(workspaceStr.indexOf(mSpinnerUnit.getSelectedItem())-1).id != christmasResponse.getXmasStartChoose().getWorkspace().id)) {
            getXmasSubsidiary(false);
        }
        else if(!mSpinnerUnit.getSelectedItem().equals("Selecione") && christmasResponse.getXmasStartChoose().getWorkspace() == null )
        {
            getXmasSubsidiary(false);
        }
    }

    public void getXmasSubsidiary(final boolean starting) {
        if (mSpinnerUnit.getSelectedItem() != null) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class);
            Call<JsonElement> call = mAPIService.getXmasSubsidiaries(workspaceList.get(workspaceStr.indexOf(mSpinnerUnit.getSelectedItem()) - 1).id);
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
                            subsidiaries = new Gson().fromJson((response.body().getAsJsonObject()), SubsidiaryXmasResponse.class).getRegisters();


                            subList = new ArrayList<>();
                            subListStr = new ArrayList<>();

                            if (subsidiaries.size() > 0) {
                                subList.addAll(subsidiaries);
                            }

                            subListStr.add("Selecione");
                            for (CompanyResponse w : subList) {
                                subListStr.add(w.id + " - " + w.description);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectAddressActivity.this,
                                    R.layout.spinner_layout, subListStr);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mSpinnerSubsidiary.setAdapter(adapter);

                            if (starting)
                                mSpinnerSubsidiary.setSelection(subListStr.indexOf(christmasResponse.getXmasStartChoose().getSubsidiary().getDescription()));
                            else if (subList.size() == 1)
                                mSpinnerSubsidiary.setSelection(1);
                        }

                        if (starting)
                            getXmasCompanies(true);
                        else
                            setLoading(false, "");

                    } else {
                        setLoading(false, "");
                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    setLoading(false, "");

                    if (t instanceof SocketTimeoutException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);

                }
            });
        } else {
            setLoading(false, "");
        }
    }

    @OnItemSelected(R.id.spinnerSubsidiary)
    void onSubsidiarySelected(int position) {

        if(!mSpinnerSubsidiary.getSelectedItem().equals("Selecione")  && (christmasResponse.getXmasStartChoose().getSubsidiary() != null &&
                !subList.get(subListStr.indexOf(mSpinnerSubsidiary.getSelectedItem())-1).id.equals(christmasResponse.getXmasStartChoose().getSubsidiary().getId()))) {

            companyList = new ArrayList<>();
            companyListStr = new ArrayList<>();
            companyListStr.add("Selecione");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectAddressActivity.this,
                    R.layout.spinner_layout, companyListStr);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerCompany.setAdapter(adapter);

            getXmasCompanies(false);
        }
        else if(mSpinnerSubsidiary.getSelectedItem().equals("Selecione"))
        {
            companyList = new ArrayList<>();
            companyListStr = new ArrayList<>();
            companyListStr.add("Selecione");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectAddressActivity.this,
                    R.layout.spinner_layout, companyListStr);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerCompany.setAdapter(adapter);
        }
        else if(!mSpinnerSubsidiary.getSelectedItem().equals("Selecione")  && christmasResponse.getXmasStartChoose().getSubsidiary() == null )
            getXmasCompanies(false);
    }

    public void getXmasCompanies(final boolean starting) {
        setLoading(true, getString(R.string.loading_searching));

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<JsonElement> call = mAPIService.getXmasComapanies(subList.get(subListStr.indexOf(mSpinnerSubsidiary.getSelectedItem())-1).id );
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
                        companies = new Gson().fromJson((response.body().getAsJsonObject()), SubsidiaryXmasResponse.class).getRegisters();

                        companyList = new ArrayList<>();
                        companyListStr = new ArrayList<>();

                        if (companies.size() > 0) {
                            companyList.addAll(companies);
                        }

                        companyListStr.add("Selecione");
                        for (CompanyResponse w : companyList) {
                            companyListStr.add(w.id + " - "+w.description);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectAddressActivity.this,
                                R.layout.spinner_layout, companyListStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinnerCompany.setAdapter(adapter);

                        if(starting)
                            mSpinnerCompany.setSelection(companyListStr.indexOf(christmasResponse.getXmasStartChoose().getCompany().getDescription()));
                        else if(subList.size()==1)
                        {
                            mSpinnerCompany.setSelection(1);
                        }

                    }

                    setLoading(false, "");

                } else {
                    setLoading(false, "");
                    showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
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

    //BOTÃO PARA SALVAR AS INFORMACOES
    @OnClick(R.id.btnSave)
    public void submit(View view) {

        boolean canContinue = true;

        //Validar o Radio que está selecionado e se os campos estão selecionados.
        SendAddressChristmas address = new SendAddressChristmas();
        if (lastChecked.getId() == mRadioSameUnit.getId()) {
            if (christmasResponse.getXmasStartChoose().getType() == typeAddress) {
                address.setAddress(christmasResponse.getXmasStartChoose().getAddress());
                address.setCPF(christmasResponse.getXmasStartChoose().getCPF());
                address.setPlan(christmasResponse.getXmasStartChoose().getPlan());
                address.setType(typeAddress);
                if (christmasResponse.getXmasStartChoose().getId() != null && !christmasResponse.getXmasStartChoose().getId().equals("00000000-0000-0000-0000-000000000000"))
                    address.setId(christmasResponse.getXmasStartChoose().getId());
            } else if (christmasResponse.getXmasStartChoose().getType() == typeWorkspace) {
                address.setCompany(christmasResponse.getXmasStartChoose().getCompany());
                address.setSubsidiary(christmasResponse.getXmasStartChoose().getSubsidiary());
                address.setWorkspace(christmasResponse.getXmasStartChoose().getWorkspace());
                address.setCPF(christmasResponse.getXmasStartChoose().getCPF());
                address.setPlan(christmasResponse.getXmasStartChoose().getPlan());
                address.setType(typeWorkspace);
                if (christmasResponse.getXmasStartChoose().getId() != null && !christmasResponse.getXmasStartChoose().getId().equals("00000000-0000-0000-0000-000000000000"))
                    address.setId(christmasResponse.getXmasStartChoose().getId());
            }
        } else if (lastChecked.getId() == mRadioNewUnit.getId()) {

            address.setCPF(christmasResponse.getXmasStartChoose().getCPF());
            address.setPlan(christmasResponse.getXmasStartChoose().getPlan());
            address.setType(typeWorkspace);
            if (christmasResponse.getXmasStartChoose().getId() != null && !christmasResponse.getXmasStartChoose().getId().equals("00000000-0000-0000-0000-000000000000"))
                address.setId(christmasResponse.getXmasStartChoose().getId());

            if (mSpinnerUnit.getSelectedItem().equals("Selecione")) {
                canContinue = false;
                dialog = Utils.showSimpleDialogReturnDialog(getString(R.string.dialog_title), getString(R.string.select_unit), this, onClickCompany);

            } else
                address.setWorkspace(workspaceList.get(workspaceStr.indexOf(mSpinnerUnit.getSelectedItem())-1));

            if (canContinue) {
                if (mSpinnerSubsidiary.getSelectedItem().equals("Selecione")) {
                    canContinue = false;
                    dialog = Utils.showSimpleDialogReturnDialog(getString(R.string.dialog_title), getString(R.string.select_subsidiary), this, onClickCompany);

                } else {
                    Subsidiary sub = new Subsidiary();
                    CompanyResponse response = subList.get(subListStr.indexOf(mSpinnerSubsidiary.getSelectedItem()) - 1);
                    sub.setId(response.id);
                    sub.setCNPJ(response.cnpj);
                    sub.setDescription(response.description);

                    address.setSubsidiary(sub);
                }
            }

            if (canContinue) {
                if (mSpinnerCompany.getSelectedItem().equals("Selecione")) {
                    canContinue = false;
                    dialog = Utils.showSimpleDialogReturnDialog(getString(R.string.dialog_title), getString(R.string.select_company), this, onClickCompany);

                } else {
                    Company comp = new Company();
                    CompanyResponse responseValue = companyList.get(companyListStr.indexOf(mSpinnerCompany.getSelectedItem()) - 1);
                    comp.setId(responseValue.id);
                    comp.setCNPJ(responseValue.cnpj);
                    comp.setDescription(responseValue.description);

                    address.setCompany(comp);
                }
            }

        } else if (lastChecked.getId() == mRadioNewAddress.getId()) {
            address.setCPF(christmasResponse.getXmasStartChoose().getCPF());
            address.setPlan(christmasResponse.getXmasStartChoose().getPlan());
            address.setType(typeAddress);
            if (christmasResponse.getXmasStartChoose().getId() != null && !christmasResponse.getXmasStartChoose().getId().equals("00000000-0000-0000-0000-000000000000"))
                address.setId(christmasResponse.getXmasStartChoose().getId());

            AddressResponse addressResponse = new AddressResponse();

            if ((mZipText.getText().toString() != null && !mZipText.getText().toString().equals("")) &&
                    (mStreetText.getText() != null && !mStreetText.getText().toString().equals("")) &&
                    (mUfText.getText().toString() != null && !mUfText.getText().toString().equals("")) &&
                    (mCityText.getText().toString() != null && !mCityText.getText().toString().equals("")) &&
                    (mDistrictText.getText().toString() != null && !mDistrictText.getText().toString().equals("")) &&
                    (mCountryText.getText().toString() != null && !mCountryText.getText().toString().equals(""))) {

                addressResponse.zip = ZipEditText.unmaskZip(mZipText.getText().toString());
                addressResponse.street = mStreetText.getText().toString();
                addressResponse.district = mDistrictText.getText().toString();
                addressResponse.state = mUfText.getText().toString();
                addressResponse.complement = mComplementText.getText().toString();
                addressResponse.city = mCityText.getText().toString();
                addressResponse.country = mCountryText.getText().toString();
            } else {
                canContinue = false;
                dialog = Utils.showSimpleDialogReturnDialog(getString(R.string.dialog_title), getString(R.string.address_data), this, onClickCompany);
            }

            if (canContinue) {
                if ((mNumberText.getText() != null && !mNumberText.getText().equals(""))) {
                    addressResponse.number = Integer.valueOf(mNumberText.getText().toString());
                } else {
                    canContinue = false;
                    dialog = Utils.showSimpleDialogReturnDialog(getString(R.string.dialog_title), getString(R.string.number_data), this, onClickCompany);
                }
            }

            if (canContinue)
                address.setAddress(addressResponse);
        }

        if (canContinue)
            sendRequest(address);

    }

    private View.OnClickListener onClickCompany = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setLoading(false, "");
            if(dialog != null) {
                dialog.dismiss();
            }
        }
    };

    public void sendRequest(SendAddressChristmas address ) {

        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            Gson gson = new Gson();
            String jsonInString = gson.toJson(address);

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<CommitResponse> call = mAPIService.sendXmasAddress(address);
            call.enqueue(new Callback<CommitResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        CommitResponse commitResponse = response.body();
                        Utils.showSimpleDialogGoBack(getString(R.string.dialog_title), commitResponse.messageIdentifier, null, SelectAddressActivity.this, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(SelectAddressActivity.this, MainActivity.class));
                                setLoading(false, "");
                                finish();
                            }
                        });


                    } else {
                        setLoading(false, "");
                        try {
                            String data = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(data);
                            String message = jObjError.getString("Message");
                            showSnackBar(message, TYPE_FAILURE);
                        } catch (Exception ex) {
                            showSnackBar(ex.getMessage(), TYPE_FAILURE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommitResponse> call, Throwable t) {
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


    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

//    public void sendRequest() {
//
//        if (sessionManager.isLoggedIn()) {
//            setLoading(true, getString(R.string.loading_searching));
//
//            Gson gson = new Gson();
//            String jsonInString = gson.toJson(requests);
//
//            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
//            Call<JsonElement> call = mAPIService.updateFinancialPlan(requests);
//            call.enqueue(new Callback<JsonElement>() {
//                @Override
//                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
//                    FahzApplication.getInstance().setToken(((Headers) response.raw().headers()).get("token"));
//                    if (response.isSuccessful()) {
//                        if (response.body().getAsJsonObject().has("messageIdentifier")) {
//                            CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
//                            showSnackBar(responseCommit.messageIdentifier, TYPE_FAILURE);
//                        } else {
//
//                            //CHAMA NOVO METODO QUE SALVA O RELACIONAMENTO
//                            ResponseHistories responseHistory = new Gson().fromJson((response.body().getAsJsonObject()), ResponseHistories.class);
//                            if (responseHistory.getCommited()) {
//                                List<String> historiesId = new ArrayList<>();
//                                for (History item:   responseHistory.getHistory()) {
//                                    historiesId.add(item.getHistoryId());
//                                }
//
//                                requestHistory.setHistoriesId(historiesId);
//                                sendRequestHistory();
//                            }
//
//                        }
//
//                    } else {
//                        setLoading(false, "");
//                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
//                    }
//
//                    setLoading(false, "");
//                }
//
//                @Override
//                public void onFailure(Call<JsonElement> call, Throwable t) {
//                    setLoading(false, "");
//                    if(t instanceof SocketTimeoutException)
//                        showSnackBar (getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
//                    else if(t instanceof UnknownHostException)
//                        showSnackBar (getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
//                    else
//                        showSnackBar(t.getMessage(), TYPE_FAILURE);
//                }
//
//            });
//        }
//    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContentChristmasAddress, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContentChristmasAddress, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

}