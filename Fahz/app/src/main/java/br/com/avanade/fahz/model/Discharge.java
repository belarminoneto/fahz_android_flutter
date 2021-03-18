package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Discharge implements Parcelable{

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("Description")
    @Expose
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.description);
    }

    protected Discharge(Parcel in) {
        this.id = in.readInt();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<Discharge> CREATOR = new Parcelable.Creator<Discharge>() {
        @Override
        public Discharge createFromParcel(Parcel source) {
            return new Discharge(source);
        }

        @Override
        public Discharge[] newArray(int size) {
            return new Discharge[size];
        }
    };

}