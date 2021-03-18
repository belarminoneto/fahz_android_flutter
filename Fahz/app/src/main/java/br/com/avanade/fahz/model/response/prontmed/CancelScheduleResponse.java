package br.com.avanade.fahz.model.response.prontmed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelScheduleResponse {

    @SerializedName("commited")
    @Expose
    private Boolean commited;

    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    public Boolean getCommited() {
        return commited;
    }
    public String getMessage() {
        return message;
    }
}
