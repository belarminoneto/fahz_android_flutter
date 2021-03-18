package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SharedInformationPerPartnerReceived implements Parcelable {

    public final static Parcelable.Creator<SharedInformationPerPartnerReceived> CREATOR = new Creator<SharedInformationPerPartnerReceived>() {

        @SuppressWarnings({
                "unchecked"
        })
        public SharedInformationPerPartnerReceived createFromParcel(Parcel in) {
            return new SharedInformationPerPartnerReceived(in);
        }

        public SharedInformationPerPartnerReceived[] newArray(int size) {
            return (new SharedInformationPerPartnerReceived[size]);
        }
    };
    @SerializedName("PartnerName")
    @Expose
    public String partnerName;
    @SerializedName("Type")
    @Expose
    public String type;
    @SerializedName("BenefitName")
    @Expose
    public String benefitName;
    @SerializedName("SharedInformations")
    @Expose
    public List<SharedInformation> sharedInformations = null;

    protected SharedInformationPerPartnerReceived(Parcel in) {
        this.partnerName = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.benefitName = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.sharedInformations, (SharedInformation.class.getClassLoader()));
    }

    public SharedInformationPerPartnerReceived() {
    }


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("partnerName");
        sb.append(partnerName);
        sb.append("type");
        sb.append(type);
        sb.append("benefitName");
        sb.append(benefitName);
        sb.append("sharedInformations");
        sb.append(sharedInformations);

        return sb.toString();
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(partnerName);
        dest.writeValue(type);
        dest.writeValue(benefitName);
        dest.writeList(sharedInformations);
    }

    public int describeContents() {
        return 0;
    }

}