package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class News implements Parcelable {
    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("Text")
    @Expose
    public String text;
    @SerializedName("AreaPanel")
    @Expose
    public List<AreaPanel> areaPanel = null;
    public final static Parcelable.Creator<News> CREATOR = new Creator<News>() {

        @SuppressWarnings({
                "unchecked"
        })
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        public News[] newArray(int size) {
            return (new News[size]);
        }
    };

    protected News(Parcel in) {
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.text = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.areaPanel, (AreaPanel.class.getClassLoader()));
    }

    public News() {
    }

    /**
     *
     * @param areaPanel
     * @param text
     * @param title
     */
    public News(String title, String text, List<AreaPanel> areaPanel) {
        super();
        this.title = title;
        this.text = text;
        this.areaPanel = areaPanel;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(title);
        dest.writeValue(text);
        dest.writeList(areaPanel);
    }

    public int describeContents() {
        return 0;
    }

}