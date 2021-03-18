package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class SignupResponse {
    @SerializedName("authorized")
    public boolean authorized;
    @SerializedName("messageIdentifier")
    public String messageIdentifier;
    @SerializedName("changePassword")
    public Boolean changePassword;
}
