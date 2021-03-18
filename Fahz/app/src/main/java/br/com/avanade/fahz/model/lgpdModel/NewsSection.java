package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsSection implements Parcelable
{

    @SerializedName("Id")
    @Expose
    public int id;
    @SerializedName("IdTextMaster")
    @Expose
    public Object idTextMaster;
    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("Review")
    @Expose
    public String review;
    @SerializedName("Complete")
    @Expose
    public String complete;
    @SerializedName("LinkInternal")
    @Expose
    public String linkInternal;
    @SerializedName("LinkExternal")
    @Expose
    public String linkExternal;
    @SerializedName("CreateDate")
    @Expose
    public String createDate;
    @SerializedName("UpdateDate")
    @Expose
    public String updateDate;
    @SerializedName("IdUser_Author")
    @Expose
    public String idUserAuthor;
    @SerializedName("IdUser_Editor")
    @Expose
    public String idUserEditor;
    @SerializedName("Order")
    @Expose
    public String order;
    @SerializedName("PanelTitle")
    @Expose
    public String panelTitle;
    @SerializedName("VersionPanelId")
    @Expose
    public String versionPanelId;
    @SerializedName("Version")
    @Expose
    public int version;
    public final static Parcelable.Creator<NewsSection> CREATOR = new Creator<NewsSection>() {

        public NewsSection createFromParcel(Parcel in) {
            return new NewsSection(in);
        }

        public NewsSection[] newArray(int size) {
            return (new NewsSection[size]);
        }

    };

    protected NewsSection(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.idTextMaster = in.readValue((Object.class.getClassLoader()));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.review = ((String) in.readValue((String.class.getClassLoader())));
        this.complete = ((String) in.readValue((String.class.getClassLoader())));
        this.linkInternal = ((String) in.readValue((Object.class.getClassLoader())));
        this.linkExternal = ((String) in.readValue((Object.class.getClassLoader())));
        this.createDate = ((String) in.readValue((String.class.getClassLoader())));
        this.updateDate = ((String) in.readValue((Object.class.getClassLoader())));
        this.idUserAuthor = ((String) in.readValue((Object.class.getClassLoader())));
        this.idUserEditor = ((String) in.readValue((Object.class.getClassLoader())));
        this.order = ((String) in.readValue((Object.class.getClassLoader())));
        this.panelTitle = ((String) in.readValue((String.class.getClassLoader())));
        this.versionPanelId = ((String) in.readValue((String.class.getClassLoader())));
        this.version = ((int) in.readValue((int.class.getClassLoader())));
    }

    public NewsSection() {
    }

    public NewsSection(int id, Object idTextMaster, String title, String review, String complete, String linkInternal, String linkExternal, String createDate, String updateDate, String idUserAuthor, String idUserEditor, String order, String panelTitle, String versionPanelId, int version) {
        super();
        this.id = id;
        this.idTextMaster = idTextMaster;
        this.title = title;
        this.review = review;
        this.complete = complete;
        this.linkInternal = linkInternal;
        this.linkExternal = linkExternal;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.idUserAuthor = idUserAuthor;
        this.idUserEditor = idUserEditor;
        this.order = order;
        this.panelTitle = panelTitle;
        this.versionPanelId = versionPanelId;
        this.version = version;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(idTextMaster);
        dest.writeValue(title);
        dest.writeValue(review);
        dest.writeValue(complete);
        dest.writeValue(linkInternal);
        dest.writeValue(linkExternal);
        dest.writeValue(createDate);
        dest.writeValue(updateDate);
        dest.writeValue(idUserAuthor);
        dest.writeValue(idUserEditor);
        dest.writeValue(order);
        dest.writeValue(panelTitle);
        dest.writeValue(versionPanelId);
        dest.writeValue(version);
    }

    public int describeContents() {
        return 0;
    }

}