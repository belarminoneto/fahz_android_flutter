package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class AccountHolder {
    @SerializedName("CPF")
    public String cpf;

    @Override
    public String toString() {
        return "AccountHolder{" +
                "cpf='" + cpf + '\'' +
                '}';
    }
}
