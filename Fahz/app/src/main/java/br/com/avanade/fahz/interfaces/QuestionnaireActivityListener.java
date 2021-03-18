package br.com.avanade.fahz.interfaces;

import br.com.avanade.fahz.model.anamnesisModel.Question;
import br.com.avanade.fahz.model.anamnesisModel.SupplementaryDataAnamnesis;

public interface QuestionnaireActivityListener {
    void onAnswerChanged(Question question);
    void onDataChanged(SupplementaryDataAnamnesis dataAnamnesis);
    void enableNextButton(Boolean enable);
    void onQuestionError(String errorMsg);
    void downloadImageRequested(String imageName);
}