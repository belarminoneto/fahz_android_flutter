package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.Schooling;

public class SchoolingReponse {

    @SerializedName("Count")
    @Expose
    private Integer count;
    @SerializedName("Registers")
    @Expose
    private List<Schooling> registers = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Schooling> getRegisters() {
        return registers;
    }

    public void setRegisters(List<Schooling> registers) {
        this.registers = registers;
    }
}
