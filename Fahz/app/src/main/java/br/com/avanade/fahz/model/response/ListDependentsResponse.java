package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListDependentsResponse {

    @SerializedName("count")
    @Expose
    private Integer count;

    @SerializedName("registers")
    @Expose
    private List<DependentResponseData> registers;

    public Integer getCount() {
        return count;
    }

    public List<DependentResponseData> getRegisters() {
        return registers;
    }


}
