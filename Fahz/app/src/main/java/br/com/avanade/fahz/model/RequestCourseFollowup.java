package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestCourseFollowup {
    @SerializedName("Commited")
    @Expose
    private Boolean commited;
    @SerializedName("IdScholarship")
    @Expose
    private String idScholarship;
    @SerializedName("CanAccess")
    @Expose
    private Boolean canAccess;
    @SerializedName("CanSendNewDocument")
    @Expose
    private Boolean canSendNewDocument;
    @SerializedName("SystemBehavior")
    @Expose
    private SystemBehaviorModel systemBehavior;
    @SerializedName("Message")
    @Expose
    private String message;

    public Boolean getCommited() {
        return commited;
    }

    public void setCommited(Boolean commited) {
        this.commited = commited;
    }

    public String getIdScholarship() {
        return idScholarship;
    }

    public void setIdScholarship(String idScholarship) {
        this.idScholarship = idScholarship;
    }

    public Boolean getCanAccess() {
        return canAccess;
    }

    public void setCanAccess(Boolean canAccess) {
        this.canAccess = canAccess;
    }

    public Boolean getCanSendNewDocument() {
        return canSendNewDocument;
    }

    public void setCanSendNewDocument(Boolean canSendNewDocument) {
        this.canSendNewDocument = canSendNewDocument;
    }

    public SystemBehaviorModel getSystemBehavior() {
        return systemBehavior;
    }

    public void setSystemBehavior(SystemBehaviorModel systemBehavior) {
        this.systemBehavior = systemBehavior;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
