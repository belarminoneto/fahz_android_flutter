package br.com.avanade.fahz.model.lgpdModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

@SuppressWarnings("unused")
public class CheckPlatform extends CPFInBody {

    @SerializedName("Platform")
    @Expose
    private String Platform;

    public CheckPlatform(String platform) {
        Platform = platform;
    }

    public CheckPlatform() {
    }

    public CheckPlatform(String cpf, String platform) {
        super(cpf);
        Platform = platform;
    }

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String platform) {
        Platform = platform;
    }


}