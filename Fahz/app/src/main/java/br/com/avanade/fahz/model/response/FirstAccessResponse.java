package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FirstAccessResponse {

    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("BirthDate")
    @Expose
    private String birthDate;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("CellPhone")
    @Expose
    private String cellPhone;
    @SerializedName("ShowCommunicationsAndPolicies")
    @Expose
    private Boolean showCommunicationsAndPolicies;

    public Boolean getShowCommunicationsAndPolicies() {
        return showCommunicationsAndPolicies;
    }

    public void setShowCommunicationsAndPolicies(Boolean showCommunicationsAndPolicies) {
        this.showCommunicationsAndPolicies = showCommunicationsAndPolicies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }
}
