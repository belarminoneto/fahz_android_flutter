package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SmallLife implements Serializable{
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Cpf")
    @Expose
    private String cpf;
    @SerializedName("Kinship")
    @Expose
    private String kinship;
    @SerializedName("Gender")
    @Expose
    private String Gender;
    @SerializedName("DateOfBirth")
    @Expose
    private String DateOfBirth;

    public SmallLife() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getKinship() {
        return kinship;
    }

    public void setKinship(String kinship) {
        this.kinship = kinship;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

}
