package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class DocumentDeleteRequest {
    @SerializedName("cpf")
    public String cpf;
    @SerializedName("path")
    public String path;

    public  DocumentDeleteRequest(String cpf, String path)
    {
        this.cpf = cpf;
        this.path = path;
    }

}
