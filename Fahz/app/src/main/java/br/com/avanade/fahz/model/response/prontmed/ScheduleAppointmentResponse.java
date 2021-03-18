package br.com.avanade.fahz.model.response.prontmed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScheduleAppointmentResponse {

    @SerializedName("commited")
    @Expose
    private Boolean commited;

    @SerializedName("messageIdentifier")
    @Expose
    private String messageIdentifier;

    public Boolean isCommited() {
        return commited != null ? commited : true;
    }
    public String getMessage() {
        return messageIdentifier != null ? messageIdentifier : "";
    }
}
