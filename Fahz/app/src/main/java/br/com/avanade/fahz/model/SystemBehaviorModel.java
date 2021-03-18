package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SystemBehaviorModel implements Parcelable {

    @SerializedName("ID")
    public int ID;

    public SystemBehaviorModel() {

    }

    protected SystemBehaviorModel(Parcel in) {
        ID = in.readInt();
    }

    public static final Creator<SystemBehaviorModel> CREATOR = new Creator<SystemBehaviorModel>() {
        @Override
        public SystemBehaviorModel createFromParcel(Parcel in) {
            return new SystemBehaviorModel(in);
        }

        @Override
        public SystemBehaviorModel[] newArray(int size) {
            return new SystemBehaviorModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
    }
}
