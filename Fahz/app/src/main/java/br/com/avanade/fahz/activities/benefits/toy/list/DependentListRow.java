package br.com.avanade.fahz.activities.benefits.toy.list;

import android.widget.EditText;

import br.com.avanade.fahz.model.response.DependentResponseData;

public class DependentListRow implements DependentListItem {

    public DependentListRow(DependentResponseData data) {
        this.data = data;
    }

    private DependentResponseData data;

    private String justification;

    private EditText justificationEdt;

    public DependentResponseData getData() {
        return data;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public void setData(DependentResponseData data) {
        this.data = data;
    }

    public EditText getJustificationEdt() {
        return justificationEdt;
    }

    public void setJustificationEdt(EditText justificationEdt) {
        this.justificationEdt = justificationEdt;
    }
}
