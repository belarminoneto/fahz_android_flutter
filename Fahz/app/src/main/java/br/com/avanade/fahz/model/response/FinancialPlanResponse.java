package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.FinancialPlanList;

public class FinancialPlanResponse {

    @SerializedName("hasDependente")
    @Expose
    private Boolean hasDependente;
    @SerializedName("financialPlanList")
    @Expose
    private List<List<FinancialPlanList>> financialPlanList = null;

    public Boolean getHasDependente() {
        return hasDependente;
    }

    public void setHasDependente(Boolean hasDependente) {
        this.hasDependente = hasDependente;
    }

    public List<List<FinancialPlanList>> getFinancialPlanList() {
        return financialPlanList;
    }

    public void setFinancialPlanList(List<List<FinancialPlanList>> financialPlanList) {
        this.financialPlanList = financialPlanList;
    }
}
