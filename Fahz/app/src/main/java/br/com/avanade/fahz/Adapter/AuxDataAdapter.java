package br.com.avanade.fahz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.interfaces.OnAuxDataClickListener;
import br.com.avanade.fahz.model.anamnesisModel.AuxDataBridge;

import static android.view.View.VISIBLE;

public class AuxDataAdapter extends RecyclerView.Adapter<AuxDataAdapter.ViewHolder> {

    private List<AuxDataBridge> auxDataList = new ArrayList<>();
    private final OnAuxDataClickListener mOnClickListener;

    public AuxDataAdapter(OnAuxDataClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public AuxDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_aux_data, parent, false);
        return new AuxDataAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AuxDataAdapter.ViewHolder holder, int position) {
        final AuxDataBridge auxData = auxDataList.get(position);

        holder.txtTitle1.setText(auxData.getTitle1FirstLetterCapitalized());
        holder.txtDesc1.setText(auxData.getDesc1FirstLetterCapitalized());

        if (auxData.getTitle2() != null) {
            holder.txtTitle2.setText(auxData.getTitle2FirstLetterCapitalized());
            holder.txtDesc2.setText(auxData.getDesc2FirstLetterCapitalized());
            holder.txtTitle2.setVisibility(VISIBLE);
            holder.txtDesc2.setVisibility(VISIBLE);
        }

        if (auxData.getTitle3() != null) {
            holder.txtTitle3.setText(auxData.getTitle3FirstLetterCapitalized());
            holder.txtDesc3.setText(auxData.getDesc3FirstLetterCapitalized());
            holder.txtTitle3.setVisibility(VISIBLE);
            holder.txtDesc3.setVisibility(VISIBLE);
        }

        if (auxData.getTitle4() != null) {
            holder.txtTitle4.setText(auxData.getTitle4FirstLetterCapitalized());
            holder.txtDesc4.setText(auxData.getDesc4FirstLetterCapitalized());
            holder.txtTitle4.setVisibility(VISIBLE);
            holder.txtDesc4.setVisibility(VISIBLE);
        }
        holder.checkBox.setChecked(auxData.isSelected());

        holder.itemAuxData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClickAuxData(auxData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return auxDataList == null ? 0 : auxDataList.size();
    }

    public void setItems(List<AuxDataBridge> auxDataList) {
        this.auxDataList = auxDataList;
        notifyDataSetChanged();
    }

    public void setLastUnselectedData(AuxDataBridge lastUnselectedAuxData) {
        for (int i = 0; i < auxDataList.size(); i++) {
            AuxDataBridge auxDataTemp = auxDataList.get(i);
            if (String.valueOf(auxDataTemp.getId()).equalsIgnoreCase(lastUnselectedAuxData.getId())) {
                auxDataTemp.setSelected(false);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void deselectAllAuxData() {
        for (AuxDataBridge auxData : auxDataList) {
            auxData.setSelected(false);
        }
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle1, txtTitle2, txtTitle3, txtTitle4,
                txtDesc1, txtDesc2, txtDesc3, txtDesc4;
        View itemAuxData;
        CheckBox checkBox;

        public ViewHolder(View item) {
            super(item);
            txtTitle1 = item.findViewById(R.id.txtTitle1);
            txtTitle2 = item.findViewById(R.id.txtTitle2);
            txtTitle3 = item.findViewById(R.id.txtTitle3);
            txtTitle4 = item.findViewById(R.id.txtTitle4);
            txtDesc1 = item.findViewById(R.id.txtDesc1);
            txtDesc2 = item.findViewById(R.id.txtDesc2);
            txtDesc3 = item.findViewById(R.id.txtDesc3);
            txtDesc4 = item.findViewById(R.id.txtDesc4);
            itemAuxData = item.findViewById(R.id.itemAuxData);
            checkBox = item.findViewById(R.id.checkbox);
        }
    }
}
