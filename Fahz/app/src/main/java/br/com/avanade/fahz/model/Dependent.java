package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class Dependent {
    @SerializedName("CPF")
    public String cpf;
    @SerializedName("Name")
    public String name;
    @SerializedName("HoldersName")
    public String HoldersName;
    @SerializedName("Grade")
    public short Grade;
    @SerializedName("Sequential")
    public int Sequential;
    @SerializedName("Status")
    public Status status;
    @SerializedName("Processing")
    public boolean processing;
    @SerializedName("KinshipId")
    public int kinshipId;
    @SerializedName("SystemBehavior")
    public SystemBehaviorModel systemBehavior;

    @Override
    public String toString() {
        return "Dependent{" +
                "cpf='" + cpf + '\'' +
                ", name='" + name + '\'' +
                ", HoldersName='" + HoldersName + '\'' +
                ", Grade=" + Grade +
                ", Sequential=" + Sequential +
                ", status=" + status +
                ", processing=" + processing +
                '}';
    }

    public static Dependent createDependent(DependentLife dependentLife)
    {
        Dependent dep = new Dependent();
        dep.status = dependentLife.status;
        dep.cpf = dependentLife.cpf;
        dep.name = dependentLife.name;
        dep.status = dependentLife.status;
        dep.kinshipId = dependentLife.kinshipId;
        dep.systemBehavior = dependentLife.systemBehavior;


        return dep;
    }
}
