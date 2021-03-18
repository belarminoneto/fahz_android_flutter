package br.com.avanade.fahz.model.response.prontmed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpecialityListResponse {

    @SerializedName("count")
    @Expose
    private Integer count;

    @SerializedName("specialities")
    @Expose
    private List<SpecialityResponse> list;

    public Integer getCount() {
        return count;
    }
    public List<SpecialityResponse> getList() {
        return list;
    }
}
