package br.com.avanade.fahz.model.document;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

public class DocumentTypeBody extends CPFInBody {
    @SerializedName("manageDocumentsDefaultPage")
    @Expose
    private boolean manageDocumentsDefaultPage;

    public DocumentTypeBody(String cpf, boolean manageDocumentsDefaultPage) {
        super(cpf);
        this.manageDocumentsDefaultPage = manageDocumentsDefaultPage;
    }
}
