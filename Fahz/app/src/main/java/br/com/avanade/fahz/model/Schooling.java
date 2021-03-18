package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Schooling {

    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("ValidateHolder")
    @Expose
    private Boolean validateHolder;
    @SerializedName("ValidateDependent")
    @Expose
    private Boolean validateDependent;

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getValidateHolder() {
        return validateHolder;
    }

    public void setValidateHolder(Boolean validateHolder) {
        this.validateHolder = validateHolder;
    }

    public Boolean getValidateDependent() {
        return validateDependent;
    }

    public void setValidateDependent(Boolean validateDependent) {
        this.validateDependent = validateDependent;
    }

}
