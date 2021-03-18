package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Cpf")
    @Expose
    private String cpf;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Date")
    @Expose
    private String date;
    @SerializedName("Content")
    @Expose
    private String content;
    @SerializedName("Read")
    @Expose
    private Boolean read;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
