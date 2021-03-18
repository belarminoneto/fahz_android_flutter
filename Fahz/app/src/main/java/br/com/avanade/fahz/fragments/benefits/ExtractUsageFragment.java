package br.com.avanade.fahz.fragments.benefits;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.avanade.fahz.BuildConfig;
import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.dentalplan.BaseDentalControlActivity;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.fragments.benefits.Adapter.ExtractUsageAdapter;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.DependentLife;
import br.com.avanade.fahz.model.invoicehandling.ListExtractUsageBody;
import br.com.avanade.fahz.model.life.ListHolderAndDependentsBody;
import br.com.avanade.fahz.model.response.ExtractUsageResponse;
import br.com.avanade.fahz.model.response.LifesAndDependents;
import br.com.avanade.fahz.model.response.SmallLife;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static br.com.avanade.fahz.util.Constants.MASK_DATE;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ExtractUsageFragment extends Fragment {

    @BindView(R.id.content_usage)
    LinearLayout mContentUsage;

    @BindView(R.id.enddate_edit_text)
    EditText mEndDateText;
    @BindView(R.id.startdate_edit_text)
    EditText mStartDateText;
    @BindView(R.id.names_extract_spinner)
    Spinner mNamesExtract;
    @BindView(R.id.names_extract_view)
    TextView mNamesExtractView;


    SessionManager sessionManager;
    @BindView(R.id.recyclerViewUsage)
    RecyclerView mUsageRecyclerView;

    private String CpfHolder;
    private ProgressDialog mProgressDialog;
    private int idBenefit = 0;

    private List<SmallLife> lifesList = new ArrayList<>();
    private List<String> lifesStr = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extract_usage, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getContext());
        setLoading(false, "");
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mUsageRecyclerView.setHasFixedSize(true);
        mUsageRecyclerView.setLayoutManager(layoutManager);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idBenefit = bundle.getInt(Constants.BENEFIT_EXTRA, 0);
        }


        setupUi();
        dateField();
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
        if (getActivity() instanceof BaseHealthControlActivity)
            ((BaseHealthControlActivity) getActivity()).toolbarTitle.setText(String.format("%s%s%s", getActivity().getString(R.string.extract_health), System.getProperty("line.separator"), getActivity().getString(R.string.extract_consult)));
        else if (getActivity() instanceof BaseDentalControlActivity)
            ((BaseDentalControlActivity)getActivity()).toolbarTitle.setText(getActivity().getString(R.string.extract_dental) +
                    System.getProperty("line.separator")+
                    getActivity().getString(R.string.extract_consult));
                    
        populateListLifesAndDependents();
    }


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
                    DependentLife mDependentLife = response.body();
                    if (mDependentLife != null) {
                        CpfHolder = mDependentLife.holderCPF;
                    }
                } else {
                    int resID = getResources().getIdentifier("MSG027", "string", getActivity().getPackageName());
                    String message = getResources().getString(resID);
                    showSnackBar(message, TYPE_FAILURE);
                }
                setLoading(false, "");
            }

            @Override
            public void onFailure(@NonNull Call<DependentLife> call, @NonNull Throwable t) {
                setLoading(false, "");
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

    private void dateField() {
        mEndDateText.addTextChangedListener(new TextWatcher() {
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
                mEndDateText.setText(mask.toString());
                mEndDateText.setSelection(mask.length());

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEndDateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkFieldEndDate();
                } else {

                }
            }
        });

        mStartDateText.addTextChangedListener(new TextWatcher() {
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
                mStartDateText.setText(mask.toString());
                mStartDateText.setSelection(mask.length());

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mStartDateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        String birthDate = mStartDateText.getText().toString();
        if (TextUtils.isEmpty(birthDate)) {
            mStartDateText.setError(getString(R.string.error_startdate_empty));
        } else if (!isDateValid(birthDate)) {
            mStartDateText.setError(getString(R.string.error_startdate_invalid));
        } else {
            return true;
        }
        return false;
    }

    private boolean checkFieldEndDate() {
        String birthDate = mEndDateText.getText().toString();
        if (TextUtils.isEmpty(birthDate)) {
            mEndDateText.setError(getString(R.string.error_enddate_empty));
        } else if (!isDateValid(birthDate)) {
            mEndDateText.setError(getString(R.string.error_enddate_invalid));
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

    private String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    //BOTÃO DE EXTRATO DE USO
    @OnClick(R.id.btnSave)
    public void ExtractOfUsage(View view) {
        try {
            String passedStartDate = mStartDateText.getText().toString();
            String startDate = DateEditText.parseToDateExtract(passedStartDate);

            String passedEndDate = mEndDateText.getText().toString();
            String endDate = DateEditText.parseToDateExtract(passedEndDate);

            String cpf = null;
            if (FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role))
                cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
            else {
                if (lifesStr.indexOf(mNamesExtract.getSelectedItem()) != 0)
                    cpf = lifesList.get(lifesStr.indexOf(mNamesExtract.getSelectedItem()) - 1).getCpf();
                else
                    throw new Exception("Selecione uma vida");
            }

            //Valida se data Final - Data Inicial tem mais de 30 dias
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            Date startDateCalc = sdf.parse(passedStartDate);
            Date endDateCalc = sdf.parse(passedEndDate);

            int differenceDays = (int) ((startDateCalc.getTime() - endDateCalc.getTime()) / 86400000L) * -1;
            if (differenceDays > 90)
                throw new Exception(getString(R.string.error_range_date));


            if (cpf != null && !cpf.equals("")
                    && startDate != null && !startDate.equals("") && isDateValid(passedStartDate)
                    && endDate != null && !endDate.equals("") && isDateValid(passedEndDate))
                getExtract(startDate, endDate, cpf);
            else
                throw new Exception(getResources().getString(getResources().getIdentifier("MSG023", "string", getActivity().getPackageName())));
        } catch (ParseException ex) {
            showSnackBar(getResources().getString(getResources().getIdentifier("MSG023", "string", getActivity().getPackageName())), TYPE_FAILURE);
        } catch (Exception ex) {
            showSnackBar(ex.getMessage(), TYPE_FAILURE);
        }
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
                            if (!FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
                                FragmentActivity activity = getActivity();
                                if (activity != null) {
                                    lifesStr.add(getActivity().getString(R.string.family_group).toUpperCase());
                                    SmallLife life = new SmallLife();
                                    life.setCpf(Constants.DEFAULT_CPF);
                                    life.setName(getActivity().getString(R.string.family_group).toUpperCase());
                                    lifesList.add(life);
                                }
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                                R.layout.spinner_layout, lifesStr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mNamesExtract.setAdapter(adapter);

                        if (lifesStr != null && lifesStr.size() > 0)
                            mNamesExtract.setSelection(lifesStr.indexOf("Selecione"));

                        if (lifesList.size() == 1){
                            mNamesExtract.setSelection(1);
                            mNamesExtract.setEnabled(false);
                        }

                    } else if (response.code() == 404) {
                        showSnackFragment(getString(R.string.validateSentData), TYPE_FAILURE);
                    } else {

                        showSnackFragment(getString(R.string.problemServer), TYPE_FAILURE);
                    }
                    setLoading(false, "");
                }

                @Override
                public void onFailure(@NonNull Call<LifesAndDependents> call, @NonNull Throwable t) {
                    setLoading(false, "");
                    if (getActivity() != null) {
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

    private void showSnackFragment(String text, int typeFailure) {
        if (getActivity() instanceof BaseDentalControlActivity)
            ((BaseDentalControlActivity) getActivity()).showSnackBar(text, typeFailure);
        else if (getActivity() instanceof BaseHealthControlActivity)
            ((BaseHealthControlActivity) getActivity()).showSnackBar(text, typeFailure);
    }


    //BUSCA Extrato
    private void getExtract(String startDate, String endDate, String cpfUser) {
        if (sessionManager.isLoggedIn()) {
            setLoading(true, getString(R.string.loading_searching));

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(180, TimeUnit.SECONDS)
                    .readTimeout(180, TimeUnit.SECONDS)
                    .writeTimeout(180, TimeUnit.SECONDS)
                    .addInterceptor(interceptor).build();


            APIService mAPIService = new Retrofit.Builder().baseUrl(BuildConfig.BENEFIT_BASE_URL)
                    .client(client).addConverterFactory(GsonConverterFactory.create()).build().create(APIService.class);


            Call<ExtractUsageResponse> call = mAPIService.listExtractUsage(new ListExtractUsageBody(CpfHolder, cpfUser, startDate, endDate, idBenefit));
            call.enqueue(new Callback<ExtractUsageResponse>() {
                @Override
                public void onResponse(@NonNull Call<ExtractUsageResponse> call, @NonNull Response<ExtractUsageResponse> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));

                    if (response.isSuccessful()) {

                        ExtractUsageResponse body = response.body();
                        ExtractUsageAdapter adapter = new ExtractUsageAdapter(body.getRegisters(), getActivity());
                        mUsageRecyclerView.setAdapter(adapter);

                    } else {
                        try {
                            String data = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(data);
                            String message = jObjError.getString("messageIdentifier");
                            showSnackBar(message, Constants.TYPE_FAILURE);
                        } catch (Exception ex) {
                            showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                        }
                    }

                    setLoading(false, "");
                }

                @Override
                public void onFailure(Call<ExtractUsageResponse> call, Throwable t) {
                    setLoading(false, "");
                    if (getActivity() != null) {
                        if (t instanceof SocketTimeoutException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else {
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                            LogUtils.error(getClass().getSimpleName(), t);
                        }
                    }
                }

            });
        }
    }

    public void showSnackBarDismiss(String message, int typeApproval, Snackbar.Callback onCallBack) {
        Snackbar snackbar = Snackbar.make(mContentUsage, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        snackbar.addCallback(onCallBack);
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mContentUsage, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

}