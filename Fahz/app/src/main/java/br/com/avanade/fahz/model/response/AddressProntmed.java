package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressProntmed {
    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("Name")
    @Expose
    private Object name;
    @SerializedName("StrAddress")
    @Expose
    private Object strAddress;
    @SerializedName("Number")
    @Expose
    private String number;
    @SerializedName("Logradouro")
    @Expose
    private String logradouro;
    @SerializedName("Complement")
    @Expose
    private String complement;
    @SerializedName("ZipCode")
    @Expose
    private String zipCode;
    @SerializedName("Neighborhood")
    @Expose
    private String neighborhood;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("State")
    @Expose
    private String state;
    @SerializedName("Country")
    @Expose
    private String country;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getStrAddress() {
        return strAddress;
    }

    public void setStrAddress(Object strAddress) {
        this.strAddress = strAddress;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
