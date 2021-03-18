package br.com.avanade.fahz.model.lgpdModel;

import com.google.gson.annotations.SerializedName;

public class CancelBenefitsFromTermDto {
    @SerializedName("Code")
    public String code;
    @SerializedName("Cpf")
    public String cpf;
    @SerializedName("Version")
    public String version;
}
