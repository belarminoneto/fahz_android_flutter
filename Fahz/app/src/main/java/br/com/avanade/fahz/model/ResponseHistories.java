package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseHistories {
    @SerializedName("commited")
    @Expose
    private Boolean commited;
    @SerializedName("history")
    @Expose
    private List<History> history = null;

    public Boolean getCommited() {
        return commited;
    }

    public void setCommited(Boolean commited) {
        this.commited = commited;
    }

    public List<History> getHistory() {
        return history;
    }

    public void setHistory(List<History> history) {
        this.history = history;
    }
}
