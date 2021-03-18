package br.com.avanade.fahz.model.externalaccess;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

public class ExternalAccessUrlBody extends CPFInBody {
    @SerializedName("environment")
    @Expose
    private Integer environment;
    @SerializedName("platform")
    @Expose
    private Integer platform;
    @SerializedName("id")
    @Expose
    private Integer id;

    public ExternalAccessUrlBody(String cpf, Integer environment, Integer platform, Integer id) {
        super(cpf);
        this.environment = environment;
        this.platform = platform;
        this.id = id;
    }
}
