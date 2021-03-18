package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SendReceiptRequest {
    @SerializedName("BenefitSchoolMaterial")
    @Expose
    private List<BenefitSchoolMaterial> benefitSchoolMaterial = null;
    @SerializedName("Documents")
    @Expose
    private List<String> documents = null;

    public List<BenefitSchoolMaterial> getBenefitSchoolMaterial() {
        return benefitSchoolMaterial;
    }

    public void setBenefitSchoolMaterial(List<BenefitSchoolMaterial> benefitSchoolMaterial) {
        this.benefitSchoolMaterial = benefitSchoolMaterial;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }
}
