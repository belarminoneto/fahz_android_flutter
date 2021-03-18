package br.com.avanade.fahz.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.response.AnnualDocumentsByDependentResponse;

public class ListAnnualDocumentYearDocumentContentAdapter extends RecyclerView.Adapter<ListAnnualDocumentYearDocumentContentAdapter.ListDocumentViewHolder> {
    private Context mContext;
    private List<AnnualDocumentsByDependentResponse.Result.Year.TypeDocument.Document> mListDocuments;

    ListAnnualDocumentYearDocumentContentAdapter(@NonNull Context context, @NonNull List<AnnualDocumentsByDependentResponse.Result.Year.TypeDocument.Document> listDocuments) {
        mContext = context;
        mListDocuments = listDocuments;
    }

    @NonNull
    @Override
    public ListDocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_document_content,
                parent, false);

        return new ListDocumentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDocumentViewHolder holder, final int position) {
        final AnnualDocumentsByDependentResponse.Result.Year.TypeDocument.Document document = mListDocuments.get(position);
        holder.txtDateDescription.setText(DateEditText.formatDate(document.getDate()));
        holder.txtNameDescription.setText(document.getName());
        holder.btnDeleteDocument.setVisibility(View.INVISIBLE);
        holder.documentContrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(document.getPath()));
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // no Activity to handle this kind of files
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListDocuments.size();
    }

    class ListDocumentViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout documentContrainer;
        private TextView txtDateDescription;
        private TextView txtNameDescription;
        private ImageButton btnDeleteDocument;

        ListDocumentViewHolder(View itemView) {
            super(itemView);

            documentContrainer = itemView.findViewById(R.id.document_container);
            txtDateDescription = itemView.findViewById(R.id.text_date_description);
            txtNameDescription = itemView.findViewById(R.id.text_name_document);
            btnDeleteDocument = itemView.findViewById(R.id.button_delete_document);
        }
    }
}
