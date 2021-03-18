package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelBenefit {
    @SerializedName("idBenefit")
    @Expose
    private Integer idBenefit;
    @SerializedName("reasonid")
    @Expose
    private Integer reasonid;
    @SerializedName("cpf")
    @Expose
    private String cpf;

    @SerializedName("describeReasonHolder")
    @Expose
    private String describeReasonHolder;


    public Integer getIdBenefit() {
        return idBenefit;
    }

    public void setIdBenefit(Integer idBenefit) {
        this.idBenefit = idBenefit;
    }

    public Integer getReasonid() {
        return reasonid;
    }

    public void setReasonid(Integer reasonid) {
        this.reasonid = reasonid;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDescribeReasonHolder() {
        return describeReasonHolder;
    }

    public void setDescribeReasonHolder(String describeReasonHolder) {
        this.describeReasonHolder = describeReasonHolder;
    }
}
