package br.com.avanade.fahz.fragments.benefits.scholarship;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.ProfileActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.AdhesionHealthControlActivity;
import br.com.avanade.fahz.activities.benefits.scholarship.RequestScholarshipControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.ScholarshipLifeResponse;
import br.com.avanade.fahz.model.Schooling;
import br.com.avanade.fahz.model.response.SchoolingReponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.MASK_DATE;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;


public class InitialDataFragment extends Fragment {
    @BindView(R.id.course_desc)
    TextView mCourseDesc;
    @BindView(R.id.scholarship_type_desc)
    TextView mScholarshipType;
    @BindView(R.id.school_desc)
    TextView mSchoolDesc;
    @BindView(R.id.evaluation_desc)
    TextView mEvaluationDesc;
    @BindView(R.id.city_text)
    TextInputEditText mCity;
    @BindView(R.id.switch_already_studding)
    Switch mAlreadyStudding;
    @BindView(R.id.period_text)
    TextInputEditText mPeriodText;
    @BindView(R.id.switch_financing)
    Switch mSwitchFinancing;
    @BindView(R.id.start_text)
    TextInputEditText mStart;
    @BindView(R.id.end_text)
    TextInputEditText mEnd;
    @BindView(R.id.total_value)
    TextInputEditText mTotalValue;
    @BindView(R.id.schooling_spinner)
    Spinner mSchooling;

    private ScholarshipLifeResponse mData;
    private ProgressDialog mProgressDialog;
    private SessionManager sessionManager;

    private List<Schooling> schoolings = new ArrayList<>();
    private List<String> schoolingsStr = new ArrayList<>();

    public static InitialDataFragment newInstance(ScholarshipLifeResponse response) {
        InitialDataFragment personalDataFragment = new InitialDataFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Personal", response);
        personalDataFragment.setArguments(bundle);

        return personalDataFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scholarship_initial, container, false);
        ButterKnife.bind(this, view);

        sessionManager = new SessionManager(getActivity().getApplicationContext());
        mProgressDialog = new ProgressDialog(getActivity());

        startDateField();
        endDateField();

        mData = ((RequestScholarshipControlActivity) getActivity()).getDataInformation();
        populateListSchooling();
        localeDecimalInput(mTotalValue);


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public Pair<ScholarshipLifeResponse,Boolean> getData(){
        Boolean canChange = updateData();
        return new Pair<ScholarshipLifeResponse,Boolean>(mData,canChange);
    }

    private Boolean updateData() {
        Boolean canChange = false;
        if (mData != null) {
            int resID = 0;


            mData.getScholarshipLife().setStudent(mAlreadyStudding.isChecked());
            mData.getScholarshipLife().setFinancing(mSwitchFinancing.isChecked());

            if (mCity.getText().toString().equals(""))
                resID = getResources().getIdentifier("MSG289", "string", getActivity().getPackageName());
            else
                mData.getScholarshipLife().setCity(!mCity.getText().toString().equals("") ? mCity.getText().toString() : null);

            if (mPeriodText.getText().toString().equals(""))
                resID = getResources().getIdentifier("MSG290", "string", getActivity().getPackageName());
            else
                mData.getScholarshipLife().setPeriod(!mPeriodText.getText().toString().equals("") ? mPeriodText.getText().toString() : null);

            if (mSchooling.getSelectedItem().equals("Selecione"))
                resID = getResources().getIdentifier("MSG222", "string", getActivity().getPackageName());
            else
                mData.getScholarshipLife().setSchooling(schoolings.get(schoolingsStr.indexOf(mSchooling.getSelectedItem())));

            String startDate = mStart.getText().toString();
            if (startDate.equals(""))
                resID = getResources().getIdentifier("MSG126", "string", getActivity().getPackageName());
            else
                mData.getScholarshipLife().setStartCourse(DateEditText.parseToShortDate(startDate));

            String endDate = mEnd.getText().toString();
            if (endDate.equals(""))
                resID = getResources().getIdentifier("MSG266", "string", getActivity().getPackageName());
            else
                mData.getScholarshipLife().setEndCourse(DateEditText.parseToShortDate(endDate));


            try {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                Date date1 = format.parse(endDate);
                Date date2 = format.parse(startDate);

                if (date1.compareTo(date2) <= 0) {
                    resID = getResources().getIdentifier("MSG267", "string", getActivity().getPackageName());
                }
            } catch (Exception ex) {
                resID = getResources().getIdentifier("MSG267", "string", getActivity().getPackageName());
            }


            try {

                String value = mTotalValue.getText().toString();
                if (value.equals("") || (value == null) || value.equals("R$0,00"))
                    resID = getResources().getIdentifier("MSG193", "string", getActivity().getPackageName());
                else{

                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                    Number number = format.parse(mTotalValue.getText().toString().replace("R$", ""));
                    mData.getScholarshipLife().setMonthlyFee(number.doubleValue());
                }
            } catch (Exception ex) {
                resID = getResources().getIdentifier("MSG221", "string", getActivity().getPackageName());
                String message = getResources().getString(resID);
                Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
            }

            if (resID != 0) {
                String message = getResources().getString(resID);
                Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
                canChange = false;
            } else {
                ((RequestScholarshipControlActivity) getActivity()).setDataInformation(mData);
                canChange = true;
            }

        }
        return canChange;
    }

    private void updateControls(){
        if (mData != null) {
            mCourseDesc.setText(mData.getScholarshipLife().getCourse());
            mScholarshipType.setText(mData.getScholarshipLife().getTypeMonitoringScholarship().description);
            mSchoolDesc.setText(mData.getScholarshipLife().getInstitution());
            //mEvaluationDesc.setText(mData.getScholarshipLife().getEvaluationIGC()>=3? "SIM" : "NÃO");
            mEvaluationDesc.setText(String.valueOf(mData.getScholarshipLife().getEvaluationIGC()));

            mCity.setText(mData.getScholarshipLife().getCity());
            mAlreadyStudding.setChecked(mData.getScholarshipLife().getStudent());
            mPeriodText.setText(mData.getScholarshipLife().getPeriod());
            mSwitchFinancing.setChecked(mData.getScholarshipLife().getFinancing());

            String value = mData.getScholarshipLife().getMonthlyFee().toString();
            mTotalValue.setText(value);

            mStart.setText(Utils.formatDateString(mData.getScholarshipLife().getStartCourse()));
            mEnd.setText(Utils.formatDateString(mData.getScholarshipLife().getEndCourse()));

            if(mData.getScholarshipLife().getSchooling()!=null)
                mSchooling.setSelection(schoolingsStr.indexOf((mData.getScholarshipLife().getSchooling().getDescription())));

        }
    }

    private void setLoading(boolean loading, String text){
        if(loading){
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        else{
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && getActivity()!=null)
            if(getActivity() instanceof  ProfileActivity)
                ((ProfileActivity)getActivity()).changeHeaderTitle(getString(R.string.text_change_personal_data));
    }

    //POPULA O COMBO COM A INFORMACAO DAS ESCOLARIDADES
    private void populateListSchooling() {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
            Call<JsonElement> call = mAPIService.getSchoolings(0);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    if (response.isSuccessful()) {

                        SchoolingReponse responseSchooling = new Gson().fromJson((response.body().getAsJsonObject()), SchoolingReponse.class);
                        schoolings = new ArrayList<>();
                        schoolingsStr = new ArrayList<>();

                        Schooling select = new Schooling();
                        select.setID(-1);
                        select.setDescription("Selecione");
                        schoolings.add(select);

                        if (responseSchooling.getRegisters().size() > 0) {
                            schoolings.addAll(responseSchooling.getRegisters());
                        }

                        for (Schooling k : schoolings) {
                                schoolingsStr.add(k.getDescription());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                                R.layout.spinner_layout, schoolingsStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSchooling.setAdapter(adapter);
                        setLoading(false,"");

                        updateControls();

                    } else if (response.code() == 404) {
                        setLoading(false,"");
                        showSnackFragment(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {
                        setLoading(false,"");
                        showSnackFragment(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    setLoading(false,"");
                    if(t instanceof SocketTimeoutException)
                        showSnackFragment (getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else if(t instanceof UnknownHostException)
                        showSnackFragment (getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                    else
                        showSnackFragment(t.getMessage(), TYPE_FAILURE);
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
    }

    private String unmask(String s) {
        return s.replaceAll("[^0-9]*","");
    }

    private void startDateField() {
        mStart.addTextChangedListener(new TextWatcher() {
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
                mStart.setText(mask.toString());
                mStart.setSelection(mask.length());

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkFieldStartDate();
                } else {

                }
            }
        });
    }

    private boolean checkFieldStartDate() {
        String birthDate = mStart.getText().toString();
        if (TextUtils.isEmpty(birthDate)) {
            mStart.setError(getString(R.string.error_birthdate_empty));
        } else if (!isDateValid(birthDate)) {
            mStart.setError(getString(R.string.error_birthdate_invalid));
        } else {
            return true;
        }
        return false;
    }

    private void endDateField() {
        mEnd.addTextChangedListener(new TextWatcher() {
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
                mEnd.setText(mask.toString());
                mEnd.setSelection(mask.length());

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkFieldEndDate();
                } else {

                }
            }
        });
    }

    private boolean checkFieldEndDate() {
        String birthDate = mEnd.getText().toString();
        if (TextUtils.isEmpty(birthDate)) {
            mEnd.setError(getString(R.string.error_birthdate_empty));
        } else if (!isDateValid(birthDate)) {
            mEnd.setError(getString(R.string.error_birthdate_invalid));
        } else {
            return true;
        }
        return false;
    }

    @SuppressWarnings("Annotator")
    private boolean isDateValid(String date) {
        String regex = "^(?:(?:31(\\/)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/)" +
                "(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?" +
                "\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?" +
                "(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|" +
                "[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:" +
                "(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
        return date != null && date.matches(regex);
    }

    private void localeDecimalInput(final EditText editText){

        DecimalFormat decFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols symbols=decFormat.getDecimalFormatSymbols();
        final String defaultSeperator=Character.toString(symbols.getDecimalSeparator());

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().matches("^R\\$(\\d{1,3}(\\.\\d{3})*|(\\d+))(\\,\\d{2})?$"))
                {
                    String userInput= ""+s.toString().replaceAll("[^\\d]", "");
                    StringBuilder cashAmountBuilder = new StringBuilder(userInput);

                    while (cashAmountBuilder.length() > 3 && cashAmountBuilder.charAt(0) == '0') {
                        cashAmountBuilder.deleteCharAt(0);
                    }
                    while (cashAmountBuilder.length() < 3) {
                        cashAmountBuilder.insert(0, '0');
                    }
                    cashAmountBuilder.insert(cashAmountBuilder.length()-2, ',');
                    cashAmountBuilder.insert(0, "R$");

                    editText.setText(cashAmountBuilder.toString());
                    // keeps the cursor always to the right
                    Selection.setSelection(editText.getText(), cashAmountBuilder.toString().length());

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().contains(defaultSeperator))
                    editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                else
                    editText.setKeyListener(DigitsKeyListener.getInstance("0123456789" + defaultSeperator));
            }
        });
    }

}