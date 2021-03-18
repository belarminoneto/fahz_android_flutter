package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class StatusResponse implements Parcelable {
    @SerializedName("Id")
    private int id;
    @SerializedName("Description")
    private String description;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.description);
    }

    public StatusResponse() {
    }

    StatusResponse(Parcel in) {
        this.id = in.readInt();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<StatusResponse> CREATOR = new Parcelable.Creator<StatusResponse>() {
        @Override
        public StatusResponse createFromParcel(Parcel source) {
            return new StatusResponse(source);
        }

        @Override
        public StatusResponse[] newArray(int size) {
            return new StatusResponse[size];
        }
    };
}