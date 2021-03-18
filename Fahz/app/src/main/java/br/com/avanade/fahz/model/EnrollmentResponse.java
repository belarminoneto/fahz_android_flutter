package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class EnrollmentResponse implements Parcelable {
    @SerializedName("Number")
    public int number;
    @SerializedName("Sequence")
    public int sequence;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.number);
        dest.writeInt(this.sequence);
    }

    public EnrollmentResponse() {
    }

    protected EnrollmentResponse(Parcel in) {
        this.number = in.readInt();
        this.sequence = in.readInt();
    }

    public static final Parcelable.Creator<EnrollmentResponse> CREATOR = new Parcelable.Creator<EnrollmentResponse>() {
        @Override
        public EnrollmentResponse createFromParcel(Parcel source) {
            return new EnrollmentResponse(source);
        }

        @Override
        public EnrollmentResponse[] newArray(int size) {
            return new EnrollmentResponse[size];
        }
    };
}
