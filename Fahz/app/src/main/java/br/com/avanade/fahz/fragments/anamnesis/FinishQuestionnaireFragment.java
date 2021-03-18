package br.com.avanade.fahz.fragments.anamnesis;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.anamnesis.ViewAnswersActivity;
import br.com.avanade.fahz.model.anamnesisModel.Questionnaire;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static br.com.avanade.fahz.util.ConstantsAnamnesis.ID_TYPE_QUEST;

public class FinishQuestionnaireFragment extends Fragment {

    @BindView(R.id.imgAlert)
    ImageView imgAlert;

    @BindView(R.id.txtNC)
    TextView txtNC;

    private Questionnaire mQuestionnaire;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finish_questionnaire, container, false);
        ButterKnife.bind(this, view);
        if (mQuestionnaire.isNonCompliance()) {
            imgAlert.setVisibility(View.VISIBLE);
            txtNC.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @NonNull
    public static FinishQuestionnaireFragment newInstance(@NonNull Questionnaire questionnaire) {
        FinishQuestionnaireFragment fragment = new FinishQuestionnaireFragment();
        fragment.mQuestionnaire = questionnaire;
        return fragment;
    }

    @OnClick(R.id.btnAnswers)
    public void onBtnAnswersPressed() {
        Intent intent = new Intent(getContext(), ViewAnswersActivity.class);
        intent.putExtra(ID_TYPE_QUEST, mQuestionnaire.getIdType());
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }

    @OnClick(R.id.btnQuestionnaires)
    public void onBtnQuestionnairesPressed() {
        Objects.requireNonNull(getActivity()).finish();
    }
}
