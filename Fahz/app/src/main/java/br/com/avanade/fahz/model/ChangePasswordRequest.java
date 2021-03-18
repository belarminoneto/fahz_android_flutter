package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("cpf")
    public String cpf;
    @SerializedName("currentPassword")
    public String currentPassword;
    @SerializedName("newPassword")
    public String newPassword;

    @Override
    public String toString() {
        return "ChangePasswordRequest{" +
                "cpf='" + cpf + '\'' +
                ", currentPassword='" + currentPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}
