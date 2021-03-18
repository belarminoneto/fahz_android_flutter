package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class SearchFamilyTreeRequest {

    @SerializedName("CPF")
    private String cpf;

    @SerializedName("Tipo")
    private int type = 0;

    public SearchFamilyTreeRequest(String cpf) {
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getType() {
        return type;
    }
}
