package br.com.avanade.fahz.model.life;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

public class DependentHolderBody extends CPFInBody {
    @SerializedName("actives")
    @Expose
    private boolean actives;

    public DependentHolderBody(String cpf, boolean actives) {
        super(cpf);
        this.actives = actives;
    }
}
