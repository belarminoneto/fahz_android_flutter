package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IncludeBenefitToyModel {

    @SerializedName("cpf")
    @Expose
    private String cpf;

    @SerializedName("Justification")
    @Expose
    private String justification;

    @SerializedName("Documents")
    @Expose
    private List<String> documents;

    public IncludeBenefitToyModel(String cpf, String justification) {
        this.cpf = cpf;
        this.justification = justification;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }
}
