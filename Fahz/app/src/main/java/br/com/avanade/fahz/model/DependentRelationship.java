package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class DependentRelationship {
    @SerializedName("AccountHolder")
    public AccountHolder accountHolder;
    @SerializedName("Dependent")
    public DependentRequest dependent;
    @SerializedName("RelationshipDegree")
    public Integer relationshipDegree;

    public DependentRelationship(AccountHolder accountHolder, DependentRequest dependent, Integer relationshipDegree) {
        this.accountHolder = accountHolder;
        this.dependent = dependent;
        this.relationshipDegree = relationshipDegree;
    }

    @Override
    public String toString() {
        return "DependentRelationship{" +
                "accountHolder=" + accountHolder +
                ", dependent=" + dependent +
                ", relationshipDegree=" + relationshipDegree +
                '}';
    }
}
