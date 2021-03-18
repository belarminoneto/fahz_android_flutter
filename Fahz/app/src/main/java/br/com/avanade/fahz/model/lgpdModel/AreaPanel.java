package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AreaPanel implements Parcelable {

    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("NewsSection")
    @Expose
    public List<NewsSection> newsSection = null;
    public final static Parcelable.Creator<AreaPanel> CREATOR = new Creator<AreaPanel>() {

        @SuppressWarnings({
                "unchecked"
        })
        public AreaPanel createFromParcel(Parcel in) {
            return new AreaPanel(in);
        }

        public AreaPanel[] newArray(int size) {
            return (new AreaPanel[size]);
        }
    };

    protected AreaPanel(Parcel in) {
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.newsSection, (NewsSection.class.getClassLoader()));
    }

    public AreaPanel() {
    }

    /**
     *
     * @param title
     * @param newsSection
     */
    public AreaPanel(String title, List<NewsSection> newsSection) {
        super();
        this.title = title;
        this.newsSection = newsSection;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(title);
        dest.writeList(newsSection);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();
        s.append("title");
        s.append(this.title);
        s.append("newsSection");
        s.append(this.newsSection);

        return s.toString();
    }


}