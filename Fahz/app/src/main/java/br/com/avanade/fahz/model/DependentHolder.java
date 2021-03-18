package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DependentHolder {
    @SerializedName("count")
    public int count;
    @SerializedName("dependants")
    public List<Dependent> dependents;

    @Override
    public String toString() {
        return "DependentHolder{" +
                "count=" + count +
                ", dependents=" + dependents +
                '}';
    }
}
