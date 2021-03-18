package br.com.avanade.fahz.model.anamnesisModel;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.BuildConfig;
import br.com.avanade.fahz.R;

public class LoginAnamnesis {

    public LoginAnamnesis(String userCPF, Context context) {
        this.cpf = BuildConfig.API_USER;
        this.accessKey = BuildConfig.API_KEY;
        this.userCPF = userCPF;
    }

    @SerializedName("CPF")
    private String cpf;

    @SerializedName("AccessKey")
    private String accessKey;

    @SerializedName("Usuario")
    private String userCPF;

    public String getCpf() {
        return cpf;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getUserCPF() {
        return userCPF;
    }

    public void setUserCPF(String userCPF) {
        this.userCPF = userCPF;
    }
}
