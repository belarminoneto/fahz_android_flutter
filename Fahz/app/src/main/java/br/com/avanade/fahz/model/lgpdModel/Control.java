package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Control implements Parcelable
{

    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("Description")
    @Expose
    public String description;
    @SerializedName("Link")
    @Expose
    public String link;
    public final static Parcelable.Creator<Control> CREATOR = new Creator<Control>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Control createFromParcel(Parcel in) {
            return new Control(in);
        }

        public Control[] newArray(int size) {
            return (new Control[size]);
        }

    };

    protected Control(Parcel in) {
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.link = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Control() {
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(title);
        dest.writeValue(description);
        dest.writeValue(link);
    }

    public int describeContents() {
        return 0;
    }

}