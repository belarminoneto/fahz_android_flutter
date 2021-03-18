package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ValidateTokenRequest {
    public enum SendMethod {
        SMS,
        EMAIL,
    }

    @SerializedName("email")
    @Expose
    private String Email;
    @SerializedName("cellPhone")
    @Expose
    private String CellPhone;
    @SerializedName("sendMethod")
    @Expose
    private SendMethod sendMethod;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCellPhone() {
        return CellPhone;
    }

    public void setCellPhone(String cellPhone) {
        CellPhone = cellPhone;
    }

    public SendMethod getSendMethod() {
        return sendMethod;
    }

    public void setSendMethod(SendMethod sendMethod) {
        this.sendMethod = sendMethod;
    }
}
