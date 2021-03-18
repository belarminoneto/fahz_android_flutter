package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.util.SystemBehavior;

public class ScholarshipLifeResponse implements Parcelable {
    @SerializedName("Life")
    @Expose
    private TitularResponse life;
    @SerializedName("ScholarshipLife")
    @Expose
    private ScholarshipLife scholarshipLife;
    @SerializedName("BankData")
    @Expose
    private BankResponse bankData;
    @SerializedName("CPF")
    @Expose
    private String cPF;
    @SerializedName("Phone")
    @Expose
    private String phone;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("Plan")
    @Expose
    private Plan plan;
    @SerializedName("SystemBehavior")
    @Expose
    private SystemBehaviorModel systemBehavior;

    public ScholarshipLifeResponse(Parcel in) {
        life = in.readParcelable(TitularResponse.class.getClassLoader());
        scholarshipLife = in.readParcelable(ScholarshipLife.class.getClassLoader());
        bankData = in.readParcelable(BankResponse.class.getClassLoader());
        cPF = in.readString();
        phone = in.readString();
        email = in.readString();
        plan = in.readParcelable(Plan.class.getClassLoader());
    }

    public static final Creator<ScholarshipLifeResponse> CREATOR = new Creator<ScholarshipLifeResponse>() {
        @Override
        public ScholarshipLifeResponse createFromParcel(Parcel in) {
            return new ScholarshipLifeResponse(in);
        }

        @Override
        public ScholarshipLifeResponse[] newArray(int size) {
            return new ScholarshipLifeResponse[size];
        }
    };

    public TitularResponse getLife() {
        return life;
    }

    public void setLife(TitularResponse life) {
        this.life = life;
    }

    public ScholarshipLife getScholarshipLife() {
        return scholarshipLife;
    }

    public void setScholarshipLife(ScholarshipLife scholarshipLife) {
        this.scholarshipLife = scholarshipLife;
    }

    public BankResponse getBankData() {
        return bankData;
    }

    public void setBankData(BankResponse bankData) {
        this.bankData = bankData;
    }

    public String getCPF() {
        return cPF;
    }

    public void setCPF(String cPF) {
        this.cPF = cPF;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public SystemBehaviorModel getSystemBehavior() {
        return systemBehavior;
    }

    public void setSystemBehavior(SystemBehaviorModel systemBehavior) {
        this.systemBehavior = systemBehavior;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.life, flags);
        dest.writeParcelable(this.scholarshipLife, flags);
        dest.writeParcelable(this.bankData, flags);
        dest.writeString(this.cPF);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeParcelable(this.plan, flags);
        dest.writeParcelable(this.systemBehavior, flags);
    }

    public ScholarshipLifeResponse() {
    }
}
