package br.com.avanade.fahz.model.servicemodels;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("cpf")
    public String CPF;
    @SerializedName("password")
    public String Password;
    @SerializedName("Platform")
    public String Platform;
}
