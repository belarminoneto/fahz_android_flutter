package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CanSendReceiptResponse {
    @SerializedName("canSendReceipt")
    @Expose
    private Boolean canSendReceipt;
    @SerializedName("needToSendReceipt")
    @Expose
    private Boolean needToSendReceipt;

    public Boolean getCanSendReceipt() {
        return canSendReceipt;
    }

    public void setCanSendReceipt(Boolean canSendReceipt) {
        this.canSendReceipt = canSendReceipt;
    }

    public Boolean getNeedToSendReceipt() {
        return needToSendReceipt;
    }

    public void setNeedToSendReceipt(Boolean needToSendReceipt) {
        this.needToSendReceipt = needToSendReceipt;
    }
}
