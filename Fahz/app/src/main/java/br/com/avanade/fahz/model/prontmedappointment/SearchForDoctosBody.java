package br.com.avanade.fahz.model.prontmedappointment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

public class SearchForDoctosBody extends CPFInBody {
    @SerializedName("top")
    @Expose
    private int top;
    @SerializedName("skip")
    @Expose
    private int skip;
    @SerializedName("specializationId")
    @Expose
    private long specializationId;

    public SearchForDoctosBody(String cpf, int top, int skip, long specializationId) {
        super(cpf);
        this.top = top;
        this.skip = skip;
        this.specializationId = specializationId;
    }

}
