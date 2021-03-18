package br.com.avanade.fahz.model.anamnesisModel;

import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.enums.SectionPosition;

public class SectionLifeStatusAnamnesis {

    private String headerTitle;
    private SectionPosition position;
    private List<LifeStatusAnamnesis> statusAnamnesisList = new ArrayList<>();

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public List<LifeStatusAnamnesis> getStatusAnamnesisList() {
        return statusAnamnesisList;
    }

    public void setStatusAnamnesisList(List<LifeStatusAnamnesis> statusAnamnesisList) {
        this.statusAnamnesisList = statusAnamnesisList;
    }

    public SectionPosition getPosition() {
        return position;
    }

    public void setPosition(SectionPosition position) {
        this.position = position;
    }
}