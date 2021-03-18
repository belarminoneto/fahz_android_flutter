package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import br.com.avanade.fahz.model.lgpdModel.AcceptData;
import br.com.avanade.fahz.model.lgpdModel.NotificationAnswer;

public class SignupRequest implements Serializable {
    @SerializedName("cpf")
    private String cpf;
    @SerializedName("password")
    private String password;
    @SerializedName("email")
    private String email;
    @SerializedName("cellphone")
    private String cellphone;
    @SerializedName("sendMethod")
    private ValidateTokenRequest.SendMethod sendMethod;
	@SerializedName("politicaPrivacidade")
    private Boolean politicaPrivacidade;
    @SerializedName("politicaDados")
    private Boolean politicaDados;
    @SerializedName("NotificationAnswer")
    @Expose
    public List<NotificationAnswer> notificationAnswer;

    @SerializedName("AcceptData")
    @Expose
    public List<AcceptData> acceptData;

    @SerializedName("date")
    private String date;

    public SignupRequest(String cpf, String password, String email, String cellphone,
                         ValidateTokenRequest.SendMethod sendMethod, Boolean politicaPrivacidade, Boolean politicaDados,
                         List<NotificationAnswer> notificationAnswer, List<AcceptData> acceptData, String date) {
        this.cpf = cpf;
        this.password = password;
        this.email = email;
        this.cellphone = cellphone;
        this.sendMethod = sendMethod;
        this.politicaPrivacidade = politicaPrivacidade;
        this.politicaDados = politicaDados;
        this.notificationAnswer = notificationAnswer;
        this.acceptData = acceptData;
        this.date = date;
    }
}
