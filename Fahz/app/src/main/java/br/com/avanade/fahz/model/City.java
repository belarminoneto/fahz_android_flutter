package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class City {
    @SerializedName("IBGECode")
    @Expose
    private Integer iBGECode;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("State")
    @Expose
    private String state;
    @SerializedName("CorreiosCode")
    @Expose
    private String correiosCode;

    public Integer getIBGECode() {
        return iBGECode;
    }

    public void setIBGECode(Integer iBGECode) {
        this.iBGECode = iBGECode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCorreiosCode() {
        return correiosCode;
    }

    public void setCorreiosCode(String correiosCode) {
        this.correiosCode = correiosCode;
    }
}
