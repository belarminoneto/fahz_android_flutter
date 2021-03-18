package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoryModel {
    @SerializedName("historiesId")
    @Expose
    private List<String> historiesId;
    @SerializedName("documentsId")
    @Expose
    private List<String> documentsId = null;

    public List<String> getHistoriesId() {
        return historiesId;
    }

    public void setHistoriesId(List<String> historiesId) {
        this.historiesId = historiesId;
    }

    public List<String> getDocumentsId() {
        return documentsId;
    }

    public void setDocumentsId(List<String> documentsId) {
        this.documentsId = documentsId;
    }
}
