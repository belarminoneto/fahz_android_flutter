package br.com.avanade.fahz.model.document;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.CPFInBody;

@SuppressWarnings("unused")
public class DocumentsMaterialSchoolBody extends CPFInBody {
    @SerializedName("planId")
    @Expose
    private Integer planId;
    @SerializedName("behaviorId")
    @Expose
    private Integer behaviorId;

    public DocumentsMaterialSchoolBody(String cpf, Integer planId, Integer behaviorId) {
        super(cpf);
        this.planId = planId;
        this.behaviorId = behaviorId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getBehaviorId() {
        return behaviorId;
    }

    public void setBehaviorId(Integer behaviorId) {
        this.behaviorId = behaviorId;
    }
}
