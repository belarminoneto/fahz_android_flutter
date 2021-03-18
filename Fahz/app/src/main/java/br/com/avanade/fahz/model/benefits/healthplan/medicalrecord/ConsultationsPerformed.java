package br.com.avanade.fahz.model.benefits.healthplan.medicalrecord;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ConsultationsPerformed implements Serializable, Parcelable
{
    @SerializedName("commited")
    @Expose
    public boolean commited;
    @SerializedName("Data")
    @Expose
    public List<MedicalRecords> data = null;
    public final static Parcelable.Creator<ConsultationsPerformed> CREATOR = new Creator<ConsultationsPerformed>() {

        @SuppressWarnings({
                "unchecked"
        })
        public ConsultationsPerformed createFromParcel(Parcel in) {
            return new ConsultationsPerformed(in);
        }

        public ConsultationsPerformed[] newArray(int size) {
            return (new ConsultationsPerformed[size]);
        }
    };

    private final static long serialVersionUID = 1598613972526253396L;

    protected ConsultationsPerformed(Parcel in) {
        this.commited = ((boolean) in.readValue((boolean.class.getClassLoader())));
        in.readList(this.data, (MedicalRecords.class.getClassLoader()));
    }

    public ConsultationsPerformed() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("commited");
        sb.append(commited);
        sb.append("data");
        sb.append(data);

        return sb.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(commited);
        dest.writeList(data);
    }

    public int describeContents() {
        return 0;
    }
}