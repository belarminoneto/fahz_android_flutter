package br.com.avanade.fahz.fragments.benefits.healthplan.prontmed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.Adapter.prontmed.DoctorsAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.prontmedappointment.SearchForDoctosBody;
import br.com.avanade.fahz.model.response.prontmed.DoctorListResponse;
import br.com.avanade.fahz.model.response.prontmed.SpecialityListResponse;
import br.com.avanade.fahz.model.response.prontmed.SpecialityResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;

@SuppressWarnings("unused")
public class FindDoctorsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.rv_list_of_doctors)
    RecyclerView mListOfDoctorsRecyclerView;
        DoctorsAdapter mAdapter;

    @BindView(R.id.sp_find_doctors)
    Spinner mSpinnerDoctors;
    @BindView(R.id.ib_search)
    ImageButton ibSearch;


    ArrayAdapter<SpecialityResponse> mSpinnerAdapter;

    SessionManager sessionManager;
    BaseHealthControlActivity activity;

    List<SpecialityResponse> mDoctorList;

    SpecialityResponse selectedSpeciality;

    public final static String DOCTOR_BUNDLE_KEY = "doctor_bundle_key";

    private int skip = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_doctors, container, false);
        ButterKnife.bind(this, view);

        activity = ((BaseHealthControlActivity) getActivity());
        assert activity != null;
        activity.setLoading(false, "");
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        activity.toolbarTitle.setText(getString(R.string.search_doctors));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mListOfDoctorsRecyclerView.setHasFixedSize(true);
        mListOfDoctorsRecyclerView.setLayoutManager(layoutManager);
        mListOfDoctorsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new DoctorsAdapter(getActivity(), activity, this);
        mListOfDoctorsRecyclerView.setAdapter(mAdapter);

        mSpinnerDoctors.setOnItemSelectedListener(this);

        mDoctorList = new ArrayList<>();
        mDoctorList.add(new SpecialityResponse((long) 0, activity.getString(R.string.select_a_speciality)));
        mSpinnerAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                R.layout.spinner_layout, mDoctorList);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDoctors.setAdapter(mSpinnerAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadSpecialityList();
    }

    @Override
    public void onResume() {
        super.onResume();
        skip = 1;
    }

    private void loadSpecialityList() {
        activity.setLoading(true, activity.getString(R.string.loading_data));
        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<SpecialityListResponse> call = mAPIService.prontmedGetAllSpeciality();
        call.enqueue(new Callback<SpecialityListResponse>() {
            @Override
            public void onResponse(@NonNull Call<SpecialityListResponse> call, @NonNull Response<SpecialityListResponse> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    SpecialityListResponse specialityListResponse = response.body();
                    if(specialityListResponse.getList().size()>1) {
                        for (SpecialityResponse speciality : specialityListResponse.getList()) {
                            mSpinnerAdapter.add(speciality);
                        }

                        mSpinnerDoctors.setVisibility(View.VISIBLE);
                        ibSearch.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        mSpinnerAdapter.clear();
                        mSpinnerAdapter.add(specialityListResponse.getList().get(0));
                        onItemSelected(null,null,0,0);

                        callSearchSpecialityAPI();
                        mSpinnerDoctors.setVisibility(View.GONE);
                        ibSearch.setVisibility(View.GONE);
                    }
                }

                //activity.setLoading(false, "");
            }

            @Override
            public void onFailure(@NonNull Call<SpecialityListResponse> call, @NonNull Throwable t) {
                if (activity != null) {
                    activity.setLoading(false, "");
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

    @OnClick(R.id.ib_search)
    public void searchSpecialityClick(View v) {
        if (selectedSpeciality != null) {
            long specialityId = selectedSpeciality.getId();
            if (specialityId <= 0) {
                AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setMessage(activity.getString(R.string.select_a_speciality));
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            } else {
                callSearchSpecialityAPI();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSpeciality = mSpinnerAdapter.getItem(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void addPagination() {
        skip += Constants.PRONTMED_DEFAULT_PAGINATION_ITENS;
    }

    public void callSearchSpecialityAPI() {
        String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        activity.setLoading(true, activity.getString(R.string.loading_data));
        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        int top = Constants.PRONTMED_DEFAULT_PAGINATION_ITENS;
        Call<DoctorListResponse> call = mAPIService.prontmedSearchForDoctors(new SearchForDoctosBody(mCpf, top, skip, selectedSpeciality.getId()));
        call.enqueue(new Callback<DoctorListResponse>() {
            @Override
            public void onResponse(@NonNull Call<DoctorListResponse> call, @NonNull Response<DoctorListResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    DoctorListResponse listResponse = response.body();
                    if (listResponse.isCommited() && listResponse.getDoctors() != null) {
                        if (listResponse.getDoctors() != null) {
                            mAdapter.addItems(listResponse.getDoctors());
                        }
                    } else {
                        activity.showSnackBar(listResponse.getMessage(), TYPE_FAILURE);
                    }
                }
                activity.setLoading(false, "");
            }

            @Override
            public void onFailure(@NonNull Call<DoctorListResponse> call, @NonNull Throwable t) {
                if (activity != null) {
                    activity.setLoading(false, "");
                    if (t instanceof SocketTimeoutException)
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", activity.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException) {
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", activity.getPackageName())), TYPE_FAILURE);
                    } else
                        activity.showSnackBar(t.getMessage(), 1);
                }
            }
        });
    }
}
