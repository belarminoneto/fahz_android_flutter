package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class LifeBenefits {
    @SerializedName("CPF")
    public String cpf;
    @SerializedName("Benefit")
    public Benefit benefit;

    @Override
    public String toString() {
        return "LifeBenefits{" +
                "cpf='" + cpf + '\'' +
                ", benefit=" + benefit.toString() +
                '}';
    }
}
