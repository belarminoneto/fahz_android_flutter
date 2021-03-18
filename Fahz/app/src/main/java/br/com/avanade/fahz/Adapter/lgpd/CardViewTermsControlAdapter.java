package br.com.avanade.fahz.Adapter.lgpd;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.dialogs.ReadTermsActivity;
import br.com.avanade.fahz.model.lgpdModel.Register;

public class CardViewTermsControlAdapter extends RecyclerView.Adapter<CardViewTermsControlAdapter.CustomViewHolder> {

    private Context context;
    public List<Register> termOfUseList;

    public CardViewTermsControlAdapter(Context context, List<Register> termOfUseList){
        this.context = context;
        this.termOfUseList = termOfUseList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_terms_control, parent,false);
        final CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewTermsControlAdapter.CustomViewHolder holder, final int position) {

        final Register authorization = this.termOfUseList.get(position);

        holder.textTitle.setText(authorization.termOfUse.code);
    }

    @Override
    public int getItemCount() {
        return (termOfUseList !=null ? termOfUseList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView textTitle;
        ImageButton btnReadTerm, btnDownloadTerm, btnRevokeTerm;
        CardView cardView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        CustomViewHolder(final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textTitle = (TextView) itemView.findViewById(R.id.text_title);
            btnReadTerm = (ImageButton) itemView.findViewById(R.id.btnReadTerm);
            btnDownloadTerm = (ImageButton) itemView.findViewById(R.id.btnDownloadTerm);
            btnRevokeTerm = (ImageButton) itemView.findViewById(R.id.btnRevokeTerm);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

            btnReadTerm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    Intent intent = new Intent(v.getContext(), ReadTermsActivity.class);
                    intent.putExtra("textTitle", termOfUseList.get(clickedPosition).termOfUse.code);
                    intent.putExtra("readTerms", termOfUseList.get(clickedPosition).termOfUse.text);
                context.startActivity(intent);
                }
            });

            btnDownloadTerm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    Toast.makeText(context, "btnDownloadTerm " + termOfUseList.get(clickedPosition).termOfUse.code, Toast.LENGTH_LONG).show();
                }
            });

            btnRevokeTerm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    Toast.makeText(context, "btnRevokeTerm " + termOfUseList.get(clickedPosition).termOfUse.code, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
        }
    }
}
