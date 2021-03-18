package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.RequestNewPersonalCard;

public class RequestNewPersonalCardResponse {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("registers")
    @Expose
    private List<RequestNewPersonalCard> registers = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<RequestNewPersonalCard> getRegisters() {
        return registers;
    }

    public void setRegisters(List<RequestNewPersonalCard> registers) {
        this.registers = registers;
    }
}
