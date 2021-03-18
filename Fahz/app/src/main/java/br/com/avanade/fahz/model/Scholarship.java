package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Scholarship {
    @SerializedName("Rules")
    @Expose
    private String rules;
    @SerializedName("CreationDate")
    @Expose
    private String creationDate;
    @SerializedName("ModifiedDate")
    @Expose
    private String modifiedDate;
    @SerializedName("Monitorings")
    @Expose
    private List<Object> monitorings = null;
    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("Operator")
    @Expose
    private Operator operator;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("StartDate")
    @Expose
    private String startDate;
    @SerializedName("EndDate")
    @Expose
    private Object endDate;
    @SerializedName("Level")
    @Expose
    private Integer level;
    @SerializedName("CodOperator")
    @Expose
    private Object codOperator;

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public List<Object> getMonitorings() {
        return monitorings;
    }

    public void setMonitorings(List<Object> monitorings) {
        this.monitorings = monitorings;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Object getEndDate() {
        return endDate;
    }

    public void setEndDate(Object endDate) {
        this.endDate = endDate;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Object getCodOperator() {
        return codOperator;
    }

    public void setCodOperator(Object codOperator) {
        this.codOperator = codOperator;
    }

}
