package br.com.avanade.fahz.model.life;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class DependentsWithPendingAnnualRenewalBody {
    @SerializedName("cpf")
    @Expose
    private String holderCpf;

    public DependentsWithPendingAnnualRenewalBody(String holderCpf) {
        this.holderCpf = holderCpf;
    }
}
