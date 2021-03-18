package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.enums.SectionStatus;
import br.com.avanade.fahz.util.AnamnesisUtils;

public class LifeStatusAnamnesis extends LifeAnamnesis {

    @SerializedName("idtipoQuestionario")
    private int idTypeQuestionnaire;

    @SerializedName("tipoquestionario")
    private String typeQuestionnaire;

    @SerializedName("pendente")
    private int pending;

    @SerializedName("percentual")
    private float percentage;

    @SerializedName("editar")
    private int edit;

    private String statusPercentage;

    private SectionStatus anamnesisStatus;

    private void setupStatus() {
        if (pending == 0) {
            statusPercentage = "Concluído";
            anamnesisStatus = SectionStatus.DONE;
        } else if (percentage == 0) {
            statusPercentage = "Responder";
            anamnesisStatus = SectionStatus.TODO;
        } else {
            statusPercentage = "Continue de onde você parou";
            anamnesisStatus = SectionStatus.DOING;
        }
    }

    public int getIdTypeQuestionnaire() {
        return idTypeQuestionnaire;
    }

    public void setIdTypeQuestionnaire(int idTypeQuestionnaire) {
        this.idTypeQuestionnaire = idTypeQuestionnaire;
    }

    public String getTypeQuestionnaire() {
        return typeQuestionnaire;
    }

    public String getTypeQuestionnaireFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(typeQuestionnaire);
    }

    public void setTypeQuestionnaire(String typeQuestionnaire) {
        this.typeQuestionnaire = typeQuestionnaire;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getEdit() {
        return edit;
    }

    public void setEdit(int edit) {
        this.edit = edit;
    }

    public String getStatusPercentage() {
        setupStatus();
        return statusPercentage;
    }

    public SectionStatus getAnamnesisStatus() {
        setupStatus();
        return anamnesisStatus;
    }
}
