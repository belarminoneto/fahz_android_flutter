package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionnaireRequest2 {

    @SerializedName("idtipo")
    private int idType;

    @SerializedName("idquestionario")
    private long idQuestionnaire;

    @SerializedName("Respostas")
    private List<Answer> answersList;

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public long getIdQuestionnaire() {
        return idQuestionnaire;
    }

    public void setIdQuestionnaire(long idQuestionnaire) {
        this.idQuestionnaire = idQuestionnaire;
    }

    public List<Answer> getAnswersList() {
        return answersList;
    }

    public void setAnswersList(List<Answer> answersList) {
        this.answersList = answersList;
    }
}
