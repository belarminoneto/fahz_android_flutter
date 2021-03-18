package br.com.avanade.fahz.activities.benefits.scholarship;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.fragments.benefits.scholarship.ScholarshipFollowupFragment;
import br.com.avanade.fahz.fragments.benefits.scholarship.SendRefundFragment;
import br.com.avanade.fahz.model.DocumentType;
import br.com.avanade.fahz.model.UploadResponse;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class BaseScholarshipControlActivity extends NavDrawerBaseActivity {

    public enum BaseSchoolFragment {
        REQUESTREFUND,
        FOLLOWUP
    }

    boolean isFirstFragment;

    @BindView(R.id.base_scholarship_manager_container)
    RelativeLayout mBaseManagerContainer;

    SendRefundFragment fragmentRefund;
    ScholarshipFollowupFragment fragmentFollowup;

    BaseSchoolFragment baseSchoolFragment;
    int idSystemBehavior;
    boolean canSendDocument;
    String scholarshipId;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onResume() {
        super.onResume();

        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.getToken() == null || sessionManager.getToken().isEmpty()) {
            sessionManager.logout();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_scholarship_control);
        ButterKnife.bind(this);

        setImageHeaderVisibility(true);
        setMenuVisible(false);
        mProgressDialog = new ProgressDialog(this);
        SessionManager sessionManager = new SessionManager(getApplicationContext());

        isFirstFragment = true;
        baseSchoolFragment = (BaseSchoolFragment) getIntent().getSerializableExtra(Constants.BASE_SCHOOL_CONTROL);
        idSystemBehavior = (int)getIntent().getSerializableExtra(Constants.SCHOLARSHIP_REFUND_DOCUMENT);
        scholarshipId = (String) getIntent().getSerializableExtra(Constants.SCHOLARSHIP_ID);
        canSendDocument = getIntent().getSerializableExtra(Constants.CAN_SEND_DOCUMENT) != null && (boolean) getIntent().getSerializableExtra(Constants.CAN_SEND_DOCUMENT);

        setFragment(baseSchoolFragment);

        setupUi();
    }

    //METODO PARA CONFIRUAÇÂO DE TELA
    private void setupUi() {

        toolbarTitle.setText(getString(R.string.scholarship_refund));
        setImageHeaderVisibility(false);
    }

    private void setLoading(boolean loading, String text) {
        if (loading) {
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else
            mProgressDialog.dismiss();
    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mBaseManagerContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }


    public void setFragment(BaseSchoolFragment type) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (type) {
            case REQUESTREFUND:
                fragmentRefund = new SendRefundFragment();
                toolbarTitle.setText(getString(R.string.scholarship_refund));
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.SCHOLARSHIP_REFUND_DOCUMENT, idSystemBehavior);
                fragmentRefund.setArguments(bundle);
                ft.replace(R.id.baseScholarshipContainer, fragmentRefund, BaseSchoolFragment.REQUESTREFUND.toString());
                break;
            case FOLLOWUP:
                fragmentFollowup = new ScholarshipFollowupFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.PLAN_ID, idSystemBehavior);
                bundle.putString(Constants.SCHOLARSHIP_ID, scholarshipId);
                bundle.putBoolean(Constants.CAN_SEND_DOCUMENT, canSendDocument);
                fragmentFollowup.setArguments(bundle);
                ft.replace(R.id.baseScholarshipContainer, fragmentFollowup, BaseSchoolFragment.FOLLOWUP.toString());
                break;
            default:
                showSnackBar(getString(R.string.NoFragmentFoud), TYPE_FAILURE);
        }

        if (isFirstFragment) {
            isFirstFragment = false;
        } else {
            ft.addToBackStack(null);
        }
        ft.commit();
        fm.executePendingTransactions();
    }

    //METODO DE RETORNO DO UPLOADDIALOGFRAGMENT, APÓS CHAMADA ENDPOINT DE UPLOAD
    public void uploadDocumentUpdate(UploadResponse resultUpload, int idType, DocumentType type, final String name, String cpf, String idDocument) {

        Fragment f =
                getSupportFragmentManager().findFragmentById(R.id.baseScholarshipContainer);
        if (f instanceof SendRefundFragment) {
            fragmentRefund.uploadDocumentUpdate(resultUpload, idType, type, name, cpf,idDocument);
        }
        else if (f instanceof ScholarshipFollowupFragment) {
            fragmentFollowup.uploadDocumentUpdate(resultUpload, idType, type, name, cpf,idDocument);
        }
    }
}
