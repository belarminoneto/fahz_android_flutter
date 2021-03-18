package br.com.avanade.fahz.model.response;

import android.location.Address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.AddressResponse;
import br.com.avanade.fahz.model.Company;
import br.com.avanade.fahz.model.Plan;
import br.com.avanade.fahz.model.Subsidiary;
import br.com.avanade.fahz.model.WorkspaceResponse;

public class XmasStartChoose {
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("CPF")
    @Expose
    private String cPF;
    @SerializedName("Plan")
    @Expose
    private Plan plan;
    @SerializedName("Workspace")
    @Expose
    private WorkspaceResponse workspace;
    @SerializedName("Subsidiary")
    @Expose
    private Subsidiary subsidiary;
    @SerializedName("Company")
    @Expose
    private Company company;
    @SerializedName("Address")
    @Expose
    private AddressResponse address;
    @SerializedName("Type")
    @Expose
    private Integer type;
    @SerializedName("HasAvailablecampaign")
    @Expose
    private Boolean hasAvailablecampaign;
    @SerializedName("CanChooseAddress")
    @Expose
    private Boolean canChooseAddress;
    @SerializedName("CanChooseWorkspace")
    @Expose
    private Boolean canChooseWorkspace;
    @SerializedName("ReasonNote")
    @Expose
    private String reasonNote;
    @SerializedName("Editable")
    @Expose
    private Boolean editable;
    @SerializedName("Date")
    @Expose
    private String date;
    @SerializedName("RulesCampaign")
    @Expose
    private String rulesCampaign;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCPF() {
        return cPF;
    }

    public void setCPF(String cPF) {
        this.cPF = cPF;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public WorkspaceResponse getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceResponse workspace) {
        this.workspace = workspace;
    }

    public Subsidiary getSubsidiary() {
        return subsidiary;
    }

    public void setSubsidiary(Subsidiary subsidiary) {
        this.subsidiary = subsidiary;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public AddressResponse getAddress() {
        return address;
    }

    public void setAddress(AddressResponse address) {
        this.address = address;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getHasAvailablecampaign() {
        return hasAvailablecampaign;
    }

    public void setHasAvailablecampaign(Boolean hasAvailablecampaign) {
        this.hasAvailablecampaign = hasAvailablecampaign;
    }

    public Boolean getCanChooseAddress() {
        return canChooseAddress;
    }

    public void setCanChooseAddress(Boolean canChooseAddress) {
        this.canChooseAddress = canChooseAddress;
    }

    public Boolean getCanChooseWorkspace() {
        return canChooseWorkspace;
    }

    public void setCanChooseWorkspace(Boolean canChooseWorkspace) {
        this.canChooseWorkspace = canChooseWorkspace;
    }

    public String getReasonNote() {
        return reasonNote;
    }

    public void setReasonNote(String reasonNote) {
        this.reasonNote = reasonNote;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRulesCampaign() {
        return rulesCampaign;
    }

    public void setRulesCampaign(String rulesCampaign) {
        this.rulesCampaign = rulesCampaign;
    }
}
