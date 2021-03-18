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
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.ProfileActivity;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.lgpdModel.Panel;
import br.com.avanade.fahz.model.lgpdModel.PanelCards;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivacyTermsFragment extends Fragment {

    private Panel mPanel;
    private ProgressDialog mProgressDialog;
    private List<PanelCards> mPanelCardsList = new ArrayList<>();
    CardViewPolicyPrivacyAdapter mCardViewPolicyPrivacyAdapter;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.txt_terms)
    TextView mTerms;

    @BindView(R.id.txt_update)
    TextView mUpdate;

    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;


    public static PrivacyTermsFragment newInstance(Panel panel) {
        PrivacyTermsFragment privacyTermsFragment = new PrivacyTermsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Panel", panel);
        privacyTermsFragment.setArguments(bundle);

        return privacyTermsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacy_terms, container, false);
        ButterKnife.bind(this, view);

        mProgressDialog = new ProgressDialog(getActivity());

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPanel = bundle.getParcelable("Panel");

            mTitle.setText(mPanel.title);
            if (mPanel.updateDate != null) {
                StringBuilder s = new StringBuilder();
                s.append(getString(R.string.text_effetive_from));
                s.append(" ");
                s.append(DateEditText.parseTODate(mPanel.updateDate));
                mUpdate.setText(s.toString());
            }
            Spanned sp = Html.fromHtml(mPanel.obs);
            mTerms.setText(sp);

            //Cada panel pode ter um comportamento diferente
            if (mPanel.id == 1) { //Visão Geral

                mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                mCardViewPolicyPrivacyAdapter = new CardViewPolicyPrivacyAdapter(getContext(), mPanelCardsList);
                mRecycleView.setAdapter(mCardViewPolicyPrivacyAdapter);

                if (mPanel.version.size() > 0) {
                    mCardViewPolicyPrivacyAdapter = new CardViewPolicyPrivacyAdapter(getContext(), mPanelCardsList);
                    mRecycleView.setAdapter(mCardViewPolicyPrivacyAdapter);
                }

            }else if (mPanel.id == 2 || //Politica de Privacidade
                    mPanel.id == 5 || //Termo de Serviço
                    mPanel.id == 6 || //Central de Segurança
                    mPanel.id == 7 || //Perguntas Frequentes
                    mPanel.id == 10 ){//Politica de uso de dados

                //Montar os cards aqui
                mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

                if (mPanel.version.get(0).textPanel.size() > 0) {

                    mPanelCardsList.clear();
                    for (int j = 0; j < mPanel.version.get(0).textPanel.size(); j++) {
                        PanelCards panelCards = new PanelCards();

                        panelCards.id = mPanel.version.get(0).textPanel.get(j).id;
                        panelCards.title = mPanel.version.get(0).textPanel.get(j).title;
                        panelCards.obs = mPanel.version.get(0).textPanel.get(j).complete;

                        mPanelCardsList.add(panelCards);
                    }

                    mCardViewPolicyPrivacyAdapter = new CardViewPolicyPrivacyAdapter(getContext(), mPanelCardsList);
                    mRecycleView.setAdapter(mCardViewPolicyPrivacyAdapter);
                }
            }
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser&& getActivity()!=null)
            if(getActivity() instanceof  ProfileActivity)
                ((ProfileActivity)getActivity()).changeHeaderTitle(getString(R.string.text_change_address));
    }
}


