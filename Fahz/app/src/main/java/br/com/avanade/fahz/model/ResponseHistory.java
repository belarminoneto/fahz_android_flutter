package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class ResponseHistory {
    @SerializedName("commited")
    public boolean commited;
    @SerializedName("idhistory")
    public String idhistory;
}
