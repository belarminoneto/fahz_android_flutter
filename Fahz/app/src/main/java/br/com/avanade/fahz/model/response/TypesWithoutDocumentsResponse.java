package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.Documents;

public class TypesWithoutDocumentsResponse {

    @SerializedName("count")
    private Integer count;

    @SerializedName("documentTypes")
    private List<DocumentType> documentTypes;

    public Integer getCount() {
        return count;
    }

    public List<DocumentType> getDocumentTypes() {
        return documentTypes;
    }

    public class DocumentType {
        @SerializedName("Id")
        private Integer id;
        @SerializedName("Description")
        private String description;
        @SerializedName("Documents")
        private List<Documents> documents;
        @SerializedName("UserHasIt")
        private Boolean userHasIt;

        public Integer getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public Boolean getUserHasIt() {
            return userHasIt;
        }

        public List<Documents> getDocuments() {
            return documents;
        }
    }
}
