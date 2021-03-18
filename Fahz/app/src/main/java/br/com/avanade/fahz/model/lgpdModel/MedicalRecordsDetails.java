package br.com.avanade.fahz.model.lgpdModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

@SuppressWarnings("unused")
public class MedicalRecordsDetails extends CPFInBody {

    @SerializedName("id")
    @Expose
    private String id;

    public MedicalRecordsDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MedicalRecordsDetails(String cpf, String id) {
        super(cpf);
        this.id = id;
    }

    public MedicalRecordsDetails(String id) {
        this.id = id;
    }
}