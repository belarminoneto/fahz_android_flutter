package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OperatorByBenefit {
    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("IsCurrent")
    @Expose
    private Boolean isCurrent;
    @SerializedName("IsAllowed")
    @Expose
    private Boolean isAllowed;

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

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public Boolean getIsAllowed() {
        return isAllowed;
    }

    public void setIsAllowed(Boolean isAllowed) {
        this.isAllowed = isAllowed;
    }
}
