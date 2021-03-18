package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DependentResponseData {

    private boolean expanded;
    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("CPF")
    @Expose
    private String cpf;

    @SerializedName("Message")
    @Expose
    private String message;

    @SerializedName("IdPlan")
    @Expose
    private String idPlan;

    @SerializedName("IsHolder")
    @Expose
    private Boolean isHolder;

    @SerializedName("CanRequest")
    @Expose
    private Boolean canRequest;

    public DependentResponseData(String name) {
        this.name = name;
    }

    public DependentResponseData(String name, String message, Boolean canRequest) {
        this.name = name;
        this.message = message;
        this.canRequest = canRequest;
    }

    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public Boolean getHolder() {
        return isHolder;
    }

    public Boolean isCanRequest() {
        return canRequest;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getMessage() {
        return message;
    }

    public String getIdPlan() {
        return idPlan;
    }


}
