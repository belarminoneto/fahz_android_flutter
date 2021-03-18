package br.com.avanade.fahz.model.benefits.healthplan.medicalrecord;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MedicalRecords implements Serializable, Parcelable
{
    @SerializedName("Id")
    @Expose
    public String id;
    @SerializedName("Consultation")
    @Expose
    public String consultation;
    @SerializedName("Doctor")
    @Expose
    public String doctor;
    @SerializedName("CareProvider")
    @Expose
    public String careProvider;

    public final static Parcelable.Creator<MedicalRecords> CREATOR = new Creator<MedicalRecords>() {

        @SuppressWarnings({
                "unchecked"
        })
        public MedicalRecords createFromParcel(Parcel in) {
            return new MedicalRecords(in);
        }

        public MedicalRecords[] newArray(int size) {
            return (new MedicalRecords[size]);
        }
    };

    private final static long serialVersionUID = 5672064754193003632L;

    protected MedicalRecords(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.consultation = ((String) in.readValue((String.class.getClassLoader())));
        this.doctor = ((String) in.readValue((String.class.getClassLoader())));
        this.careProvider = ((String) in.readValue((String.class.getClassLoader())));
    }

    public MedicalRecords() {
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("id");
        sb.append(id);
        sb.append("consultation");
        sb.append(consultation);
        sb.append("doctor");
        sb.append(doctor);
        sb.append("careProvider");
        sb.append(careProvider);

        return sb.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(consultation);
        dest.writeValue(doctor);
        dest.writeValue(careProvider);
    }

    public int describeContents() {
        return 0;
    }
}