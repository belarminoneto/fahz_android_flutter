package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CompanyResponse implements Parcelable {
    @SerializedName("Id")
    public String id;
    @SerializedName("Description")
    public String description;
    @SerializedName("CNPJ")
    public String cnpj;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.cnpj);
    }

    public CompanyResponse() {
    }

    protected CompanyResponse(Parcel in) {
        this.id = in.readString();
        this.description = in.readString();
        this.cnpj = in.readString();
    }

    public static final Parcelable.Creator<CompanyResponse> CREATOR = new Parcelable.Creator<CompanyResponse>() {
        @Override
        public CompanyResponse createFromParcel(Parcel source) {
            return new CompanyResponse(source);
        }

        @Override
        public CompanyResponse[] newArray(int size) {
            return new CompanyResponse[size];
        }
    };
}

