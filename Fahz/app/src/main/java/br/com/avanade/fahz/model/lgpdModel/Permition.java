package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Permition implements Parcelable
{

    @SerializedName("Id")
    @Expose
    public int id;
    @SerializedName("DescriptionHeader")
    @Expose
    public String descriptionHeader;
    @SerializedName("AutorizationId")
    @Expose
    public int autorizationId;
    @SerializedName("OptionId")
    @Expose
    public int optionId;
    @SerializedName("NotificationOptions")
    @Expose
    public NotificationOptions notificationOptions;
    @SerializedName("NotificationAutorization")
    @Expose
    public NotificationAutorization notificationAutorization;
    public final static Parcelable.Creator<Permition> CREATOR = new Creator<Permition>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Permition createFromParcel(Parcel in) {
            return new Permition(in);
        }

        public Permition[] newArray(int size) {
            return (new Permition[size]);
        }

    };

    protected Permition(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.descriptionHeader = ((String) in.readValue((String.class.getClassLoader())));
        this.autorizationId = ((int) in.readValue((int.class.getClassLoader())));
        this.optionId = ((int) in.readValue((int.class.getClassLoader())));
        this.notificationOptions = ((NotificationOptions) in.readValue((NotificationOptions.class.getClassLoader())));
        this.notificationAutorization = ((NotificationAutorization) in.readValue((NotificationAutorization.class.getClassLoader())));
    }

    public Permition() {
    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();
        s.append("id");
        s.append(id);
        s.append("descriptionHeader");
        s.append(descriptionHeader);
        s.append("autorizationId");
        s.append(autorizationId);
        s.append("optionId");
        s.append(optionId);
        s.append("notificationOptions");
        s.append(notificationOptions);
        s.append("notificationAutorization");
        s.append(notificationAutorization);

        return s.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(descriptionHeader);
        dest.writeValue(autorizationId);
        dest.writeValue(optionId);
        dest.writeValue(notificationOptions);
        dest.writeValue(notificationAutorization);
    }

    public int describeContents() {
        return 0;
    }

}