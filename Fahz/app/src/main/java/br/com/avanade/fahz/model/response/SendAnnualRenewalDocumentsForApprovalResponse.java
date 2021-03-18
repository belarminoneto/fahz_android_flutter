package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.SerializedName;

public class SendAnnualRenewalDocumentsForApprovalResponse {

    @SerializedName("commited")
    private Boolean commited;

    @SerializedName("result")
    private String result;

    @SerializedName("messageIdentifier")
    private String messageIdentifier;

    public SendAnnualRenewalDocumentsForApprovalResponse(Boolean commited, String result) {
        this.commited = commited;
        this.result = result;
    }

    public Boolean getCommited() {
        return commited;
    }

    public String getMessage() {
        return result == null || result.isEmpty() ? messageIdentifier : result;
    }

    public void setMessageIdentifier(String messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }
}
