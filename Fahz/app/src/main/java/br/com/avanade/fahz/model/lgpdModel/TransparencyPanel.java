package br.com.avanade.fahz.model.lgpdModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransparencyPanel implements Parcelable {

    public final static Parcelable.Creator<TransparencyPanel> CREATOR = new Creator<TransparencyPanel>() {

        @SuppressWarnings({
                "unchecked"
        })
        public TransparencyPanel createFromParcel(Parcel in) {
            return new TransparencyPanel(in);
        }

        public TransparencyPanel[] newArray(int size) {
            return (new TransparencyPanel[size]);
        }
    };
    @SerializedName("Id")
    @Expose
    public String id;
    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("Order")
    @Expose
    public int order;
    @SerializedName("PersonQuantityType")
    @Expose
    public String personQuantityType;
    @SerializedName("FamilyGroup")
    @Expose
    public List<FamilyGroup> familyGroup = null;

    protected TransparencyPanel(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.order = ((int) in.readValue((int.class.getClassLoader())));
        this.personQuantityType = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.familyGroup, (FamilyGroup.class.getClassLoader()));
    }

    public TransparencyPanel() {
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("id");
        sb.append(id);
        sb.append("title");
        sb.append(title);
        sb.append("order");
        sb.append(order);
        sb.append("personQuantityType");
        sb.append(personQuantityType);
        sb.append("familyGroup");
        sb.append(familyGroup);

        return sb.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(title);
        dest.writeValue(order);
        dest.writeValue(personQuantityType);
        dest.writeList(familyGroup);
    }

    public int describeContents() {
        return 0;
    }

}