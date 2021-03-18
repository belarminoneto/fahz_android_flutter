package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Register implements Parcelable
{

    @SerializedName("CPF")
    @Expose
    public String cPF;
    @SerializedName("TermOfUse")
    @Expose
    public TermOfUse termOfUse;
    @SerializedName("Date")
    @Expose
    public String date;
    public final static Parcelable.Creator<Register> CREATOR = new Creator<Register>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Register createFromParcel(Parcel in) {
            return new Register(in);
        }

        public Register[] newArray(int size) {
            return (new Register[size]);
        }

    };

    protected Register(Parcel in) {
        this.cPF = ((String) in.readValue((String.class.getClassLoader())));
        this.termOfUse = ((TermOfUse) in.readValue((TermOfUse.class.getClassLoader())));
        this.date = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Register() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(cPF);
        dest.writeValue(termOfUse);
        dest.writeValue(date);
    }

    public int describeContents() {
        return 0;
    }

}