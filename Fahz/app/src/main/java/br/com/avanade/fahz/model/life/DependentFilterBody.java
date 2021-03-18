package br.com.avanade.fahz.model.life;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

public class DependentFilterBody extends CPFInBody {
    @SerializedName("statusFilter")
    @Expose
    private int statusFilter;

    public DependentFilterBody(String cpf, int statusFilter) {
        super(cpf);
        this.statusFilter = statusFilter;
    }
}
