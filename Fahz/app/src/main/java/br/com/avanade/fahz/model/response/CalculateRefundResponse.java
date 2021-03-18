package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.SerializedName;

public class CalculateRefundResponse {

    @SerializedName("commited")
    public boolean commited;
    @SerializedName("refund")
    public Double refund;
}
