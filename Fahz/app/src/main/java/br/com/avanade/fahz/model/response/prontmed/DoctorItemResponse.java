package br.com.avanade.fahz.model.response.prontmed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.response.AddressProntmed;

public class DoctorItemResponse {

    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Council")
    @Expose
    private String council;
    @SerializedName("CouncilNumber")
    @Expose
    private String councilNumber;
    @SerializedName("CouncilState")
    @Expose
    private String councilState;
    @SerializedName("Specialties")
    @Expose
    private List<String> specialties = null;
    @SerializedName("daysOfWeek")
    @Expose
    private List<Integer> daysOfWeek = null;
    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("Address")
    @Expose
    private AddressProntmed address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCouncil() {
        return council;
    }

    public void setCouncil(String council) {
        this.council = council;
    }

    public String getCouncilNumber() {
        return councilNumber;
    }

    public void setCouncilNumber(String councilNumber) {
        this.councilNumber = councilNumber;
    }

    public String getCouncilState() {
        return councilState;
    }

    public void setCouncilState(String councilState) {
        this.councilState = councilState;
    }

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AddressProntmed getAddress() {
        return address;
    }

    public void setAddress(AddressProntmed address) {
        this.address = address;
    }
}
