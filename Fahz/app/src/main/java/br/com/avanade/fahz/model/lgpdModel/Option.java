package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Option implements Parcelable
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
    public final static Parcelable.Creator<Option> CREATOR = new Creator<Option>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Option createFromParcel(Parcel in) {
            return new Option(in);
        }

        public Option[] newArray(int size) {
            return (new Option[size]);
        }

    };

    protected Option(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.descriptionOption = ((String) in.readValue((String.class.getClassLoader())));
        this.answerUser = ((boolean) in.readValue((boolean.class.getClassLoader())));
    }

    public Option() {
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