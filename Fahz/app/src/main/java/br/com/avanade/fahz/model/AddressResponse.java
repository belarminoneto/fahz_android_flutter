package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class AddressResponse implements Parcelable {
    @SerializedName("CPF")
    public String cpf;
    @SerializedName("CEP")
    public String zip;
    @SerializedName("StreetDescription")
    public String street;
    @SerializedName("Number")
    public int number;
    @SerializedName("Complement")
    public String complement;
    @SerializedName("Neighborhood")
    public String district;
    @SerializedName("City")
    public String city;
    @SerializedName("State")
    public String state;
    @SerializedName("Country")
    public String country;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cpf);
        dest.writeString(this.zip);
        dest.writeString(this.street);
        dest.writeInt(this.number);
        dest.writeString(this.complement);
        dest.writeString(this.district);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeString(this.country);
    }

    public AddressResponse() {
    }

    protected AddressResponse(Parcel in) {
        this.cpf = in.readString();
        this.zip = in.readString();
        this.street = in.readString();
        this.number = in.readInt();
        this.complement = in.readString();
        this.district = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.country = in.readString();
    }

    public static final Parcelable.Creator<AddressResponse> CREATOR = new Parcelable.Creator<AddressResponse>() {
        @Override
        public AddressResponse createFromParcel(Parcel source) {
            return new AddressResponse(source);
        }

        @Override
        public AddressResponse[] newArray(int size) {
            return new AddressResponse[size];
        }
    };
}
