package br.com.avanade.fahz.fragments.benefits.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.RegisterEntry;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class ExtractUsageAdapter extends RecyclerView.Adapter<ExtractUsageAdapter.ExtractUsageViewHolder> {

    private List<RegisterEntry> mList;
    private Context mContext;

    public ExtractUsageAdapter(List<RegisterEntry> list, Context context) {
        mList = list;
        mContext = context;
    }

    void updateData(List<RegisterEntry> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExtractUsageAdapter.ExtractUsageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_extract_usage_item,
                parent, false);
        return new ExtractUsageAdapter.ExtractUsageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExtractUsageAdapter.ExtractUsageViewHolder holder, final int position) {
        final RegisterEntry register = mList.get(position);
        if(register != null)
        {
            String useDate = DateEditText.parseTODate(register.getDataAtendimento());

            holder.dateRegister.setText(useDate);
            holder.hospitalRegister.setText(register.getNomePrestador());

            holder.dateRegister.setVisibility(View.VISIBLE);
            holder.hospitalRegister.setVisibility(View.VISIBLE);
            holder.separator.setVisibility(View.INVISIBLE);
            holder.recyclerviewRegisters.setVisibility(GONE);


            holder.read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controlVisibilityInformation(holder);
                }
            });

            holder.hospitalRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controlVisibilityInformation(holder);
                }
            });


            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
            holder.recyclerviewRegisters.setHasFixedSize(true);
            holder.recyclerviewRegisters.setLayoutManager(layoutManager);
            ExtractUsageDetailAdapter adapter = new ExtractUsageDetailAdapter(register.getUsageEntries(), mContext);
            holder.recyclerviewRegisters.setAdapter(adapter);

        }
        else
        {
            holder.separator.setVisibility(View.INVISIBLE);
            holder.recyclerviewRegisters.setVisibility(GONE);
            holder.hospitalRegister.setVisibility(GONE);
            holder.dateRegister.setVisibility(GONE);
        }
    }

    public void controlVisibilityInformation(ExtractUsageAdapter.ExtractUsageViewHolder holder)
    {
        if(holder.read.getText().equals(mContext.getString(R.string.read_more)))
        {
            holder.read.setText(mContext.getString(R.string.read_less));
            holder.separator.setVisibility(VISIBLE);
            holder.recyclerviewRegisters.setVisibility(VISIBLE);
        }
        else
        {
            holder.separator.setVisibility(View.INVISIBLE);
            holder.recyclerviewRegisters.setVisibility(View.GONE);
            holder.read.setText(mContext.getString(R.string.read_more));
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ExtractUsageViewHolder extends RecyclerView.ViewHolder {
        private TextView dateRegister;
        private TextView hospitalRegister;
        private TextView read;
        private View separator;

        private RecyclerView recyclerviewRegisters;

        ExtractUsageViewHolder(View itemView) {
            super(itemView);

            dateRegister = itemView.findViewById(R.id.date_register);
            hospitalRegister = itemView.findViewById(R.id.hospital_register);
            separator = itemView.findViewById(R.id.view_separator);
            read= itemView.findViewById(R.id.read);
            recyclerviewRegisters = itemView.findViewById(R.id.recyclerviewRegisters);

        }
    }
}
