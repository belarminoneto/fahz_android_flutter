package br.com.avanade.fahz.model.lgpdModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrivacyControlResponse
{

    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    @SerializedName("PrivacyControl")
    @Expose
    private PrivacyControl privacyControl;

    public String getMessage() {
        return message;
    }

    public boolean hasMessage(){
        return message!=null;
    }

    public PrivacyControl getPrivacyControl() {
        return privacyControl;
    }
}
