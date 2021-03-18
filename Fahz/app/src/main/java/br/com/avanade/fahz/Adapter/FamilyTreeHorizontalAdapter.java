package br.com.avanade.fahz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.interfaces.OnLifeStatusClickListener;
import br.com.avanade.fahz.model.anamnesisModel.LifeStatusAnamnesis;

public class FamilyTreeHorizontalAdapter extends RecyclerView.Adapter<FamilyTreeHorizontalAdapter.ViewHolder> {

    private List<LifeStatusAnamnesis> mFamilyTreeList;
    private final OnLifeStatusClickListener mOnClickListener;
    private Context context;

    FamilyTreeHorizontalAdapter(List<LifeStatusAnamnesis> mFamilyTreeList, OnLifeStatusClickListener mOnClickListener, Context context) {
        this.mFamilyTreeList = mFamilyTreeList;
        this.mOnClickListener = mOnClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_family_tree, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final LifeStatusAnamnesis lifeAnamnesis = mFamilyTreeList.get(position);
        holder.txtTitle.setText(lifeAnamnesis.getTypeQuestionnaireFirstLetterCapitalized());
        holder.txtName.setText(lifeAnamnesis.getNameFirstLetterCapitalized());
        String type = context.getString(R.string.type_life, lifeAnamnesis.getTypeFirstLetterCapitalized());
        holder.txtType.setText(type);
        holder.txtStatus.setText(lifeAnamnesis.getStatusPercentage());

        int percentage = (int) lifeAnamnesis.getPercentage();
        holder.seekProgress.setProgress(percentage);
        holder.btnGoToQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.goToQuest(lifeAnamnesis);
            }
        });
        holder.btnGoToAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.goToAnswersList(lifeAnamnesis);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFamilyTreeList == null ? 0 : mFamilyTreeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtName, txtType, txtStatus;
        Button btnGoToAnswers, btnGoToQuest;
        SeekBar seekProgress;

        public ViewHolder(View item) {
            super(item);
            txtTitle = item.findViewById(R.id.txtTitle);
            txtName = item.findViewById(R.id.txtName);
            txtType = item.findViewById(R.id.txtType);
            txtStatus = item.findViewById(R.id.txtStatus);
            seekProgress = item.findViewById(R.id.seekProgress);
            btnGoToAnswers = item.findViewById(R.id.btnGoToAnswers);
            btnGoToQuest = item.findViewById(R.id.btnGoToQuest);
        }
    }
}
