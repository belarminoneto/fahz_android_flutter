package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class SerproResponse {
    @SerializedName("cpf")
    public String cpf;
    @SerializedName("nome")
    public String name;
    @SerializedName("nascimento")
    public String birthdate;
    @SerializedName("status")
    public StatusSerproResponse statusResponse;

    @Override
    public String toString() {
        return "User{" +
                "cpf='" + cpf + '\'' +
                ", name='" + name + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", statusResponse=" + statusResponse +
                '}';
    }
}
