package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.Operator;

public class OperatorResponse {
    @SerializedName("commited")
    @Expose
    private Boolean commited;
    @SerializedName("Operator")
    @Expose
    private Operator operator;

    @SerializedName("messageIdentifier")
    @Expose
    private String messageIdentifier;

    public Boolean getCommited() {
        return commited;
    }

    public void setCommited(Boolean commited) {
        this.commited = commited;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getMessageIdentifier() {
        return messageIdentifier;
    }

    public void setMessageIdentifier(String messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }
}
