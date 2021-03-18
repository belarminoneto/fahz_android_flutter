package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.Kinship;

public class KinshipListResponse {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("kinship")
    @Expose
    private List<Kinship> kinship = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Kinship> getKinship() {
        return kinship;
    }

    public void setKinship(List<Kinship> kinship) {
        this.kinship = kinship;
    }
}
