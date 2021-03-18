package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class BankResponse implements Parcelable {
    @SerializedName("CPF")
    public String cpf;
    @SerializedName("Bank")
    public int bank;
    @SerializedName("BankBranch")
    public String agency;
    @SerializedName("DigitBankBranch")
    public String agencydigit;
    @SerializedName("Account")
    public String account;
    @SerializedName("DigitAccount")
    public String accountdigit;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cpf);
        dest.writeInt(this.bank);
        dest.writeString(this.agency);
        dest.writeString(this.agencydigit);
        dest.writeString(this.account);
        dest.writeString(this.accountdigit);
    }

    public BankResponse() {
    }

    protected BankResponse(Parcel in) {
        this.cpf = in.readString();
        this.bank = in.readInt();
        this.agency = in.readString();
        this.agencydigit = in.readString();
        this.account = in.readString();
        this.accountdigit = in.readString();
    }

    public static final Parcelable.Creator<BankResponse> CREATOR = new Parcelable.Creator<BankResponse>() {
        @Override
        public BankResponse createFromParcel(Parcel source) {
            return new BankResponse(source);
        }

        @Override
        public BankResponse[] newArray(int size) {
            return new BankResponse[size];
        }
    };
}
