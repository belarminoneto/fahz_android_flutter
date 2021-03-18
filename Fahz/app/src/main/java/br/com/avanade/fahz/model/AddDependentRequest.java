package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddDependentRequest {
    @SerializedName("DependentRelationship")
    public DependentRelationship dependentRelationship;
    @SerializedName("LifeBenefits")
    public List<LifeBenefits> lifeBenefits;

    public AddDependentRequest(DependentRelationship dependentRelationship, List<LifeBenefits> lifeBenefits) {
        this.dependentRelationship = dependentRelationship;
        this.lifeBenefits = lifeBenefits;
    }

    @Override
    public String toString() {
        return "AddDependentRequest{" +
                "dependentRelationship=" + dependentRelationship.toString() +
                '}';
    }
}
