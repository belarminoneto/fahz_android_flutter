package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PrivacyControl implements Parcelable {

    public final static Parcelable.Creator<PrivacyControl> CREATOR = new Creator<PrivacyControl>() {

        @SuppressWarnings({
                "unchecked"
        })
        public PrivacyControl createFromParcel(Parcel in) {
            return new PrivacyControl(in);
        }

        public PrivacyControl[] newArray(int size) {
            return (new PrivacyControl[size]);
        }

    };

    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("Text")
    @Expose
    public String text;
    @SerializedName("Controls")
    @Expose
    public List<Control> controls = null;
    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    protected PrivacyControl(Parcel in) {
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.text = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.controls, (br.com.avanade.fahz.model.lgpdModel.Control.class.getClassLoader()));
    }

    public PrivacyControl() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(title);
        dest.writeValue(text);
        dest.writeList(controls);
    }

    public int describeContents() {
        return 0;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean hasMessage() {
        return message != null;
    }

}
