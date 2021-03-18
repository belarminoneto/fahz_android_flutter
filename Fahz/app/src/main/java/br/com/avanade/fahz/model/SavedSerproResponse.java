package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class SavedSerproResponse {
    @SerializedName("cpf")
    public String cpf;
    @SerializedName("nome")
    public String name;
    @SerializedName("nascimento")
    public String birthdate;
    @SerializedName("status")
    public StatusSavedSerproResponse statusSavedResponse;

    @Override
    public String toString() {
        return "User{" +
                "cpf='" + cpf + '\'' +
                ", name='" + name + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", statusResponse=" + statusSavedResponse +
                '}';
    }
}
