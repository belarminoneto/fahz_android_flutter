package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.CivilState;

public class CivilStateListResponse {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("civilstates")
    @Expose
    private List<CivilState> civilstates = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<CivilState> getCivilstates() {
        return civilstates;
    }

    public void setCivilstates(List<CivilState> civilstates) {
        this.civilstates = civilstates;
    }
}
