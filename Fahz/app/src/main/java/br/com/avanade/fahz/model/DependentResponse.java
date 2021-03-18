package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class DependentResponse {
    @SerializedName("commited")
    public Boolean commited;
    @SerializedName("messageIdentifier")
    public String messageIdentifier;
}
