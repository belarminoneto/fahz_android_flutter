package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class CivilStatusRequest {
    @SerializedName("Id")
    public int id;
    @SerializedName("Description")
    public String description;

    @Override
    public String toString() {
        return "CivilStatusRequest{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
