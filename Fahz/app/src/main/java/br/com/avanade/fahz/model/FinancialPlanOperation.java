package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FinancialPlanOperation {

    @SerializedName("AmountPaid")
    @Expose
    private Double AmountPaid;
    @SerializedName("IdFinancialPlan")
    @Expose
    private String IdFinancialPlan;

    public int indexSelected;

    public String period;
    public String cpf;


    public Double getAmountPaid() {
        return AmountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        AmountPaid = amountPaid;
    }

    public String getIdFinancialPlan() {
        return IdFinancialPlan;
    }

    public void setIdFinancialPlan(String idScholarship) {
        IdFinancialPlan = idScholarship;
    }
}
