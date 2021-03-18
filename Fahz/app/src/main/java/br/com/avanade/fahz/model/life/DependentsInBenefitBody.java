package br.com.avanade.fahz.model.life;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

public class DependentsInBenefitBody extends CPFInBody {
    @SerializedName("benefit")
    @Expose
    private int benefit;

    public DependentsInBenefitBody(String cpf, int benefit) {
        super(cpf);
        this.benefit = benefit;
    }
}
