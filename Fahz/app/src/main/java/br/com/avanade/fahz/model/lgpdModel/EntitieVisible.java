package br.com.avanade.fahz.model.lgpdModel;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EntitieVisible implements Serializable
{
    @SerializedName("V")
    @Expose
    public int canView;
    @SerializedName("E")
    @Expose
    public int canEdit;
    @SerializedName("I")
    @Expose
    public int idMenu;

    @Override
    public String toString() {
        return "{" +
                "idMenu=" + idMenu +
                ", canView=" + canView +
                ", canEdit=" + canEdit +
                "}";
    }
}
