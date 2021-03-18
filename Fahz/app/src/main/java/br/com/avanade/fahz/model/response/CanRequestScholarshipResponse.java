package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.SystemBehaviorModel;

public class CanRequestScholarshipResponse {
    @SerializedName("CanRequest")
    @Expose
    private Boolean canRequest;
    @SerializedName("PlanId")
    @Expose
    private Integer planId;
    @SerializedName("SystemBehavior")
    @Expose
    private SystemBehaviorModel systemBehavior;
    @SerializedName("holderHasBankData")
    @Expose
    private boolean holderHasBankData;

    public Boolean getCanRequest() {
        return canRequest;
    }

    public void setCanRequest(Boolean canRequest) {
        this.canRequest = canRequest;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public SystemBehaviorModel getSystemBehavior() {
        return systemBehavior;
    }

    public void setSystemBehavior(SystemBehaviorModel systemBehavior) {
        this.systemBehavior = systemBehavior;
    }

    public boolean isHolderHasBankData() {
        return holderHasBankData;
    }

    public void setHolderHasBankData(boolean holderHasBankData) {
        this.holderHasBankData = holderHasBankData;
    }
}
