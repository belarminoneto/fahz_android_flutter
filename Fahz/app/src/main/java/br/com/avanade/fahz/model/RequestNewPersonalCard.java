package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestNewPersonalCard {
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("CPF")
    @Expose
    private String cPF;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("RelationshipDegree")
    @Expose
    private Kinship relationshipDegree;
    @SerializedName("ReasonRequestPersonalCard")
    @Expose
    private ReasonRequestPersonalCard reasonRequestPersonalCard;
    @SerializedName("RequestDate")
    @Expose
    private String requestDate;
    @SerializedName("IsHolder")
    @Expose
    private Boolean isHolder;
    @SerializedName("CanRequest")
    @Expose
    private Boolean canRequest;

    public int indexSelected;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Kinship getRelationshipDegree() {
        return relationshipDegree;
    }

    public void setRelationshipDegree(Kinship relationshipDegree) {
        this.relationshipDegree = relationshipDegree;
    }

    public ReasonRequestPersonalCard getReasonRequestPersonalCard() {
        return reasonRequestPersonalCard;
    }

    public void setReasonRequestPersonalCard(ReasonRequestPersonalCard reasonRequestPersonalCard) {
        this.reasonRequestPersonalCard = reasonRequestPersonalCard;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public Boolean getIsHolder() {
        return isHolder;
    }

    public void setIsHolder(Boolean isHolder) {
        this.isHolder = isHolder;
    }

    public Boolean getCanRequest() {
        return canRequest;
    }

    public void setCanRequest(Boolean canRequest) {
        this.canRequest = canRequest;
    }

}
