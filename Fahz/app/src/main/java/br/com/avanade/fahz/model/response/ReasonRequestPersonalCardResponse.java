package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.ReasonRequestPersonalCard;

public class ReasonRequestPersonalCardResponse {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("reasons")
    @Expose
    private List<ReasonRequestPersonalCard> reasons = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<ReasonRequestPersonalCard> getReasons() {
        return reasons;
    }

    public void setReasons(List<ReasonRequestPersonalCard> reasons) {
        this.reasons = reasons;
    }
}
