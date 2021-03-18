package br.com.avanade.fahz.model.invoicehandling;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListExtractUsageBody {
    @SerializedName("CPF")
    @Expose
    private String cpf;
    @SerializedName("UserCPF")
    @Expose
    private String userCPF;
    @SerializedName("StartDate")
    @Expose
    private String startDate;
    @SerializedName("EndDate")
    @Expose
    private String endDate;
    @SerializedName("UsageEntrySearchEnum")
    @Expose
    private int entry;

    public ListExtractUsageBody(String cpf, String userCPF, String startDate, String endDate, int entry) {
        this.cpf = cpf;
        this.userCPF = userCPF;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entry = entry;
    }
}
