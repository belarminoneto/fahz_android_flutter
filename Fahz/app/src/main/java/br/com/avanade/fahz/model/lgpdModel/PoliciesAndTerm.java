package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PoliciesAndTerm implements Serializable, Parcelable {

    public final static Parcelable.Creator<PoliciesAndTerm> CREATOR = new Creator<PoliciesAndTerm>() {

        @SuppressWarnings({
                "unchecked"
        })
        public PoliciesAndTerm createFromParcel(Parcel in) {
            return new PoliciesAndTerm(in);
        }

        public PoliciesAndTerm[] newArray(int size) {
            return (new PoliciesAndTerm[size]);
        }
    };
    @SerializedName("VersionPanelId")
    @Expose
    public String versionPanelId;
    @SerializedName("Code")
    @Expose
    public String code;
    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("Text")
    @Expose
    public String text;
    @SerializedName("DocumentType")
    @Expose
    public int documentType;
    @SerializedName("ActionAfterNegativeAccept")
    @Expose
    public int actionAfterNegativeAccept;
    @SerializedName("Cpf")
    @Expose
    public String cpf;
    @SerializedName("IsHolder")
    @Expose
    public boolean isHolder;
    @SerializedName("Version")
    @Expose
    public String version;

    protected PoliciesAndTerm(Parcel in) {
        this.versionPanelId = ((String) in.readValue((String.class.getClassLoader())));
        this.code = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.text = ((String) in.readValue((String.class.getClassLoader())));
        this.documentType = ((int) in.readValue((int.class.getClassLoader())));
        this.actionAfterNegativeAccept = ((int) in.readValue((int.class.getClassLoader())));
        this.cpf = ((String) in.readValue((String.class.getClassLoader())));
        this.isHolder = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.version = ((String) in.readValue((String.class.getClassLoader())));
    }

    public PoliciesAndTerm() {
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("versionPanelId");
        sb.append(versionPanelId);
        sb.append("code");
        sb.append(code);
        sb.append("title");
        sb.append(title);
        sb.append("text");
        sb.append(text);
        sb.append("documentType");
        sb.append(documentType);
        sb.append("actionAfterNegativeAccept");
        sb.append(actionAfterNegativeAccept);
        sb.append("version");
        sb.append(version);
        sb.append("cpf");
        sb.append(cpf);
        sb.append("isHolder");
        sb.append(isHolder);

        return sb.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(versionPanelId);
        dest.writeValue(code);
        dest.writeValue(title);
        dest.writeValue(text);
        dest.writeValue(documentType);
        dest.writeValue(actionAfterNegativeAccept);
        dest.writeValue(cpf);
        dest.writeValue(isHolder);
        dest.writeValue(version);
    }

    public int describeContents() {
        return 0;
    }
}
