package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangeBenefit {
    @SerializedName("CPF")
    @Expose
    private String cPF;
    @SerializedName("BenefitId")
    @Expose
    private String benefitId;
    @SerializedName("Operator")
    @Expose
    private String operator;
    @SerializedName("PlanId")
    @Expose
    private String planId;
    @SerializedName("outOfPolicy")
    @Expose
    private boolean outOfPolicy;
    @SerializedName("justification")
    @Expose
    private String justification;

    public String getCPF() {
        return cPF;
    }

    public void setCPF(String cPF) {
        this.cPF = cPF;
    }

    public String getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(String benefitId) {
        this.benefitId = benefitId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public boolean getOutOfPolicy() {
        return outOfPolicy;
    }

    public void setOutOfPolicy(boolean outOfPolicy) {
        this.outOfPolicy = outOfPolicy;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
