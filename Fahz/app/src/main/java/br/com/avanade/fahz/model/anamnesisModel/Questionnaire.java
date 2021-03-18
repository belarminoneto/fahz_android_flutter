package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.util.AnamnesisUtils;

public class Questionnaire {

    public Questionnaire() {
    }

    public Questionnaire(long id, int idType) {
        this.id = id;
        this.idType = idType;
    }

    private long id;

    @SerializedName("idtipo")
    private int idType;

    @SerializedName("tipo")
    private String type;

    @SerializedName("descricao")
    private String desc;

    @SerializedName("numeracao")
    private int isCountable;

    @SerializedName("ativo")
    private int active;

    @SerializedName("usuario")
    private Object user;

    @SerializedName("altq")
    private Object altq;

    @SerializedName("altp")
    private Object altp;

    private boolean nonCompliance;

    @SerializedName("perguntas")
    private List<Question> questions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getType() {
        return type;
    }

    public String getTypeFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public Object getAltq() {
        return altq;
    }

    public void setAltq(Object altq) {
        this.altq = altq;
    }

    public Object getAltp() {
        return altp;
    }

    public void setAltp(Object altp) {
        this.altp = altp;
    }

    public int getIsCountable() {
        return isCountable;
    }

    public void setIsCountable(int isCountable) {
        this.isCountable = isCountable;
    }

    public boolean isNonCompliance() {
        return nonCompliance;
    }

    public void setNonCompliance(boolean nonCompliance) {
        this.nonCompliance = nonCompliance;
    }
}
