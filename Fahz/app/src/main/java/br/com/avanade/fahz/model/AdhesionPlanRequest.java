package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdhesionPlanRequest {
    @SerializedName("cpf")
    @Expose
    private String cpf;
    @SerializedName("operatorCode")
    @Expose
    private Object operatorCode;
    @SerializedName("exception")
    @Expose
    private boolean exception;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("PlanId")
    @Expose
    private Object planId;
    @SerializedName("Coparticipation")
    @Expose
    private Boolean coparticipation;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Object getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(Object operatorCode) {
        this.operatorCode = operatorCode;
    }

    public boolean getException() {
        return exception;
    }

    public void setException(boolean exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getPlanId() {
        return planId;
    }

    public void setPlanId(Object planId) {
        this.planId = planId;
    }

    public Boolean getCoparticipation() {
        return coparticipation;
    }

    public void setCoparticipation(Boolean coparticipation) {
        this.coparticipation = coparticipation;
    }

}
