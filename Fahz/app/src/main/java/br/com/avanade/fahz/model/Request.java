package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import br.com.avanade.fahz.model.response.RequestStep;

public class Request implements Serializable {

    @SerializedName("HistoryId")
    @Expose
    private String historyId;

    @SerializedName("Date")
    @Expose
    private String date;

    @SerializedName("Approved")
    @Expose
    private Boolean approved;

    @SerializedName("ApprovedBy")
    @Expose
    private Object approvedBy;

    @SerializedName("BehaviorDescription")
    @Expose
    private String behaviorDescription;

    @SerializedName("Message")
    @Expose
    private String message;

    @SerializedName("ReasonReproved")
    @Expose
    private String reasonReproved;

    @SerializedName("Status")
    @Expose
    private Status status;

    @SerializedName("ApprovedDate")
    @Expose
    private String ApprovedDate;

    @SerializedName("Flows")
    @Expose
    private List<RequestStep> flows;

    public List<RequestStep> getFlows() {
        return flows;
    }

    public String getMessage() {
        return message;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean isApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Object getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Object approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getBehaviorDescription() {
        return behaviorDescription;
    }

    public void setBehaviorDescription(String behaviorDescription) {
        this.behaviorDescription = behaviorDescription;
    }

    public String getReasonReproved() {
        return reasonReproved;
    }

    public void setReasonReproved(String reasonReproved) {
        this.reasonReproved = reasonReproved;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getApprovedDate() {
        return ApprovedDate;
    }

    public void setApprovedDate(String approvedDate) {
        ApprovedDate = approvedDate;
    }

    public Result getFinalResult() {
        switch (status.id) {
            case 0:
                return Result.PENDENT;
            case 1:
                return Result.APPROVED;
            case 2:
                return Result.REPROVED;
            default:
                return null;
        }
    }

    public enum Result {
        PENDENT,
        APPROVED,
        REPROVED
    }
}
