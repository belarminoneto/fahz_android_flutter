package br.com.avanade.fahz.fragments.lpgd;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.Adapter.lgpd.CardViewPolicyPrivacyAdapter;
import br.com.avanade.fahz.Adapter.lgpd.CardViewPolicyPrivacyQuickLinkAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.lgpdModel.PanelCards;
import br.com.avanade.fahz.model.lgpdModel.PrivacyTerms;
import br.com.avanade.fahz.model.lgpdModel.QuickLinkPanel;
import br.com.avanade.fahz.util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivacyTermsOverViewFragment extends Fragment {

    private PrivacyTerms mPanelsLgpd;
    private ProgressDialog mProgressDialog;
    private List<PanelCards> mPanelCardsList = new ArrayList<>();
    private List<QuickLinkPanel> mQuickLinkPanelList = new ArrayList<>();
    CardViewPolicyPrivacyAdapter mCardViewPolicyPrivacyAdapter;
    CardViewPolicyPrivacyQuickLinkAdapter mCardViewPolicyPrivacyQuickLinkAdapter;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.txt_terms)
    TextView mTerms;

    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;

    @BindView(R.id.recycle_view_links)
    RecyclerView mRecycleViewLinks;


    public static PrivacyTermsOverViewFragment newInstance(PrivacyTerms panelsLgpd) {
        PrivacyTermsOverViewFragment privacyTermsFragment = new PrivacyTermsOverViewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("PanelsLgpd", panelsLgpd);
        privacyTermsFragment.setArguments(bundle);

        return privacyTermsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacy_terms_overview, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getActivity());

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPanelsLgpd = bundle.getParcelable("PanelsLgpd");

            mTitle.setText(mPanelsLgpd.section.get(0).panel.get(0).title);

            Spanned sp = Html.fromHtml(mPanelsLgpd.section.get(0).panel.get(0).obs);
            mTerms.setText(sp);

            //CardView links rápidos
            mQuickLinkPanelList.clear();
            for (int k = 0; k < mPanelsLgpd.quickLinkPanels.size(); k++) {

                if (mPanelsLgpd.quickLinkPanels.get(k).linkTo.equals(Constants.LINK_CENTRAL_DE_NOTIFICACOES)) {
                    QuickLinkPanel quickLinkPanel = new QuickLinkPanel();
                    quickLinkPanel.description = mPanelsLgpd.quickLinkPanels.get(k).description;
                    quickLinkPanel.linkTo = mPanelsLgpd.quickLinkPanels.get(k).linkTo;
                    mQuickLinkPanelList.add(quickLinkPanel);
                }
            }

            mRecycleViewLinks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mCardViewPolicyPrivacyQuickLinkAdapter = new CardViewPolicyPrivacyQuickLinkAdapter(getContext(), mQuickLinkPanelList);
            mRecycleViewLinks.setAdapter(mCardViewPolicyPrivacyQuickLinkAdapter);


            //CardView Politica de Privacidade
            mPanelCardsList.clear();
            for (int j = 0; j < mPanelsLgpd.section.get(0).panel.size(); j++) {
                if(mPanelsLgpd.section.get(0).panel.get(j).id != 1) {
                    PanelCards panelCards = new PanelCards();
                    panelCards.id = mPanelsLgpd.section.get(0).panel.get(j).id;
                    panelCards.title = mPanelsLgpd.section.get(0).panel.get(j).title;
                    panelCards.obs = mPanelsLgpd.section.get(0).panel.get(j).obs;
                    mPanelCardsList.add(panelCards);
                }
            }

            mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mCardViewPolicyPrivacyAdapter = new CardViewPolicyPrivacyAdapter(getContext(), mPanelCardsList);
            mRecycleView.setAdapter(mCardViewPolicyPrivacyAdapter);
        }

        return view;
    }
}


