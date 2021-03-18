package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.TermsByUser;

public class TermsByUserResponse {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("registers")
    @Expose
    private List<TermsByUser> registers = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<TermsByUser> getRegisters() {
        return registers;
    }

    public void setRegisters(List<TermsByUser> registers) {
        this.registers = registers;
    }
}
