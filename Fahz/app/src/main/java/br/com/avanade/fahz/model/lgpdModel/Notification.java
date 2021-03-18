package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Notification implements Parcelable {
    public final static Parcelable.Creator<Notification> CREATOR = new Creator<Notification>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        public Notification[] newArray(int size) {
            return (new Notification[size]);
        }

    };
    @SerializedName("Id")
    @Expose
    public int id;
    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("Text")
    @Expose
    public String text;
    @SerializedName("Authorizations")
    @Expose
    public List<Authorization> authorizations = null;

    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    protected Notification(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.text = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.authorizations, (Authorization.class.getClassLoader()));
    }

    public Notification() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(title);
        dest.writeValue(text);
        dest.writeList(authorizations);
    }

    public int describeContents() {
        return 0;
    }

    public String getMessage() {
        return message;
    }

    public boolean hasMessage() {
        return message != null;
    }

}