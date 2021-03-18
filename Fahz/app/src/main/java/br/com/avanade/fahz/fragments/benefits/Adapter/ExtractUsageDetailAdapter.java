package br.com.avanade.fahz.fragments.benefits.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.UsageEntry;


public class ExtractUsageDetailAdapter extends RecyclerView.Adapter<ExtractUsageDetailAdapter.ExtractUsageDetailViewHolder> {

    private List<UsageEntry> mList;
    private Context mContext;

    public ExtractUsageDetailAdapter(List<UsageEntry> list, Context context) {
        mList = list;
        mContext = context;
    }

    void updateData(List<UsageEntry> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExtractUsageDetailAdapter.ExtractUsageDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.extract_usage_item,
                parent, false);
        return new ExtractUsageDetailAdapter.ExtractUsageDetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExtractUsageDetailAdapter.ExtractUsageDetailViewHolder holder, final int position) {
        final UsageEntry register = mList.get(position);

        holder.procedureDesc.setText(register.getDescricaoProcedimento());
        holder.valueDesc.setText(register.getValorRecebidoStr());
        holder.covalueDesc.setText(register.getValorParteEmpregadoStr());

        String useDate = DateEditText.parseTODate(register.getDataCronograma());
        holder.extractDateScheduleDesc.setText(useDate);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ExtractUsageDetailViewHolder extends RecyclerView.ViewHolder {
        private TextView procedureDesc;
        private TextView valueDesc;
        private TextView covalueDesc;
        private TextView extractDateScheduleDesc;

        ExtractUsageDetailViewHolder(View itemView) {
            super(itemView);

            procedureDesc = itemView.findViewById(R.id.procedure_desc);
            valueDesc = itemView.findViewById(R.id.value_desc);
            covalueDesc = itemView.findViewById(R.id.covalue_desc);
            extractDateScheduleDesc = itemView.findViewById(R.id.extract_date_schedule);

        }
    }
}