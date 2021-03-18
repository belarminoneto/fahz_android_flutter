package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SendAnnualRenewalDocumentsForApprovalRequest {

    @SerializedName("CPF")
    private String cpf;

    @SerializedName("Year")
    private String year;

    @SerializedName("Documents")
    private List<String> documentsIds;

    public SendAnnualRenewalDocumentsForApprovalRequest(String cpf, String year, List<String> documentsIds) {
        this.cpf = cpf;
        this.year = year;
        this.documentsIds = documentsIds;
    }

    public String getCpf() {
        return cpf;
    }

}
