package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.WorkspaceResponse;

public class WorksapceXmasResponse {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("registers")
    @Expose
    private List<WorkspaceResponse> registers = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<WorkspaceResponse> getRegisters() {
        return registers;
    }

    public void setRegisters(List<WorkspaceResponse> registers) {
        this.registers = registers;
    }
}
