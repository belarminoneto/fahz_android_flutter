package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FollowupScholarship {
    @SerializedName("ScholarshipLife")
    @Expose
    private ScholarshipLife scholarshipLife;
    @SerializedName("SystemBehavior")
    @Expose
    private SystemBehaviorModel systemBehavior;
    @SerializedName("ReceiptDate")
    @Expose
    private String receiptDate;

    public int indexSelected;
    public boolean receiptDateNeeded;
    public String period;
    public String cpf;


    public ScholarshipLife getScholarshipLife() {
        return scholarshipLife;
    }

    public void setScholarshipLife(ScholarshipLife scholarshipLife) {
        this.scholarshipLife = scholarshipLife;
    }

    public SystemBehaviorModel getSystemBehavior() {
        return systemBehavior;
    }

    public void setSystemBehavior(SystemBehaviorModel systemBehavior) {
        this.systemBehavior = systemBehavior;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }
}
