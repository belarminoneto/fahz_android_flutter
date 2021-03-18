package br.com.avanade.fahz.util;

public class ConstantsAnamnesis {
    public static final int UNAUTHORIZED_ACCESS = 401;

    // EXTRA INTENT
    public static final String ID_TYPE_QUEST = "idTypeQuest";
    public static final String INTER_COD = "internalCode";
    public static final String ID_QUEST = "idQuest";
    public static final String AUX_DATA_INTENT = "aux_data_intent";
    public static final String AUX_DATA_TYPE = "aux_data_type";
    public static final String CAN_MULTI_OPTIONS_AUX_DATA = "canMultipleOptions";

    // START ACTIVITY FOR RESULT
    public static final int REQUEST_AUX_DATA = 1;

    // Countly keys
    public static final String MANUAL_LOGIN_KEY = "manualLoginEvent";
    // Request
    public static final String REFRESH_SESSION_KEY = "refreshSessionRequest";
    public static final String SEARCH_FAMILY_TREE_KEY = "searchForFamilyTreeByCPFRequest";
    public static final String SEARCH_LIFE_KEY = "searchLifeByFilterRequest";
    public static final String GET_QUESTIONNAIRE_KEY = "getQuestionnaireRequest";
    public static final String ANSWER_QUESTIONNAIRE_KEY = "answerQuestionnaireRequest";
    public static final String SEARCH_AUX_DATA_KEY = "searchAuxDataRequest";
    // View
    public static final String EDIT_ANSWER_VIEW_KEY = "editAnswerView";
    public static final String VIEW_ANSWERS_VIEW_KEY = "viewAnswersView";
    public static final String FAMILY_TREE_VIEW_KEY = "familyTreeAnamnesisView";
    public static final String QUEST_VIEW_KEY = "questionnaireView";
    public static final String SEARCH_AUX_DATA_VIEW_KEY = "searchAuxiliaryDataView";
    // Action
    public static final String NEXT_QUESTION_ACTION_KEY = "nextQuestionAction";
    public static final String PREVIOUS_QUESTION_ACTION_KEY = "previousQuestionAction";
    public static final String BACK_QUEST_ACTION_KEY = "backQuestionnaireAction";
    public static final String FINISH_QUEST_ACTION_KEY = "finishQuestionnaireAction";

}