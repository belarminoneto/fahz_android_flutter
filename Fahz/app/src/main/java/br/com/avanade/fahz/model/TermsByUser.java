package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.response.TermOfUseResponse;

public class TermsByUser {

    @SerializedName("CPF")
    @Expose
    private String cPF;
    @SerializedName("TermOfUse")
    @Expose
    private TermOfUseResponse termOfUse;
    @SerializedName("Date")
    @Expose
    private String date;

    public String getCPF() {
        return cPF;
    }

    public void setCPF(String cPF) {
        this.cPF = cPF;
    }

    public TermOfUseResponse getTermOfUse() {
        return termOfUse;
    }

    public void setTermOfUse(TermOfUseResponse termOfUse) {
        this.termOfUse = termOfUse;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
