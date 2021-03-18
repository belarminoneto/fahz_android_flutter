package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InactivateDependent {
    @SerializedName("cpf")
    @Expose
    private String cpf;
    @SerializedName("reasonCode")
    @Expose
    private Integer reasonCode;
    @SerializedName("date")
    @Expose
    private String date;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
