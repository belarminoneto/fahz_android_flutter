package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class CommitResponse {
    @SerializedName("commited")
    public boolean commited;
    @SerializedName("messageIdentifier")
    public String messageIdentifier;
    @SerializedName("messageCode")
    public String messageCode;
}
