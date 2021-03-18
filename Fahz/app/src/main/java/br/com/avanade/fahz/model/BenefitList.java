package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BenefitList {
    @SerializedName("count")
    public int count;
    @SerializedName("benefits")
    public List<Benefit> benefits;
}
