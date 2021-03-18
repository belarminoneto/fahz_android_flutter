package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class ExternalAccess {
    @SerializedName("url")
    public String URL;

    @SerializedName("user")
    public String User;

    @SerializedName("pwd")
    public String password;
}
