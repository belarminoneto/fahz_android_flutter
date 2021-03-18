package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TermsGeneral implements Parcelable {
    @SerializedName("section")
    @Expose
    private List<Section> section = null;
    public final static Parcelable.Creator<TermsGeneral> CREATOR = new Creator<TermsGeneral>() {

        @SuppressWarnings({
                "unchecked"
        })
        public TermsGeneral createFromParcel(Parcel in) {
            return new TermsGeneral(in);
        }

        public TermsGeneral[] newArray(int size) {
            return (new TermsGeneral[size]);
        }

    };

    protected TermsGeneral(Parcel in) {
        in.readList(this.section, Section.class.getClassLoader());
    }

    public TermsGeneral() {
    }

    public List<Section> getSection() {
        return section;
    }

    public void setSection(List<Section> section) {
        this.section = section;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(section);
    }

    public int describeContents() {
        return 0;
    }

}