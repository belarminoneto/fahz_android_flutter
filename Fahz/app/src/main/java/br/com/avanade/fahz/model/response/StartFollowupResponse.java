package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.StartFollowup;

public class StartFollowupResponse {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("registers")
    @Expose
    private List<StartFollowup> registers = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<StartFollowup> getRegisters() {
        return registers;
    }

    public void setRegisters(List<StartFollowup> registers) {
        this.registers = registers;
    }
}
