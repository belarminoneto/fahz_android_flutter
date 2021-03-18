package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuickLinkPanel implements Parcelable
{

    @SerializedName("LinkTo")
    @Expose
    public String linkTo;
    @SerializedName("Description")
    @Expose
    public String description;
    @SerializedName("Link")
    @Expose
    public String link;
    public final static Parcelable.Creator<QuickLinkPanel> CREATOR = new Creator<QuickLinkPanel>() {

        @SuppressWarnings({
                "unchecked"
        })
        public QuickLinkPanel createFromParcel(Parcel in) {
            return new QuickLinkPanel(in);
        }

        public QuickLinkPanel[] newArray(int size) {
            return (new QuickLinkPanel[size]);
        }
    };

    protected QuickLinkPanel(Parcel in) {
        this.linkTo = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.link = ((String) in.readValue((String.class.getClassLoader())));
    }

    public QuickLinkPanel() {
    }

    /**
     *
     * @param linkTo
     * @param link
     * @param description
     */
    public QuickLinkPanel(String linkTo, String description, String link) {
        super();
        this.linkTo = linkTo;
        this.description = description;
        this.link = link;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(linkTo);
        dest.writeValue(description);
        dest.writeValue(link);
    }

    public int describeContents() {
        return hashCode();
    }
}