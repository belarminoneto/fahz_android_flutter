package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AcceptData implements Parcelable
{
    @SerializedName("Cpf")
    @Expose
    public String cpf;
    @SerializedName("Type")
    @Expose
    public int type;
    @SerializedName("IpAddress")
    @Expose
    public String ipAddress;
    @SerializedName("Browser")
    @Expose
    public String browser;
    @SerializedName("VersionBrowser")
    @Expose
    public String versionBrowser;
    @SerializedName("OperationalSystem")
    @Expose
    public String operationalSystem;
    @SerializedName("AppVersion")
    @Expose
    public String appVersion;
    @SerializedName("Device")
    @Expose
    public String device;
    @SerializedName("VersionPanelId")
    @Expose
    public String versionPanelId;
    public final static Parcelable.Creator<AcceptData> CREATOR = new Creator<AcceptData>() {

        @SuppressWarnings({
                "unchecked"
        })

        public AcceptData createFromParcel(Parcel in) {
            return new AcceptData(in);
        }

        public AcceptData[] newArray(int size) {
            return (new AcceptData[size]);
        }
    };

    protected AcceptData(Parcel in) {
        this.cpf = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((int) in.readValue((int.class.getClassLoader())));
        this.ipAddress = ((String) in.readValue((String.class.getClassLoader())));
        this.browser = ((String) in.readValue((String.class.getClassLoader())));
        this.versionBrowser = ((String) in.readValue((String.class.getClassLoader())));
        this.operationalSystem = ((String) in.readValue((String.class.getClassLoader())));
        this.appVersion = ((String) in.readValue((String.class.getClassLoader())));
        this.device = ((String) in.readValue((String.class.getClassLoader())));
        this.versionPanelId = ((String) in.readValue((String.class.getClassLoader())));
    }

    public AcceptData() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(cpf);
        dest.writeValue(type);
        dest.writeValue(ipAddress);
        dest.writeValue(browser);
        dest.writeValue(versionBrowser);
        dest.writeValue(operationalSystem);
        dest.writeValue(appVersion);
        dest.writeValue(device);
        dest.writeValue(versionPanelId);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "{" +
                "Cpf='" + cpf + '\'' +
                ", Type=" + type +
                ", IpAddress='" + ipAddress + '\'' +
                ", OperationalSystem='" + operationalSystem + '\'' +
                ", AppVersion='" + appVersion + '\'' +
                ", Device='" + device + '\'' +
                ", VersionPanelId=" + versionPanelId +
                '}';
    }
}