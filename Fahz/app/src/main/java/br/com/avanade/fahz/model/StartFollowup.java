package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StartFollowup {

    @SerializedName("ScholarshipLife")
    @Expose
    private ScholarshipLife scholarshipLife;
    @SerializedName("DocumentsTypes")
    @Expose
    private List<DocumentType> documentsTypes = null;
    @SerializedName("SystemBehavior")
    @Expose
    private SystemBehaviorModel systemBehavior;
    @SerializedName("ReceiptDate")
    @Expose
    private Object receiptDate;
    @SerializedName("CanSendNewDocument")
    private boolean CanSendNewDocument;
    @SerializedName("ReceiptDateNeeded")
    private boolean ReceiptDateNeeded;
    @SerializedName("Message")
    private String Message;

    public ScholarshipLife getScholarshipLife() {
        return scholarshipLife;
    }

    public void setScholarshipLife(ScholarshipLife scholarshipLife) {
        this.scholarshipLife = scholarshipLife;
    }

    public List<DocumentType> getDocumentsType() {
        return documentsTypes;
    }

    public void setDocumentsType(List<DocumentType> documentsType) {
        this.documentsTypes = documentsType;
    }

    public SystemBehaviorModel getSystemBehavior() {
        return systemBehavior;
    }

    public void setSystemBehavior(SystemBehaviorModel systemBehavior) {
        this.systemBehavior = systemBehavior;
    }

    public Object getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Object receiptDate) {
        this.receiptDate = receiptDate;
    }

    public boolean isCanSendNewDocument() {
        return CanSendNewDocument;
    }

    public void setCanSendNewDocument(boolean canSendNewDocument) {
        CanSendNewDocument = canSendNewDocument;
    }

    public boolean isReceiptDateNeeded() {
        return ReceiptDateNeeded;
    }

    public void setReceiptDateNeeded(boolean receiptDateNeeded) {
        ReceiptDateNeeded = receiptDateNeeded;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
