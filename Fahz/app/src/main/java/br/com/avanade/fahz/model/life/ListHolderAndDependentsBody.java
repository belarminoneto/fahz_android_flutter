package br.com.avanade.fahz.model.life;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListHolderAndDependentsBody {
    @SerializedName("cpfHolder")
    @Expose
    private String cpf;
    @SerializedName("showDependentMajor")
    @Expose
    private boolean showDependentMajor;

    public ListHolderAndDependentsBody(String cpf, boolean showDependentMajor) {
        this.cpf = cpf;
        this.showDependentMajor = showDependentMajor;
    }
}
