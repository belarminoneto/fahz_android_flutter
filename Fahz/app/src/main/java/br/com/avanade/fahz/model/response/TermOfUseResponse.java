package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.SerializedName;


public class TermOfUseResponse {
    @SerializedName("Code")
    public String code;
    @SerializedName("lastVersion")
    public String lastVersion;
    @SerializedName("Text")
    public String Text;
    @SerializedName("InitialDate")
    public String InitialDate;
    @SerializedName("FinalDate")
    public String FinalDate;
    @SerializedName("FilePath")
    public String filePath;
    @SerializedName("Title")
    public String title;
}
