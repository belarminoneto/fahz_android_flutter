package br.com.avanade.fahz.activities.benefits.schoolsupplies;

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
import br.com.avanade.fahz.fragments.benefits.schoolsupplies.RequestCardFragment;
import br.com.avanade.fahz.fragments.benefits.schoolsupplies.RequestRefundFragment;
import br.com.avanade.fahz.fragments.benefits.schoolsupplies.SendReceiptFragment;
import br.com.avanade.fahz.model.DocumentType;
import br.com.avanade.fahz.model.UploadResponse;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class BaseSchoolSuppliesControlActivity extends NavDrawerBaseActivity {

    public enum BaseSchoolFragment {
        REQUESTCARD,
        REQUESTREFUND,
        SENDRECEIPT
    }

    boolean isFirstFragment;

    @BindView(R.id.base_school_supplies_manager_container)
    RelativeLayout mBaseManagerContainer;

    RequestCardFragment fragmentCard;
    RequestRefundFragment fragmentRefund;
    SendReceiptFragment fragmentReceipt;

    BaseSchoolFragment baseSchoolFragment;

    private ProgressDialog mProgressDialog;
    private int idPlan;


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
        setContentView(R.layout.activity_base_school_supplies_control);
        ButterKnife.bind(this);

        setImageHeaderVisibility(true);
        setMenuVisible(false);
        mProgressDialog = new ProgressDialog(this);
        SessionManager sessionManager = new SessionManager(getApplicationContext());

        isFirstFragment = true;
        baseSchoolFragment = (BaseSchoolFragment) getIntent().getSerializableExtra(Constants.BASE_SCHOOL_CONTROL);
        idPlan = Integer.valueOf(getIntent().getStringExtra(Constants.IDPLAN_SCHOOL_SUPPLIES));
        setFragment(baseSchoolFragment);

        setupUi();
    }

    //METODO PARA CONFIRUAÇÂO DE TELA
    private void setupUi() {

        toolbarTitle.setText(getString(R.string.label_personal_data));
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
            case REQUESTCARD:
                fragmentCard = new RequestCardFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.PLAN_ID, idPlan);
                fragmentCard.setArguments(bundle);
                ft.replace(R.id.baseSchoolSuppliesContainer, fragmentCard, BaseSchoolFragment.REQUESTCARD.toString());
                break;
            case REQUESTREFUND:
                fragmentRefund = new RequestRefundFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.PLAN_ID, idPlan);
                fragmentRefund.setArguments(bundle);
                ft.replace(R.id.baseSchoolSuppliesContainer, fragmentRefund, BaseSchoolFragment.REQUESTREFUND.toString());
                break;
            case SENDRECEIPT:
                fragmentReceipt = new SendReceiptFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.PLAN_ID, idPlan);
                fragmentReceipt.setArguments(bundle);
                ft.replace(R.id.baseSchoolSuppliesContainer, fragmentReceipt, BaseSchoolFragment.SENDRECEIPT.toString());
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
                getSupportFragmentManager().findFragmentById(R.id.baseSchoolSuppliesContainer);
        if (f instanceof RequestRefundFragment) {
            fragmentRefund.uploadDocumentUpdate(resultUpload, idType, type, name, cpf, idDocument);
        } else if (f instanceof SendReceiptFragment) {
            fragmentReceipt.uploadDocumentUpdate(resultUpload, idType, type, name, cpf, idDocument);
        } else if (f instanceof RequestCardFragment) {
            fragmentCard.uploadDocumentUpdate(resultUpload, idType, type, name, cpf, idDocument);
        }
    }
}
