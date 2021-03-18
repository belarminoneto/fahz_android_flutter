package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class AcceptedCondition implements Parcelable
{

    @SerializedName("Id")
    @Expose
    public String id;
    @SerializedName("Content")
    @Expose
    public String content;
    @SerializedName("AcceptDate")
    @Expose
    public String acceptDate;
    @SerializedName("Status")
    @Expose
    public String status;
    @SerializedName("EndDate")
    @Expose
    public String endDate;
    @SerializedName("FilePath")
    @Expose
    public String filePath;
    @SerializedName("BenefitName")
    @Expose
    public String benefitName;
    @SerializedName("Cpf")
    @Expose
    public String cpf;
    @SerializedName("TermVersion")
    @Expose
    public String termVersion;
    public final static Parcelable.Creator<AcceptedCondition> CREATOR = new Creator<AcceptedCondition>() {

        @SuppressWarnings({
                "unchecked"
        })
        public AcceptedCondition createFromParcel(Parcel in) {
            return new AcceptedCondition(in);
        }

        public AcceptedCondition[] newArray(int size) {
            return (new AcceptedCondition[size]);
        }
    };

    protected AcceptedCondition(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.content = ((String) in.readValue((String.class.getClassLoader())));
        this.acceptDate = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.endDate = ((String) in.readValue((String.class.getClassLoader())));
        this.filePath = ((String) in.readValue((String.class.getClassLoader())));
        this.benefitName = ((String) in.readValue((String.class.getClassLoader())));
        this.cpf = ((String) in.readValue((String.class.getClassLoader())));
        this.termVersion = ((String) in.readValue((String.class.getClassLoader())));
    }

    public AcceptedCondition() {
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("id");
        sb.append(id);
        sb.append("content");
        sb.append(content);
        sb.append("acceptDate");
        sb.append(acceptDate);
        sb.append("status");
        sb.append(status);
        sb.append("endDate");
        sb.append(endDate);
        sb.append("filePath");
        sb.append(filePath);
        sb.append("benefitName");
        sb.append(benefitName);
        sb.append("cpf");
        sb.append(cpf);
        sb.append("termVersion");
        sb.append(termVersion);

        return sb.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(content);
        dest.writeValue(acceptDate);
        dest.writeValue(status);
        dest.writeValue(endDate);
        dest.writeValue(filePath);
        dest.writeValue(benefitName);
        dest.writeValue(cpf);
        dest.writeValue(termVersion);
    }

    public int describeContents() {
        return 0;
    }

}