package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class DependentRequest {
    @SerializedName("CPF")
    public String cpf;
    @SerializedName("Name")
    public String name;
    @SerializedName("BirthDate")
    public String birthDate;
    @SerializedName("MothersName")
    public String mothersName;
    @SerializedName("Sex")
    public String sex;
    @SerializedName("CivilState")
    public CivilStatusRequest civilState;
    @SerializedName("Nationality")
    public String nationality;
    @SerializedName("Student")
    public Boolean student;
    @SerializedName("Invalid")
    public Boolean invalid;
    @SerializedName("OutOfPolicy")
    public Boolean outOfPolicy;
    @SerializedName("Sequential")
    public Integer sequential;
    @SerializedName("Status")
    public Status status;
    @SerializedName("SystemBehavior")
    public SystemBehaviorModel systemBehavior;
    @SerializedName("MarriageDate")
    public String marriageDate;
    @SerializedName("Email")
    public String email;
    @SerializedName("CellPhone")
    public String cellPhone;

    public boolean isMinor;

    @Override
    public String toString() {
        return "DependentRequest{" +
                "cpf='" + cpf + '\'' +
                ", name='" + name + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", mothersName='" + mothersName + '\'' +
                ", sex='" + sex + '\'' +
                ", civilState=" + civilState +
                ", nationality='" + nationality + '\'' +
                ", student=" + student +
                ", invalid=" + invalid +
                ", outOfPolicy=" + outOfPolicy +
                ", sequential=" + sequential +
                ", status=" + status +
                '}';
    }
}
