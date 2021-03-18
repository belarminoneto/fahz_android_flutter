package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.OperatorByBenefit;

public class OperatorsResponse {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("registers")
    @Expose
    private List<OperatorByBenefit> operatorByBenefits = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<OperatorByBenefit> getOperatorByBenefits() {
        return operatorByBenefits;
    }

    public void setOperatorByBenefits(List<OperatorByBenefit> registers) {
        this.operatorByBenefits = registers;
    }
}
