package br.com.avanade.fahz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.response.AnnualDocumentsByDependentResponse;

public class ListAnnualDocumentYearDocumentTypeAdapter extends RecyclerView.Adapter<ListAnnualDocumentYearDocumentTypeAdapter.DocumentTypesViewHolder> {
    private Context mContext;
    private List<AnnualDocumentsByDependentResponse.Result.Year.TypeDocument> mAnnualDocumentYearList;
    private RecyclerView mDocumentsRecyclerView;

    ListAnnualDocumentYearDocumentTypeAdapter(Context mContext, List<AnnualDocumentsByDependentResponse.Result.Year.TypeDocument> typeDocuments) {
        this.mContext = mContext;
        mAnnualDocumentYearList = typeDocuments;
    }

    @NonNull
    @Override
    public DocumentTypesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_document_header,
                parent, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mDocumentsRecyclerView = itemView.findViewById(R.id.recyclerViewDocumentFiles);
        mDocumentsRecyclerView.setHasFixedSize(true);
        mDocumentsRecyclerView.setLayoutManager(layoutManager);

        return new DocumentTypesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentTypesViewHolder holder, final int position) {
        final AnnualDocumentsByDependentResponse.Result.Year.TypeDocument documentYearType = mAnnualDocumentYearList.get(position);

        holder.header.setBackgroundColor(mContext.getResources().getColor(R.color.grey_content));
        holder.txtTitleDocument.setText(String.valueOf(documentYearType.getDescription()));
        holder.btnAdd.setVisibility(View.GONE);
        holder.imgStatus.setVisibility(View.GONE);
        ListAnnualDocumentYearDocumentContentAdapter adapter = new ListAnnualDocumentYearDocumentContentAdapter(mContext, documentYearType.getDocuments());
        mDocumentsRecyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mAnnualDocumentYearList.size();
    }

    class DocumentTypesViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout header;
        private ImageView imgStatus;
        private TextView txtTitleDocument;
        private Button btnAdd;

        DocumentTypesViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
            imgStatus = itemView.findViewById(R.id.image_status);
            txtTitleDocument = itemView.findViewById(R.id.text_title_document);
            btnAdd = itemView.findViewById(R.id.button_add);
        }
    }

}