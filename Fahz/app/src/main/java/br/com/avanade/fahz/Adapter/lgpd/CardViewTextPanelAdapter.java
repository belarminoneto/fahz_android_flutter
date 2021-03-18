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
import br.com.avanade.fahz.model.lgpdModel.TextPanel;

public class CardViewTextPanelAdapter extends RecyclerView.Adapter<CardViewTextPanelAdapter.CustomViewHolder> {

    private Context context;
    public List<TextPanel> textPanelList;

    public CardViewTextPanelAdapter(Context context, List<TextPanel> textPanels){
        this.context = context;
        this.textPanelList = textPanels;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_textpanel, parent,false);
        CardViewTextPanelAdapter.CustomViewHolder viewHolder = new CardViewTextPanelAdapter.CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewTextPanelAdapter.CustomViewHolder holder, final int position) {

        final TextPanel textPanel = this.textPanelList.get(position);

        holder.textViewTitle.setText(Html.fromHtml(textPanel.title));
        holder.textViewComplete.setText(Html.fromHtml(textPanel.complete));
    }

    @Override
    public int getItemCount() {
        return (textPanelList !=null ? textPanelList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewTitle;
        TextView textViewComplete;
        CardView cardView;

        CustomViewHolder(View itemView) {
            super(itemView);

            textViewTitle = (TextView) itemView.findViewById(R.id.textview_title);
            textViewComplete = (TextView) itemView.findViewById(R.id.textview_complete);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }
}