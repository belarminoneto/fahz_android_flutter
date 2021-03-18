package br.com.avanade.fahz.model.terms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TermCheckBody {
    @SerializedName("code")
    @Expose
    String code;
    @SerializedName("usr")
    @Expose
    String cpf;

    public TermCheckBody(String code, String cpf) {
        this.code = code;
        this.cpf = cpf;
    }
}
