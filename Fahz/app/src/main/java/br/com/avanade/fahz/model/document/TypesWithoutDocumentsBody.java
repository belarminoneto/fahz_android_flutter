package br.com.avanade.fahz.model.document;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

public class TypesWithoutDocumentsBody extends CPFInBody {
    @SerializedName("behaviorId")
    @Expose
    private Integer behaviorId;
    @SerializedName("accountReceipt")
    @Expose
    private boolean accountReceipt;

    public TypesWithoutDocumentsBody(String cpf, Integer behaviorId, boolean accountReceipt) {
        super(cpf);
        this.behaviorId = behaviorId;
        this.accountReceipt = accountReceipt;
    }
}
