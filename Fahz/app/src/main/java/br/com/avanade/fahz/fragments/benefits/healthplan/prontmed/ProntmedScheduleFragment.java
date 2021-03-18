package br.com.avanade.fahz.fragments.benefits.healthplan.prontmed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import br.com.avanade.fahz.Adapter.prontmed.ProntMedScheduleAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.prontmedappointment.ListOfAppointmentsBody;
import br.com.avanade.fahz.model.response.prontmed.CancelScheduleResponse;
import br.com.avanade.fahz.model.response.prontmed.ListOfAppointmentsItemResponse;
import br.com.avanade.fahz.model.response.prontmed.ListOfAppointmentsResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import br.com.avanade.fahz.util.prontmed.ScheduleRecyclerItemTouchHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ProntmedScheduleFragment extends Fragment implements ScheduleRecyclerItemTouchHelper.RecyclerItemTouchHelperListener, CancelScheduleDialogFragment.CancelScheduleDialogListener {

    @BindView(R.id.recyclerViewScheduleItem)
    RecyclerView mScheduleRecyclerView;
    ProntMedScheduleAdapter mAdapter;

    @BindView(R.id.bt_show_more)
    Button btShowMore;


    SessionManager sessionManager;

    ItemTouchHelper.SimpleCallback itCallback;

    BaseHealthControlActivity activity;

    int skip = 1;
    int top = Constants.PRONTMED_DEFAULT_PAGINATION_ITENS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_prontmed_schedule, container, false);
        ButterKnife.bind(this, view);

        activity = ((BaseHealthControlActivity) getActivity());
        if (activity != null) {
            activity.setLoading(false, "");
            sessionManager = new SessionManager(activity.getApplicationContext());
            activity.toolbarTitle.setText(getString(R.string.schedules));
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mScheduleRecyclerView.setHasFixedSize(true);
        mScheduleRecyclerView.setLayoutManager(layoutManager);
        mScheduleRecyclerView.setItemAnimator(new DefaultItemAnimator());
        itCallback = new ScheduleRecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itCallback).attachToRecyclerView(mScheduleRecyclerView);

        mAdapter = new ProntMedScheduleAdapter(activity, new ArrayList<ListOfAppointmentsItemResponse>(), this);
        mScheduleRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //getSchedules();
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdapter = new ProntMedScheduleAdapter(activity, new ArrayList<ListOfAppointmentsItemResponse>(),   ProntmedScheduleFragment.this);
        mScheduleRecyclerView.setAdapter(null);
        mScheduleRecyclerView.setAdapter(mAdapter);

        skip = 1;
        getSchedules();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_new_schedule)
    public void onNewScheduleClick(View v) {
        activity.setFragment(BaseHealthControlActivity.BaseHealthFragment.FINDDOCTORS);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        final int index = position;
        ListOfAppointmentsItemResponse item = mAdapter.getItem(position);
        if (item != null && item.getCanRemove()) {
            showConfirmationDialog((ProntMedScheduleAdapter.ScheduleItemViewHolder) viewHolder, position);
        } else {
            AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
            if(item.getMessageCanNotRemove() == null || item.getMessageCanNotRemove().equals(""))
                dialog.setMessage("Este agendamento não pode ser cancelado.");
            else
                dialog.setMessage(item.getMessageCanNotRemove());
//            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    dismissDialog(dialog, index);
//                }
//            });
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismissDialog(dialog, index);
                }
            });
            dialog.show();
        }
    }

    private void showConfirmationDialog(ProntMedScheduleAdapter.ScheduleItemViewHolder viewHolder, int position) {
        CancelScheduleDialogFragment dialog = new CancelScheduleDialogFragment();
        dialog.setItemPosition(position);
        dialog.setViewHolder(viewHolder);
        assert getFragmentManager() != null;
        dialog.show(getFragmentManager(), "");
    }


    @Override
    public void onDialogPositiveClick(final CancelScheduleDialogFragment dialog) {
        activity.setLoading(true, "Enviando dados...");

        final int itemPosition = dialog.getItemPosition();
        ListOfAppointmentsItemResponse scheduleToCancel = mAdapter.getItem(itemPosition);

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<CancelScheduleResponse> call = mAPIService.prontmedCancelSchedule(scheduleToCancel.getIdAppointment());
        call.enqueue(new Callback<CancelScheduleResponse>() {
            @Override
            public void onResponse(@NonNull Call<CancelScheduleResponse> call, @NonNull Response<CancelScheduleResponse> response) {
                activity.setLoading(false, "");
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    CancelScheduleResponse cancelScheduleResponse = response.body();
                    if (cancelScheduleResponse.getCommited()) {
                        activity.showSnackBar("Agendamento cancelado.", TYPE_SUCCESS);
                        top = 10;
                        skip = 1;
                        mAdapter = new ProntMedScheduleAdapter(activity, new ArrayList<ListOfAppointmentsItemResponse>(),   ProntmedScheduleFragment.this);
                        mScheduleRecyclerView.setAdapter(null);
                        mScheduleRecyclerView.setAdapter(mAdapter);
                        getSchedules();
                    } else {
                        activity.showSnackBar(cancelScheduleResponse.getMessage(), TYPE_FAILURE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CancelScheduleResponse> call, @NonNull Throwable t) {
                if (activity != null) {
                    activity.setLoading(false, "");
                    if (t instanceof SocketTimeoutException)
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", activity.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", activity.getPackageName())), TYPE_FAILURE);
                    else
                        activity.showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            }
        });
    }

    @Override
    public void onDialogNegativeClick(CancelScheduleDialogFragment dialog) {
        itCallback.clearView(mScheduleRecyclerView, dialog.getViewHolder());
        mAdapter.notifyItemChanged(dialog.getItemPosition());
        dialog.dismiss();
    }

    private void dismissDialog(DialogInterface dialog, int index) {
        itCallback.clearView(mScheduleRecyclerView, mScheduleRecyclerView.findViewHolderForAdapterPosition(index));
        mAdapter.notifyItemChanged(index);
        dialog.dismiss();
    }

    public void updateSkip() {
        skip += 1;
    }

    public void getSchedules() {
        activity.setLoading(true, getString(R.string.loading_data));

        String mCpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        Call<ListOfAppointmentsResponse> call = mAPIService.prontmedListOfAppointments(new ListOfAppointmentsBody(mCpf, top, skip));
        call.enqueue(new Callback<ListOfAppointmentsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListOfAppointmentsResponse> call, @NonNull Response<ListOfAppointmentsResponse> response) {
                activity.setLoading(false, "");
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ListOfAppointmentsResponse listOfAppointments = response.body();
                    if (listOfAppointments.isCommited() && listOfAppointments.getList() != null) {
                        mAdapter.addItems(listOfAppointments.getList());
                        if (listOfAppointments.getCount() > 9) {

                            btShowMore.setVisibility(View.VISIBLE);
                            btShowMore.setEnabled(true);
                            btShowMore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    updateSkip();
                                    getSchedules();
                                }
                            });
                        }
                    } else {
                        Utils.showSimpleDialog(getActivity().getString(R.string.attention_text), listOfAppointments.getMessage(), null, getActivity(), null);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListOfAppointmentsResponse> call, @NonNull Throwable t) {
                if (activity != null) {
                    activity.setLoading(false, "");
                    if (t instanceof SocketTimeoutException)
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", activity.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        activity.showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", activity.getPackageName())), TYPE_FAILURE);
                    else
                        activity.showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            }
        });
    }

}
