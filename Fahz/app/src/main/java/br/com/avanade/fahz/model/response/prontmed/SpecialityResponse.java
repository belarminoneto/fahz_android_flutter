package br.com.avanade.fahz.model.response.prontmed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpecialityResponse {

    @SerializedName("Id")
    @Expose
    private Long id;

    @SerializedName("Speciality")
    @Expose
    private String description;

    public SpecialityResponse(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public Long getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    @Override
    public String toString() {
        return description;
    }
}
