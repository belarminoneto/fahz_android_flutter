package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class QuestionnaireResponse {

    @SerializedName("Vida")
    private LifeAnamnesis life;

    @SerializedName("Questionario")
    private Questionnaire questionnaire;

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public LifeAnamnesis getLife() {
        return life;
    }

    public void setLife(LifeAnamnesis life) {
        this.life = life;
    }
}
