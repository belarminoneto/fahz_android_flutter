package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("commited")
    public boolean commited;
    @SerializedName("messageIdentifier")
    public String messageIdentifier;
    @SerializedName("path")
    public String path;
    @SerializedName("documentId")
    public String documentId;
}
