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
import br.com.avanade.fahz.model.response.MajorDependentsForAnnualDocumentRenewalResponse;

public class ListAnnualDocumentYearAdapter extends RecyclerView.Adapter<ListAnnualDocumentYearAdapter.DocumentYearViewHolder> {

    private int mStatusAnnualRenewal;
    private ListAnnualDocumentYearAdapterButtonListener mClickListener;
    private Context mContext;
    private List<AnnualDocumentsByDependentResponse.Result.Year> mAnnualDocumentYearList;
    private RecyclerView mDocumentsRecyclerView;
    private boolean mCanSendDocuments;

    public ListAnnualDocumentYearAdapter(@NonNull Context context, @NonNull List<AnnualDocumentsByDependentResponse.Result.Year> documentTypes, @NonNull MajorDependentsForAnnualDocumentRenewalResponse.Dependent selectedDependent, @NonNull ListAnnualDocumentYearAdapterButtonListener mClickListener) {
        mContext = context;
        mAnnualDocumentYearList = documentTypes;
        mCanSendDocuments = selectedDependent.getStatusAnualRenewal() == 2; // unico status para envio de documentos
        mStatusAnnualRenewal = selectedDependent.getStatusAnualRenewal();
        this.mClickListener = mClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentYearViewHolder holder, final int position) {
        final AnnualDocumentsByDependentResponse.Result.Year documentYear = mAnnualDocumentYearList.get(position);

        holder.header.setBackgroundColor(mContext.getResources().getColor(R.color.grey_header));
        if (mStatusAnnualRenewal == 1) {
            holder.imgStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_ok));
        } else if (mStatusAnnualRenewal == 2 && position == 0) {
            holder.imgStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_error));
        } else if (mStatusAnnualRenewal == 0 && position == 0) {
            holder.imgStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_pending));
        } else if (mStatusAnnualRenewal == 3) {
            holder.imgStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_inactive));
        }

        holder.txtTitleDocument.setText(String.valueOf(documentYear.getYear()));
        holder.btnAdd.setEnabled(mStatusAnnualRenewal == 2);
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClick(v, documentYear.getYear().toString(), documentYear.getIdHistory());
            }
        });

        if (mCanSendDocuments && position == 0) {
            holder.btnAdd.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.custom_button_add));
        } else {
            holder.btnAdd.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.custom_disabled_button_add));
        }

        ListAnnualDocumentYearDocumentTypeAdapter adapter = new ListAnnualDocumentYearDocumentTypeAdapter(mContext, documentYear.getTypeDocuments());
        mDocumentsRecyclerView.setAdapter(adapter);
    }

    @NonNull
    @Override
    public DocumentYearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_document_header,
                parent, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mDocumentsRecyclerView = itemView.findViewById(R.id.recyclerViewDocumentFiles);
        mDocumentsRecyclerView.setHasFixedSize(true);
        mDocumentsRecyclerView.setLayoutManager(layoutManager);

        return new DocumentYearViewHolder(itemView);
    }

    public interface ListAnnualDocumentYearAdapterButtonListener {
        void onClick(View v, String selectedYear, String idHistory);
    }

    @Override
    public int getItemCount() {
        return mAnnualDocumentYearList != null ? mAnnualDocumentYearList.size() : 0;
    }

    class DocumentYearViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout header;
        private ImageView imgStatus;
        private TextView txtTitleDocument;
        private Button btnAdd;

        DocumentYearViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
            imgStatus = itemView.findViewById(R.id.image_status);
            txtTitleDocument = itemView.findViewById(R.id.text_title_document);
            btnAdd = itemView.findViewById(R.id.button_add);
        }
    }
}