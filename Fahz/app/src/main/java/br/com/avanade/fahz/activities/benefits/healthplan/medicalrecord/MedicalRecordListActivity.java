package br.com.avanade.fahz.activities.benefits.healthplan.medicalrecord;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.Adapter.healthplan.CardViewMedicalRecordsAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.benefits.healthplan.medicalrecord.ConsultationsPerformed;
import br.com.avanade.fahz.model.benefits.healthplan.medicalrecord.MedicalRecords;
import br.com.avanade.fahz.model.response.SmallLife;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class MedicalRecordListActivity extends NavDrawerBaseActivity {

    private ProgressDialog mProgressDialog;

    @BindView(R.id.ConstraintLayout)
    ConstraintLayout mConstraintLayout;

    @BindView(R.id.txtName)
    TextView mTextName;

    @BindView(R.id.txtAge)
    TextView mTextAge;

    @BindView(R.id.imageViewUser)
    ImageView imgUser;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    CardViewMedicalRecordsAdapter cardViewMedicalRecordsAdapter;

    private List<MedicalRecords> medicalRecordsList = new ArrayList<>();
    SmallLife smallLife;
    boolean cansearch;
    RoundedBitmapDrawable imageDrawable;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record_list);
        toolbarTitle.setText(R.string.medical_records);

        mContext = this;
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        smallLife = (SmallLife) getIntent().getSerializableExtra("smallLife");

        if (smallLife != null) {
            setupUi();
            loadData();
        }
    }

    private void setupUi() {

        SessionManager manager = new SessionManager(this);

        if (manager.getToken() == null || manager.getToken().isEmpty()) {
            manager.logout();
        }

        cansearch = Boolean.parseBoolean(manager.getPreference(Constants.CAN_SEARCH_PICTURE));
        //ATUALIZACAO DE IMAGEM COM A JA SALVA
        if (!cansearch) {
            String input = manager.getPreference(Constants.BITMAP_PROFILE);
            byte[] decodedByte = Base64.decode(input, 0);
            Bitmap image = BitmapFactory
                    .decodeByteArray(decodedByte, 0, decodedByte.length);

            if (image != null) {
                imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), image);
                imageDrawable.setCircular(true);
                imageDrawable.setCornerRadius(Math.min(image.getWidth(), image.getHeight()) / 2.0f);

                imgUser.invalidate();
                imgUser.setImageDrawable(null);
                imgUser.setImageURI(null);
                imgUser.setImageDrawable(imageDrawable);
            }
        }

        if(smallLife.getName().length() > 25)
            mTextName.setText(Utils.convertName(smallLife.getName()));
        else
            mTextName.setText(smallLife.getName());

        try {
            String dt = DateEditText.parseToDateRequest(smallLife.getDateOfBirth());
            Integer age = DateEditText.getAge(dt);
            String gender = smallLife.getGender().contains("M") ? "Masculino":"Feminino";
            StringBuilder sb = new StringBuilder();
            sb.append("Idade: ");
            sb.append(age.toString());
            sb.append(" - Sexo: ");
            sb.append(gender);
            mTextAge.setText(sb.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mConstraintLayout, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void loadData() {

        setLoading(true, getString(R.string.loading_searching));

        //Verifica se pode desabilitar o beneficio
        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        Call<ConsultationsPerformed> call = mAPIService.getListOfConsultationsPerformed(new CPFInBody(smallLife.getCpf()));
        call.enqueue(new Callback<ConsultationsPerformed>() {
            @Override
            public void onResponse(@NonNull Call<ConsultationsPerformed> call, @NonNull Response<ConsultationsPerformed> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    ConsultationsPerformed consultationsPerformed = response.body();
                    if (consultationsPerformed.commited) {

                        medicalRecordsList = consultationsPerformed.data;
                        if (medicalRecordsList.size() > 0) {
                            cardViewMedicalRecordsAdapter = new CardViewMedicalRecordsAdapter(MedicalRecordListActivity.this, medicalRecordsList, smallLife);
                            mRecyclerView.setAdapter(cardViewMedicalRecordsAdapter);
                        }else{

                            Utils.showSimpleDialog(getString(R.string.dialog_title), getString(R.string.no_found_medical_record), getString(R.string.close), mContext, null);

                        }
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
                setLoading(false, "");
            }

            @Override
            public void onFailure(Call<ConsultationsPerformed> call, Throwable t) {
                setLoading(false, "");
                if (mConstraintLayout != null) {
                    if (t instanceof SocketTimeoutException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", mContext.getPackageName())), TYPE_FAILURE);
                    else if (t instanceof UnknownHostException)
                        showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", mContext.getPackageName())), TYPE_FAILURE);
                    else
                        showSnackBar(t.getMessage(), TYPE_FAILURE);
                }
            }
        });
    }

    private void setLoading(Boolean loading, String text) {
        if (loading) {
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }
}
