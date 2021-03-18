package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Operator {

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("Benefit")
    @Expose
    private Object benefit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getBenefit() {
        return benefit;
    }

    public void setBenefit(Object benefit) {
        this.benefit = benefit;
    }

}