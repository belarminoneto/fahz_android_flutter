package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class StatusSavedSerproResponse {
    @SerializedName("situacao")
    public String code;
    @SerializedName("descricao")
    public String description;

    @Override
    public String toString() {
        return "StatusResponse{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
