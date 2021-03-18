package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SharedInformation implements Parcelable {

    public final static Parcelable.Creator<SharedInformation> CREATOR = new Creator<SharedInformation>() {

        @SuppressWarnings({
                "unchecked"
        })
        public SharedInformation createFromParcel(Parcel in) {
            return new SharedInformation(in);
        }

        public SharedInformation[] newArray(int size) {
            return (new SharedInformation[size]);
        }
    };
    @SerializedName("Cpf")
    @Expose
    public String cpf;
    @SerializedName("ReceivedDate")
    @Expose
    public String receivedDate;
    @SerializedName("FormattedDate")
    @Expose
    public String formattedDate;
    @SerializedName("PartnerName")
    @Expose
    public String partnerName;
    @SerializedName("BenefitName")
    @Expose
    public String benefitName;
    @SerializedName("Description")
    @Expose
    public String description;
    @SerializedName("Type")
    @Expose
    public String type;

    protected SharedInformation(Parcel in) {
        this.cpf = ((String) in.readValue((String.class.getClassLoader())));
        this.receivedDate = ((String) in.readValue((String.class.getClassLoader())));
        this.formattedDate = ((String) in.readValue((String.class.getClassLoader())));
        this.partnerName = ((String) in.readValue((String.class.getClassLoader())));
        this.benefitName = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
    }

    public SharedInformation() {
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("cpf");
        sb.append(cpf);
        sb.append("receivedDate");
        sb.append(receivedDate);
        sb.append("formattedDate");
        sb.append(formattedDate);
        sb.append("partnerName");
        sb.append(partnerName);
        sb.append("benefitName");
        sb.append(benefitName);
        sb.append("description");
        sb.append(description);
        sb.append("type");
        sb.append(type);

        return sb.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(cpf);
        dest.writeValue(receivedDate);
        dest.writeValue(formattedDate);
        dest.writeValue(partnerName);
        dest.writeValue(benefitName);
        dest.writeValue(description);
        dest.writeValue(type);
    }

    public int describeContents() {
        return 0;
    }

}