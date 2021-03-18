package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListDependentsWithPendingAnnualRenewalResponse {

    @SerializedName("commited")
    @Expose
    private Boolean commited;

    @SerializedName("result")
    @Expose
    private Integer result;

    public Boolean getCommited() {
        return commited != null && commited;
    }

    public Integer getResult() {
        return result;
    }
}
