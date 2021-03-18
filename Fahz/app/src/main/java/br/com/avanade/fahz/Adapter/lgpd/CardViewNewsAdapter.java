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
import br.com.avanade.fahz.model.lgpdModel.NewsPlainText;

public class CardViewNewsAdapter extends RecyclerView.Adapter<CardViewNewsAdapter.CustomViewHolder> {

    public List<NewsPlainText> newsPlainTextList;
    private Context context;

    public CardViewNewsAdapter(Context context, List<NewsPlainText> newsPlainTextList) {
        this.context = context;
        this.newsPlainTextList = newsPlainTextList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_news, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewNewsAdapter.CustomViewHolder holder, final int position) {

        final NewsPlainText newsPlainText = this.newsPlainTextList.get(position);

        holder.textviewTitle.setText(Html.fromHtml(newsPlainText.title));
        holder.textviewComplete.setText(Html.fromHtml(newsPlainText.text));
    }

    @Override
    public int getItemCount() {
        return (newsPlainTextList != null ? newsPlainTextList.size() : 0);
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
