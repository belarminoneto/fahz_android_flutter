package br.com.avanade.fahz.fragments.benefits.healthplan.prontmed;


import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.avanade.fahz.Adapter.prontmed.DoctorDayScheduleAdapter;
import br.com.avanade.fahz.Adapter.prontmed.DoctorHourScheduleAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.ScheduleDoctorDay;
import br.com.avanade.fahz.model.response.FamilyGroupItemResponse;
import br.com.avanade.fahz.model.response.FamilyGroupListResponse;
import br.com.avanade.fahz.model.response.prontmed.DoctorItemResponse;
import br.com.avanade.fahz.model.response.prontmed.ScheduleAppointmentRequest;
import br.com.avanade.fahz.model.response.prontmed.ScheduleAppointmentResponse;
import br.com.avanade.fahz.model.response.prontmed.SearchAppointmentResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

@SuppressWarnings("unused")
public class SearchTimeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private final String TAG = "SearchTimeFrag";

    SessionManager sessionManager;
    BaseHealthControlActivity activity;

    @BindView(R.id.tv_doctor_name)
    TextView doctorName;

    @BindView(R.id.tv_address)
    TextView doctorAddress;

    @BindView(R.id.rv_list_of_days)
    RecyclerView rvListDays;
    @BindView(R.id.rv_list_of_hours)
    RecyclerView rvListHours;
    private Dialog dialog;
    private boolean hasGoneToCalendar = false;

    public final static String DOCTOR_SCHEDULE_HOUR_BUNDLE_KEY = "doctor_schedule_hour_bundle_key";

    DoctorDayScheduleAdapter mAdapter;
    DoctorHourScheduleAdapter mAdapterHour;
    public final static String DOCTOR_SCHEDULE_DATE_BUNDLE_KEY = "doctor_schedule_date_bundle_key";
    public final static String DOCTOR_SCHEDULE_ADDRESS_BUNDLE_KEY = "doctor_schedule_address_bundle_key";
    public String selectedAddress;
    public String selectedDate;
    public DoctorItemResponse doctorSelected;
    SearchAppointmentResponse schedule;
    List<ScheduleDoctorDay> days;

    private int skip = 1;

    private Dialog confirmationDialog;
    private List<FamilyGroupItemResponse> beneficiaryList;

    private FamilyGroupItemResponse selectedBeneficiary;
    private SearchAppointmentResponse.Hour selectedHour;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prontmed_search_time, container, false);
        ButterKnife.bind(this, view);

        activity = ((BaseHealthControlActivity) getActivity());
        assert activity != null;
        activity.setLoading(false, "");
        sessionManager = new SessionManager(activity.getApplicationContext());

        activity.toolbarTitle.setText(getString(R.string.find_schedules));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String gsonDoctorData = bundle.getString(FindDoctorsFragment.DOCTOR_BUNDLE_KEY);
            doctorSelected = new Gson().fromJson(gsonDoctorData, DoctorItemResponse.class);

            doctorName.setText(doctorSelected.getName());
            if(doctorSelected.getAddress()!= null) {
                doctorAddress.setText(doctorSelected.getAddress().getLogradouro() + " " +
                        doctorSelected.getAddress().getNumber() + " " +
                        doctorSelected.getAddress().getComplement() + ", " +
                        doctorSelected.getAddress().getNeighborhood() + ", " +
                        doctorSelected.getAddress().getCity() + " - " +
                        doctorSelected.getAddress().getState() + ". " +
                        "CEP: " + doctorSelected.getAddress().getZipCode());
            }
        }

        configureRecyclerView();
        setDefaultDate();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        skip = 1;

        if (hasGoneToCalendar) {
            Intent intentStart = new Intent(getContext(), BaseHealthControlActivity.class);
            intentStart.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.MYSCHEDULE);
            startActivity(intentStart);
            activity.finish();
        }
    }

    private void configureRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        rvListDays.setHasFixedSize(true);
        rvListDays.setLayoutManager(layoutManager);

        rvListDays.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DoctorDayScheduleAdapter(getContext(), doctorSelected.getDaysOfWeek(), this);
        rvListDays.setAdapter(mAdapter);

        LinearLayoutManager lManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        rvListHours.setHasFixedSize(true);
        rvListHours.setLayoutManager(lManager);

        rvListHours.setItemAnimator(new DefaultItemAnimator());
        mAdapterHour = new DoctorHourScheduleAdapter(getContext(), new ArrayList<SearchAppointmentResponse.Hour>(), this);
        rvListHours.setAdapter(mAdapterHour);
    }

    private void setDefaultDate() {

        if (activity != null) {
            activity.setLoading(true, activity.getString(R.string.loading_data));
            new DaysGenerateTask().execute();
        }
    }

    public void searchAppointments(Date dateToSearch) {
        activity.setLoading(true, activity.getString(R.string.loading_data));
        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        int top = Constants.PRONTMED_DEFAULT_PAGINATION_ITENS;

        SimpleDateFormat fmtOut = new SimpleDateFormat("yyyyMMdd");
        final String date = fmtOut.format(dateToSearch);

        SimpleDateFormat fmtOutValidate = new SimpleDateFormat("dd/MM/yyyy");
        final String dateValidate = fmtOutValidate.format(dateToSearch);

        Call<SearchAppointmentResponse> call = mAPIService.prontMedSearchAppointment(String.valueOf(doctorSelected.getId()), date, top, skip);
        call.enqueue(new Callback<SearchAppointmentResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchAppointmentResponse> call, @NonNull Response<SearchAppointmentResponse> response) {
                activity.setLoading(false, "");
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    schedule = response.body();

                    if (schedule.isCommited()) {
                        List<SearchAppointmentResponse.Agenda> agendas = schedule.getAppointment().getAgendas();
                        if (agendas != null && agendas.size() > 0) {
                            SearchAppointmentResponse.Agenda agenda = schedule.getAppointment().getAgendas().get(0);
                            selectedDate = dateValidate;
                            mAdapterHour.clear();


                            if(agenda.getHours().size()==0)
                            {
                                activity.showSnackBar("Sem horário disponível.", TYPE_FAILURE);
                                DoctorHourScheduleAdapter adapter = new DoctorHourScheduleAdapter(getContext(), new ArrayList<SearchAppointmentResponse.Hour>(),SearchTimeFragment.this);
                                rvListHours.setAdapter(adapter);
                            }
                            else {
                                mAdapterHour = new DoctorHourScheduleAdapter(getContext(), agenda.getHours(), SearchTimeFragment.this);
                                rvListHours.setAdapter(mAdapterHour);
                            }
                        }
                    } else {
                        activity.showSnackBar(schedule.getMessage(), TYPE_FAILURE);
                        DoctorHourScheduleAdapter adapter = new DoctorHourScheduleAdapter(getContext(), new ArrayList<SearchAppointmentResponse.Hour>(),SearchTimeFragment.this);
                        rvListHours.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchAppointmentResponse> call, @NonNull Throwable t) {
                activity.setLoading(false, "");
                if (activity != null) {
                    if (t instanceof SocketTimeoutException)
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", activity.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", activity.getPackageName())), TYPE_FAILURE);
                    else
                        activity.showSnackBar(t.getMessage(), 1);
                }
            }
        });
    }

    public void addPagination() {
        skip += Constants.PRONTMED_DEFAULT_PAGINATION_ITENS;
    }

    public void showConfirmationDialog(SearchAppointmentResponse.Hour hour) {
        selectedHour = hour;
        confirmationDialog = new Dialog(activity);
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmationDialog.setContentView(R.layout.dialog_prontmed_new_schedule_confirmation);

        Button confirmButton = confirmationDialog.findViewById(R.id.bt_confirm);
        Button cancelButton = confirmationDialog.findViewById(R.id.bt_cancel);

        Spinner spBeneficiaryList = confirmationDialog.findViewById(R.id.sp_select_beneficiary);
        loadFamilyGroup(spBeneficiaryList, confirmButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.cancel();
                //activity.setFragment(BaseHealthControlActivity.BaseHealthFragment.MYSCHEDULE);
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.dismiss();
                callScheduleAppointment();
            }
        });

        confirmationDialog.show();
    }


    private void loadFamilyGroup(final Spinner spBeneficiaryList, final Button confirmButton) {
        activity.setLoading(true, activity.getString(R.string.loading_data));

        String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<FamilyGroupListResponse> call = mAPIService.getFamilyGroup(new CPFInBody(mCpf));
        call.enqueue(new Callback<FamilyGroupListResponse>() {
            @Override
            public void onResponse(@NonNull Call<FamilyGroupListResponse> call, @NonNull Response<FamilyGroupListResponse> response) {
                activity.setLoading(false, "");
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    beneficiaryList = response.body().getLives();

                    final List<String> listOfNames = new ArrayList<>();
                    for (FamilyGroupItemResponse item : beneficiaryList) {
                        listOfNames.add(item.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                            R.layout.spinner_layout, listOfNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spBeneficiaryList.setAdapter(adapter);
                    spBeneficiaryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedBeneficiary = beneficiaryList.get(position);
                            confirmButton.setEnabled(true);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedBeneficiary = null;
                            confirmButton.setEnabled(false);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<FamilyGroupListResponse> call, @NonNull Throwable t) {
                activity.setLoading(false, "");
                if (activity != null) {
                    if (t instanceof SocketTimeoutException)
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", activity.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", activity.getPackageName())), TYPE_FAILURE);
                    else
                        activity.showSnackBar(t.getMessage(), 1);
                }
            }
        });
    }

    private void callScheduleAppointment() {
        activity.setLoading(true, activity.getString(R.string.loading_data));

        ScheduleAppointmentRequest body = new ScheduleAppointmentRequest();
        body.setTipoOper("I");
        body.setCodPac(selectedBeneficiary.getCpf());
        body.setCalendarId(selectedHour.getCalendarId());
        body.setDateConverter(selectedDate);
        body.setDateTimeConverter(selectedDate, selectedHour.getLabel());

        Gson gson = new Gson();
        String jsonInString = gson.toJson(body);

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<ScheduleAppointmentResponse> call = mAPIService.prontmedScheduleAppointment(body);
        call.enqueue(new Callback<ScheduleAppointmentResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScheduleAppointmentResponse> call, @NonNull Response<ScheduleAppointmentResponse> response) {
                activity.setLoading(false, "");
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ScheduleAppointmentResponse body = response.body();
                    if (body.isCommited()) {
                        activity.showSnackBar(body.getMessage(), TYPE_SUCCESS, 5000);
                        final Handler handler = new Handler();

                        Utils.showQuestionDialog(getString(R.string.scheduled_appointment), getString(R.string.scheduled_new_event), getActivity(), onClickOk,onClickCancel,"Sim","Não");


                    } else {
                        activity.showSnackBar(body.getMessage(), TYPE_FAILURE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScheduleAppointmentResponse> call, @NonNull Throwable t) {
                activity.setLoading(false, "");
                if (activity != null) {
                    if (t instanceof SocketTimeoutException)
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", activity.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", activity.getPackageName())), TYPE_FAILURE);
                    else
                        activity.showSnackBar(t.getMessage(), 1);
                }
            }
        });
    }

    private View.OnClickListener onClickOk = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                Date date = sdf.parse(String.format("%s %s", selectedDate, selectedHour.getLabel()));
                cal.setTime(date);

                Calendar calEnd = Calendar.getInstance();
                calEnd.setTime(date);
                calEnd.add(Calendar.MINUTE, 30);

                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("endTime", calEnd.getTimeInMillis());
                intent.putExtra("title", "Consulta Meu Doutor " + doctorSelected.getName());
                intent.putExtra(CalendarContract.Events.DESCRIPTION, "Consulta para:  " + selectedBeneficiary.getName());

                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, doctorSelected.getAddress().getLogradouro() + " " +
                        doctorSelected.getAddress().getNumber() + " " +
                        doctorSelected.getAddress().getComplement() + ", " +
                        doctorSelected.getAddress().getNeighborhood());

                startActivity(intent);
                hasGoneToCalendar = true;

                //getActivity().finish();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    };

    private View.OnClickListener onClickCancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (dialog != null) {
                dialog.dismiss();
            }

            Intent intent = new Intent(getContext(), BaseHealthControlActivity.class);
            intent.putExtra(Constants.BASE_HEALTH_CONTROL, BaseHealthControlActivity.BaseHealthFragment.MYSCHEDULE);
            startActivity(intent);
            activity.finish();
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class DaysGenerateTask extends AsyncTask<String, Void, String> {
        protected String  doInBackground(String... urls) {
            //Configuras as datas que podem ser mostradas e qual o status
            days = new ArrayList<>();
            boolean isFirst = true;

            int count = 1;
            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);

            while (count <= 15) {

                //Verificar se e domingo

                int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if(dayWeek!=1) {

                    ScheduleDoctorDay day;
                    if (doctorSelected.getDaysOfWeek()!= null && doctorSelected.getDaysOfWeek().contains(dayWeek)) {
                        day = new ScheduleDoctorDay(calendar.getTime(), true, isFirst);
                        isFirst = false;
                    }
                    else
                    {
                        day = new ScheduleDoctorDay(calendar.getTime(), false,false);
                    }

                    days.add(day);
                    count++;
                }
                calendar.add(Calendar.DATE,1);
            }
            return "";
        }

        protected void onPostExecute(String result) {
            mAdapter.clear();
            mAdapter.addItems(days);
            activity.setLoading(false, activity.getString(R.string.loading_data));
        }
    }
}
