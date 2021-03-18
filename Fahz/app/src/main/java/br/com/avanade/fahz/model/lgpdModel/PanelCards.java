package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PanelCards implements Parcelable
{

    @SerializedName("Id")
    @Expose
    public int id;
    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("Obs")
    @Expose
    public String obs;
    @SerializedName("UpdateDate")
    @Expose
    public String updateDate;

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

    protected PanelCards(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.obs = ((String) in.readValue((String.class.getClassLoader())));
        this.updateDate = ((String) in.readValue((String.class.getClassLoader())));
    }

    public PanelCards() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(title);
        dest.writeValue(obs);
        dest.writeValue(updateDate);
    }

    public int describeContents() {
        return 0;
    }

}
