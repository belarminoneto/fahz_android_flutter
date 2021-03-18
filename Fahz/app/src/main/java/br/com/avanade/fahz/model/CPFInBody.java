package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class CPFInBody {

    @SerializedName("cpf")
    @Expose
    private String cpf;

    public CPFInBody(String cpf) {
        this.cpf = cpf;
    }

    public CPFInBody() {
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

}