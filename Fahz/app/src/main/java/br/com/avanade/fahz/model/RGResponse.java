package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RGResponse implements Parcelable {
    @SerializedName("Number")
    public String number;
    @SerializedName("Emitter")
    public String emitter;
    @SerializedName("Country")
    public String country;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.number);
        dest.writeString(this.emitter);
        dest.writeString(this.country);
    }

    public RGResponse() {
    }

    protected RGResponse(Parcel in) {
        this.number = in.readString();
        this.emitter = in.readString();
        this.country = in.readString();
    }

    public static final Parcelable.Creator<RGResponse> CREATOR = new Parcelable.Creator<RGResponse>() {
        @Override
        public RGResponse createFromParcel(Parcel source) {
            return new RGResponse(source);
        }

        @Override
        public RGResponse[] newArray(int size) {
            return new RGResponse[size];
        }
    };
}
