package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class SearchAuxiliaryDataRequest {

    public SearchAuxiliaryDataRequest(int type, String desc) {
        this.setType(type);
        this.desc = desc;
    }

    @SerializedName("Tipo")
    private int type;

    @SerializedName("Filtro")
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
