package br.com.avanade.fahz.Adapter.healthplan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.healthplan.medicalrecord.MedicalRecordDetailsActivity;
import br.com.avanade.fahz.model.benefits.healthplan.medicalrecord.MedicalRecords;
import br.com.avanade.fahz.model.response.SmallLife;

public class CardViewMedicalRecordsAdapter extends RecyclerView.Adapter<CardViewMedicalRecordsAdapter.CustomViewHolder> {

    private Context context;
    public List<MedicalRecords> medicalRecordsList;
    public SmallLife smallLife;

    public CardViewMedicalRecordsAdapter(Context context, List<MedicalRecords> medicalRecords, SmallLife smallLife){
        this.context = context;
        this.medicalRecordsList = medicalRecords;
        this.smallLife = smallLife;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_medical_record,  parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewMedicalRecordsAdapter.CustomViewHolder holder, final int position) {

        final MedicalRecords textPanel = this.medicalRecordsList.get(position);

        holder.textviewConsultation.setText(Html.fromHtml(textPanel.consultation));
        holder.textviewDoctor.setText(Html.fromHtml(textPanel.doctor));
        holder.textviewCareprovider.setText(Html.fromHtml(textPanel.careProvider));
    }

    @Override
    public int getItemCount() {
        return (medicalRecordsList !=null ? medicalRecordsList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView textviewConsultation;
        TextView textviewDoctor;
        TextView textviewCareprovider;
        ImageView imageViewDetails;
        CardView cardView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        CustomViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textviewConsultation = (TextView) itemView.findViewById(R.id.textview_consultation);
            textviewDoctor = (TextView) itemView.findViewById(R.id.textview_doctor);
            textviewCareprovider = (TextView) itemView.findViewById(R.id.textview_careprovider);
            imageViewDetails = (ImageView) itemView.findViewById(R.id.imageViewDetails);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

            imageViewDetails.setOnClickListener(v -> {
                int clickedPosition = getAdapterPosition();

                Intent intent = new Intent(context, MedicalRecordDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("medicalRecord", (Parcelable) medicalRecordsList.get(clickedPosition));
                bundle.putSerializable("smallLife", (Serializable) smallLife);
                intent.putExtras(bundle);
                context.startActivity(intent, bundle);
            });
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            //Toast.makeText(context, medicalRecordsList.get(clickedPosition).doctor , Toast.LENGTH_LONG).show();
        }
    }
}
