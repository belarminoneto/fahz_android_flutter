package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Status implements Serializable {
    @SerializedName("Id")
    public int id;
    @SerializedName("Description")
    public String description;


    @Override
    public String toString() {
        return "Status{" +
                "id=" + id +
                '}';
    }

    public Status(int id) {
        this.id = id;
    }
}
