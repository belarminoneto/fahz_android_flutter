package br.com.avanade.fahz.model.lgpdModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class GetListOfTerms extends CheckPlatform {

    @SerializedName("Codes")
    @Expose
    private List<String> Codes = new ArrayList<String>();

    public GetListOfTerms(String platform, List<String> codes) {
        super(platform);
        Codes = codes;
    }

    public GetListOfTerms(List<String> codes) {
        Codes = codes;
    }

    public GetListOfTerms(String cpf, String platform, List<String> codes) {
        super(cpf, platform);
        Codes = codes;
    }

    public GetListOfTerms() {

    }

    public List<String> getCodes() {
        return Codes;
    }

    public void setCodes(List<String> codes) {
        Codes = codes;
    }

}