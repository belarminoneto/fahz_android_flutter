package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.Discharge;

public class DischargesListResponse {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("discharges")
    @Expose
    private List<Discharge> discharges = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Discharge> getDischarges() {
        return discharges;
    }

    public void setDischarges(List<Discharge> discharges) {
        this.discharges = discharges;
    }
}
