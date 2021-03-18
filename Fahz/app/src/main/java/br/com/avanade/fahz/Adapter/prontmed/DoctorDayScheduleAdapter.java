package br.com.avanade.fahz.Adapter.prontmed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.fragments.benefits.healthplan.prontmed.SearchTimeFragment;
import br.com.avanade.fahz.model.ScheduleDoctorDay;

public class DoctorDayScheduleAdapter extends RecyclerView.Adapter<DoctorDayScheduleAdapter.DoctorScheduleItemViewHolder> {

    private final Context mContext;
    private List<Integer> mAvailableDay;
    private List<ScheduleDoctorDay> mDays;
    private SearchTimeFragment parentFragment;
    int index = -1;


    public DoctorDayScheduleAdapter(Context context, List<Integer> availableDay, SearchTimeFragment parentFragment) {
        this.mContext = context;
        mAvailableDay = availableDay;
        this.parentFragment = parentFragment;
        mDays = new ArrayList<>();
    }

    @NonNull
    @Override
    public DoctorScheduleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_doctor_day, parent, false);
        return new DoctorScheduleItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final @NonNull DoctorScheduleItemViewHolder holder, final int position) {

        //Com a lista adicionar informacoes de Data
        final ScheduleDoctorDay item = mDays.get(position);

        if(item!= null && item.getDate()!=null) {
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("E");
            SimpleDateFormat dateFormt = new SimpleDateFormat("dd/MM");// the day of the week abbreviated
            holder.btnDay.setText(simpleDateformat.format(item.getDate()) + "\n" + dateFormt.format(item.getDate()));

            holder.btnDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index = position;
                    notifyDataSetChanged();
                    parentFragment.searchAppointments(item.getDate());
                }
            });

            if(index ==-1 && item.isShouldBeSelected())
            {
                index = position;
                parentFragment.searchAppointments(item.getDate());
            }

            if(index==position){
                holder.btnDay.setEnabled(true);
                holder.btnDay.setTextColor(mContext.getResources().getColor(R.color.white01));
                holder.btnDay.setBackgroundColor(mContext.getResources().getColor(R.color.blue_dark));
            } else if (item.isAvailable()) {
                holder.btnDay.setEnabled(true);
                holder.btnDay.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.btnDay.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            } else if (!item.isAvailable()) {
                holder.btnDay.setEnabled(false);
                holder.btnDay.setTextColor(mContext.getResources().getColor(R.color.grey_light_text));
                holder.btnDay.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            }

        }

    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    public void addItems(List<ScheduleDoctorDay> days) {
        if (mDays == null) {
            mDays = days;
            notifyDataSetChanged();
        } else {
            int lastIndex = 0;
            if (mDays.size() > 0) {
                lastIndex = mDays.size() - 1;
                mDays.remove(lastIndex);
                notifyItemRemoved(lastIndex);
            }
            mDays.addAll(days);
            notifyItemRangeInserted(lastIndex, days.size() + 1);
        }
    }

    public void clear() {
        mDays.clear();
        notifyDataSetChanged();
    }

    public ScheduleDoctorDay getItem(int position) {
        return mDays.get(position);
    }

    static class DoctorScheduleItemViewHolder extends RecyclerView.ViewHolder {
        Button btnDay;

        DoctorScheduleItemViewHolder(View v) {
            super(v);
            btnDay = v.findViewById(R.id.day_button);
        }
    }
}
