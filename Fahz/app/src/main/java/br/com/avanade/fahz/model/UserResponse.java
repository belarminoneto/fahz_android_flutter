package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserResponse implements Parcelable {
    @SerializedName("CPF")
    public String cpf;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cpf);
    }

    public UserResponse() {
    }

    protected UserResponse(Parcel in) {
        this.cpf = in.readString();
    }

    public static final Parcelable.Creator<UserResponse> CREATOR = new Parcelable.Creator<UserResponse>() {
        @Override
        public UserResponse createFromParcel(Parcel source) {
            return new UserResponse(source);
        }

        @Override
        public UserResponse[] newArray(int size) {
            return new UserResponse[size];
        }
    };
}
