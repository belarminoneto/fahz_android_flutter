package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class WorkspaceResponse implements Parcelable {
    @SerializedName("Id")
    public int id;
    @SerializedName("CNPJ")
    public String cnpj;
    @SerializedName("Sector")
    public String sector;
    @SerializedName("Address")
    public String address;
    @SerializedName("StartAllocation")
    public String startaloocation;
    @SerializedName("Subsidiary")
    public SubsidiaryResponse subsidiary;
    @SerializedName("Description")
    public String description;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.cnpj);
        dest.writeString(this.sector);
        dest.writeString(this.address);
        dest.writeString(this.startaloocation);
        dest.writeParcelable(this.subsidiary, flags);
    }

    public WorkspaceResponse() {
    }

    protected WorkspaceResponse(Parcel in) {
        this.id = in.readInt();
        this.cnpj = in.readString();
        this.sector = in.readString();
        this.address = in.readString();
        this.startaloocation = in.readString();
        this.subsidiary = in.readParcelable(SubsidiaryResponse.class.getClassLoader());
    }

    public static final Parcelable.Creator<WorkspaceResponse> CREATOR = new Parcelable.Creator<WorkspaceResponse>() {
        @Override
        public WorkspaceResponse createFromParcel(Parcel source) {
            return new WorkspaceResponse(source);
        }

        @Override
        public WorkspaceResponse[] newArray(int size) {
            return new WorkspaceResponse[size];
        }
    };
}
