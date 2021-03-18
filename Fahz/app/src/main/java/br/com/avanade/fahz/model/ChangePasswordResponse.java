package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordResponse {
    @SerializedName("commited")
    public boolean commited;
    @SerializedName("messageIdentifier")
    public String message;

    @Override
    public String toString() {
        return "ChangePasswordResponse{" +
                "commited='" + commited + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
