package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FinancialPlanUpdate {


    private List<FinancialPlanOperation> financialPlanOperation;
    private List<String> documents;

    public List<FinancialPlanOperation> getFinancialPlanOperation() {
        return financialPlanOperation;
    }

    public void setFinancialPlanOperation(List<FinancialPlanOperation> financialPlanOperation) {
        this.financialPlanOperation = financialPlanOperation;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

}
