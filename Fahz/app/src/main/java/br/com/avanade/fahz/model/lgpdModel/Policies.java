package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class Policies implements Parcelable {
    public final static Creator<Policies> CREATOR = new Creator<Policies>() {

        public Policies createFromParcel(Parcel in) {
            return new Policies(in);
        }

        public Policies[] newArray(int size) {
            return (new Policies[size]);
        }
    };
    @SerializedName("VersionPanelId")
    @Expose
    public String versionPanelId;
    @SerializedName("CodeTerm")
    @Expose
    public String codeTerm;
    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("Text")
    @Expose
    public String text;
    @SerializedName("DocumentType")
    @Expose
    public int documentType;

    protected Policies(Parcel in) {
        this.versionPanelId = ((String) in.readValue((String.class.getClassLoader())));
        this.codeTerm = ((String) in.readValue((Object.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.text = ((String) in.readValue((String.class.getClassLoader())));
        this.documentType = ((int) in.readValue((long.class.getClassLoader())));
    }

    public Policies() {
    }

    @NotNull
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("versionPanelId");
        sb.append(versionPanelId);
        sb.append("codeTerm");
        sb.append(codeTerm);
        sb.append("title");
        sb.append(title);
        sb.append("text");
        sb.append(text);
        sb.append("documentType");
        sb.append(documentType);

        return sb.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(versionPanelId);
        dest.writeValue(codeTerm);
        dest.writeValue(title);
        dest.writeValue(text);
        dest.writeValue(documentType);
    }

    public int describeContents() {
        return 0;
    }
}