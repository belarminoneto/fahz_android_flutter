package br.com.avanade.fahz.model.prontmedappointment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

public class ListOfAppointmentsBody extends CPFInBody {

    @SerializedName("top")
    @Expose
    private int top;
    @SerializedName("skip")
    @Expose
    private int skip;

    public ListOfAppointmentsBody(String cpf, int top, int skip) {
        super(cpf);
        this.top = top;
        this.skip = skip;
    }
}
