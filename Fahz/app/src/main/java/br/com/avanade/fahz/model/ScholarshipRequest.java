package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScholarshipRequest {

    @SerializedName("ScholarshipLife")
    @Expose
    private ScholarshipLife ScholarshipLife;
    @SerializedName("BankData")
    @Expose
    private BankResponse BankData;
    @SerializedName("CPF")
    @Expose
    private String CPF;
    @SerializedName("Phone")
    @Expose
    private String Phone;
    @SerializedName("Email")
    @Expose
    private String Email;
    private List<String> documents;


    public br.com.avanade.fahz.model.ScholarshipLife getScholarshipLife() {
        return ScholarshipLife;
    }

    public void setScholarshipLife(br.com.avanade.fahz.model.ScholarshipLife scholarshipLife) {
        ScholarshipLife = scholarshipLife;
    }

    public BankResponse getBankData() {
        return BankData;
    }

    public void setBankData(BankResponse bankData) {
        BankData = bankData;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }
}
