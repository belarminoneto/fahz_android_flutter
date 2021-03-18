package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class SupplementaryDataAnamnesisRequest {

    @SerializedName("peso")
    private float weight;

    @SerializedName("altura")
    private float height;

//    @SerializedName("idReligiao")
//    private Integer religion;

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

//    public Integer getReligion() {
//        return religion;
//    }
//
//    public void setReligion(Integer religion) {
//        this.religion = religion;
//    }
}
