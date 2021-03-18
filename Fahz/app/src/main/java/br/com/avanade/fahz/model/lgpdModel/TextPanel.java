package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TextPanel implements Parcelable
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
    public Object idUserAuthor;
    @SerializedName("IdUser_Editor")
    @Expose
    public Object idUserEditor;
    @SerializedName("Order")
    @Expose
    public Object order;
    @SerializedName("PanelTitle")
    @Expose
    public String panelTitle;
    @SerializedName("VersionPanelId")
    @Expose
    public String versionPanelId;
    @SerializedName("Version")
    @Expose
    public long version;
    public final static Parcelable.Creator<TextPanel> CREATOR = new Creator<TextPanel>() {

        public TextPanel createFromParcel(Parcel in) {
            return new TextPanel(in);
        }

        public TextPanel[] newArray(int size) {
            return (new TextPanel[size]);
        }
    };

    protected TextPanel(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.idTextMaster = in.readValue((Object.class.getClassLoader()));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.review = ((String) in.readValue((String.class.getClassLoader())));
        this.complete = ((String) in.readValue((String.class.getClassLoader())));
        this.linkInternal = (String) in.readValue((String.class.getClassLoader()));
        this.linkExternal = (String) in.readValue((String.class.getClassLoader()));
        this.createDate = ((String) in.readValue((String.class.getClassLoader())));
        this.updateDate = ((String) in.readValue((String.class.getClassLoader())));
        this.idUserAuthor = in.readValue((Object.class.getClassLoader()));
        this.idUserEditor = in.readValue((Object.class.getClassLoader()));
        this.order = in.readValue((Object.class.getClassLoader()));
        this.panelTitle = ((String) in.readValue((String.class.getClassLoader())));
        this.versionPanelId = ((String) in.readValue((String.class.getClassLoader())));
        this.version = ((long) in.readValue((long.class.getClassLoader())));
    }

    public TextPanel() {
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

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append("id");
        s.append(id);
        s.append("idTextMaster");
        s.append(idTextMaster);
        s.append("title");
        s.append(title);
        s.append("review");
        s.append(review);
        s.append("complete");
        s.append(complete);
        s.append("linkInternal");
        s.append(linkInternal);
        s.append("linkExternal");
        s.append(linkExternal);
        s.append("createDate");
        s.append(createDate);
        s.append("updateDate");
        s.append(updateDate);
        s.append("idUserAuthor");
        s.append(idUserAuthor);
        s.append("idUserEditor");
        s.append(idUserEditor);
        s.append("order");
        s.append(order);
        s.append("panelTitle");
        s.append(panelTitle);
        s.append("versionPanelId");
        s.append(versionPanelId);
        s.append("version");
        s.append(version);

        return s.toString();
    }
}