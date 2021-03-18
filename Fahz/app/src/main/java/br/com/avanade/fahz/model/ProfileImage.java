package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class ProfileImage {
    @SerializedName("Filename")
    public String Filename;
    @SerializedName("Data")
    public String Data;
    @SerializedName("Mimetype")
    public String Mimetype;
    @SerializedName("commited")
    public boolean commited;
    @SerializedName("messageIdentifier")
    public String messageIdentifier;
}
