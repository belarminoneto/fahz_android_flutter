package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TermOfService implements Parcelable {

    public final static Parcelable.Creator<TermOfService> CREATOR = new Creator<TermOfService>() {

        @SuppressWarnings({
                "unchecked"
        })
        public TermOfService createFromParcel(Parcel in) {
            return new TermOfService(in);
        }

        public TermOfService[] newArray(int size) {
            return (new TermOfService[size]);
        }
    };

    @SerializedName("count")
    @Expose
    public int count;
    @SerializedName("registers")
    @Expose
    public List<Register> registers = null;
    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    protected TermOfService(Parcel in) {
        this.count = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.registers, (Register.class.getClassLoader()));
    }

    public TermOfService() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(registers);
    }

    public int describeContents() {
        return 0;
    }

    public String getMessage() {
        return message;
    }

    public boolean hasMessage() {
        return message != null;
    }

}