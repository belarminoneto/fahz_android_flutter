package br.com.avanade.fahz.Adapter.prontmed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.fragments.benefits.healthplan.prontmed.SearchTimeFragment;
import br.com.avanade.fahz.model.response.prontmed.SearchAppointmentResponse;

public class DoctorHourScheduleAdapter extends RecyclerView.Adapter<DoctorHourScheduleAdapter.DoctorScheduleHourViewHolder> {

    private final Context mContext;
    private List<SearchAppointmentResponse.Hour> mHours;
    private SearchTimeFragment parentFragment;

    public DoctorHourScheduleAdapter(Context context, List<SearchAppointmentResponse.Hour> hours, SearchTimeFragment parentFragment) {
        this.mContext = context;
        mHours = hours;
        this.parentFragment = parentFragment;
    }

    @NonNull
    @Override
    public DoctorScheduleHourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_doctor_hour, parent, false);
        return new DoctorScheduleHourViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorScheduleHourViewHolder holder, int position) {
        final int index = position;


        //Com a lista adicionar informacoes de Data
        final SearchAppointmentResponse.Hour item = mHours.get(position);

        if(item!= null && item.getLabel()!=null) {
            holder.btnHour.setText(item.getLabel());

            holder.btnHour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   parentFragment.showConfirmationDialog(item);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mHours.size();
    }

    public void clear() {
        mHours.clear();
        notifyDataSetChanged();
    }

    static class DoctorScheduleHourViewHolder extends RecyclerView.ViewHolder {
        Button btnHour;

        DoctorScheduleHourViewHolder(View v) {
            super(v);
            btnHour = v.findViewById(R.id.hour_button);
        }
    }
}
