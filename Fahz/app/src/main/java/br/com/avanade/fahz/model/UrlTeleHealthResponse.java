package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class UrlTeleHealthResponse {
    @SerializedName("commit")
    public boolean commit;
    @SerializedName("messageIdentifier")
    public String messageIdentifier;
    @SerializedName("url")
    public String url;
    @SerializedName("redirectAlterEmail")
    public boolean redirectAlterEmail;

}
