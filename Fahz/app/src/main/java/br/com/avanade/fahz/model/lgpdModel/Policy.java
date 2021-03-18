package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class Policy implements Parcelable {
    public final static Creator<Policy> CREATOR = new Creator<Policy>() {

        public Policy createFromParcel(Parcel in) {
            return new Policy(in);
        }

        public Policy[] newArray(int size) {
            return (new Policy[size]);
        }
    };
    @SerializedName("policies")
    @Expose
    public Policies policies;

    protected Policy(Parcel in) {
        this.policies = ((Policies) in.readValue((Policies.class.getClassLoader())));
    }

    public Policy() {
    }

    @NotNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("policies");
        sb.append(policies);

        return sb.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(policies);
    }

    public int describeContents() {
        return 0;
    }
}
