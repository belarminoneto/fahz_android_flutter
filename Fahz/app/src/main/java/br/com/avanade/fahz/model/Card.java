package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Card {

    @SerializedName("CPF")
    @Expose
    private String cPF;
    @SerializedName("FullName")
    @Expose
    private String fullName;
    @SerializedName("Operator")
    @Expose
    private Operator operator;
    @SerializedName("Plan")
    @Expose
    private Plan plan;
    @SerializedName("Number")
    @Expose
    private String number;
    @SerializedName("isHolder")
    @Expose
    private Boolean isHolder;

    public String getCPF() {
        return cPF;
    }

    public void setCPF(String cPF) {
        this.cPF = cPF;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Boolean getIsHolder() {
        return isHolder;
    }

    public void setIsHolder(Boolean isHolder) {
        this.isHolder = isHolder;
    }

}
