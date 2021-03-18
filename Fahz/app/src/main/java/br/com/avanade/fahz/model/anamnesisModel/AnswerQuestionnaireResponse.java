package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class AnswerQuestionnaireResponse {

    @SerializedName("respostasinvalidas")
    private String invalidAnswers;

    @SerializedName("Controle")
    private QuestionnaireControl control;

    public String getInvalidAnswers() {
        return invalidAnswers;
    }

    public void setInvalidAnswers(String invalidAnswers) {
        this.invalidAnswers = invalidAnswers;
    }

    public QuestionnaireControl getControl() {
        return control;
    }

    public void setControl(QuestionnaireControl control) {
        this.control = control;
    }
}
