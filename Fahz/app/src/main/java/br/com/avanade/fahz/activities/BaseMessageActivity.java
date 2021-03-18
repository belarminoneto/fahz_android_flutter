package br.com.avanade.fahz.activities;

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
import br.com.avanade.fahz.fragments.message.ListMessageFragment;
import br.com.avanade.fahz.fragments.message.ViewMessageFragment;
import br.com.avanade.fahz.util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.avanade.fahz.util.Constants.TYPE_SUCCESS;

public class BaseMessageActivity extends NavDrawerBaseActivity {
    public enum BaseMessageFragment {
        LISTMESSAGE,
        VIEWMESSAGE
    }

    public void setLoading(boolean loading, String text) {
        if (loading) {
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else
            mProgressDialog.dismiss();
    }

    @BindView(R.id.base_message_manager_container)
    RelativeLayout mBaseManagerContainer;

    BaseMessageFragment baseMessageragment;
    boolean isFirstFragment;
    private ProgressDialog mProgressDialog;
    private Snackbar snackbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenter.start(getApplication(), "33073782-4249-473a-890f-0f67a8b178f8", Analytics.class, Crashes.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_base_message);
        ButterKnife.bind(this);

        setImageHeaderVisibility(true);
        setMenuVisible(false);
        mProgressDialog = new ProgressDialog(this);

        isFirstFragment = true;
        baseMessageragment = (BaseMessageFragment) getIntent().getSerializableExtra(Constants.BASE_MESSAGE_CONTROL);
        assert baseMessageragment != null;
        setFragment(baseMessageragment);

        setupUi();
    }

    public void setFragment(BaseMessageFragment type) {
        cancelSnackBar();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (type) {
            case LISTMESSAGE:
                ListMessageFragment dependentAdhesion = new ListMessageFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BENEFIT_EXTRA, Constants.BENEFITHEALTH);
                dependentAdhesion.setArguments(bundle);
                ft.replace(R.id.baseMessageContainer, dependentAdhesion, BaseMessageFragment.LISTMESSAGE.toString());
                break;
            case VIEWMESSAGE:
                ViewMessageFragment cancelHolder = new ViewMessageFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.BENEFIT_EXTRA, Constants.BENEFITHEALTH);
                bundle.putString(Constants.BENEFIT_DESCRIPTION_EXTRA, Constants.BENEFITHEALTH_DESC);
                cancelHolder.setArguments(bundle);
                ft.replace(R.id.baseMessageContainer, cancelHolder, BaseMessageFragment.VIEWMESSAGE.toString());
                break;
            default:
                showSnackBar(getString(R.string.NoFragmentFoud), Constants.TYPE_FAILURE);
        }

        if (isFirstFragment) {
            isFirstFragment = false;
        } else {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    //METODO PARA CONFIRUAÇÂO DE TELA
    private void setupUi() {
        toolbarTitle.setText(getString(R.string.label_personal_data));
        setImageHeaderVisibility(false);
    }

    public void cancelSnackBar() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
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
}
