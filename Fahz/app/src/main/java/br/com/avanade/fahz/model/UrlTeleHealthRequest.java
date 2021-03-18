package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class UrlTeleHealthRequest implements Serializable {

    @SerializedName("cpf")
    @Expose
    private String cpf;
    @SerializedName("isHolder")
    @Expose
    private Boolean isHolder;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Boolean getHolder() {
        return isHolder;
    }

    public void setHolder(Boolean holder) {
        isHolder = holder;
    }
}
