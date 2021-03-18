package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationAnswer implements Parcelable
{

    @SerializedName("IdLife")
    @Expose
    public String idLife;
    @SerializedName("IdAutorization")
    @Expose
    public int idAutorization;
    @SerializedName("IdOption")
    @Expose
    public int idOption;
    @SerializedName("Answer")
    @Expose
    public int answer;
    @SerializedName("cpf")
    @Expose
    public String cpf;
    public final static Parcelable.Creator<NotificationAnswer> CREATOR = new Creator<NotificationAnswer>() {

        @SuppressWarnings({
                "unchecked"
        })
        public NotificationAnswer createFromParcel(Parcel in) {
            return new NotificationAnswer(in);
        }

        public NotificationAnswer[] newArray(int size) {
            return (new NotificationAnswer[size]);
        }

    };

    protected NotificationAnswer(Parcel in) {
        this.idLife = ((String) in.readValue((String.class.getClassLoader())));
        this.idAutorization = ((int) in.readValue((int.class.getClassLoader())));
        this.idOption = ((int) in.readValue((int.class.getClassLoader())));
        this.answer = ((int) in.readValue((int.class.getClassLoader())));
        this.cpf = ((String) in.readValue((String.class.getClassLoader())));
    }

    public NotificationAnswer() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(idLife);
        dest.writeValue(idAutorization);
        dest.writeValue(idOption);
        dest.writeValue(answer);
        dest.writeValue(cpf);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "{" +
                " idAutorization=" + idAutorization +
                ", idOption=" + idOption +
                ", answer=" + answer +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}