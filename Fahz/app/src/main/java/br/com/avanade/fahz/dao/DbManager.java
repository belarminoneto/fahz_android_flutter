package br.com.avanade.fahz.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import br.com.avanade.fahz.LogUtils;

public class DbManager extends SQLiteOpenHelper {

    private static final String DB_NAME = "fahz";
    private static final int DB_VERSION = 3;

    static final String TB_QUESTIONNAIRE = "tb_questionnaire";
    static final String QUESTIONNAIRE_ID = "questionnaire_id";
    static final String QUESTIONNAIRE_IDTYPE = "questionnaire_idtype";
    static final String QUESTIONNAIRE_TYPE = "questionnaire_type";
    static final String QUESTIONNAIRE_DESC = "questionnaire_desc";
    static final String QUESTIONNAIRE_NUMBER = "questionnaire_number";

    static final String TB_QUESTION = "tb_question";
    static final String QUESTION_ORDER = "question_order";
    static final String QUESTION_QUESTIONNAIRE_ID = "question_questionnaire_id";
    static final String QUESTION_QUESTIONNAIRE_IDTYPE = "question_questionnaire_idtype";
    static final String QUESTION_TYPE = "question_type";
    static final String QUESTION_NUMBER = "question_number";
    static final String QUESTION_INTER_COD = "question_inter_cod";
    static final String QUESTION_TEXT = "question_text";
    static final String QUESTION_REQUIRED = "question_required";
    static final String QUESTION_ONLY_RETIRED = "question_only_retired";
    static final String QUESTION_OMIT_RETIRED = "question_omit_retired";
    static final String QUESTION_OPTIONS = "question_options";
    static final String QUESTION_DEFAULT_OPTION = "question_default_option";
    static final String QUESTION_EXPLICATIVE_TEXT = "question_explicative_text";
    static final String QUESTION_EXPLICATIVE_IMAGE = "question_explicative_image";
    static final String QUESTION_INTERN_COD_DEP = "question_intern_cod_dep";
    static final String QUESTION_CONDITIONAL_DEP = "question_conditional_dep";
    static final String QUESTION_OPTION_DEP = "question_option_dep";
    static final String QUESTION_CONDITIONAL_NC = "question_conditional_nc";
    static final String QUESTION_OPTION_NC = "question_option_nc";
    static final String QUESTION_EXCLUDE_OPTION = "question_exclude_option";
    static final String QUESTION_CONDITION_VALIDATE = "question_condition_validate";
    static final String QUESTION_OPTIONS_VALIDATE = "question_options_validate";
    static final String QUESTION_CONDITION_AGE_DEP = "question_condition_age_dep";
    static final String QUESTION_OPTIONS_AGE_DEP = "question_options_age_dep";
    static final String QUESTION_CONDITION_AGE_NC = "question_condition_age_nc";
    static final String QUESTION_OPTIONS_AGE_NC = "question_options_age_nc";
    static final String QUESTION_CAN_MULTIPLE = "question_can_multiple";

    static final String TB_SUPPLEMENTARY_DATA = "tb_supplementary_data";
    static final String SUPPLEMENTARY_DATA_CPF = "supplementary_data_cpf";
    static final String SUPPLEMENTARY_DATA_WEIGHT = "supplementary_data_weight";
    static final String SUPPLEMENTARY_DATA_HEIGHT = "supplementary_data_height";
    static final String SUPPLEMENTARY_DATA_BMI = "supplementary_data_bmi";
    static final String SUPPLEMENTARY_DATA_BMI_RANGE = "supplementary_data_bmi_range";
    static final String SUPPLEMENTARY_DATA_RELIGION_FK = "supplementary_data_religion_fk";

    static final String TB_ANSWER = "tb_answer";
    static final String ANSWER_ID = "answer_id";
    static final String ANSWER_INTER_COD = "answer_inter_cod";
    static final String ANSWER_TEXT = "answer_text";
    static final String ANSWER_LIFE_CPF = "answer_life_cpf";
    static final String ANSWER_USER_CPF = "answer_user_cpf";
    static final String ANSWER_CHANGED = "answer_changed";
    static final String ANSWER_IDTYPE = "answer_idtype";
    static final String ANSWER_QUESTIONNAIRE_ID = "answer_questionnaire_id";
    static final String ANSWER_EXCLUDE_OPTION = "answer_exclude_option";

    DbManager(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String createTbQuestionnaire = "CREATE TABLE " + TB_QUESTIONNAIRE + "("
                    + QUESTIONNAIRE_ID + " INTEGER primary key,"
                    + QUESTIONNAIRE_IDTYPE + " INTEGER NOT NULL,"
                    + QUESTIONNAIRE_TYPE + " TEXT NOT NULL,"
                    + QUESTIONNAIRE_DESC + " TEXT NOT NULL,"
                    + QUESTIONNAIRE_NUMBER + " INTEGER NOT NULL"
                    + ")";
            db.execSQL(createTbQuestionnaire);

            String createTbQuestion = "CREATE TABLE " + TB_QUESTION + "("
                    + QUESTION_ORDER + " INTEGER NOT NULL,"
                    + QUESTION_QUESTIONNAIRE_ID + " INTEGER NOT NULL,"
                    + QUESTION_QUESTIONNAIRE_IDTYPE + " INTEGER NOT NULL,"
                    + QUESTION_TYPE + " INTEGER NOT NULL,"
                    + QUESTION_NUMBER + " INTEGER NOT NULL,"
                    + QUESTION_INTER_COD + " INTEGER primary key,"
                    + QUESTION_TEXT + " TEXT NOT NULL,"
                    + QUESTION_REQUIRED + " INTEGER NOT NULL,"
                    + QUESTION_ONLY_RETIRED + " INTEGER NOT NULL,"
                    + QUESTION_OMIT_RETIRED + " INTEGER NOT NULL,"
                    + QUESTION_OPTIONS + " TEXT,"
                    + QUESTION_DEFAULT_OPTION + " TEXT,"
                    + QUESTION_EXPLICATIVE_TEXT + " TEXT,"
                    + QUESTION_EXPLICATIVE_IMAGE + " INTEGER,"
                    + QUESTION_INTERN_COD_DEP + " INTEGER,"
                    + QUESTION_CONDITIONAL_DEP + " INTEGER,"
                    + QUESTION_OPTION_DEP + " TEXT,"
                    + QUESTION_CONDITIONAL_NC + " INTEGER,"
                    + QUESTION_OPTION_NC + " TEXT,"
                    + QUESTION_EXCLUDE_OPTION + " TEXT,"
                    + QUESTION_CONDITION_VALIDATE + " INTEGER,"
                    + QUESTION_OPTIONS_VALIDATE + " TEXT,"
                    + QUESTION_CONDITION_AGE_DEP + " INTEGER,"
                    + QUESTION_OPTIONS_AGE_DEP + " TEXT,"
                    + QUESTION_CONDITION_AGE_NC + " INTEGER,"
                    + QUESTION_OPTIONS_AGE_NC + " TEXT,"
                    + QUESTION_CAN_MULTIPLE + " INTEGER"
                    + ")";
            db.execSQL(createTbQuestion);

            String createTbSupplementaryData = "CREATE TABLE " + TB_SUPPLEMENTARY_DATA + "("
                    + SUPPLEMENTARY_DATA_CPF + " TEXT primary key,"
                    + SUPPLEMENTARY_DATA_WEIGHT + " REAL,"
                    + SUPPLEMENTARY_DATA_HEIGHT + " REAL,"
                    + SUPPLEMENTARY_DATA_BMI + " REAL,"
                    + SUPPLEMENTARY_DATA_BMI_RANGE + " TEXT,"
                    + SUPPLEMENTARY_DATA_RELIGION_FK + " INTEGER"
                    + ")";
            db.execSQL(createTbSupplementaryData);

            String createTbAnswer = "CREATE TABLE " + TB_ANSWER + "("
                    + ANSWER_ID + " INTEGER primary key,"
                    + ANSWER_INTER_COD + " INTEGER NOT NULL,"
                    + ANSWER_TEXT + " TEXT NOT NULL,"
                    + ANSWER_LIFE_CPF + " TEXT NOT NULL,"
                    + ANSWER_USER_CPF + " TEXT NOT NULL,"
                    + ANSWER_CHANGED + " INTEGER NOT NULL,"
                    + ANSWER_IDTYPE + " INTEGER NOT NULL,"
                    + ANSWER_QUESTIONNAIRE_ID + " INTEGER NOT NULL,"
                    + ANSWER_EXCLUDE_OPTION + " INTEGER NOT NULL"
                    + ")";
            db.execSQL(createTbAnswer);
        } catch (Exception e) {
            LogUtils.error("SQL ERROR", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_QUESTIONNAIRE);
        db.execSQL("DROP TABLE IF EXISTS " + TB_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TB_SUPPLEMENTARY_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TB_ANSWER);
        onCreate(db);
    }
}

