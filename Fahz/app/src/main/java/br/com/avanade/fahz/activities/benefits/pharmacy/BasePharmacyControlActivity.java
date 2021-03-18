package br.com.avanade.fahz.activities.benefits.pharmacy;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.fragments.benefits.CancelBenefitHolderFragment;
import br.com.avanade.fahz.fragments.benefits.CardsPlanFragment;
import br.com.avanade.fahz.fragments.benefits.DependentBenefitAdhesionFragment;
import br.com.avanade.fahz.fragments.benefits.DependentBenefitCancelFragment;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class BasePharmacyControlActivity extends NavDrawerBaseActivity {

    public enum BasePharmacyFragment {
        LISTDEPENDENT,
        CANCELPLANHOLDER,
        LISTDEPENDENTCANCEL,
        PLANCARDS
    }

    boolean isFirstFragment;

    @BindView(R.id.base_pharmacy_manager_container)
    RelativeLayout mBaseManagerContainer;

    BasePharmacyFragment basePharmacyFragment;

    private final String LOG_KEY = BasePharmacyControlActivity.class.getSimpleName();
    private APIService mAPIService;
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
        setContentView(R.layout.activity_base_pharmacy_control);
        ButterKnife.bind(this);

        setImageHeaderVisibility(true);
        setMenuVisible(false);
        mProgressDialog = new ProgressDialog(this);
        SessionManager sessionManager = new SessionManager(getApplicationContext());

        isFirstFragment = true;
        basePharmacyFragment = (BasePharmacyFragment) getIntent().getSerializableExtra(Constants.BASE_HEALTH_CONTROL);
        setFragment(basePharmacyFragment);

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


    public void setFragment(BasePharmacyFragment type) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (type) {
            case LISTDEPENDENT:
                DependentBenefitAdhesionFragment dependentAdhesion = new DependentBenefitAdhesionFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BENEFIT_EXTRA, Constants.BENEFITPHARMA);
                dependentAdhesion.setArguments(bundle);
                ft.replace(R.id.basePharmacyContainer, dependentAdhesion, BasePharmacyFragment.LISTDEPENDENT.toString());
                break;
            case CANCELPLANHOLDER:
                CancelBenefitHolderFragment cancelHolder = new CancelBenefitHolderFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.BENEFIT_EXTRA, Constants.BENEFITPHARMA);
                bundle.putString(Constants.BENEFIT_DESCRIPTION_EXTRA, Constants.BENEFITPHARMA_DESC);
                cancelHolder.setArguments(bundle);
                ft.replace(R.id.basePharmacyContainer, cancelHolder, BasePharmacyFragment.CANCELPLANHOLDER.toString());
                break;
            case LISTDEPENDENTCANCEL:
                DependentBenefitCancelFragment dependentCancel = new DependentBenefitCancelFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.BENEFIT_EXTRA, Constants.BENEFITPHARMA);
                dependentCancel.setArguments(bundle);
                ft.replace(R.id.basePharmacyContainer, dependentCancel, BasePharmacyFragment.LISTDEPENDENTCANCEL.toString());
                break;
            case PLANCARDS:
                CardsPlanFragment cardsPlanF = new CardsPlanFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.BENEFIT_EXTRA, Constants.BENEFITPHARMA);
                bundle.putString(Constants.BENEFIT_DESCRIPTION_EXTRA, Constants.BENEFITPHARMA_DESC);
                cardsPlanF.setArguments(bundle);
                ft.replace(R.id.basePharmacyContainer, cardsPlanF, BasePharmacyFragment.PLANCARDS.toString());
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
    }
}
