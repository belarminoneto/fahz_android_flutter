package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class Answer {

    @SerializedName("codigointerno")
    private long internalCode;

    @SerializedName("cpf")
    private String lifeCPF;

    private String userCPF;

    @SerializedName("idtipo")
    private int idType;

    @SerializedName("idquestionario")
    private long idQuest;

    @SerializedName("texto")
    private String text;

    @SerializedName("excludente")
    private int excluding;

    private boolean answerChanged;

    public long getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(long internalCode) {
        this.internalCode = internalCode;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public long getIdQuest() {
        return idQuest;
    }

    public void setIdQuest(long idQuest) {
        this.idQuest = idQuest;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getExcluding() {
        return excluding;
    }

    public void setExcluding(int excluding) {
        this.excluding = excluding;
    }

    public boolean isAnswerChanged() {
        return answerChanged;
    }

    public void setAnswerChanged(boolean answerChanged) {
        this.answerChanged = answerChanged;
    }

    public String getLifeCPF() {
        return lifeCPF;
    }

    public void setLifeCPF(String lifeCPF) {
        this.lifeCPF = lifeCPF;
    }

    public String getUserCPF() {
        return userCPF;
    }

    public void setUserCPF(String userCPF) {
        this.userCPF = userCPF;
    }
}
