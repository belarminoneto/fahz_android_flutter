package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subsidiary {
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("CNPJ")
    @Expose
    private String cNPJ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCNPJ() {
        return cNPJ;
    }

    public void setCNPJ(String cNPJ) {
        this.cNPJ = cNPJ;
    }
}
