package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FamilyGroupItemResponse {

    @SerializedName("CPF")
    @Expose
    private String cpf;

    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("IsAccountHolder")
    @Expose
    private boolean isHolder;

    public String getCpf() {
        return cpf;
    }
    public String getName() {
        return name;
    }
    public boolean isHolder() {
        return isHolder;
    }
}
