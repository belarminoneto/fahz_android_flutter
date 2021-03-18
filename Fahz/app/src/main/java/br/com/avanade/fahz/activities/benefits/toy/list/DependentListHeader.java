package br.com.avanade.fahz.activities.benefits.toy.list;

public class DependentListHeader implements DependentListItem {

    public DependentListHeader(String observation) {
        this.observation = observation;
    }

    private String observation;

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
