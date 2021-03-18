package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

public class Question {

    private long id;

    @SerializedName("ordem")
    private int order;

    @SerializedName("tipo")
    private int type;

    @SerializedName("numeracao")
    private int countable;

    @SerializedName("codigointerno")
    private long internalCode;

    @SerializedName("texto")
    private String text;

    @SerializedName("obrigatorio")
    private int required;

    @SerializedName("multipla")
    private int canMultiple;

    @SerializedName("aposentado")
    private int onlyRetired;

    @SerializedName("omitiraposentado")
    private int omitRetired;

    @SerializedName("usuario")
    private Object user;

    @SerializedName("opcoes")
    private String options;

    @SerializedName("opcaopadrao")
    private String defaultOption;

    @SerializedName("opcaoexcludente")
    private String excludeOption;

    @SerializedName("explicativo")
    private String explicative;

    @SerializedName("foto")
    private int photo;

    @SerializedName("codigointernodep")
    private long internalCodeDependency;

    @SerializedName("condicaodep")
    private int conditionDependency;

    @SerializedName("opcaodep")
    private String optionsDependency;

    @SerializedName("condicaonc")
    private int conditionNC;

    @SerializedName("opcaonc")
    private String optionsNC;

    @SerializedName("condicaovalidade")
    private int conditionValidate;

    @SerializedName("opcaovalidade")
    private String optionsValidate;

    @SerializedName("condicaoidadedep")
    private int conditionAgeDep;

    @SerializedName("opcaoidadedep")
    private String optionsAgeDep;

    @SerializedName("condicaoidadenc")
    private int conditionAgeNC;

    @SerializedName("opcaoidadenc")
    private String optionsAgeNC;

    @SerializedName("resposta")
    private Answer answer;

    private int count;

    private Question divider;

    private Questionnaire questionnaire;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(long internalCode) {
        this.internalCode = internalCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getDefaultOption() {
        return defaultOption;
    }

    public void setDefaultOption(String defaultOption) {
        this.defaultOption = defaultOption;
    }

    public String getExplicative() {
        return explicative;
    }

    public void setExplicative(String explicative) {
        this.explicative = explicative;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public long getInternalCodeDependency() {
        return internalCodeDependency;
    }

    public void setInternalCodeDependency(long internalCodeDependency) {
        this.internalCodeDependency = internalCodeDependency;
    }

    public int getConditionDependency() {
        return conditionDependency;
    }

    public void setConditionDependency(int conditionDependency) {
        this.conditionDependency = conditionDependency;
    }

    public String getOptionsDependency() {
        return optionsDependency;
    }

    public void setOptionsDependency(String optionsDependency) {
        this.optionsDependency = optionsDependency;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Question getDivider() {
        return divider;
    }

    public void setDivider(Question divider) {
        this.divider = divider;
    }

    public String getExcludeOption() {
        return excludeOption;
    }

    public void setExcludeOption(String excludeOption) {
        this.excludeOption = excludeOption;
    }

    public int getConditionValidate() {
        return conditionValidate;
    }

    public void setConditionValidate(int conditionValidate) {
        this.conditionValidate = conditionValidate;
    }

    public String getOptionsValidate() {
        return optionsValidate;
    }

    public void setOptionsValidate(String optionsValidate) {
        this.optionsValidate = optionsValidate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int isCountable() {
        return countable;
    }

    public void setCountable(int countable) {
        this.countable = countable;
    }

    public int isRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }

    public int getConditionNC() {
        return conditionNC;
    }

    public void setConditionNC(int conditionNC) {
        this.conditionNC = conditionNC;
    }

    public String getOptionsNC() {
        return optionsNC;
    }

    public void setOptionsNC(String optionsNC) {
        this.optionsNC = optionsNC;
    }

    public int getConditionAgeDep() {
        return conditionAgeDep;
    }

    public void setConditionAgeDep(int conditionAgeDep) {
        this.conditionAgeDep = conditionAgeDep;
    }

    public String getOptionsAgeDep() {
        return optionsAgeDep;
    }

    public void setOptionsAgeDep(String optionsAgeDep) {
        this.optionsAgeDep = optionsAgeDep;
    }

    public int getConditionAgeNC() {
        return conditionAgeNC;
    }

    public void setConditionAgeNC(int conditionAgeNC) {
        this.conditionAgeNC = conditionAgeNC;
    }

    public String getOptionsAgeNC() {
        return optionsAgeNC;
    }

    public void setOptionsAgeNC(String optionsAgeNC) {
        this.optionsAgeNC = optionsAgeNC;
    }

    public int getOnlyRetired() {
        return onlyRetired;
    }

    public void setOnlyRetired(int onlyRetired) {
        this.onlyRetired = onlyRetired;
    }

    public int getOmitRetired() {
        return omitRetired;
    }

    public void setOmitRetired(int omitRetired) {
        this.omitRetired = omitRetired;
    }

    public int getCanMultiple() {
        return canMultiple;
    }

    public void setCanMultiple(int canMultiple) {
        this.canMultiple = canMultiple;
    }
}