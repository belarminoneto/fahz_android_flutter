package br.com.avanade.fahz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.interfaces.OnDivisorClickListener;
import br.com.avanade.fahz.model.anamnesisModel.Question;

public class DivisionAdapter extends RecyclerView.Adapter<DivisionAdapter.ViewHolder> {

    private List<Question> mQuestionList;
    private OnDivisorClickListener mListener;

    public DivisionAdapter(OnDivisorClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public DivisionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_divisor, parent, false);
        return new DivisionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DivisionAdapter.ViewHolder holder, int position) {
        final Question divisor = mQuestionList.get(position);
        holder.txtTitle.setText(divisor.getText());
        holder.itemDivisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickDivisor(divisor);
            }
        });
    }

    public void setupDivisor(List<Question> questionList) {
        mQuestionList = questionList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mQuestionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        View itemDivisor;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            itemDivisor = itemView.findViewById(R.id.itemDivisor);
        }
    }
}
