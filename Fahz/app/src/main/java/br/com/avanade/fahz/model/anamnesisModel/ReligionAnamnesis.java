package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.util.AnamnesisUtils;

public class ReligionAnamnesis {

    public ReligionAnamnesis(AuxDataBridge auxData) {
        this.id = Integer.valueOf(auxData.getId());
        this.desc = auxData.getTextShown();
    }

    public ReligionAnamnesis() {
    }

    private int id;

    @SerializedName("descricao")
    private String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public String getDescFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(desc);
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
