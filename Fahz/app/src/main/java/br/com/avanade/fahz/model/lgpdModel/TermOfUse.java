package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TermOfUse implements Parcelable
{

    @SerializedName("Code")
    @Expose
    public String code;
    @SerializedName("lastVersion")
    @Expose
    public int lastVersion;
    @SerializedName("Text")
    @Expose
    public String text;
    @SerializedName("InitialDate")
    @Expose
    public String initialDate;
    @SerializedName("FinalDate")
    @Expose
    public String finalDate;
    @SerializedName("FilePath")
    @Expose
    public String filePath;
    public final static Parcelable.Creator<TermOfUse> CREATOR = new Creator<TermOfUse>() {

        public TermOfUse createFromParcel(Parcel in) {
            return new TermOfUse(in);
        }

        public TermOfUse[] newArray(int size) {
            return (new TermOfUse[size]);
        }

    };

    protected TermOfUse(Parcel in) {
        this.code = ((String) in.readValue((String.class.getClassLoader())));
        this.lastVersion = ((int) in.readValue((int.class.getClassLoader())));
        this.text = ((String) in.readValue((String.class.getClassLoader())));
        this.initialDate = ((String) in.readValue((String.class.getClassLoader())));
        this.finalDate = (String) in.readValue((String.class.getClassLoader()));
        this.filePath = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TermOfUse() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(code);
        dest.writeValue(lastVersion);
        dest.writeValue(text);
        dest.writeValue(initialDate);
        dest.writeValue(finalDate);
        dest.writeValue(filePath);
    }

    public int describeContents() {
        return 0;
    }

}