package br.com.avanade.fahz.activities.benefits.healthplan;

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
import br.com.avanade.fahz.fragments.benefits.ExtractUsageFragment;
import br.com.avanade.fahz.fragments.benefits.healthplan.ChangeOperatorFragment;
import br.com.avanade.fahz.fragments.benefits.healthplan.RequestNewCardFragment;
import br.com.avanade.fahz.fragments.benefits.healthplan.medicalrecord.SearchMedicalRecordFragment;
import br.com.avanade.fahz.fragments.benefits.healthplan.pregnant.PregnantFragment;
import br.com.avanade.fahz.fragments.benefits.healthplan.prontmed.FindDoctorsFragment;
import br.com.avanade.fahz.fragments.benefits.healthplan.prontmed.ProntmedScheduleFragment;
import br.com.avanade.fahz.fragments.benefits.healthplan.prontmed.SearchTimeFragment;
import br.com.avanade.fahz.fragments.benefits.healthplan.telehealth.TeleHealthFragment;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class BaseHealthControlActivity extends NavDrawerBaseActivity {

    public void setLoading(boolean loading, String text) {
        if (loading) {
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else
            mProgressDialog.dismiss();
    }

    boolean isFirstFragment;

    @BindView(R.id.base_health_manager_container)
    RelativeLayout mBaseManagerContainer;

    BaseHealthFragment baseHealthFragment;
    private ProgressDialog mProgressDialog;
    private Snackbar snackbar;


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
        setContentView(R.layout.activity_base_health_control);
        ButterKnife.bind(this);

        setImageHeaderVisibility(true);
        setMenuVisible(false);
        mProgressDialog = new ProgressDialog(this);

        isFirstFragment = true;
        baseHealthFragment = (BaseHealthFragment) getIntent().getSerializableExtra(Constants.BASE_HEALTH_CONTROL);
        setFragment(baseHealthFragment);

        setupUi();
    }

    //METODO PARA CONFIRUAÇÂO DE TELA
    private void setupUi() {
        toolbarTitle.setText(getString(R.string.label_personal_data));
        setImageHeaderVisibility(false);
    }

    public void setFragment(BaseHealthFragment type) {
        cancelSnackBar();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (type) {
            case LISTDEPENDENT:
                DependentBenefitAdhesionFragment dependentAdhesion = new DependentBenefitAdhesionFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BENEFIT_EXTRA, Constants.BENEFITHEALTH);
                dependentAdhesion.setArguments(bundle);
                ft.replace(R.id.baseHealthContainer, dependentAdhesion, BaseHealthFragment.LISTDEPENDENT.toString());
                break;
            case CANCELPLANHOLDER:
                CancelBenefitHolderFragment cancelHolder = new CancelBenefitHolderFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.BENEFIT_EXTRA, Constants.BENEFITHEALTH);
                bundle.putString(Constants.BENEFIT_DESCRIPTION_EXTRA, Constants.BENEFITHEALTH_DESC);
                cancelHolder.setArguments(bundle);
                ft.replace(R.id.baseHealthContainer, cancelHolder, BaseHealthFragment.CANCELPLANHOLDER.toString());
                break;
            case LISTDEPENDENTCANCEL:
                DependentBenefitCancelFragment dependentCancel = new DependentBenefitCancelFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.BENEFIT_EXTRA, Constants.BENEFITHEALTH);
                dependentCancel.setArguments(bundle);
                ft.replace(R.id.baseHealthContainer, dependentCancel, BaseHealthFragment.LISTDEPENDENTCANCEL.toString());
                break;
            case PLANCARDS:
                CardsPlanFragment cardsPlanF = new CardsPlanFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.BENEFIT_EXTRA, Constants.BENEFITHEALTH);
                bundle.putString(Constants.BENEFIT_DESCRIPTION_EXTRA, Constants.BENEFITHEALTH_DESC);
                cardsPlanF.setArguments(bundle);
                ft.replace(R.id.baseHealthContainer, cardsPlanF, BaseHealthFragment.PLANCARDS.toString());
                break;
            case CHANGEOPERATOR:
                ChangeOperatorFragment sFragment = ChangeOperatorFragment.newInstance(true, Constants.BENEFITHEALTH);
                ft.replace(R.id.baseHealthContainer, sFragment, AdhesionHealthControlActivity.AdhesionHealthFragment.ADEHESION.toString());
                break;
            case EXTRACTOFUSAGE:
                ExtractUsageFragment extractF = new ExtractUsageFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.BENEFIT_EXTRA, Constants.BENEFITHEALTH);
                extractF.setArguments(bundle);
                ft.replace(R.id.baseHealthContainer, extractF, BaseHealthFragment.EXTRACTOFUSAGE.toString());
                break;
            case REQUESTNEWCARD:
                RequestNewCardFragment requestCard = new RequestNewCardFragment();
                bundle = new Bundle();
                requestCard.setArguments(bundle);
                ft.replace(R.id.baseHealthContainer, requestCard, BaseHealthFragment.REQUESTNEWCARD.toString());
                break;
            case MYSCHEDULE:
                ft.replace(R.id.baseHealthContainer, new ProntmedScheduleFragment(), BaseHealthFragment.MYSCHEDULE.toString());
                break;
            case FINDDOCTORS:
                ft.replace(R.id.baseHealthContainer, new FindDoctorsFragment(), BaseHealthFragment.FINDDOCTORS.toString());
                break;
            case SEARCHTIME:
                ft.replace(R.id.baseHealthContainer, new SearchTimeFragment(), BaseHealthFragment.SEARCHTIME.toString());
                break;
            case TELEHEALTH:
                TeleHealthFragment teleHealthFragment = new TeleHealthFragment();
                bundle = new Bundle();
                teleHealthFragment.setArguments(bundle);
                ft.replace(R.id.baseHealthContainer, teleHealthFragment, BaseHealthFragment.TELEHEALTH.toString());
                break;
            case PROGRAMPREGNANT:
                PregnantFragment pregnantFragment = new PregnantFragment();
                bundle = new Bundle();
                pregnantFragment.setArguments(bundle);
                ft.replace(R.id.baseHealthContainer, pregnantFragment, BaseHealthFragment.PROGRAMPREGNANT.toString());
                break;
            case MEDICALRECORD:
                SearchMedicalRecordFragment medicalRecordFragment = new SearchMedicalRecordFragment();
                bundle = new Bundle();
                medicalRecordFragment.setArguments(bundle);
                ft.replace(R.id.baseHealthContainer, medicalRecordFragment, BaseHealthFragment.MEDICALRECORD.toString());
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

    public void setFragment(BaseHealthFragment type, Bundle bundle) {
        cancelSnackBar();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (type == BaseHealthFragment.SEARCHTIME) {
            SearchTimeFragment fragment = new SearchTimeFragment();
            if (bundle != null && !bundle.isEmpty()) {
                fragment.setArguments(bundle);
            }
            ft.replace(R.id.baseHealthContainer, fragment, BaseHealthFragment.SEARCHTIME.toString());
        } else {
            showSnackBar(getString(R.string.NoFragmentFoud), TYPE_FAILURE);
        }

        if (isFirstFragment) {
            isFirstFragment = false;
        } else {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void showSnackBar(String message, int typeApproval) {
        showSnackBar(message, typeApproval, BaseTransientBottomBar.LENGTH_LONG);
    }

    public void showSnackBar(String message, int typeApproval, int duration) {
        snackbar = Snackbar.make(mBaseManagerContainer, message, Snackbar.LENGTH_LONG).setDuration(duration);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }


    public void cancelSnackBar() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    public enum BaseHealthFragment {
        LISTDEPENDENT,
        CANCELPLANHOLDER,
        LISTDEPENDENTCANCEL,
        PLANCARDS,
        CHANGEOPERATOR,
        REQUESTNEWCARD,
        EXTRACTOFUSAGE,
        MYSCHEDULE,
        FINDDOCTORS,
        SEARCHTIME,
        PROGRAMPREGNANT,
        TELEHEALTH,
        MEDICALRECORD
    }
}
