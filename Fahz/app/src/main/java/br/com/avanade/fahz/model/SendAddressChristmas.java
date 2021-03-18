package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendAddressChristmas {
    @SerializedName("Id")
    @Expose
    private Object id;
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

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
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
}
