package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendCardRequest {
    @SerializedName("Plan")
    @Expose
    private Plan plan;
    @SerializedName("Schooling")
    @Expose
    private Schooling schooling;
    @SerializedName("CPF")
    @Expose
    private String cPF;
    @SerializedName("Name")
    @Expose
    private String Name;

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Schooling getSchooling() {
        return schooling;
    }

    public void setSchooling(Schooling schooling) {
        this.schooling = schooling;
    }

    public String getCPF() {
        return cPF;
    }

    public void setCPF(String cPF) {
        this.cPF = cPF;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int indexSelected;
}
