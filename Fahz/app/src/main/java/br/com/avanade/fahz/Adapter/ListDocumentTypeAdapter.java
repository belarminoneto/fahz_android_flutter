package br.com.avanade.fahz.Adapter;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.fragments.UploadDialogFragment;
import br.com.avanade.fahz.model.Dependent;
import br.com.avanade.fahz.model.DependentHolder;
import br.com.avanade.fahz.model.DocumentDownload;
import br.com.avanade.fahz.model.DocumentType;
import br.com.avanade.fahz.model.Documents;
import br.com.avanade.fahz.model.life.DependentHolderBody;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.DocumentsHelper;
import br.com.avanade.fahz.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;

public class ListDocumentTypeAdapter extends RecyclerView.Adapter<ListDocumentTypeAdapter.DocumentTypesViewHolder> {
    private Context mContext;
    private ArrayList<DocumentType> mDocumentTypes;
    private RecyclerView mDocumentsRecyclerView;
    private String mCpf;
    private FragmentManager mSuportFragment;
    private final DeleteOnClickHandler mClickHandler;
    private boolean mCanSendDocuments;
    private String mPreviousMessageToShowSelectedDocument;
    private Dialog dialog;
    private DocumentType documentTypeSaved;

    /**
     * The interface that receives onClick messages.
     */
    public interface DeleteOnClickHandler {
        void onDelete(String mPath, String mCpf);
    }


    public ListDocumentTypeAdapter(@NonNull Context context, @NonNull ArrayList<DocumentType> documentTypes, String cpf, FragmentManager supportFragment, DeleteOnClickHandler clickHandler,
                                   String previousMessage) {
        mContext = context;
        mDocumentTypes = documentTypes;
        mCpf = cpf;
        mSuportFragment = supportFragment;
        mClickHandler = clickHandler;
        mCanSendDocuments = true;
        mPreviousMessageToShowSelectedDocument = previousMessage;
    }

    public ListDocumentTypeAdapter(@NonNull Context context, @NonNull ArrayList<DocumentType> documentTypes, String cpf, FragmentManager supportFragment, DeleteOnClickHandler clickHandler,
                                   boolean canSendDocuments) {
        mContext = context;
        mDocumentTypes = documentTypes;
        mCpf = cpf;
        mSuportFragment = supportFragment;
        mClickHandler = clickHandler;
        mCanSendDocuments = canSendDocuments;
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
        final DocumentType documentType = mDocumentTypes.get(position);
        boolean hasAddedNewDocument = false;
        for (Documents document : documentType.documents) {
            if (document.newAdition) {
                hasAddedNewDocument = true;
            }
        }

        if (documentType.userHasIt && (hasAddedNewDocument || documentType.documents.size() > 0)) {
            Drawable statusOK = mContext.getResources().getDrawable(R.drawable.status_ok);
            holder.imgStatus.setImageDrawable(statusOK);
        } else {
            Drawable statusError = mContext.getResources().getDrawable(R.drawable.status_error);
            holder.imgStatus.setImageDrawable(statusError);
        }

        if (documentType.userCanUpload)
            holder.btnAdd.setVisibility(View.VISIBLE);
        else
            holder.btnAdd.setVisibility(View.INVISIBLE);

        holder.txtTitleDocument.setText(documentType.description);
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCanSendDocuments) {
                    if(mPreviousMessageToShowSelectedDocument!=null) {
                        documentTypeSaved = mDocumentTypes.get(position);
                        dialog = Utils.showAlertDialogReturnDialog(mContext.getString(R.string.dialog_title), mPreviousMessageToShowSelectedDocument,
                                "Entendi", mContext, alertClick);
                    }
                    else
                        ShowUploadDocument(mDocumentTypes.get(position));
                }
                else
                {
                    int resID = mContext.getResources().getIdentifier("MSG344", "string", mContext.getPackageName());
                    String message = mContext.getResources().getString(resID);
                    Utils.showSimpleDialog(mContext.getString(R.string.dialog_title), message, null, mContext, null);

                }
            }
        });

        if (!mCanSendDocuments) {
            holder.btnAdd.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.custom_disabled_button_add));
        } else {
            holder.btnAdd.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.custom_button_add));
        }

        ListDocumentContentAdapter adapter = new ListDocumentContentAdapter(mContext, documentType.documents);
        mDocumentsRecyclerView.setAdapter(adapter);

    }

    public void ShowUploadDocument(DocumentType documentType){
        Bundle bundle = new Bundle();
        bundle.putString("cpf", mCpf);
        bundle.putString("title", "Upload de documento");
        bundle.putInt("id", documentType.id);
        bundle.putBoolean("isNewType", false);

        UploadDialogFragment uploadDialogFragment = new UploadDialogFragment();
        uploadDialogFragment.setArguments(bundle);
        uploadDialogFragment.show(mSuportFragment, "uploadFile");
    }

    private View.OnClickListener alertClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(dialog != null) {
                dialog.dismiss();
            }
            ShowUploadDocument(documentTypeSaved);
        }
    };

    @Override
    public int getItemCount() {
        return mDocumentTypes.size();
    }

    class DocumentTypesViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgStatus;
        private TextView txtTitleDocument;
        private Button btnAdd;

        DocumentTypesViewHolder(View itemView) {
            super(itemView);
            imgStatus = itemView.findViewById(R.id.image_status);
            txtTitleDocument = itemView.findViewById(R.id.text_title_document);
            btnAdd = itemView.findViewById(R.id.button_add);
        }
    }

    class ListDocumentContentAdapter extends RecyclerView.Adapter<ListDocumentContentAdapter.ListDocumentViewHolder> {
        private Context mContext;
        private List<Documents> mListDocuments;

        ListDocumentContentAdapter(@NonNull Context context, @NonNull List<Documents> listDocuments) {
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
            final Documents document = mListDocuments.get(position);
            String date = DateEditText.formatDate(document.date, "yyyyMMdd", "dd/MM/yyyy");
            holder.txtDateDescription.setText(date);

            holder.txtNameDescription.setText(document.name);

            if (document.newAdition)
                holder.btnDeleteDocument.setVisibility(View.VISIBLE);
            else
                holder.btnDeleteDocument.setVisibility(View.INVISIBLE);

            holder.btnDeleteDocument.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickHandler.onDelete(document.path, mCpf);
                }
            });

            holder.documentContrainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
                        Call<DocumentDownload> call = mAPIService.downloadDocument(document.path, mCpf);
                        call.enqueue(new Callback<DocumentDownload>() {
                            @RequiresApi(api = Build.VERSION_CODES.Q)
                            @Override
                            public void onResponse(@NonNull Call<DocumentDownload> call, @NonNull Response<DocumentDownload> response) {
                                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                                if (response.isSuccessful()) {
                                    DocumentDownload documentBody = response.body();
                                    try{
                                        File ext = DocumentsHelper.saveFileIntoExternalStorage(mContext,documentBody);
                                        Uri uriFile = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName(), ext);
                                        openFile(mContext,uriFile,documentBody);
                                    }catch (Exception e){
                                        System.out.println("Error: "+e.getMessage());
                                        Toast.makeText(mContext,"Erro ao abrir o arquivo!",Toast.LENGTH_LONG);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<DocumentDownload> call, @NonNull Throwable t) {

                            }
                        });
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

        /**
         * Open file
         *
         * @param uri path to file
         */
        public void openFile(Context context,Uri uri, DocumentDownload document) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            if (document.Filename.contains(".doc") || document.Filename.contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (document.Filename.contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (document.Filename.contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (document.Filename.contains(".jpg") || document.Filename.contains(".jpeg") || document.Filename.contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else {
                // Other files
                intent.setDataAndType(uri, document.Mimetype);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
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

}