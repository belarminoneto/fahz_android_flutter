package br.com.avanade.fahz.model.benefits;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

public class OperatorsByBenefitBody extends CPFInBody {
    @SerializedName("idBenefit")
    @Expose
    private int idBenefit;

    public OperatorsByBenefitBody(String cpf, int idBenefit) {
        super(cpf);
        this.idBenefit = idBenefit;
    }
}
