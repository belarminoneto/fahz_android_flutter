package br.com.avanade.fahz.Adapter.lgpd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.lgpdModel.AcceptedCondition;

public class CardViewTransparencyDetailAdapter extends RecyclerView.Adapter<CardViewTransparencyDetailAdapter.CustomViewHolder> {

    private Context context;
    private List<AcceptedCondition> conditionList;

    public CardViewTransparencyDetailAdapter(Context context, List<AcceptedCondition> acceptedConditionList){
        this.context = context;
        this.conditionList = acceptedConditionList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_transparency_details_life, parent,false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CardViewTransparencyDetailAdapter.CustomViewHolder holder, int position) {

        AcceptedCondition acceptedCondition = this.conditionList.get(position);

        holder.textViewContent.setText(acceptedCondition.content);
        holder.textViewAcceptDate.setText(acceptedCondition.acceptDate);
        holder.textViewEndDate.setText(acceptedCondition.endDate);
        holder.textViewStatus.setText(acceptedCondition.status);
    }

    @Override
    public int getItemCount() {
        return (conditionList !=null ? conditionList.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView textViewContent;
        TextView textViewAcceptDate;
        TextView textViewEndDate;
        TextView textViewStatus;
        CardView cardView;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        CustomViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textViewContent = (TextView) itemView.findViewById(R.id.textViewContent);
            textViewAcceptDate = (TextView) itemView.findViewById(R.id.textViewAcceptDate);
            textViewEndDate = (TextView) itemView.findViewById(R.id.textViewEndDate);
            textViewStatus = (TextView) itemView.findViewById(R.id.textViewStatus);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Toast.makeText(context, conditionList.get(clickedPosition).content , Toast.LENGTH_LONG).show();
        }
    }
}

