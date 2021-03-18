package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Document {
    @SerializedName("count")
    private String count;
    @SerializedName("documentTypes")
    public List<DocumentType> documentTypes = null;

    @Override
    public String toString() {
        return "Document{" +
                "count='" + count + '\'' +
                ", documentTypes=" + documentTypes +
                '}';
    }
}
