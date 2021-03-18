package br.com.avanade.fahz.activities.benefits.toy.list;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import br.com.avanade.fahz.Adapter.ListDocumentTypeAdapter;
import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.toy.RequestNewToyActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.Document;
import br.com.avanade.fahz.model.DocumentByPerson;
import br.com.avanade.fahz.model.DocumentDeleteRequest;
import br.com.avanade.fahz.model.DocumentType;
import br.com.avanade.fahz.model.Documents;
import br.com.avanade.fahz.model.UploadResponse;
import br.com.avanade.fahz.model.document.QueryDocumentTypeGenericBody;
import br.com.avanade.fahz.model.response.DependentResponseData;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SystemBehavior;
import br.com.avanade.fahz.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;

public class DependentsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ListDocumentTypeAdapter.DeleteOnClickHandler {

    private static final int HEADER = 0;
    private static final int ROW = 1;
    private List<DependentListItem> list;
    private FragmentManager mSuportFragment;
    private SparseArray<DependentListRow> selected;
    private Context context;
    final List<DocumentByPerson> documentByPersonArrayList = new ArrayList<>();

    public DependentsListAdapter(List<DependentListItem> list, Context context) {
        this.list = list;
        this.context = context;
        selected = new SparseArray<>();
    }

    public DependentsListAdapter(List<DependentListItem> list, Context context, FragmentManager mSuportFragment) {
        this.list = list;
        this.context = context;
        selected = new SparseArray<>();
        this.mSuportFragment = mSuportFragment;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ROW) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.toy_dependents_list_row,
                    parent, false);
            return new RowHolder(itemView);
        }
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.toy_dependents_list_header,
                    parent, false);
            return new HeaderHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final DependentListItem item = list.get(position);
        if (item instanceof DependentListRow) {

            RowHolder rowHolder = (RowHolder) holder;
            DependentListRow dependentListRow = (DependentListRow) item;

            rowHolder.initRow(dependentListRow, position);

        } else if (item instanceof DependentListHeader) {
            HeaderHolder headerHolder = (HeaderHolder) holder;
            DependentListHeader dependentListHeader = (DependentListHeader) item;
            headerHolder.init(dependentListHeader.getObservation());
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof DependentListHeader) {
            return HEADER;
        } else {
            return ROW;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onDelete(final String mPath, final String mCpf) {
        ((RequestNewToyActivity)context).setLoading(true);
        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        DocumentDeleteRequest request = new DocumentDeleteRequest(mCpf, mPath);
        Call<CommitResponse> call = mAPIService.deleteDocument(request);
        call.enqueue(new Callback<CommitResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommitResponse> call, @NonNull Response<CommitResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                ((RequestNewToyActivity) context).setLoading(false);
                if (response.isSuccessful()) {
                    removeDocumentFromView(mCpf, mPath);
                } else {
                    ((RequestNewToyActivity) context).showSnackBar(context.getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<CommitResponse> call, Throwable t) {
                ((RequestNewToyActivity)context).setLoading(false);
                if (t instanceof SocketTimeoutException)
                    ((RequestNewToyActivity) context).showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG362", "string", context.getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    ((RequestNewToyActivity) context).showSnackBar(context.getResources().getString(context.getResources().getIdentifier("MSG361", "string", context.getPackageName())), TYPE_FAILURE);
                else
                    ((RequestNewToyActivity) context).showSnackBar(t.getMessage(), TYPE_FAILURE);
            }

        });
    }

    private void removeDocumentFromView(String mCpf, String mPath) {
        if (documentByPersonArrayList.size() > 0) {
            for (DocumentByPerson array : documentByPersonArrayList) {
                if (array.getCPF().equals(mCpf)) {
                    Documents docToRemove = null;
                    for (DocumentType type : array.getDocumentsType()) {
                        for (Documents doc : type.documents) {
                            if (doc.path.equals(mPath))
                                docToRemove = doc;
                        }

                        if (docToRemove != null) {
                            type.documents.remove(docToRemove);

                            if (type.documents.size() == 0)
                                type.userHasIt = false;
                            break;
                        }
                    }
                    showDocumentTypeData(array.getDocumentsType(), array.getmDocumentTypesRecyclerView(), array.getCPF());
                    break;
                }
            }
        }
    }

    private class RowHolder extends RecyclerView.ViewHolder {

        private TextView dependentName;
        private ViewGroup layoutShowHideClick;
        private ViewGroup layoutShowHide;
        private ImageView arrow;
        private View itemView;
        private ViewGroup descriptionLayout;
        private TextView statusEligible;
        private CheckBox checkBoxSelected;
        private EditText edtJustification;
        private TextView labelDependent;
        private RecyclerView documentRecycler;


        private RowHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            dependentName = itemView.findViewById(R.id.dependent_name);
            layoutShowHideClick = itemView.findViewById(R.id.layout_show_hide_click);
            layoutShowHide = itemView.findViewById(R.id.layout_show_hide);
            arrow = itemView.findViewById(R.id.arrow_ic);
            descriptionLayout = itemView.findViewById(R.id.description_layout);
            statusEligible = itemView.findViewById(R.id.status_eligible);
            checkBoxSelected = itemView.findViewById(R.id.checkboxSelect);
            edtJustification = itemView.findViewById(R.id.edt_justification);
            labelDependent = itemView.findViewById(R.id.labelDependent);
            documentRecycler = itemView.findViewById(R.id.recyclerViewDocumentTypes);
        }

        private void initRow(final DependentListRow dependentListRow, final int finalPos) {

            if (dependentListRow != null) {
                final DependentResponseData data = dependentListRow.getData();
                dependentName.setText(data.getName());

                if (data.isCanRequest() != null && !data.isCanRequest()) {

                    statusEligible.setText(data.getMessage());
                    layoutShowHideClick.setVisibility(View.GONE);
                    statusEligible.setVisibility(View.VISIBLE);
                    checkBoxSelected.setClickable(false);
                    dependentName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.grey_disabled_text));
                    labelDependent.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.grey_text));

                } else {

                    dependentName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.blueHeader));
                    labelDependent.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.grey_dark));
                    checkBoxSelected.setClickable(true);
                    statusEligible.setVisibility(View.GONE);

                    layoutShowHideClick.setVisibility(View.VISIBLE);
                    layoutShowHide.setVisibility(data.isExpanded() ? View.VISIBLE : View.GONE);

                    if (data.isExpanded()) {
                        arrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_up));
                    } else {
                        arrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
                    }

                    final View.OnClickListener showHideClick = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean expanded = data.isExpanded();
                            data.setExpanded(!expanded);
                            notifyItemChanged(finalPos);
                        }
                    };

                    checkBoxSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked) {
                                selected.put(finalPos, dependentListRow);
                                if(!data.isExpanded()) {
                                    showHideClick.onClick(null);
                                    edtJustification.requestFocus();
                                    Utils.showKeyboard(context);
                                }
                            } else {
                                selected.remove(finalPos);
                            }

                        }
                    });

                    layoutShowHideClick.setOnClickListener(showHideClick);

                    dependentListRow.setJustificationEdt(edtJustification);
                    edtJustification.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            dependentListRow.setJustification(s.toString());
                        }
                    });
                    if(data.isExpanded())
                        getDocuments(data.getCpf(), documentRecycler);
                }
            } else {
                itemView.setVisibility(View.GONE);
            }

        }
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {

        private ViewGroup rulesLayout;
        private View itemView;
        private String observations;

        private HeaderHolder(final View itemView) {
            super(itemView);
            this.itemView = itemView;

            rulesLayout = itemView.findViewById(R.id.layout_rules);
            rulesLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showSimpleDialog(itemView.getContext().getString(R.string.information), observations, null, itemView.getContext(), null);
                }
            });
        }

        private void init(String observations) {
            this.observations = observations;
        }
    }

    public SparseArray<DependentListRow> getSelected() {
        return selected;
    }

    public List<DocumentByPerson> getDocuments() {
        return documentByPersonArrayList;
    }

    public boolean validateSelected() {
        int count = 0;
        for(int i = 0; i < selected.size(); i++) {
            int key = selected.keyAt(i);


            // get the object by the key.
            DependentListRow row = selected.get(key);
            if(TextUtils.isEmpty(row.getJustification())) {
                row.getJustificationEdt().setError("Campo obrigatório");
                count++;
                if(!row.getData().isExpanded()) {
                    row.getData().setExpanded(true);
                    notifyItemChanged(key);
                }
            } else {
                row.getJustificationEdt().setError(null);
            }

        }

        return count == 0;
    }

    //METODO DE RETORNO DO UPLOADDIALOGFRAGMENT, APÓS CHAMADA ENDPOINT DE UPLOAD
    public void uploadDocumentUpdate(UploadResponse resultUpload, int idType, DocumentType type, final String name, String mCpf, String idDocument) {
        if (resultUpload.commited) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentDateandTime = formatter.format(new Date());
            int indexFound = -1;

            if (documentByPersonArrayList.size() > 0) {
                for (DocumentByPerson array : documentByPersonArrayList) {
                    if (array.getCPF().equals(mCpf)) {
                        for (int i = 0; i < array.getDocumentsType().size(); i++) {
                            if (array.getDocumentsType().get(i).id == idType)
                                indexFound = i;
                        }


                        if (indexFound != -1) {

                            type = array.getDocumentsType().get(indexFound);
                            type.documents.add(new Documents(resultUpload.path, currentDateandTime, name, true, idDocument));

                            if (!array.getDocumentsType().get(indexFound).userHasIt)
                                array.getDocumentsType().get(indexFound).userHasIt = true;
                            array.getDocumentsType().get(indexFound).documents = type.documents;
                        } else {

                            type.documents = new ArrayList<>();
                            type.documents.add(new Documents(resultUpload.path, currentDateandTime, name, true, idDocument));

                            if (!type.userHasIt)
                                type.userHasIt = true;
                            array.getDocumentsType().add(type);
                        }

                        showDocumentTypeData(array.getDocumentsType(), array.getmDocumentTypesRecyclerView(), array.getCPF());
                        break;
                    }
                }
            } else {
                ((RequestNewToyActivity) context).showSnackBar(resultUpload.messageIdentifier, TYPE_FAILURE);
            }
        }
    }

    private void getDocuments(final String cpf, final RecyclerView documentRecycler) {
        ((RequestNewToyActivity)context).setLoading(true);

        int idBehavior = SystemBehavior.BehaviorEnum.INCL_DEP_ESP_TOY.id();
        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);

        int planId = -1;
        if(list != null && list.size()>1 && list.get(1) instanceof  DependentListRow)
            planId = Integer.parseInt (((DependentListRow)list.get(1)).getData().getIdPlan());

        Call<Document> call = mAPIService.queryDocumentTypeGeneric(new QueryDocumentTypeGenericBody(cpf, idBehavior, false, 0, false, planId));
        call.enqueue(new Callback<Document>() {
            @Override
            public void onResponse(@NonNull Call<Document> call, @NonNull Response<Document> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                Document documentResponse = response.body();
                if (response.isSuccessful() && documentResponse != null) {

                    ArrayList<DocumentType> listDocumentTypes = (ArrayList<DocumentType>)documentResponse.documentTypes;

                    DocumentByPerson documentByPerson = new DocumentByPerson(listDocumentTypes, cpf, documentRecycler);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    documentRecycler.setHasFixedSize(true);
                    documentRecycler.setLayoutManager(layoutManager);
                    documentRecycler.setNestedScrollingEnabled(false);

                    documentByPersonArrayList.add(documentByPerson);
                    showDocumentTypeData(listDocumentTypes, documentRecycler, cpf);
                    ((RequestNewToyActivity)context).setLoading(false);

                } else {
                    ((RequestNewToyActivity) context).setLoading(false);
                    LogUtils.info("DocumentsActivity", "Not found");
                    ((RequestNewToyActivity) context).showSnackBar(context.getString(R.string.problemServer), Constants.TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Document> call, @NonNull Throwable t) {
                ((RequestNewToyActivity) context).setLoading(false);
                LogUtils.error("DocumentsActivity", "Error: " + t.getMessage());
                if (t instanceof SocketTimeoutException)
                    ((RequestNewToyActivity) context).showSnackBar(context.getString(context.getResources().getIdentifier("MSG362", "string", context.getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    ((RequestNewToyActivity) context).showSnackBar(context.getString(context.getResources().getIdentifier("MSG361", "string", context.getPackageName())), TYPE_FAILURE);
                else
                    ((RequestNewToyActivity) context).showSnackBar(t.getMessage(), TYPE_FAILURE);
            }
        });
    }

    public void showDocumentTypeData(ArrayList<DocumentType> documentTypesArrayList, RecyclerView mDocumentTypesRecyclerView, String mCpf) {
        if(context!=null) {
            ListDocumentTypeAdapter documentTypeAdapter = new ListDocumentTypeAdapter(context, documentTypesArrayList, mCpf, mSuportFragment, this,null);
            mDocumentTypesRecyclerView.setAdapter(documentTypeAdapter);
            mDocumentTypesRecyclerView.setAlpha(1);
        }
    }
}
