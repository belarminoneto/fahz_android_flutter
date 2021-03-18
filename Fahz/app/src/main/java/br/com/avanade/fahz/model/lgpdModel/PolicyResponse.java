package br.com.avanade.fahz.model.lgpdModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PolicyResponse {

    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    @SerializedName("policies")
    @Expose
    private Policies policies;

    public String getMessage() {
        return message;
    }

    public boolean hasMessage() {
        return message != null;
    }

    public Policies getPolicy() {
        return policies;
    }
}