package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchAuxiliaryDataResponse {

    @SerializedName("tipo")
    private int type;

    @SerializedName("exibir")
    private int show;

    @SerializedName("enviar")
    private int send;

    @SerializedName("excedeuLimite")
    private int exceededLimit;

    @SerializedName("campos")
    private List<String> fields;

    @SerializedName("registros")
    private List<DataRecord> dataRecords;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    public int getSend() {
        return send;
    }

    public void setSend(int send) {
        this.send = send;
    }

    public int getExceededLimit() {
        return exceededLimit;
    }

    public void setExceededLimit(int exceededLimit) {
        this.exceededLimit = exceededLimit;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<DataRecord> getDataRecords() {
        return dataRecords;
    }

    public void setDataRecords(List<DataRecord> dataRecords) {
        this.dataRecords = dataRecords;
    }
}