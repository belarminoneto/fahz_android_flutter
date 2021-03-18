package br.com.avanade.fahz.Adapter.prontmed;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import br.com.avanade.fahz.fragments.benefits.healthplan.prontmed.FindDoctorsFragment;
import br.com.avanade.fahz.model.response.prontmed.DoctorItemResponse;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorItemViewHolder> {

    
    private final Context mContext;
    private List<DoctorItemResponse> mDataset;
    private BaseHealthControlActivity parentActivity;
    private FindDoctorsFragment parentFragment;

    public DoctorsAdapter(Context context, BaseHealthControlActivity activity, FindDoctorsFragment fragment) {
        this.mContext = context;
        mDataset = new ArrayList<>();
        parentActivity = activity;
        parentFragment = fragment;
    }

    @NonNull
    @Override
    public DoctorItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_prontmed_doctor, parent, false);
        return new DoctorsAdapter.DoctorItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorItemViewHolder holder, int position) {
        final int index = position;
        final DoctorItemResponse doctorItemResponse = mDataset.get(index);

        if (doctorItemResponse == null) {
            //holder.btShowMore.setEnabled(true);
//            holder.btShowMore.setVisibility(View.VISIBLE);
//            holder.btShowMore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    parentFragment.addPagination();
//                    parentFragment.callSearchSpecialityAPI();
//                }
//            });
            holder.cvBackground.setVisibility(View.INVISIBLE);
        } else {
            //holder.btShowMore.setEnabled(false);
//            holder.btShowMore.setVisibility(View.INVISIBLE);
//            holder.btShowMore.setOnClickListener(null);
            holder.cvBackground.setVisibility(View.VISIBLE);

            holder.tvDoctorName.setText(doctorItemResponse.getName());
            if(doctorItemResponse.getAddress()!=null) {
                holder.tvAddress.setText(doctorItemResponse.getAddress().getLogradouro() + " " + doctorItemResponse.getAddress().getNumber() +
                        " " + doctorItemResponse.getAddress().getComplement() +
                        ", " + doctorItemResponse.getAddress().getNeighborhood() +
                        ", " + doctorItemResponse.getAddress().getCity() +
                        " - " + doctorItemResponse.getAddress().getState() + "." +
                        " CEP: " + doctorItemResponse.getAddress().getZipCode());
            }

            holder.btSeeSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_button_animation));
                    parentActivity.setLoading(true, parentActivity.getString(R.string.loading));
                    Bundle bundle = new Bundle();
                    bundle.putString(FindDoctorsFragment.DOCTOR_BUNDLE_KEY, new Gson().toJson(doctorItemResponse));
                    parentActivity.setFragment(BaseHealthControlActivity.BaseHealthFragment.SEARCHTIME, bundle);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addItems(List<DoctorItemResponse> listResponse) {
        if (mDataset == null) {
            mDataset = listResponse;
            mDataset.add(null);
            notifyDataSetChanged();
        } else {
            int lastIndex = 0;
            if (mDataset.size() > 0) {
                lastIndex = mDataset.size() - 1;
                mDataset.remove(lastIndex);
                notifyItemRemoved(lastIndex);
            }
            mDataset.addAll(listResponse);
            mDataset.add(null);
            notifyItemRangeInserted(lastIndex, listResponse.size() + 1);
        }
    }

    static class DoctorItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName;
        TextView tvAddress;
        Button btSeeSchedule;
       // Button btShowMore;
        CardView cvBackground;

        DoctorItemViewHolder(View v) {
            super(v);
            tvDoctorName = v.findViewById(R.id.tv_hour);
            tvAddress = v.findViewById(R.id.address);
            btSeeSchedule = v.findViewById(R.id.bt_see_schedule);
            //btShowMore = v.findViewById(R.id.bt_show_more);
            cvBackground = v.findViewById(R.id.cv_doctor);
        }
    }
}
