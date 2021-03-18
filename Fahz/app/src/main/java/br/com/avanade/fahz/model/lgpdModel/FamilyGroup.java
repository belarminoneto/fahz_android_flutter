package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FamilyGroup implements Parcelable
{

    @SerializedName("isOpen")
    @Expose
    public Boolean isOpen;
    @SerializedName("Id")
    @Expose
    public String id;
    @SerializedName("Name")
    @Expose
    public String name;
    @SerializedName("Cpf")
    @Expose
    public String cpf;
    @SerializedName("Ownership")
    @Expose
    public String ownership;
    @SerializedName("AcceptedConditionsTitle")
    @Expose
    public String acceptedConditionsTitle;

    @SerializedName("AcceptedConditions")
    @Expose
    public List<AcceptedCondition> acceptedConditions = null;

    @SerializedName("SharedInformationPerPartnerSent")
    @Expose
    public List<SharedInformationPerPartnerSend> sharedInformationPerPartnerSent = null;

    @SerializedName("SharedInformationPerPartnerReceived")
    @Expose
    public List<SharedInformationPerPartnerReceived> sharedInformationPerPartnerReceived = null;


    public final static Parcelable.Creator<FamilyGroup> CREATOR = new Creator<FamilyGroup>() {

        @SuppressWarnings({
                "unchecked"
        })
        public FamilyGroup createFromParcel(Parcel in) {
            return new FamilyGroup(in);
        }

        public FamilyGroup[] newArray(int size) {
            return (new FamilyGroup[size]);
        }
    };

    protected FamilyGroup(Parcel in) {
        this.isOpen = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.cpf = ((String) in.readValue((String.class.getClassLoader())));
        this.ownership = ((String) in.readValue((String.class.getClassLoader())));
        this.acceptedConditionsTitle = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.acceptedConditions, (AcceptedCondition.class.getClassLoader()));
        in.readList(this.sharedInformationPerPartnerSent, (SharedInformationPerPartnerSend.class.getClassLoader()));
        in.readList(this.sharedInformationPerPartnerReceived, (SharedInformationPerPartnerReceived.class.getClassLoader()));
    }

    public FamilyGroup() {
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("id");
        sb.append(id);
        sb.append("name");
        sb.append(name);
        sb.append("cpf");
        sb.append(cpf);
        sb.append("ownership");
        sb.append(ownership);
        sb.append("acceptedConditionsTitle");
        sb.append(acceptedConditionsTitle);
        sb.append("acceptedConditions");
        sb.append(acceptedConditions);
        sb.append("sharedInformationPerPartnerSent");
        sb.append(sharedInformationPerPartnerSent);
        sb.append("sharedInformationPerPartnerReceived");
        sb.append(sharedInformationPerPartnerReceived);
        sb.append("isOpen");
        sb.append(isOpen);

        return sb.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(cpf);
        dest.writeValue(ownership);
        dest.writeValue(acceptedConditionsTitle);
        dest.writeList(acceptedConditions);
        dest.writeList(sharedInformationPerPartnerSent);
        dest.writeList(sharedInformationPerPartnerReceived);
        dest.writeValue(isOpen);
    }

    public int describeContents() {
        return 0;
    }


}