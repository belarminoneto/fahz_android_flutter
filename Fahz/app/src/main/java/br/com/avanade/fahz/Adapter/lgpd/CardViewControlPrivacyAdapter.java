package br.com.avanade.fahz.Adapter.lgpd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.lgpdModel.Control;

public class CardViewControlPrivacyAdapter extends RecyclerView.Adapter<CardViewControlPrivacyAdapter.CustomViewHolder> {

    private Context context;
    public List<Control> mControlList;

    public CardViewControlPrivacyAdapter(Context context, List<Control> controls){
        this.context = context;
        this.mControlList = controls;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_control_of_privacy, parent,false);
        final CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewControlPrivacyAdapter.CustomViewHolder holder, final int position) {

        final Control control = this.mControlList.get(position);

        holder.textViewDescription.setText(control.description);
        holder.textViewLink.setText(control.link);

        if(control.title != null)
            holder.textviewTitle.setText(control.title);
        else
            holder.textviewTitle.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return (mControlList !=null ? mControlList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView textviewTitle;
        TextView textViewDescription;
        TextView textViewLink;
        CardView cardView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        CustomViewHolder(final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textviewTitle = (TextView) itemView.findViewById(R.id.textview_title);
            textViewDescription = (TextView) itemView.findViewById(R.id.textView_description);
            textViewLink = (TextView) itemView.findViewById(R.id.textView_link);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

            textViewLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                }
            });
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
        }
    }
}



