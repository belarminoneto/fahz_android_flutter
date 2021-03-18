package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationOptions implements Parcelable
{

    @SerializedName("Id")
    @Expose
    public int id;
    @SerializedName("DescriptionOption")
    @Expose
    public String descriptionOption;
    @SerializedName("AnswerUser")
    @Expose
    public boolean answerUser;
    public final static Parcelable.Creator<NotificationOptions> CREATOR = new Creator<NotificationOptions>() {

        @SuppressWarnings({
                "unchecked"
        })
        public NotificationOptions createFromParcel(Parcel in) {
            return new NotificationOptions(in);
        }

        public NotificationOptions[] newArray(int size) {
            return (new NotificationOptions[size]);
        }

    };

    protected NotificationOptions(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.descriptionOption = ((String) in.readValue((String.class.getClassLoader())));
        this.answerUser = ((boolean) in.readValue((boolean.class.getClassLoader())));
    }

    public NotificationOptions() {
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("id");
        s.append(id);
        s.append("descriptionOption");
        s.append(descriptionOption);
        s.append("answerUser");
        s.append(answerUser);

        return s.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(descriptionOption);
        dest.writeValue(answerUser);
    }

    public int describeContents() {
        return 0;
    }

}