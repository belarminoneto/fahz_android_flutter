package br.com.avanade.fahz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.anamnesisModel.LifeAnamnesis;

public class LifeAdapter extends RecyclerView.Adapter<LifeAdapter.ViewHolder> {

    private List<LifeAnamnesis> mLifeAnamnesisList;
    private final View.OnClickListener mOnClickListener;
    private Context context;

    public LifeAdapter(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public LifeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_life, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        return new LifeAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LifeAdapter.ViewHolder holder, int position) {
        LifeAnamnesis lifeAnamnesis = mLifeAnamnesisList.get(position);
        holder.txtName.setText(lifeAnamnesis.getNameFirstLetterCapitalized());
        holder.txtType.setText(lifeAnamnesis.getTypeFirstLetterCapitalized());

        String cpf = context.getString(R.string.cpf_life, lifeAnamnesis.getCpf());
        holder.txtCPF.setText(cpf);
        String sharpID = context.getString(R.string.sharpid_life, String.valueOf(lifeAnamnesis.getSharpID()));
        holder.txtSharp.setText(sharpID);
    }

    @Override
    public int getItemCount() {
        return mLifeAnamnesisList == null ? 0 : mLifeAnamnesisList.size();
    }

    public void setItems(List<LifeAnamnesis> mFamilyTreeList) {
        this.mLifeAnamnesisList = mFamilyTreeList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtCPF;
        TextView txtSharp;

        public ViewHolder(View item) {
            super(item);
            txtName = item.findViewById(R.id.txtName);
            txtType = item.findViewById(R.id.txtType);
            txtCPF = item.findViewById(R.id.txtCPF);
            txtSharp = item.findViewById(R.id.txtSharp);
        }
    }
}