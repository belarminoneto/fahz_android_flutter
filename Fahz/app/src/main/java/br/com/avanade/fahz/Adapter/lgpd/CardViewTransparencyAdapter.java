package br.com.avanade.fahz.Adapter.lgpd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.lgpdModel.FamilyGroup;

public class CardViewTransparencyAdapter extends RecyclerView.Adapter<CardViewTransparencyAdapter.CustomViewHolder> {

    private Context context;
    private List<FamilyGroup> familyGroupList;

    public CardViewTransparencyAdapter(Context context, List<FamilyGroup> familyGroup){
        this.context = context;
        this.familyGroupList = familyGroup;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_transparency_familygroup, parent,false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final CardViewTransparencyAdapter.CustomViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final FamilyGroup familyGroup = this.familyGroupList.get(position);
        holder.imageViewDetails.setBackgroundResource(R.drawable.ic_arrow_drop_down);

        holder.constraintLayOutDetail.setVisibility(View.GONE);

        holder.textViewName.setText(familyGroup.name);
        holder.textViewOwnership.setText(familyGroup.ownership);

        TableRow row = new TableRow(context);
        holder.tableLayout.setStretchAllColumns(true);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);

        TextView textView = new TextView(context);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextSize(14);
        textView.setText(familyGroup.acceptedConditionsTitle);
        row.addView(textView);
        holder.tableLayout.addView(row);

        familyGroup.isOpen = false;

        if (familyGroup.acceptedConditions != null) {

            for (int j = 0; j < familyGroup.acceptedConditions.size(); j++) {

                //Row Terms
                TableRow rowTerms = new TableRow(context);
                holder.tableLayout.setStretchAllColumns(true);
                TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                rowTerms.setLayoutParams(lp2);

                TextView textContent = new TextView(context);
                textContent.setText(familyGroup.acceptedConditions.get(j).content);
                textContent.setPadding(0, 40, 0, 0);
                rowTerms.addView(textContent);
                holder.tableLayout.addView(rowTerms);

                //Row Data de aceite
                TableRow rowtextAcceptDate = new TableRow(context);
                rowtextAcceptDate.setLayoutParams(lp2);

                TextView textAcceptDate = new TextView(context);
                textAcceptDate.setText("Aceito em " + familyGroup.acceptedConditions.get(j).acceptDate);
                rowtextAcceptDate.addView(textAcceptDate);
                holder.tableLayout.addView(rowtextAcceptDate);

                //Row Data de aceite
                if(familyGroup.acceptedConditions.get(j).endDate != null ) {
                    TableRow rowEndDate = new TableRow(context);
                    rowEndDate.setLayoutParams(lp2);

                    TextView textEndDate = new TextView(context);
                    textEndDate.setText("Finalizado em " + familyGroup.acceptedConditions.get(j).endDate);
                    rowEndDate.addView(textEndDate);
                    holder.tableLayout.addView(rowEndDate);
                }

                //Row Status
                if(familyGroup.acceptedConditions.get(j).status.contains("Em Vigor")) {
                    TableRow rowStatus = new TableRow(context);
                    rowStatus.setLayoutParams(lp2);

                    TextView textStatus = new TextView(context);
                    textStatus.setText(familyGroup.acceptedConditions.get(j).status);
                    textStatus.setTextColor(context.getResources().getColor(R.color.green_steps) );
                    textStatus.setTypeface(textView.getTypeface(), Typeface.BOLD);

                    rowStatus.addView(textStatus);
                    holder.tableLayout.addView(rowStatus);
                }

                holder.imageViewDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!familyGroup.isOpen){
                            familyGroup.isOpen = true;
                            holder.imageViewDetails.setBackgroundResource(R.drawable.ic_arrow_drop_up);
                            holder.constraintLayOutDetail.setVisibility(View.VISIBLE);
                        }else{
                            familyGroup.isOpen = false;
                            holder.imageViewDetails.setBackgroundResource(R.drawable.ic_arrow_drop_down);
                            holder.constraintLayOutDetail.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }else{
            holder.imageViewDetails.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (familyGroupList !=null ? familyGroupList.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        TextView textViewName;
        TextView textViewOwnership;
        ImageView imageViewDetails;
        TableLayout tableLayout;
        ConstraintLayout constraintLayOutDetail;

        CustomViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            textViewOwnership = itemView.findViewById(R.id.textViewOwnership);
            cardView = itemView.findViewById(R.id.cardView);
            imageViewDetails = itemView.findViewById(R.id.imageViewDetails);
            constraintLayOutDetail = itemView.findViewById(R.id.constraintLayOutDetail);
            tableLayout = itemView.findViewById(R.id.tableLayout);
        }

        @Override
        public void onClick(View v) {
        }
    }
}

