package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserBenefits {
    @SerializedName("count")
    public int count;
    @SerializedName("benefits")
    public List<Benefits> benefits;
}
