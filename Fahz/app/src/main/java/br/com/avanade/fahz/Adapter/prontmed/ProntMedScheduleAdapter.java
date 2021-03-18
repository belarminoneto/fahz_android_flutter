package br.com.avanade.fahz.Adapter.prontmed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.fragments.benefits.healthplan.prontmed.ProntmedScheduleFragment;
import br.com.avanade.fahz.model.response.prontmed.ListOfAppointmentsItemResponse;

public class ProntMedScheduleAdapter extends RecyclerView.Adapter<ProntMedScheduleAdapter.ScheduleItemViewHolder> {
    private List<ListOfAppointmentsItemResponse> mDataset;
    private Context mContext;
    private ProntmedScheduleFragment parentFragment;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProntMedScheduleAdapter(@NonNull Context context, @NonNull List<ListOfAppointmentsItemResponse> myDataset, @NonNull ProntmedScheduleFragment fragment) {
        mDataset = myDataset;
        mContext = context;
        parentFragment = fragment;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ScheduleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_prontmed_schedule, parent, false);
        return new ScheduleItemViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ScheduleItemViewHolder holder, int position) {
        ListOfAppointmentsItemResponse schedule = mDataset.get(position);

        if (schedule == null) {
            holder.viewBackground.setVisibility(View.INVISIBLE);
            holder.viewForeground.setVisibility(View.INVISIBLE);
//            holder.btShowMore.setVisibility(View.VISIBLE);
//            holder.btShowMore.setEnabled(true);
//            holder.btShowMore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    parentFragment.updateSkip();
//                    parentFragment.getSchedules();
//                }
//            });
        } else {
            holder.viewBackground.setVisibility(View.INVISIBLE);
            holder.viewForeground.setVisibility(View.VISIBLE);
//            holder.btShowMore.setVisibility(View.INVISIBLE);
//            holder.btShowMore.setEnabled(false);
//            holder.btShowMore.setOnClickListener(null);

            holder.tvScheduleDateDay.setText(schedule.getDay());
            holder.tvScheduleDateMonth.setText(schedule.getMonth());
            holder.tvScheduleDateHour.setText(schedule.getHourAndMinutes());
            //holder.tvDoctorType.setText();
            holder.tvDoctorName.setText(schedule.getDoctorName());
            //holder.tvDoctorSpeciality.setText(schedule.getSpeciality().toString().replace("[", "").replace("]", ""));
            holder.tvBeneficiaryType.setText(mContext.getString(schedule.getBeneficiaryTypeStringResource()));
            holder.tvBeneficiaryName.setText(schedule.getName());
            holder.cardStatus.setCardBackgroundColor(
                    holder.cardStatus.getContext().getResources().getColor(schedule.getStatusColor()));
            holder.tvStatus.setText(schedule.getStatus());
            if(schedule.getAddress()!=null) {
                holder.tvAddress.setText("Rua " + schedule.getAddress().getLogradouro() + " " +
                        schedule.getAddress().getNumber() + " " +
                        schedule.getAddress().getComplement() + ", " +
                        schedule.getAddress().getNeighborhood() + ", " +
                        schedule.getAddress().getCity() + " - " +
                        schedule.getAddress().getState() + ". " +
                        "CEP: " + schedule.getAddress().getZipCode());
            }
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ListOfAppointmentsItemResponse getItem(int position) {
        return mDataset.get(position);
    }

    public void addItems(List<ListOfAppointmentsItemResponse> list) {
        if (list != null && list.size() > 0) {
            if (mDataset != null && mDataset.size() > 0) {
                int lastIndex = mDataset.size() - 1;
                mDataset.remove(lastIndex);
                notifyItemRemoved(lastIndex);
                mDataset.addAll(list);
                mDataset.add(null);
                notifyItemRangeChanged(lastIndex, list.size() + 1);
            } else {
                mDataset = list;
                mDataset.add(null);
                notifyItemRangeInserted(0, mDataset.size());
            }
        }
    }

    public static class ScheduleItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvScheduleDateDay;
        TextView tvScheduleDateMonth;
        TextView tvScheduleDateHour;
        //TextView tvDoctorType;
        TextView tvDoctorName;
        TextView tvBeneficiaryType;
        TextView tvBeneficiaryName;
        TextView tvStatus;
        //TextView tvDoctorSpeciality;
        TextView tvAddress;
        public ConstraintLayout viewBackground;
        public CardView viewForeground;
        CardView cardStatus;
        //Button btShowMore;

        ScheduleItemViewHolder(View v) {
            super(v);
            //tvDoctorType = v.findViewById(R.id.tv_doctor_type);
            tvDoctorName = v.findViewById(R.id.tv_hour);
            tvScheduleDateDay = v.findViewById(R.id.tv_schedule_date_day);
            tvScheduleDateMonth = v.findViewById(R.id.tv_schedule_date_month);
            tvScheduleDateHour = v.findViewById(R.id.tv_schedule_date_hour);
            tvBeneficiaryType = v.findViewById(R.id.tv_beneficiary_type);
            tvBeneficiaryName = v.findViewById(R.id.tv_beneficiary_name);
            tvStatus = v.findViewById(R.id.tv_status);
            //tvDoctorSpeciality = v.findViewById(R.id.tv_doctor_speciality);
            viewBackground = v.findViewById(R.id.view_background);
            viewForeground = v.findViewById(R.id.view_foreground);
            cardStatus = v.findViewById(R.id.card_status);
            //btShowMore = v.findViewById(R.id.bt_show_more);
            tvAddress = v.findViewById(R.id.tv_address);
        }
    }
}