package br.com.avanade.fahz.Adapter.lgpd;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.lgpd.ControlOfPrivacyActivity;
import br.com.avanade.fahz.activities.lgpd.NotificationActivity;
import br.com.avanade.fahz.activities.lgpd.TermsControlActivity;
import br.com.avanade.fahz.model.lgpdModel.QuickLinkPanel;
import br.com.avanade.fahz.util.Constants;

public class CardViewPolicyPrivacyQuickLinkAdapter extends RecyclerView.Adapter<CardViewPolicyPrivacyQuickLinkAdapter.CustomViewHolder> {

    private Context context;
    public List<QuickLinkPanel> quickLinkPanelList;

    public CardViewPolicyPrivacyQuickLinkAdapter(Context context, List<QuickLinkPanel> quickLinkPanels){
        this.context = context;
        this.quickLinkPanelList = quickLinkPanels;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_policy_privacy_quick_link,  parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewPolicyPrivacyQuickLinkAdapter.CustomViewHolder holder, final int position) {

        final QuickLinkPanel textPanel = this.quickLinkPanelList.get(position);

        holder.textviewResume.setText(Html.fromHtml(textPanel.description));
        holder.textviewLink.setText(Html.fromHtml(textPanel.linkTo));
    }

    @Override
    public int getItemCount() {
        return (quickLinkPanelList !=null ? quickLinkPanelList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textviewResume;
        TextView textviewLink;
        CardView cardView;

        CustomViewHolder(final View itemView) {
            super(itemView);

            textviewResume = (TextView) itemView.findViewById(R.id.textview_resume);
            textviewLink = (TextView) itemView.findViewById(R.id.textview_link);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

            textviewLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int clickedPosition = getAdapterPosition();
                    if(quickLinkPanelList.get(clickedPosition).linkTo.equals(Constants.LINK_CONTROLES_DE_POLITICA_DE_PRIVACIDADE)){
                        Intent intent = new Intent(v.getContext(), ControlOfPrivacyActivity.class);
                        context.startActivity(intent);
                    }else if(quickLinkPanelList.get(clickedPosition).linkTo.equals(Constants.LINK_CONTROLES_DE_TERMOS_DE_SERVICO)){
                        Intent intent = new Intent(v.getContext(), TermsControlActivity.class);
                        context.startActivity(intent);
                    }else if (quickLinkPanelList.get(clickedPosition).linkTo.equals(Constants.LINK_CENTRAL_DE_NOTIFICACOES)){
                        Intent intent = new Intent(v.getContext(), NotificationActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
        }
    }
}