package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class DocumentDownload {
    @SerializedName("Mimetype")
    public String Mimetype;
    @SerializedName("Filename")
    public String Filename;
    @SerializedName("Data")
    public String Data;
}
