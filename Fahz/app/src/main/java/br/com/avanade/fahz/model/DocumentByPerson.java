package br.com.avanade.fahz.model;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DocumentByPerson {

    private ArrayList<DocumentType> documentsType;
    private String CPF;
    private RecyclerView mDocumentTypesRecyclerView;

    public ArrayList<DocumentType> getDocumentsType() {
        return documentsType;
    }

    public void setDocumentsType(ArrayList<DocumentType> documentsType) {
        this.documentsType = documentsType;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public RecyclerView getmDocumentTypesRecyclerView() {
        return mDocumentTypesRecyclerView;
    }

    public void setmDocumentTypesRecyclerView(RecyclerView mDocumentTypesRecyclerView) {
        this.mDocumentTypesRecyclerView = mDocumentTypesRecyclerView;
    }

    public DocumentByPerson(ArrayList<DocumentType> documentsType, String CPF, RecyclerView mDocumentTypesRecyclerView) {
        this.documentsType = documentsType;
        this.CPF = CPF;
        this.mDocumentTypesRecyclerView = mDocumentTypesRecyclerView;
    }
}
