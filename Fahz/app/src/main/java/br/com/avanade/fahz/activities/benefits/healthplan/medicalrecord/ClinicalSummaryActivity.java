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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.avanade.fahz.Adapter.healthplan.CardViewClinicalSummaryAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.benefits.healthplan.medicalrecord.MedicalRecords;
import br.com.avanade.fahz.model.response.SmallLife;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class ClinicalSummaryActivity extends NavDrawerBaseActivity {

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

    CardViewClinicalSummaryAdapter cardViewClinicalSummaryAdapter;

    private List<MedicalRecords> medicalRecordsList = new ArrayList<>();
    SmallLife smallLife;
    boolean cansearch;
    RoundedBitmapDrawable imageDrawable;
    private Context mContext;
    private String[] clinicalSummaryOfList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinical_summary_list);
        toolbarTitle.setText(R.string.clinical_summary);

        mContext = this;
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        clinicalSummaryOfList = mContext.getResources().getStringArray(R.array.clinical_summary_array);

        smallLife = (SmallLife) getIntent().getSerializableExtra("smallLife");

        if (smallLife != null) {
            setupUi();
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

        cardViewClinicalSummaryAdapter = new CardViewClinicalSummaryAdapter(ClinicalSummaryActivity.this, Arrays.asList(clinicalSummaryOfList), smallLife);
        mRecyclerView.setAdapter(cardViewClinicalSummaryAdapter);
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
