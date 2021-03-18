package br.com.avanade.fahz.model.response.prontmed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DoctorListResponse {

    @SerializedName("commited")
    @Expose
    private Boolean commited;

    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    @SerializedName("doctors")
    @Expose
    private List<DoctorItemResponse> doctors;

    @SerializedName("count")
    @Expose
    private Integer count;

    public List<DoctorItemResponse> getDoctors() {
        return doctors;
    }

    public Integer getCount() {
        return count;
    }
    public boolean isCommited() {
        return commited != null ? commited : true;
    }
    public String getMessage() {
        return message != null && !message.isEmpty() ? message : "";
    }
}
