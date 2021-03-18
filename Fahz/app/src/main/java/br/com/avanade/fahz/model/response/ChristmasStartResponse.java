package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChristmasStartResponse {
    @SerializedName("commited")
    @Expose
    private Boolean commited;
    @SerializedName("xmasStartChoose")
    @Expose
    private XmasStartChoose xmasStartChoose;

    public Boolean getCommited() {
        return commited;
    }

    public void setCommited(Boolean commited) {
        this.commited = commited;
    }

    public XmasStartChoose getXmasStartChoose() {
        return xmasStartChoose;
    }

    public void setXmasStartChoose(XmasStartChoose xmasStartChoose) {
        this.xmasStartChoose = xmasStartChoose;
    }
}
