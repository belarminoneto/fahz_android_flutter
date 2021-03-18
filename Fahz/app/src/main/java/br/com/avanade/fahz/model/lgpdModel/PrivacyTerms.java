package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PrivacyTerms implements Parcelable {

    public final static Parcelable.Creator<PrivacyTerms> CREATOR = new Creator<PrivacyTerms>() {

        @SuppressWarnings({
                "unchecked"
        })
        public PrivacyTerms createFromParcel(Parcel in) {
            return new PrivacyTerms(in);
        }

        public PrivacyTerms[] newArray(int size) {
            return (new PrivacyTerms[size]);
        }
    };

    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    @SerializedName("section")
    @Expose
    public List<Section> section = null;
    @SerializedName("news")
    @Expose
    public News news;
    @SerializedName("QuickLinkPanels")
    @Expose
    public List<QuickLinkPanel> quickLinkPanels = null;

    @SerializedName("newsPlainText")
    @Expose
    public List<NewsPlainText> newsPlainText = null;


    public boolean hasMessage() {
        return message != null;
    }

    protected PrivacyTerms(Parcel in) {
        in.readList(this.section, (Section.class.getClassLoader()));
        this.news = ((News) in.readValue((News.class.getClassLoader())));
        in.readList(this.quickLinkPanels, (br.com.avanade.fahz.model.lgpdModel.QuickLinkPanel.class.getClassLoader()));
        in.readList(this.newsPlainText, (br.com.avanade.fahz.model.lgpdModel.NewsPlainText.class.getClassLoader()));

    }

    /**
     * @param news
     * @param quickLinkPanels
     * @param newsPlainText
     * @param section
     */
    public PrivacyTerms(List<Section> section, News news, List<QuickLinkPanel> quickLinkPanels, List<NewsPlainText> newsPlainText) {
        super();
        this.section = section;
        this.news = news;
        this.quickLinkPanels = quickLinkPanels;
        this.newsPlainText = newsPlainText;
    }

    public PrivacyTerms() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(section);
        dest.writeValue(news);
        dest.writeList(quickLinkPanels);
        dest.writeList(newsPlainText);
    }

    public int describeContents() {
        return hashCode();
    }
}
