package br.com.avanade.fahz.model.document;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

public class QueryDocumentTypeBody extends CPFInBody {
    @SerializedName("behaviorId")
    @Expose
    private Integer behaviorId;
    @SerializedName("accountReceipt")
    @Expose
    private Boolean accountReceipt;
    @SerializedName("idReasonInactivate")
    @Expose
    private Integer idReasonInactivate;

    public QueryDocumentTypeBody(String cpf, Integer behaviorId, Boolean accountReceipt, Integer idReasonInactivate) {
        super(cpf);
        this.behaviorId = behaviorId;
        this.accountReceipt = accountReceipt;
        this.idReasonInactivate = idReasonInactivate;
    }

    public QueryDocumentTypeBody(String cpf, Boolean accountReceipt) {
        super(cpf);
        this.accountReceipt = accountReceipt;
    }
}
