package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class Documents {
    @SerializedName("Id")
    public String Id;
    @SerializedName("Path")
    public String path;
    @SerializedName("Date")
    public String date;
    @SerializedName("Name")
    public String name;

    public String cpf;
    public int type;
    public String id;

    public boolean newAdition;

    public Documents(String path, String date, String name, boolean newAdition) {
        this.path = path;
        this.date = date;
        this.name = name;
        this.newAdition = newAdition;
    }

    public Documents(String path, String date, String name, boolean newAdition, String id) {
        this.path = path;
        this.date = date;
        this.name = name;
        this.newAdition = newAdition;
        this.Id = id;
        this.id = id;
    }
}
