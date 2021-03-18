package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class Benefit {
    @SerializedName("Id")
    public int id;
    @SerializedName("Description")
    public String description;

    @Override
    public String toString() {
        return "Benefit{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
