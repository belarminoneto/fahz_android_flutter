package br.com.avanade.fahz.model.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class GetRequestBody {
    @SerializedName("cpf")
    @Expose
    private String cpf;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("top")
    @Expose
    private Integer top;
    @SerializedName("skip")
    @Expose
    private Integer skip;

    public GetRequestBody(String cpf, Integer status, Integer top, Integer skip) {
        this.cpf = cpf;
        this.status = status;
        this.top = top;
        this.skip = skip;
    }

    public GetRequestBody() {
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }
}
