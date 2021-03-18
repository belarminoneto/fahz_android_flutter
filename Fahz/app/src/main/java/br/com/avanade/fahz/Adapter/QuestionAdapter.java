package br.com.avanade.fahz.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import br.com.avanade.fahz.fragments.anamnesis.FinishQuestionnaireFragment;
import br.com.avanade.fahz.fragments.anamnesis.QuestionFragment;
import br.com.avanade.fahz.fragments.anamnesis.SupplementaryDataFragment;
import br.com.avanade.fahz.interfaces.QuestionnaireActivityListener;
import br.com.avanade.fahz.model.anamnesisModel.LifeAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.Questionnaire;

public class QuestionAdapter extends FragmentStatePagerAdapter {

    private final int SUPPLEMENTARY_DATA = 0;

    public static final int DATA_PAGES = 1;
    private final int FINISH_PAGE = 1;

    private Questionnaire mQuestionnaire;
    private LifeAnamnesis mLife;
    private QuestionnaireActivityListener listener;

    public QuestionAdapter(FragmentManager fm, QuestionnaireActivityListener listener, LifeAnamnesis life) {
        super(fm);
        this.listener = listener;
        this.mLife = life;
    }

    @Override
    public Fragment getItem(int position) {
        int previousPages = 0;
        if (position == getCount() - 1) {
            return FinishQuestionnaireFragment.newInstance(mQuestionnaire);
        } else if (mLife != null) {
            previousPages = DATA_PAGES;
            switch (position) {
                case SUPPLEMENTARY_DATA:
                    return SupplementaryDataFragment.newInstance(mLife.getSupplementaryData(), listener);
            }
        }
        return QuestionFragment.newInstance(mQuestionnaire.getQuestions().get(position - previousPages), listener);
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.mQuestionnaire = questionnaire;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        if (mQuestionnaire == null) {
            return 0;
        } else {
            return mQuestionnaire.getQuestions().size() + (mLife == null ? FINISH_PAGE : FINISH_PAGE + DATA_PAGES);
        }
    }
}
