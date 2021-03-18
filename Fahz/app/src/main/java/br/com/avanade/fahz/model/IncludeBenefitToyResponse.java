package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IncludeBenefitToyResponse {

    @SerializedName("commited")
    @Expose
    private Boolean commited;

    @SerializedName("messageIdentifier")
    @Expose
    private String messageIdentifier;

    public Boolean getCommited() {
        return commited;
    }

    public String getMessageIdentifier() {
        return messageIdentifier;
    }
}
