package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DocumentType {
    @SerializedName("Id")
    public int id;
    @SerializedName("Description")
    public String description;
    @SerializedName("Documents")
    public List<Documents> documents;
    @SerializedName("UserHasIt")
    public boolean userHasIt;
    @SerializedName("UserCanUpload")
    public boolean userCanUpload;

    public DocumentType(int id, String description, List<Documents> documents, boolean userHasIt, boolean userCanUpload) {
        this.id = id;
        this.description = description;
        this.documents = documents;
        this.userHasIt = userHasIt;
        this.userCanUpload = userCanUpload;
    }

    public DocumentType(int id, String description, List<Documents> documents, boolean userHasIt) {
        this.id = id;
        this.description = description;
        this.documents = documents;
        this.userHasIt = userHasIt;
    }
}
