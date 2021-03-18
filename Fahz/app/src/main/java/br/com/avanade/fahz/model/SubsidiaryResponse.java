package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SubsidiaryResponse implements Parcelable {
    @SerializedName("Id")
    public String id;
    @SerializedName("Description")
    public String description;
    @SerializedName("CNPJ")
    public String cnpj;
    @SerializedName("Company")
    public CompanyResponse company;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.cnpj);
        dest.writeParcelable(this.company, flags);
    }

    public SubsidiaryResponse() {
    }

    protected SubsidiaryResponse(Parcel in) {
        this.id = in.readString();
        this.description = in.readString();
        this.cnpj = in.readString();
        this.company = in.readParcelable(CompanyResponse.class.getClassLoader());
    }

    public static final Parcelable.Creator<SubsidiaryResponse> CREATOR = new Parcelable.Creator<SubsidiaryResponse>() {
        @Override
        public SubsidiaryResponse createFromParcel(Parcel source) {
            return new SubsidiaryResponse(source);
        }

        @Override
        public SubsidiaryResponse[] newArray(int size) {
            return new SubsidiaryResponse[size];
        }
    };
}
