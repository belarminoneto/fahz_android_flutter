package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegisterEntry {

    @SerializedName("IndexID")
    @Expose
    private Integer indexID;
    @SerializedName("DataAtendimento")
    @Expose
    private String dataAtendimento;
    @SerializedName("NomePrestador")
    @Expose
    private String nomePrestador;
    @SerializedName("UsageEntries")
    @Expose
    private List<UsageEntry> usageEntries = null;

    public Integer getIndexID() {
        return indexID;
    }

    public void setIndexID(Integer indexID) {
        this.indexID = indexID;
    }

    public String getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(String dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public String getNomePrestador() {
        return nomePrestador;
    }

    public void setNomePrestador(String nomePrestador) {
        this.nomePrestador = nomePrestador;
    }

    public List<UsageEntry> getUsageEntries() {
        return usageEntries;
    }

    public void setUsageEntries(List<UsageEntry> usageEntries) {
        this.usageEntries = usageEntries;
    }

}
