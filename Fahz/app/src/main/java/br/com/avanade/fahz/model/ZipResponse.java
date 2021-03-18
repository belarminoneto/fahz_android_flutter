package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class ZipResponse {
    @SerializedName("zip")
    public String zip;
    @SerializedName("street")
    public String street;
    @SerializedName("district")
    public String district;
    @SerializedName("city")
    public String city;
    @SerializedName("state")
    public String state;
}
