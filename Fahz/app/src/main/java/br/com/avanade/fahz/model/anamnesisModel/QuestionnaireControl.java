package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class QuestionnaireControl {

    private String cpf;

    @SerializedName("idtipo")
    private int idType;

    @SerializedName("pendente")
    private int pending;

    @SerializedName("percentual")
    private float percentage;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }
}
