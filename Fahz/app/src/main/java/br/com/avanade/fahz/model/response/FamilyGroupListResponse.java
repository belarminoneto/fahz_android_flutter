package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FamilyGroupListResponse {

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("lives")
    @Expose
    private List<FamilyGroupItemResponse> lives;

    public int getCount() {
        return count;
    }
    public List<FamilyGroupItemResponse> getLives() {
        return lives;
    }
}
