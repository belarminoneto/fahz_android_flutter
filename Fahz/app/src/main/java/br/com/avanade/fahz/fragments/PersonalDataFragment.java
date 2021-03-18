package br.com.avanade.fahz.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.ProfileActivity;
import br.com.avanade.fahz.activities.benefits.dentalplan.AdhesionDentalControlActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.AdhesionHealthControlActivity;
import br.com.avanade.fahz.activities.benefits.pharmacy.AdhesionPharmacyControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.controls.PhoneEditText;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.City;
import br.com.avanade.fahz.model.CivilState;
import br.com.avanade.fahz.model.SavedSerproResponse;
import br.com.avanade.fahz.model.SerproResponse;
import br.com.avanade.fahz.model.States;
import br.com.avanade.fahz.model.TitularResponse;
import br.com.avanade.fahz.model.response.CityListResponse;
import br.com.avanade.fahz.model.response.CivilStateListResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.START_SEARCH_AUTO_COMPLETE;
import static br.com.avanade.fahz.util.Constants.START_SHOW_AUTO_COMPLETE;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;


public class PersonalDataFragment extends Fragment {
    private EditText mName, mMotherName, mCPF, mEmail, mBirthdate;
    private AutoCompleteTextView mCity;
    private PhoneEditText mCellphone, mPhone;
    private Spinner mSex, mNationality, mUF, mCivilStatus;
    private TitularResponse mPersonalData;
    private ProgressDialog mProgressDialog;
    private SessionManager sessionManager;
    private TextInputLayout mCityLabel;
    private TextInputLayout descCpf, descBirthDate, descName;
    private Dialog dialog;

    AppCompatTextView mUfLabel;

    @BindView(R.id.extension_line_text_edit)
    EditText mExtensionLine;

    private List<CivilState> civilStates = new ArrayList<>();
    private List<String> civilStatesStr = new ArrayList<>();

    private List<String> statesStr = new ArrayList<>();
    private List<String> citiesStr = new ArrayList<>();

    public static PersonalDataFragment newInstance(TitularResponse titularResponse) {
        PersonalDataFragment personalDataFragment = new PersonalDataFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Personal", titularResponse);
        personalDataFragment.setArguments(bundle);

        return personalDataFragment;
    }


    @OnTextChanged(value = R.id.city_edit_text,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterCityInput(Editable editable) {
        try {
            if (!mUF.getSelectedItem().equals("Selecione") && editable.toString().length() > START_SEARCH_AUTO_COMPLETE) {
                populatePossibleCity();
            } else {
                mCity.setAdapter(null);
            }
        }
        catch (Exception ex)
        {
            LogUtils.error("PersonalDataFragment", ex);
        }
    }

    //CASO A SELECAO NO COMBO DE ESTADO MUDE
    @OnItemSelected(R.id.uf_spinner)
    void onItemSelected(int position) {
        if(statesStr.size() >0 && !statesStr.get(position).equals(mPersonalData.birthstate) ) {
            String state = statesStr.get(position);
            mCity.setText("");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_data, container, false);
        ButterKnife.bind(this,view);

        mName = view.findViewById(R.id.full_name_edit_text);
        mMotherName = view.findViewById(R.id.name_mother_edit_text);
        mSex = view.findViewById(R.id.sex_spinner);
        mCPF = view.findViewById(R.id.cpf_edit_text);
        mNationality = view.findViewById(R.id.nationality_spinner);
        mBirthdate = view.findViewById(R.id.birthdate_edit_text);
        mUF = view.findViewById(R.id.uf_spinner);
        mEmail = view.findViewById(R.id.email_text_edit);
        mPhone = view.findViewById(R.id.phone_text_edit);
        mCellphone = view.findViewById(R.id.cellphone_edit_text);
        mCity = view.findViewById(R.id.city_edit_text);
        mCivilStatus = view.findViewById(R.id.civil_status_spinner);
        mCityLabel= view.findViewById(R.id.city_label);
        mUfLabel= view.findViewById(R.id.uf_label);
        descCpf = view.findViewById(R.id.descCpf);
        descBirthDate= view.findViewById(R.id.descBirthDate);
        descName= view.findViewById(R.id.descName);

        sessionManager = new SessionManager(getActivity().getApplicationContext());
        mProgressDialog = new ProgressDialog(getActivity());

        mCPF.setClickable(true);
        mBirthdate.setClickable(true);
        mName.setClickable(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPersonalData = new TitularResponse();
            mPersonalData = bundle.getParcelable("Personal");
            populateListCivilState();
        }

        descCpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnackFragment (getResources().getString(getResources().getIdentifier("MSG214", "string", getActivity().getPackageName())), TYPE_FAILURE);
            }
        });

        mCPF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnackFragment (getResources().getString(getResources().getIdentifier("MSG214", "string", getActivity().getPackageName())), TYPE_FAILURE);
            }
        });

        descBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int resID = getResources().getIdentifier("MSG213", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                dialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, getActivity(), validateSerpro);
            }
        });

        mBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int resID = getResources().getIdentifier("MSG213", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                dialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, getActivity(), validateSerpro);
            }
        });

        descName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int resID = getResources().getIdentifier("MSG213", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                dialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, getActivity(), validateSerpro);
            }
        });

        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int resID = getResources().getIdentifier("MSG213", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                dialog = Utils.showQuestionReturnDialog(getString(R.string.dialog_title), message, getActivity(), validateSerpro);
            }
        });


        updateControls();
        return view;
    }

    private View.OnClickListener validateSerpro = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            searchSerpro(mCPF.getText().toString());
            if(dialog != null) {
                dialog.dismiss();
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        updateData();
    }

    public TitularResponse getPersonalData(){
        updateData();
        return mPersonalData;
    }

    private void updateData(){
        if (mPersonalData != null) {
            mPersonalData.name = !mName.getText().toString().equals("")?mName.getText().toString():null;
            mPersonalData.mother = !mMotherName.getText().toString().equals("")?mMotherName.getText().toString():null;
            mPersonalData.sex = mSex.getSelectedItem().toString().toUpperCase().toCharArray()[0];
            mPersonalData.cpf = mCPF.getText().toString();
            mPersonalData.nationality = mNationality.getSelectedItem().toString();
            if(!mBirthdate.getText().equals(""))
                mPersonalData.birthdate = DateEditText.formatDateForYMD(mBirthdate.getText().toString());
            mPersonalData.birthstate = mUF.getSelectedItem().toString()!= "Selecione"?mUF.getSelectedItem().toString(): "";
            mPersonalData.email = mEmail.getText().toString();
            if(!mPhone.getText().equals("")) {
                mPersonalData.phone = PhoneEditText.unmaskPhone(mPhone.getText().toString());
            }
            mPersonalData.cellphone = PhoneEditText.unmaskPhone(mCellphone.getText().toString());
            mPersonalData.birthCity = !mCity.getText().toString().equals("")?mCity.getText().toString():null;
            mPersonalData.civilstate = civilStates.get(civilStatesStr.indexOf(mCivilStatus.getSelectedItem()));
            mPersonalData.extesionline = mExtensionLine.getText().toString();

        }
    }

    private void updateControls(){
        if (mPersonalData != null) {
            mName.setText(mPersonalData.name);
            mMotherName.setText(mPersonalData.mother);
            mSex.setSelection(Arrays.asList(getResources().getStringArray(R.array.sex_array)).indexOf(mPersonalData.sex == 'M' ? "Masculino" : "Feminino"));
            mCPF.setText(mPersonalData.cpf);
            mNationality.setSelection(Arrays.asList(getResources().getStringArray(R.array.nationality_array)).indexOf(mPersonalData.nationality));
            mBirthdate.setText(Utils.formatDateString(mPersonalData.birthdate));
            if(statesStr!= null && statesStr.size() >0 && mPersonalData.birthstate!= null && !mPersonalData.birthstate.equals(""))
                mUF.setSelection(statesStr.indexOf(mPersonalData.birthstate));
            mEmail.setText(mPersonalData.email);
            mPhone.setText(PhoneEditText.maskPhone(mPersonalData.phone));
            mCellphone.setText(PhoneEditText.maskPhone(mPersonalData.cellphone));
            if(mPersonalData.birthCity!=null && !mPersonalData.birthCity.equals(""))
                mCity.setText(mPersonalData.birthCity);
            if(mCivilStatus!= null && mCivilStatus.getCount() > 0 && mPersonalData.civilstate!=null)
                mCivilStatus.setSelection(civilStatesStr.indexOf(mPersonalData.civilstate.getDescription()));
            if(mPersonalData.extesionline!=null)
                mExtensionLine.setText(mPersonalData.extesionline);
        }
    }

    private void setLoading(boolean loading, String text) {
        if(loading){
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        else{
            mProgressDialog.dismiss();
        }
    }

    public boolean validateRequiredData()
    {
        boolean personalDataIsValid = true;
        List<String> personalDataInvalid = new ArrayList<String>();
        StringBuilder messageNotValid = new StringBuilder(getString(R.string.not_valid_personal_data) + " ");
        updateData();

        if (mPersonalData.mother == null || mPersonalData.mother.equals(""))
        {
            personalDataInvalid.add(getString(R.string.mother_name));
            personalDataIsValid =  false;
        }

        if (mPersonalData.sex == Character.MIN_VALUE)
        {
            personalDataInvalid.add(getString(R.string.hint_sex));
            personalDataIsValid =  false;
        }

        if (mPersonalData.nationality == null || mPersonalData.nationality.equals(""))
        {
            personalDataInvalid.add(getString(R.string.hint_nationality));
            personalDataIsValid =  false;
        }


        if ((mPersonalData.birthstate == null || mPersonalData.birthstate.equals("")) && !mPersonalData.nationality.equals(getString(R.string.text_foreign)))
        {
            personalDataInvalid.add(getString(R.string.hint_uf_birth));
            personalDataIsValid =  false;
        }

        if ((mPersonalData.birthCity == null || mPersonalData.birthCity.equals("")) && !mPersonalData.nationality.equals(getString(R.string.text_foreign)))
        {
            personalDataInvalid.add(getString(R.string.hint_city_of_birth));
            personalDataIsValid =  false;
        }

        if (mPersonalData.civilstate == null || mPersonalData.civilstate.equals(""))
        {
            personalDataInvalid.add(getString(R.string.hint_civilState));
            personalDataIsValid =  false;
        }

        if (mPersonalData.email != null && !mPersonalData.email.equals("") && !Utils.isEmailValid(mPersonalData.email))
        {
            personalDataInvalid.add(getString(R.string.hint_email));
            personalDataIsValid =  false;
        }

        if (mPersonalData.cellphone != null && !mPersonalData.cellphone.equals("") && !Utils.validateCellPhoneNumber(mPersonalData.cellphone))
        {
            personalDataInvalid.add(getString(R.string.hint_cellphone));
            personalDataIsValid =  false;
        }

        if(!personalDataIsValid)
        {
            int count = 0;
            for (String word : personalDataInvalid)
            {
                messageNotValid.append(word);

                if(count != personalDataInvalid.size()-1)
                {
                    messageNotValid.append(", ");
                }

                count ++;
            }

            showSnackFragment (messageNotValid.toString(), TYPE_FAILURE);
        }

        return personalDataIsValid;
    }

    //BUSCA NO ENDPOINT DA SERPRO
    public void searchSerpro(String cpf) {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            cpf = cpf.replace(".", "");
            cpf = cpf.replace("-", "");

            APIService mAPIService = ServiceGenerator.createService(APIService.class);
            Call<JsonElement> call = mAPIService.serpro(new CPFInBody(cpf));
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        SerproResponse user = null;
                        SavedSerproResponse user2 = null;

                        if(response.body().getAsJsonObject().toString().contains("situacao"))
                            user2 =  new Gson().fromJson((response.body().getAsJsonObject()), SavedSerproResponse.class);
                        else if(response.body().getAsJsonObject().toString().contains("codigo"))
                            user =  new Gson().fromJson((response.body().getAsJsonObject()), SerproResponse.class);

                        if (user != null || user2 != null) {

                            setLoading(false,"");

                            if ((user!= null && user.statusResponse.code != null &&user.statusResponse.code.equals(Constants.REGULAR))
                                    ||(user2 != null && user2.statusSavedResponse.code != null &&user2.statusSavedResponse.code.equals(Constants.REGULAR) )) {
                                mName.setText(user == null ? user2.name: user.name);
                                mBirthdate.setText(DateEditText.parseTODate(user== null? user2.birthdate: user.birthdate));

                            } else {
                                showSnackFragment(getResources().getString(R.string.dialog_error5), Constants.TYPE_FAILURE);
                            }
                        }

                    } else if (response.code() == 400) {
                        setLoading(false,"");
                        try {
                            String data = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(data);
                            String message = jObjError.getString("Message");
                            showSnackFragment(message, Constants.TYPE_FAILURE);
                        } catch (Exception ex) {
                            Utils.showSimpleDialog(getString(R.string.dialog_title), ex.getMessage(), null, getActivity(), null);
                        }
                    } else {
                        setLoading(false,"");
                        showSnackFragment(getResources().getString(R.string.problemServer), Constants.TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, Throwable t) {
                    setLoading(false,"");
                    if(getActivity()!= null) {
                        if (t instanceof SocketTimeoutException)
                            showSnackFragment(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackFragment(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else
                            showSnackFragment(t.getMessage(), TYPE_FAILURE);
                    }
                }
            });
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && getActivity()!=null)
            if(getActivity() instanceof  ProfileActivity)
                ((ProfileActivity)getActivity()).changeHeaderTitle(getString(R.string.text_change_personal_data));
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
                    if (response.isSuccessful()) {

                        CivilStateListResponse civilStatesResponse = response.body();
                        civilStates = new ArrayList<>();
                        civilStatesStr = new ArrayList<>();

                        if (civilStatesResponse.getCount() > 0) {
                            civilStates.addAll(civilStatesResponse.getCivilstates());
                        }

                        for (CivilState state : civilStates) {
                            civilStatesStr.add(state.getDescription());
                        }


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                                R.layout.spinner_layout, civilStatesStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mCivilStatus.setAdapter(adapter);
                        setLoading(false,"");
                        if(mCivilStatus!= null && mCivilStatus.getCount() > 0 && mPersonalData.civilstate!=null)
                            mCivilStatus.setSelection(civilStatesStr.indexOf(mPersonalData.civilstate.getDescription()));

                        populateListState();

                    } else if (response.code() == 404) {
                        setLoading(false,"");
                        showSnackFragment(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {
                        setLoading(false,"");
                        showSnackFragment(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CivilStateListResponse> call, @NonNull Throwable t) {
                    setLoading(false,"");
                    if(getActivity()!= null) {
                        if (t instanceof SocketTimeoutException)
                            showSnackFragment(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackFragment(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else
                            showSnackFragment(t.getMessage(), TYPE_FAILURE);
                    }
                }

            });
        }
    }

    //POPULA O COMBO COM O ESTADO
    private void populateListState() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<States> call = mAPIService.getStates();
            call.enqueue(new Callback<States>() {
                @Override
                public void onResponse(@NonNull Call<States> call, @NonNull Response<States> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        States statesResponse = response.body();
                        statesStr = new ArrayList<>();
                        statesStr.add("Selecione");

                        if (statesResponse.getCount() > 0) {
                            statesStr.addAll(statesResponse.getStates());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                                R.layout.spinner_layout, statesStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mUF.setAdapter(adapter);

                        if(statesStr!= null && statesStr.size() >0 && mPersonalData.birthstate!= null && !mPersonalData.birthstate.equals(""))
                            mUF.setSelection(statesStr.indexOf(mPersonalData.birthstate));

                    } else if (response.code() == 404) {
                        showSnackFragment(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {

                        showSnackFragment(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                    setLoading(false,"");
                }

                @Override
                public void onFailure(@NonNull Call<States> call, @NonNull Throwable t) {
                    setLoading(false,"");
                    if(getActivity()!= null) {
                        if (t instanceof SocketTimeoutException)
                            showSnackFragment(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackFragment(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else
                            showSnackFragment(t.getMessage(), TYPE_FAILURE);
                    }
                }

            });
        }
    }

    //POPULA A OPÇÃO DE CIDADE
    private void populatePossibleCity() {
        if (sessionManager.isLoggedIn()) {

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<CityListResponse> call = mAPIService.getCityByState(mUF.getSelectedItem().toString(), mCity.getText().toString());
            call.enqueue(new Callback<CityListResponse>() {
                @Override
                public void onResponse(@NonNull Call<CityListResponse> call, @NonNull Response<CityListResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        CityListResponse cityList = response.body();

                        citiesStr = new ArrayList<>();
                        for (City city : cityList.getRegisters()) {
                            citiesStr.add(city.getName());
                        }

                        //Creating the instance of ArrayAdapter containing list of fruit names
                        ArrayAdapter<String> adapter = new ArrayAdapter<>
                                (getActivity(), android.R.layout.select_dialog_item, citiesStr);
                        adapter.notifyDataSetChanged();

                        //Getting the instance of AutoCompleteTextView
                        mCity.setThreshold(START_SHOW_AUTO_COMPLETE);//will start working from first character
                        mCity.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                    } else {

                        showSnackFragment(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CityListResponse> call, @NonNull Throwable t) {
                    if(getActivity()!= null) {
                        if (t instanceof SocketTimeoutException)
                            showSnackFragment(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackFragment(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else
                            showSnackFragment(t.getMessage(), TYPE_FAILURE);
                    }
                }

            });
        }
    }

    private void showSnackFragment(String text, int typeFailure )
    {
        if(getActivity() instanceof  ProfileActivity)
            ((ProfileActivity) getActivity()).showSnackBar(text, typeFailure);
        else if(getActivity() instanceof AdhesionHealthControlActivity)
            ((AdhesionHealthControlActivity) getActivity()).showSnackBar(text, typeFailure);
        else if(getActivity() instanceof AdhesionDentalControlActivity)
            ((AdhesionDentalControlActivity) getActivity()).showSnackBar(text, typeFailure);
        else if(getActivity() instanceof AdhesionPharmacyControlActivity)
            ((AdhesionPharmacyControlActivity) getActivity()).showSnackBar(text, typeFailure);
    }

    //CASO A SELECAO NO COMBO DE NACIONALIDADE MUDE
    @OnItemSelected(R.id.nationality_spinner)
    void onNationalitySelected(int position) {

        if(mNationality.getSelectedItem().toString().equals( Arrays.asList(getResources().getStringArray(R.array.nationality_array)).get(0)) ) {
            mUF.setEnabled(true);
            populateListState();
            mCity.setEnabled(true);

            mUfLabel.setTextColor(getResources().getColor(R.color.black));

            mCityLabel.setHintTextAppearance(R.style.ActivateHint);
            mCity.setTextColor(getResources().getColor(R.color.black));

            TextViewCompat.setTextAppearance(mCity, R.style.ActivateHint);
            //mCity.setTextAppearance(R.style.ActivateHint);
        }
        else
        {
            mUF.setEnabled(false);
            statesStr = new ArrayList<>();
            statesStr.add("Selecione");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                    R.layout.spinner_layout_disabled, statesStr);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mUF.setAdapter(adapter);

            mUfLabel.setTextColor(getResources().getColor(R.color.grey_light_text));

            mCity.setText(null);
            mCity.setEnabled(false);

            mCityLabel.setHintTextAppearance(R.style.DesactivateHint);
            mCity.setTextColor(getResources().getColor(R.color.grey_light_text));
            TextViewCompat.setTextAppearance(mCity, R.style.DesactivateHint);
            //mCity.setTextAppearance(R.style.DesactivateHint);
        }

    }

}