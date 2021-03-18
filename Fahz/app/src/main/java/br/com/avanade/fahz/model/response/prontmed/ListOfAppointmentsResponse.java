package br.com.avanade.fahz.model.response.prontmed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListOfAppointmentsResponse {

    @SerializedName("commited")
    @Expose
    private Boolean commited;

    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    @SerializedName("count")
    @Expose
    private Integer count;

    @SerializedName("patients")
    @Expose
    private List<ListOfAppointmentsItemResponse> list;

    public Integer getCount() {
        return count;
    }
    public List<ListOfAppointmentsItemResponse> getList() {
        return list;
    }
    public Boolean isCommited() {
        return commited != null ? commited : true;
    }
    public String getMessage() {
        return message != null ? message : "";
    }
}
