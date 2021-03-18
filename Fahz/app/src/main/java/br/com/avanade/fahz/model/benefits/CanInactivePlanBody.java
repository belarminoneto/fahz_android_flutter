package br.com.avanade.fahz.model.benefits;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

@SuppressWarnings("unused")
public class CanInactivePlanBody extends CPFInBody {
    @SerializedName("idBenefit")
    @Expose
    private int idBenefit;
    @SerializedName("isHolder")
    @Expose
    private boolean isHolder;

    public CanInactivePlanBody(String cpf, int idBenefit, boolean isHolder) {
        super(cpf);
        this.idBenefit = idBenefit;
        this.isHolder = isHolder;
    }

    public int getIdBenefit() {
        return idBenefit;
    }

    public void setIdBenefit(int idBenefit) {
        this.idBenefit = idBenefit;
    }

    public boolean isHolder() {
        return isHolder;
    }

    public void setHolder(boolean holder) {
        isHolder = holder;
    }
}
