package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("authorized")
    public boolean authorized;
    @SerializedName("messageIdentifier")
    public String messageIdentifier;
    @SerializedName("changePassword")
    public boolean changePassword;
    @SerializedName("token")
    public String token;

    @Override
    public String toString() {
        return "LoginResponse{" +
                "authorized=" + authorized +
                   ", messageIdentifier='" + messageIdentifier + '\'' +
                ", changePassword=" + changePassword +
                "', token='" + token +
                "'}";
    }
}
