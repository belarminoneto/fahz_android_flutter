package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SharpResponse implements Parcelable {
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

    public SharpResponse() {
    }

    protected SharpResponse(Parcel in) {
        this.number = in.readInt();
        this.sequence = in.readInt();
    }

    public static final Parcelable.Creator<SharpResponse> CREATOR = new Parcelable.Creator<SharpResponse>() {
        @Override
        public SharpResponse createFromParcel(Parcel source) {
            return new SharpResponse(source);
        }

        @Override
        public SharpResponse[] newArray(int size) {
            return new SharpResponse[size];
        }
    };
}
