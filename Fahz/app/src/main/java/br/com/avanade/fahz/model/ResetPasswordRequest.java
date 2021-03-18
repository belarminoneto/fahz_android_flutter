package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {
    @SerializedName("cpf")
    public String cpf;
    @SerializedName("method")
    public String method;

    @Override
    public String toString() {
        return "ResetPasswordRequest{" +
                "cpf='" + cpf + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
