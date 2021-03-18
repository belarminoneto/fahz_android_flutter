package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LifesAndDependents {

    @SerializedName("Lifes")
    @Expose
    private List<SmallLife> lifes = null;

    public List<SmallLife> getLifes() {
        return lifes;
    }

    public void setLifes(List<SmallLife> lifes) {
        this.lifes = lifes;
    }
}
