package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsPlainText implements Parcelable {
    public final static Parcelable.Creator<NewsPlainText> CREATOR = new Creator<NewsPlainText>() {

        @SuppressWarnings({
                "unchecked"
        })
        public NewsPlainText createFromParcel(Parcel in) {
            return new NewsPlainText(in);
        }

        public NewsPlainText[] newArray(int size) {
            return (new NewsPlainText[size]);
        }
    };
    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("Text")
    @Expose
    public String text;

    protected NewsPlainText(Parcel in) {
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.text = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public NewsPlainText() {
    }

    /**
     * @param text
     * @param title
     */
    public NewsPlainText(String title, String text) {
        super();
        this.title = title;
        this.text = text;
    }

    @Override
    public String toString() {
        String s;
        s = "title" + title + "text" + text;

        return s;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(title);
        dest.writeValue(text);
    }

    public int describeContents() {
        return 0;
    }
}