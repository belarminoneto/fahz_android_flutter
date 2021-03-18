package br.com.avanade.fahz.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CivilStateResponse implements Parcelable {
    @SerializedName("Id")
    public int id;
    @SerializedName("Description")
    public String description;
    @SerializedName("SuperType")
    public String supertype;
    @SerializedName("OperatorsCod")
    public String operatorscod;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.description);
        dest.writeString(this.supertype);
        dest.writeString(this.operatorscod);
    }

    public CivilStateResponse() {
    }

    protected CivilStateResponse(Parcel in) {
        this.id = in.readInt();
        this.description = in.readString();
        this.supertype = in.readString();
        this.operatorscod = in.readString();
    }

    public static final Parcelable.Creator<CivilStateResponse> CREATOR = new Parcelable.Creator<CivilStateResponse>() {
        @Override
        public CivilStateResponse createFromParcel(Parcel source) {
            return new CivilStateResponse(source);
        }

        @Override
        public CivilStateResponse[] newArray(int size) {
            return new CivilStateResponse[size];
        }
    };
}
