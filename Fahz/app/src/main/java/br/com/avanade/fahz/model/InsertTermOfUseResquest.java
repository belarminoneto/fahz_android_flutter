package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class InsertTermOfUseResquest {
    @SerializedName("Code")
    public String code;
    @SerializedName("CPF")
    public String cpf;
    @SerializedName("Text")
    public String text;
    @SerializedName("Date")
    public String date;
    @SerializedName("Version")
    public String version;
    @SerializedName("typeAcceptedTerm")
    public int typeAcceptedTerm;
}
