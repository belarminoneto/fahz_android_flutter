package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class CalculateRefundRequest {

    @SerializedName("IdFinancialPlan")
    public String IdFinancialPlan;
    @SerializedName("AmountPaid")
    public Double AmountPaid;
}
