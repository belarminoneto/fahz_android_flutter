package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class OriginResponse implements Parcelable {
    @SerializedName("Id")
    public int id;
    @SerializedName("Description")
    public String description;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.description);
    }

    public OriginResponse() {
    }

    protected OriginResponse(Parcel in) {
        this.id = in.readInt();
        this.description = in.readString();
    }

    public static final Creator<OriginResponse> CREATOR = new Creator<OriginResponse>() {
        @Override
        public OriginResponse createFromParcel(Parcel source) {
            return new OriginResponse(source);
        }

        @Override
        public OriginResponse[] newArray(int size) {
            return new OriginResponse[size];
        }
    };
}
