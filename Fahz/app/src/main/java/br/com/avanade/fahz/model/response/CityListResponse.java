package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.City;

public class CityListResponse {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("registers")
    @Expose
    private List<City> registers = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<City> getRegisters() {
        return registers;
    }

    public void setRegisters(List<City> registers) {
        this.registers = registers;
    }
}
