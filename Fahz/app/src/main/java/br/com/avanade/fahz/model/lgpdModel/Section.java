package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Section implements Parcelable {
    @SerializedName("Id")
    @Expose
    public int id;
    @SerializedName("Name")
    @Expose
    public String name;
    @SerializedName("Panel")
    @Expose
    public List<Panel> panel = null;
    public final static Parcelable.Creator<Section> CREATOR = new Creator<Section>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Section createFromParcel(Parcel in) {
            return new Section(in);
        }

        public Section[] newArray(int size) {
            return (new Section[size]);
        }

    };

    protected Section(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.panel, (Panel.class.getClassLoader()));
    }

    /**
     *
     * @param name
     * @param id
     * @param panel
     */
    public Section(int id, String name, List<Panel> panel) {
        super();
        this.id = id;
        this.name = name;
        this.panel = panel;
    }

    public Section() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeList(panel);
    }

    public int describeContents() {
        return hashCode();
    }

}