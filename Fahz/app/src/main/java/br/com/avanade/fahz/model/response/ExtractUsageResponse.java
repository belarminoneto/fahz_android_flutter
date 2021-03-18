package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.RegisterEntry;

public class ExtractUsageResponse {

    @SerializedName("Count")
    @Expose
    private Integer count;

    @SerializedName("commited")
    @Expose
    private Boolean commited;

    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    @SerializedName("TotalValue")
    @Expose
    private Double totalValue;

    @SerializedName("TotalCopartValue")
    @Expose
    private Double totalCopartValue;

    @SerializedName("Registers")
    @Expose
    private List<RegisterEntry> registers = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<RegisterEntry> getRegisters() {
        return registers;
    }

    public void setRegisters(List<RegisterEntry> registers) {
        this.registers = registers;
    }

    public boolean hasTotal() {
        boolean hasTotal = false;
        if (this.totalValue != null && this.totalValue > 0 && !this.totalValue.isNaN()
                && this.totalCopartValue != null && this.totalCopartValue > 0 && !this.totalCopartValue.isNaN())
            hasTotal = true;
        return hasTotal;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public Double getTotalCopartValue() {
        return totalCopartValue;
    }

    public boolean getCommited() {
        return commited == null || commited;
    }

    public String getMessage() {
        return !getCommited() ? message : "";
    }
}
