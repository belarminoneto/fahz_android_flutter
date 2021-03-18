package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RequestStep implements Serializable {

    @SerializedName("DescriptionFlow")
    @Expose
    private String description;

    @SerializedName("Success")
    @Expose
    private Boolean success;

    @SerializedName("PassedOn")
    @Expose
    private Boolean passedOn;

    @SerializedName("IsLast")
    @Expose
    private Boolean isLast;

    @SerializedName("Date")
    @Expose
    private String date;

    @SerializedName("StepNumber")
    @Expose
    private Integer stepNumber;

    public RequestStep(String description, Boolean success, Boolean passedOn, Boolean isLast, String date, Integer stepNumber) {
        this.description = description;
        this.success = success;
        this.passedOn = passedOn;
        this.isLast = isLast;
        this.date = date;
        this.stepNumber = stepNumber;
    }

    public String getDescription() {
        return description;
    }

    public Boolean isSuccess() {
        return success;
    }

    public Boolean isPassedOn() {
        return passedOn;
    }

    public Boolean isLast() {
        return isLast;
    }

    public String getDate() {
        return date;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }
}
