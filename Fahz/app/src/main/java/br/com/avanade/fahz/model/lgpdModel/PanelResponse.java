package br.com.avanade.fahz.model.lgpdModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PanelResponse {

    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    @SerializedName("panels")
    @Expose
    private Panel panel;

    public String getMessage() {
        return message;
    }

    public boolean hasMessage(){
        return message!=null;
    }

    public Panel getPanel() {
        return panel;
    }
}