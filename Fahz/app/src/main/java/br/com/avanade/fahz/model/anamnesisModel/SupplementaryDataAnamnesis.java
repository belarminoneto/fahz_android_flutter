package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class SupplementaryDataAnamnesis {

    private String cpf;

    @SerializedName("peso")
    private float weight;

    @SerializedName("altura")
    private float height;

    @SerializedName("imc")
    private float bodyMassIndex;

    @SerializedName("imcfaixa")
    private String bodyMassIndexRange;

    @SerializedName("religiao")
    private ReligionAnamnesis religion;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

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

    public float getBodyMassIndex() {
        return bodyMassIndex;
    }

    public void setBodyMassIndex(float bodyMassIndex) {
        this.bodyMassIndex = bodyMassIndex;
    }

    public String getBodyMassIndexRange() {
        return bodyMassIndexRange;
    }

    public void setBodyMassIndexRange(String bodyMassIndexRange) {
        this.bodyMassIndexRange = bodyMassIndexRange;
    }

    public ReligionAnamnesis getReligion() {
        return religion;
    }

    public void setReligion(ReligionAnamnesis religion) {
        this.religion = religion;
    }
}
