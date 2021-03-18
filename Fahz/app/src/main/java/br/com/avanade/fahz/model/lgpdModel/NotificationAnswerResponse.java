package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationAnswerResponse implements Parcelable {
    @SerializedName("NotificationAnswer")
    @Expose
    public List<NotificationAnswer> notificationAnswer = null;
    public final static Parcelable.Creator<NotificationAnswerResponse> CREATOR = new Creator<NotificationAnswerResponse>() {

        @SuppressWarnings({
                "unchecked"
        })
        public NotificationAnswerResponse createFromParcel(Parcel in) {
            return new NotificationAnswerResponse(in);
        }

        public NotificationAnswerResponse[] newArray(int size) {
            return (new NotificationAnswerResponse[size]);
        }
    };

    protected NotificationAnswerResponse(Parcel in) {
        in.readList(this.notificationAnswer, (NotificationAnswer.class.getClassLoader()));
    }

    public NotificationAnswerResponse() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(notificationAnswer);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "{" +
                "NotificationAnswer=" + notificationAnswer.toString() +
                '}';
    }

}
