package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationAutorization implements Parcelable
{

    @SerializedName("Id")
    @Expose
    public int id;
    @SerializedName("DescriptionQuestionAutorization")
    @Expose
    public String descriptionQuestionAutorization;
    @SerializedName("DescriptionQuestionDetails")
    @Expose
    public String descriptionQuestionDetails;
    public final static Parcelable.Creator<NotificationAutorization> CREATOR = new Creator<NotificationAutorization>() {

        @SuppressWarnings({
                "unchecked"
        })
        public NotificationAutorization createFromParcel(Parcel in) {
            return new NotificationAutorization(in);
        }

        public NotificationAutorization[] newArray(int size) {
            return (new NotificationAutorization[size]);
        }

    };

    protected NotificationAutorization(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.descriptionQuestionAutorization = ((String) in.readValue((String.class.getClassLoader())));
        this.descriptionQuestionDetails = ((String) in.readValue((String.class.getClassLoader())));
    }

    public NotificationAutorization() {
    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append("id");
        s.append(id);
        s.append("descriptionQuestionAutorization");
        s.append(descriptionQuestionAutorization);
        s.append("descriptionQuestionDetails");
        s.append(descriptionQuestionDetails);

        return s.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(descriptionQuestionAutorization);
        dest.writeValue(descriptionQuestionDetails);
    }

    public int describeContents() {
        return 0;
    }

}