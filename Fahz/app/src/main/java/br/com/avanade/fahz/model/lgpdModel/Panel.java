package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Panel implements Parcelable {

    @SerializedName("Id")
    @Expose
    public int id;
    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("Obs")
    @Expose
    public String obs;
    public final static Parcelable.Creator<Panel> CREATOR = new Creator<Panel>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Panel createFromParcel(Parcel in) {
            return new Panel(in);
        }

        public Panel[] newArray(int size) {
            return (new Panel[size]);
        }
    };
    @SerializedName("CreateDate")
    @Expose
    public String createDate;
    @SerializedName("IdSection")
    @Expose
    public long idSection;
    @SerializedName("IdForum")
    @Expose
    public Object idForum;
    @SerializedName("Forum")
    @Expose
    public Object forum;
    @SerializedName("Version")
    @Expose
    public List<Version> version = null;
    @SerializedName("CodePanel")
    @Expose
    public String codePanel;
    @SerializedName("UpdateDate")
    @Expose
    public String updateDate;

    protected Panel(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.obs = ((String) in.readValue((String.class.getClassLoader())));
        this.idSection = ((long) in.readValue((long.class.getClassLoader())));
        this.createDate = ((String) in.readValue((String.class.getClassLoader())));
        this.updateDate = ((String) in.readValue((String.class.getClassLoader())));
        this.idForum = in.readValue((Object.class.getClassLoader()));
        this.forum = in.readValue((Object.class.getClassLoader()));
        in.readList(this.version, (br.com.avanade.fahz.model.lgpdModel.Version.class.getClassLoader()));
        this.codePanel = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Panel() {
    }

    /**
     *
     * @param forum
     * @param codePanel
     * @param obs
     * @param updateDate
     * @param idSection
     * @param idForum
     * @param id
     * @param title
     * @param version
     * @param createDate
     */
    public Panel(int id, String title, String obs, long idSection, String createDate, String updateDate, Object idForum, Object forum, List<Version> version, String codePanel) {
        super();
        this.id = id;
        this.title = title;
        this.obs = obs;
        this.idSection = idSection;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.idForum = idForum;
        this.forum = forum;
        this.version = version;
        this.codePanel = codePanel;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("id");
        s.append(id);
        s.append("title");
        s.append(title);
        s.append("obs");
        s.append(obs);
        s.append("idSection");
        s.append(idSection);
        s.append("createDate");
        s.append(createDate);
        s.append("updateDate");
        s.append(updateDate);
        s.append("idForum");
        s.append(idForum);
        s.append("forum");
        s.append(forum);
        s.append("version");
        s.append(version);
        s.append("codePanel");
        s.append(codePanel);

        return s.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(title);
        dest.writeValue(obs);
        dest.writeValue(idSection);
        dest.writeValue(createDate);
        dest.writeValue(updateDate);
        dest.writeValue(idForum);
        dest.writeValue(forum);
        dest.writeList(version);
        dest.writeValue(codePanel);
    }

    public int describeContents() {
        return hashCode();
    }
}