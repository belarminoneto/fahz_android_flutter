package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SchoolBenefitPeople {
    @SerializedName("CPF")
    @Expose
    private String cPF;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("IsHolder")
    @Expose
    private Boolean isHolder;
    @SerializedName("CanRequest")
    @Expose
    private Boolean canRequest;
    @SerializedName("NameRule")
    @Expose
    private Object nameRule;
    @SerializedName("IdSchool")
    @Expose
    private Integer idSchool;
    @SerializedName("Message")
    @Expose
    private String message;

    public String getCPF() {
        return cPF;
    }

    public void setCPF(String cPF) {
        this.cPF = cPF;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsHolder() {
        return isHolder;
    }

    public void setIsHolder(Boolean isHolder) {
        this.isHolder = isHolder;
    }

    public Boolean getCanRequest() {
        return canRequest;
    }

    public void setCanRequest(Boolean canRequest) {
        this.canRequest = canRequest;
    }

    public Object getNameRule() {
        return nameRule;
    }

    public void setNameRule(Object nameRule) {
        this.nameRule = nameRule;
    }

    public Integer getIdSchool() {
        return idSchool;
    }

    public void setIdSchool(Integer idSchool) {
        this.idSchool = idSchool;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
