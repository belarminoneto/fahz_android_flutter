package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordResponse {
    @SerializedName("commited")
    public boolean committed;
    @SerializedName("message")
    public String message;
    @SerializedName("email")
    public String email;
    @SerializedName("cellphone")
    public String cellphone;

    @Override
    public String toString() {
        return "ResetPasswordResponse{" +
                "commited=" + committed +
                ", message='" + message + '\'' +
                '}';
    }
}
