package br.com.avanade.fahz.Adapter.lgpd;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.lgpdModel.PanelCards;

public class CardViewPolicyPrivacyAdapter extends RecyclerView.Adapter<CardViewPolicyPrivacyAdapter.CustomViewHolder> {

    private Context context;
    public List<PanelCards> textPanelList;

    public CardViewPolicyPrivacyAdapter(Context context, List<PanelCards> textPanels){
        this.context = context;
        this.textPanelList = textPanels;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_policy_privacy,  parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewPolicyPrivacyAdapter.CustomViewHolder holder, final int position) {

        final PanelCards textPanel = this.textPanelList.get(position);

        holder.textviewTitle.setText(Html.fromHtml(textPanel.title));
        holder.textviewComplete.setText(Html.fromHtml(textPanel.obs));
    }

    @Override
    public int getItemCount() {
        return (textPanelList !=null ? textPanelList.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView textviewTitle;
        TextView textviewComplete;
        CardView cardView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        CustomViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textviewTitle = (TextView) itemView.findViewById(R.id.textview_title);
            textviewComplete = (TextView) itemView.findViewById(R.id.textview_complete);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
