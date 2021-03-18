package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class DependentLife {
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
    public CivilStatusRequest civilStatusRequest;
    @SerializedName("Nationality")
    public String nationality;
    @SerializedName("Student")
    public boolean student;
    @SerializedName("Invalid")
    public boolean invalid;
    @SerializedName("OutOfPolicy")
    public boolean outOfPolicy;
    @SerializedName("Sequential")
    public int sequential;
    @SerializedName("Status")
    public Status status;
    @SerializedName("KinshipId")
    public int kinshipId;
    @SerializedName("SystemBehavior")
    public SystemBehaviorModel systemBehavior;
    @SerializedName("MarriageDate")
    public String marriageDate;
    @SerializedName("Email")
    public String email;
    @SerializedName("CellPhone")
    public String cellPhone;
    @SerializedName("HolderCPF")
    public String holderCPF;


    public void populateWithInformation(DependentRequest dependent, int kinshipId) {

        cpf = dependent.cpf;
        name = dependent.name;
        birthDate = dependent.birthDate;
        mothersName = dependent.mothersName;
        sex = dependent.sex;
        civilStatusRequest = dependent.civilState;
        nationality = dependent.nationality;
        student = dependent.student;
        invalid = dependent.invalid;
        outOfPolicy = dependent.outOfPolicy;
        status = dependent.status;
        this.kinshipId = kinshipId;
        systemBehavior = dependent.systemBehavior;
        marriageDate = dependent.marriageDate;
        email = dependent.email;
        cellPhone = dependent.cellPhone;

    }

    public void populateDependent(Dependent dependent) {

        cpf = dependent.cpf;
        name = dependent.name;
        status = dependent.status;
        systemBehavior = dependent.systemBehavior;
    }
}
