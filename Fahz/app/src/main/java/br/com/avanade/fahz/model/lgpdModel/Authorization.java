package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Authorization implements Parcelable {

    @SerializedName("Id")
    @Expose
    public int id;
    @SerializedName("TextAutorization")
    @Expose
    public String textAutorization;
    @SerializedName("TextDetails")
    @Expose
    public String textDetails;
    @SerializedName("Options")
    @Expose
    public List<Option> options = null;
    public final static Parcelable.Creator<Authorization> CREATOR = new Creator<Authorization>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Authorization createFromParcel(Parcel in) {
            return new Authorization(in);
        }

        public Authorization[] newArray(int size) {
            return (new Authorization[size]);
        }

    };

    protected Authorization(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.textAutorization = ((String) in.readValue((String.class.getClassLoader())));
        this.textDetails = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.options, (Option.class.getClassLoader()));
    }

    public Authorization() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(textAutorization);
        dest.writeValue(textDetails);
        dest.writeList(options);
    }

    public int describeContents() {
        return 0;
    }

}