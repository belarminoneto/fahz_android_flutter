package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class AnswerQuestionnaireRequest {

    @SerializedName("cpf")
    private String cpf;

    @SerializedName("ambiente")
    private int environment;

    @SerializedName("usuario")
    private String userName;

    @SerializedName("Dados")
    private SupplementaryDataAnamnesisRequest data;

    @SerializedName("Questionario")
    private QuestionnaireRequest2 questionnaire;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getEnvironment() {
        return environment;
    }

    public void setEnvironment(int environment) {
        this.environment = environment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public SupplementaryDataAnamnesisRequest getData() {
        return data;
    }

    public void setData(SupplementaryDataAnamnesisRequest data) {
        this.data = data;
    }

    public QuestionnaireRequest2 getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(QuestionnaireRequest2 questionnaire) {
        this.questionnaire = questionnaire;
    }
}
