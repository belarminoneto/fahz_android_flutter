package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class QuestionnaireRequest {

    @SerializedName("ID")
    private long id;

    @SerializedName("CPF")
    private String cpf;

    public QuestionnaireRequest(long id, String cpf) {
        this.id = id;
        this.cpf = cpf;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
