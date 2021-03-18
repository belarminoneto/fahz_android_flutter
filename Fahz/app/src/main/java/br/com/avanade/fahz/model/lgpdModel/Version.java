package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Version implements Parcelable {
    @SerializedName("Id")
    @Expose
    public String id;
    @SerializedName("VersionPanel")
    @Expose
    public float versionPanel;
    @SerializedName("CreateDate")
    @Expose
    public String createDate;
    @SerializedName("IdPanel")
    @Expose
    public long idPanel;
    @SerializedName("IdTextPanel")
    @Expose
    public Object idTextPanel;
    @SerializedName("Active")
    @Expose
    public boolean active;
    @SerializedName("TextPanel")
    @Expose
    public List<TextPanel> textPanel = null;
    @SerializedName("DateStart")
    @Expose
    public String dateStart;
    @SerializedName("DateEnd")
    @Expose
    public Object dateEnd;
    @SerializedName("LastVersionId")
    @Expose
    public String lastVersionId;
    public final static Parcelable.Creator<Version> CREATOR = new Creator<Version>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Version createFromParcel(Parcel in) {
            return new Version(in);
        }

        public Version[] newArray(int size) {
            return (new Version[size]);
        }
    };

    protected Version(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.versionPanel = ((float) in.readValue((float.class.getClassLoader())));
        this.createDate = ((String) in.readValue((String.class.getClassLoader())));
        this.idPanel = ((long) in.readValue((long.class.getClassLoader())));
        this.idTextPanel = in.readValue((Object.class.getClassLoader()));
        this.active = ((boolean) in.readValue((boolean.class.getClassLoader())));
        in.readList(this.textPanel, (br.com.avanade.fahz.model.lgpdModel.TextPanel.class.getClassLoader()));
        this.dateStart = ((String) in.readValue((String.class.getClassLoader())));
        this.dateEnd = in.readValue((Object.class.getClassLoader()));
        this.lastVersionId = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Version() {
    }


    /**
     *
     * @param lastVersionId
     * @param idPanel
     * @param versionPanel
     * @param dateStart
     * @param idTextPanel
     * @param active
     * @param id
     * @param textPanel
     * @param dateEnd
     * @param createDate
     */
    public Version(String id, float versionPanel, String createDate, long idPanel, Object idTextPanel, boolean active, List<TextPanel> textPanel, String dateStart, String dateEnd, String lastVersionId) {
        super();
        this.id = id;
        this.versionPanel = versionPanel;
        this.createDate = createDate;
        this.idPanel = idPanel;
        this.idTextPanel = idTextPanel;
        this.active = active;
        this.textPanel = textPanel;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.lastVersionId = lastVersionId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(versionPanel);
        dest.writeValue(createDate);
        dest.writeValue(idPanel);
        dest.writeValue(idTextPanel);
        dest.writeValue(active);
        dest.writeList(textPanel);
        dest.writeValue(dateStart);
        dest.writeValue(dateEnd);
        dest.writeValue(lastVersionId);
    }

    public int describeContents() {
        return 0;
    }
}