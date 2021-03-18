package br.com.avanade.fahz.activities.benefits.healthplan.medicalrecord;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.benefits.healthplan.medicalrecord.MedicalRecords;
import br.com.avanade.fahz.model.lgpdModel.MedicalRecordsDetails;
import br.com.avanade.fahz.model.response.SmallLife;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class MedicalRecordDetailsActivity extends NavDrawerBaseActivity {

    private ProgressDialog mProgressDialog;

    @BindView(R.id.ConstraintLayout)
    ConstraintLayout mConstraintLayout;

    @BindView(R.id.txtConsultation)
    TextView txtConsultation;

    @BindView(R.id.txtDoctor)
    TextView txtDoctor;

    @BindView(R.id.txtCareProvider)
    TextView txtCareProvider;

    @BindView(R.id.txtMainComplaints)
    TextView txtMainComplaints;

    private MedicalRecords medicalRecord;
    private SmallLife smallLife;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record_details);
        toolbarTitle.setText(R.string.medical_records_details);

        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);

        medicalRecord = (MedicalRecords) getIntent().getSerializableExtra("medicalRecord");
        smallLife = (SmallLife) getIntent().getSerializableExtra("smallLife");

        if (medicalRecord != null) {
            setupUi();
            loadList();
        }
    }

    private void setupUi() {
        txtConsultation.setText(medicalRecord.consultation);
        txtDoctor.setText(medicalRecord.doctor);
        txtCareProvider.setText(medicalRecord.careProvider);
    }

    private void loadList() {

        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        Call<JsonElement> call = mAPIService.getConsultationsPerformedDetails(new MedicalRecordsDetails(smallLife.getCpf(), medicalRecord.id));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    JsonElement value = response.body().getAsJsonObject().get("commited");

                    if (value.getAsBoolean()) {

                        JsonElement json = response.body().getAsJsonObject().get("Data");

                        JsonArray jsonMainComplaints = ((JsonObject) json).getAsJsonArray("MainComplaints");
                        JsonArray jsonDiagnostics = ((JsonObject) json).getAsJsonArray("Diagnostics");
                        JsonArray jsonTreatmentInProgress = ((JsonObject) json).getAsJsonArray("TreatmentInProgress");

                        StringBuilder sb = new StringBuilder();
                        sb.append("<b>Queixa Principal</b>");
                        sb.append("<br>");
                        for (int j = 0; j < jsonMainComplaints.size(); j++){
                            sb.append(jsonMainComplaints.get(j).getAsJsonObject().get("MainComplaint").getAsString());
                            sb.append("<br><br>");
                        }

                        sb.append("<b>Diagnóstico</b>");
                        sb.append("<br>");
                        for (int j = 0; j < jsonDiagnostics.size(); j++){
                            sb.append(jsonDiagnostics.get(j).getAsJsonObject().get("Diagnosis").getAsString());
                            sb.append("<br><br>");
                        }

                        sb.append("<b>Tratamento em Andamento</b>");
                        sb.append("<br>");
                        for (int j = 0; j < jsonTreatmentInProgress.size(); j++){
                            sb.append(jsonTreatmentInProgress.get(j).getAsJsonObject().get("Name").getAsString());
                            sb.append("<br><br>");
                        }

                        txtMainComplaints.setText(Html.fromHtml(sb.toString()));

                    } else {

                        showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);

                    }

                } else {
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        showSnackBar(message, Constants.TYPE_FAILURE);
                    } catch (Exception ex) {
                        showSnackBar(ex.getMessage(), Constants.TYPE_FAILURE);
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                setLoading(false);
                if (getBaseContext() != null) {
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getBaseContext().getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getBaseContext().getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            }
        });
    }

    private void setLoading(boolean loading) {
        if (loading) {
            mProgressDialog.setMessage(getResources().getString(R.string.wait_moment));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else
            mProgressDialog.dismiss();
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mConstraintLayout, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }
}