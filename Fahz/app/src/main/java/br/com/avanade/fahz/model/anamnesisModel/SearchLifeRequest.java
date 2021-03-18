package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class SearchLifeRequest {

    @SerializedName("Filtro")
    private String filter;

    public SearchLifeRequest(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
