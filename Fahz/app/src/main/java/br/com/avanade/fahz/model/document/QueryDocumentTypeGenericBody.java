package br.com.avanade.fahz.model.document;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QueryDocumentTypeGenericBody extends QueryDocumentTypeBody {
    @SerializedName("manageDocumentsDefaultPage")
    @Expose
    private Boolean manageDocumentsDefaultPage;
    @SerializedName("planId")
    @Expose
    private Integer planId;

    public QueryDocumentTypeGenericBody(String cpf, Integer behaviorId, Boolean accountReceipt, Integer idReasonInactivate, Boolean manageDocumentsDefaultPage, Integer planId) {
        super(cpf, behaviorId, accountReceipt, idReasonInactivate);
        this.manageDocumentsDefaultPage = manageDocumentsDefaultPage;
        this.planId = planId;
    }
}
