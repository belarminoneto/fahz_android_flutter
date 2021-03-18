package br.com.avanade.fahz.activities.benefits;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.NavDrawerBaseActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.fragments.benefits.christmas.ChristmasFragment;
import br.com.avanade.fahz.fragments.benefits.dentalplan.DentalFragment;
import br.com.avanade.fahz.fragments.benefits.healthplan.HealthPlanFragment;
import br.com.avanade.fahz.fragments.benefits.pharmacy.PharmacyFragment;
import br.com.avanade.fahz.fragments.benefits.scholarship.ScholarshipFragment;
import br.com.avanade.fahz.fragments.benefits.schoolsupplies.SchoolSuppliesFragment;
import br.com.avanade.fahz.fragments.benefits.toy.ToyFragment;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.avanade.fahz.activities.benefits.BenefitsControlActivity.BenefitsFragment.DENTALPLAN;
import static br.com.avanade.fahz.activities.benefits.BenefitsControlActivity.BenefitsFragment.HEALTHPLAN;
import static br.com.avanade.fahz.activities.benefits.BenefitsControlActivity.BenefitsFragment.PHARMACY;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;
import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class BenefitsControlActivity extends NavDrawerBaseActivity {

    public enum BenefitsFragment {
        HEALTHPLAN,
        DENTALPLAN,
        PHARMACY,
        SCHOLARSHIP,
        SCHOOLSUPPLIES,
        TOY,
        CHRISTMAS
    }

    boolean isFirstFragment;

    @BindView(R.id.lbHello)
    TextView lblHello;
    @BindView(R.id.lblName)
    TextView lblName;
    @BindView(R.id.benefitsContainer)
    LinearLayout benefitsContainer;
    @BindView(R.id.benefit_manager_container)
    ConstraintLayout mBenefitManagerContainer;

    @Override
    protected void onResume() {
        super.onResume();

        SessionManager sessionManager = new SessionManager(this);

        if(sessionManager.getToken() == null || sessionManager.getToken().isEmpty()){
            sessionManager.logout();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_benefits_control);
        ButterKnife.bind(this);
        setImageHeaderVisibility(true);
        setMenuVisible(false);

        isFirstFragment=true;
        BenefitsFragment benefitFragment = (BenefitsFragment) getIntent().getSerializableExtra(Constants.BENEFITIS_CONTROL);
        setFragment(benefitFragment);

        setupUi();
    }

    //METODO PARA CONFIRUAÇÂO DE TELA
    private void setupUi() {

        lblName.setText(FahzApplication.getInstance().getFahzClaims().getName().split(" ")[0]);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void showSnackBar(String message, int typeApproval) {
        Snackbar snackbar = Snackbar.make(mBenefitManagerContainer, message, Snackbar.LENGTH_LONG).setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(typeApproval == TYPE_SUCCESS ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void setFragment(BenefitsFragment type)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (type) {
            case HEALTHPLAN:
                HealthPlanFragment fragment = new HealthPlanFragment();
                ft.replace(R.id.benefitsContainer, fragment, HEALTHPLAN.toString());
                break;
            case DENTALPLAN:
                DentalFragment dentalFragment = new DentalFragment();
                ft.replace(R.id.benefitsContainer, dentalFragment, DENTALPLAN.toString());

                break;
            case PHARMACY:
                PharmacyFragment pFragment = new PharmacyFragment();
                ft.replace(R.id.benefitsContainer, pFragment, PHARMACY.toString());

                break;
            case SCHOLARSHIP:
                ScholarshipFragment sFragment = new ScholarshipFragment();
                ft.replace(R.id.benefitsContainer, sFragment, BenefitsFragment.SCHOLARSHIP.toString());

                break;
            case SCHOOLSUPPLIES:
                SchoolSuppliesFragment ssFragment = new SchoolSuppliesFragment();
                ft.replace(R.id.benefitsContainer, ssFragment, BenefitsFragment.SCHOOLSUPPLIES.toString());

                break;
            case TOY:
                ToyFragment tFragment = new ToyFragment();
                ft.replace(R.id.benefitsContainer, tFragment, BenefitsFragment.TOY.toString());
                break;
            case CHRISTMAS:
                ChristmasFragment cFragment = new ChristmasFragment();
                ft.replace(R.id.benefitsContainer, cFragment, BenefitsFragment.CHRISTMAS.toString());
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
