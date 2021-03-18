package br.com.avanade.fahz.Adapter.lgpd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.lgpdModel.Authorization;

public class CardViewNotificationAdapter extends RecyclerView.Adapter<CardViewNotificationAdapter.CustomViewHolder> {

    private Context context;
    public List<Authorization> permitionList;

    public CardViewNotificationAdapter(Context context, List<Authorization> permitions){
        this.context = context;
        this.permitionList = permitions;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_notification, parent,false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CardViewNotificationAdapter.CustomViewHolder holder, final int position) {

        final Authorization authorization = this.permitionList.get(position);

        holder.textviewAuthorization.setText(authorization.textAutorization);
        holder.textviewDetail.setText(authorization.textDetails);

        for (int i = 0; i < 1; i++) {
            TableRow row = new TableRow(context);
            holder.tableLayout.setStretchAllColumns(true);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);

            for (int j = 0; j < authorization.options.size(); j++){
                CheckBox checkBox = new CheckBox(context);
                checkBox.setId(authorization.options.get(j).id);
                checkBox.setText(authorization.options.get(j).descriptionOption);
                checkBox.setChecked(authorization.options.get(j).answerUser);
                final int k = j;
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        authorization.options.get(k).answerUser = isChecked;
                    }
                });
                row.addView(checkBox);
            }

            holder.tableLayout.addView(row,i);
        }
    }

    @Override
    public int getItemCount() {
        return (permitionList !=null ? permitionList.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView textviewAuthorization;
        TextView textviewDetail;
        CardView cardView;
        TableLayout tableLayout;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        CustomViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textviewAuthorization = (TextView) itemView.findViewById(R.id.textview_authorization);
            tableLayout = (TableLayout) itemView.findViewById(R.id.tableLayout);
            textviewDetail = (TextView) itemView.findViewById(R.id.textViewDetail);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
