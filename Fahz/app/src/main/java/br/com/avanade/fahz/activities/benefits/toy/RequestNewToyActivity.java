package br.com.avanade.fahz.activities.benefits.toy;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.activities.benefits.toy.list.DependentListHeader;
import br.com.avanade.fahz.activities.benefits.toy.list.DependentListItem;
import br.com.avanade.fahz.activities.benefits.toy.list.DependentListRow;
import br.com.avanade.fahz.activities.benefits.toy.list.DependentsListAdapter;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.DocumentByPerson;
import br.com.avanade.fahz.model.DocumentType;
import br.com.avanade.fahz.model.Documents;
import br.com.avanade.fahz.model.IncludeBenefitToyModel;
import br.com.avanade.fahz.model.IncludeBenefitToyResponse;
import br.com.avanade.fahz.model.UploadResponse;
import br.com.avanade.fahz.model.response.DependentResponseData;
import br.com.avanade.fahz.model.response.ListDependentsResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class RequestNewToyActivity extends NavDrawerBaseActivity{

    private RecyclerView mDependentRecyclerView;
    private DependentsListAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private APIService mApiService;
    private ViewGroup mRootView;
    private List<DependentListItem> list;
    ArrayList<DocumentType> documentTypeArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_request_new_toy_acitivity);

        mDependentRecyclerView = findViewById(R.id.mDependentRecyclerView);

        mApiService = ServiceGenerator.createService(APIService.class);
        configureBtnSave();
        setImageHeaderVisibility(false);
        setMenuVisible(false);

        toolbarTitle.setText(getString(R.string.toy_request));

        mProgressDialog = new ProgressDialog(this);

        mRootView = findViewById(R.id.content);

        getDependentsList();

    }

    private void getDependentsList() {
        list = new ArrayList<>();

        list.add(new DependentListHeader(getIntent().getStringExtra("observation")));
        setLoading(true);
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        Call<ListDependentsResponse> call = mApiService.listDependents(new CPFInBody(cpf));
        call.enqueue(new Callback<ListDependentsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListDependentsResponse> call, @NonNull Response<ListDependentsResponse> response) {
                ListDependentsResponse body = response.body();
                if (body != null) {
                    List<DependentResponseData> dependents = body.getRegisters();
                    for (DependentResponseData dependent : dependents) {
                        list.add(new DependentListRow(dependent));
                    }
                }
                initList(list);
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<ListDependentsResponse> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                else if (t instanceof UnknownHostException)
                    showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                else
                    showSnackBar(t.getMessage(), TYPE_FAILURE);
                setLoading(false);
            }
        });
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackBarView = snackbar.getView();
        TextView textView = snackBarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }


    private void initList(List<DependentListItem> list) {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ((SimpleItemAnimator) mDependentRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mDependentRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new DependentsListAdapter(list, this, getSupportFragmentManager());
        mDependentRecyclerView.setAdapter(mAdapter);
        mDependentRecyclerView.setHasFixedSize(true);
    }

    private void configureBtnSave() {
        final Context context = this;
        Button mBtnSave = findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.validateSelected()) {
                    final SparseArray<DependentListRow> selected = mAdapter.getSelected();
                    if (selected != null && selected.size() > 0) {
                        View.OnClickListener onClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callIncludeBenefitToyEndpoint(selected);
                            }
                        };
                        Utils.showQuestionDialog(getString(R.string.dialog_title), getString(R.string.confirm_operation), context, onClickListener);
                    } else {
                        showSnackBar("Selecione pelo menos 1 dependente", TYPE_FAILURE);
                    }
                } else {
                    showSnackBar( "Preencha os campos obrigatórios", TYPE_FAILURE);
                }
            }
        });
    }

    private void callIncludeBenefitToyEndpoint(SparseArray<DependentListRow> selected) {
        if (selected != null) {
            List<IncludeBenefitToyModel> requestBody = new ArrayList<>();
            List<DocumentByPerson> documents = mAdapter.getDocuments();

            for (int i = 0; i < selected.size(); i++) {
                int key = selected.keyAt(i);

                DependentListRow dependent = selected.get(key);
                DependentResponseData data = dependent.getData();
                IncludeBenefitToyModel model = new IncludeBenefitToyModel(data.getCpf(), dependent.getJustification());

                model.setDocuments(new ArrayList<String>());

                for (DocumentByPerson item : documents) {
                    if(item.getCPF().equals(data.getCpf())) {
                        for (DocumentType docList : item.getDocumentsType()) {
                            for (Documents doc : docList.documents) {
                                model.getDocuments().add(doc.Id);
                            }
                        }
                    }
                }
                requestBody.add(model);
            }

            Gson gson = new Gson();
            String jsonInString = gson.toJson(requestBody);

            if (requestBody.size() > 0) {
                setLoading(true);
                Call<IncludeBenefitToyResponse> call = mApiService.includeBenefitToyDocument(requestBody);
                call.enqueue(new Callback<IncludeBenefitToyResponse>() {
                    @Override
                    public void onResponse(Call<IncludeBenefitToyResponse> call, Response<IncludeBenefitToyResponse> response) {
                        IncludeBenefitToyResponse body = response.body();
                        if (body != null) {
                            if (body.getCommited()) {
                                int resID = getResources().getIdentifier("MSG184", "string", getPackageName());
                                String message = getResources().getString(resID);
                                Utils.showSimpleDialogGoBack(getResources().getString(R.string.dialog_title), message, null, RequestNewToyActivity.this, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                });
                            } else {
                                Utils.showSimpleDialog(getResources().getString(R.string.dialog_title), body.getMessageIdentifier(), null, RequestNewToyActivity.this, null);
                            }
                        }
                        setLoading(false);
                    }

                    @Override
                    public void onFailure(Call<IncludeBenefitToyResponse> call, Throwable t) {
                        if (t instanceof SocketTimeoutException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), TYPE_FAILURE);
                        else
                            showSnackBar(t.getMessage(), TYPE_FAILURE);
                        setLoading(false);
                    }
                });
            }
        }

    }

    //METODO DE RETORNO DO UPLOADDIALOGFRAGMENT, APÓS CHAMADA ENDPOINT DE UPLOAD
    public void uploadDocumentUpdate(UploadResponse resultUpload, int idType, DocumentType type, final String name, String cpf, String idDocument) {

        mAdapter.uploadDocumentUpdate(resultUpload,idType,type,name,cpf,idDocument);
    }

    @Override
    protected void onPause() {
        Utils.closeKeyboard(this);
        super.onPause();
    }

    public void setLoading(Boolean loading) {
        if (loading) {
            mProgressDialog.setMessage("Aguarde um momento");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

}
