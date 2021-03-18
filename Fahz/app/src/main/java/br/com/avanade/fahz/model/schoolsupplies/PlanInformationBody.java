package br.com.avanade.fahz.model.schoolsupplies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

@SuppressWarnings("unused")
public class PlanInformationBody extends CPFInBody {
    @SerializedName("planId")
    @Expose
    private Integer planId;

    public PlanInformationBody(String cpf, Integer planId) {
        super(cpf);
        this.planId = planId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }
}
