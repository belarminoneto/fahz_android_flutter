package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class LoginAnamnesisResponse {

    @SerializedName("accessToken")
    private String token;

    private int environment;

    @SerializedName("pendencias")
    private int pendencies;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getEnvironment() {
        return environment;
    }

    public void setEnvironment(int environment) {
        this.environment = environment;
    }

    public int getPendencies() {
        return pendencies;
    }

    public void setPendencies(int pendencies) {
        this.pendencies = pendencies;
    }
}
