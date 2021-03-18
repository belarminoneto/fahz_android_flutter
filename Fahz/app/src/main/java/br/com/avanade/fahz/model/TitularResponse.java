package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.response.CivilStateResponse;

public class TitularResponse implements Parcelable {
    @SerializedName("CPF")
    public String cpf;
    @SerializedName("Name")
    public String name;
    @SerializedName("Mother")
    public String mother;
    @SerializedName("PIS")
    public int pis;
    @SerializedName("RG")
    public RGResponse rg;
    @SerializedName("Sex")
    public char sex;
    @SerializedName("Nationality")
    public String nationality;
    @SerializedName("BirthDate")
    public String birthdate;
    @SerializedName("CivilState")
    public CivilState civilstate;
    @SerializedName("Phone")
    public String phone;
    @SerializedName("CellPhone")
    public String cellphone;
    @SerializedName("ExtensionLine")
    public String extesionline;
    @SerializedName("Email")
    public String email;
    @SerializedName("Grade")
    public Grade grade;
    @SerializedName("RealWorkspace")
    public WorkspaceResponse realworkspace;
    @SerializedName("Enrollment")
    public int enrollment;
    @SerializedName("Sequential")
    public short sequential;
    @SerializedName("SharpId")
    public int sharp;
    @SerializedName("Status")
    public StatusResponse status;
    @SerializedName("OrganizationalAttribution")
    public OrganizationalAttribution organizationalattribution;
    @SerializedName("Source")
    public OriginResponse source;
    @SerializedName("AdmissionDate")
    public String admissiondate;
    @SerializedName("DischargeDate")
    public String dischargedate;
    @SerializedName("Discharge")
    public Discharge discharge;
    @SerializedName("CreationDate")
    public String creationdate;
    @SerializedName("Creator")
    public UserResponse creator;
    @SerializedName("UpdateDate")
    public String updatedate;
    @SerializedName("Updater")
    public UserResponse updater;
    @SerializedName("BirthState")
    public String birthstate;
    @SerializedName("BirthCity")
    public String birthCity;
    @SerializedName("Post")
    public Post post;
    @SerializedName("Address")
    public AddressResponse address;
    @SerializedName("BankData")
    public BankResponse bank;
    @SerializedName("Responsable")
    public String responsable;
    @SerializedName("PharmacyLimit")
    public double pharmacyLimit;
    @SerializedName("IsPreRegistration")
    public boolean isPreRegistration;
    @SerializedName("OutOfPolicy")
    public boolean outOfPolicy;
    @SerializedName("UnionHealthHolder")
    public boolean UnionHealthHolder;
    @SerializedName("CostCenter")
    public String costCenter;
    @SerializedName("Comments")
    public String comments;
    @SerializedName("Contract")
    public int contract;
    @SerializedName("CompanyOdontoprev")
    public int CompanyOdontoprev;
    @SerializedName("Subsidiary")
    public Subsidiary subsidiary;
    @SerializedName("Company")
    public Company company;

    public ValidateTokenRequest.SendMethod tokenMethod;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cpf);
        dest.writeString(this.name);
        dest.writeString(this.mother);
        dest.writeInt(this.pis);
        dest.writeParcelable(this.rg, flags);
        dest.writeInt(this.sex);
        dest.writeString(this.nationality);
        dest.writeString(this.birthdate);
        dest.writeParcelable(this.civilstate, flags);
        dest.writeString(this.phone);
        dest.writeString(this.cellphone);
        dest.writeString(this.extesionline);
        dest.writeString(this.email);
        dest.writeParcelable(this.grade, flags);
        dest.writeParcelable(this.realworkspace, flags);
        dest.writeInt(this.enrollment);
        dest.writeInt(this.sharp);
        dest.writeInt(this.sequential);
        dest.writeParcelable(this.status, flags);
        dest.writeParcelable(this.organizationalattribution, flags);
        dest.writeParcelable(this.source, flags);
        dest.writeString(this.admissiondate);
        dest.writeString(this.dischargedate);
        dest.writeParcelable(this.discharge, flags);
        dest.writeString(this.creationdate);
        dest.writeParcelable(this.creator, flags);
        dest.writeString(this.updatedate);
        dest.writeParcelable(this.updater, flags);
        dest.writeString(this.birthstate);
        dest.writeString(this.birthCity);
        dest.writeParcelable(this.post, flags);
        dest.writeParcelable(this.address, flags);
        dest.writeParcelable(this.bank, flags);
        dest.writeInt(this.CompanyOdontoprev);
        dest.writeInt(this.contract);
    }

    public TitularResponse() {
    }

    protected TitularResponse(Parcel in) {
        this.cpf = in.readString();
        this.name = in.readString();
        this.mother = in.readString();
        this.pis = in.readInt();
        this.rg = in.readParcelable(RGResponse.class.getClassLoader());
        this.sex = (char) in.readInt();
        this.nationality = in.readString();
        this.birthdate = in.readString();
        this.civilstate = in.readParcelable(CivilStateResponse.class.getClassLoader());
        this.phone = in.readString();
        this.cellphone = in.readString();
        this.extesionline = in.readString();
        this.email = in.readString();
        this.grade = in.readParcelable(Grade.class.getClassLoader());
        this.realworkspace = in.readParcelable(WorkspaceResponse.class.getClassLoader());
        this.enrollment = in.readParcelable(EnrollmentResponse.class.getClassLoader());
        this.sharp = in.readParcelable(SharpResponse.class.getClassLoader());
        this.status = in.readParcelable(StatusResponse.class.getClassLoader());
        this.organizationalattribution = in.readParcelable(OrganizationalAttribution.class.getClassLoader());
        this.source = in.readParcelable(OriginResponse.class.getClassLoader());
        this.admissiondate = in.readString();
        this.dischargedate = in.readString();
        this.discharge = in.readParcelable(Discharge.class.getClassLoader());
        this.creationdate = in.readString();
        this.creator = in.readParcelable(UserResponse.class.getClassLoader());
        this.updatedate = in.readString();
        this.updater = in.readParcelable(UserResponse.class.getClassLoader());
        this.birthstate = in.readString();
        this.birthCity = in.readString();
        this.post = in.readParcelable(Post.class.getClassLoader());
        this.address = in.readParcelable(AddressResponse.class.getClassLoader());
        this.bank = in.readParcelable(BankResponse.class.getClassLoader());
        this.CompanyOdontoprev = (char) in.readInt();
        this.contract = (char) in.readInt();
    }

    public static final Parcelable.Creator<TitularResponse> CREATOR = new Parcelable.Creator<TitularResponse>() {
        @Override
        public TitularResponse createFromParcel(Parcel source) {
            return new TitularResponse(source);
        }

        @Override
        public TitularResponse[] newArray(int size) {
            return new TitularResponse[size];
        }
    };
}
