package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import br.com.avanade.fahz.model.Bank;

public class ValidateToken {
    @SerializedName("statusTokenValidation")
    @Expose
    private Integer statusTokenValidation ;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("expireDate")
    @Expose
    private String expireDate;
    @SerializedName("cellphone")
    @Expose
    private String cellphone ;

    public Integer getStatusTokenValidation() {
        return statusTokenValidation;
    }

    public void setStatusTokenValidation(Integer statusTokenValidation) {
        this.statusTokenValidation = statusTokenValidation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }
}
