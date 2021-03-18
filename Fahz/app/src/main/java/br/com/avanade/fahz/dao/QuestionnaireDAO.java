package br.com.avanade.fahz.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.model.anamnesisModel.Answer;
import br.com.avanade.fahz.model.anamnesisModel.Question;
import br.com.avanade.fahz.model.anamnesisModel.Questionnaire;
import br.com.avanade.fahz.model.anamnesisModel.SupplementaryDataAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.SupplementaryDataAnamnesisRequest;

import static br.com.avanade.fahz.dao.DbManager.ANSWER_CHANGED;
import static br.com.avanade.fahz.dao.DbManager.ANSWER_EXCLUDE_OPTION;
import static br.com.avanade.fahz.dao.DbManager.ANSWER_ID;
import static br.com.avanade.fahz.dao.DbManager.ANSWER_IDTYPE;
import static br.com.avanade.fahz.dao.DbManager.ANSWER_INTER_COD;
import static br.com.avanade.fahz.dao.DbManager.ANSWER_LIFE_CPF;
import static br.com.avanade.fahz.dao.DbManager.ANSWER_QUESTIONNAIRE_ID;
import static br.com.avanade.fahz.dao.DbManager.ANSWER_TEXT;
import static br.com.avanade.fahz.dao.DbManager.ANSWER_USER_CPF;
import static br.com.avanade.fahz.dao.DbManager.QUESTIONNAIRE_DESC;
import static br.com.avanade.fahz.dao.DbManager.QUESTIONNAIRE_ID;
import static br.com.avanade.fahz.dao.DbManager.QUESTIONNAIRE_IDTYPE;
import static br.com.avanade.fahz.dao.DbManager.QUESTIONNAIRE_NUMBER;
import static br.com.avanade.fahz.dao.DbManager.QUESTIONNAIRE_TYPE;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_CAN_MULTIPLE;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_CONDITIONAL_DEP;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_CONDITIONAL_NC;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_CONDITION_AGE_DEP;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_CONDITION_AGE_NC;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_CONDITION_VALIDATE;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_DEFAULT_OPTION;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_EXCLUDE_OPTION;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_EXPLICATIVE_IMAGE;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_EXPLICATIVE_TEXT;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_INTERN_COD_DEP;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_INTER_COD;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_NUMBER;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_OMIT_RETIRED;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_ONLY_RETIRED;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_OPTIONS;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_OPTIONS_AGE_DEP;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_OPTIONS_AGE_NC;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_OPTIONS_VALIDATE;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_OPTION_DEP;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_OPTION_NC;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_ORDER;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_QUESTIONNAIRE_ID;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_QUESTIONNAIRE_IDTYPE;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_REQUIRED;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_TEXT;
import static br.com.avanade.fahz.dao.DbManager.QUESTION_TYPE;
import static br.com.avanade.fahz.dao.DbManager.SUPPLEMENTARY_DATA_BMI;
import static br.com.avanade.fahz.dao.DbManager.SUPPLEMENTARY_DATA_BMI_RANGE;
import static br.com.avanade.fahz.dao.DbManager.SUPPLEMENTARY_DATA_CPF;
import static br.com.avanade.fahz.dao.DbManager.SUPPLEMENTARY_DATA_HEIGHT;
import static br.com.avanade.fahz.dao.DbManager.SUPPLEMENTARY_DATA_RELIGION_FK;
import static br.com.avanade.fahz.dao.DbManager.SUPPLEMENTARY_DATA_WEIGHT;
import static br.com.avanade.fahz.dao.DbManager.TB_ANSWER;
import static br.com.avanade.fahz.dao.DbManager.TB_QUESTION;
import static br.com.avanade.fahz.dao.DbManager.TB_QUESTIONNAIRE;
import static br.com.avanade.fahz.dao.DbManager.TB_SUPPLEMENTARY_DATA;

public class QuestionnaireDAO {

    private SQLiteDatabase db;
    private DbManager dbManager;

    public QuestionnaireDAO(Context context) {
        dbManager = new DbManager(context);
    }

    // MARK: Business logic

    public boolean saveAnswer(Answer answer) {
        if (checkAnswer(answer)) {
            return updateAnswer(answer);
        } else {
            return insertAnswer(answer);
        }
    }

    public boolean saveSupplementaryData(SupplementaryDataAnamnesis data) {
        if (checkSupplementaryData(data)) {
            return updateSupplementaryData(data);
        } else {
            return insertSupplementaryData(data);
        }
    }

    // MARK: INSERT

    public boolean insertQuestionnaire(Questionnaire questionnaire) {
        db = dbManager.getWritableDatabase();
        // FIXME
        db.execSQL("DELETE FROM " + TB_ANSWER);
        db.execSQL("DELETE FROM " + TB_SUPPLEMENTARY_DATA);
        db.execSQL("DELETE FROM " + TB_QUESTION);
        db.execSQL("DELETE FROM " + TB_QUESTIONNAIRE);

        long result = db.insert(TB_QUESTIONNAIRE, null, questionnaireValues(questionnaire));
        db.close();
        return !(result == -1);
    }

    public boolean insertQuestion(Question question) {
        db = dbManager.getWritableDatabase();
        long result = db.insert(TB_QUESTION, null, questionValues(question));
        db.close();
        return !(result == -1);
    }

    private boolean insertAnswer(Answer answer) {
        db = dbManager.getWritableDatabase();
        long result = db.insert(TB_ANSWER, null, answerValues(answer));
        db.close();
        return !(result == -1);
    }

    private boolean insertSupplementaryData(SupplementaryDataAnamnesis data) {
        db = dbManager.getWritableDatabase();
        long result = db.insert(TB_SUPPLEMENTARY_DATA, null, supplementaryDataValues(data));
        db.close();
        return !(result == -1);
    }

    // MARK: UPDATE

    private boolean updateAnswer(Answer answer) {
        db = dbManager.getWritableDatabase();
        String where = String.format("%s = ?", ANSWER_INTER_COD);
        String[] whereArgs = {String.valueOf(answer.getInternalCode())};
        long result = db.update(TB_ANSWER, answerValues(answer), where, whereArgs);
        db.close();
        return !(result == -1);
    }

    private boolean updateSupplementaryData(SupplementaryDataAnamnesis data) {
        db = dbManager.getWritableDatabase();
        String where = String.format("%s = ?", SUPPLEMENTARY_DATA_CPF);
        String[] whereArgs = {String.valueOf(data.getCpf())};
        long result = db.update(TB_SUPPLEMENTARY_DATA, supplementaryDataValues(data), where, whereArgs);
        db.close();
        return !(result == -1);
    }

    // MARK: SELECT

    public Questionnaire selectQuestionnaire(Questionnaire questionnaire) {
        Cursor cursor;
        String where = String.format("%s = ?", QUESTIONNAIRE_ID);
        String[] whereArgs = {String.valueOf(questionnaire.getId())};
        db = dbManager.getReadableDatabase();
        cursor = db.query(TB_QUESTIONNAIRE, null, where, whereArgs, null, null, null, null);

        Questionnaire q = null;
        if (cursor != null) {
            cursor.moveToFirst();
            q = createQuestionnaire(cursor);
            cursor.close();
        }
        db.close();
        return q;
    }

    public List<Question> selectQuestions(Questionnaire questionnaire) {
        Cursor cursor;
        String where = String.format("%s = ?", QUESTION_QUESTIONNAIRE_ID);
        String[] whereArgs = {String.valueOf(questionnaire.getId())};
        db = dbManager.getReadableDatabase();
        cursor = db.query(TB_QUESTION, null, where, whereArgs, null, null, QUESTION_ORDER, null);

        ArrayList<Question> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                list.add(createQuestion(cursor));
            }
        } finally {
            cursor.close();
        }
        db.close();
        return list;
    }

    public SupplementaryDataAnamnesisRequest selectSupplementaryData(String lifeCPF) {
        Cursor cursor;
        String where = String.format("%s = ?", SUPPLEMENTARY_DATA_CPF);
        String[] whereArgs = {lifeCPF};
        db = dbManager.getReadableDatabase();
        cursor = db.query(TB_SUPPLEMENTARY_DATA, null, where, whereArgs, null, null, null, null);

        SupplementaryDataAnamnesisRequest data = null;
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                data = createSupplementaryData(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.println(Log.ERROR, "SQL Error", e.getMessage());
        }
        db.close();
        return data;
    }

    public Question selectQuestion(Questionnaire questionnaire, long internalCode) {
        Cursor cursor;
        String where = String.format("%s = ? AND %s = ?", QUESTION_QUESTIONNAIRE_ID, QUESTION_INTER_COD);
        String[] whereArgs = {String.valueOf(questionnaire.getId()), String.valueOf(internalCode)};
        db = dbManager.getReadableDatabase();
        cursor = db.query(TB_QUESTION, null, where, whereArgs, null, null, null, null);

        Question q = null;
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                q = createQuestion(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.println(Log.ERROR, "SQL Error", e.getMessage());
        }
        db.close();
        return q;
    }

    public Answer selectAnswer(Question question, String lifeCPF) {
        Cursor cursor;
        String where = String.format("%s = ? AND %s = ?", ANSWER_INTER_COD, ANSWER_LIFE_CPF);
        String[] whereArgs = {String.valueOf(question.getInternalCode()), lifeCPF};
        db = dbManager.getReadableDatabase();
        cursor = db.query(TB_ANSWER, null, where, whereArgs, null, null, null, null);

        Answer a = null;
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                a = createAnswer(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.println(Log.ERROR, "SQL Error", e.getMessage());
        }
        db.close();
        return a;
    }

    public List<Answer> selectAnswers(String userCPF, boolean answerChanged) {
        Cursor cursor;
        String where = String.format("%s = ? AND %s = ?", ANSWER_USER_CPF, ANSWER_CHANGED);
        String[] whereArgs = {userCPF, (answerChanged ? "1" : "0")};
        db = dbManager.getReadableDatabase();
        cursor = db.query(TB_ANSWER, null, where, whereArgs, null, null, null, null);

        ArrayList<Answer> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                list.add(createAnswer(cursor));
            }
        } finally {
            cursor.close();
            db.close();
        }
        return list;
    }

    private boolean checkSupplementaryData(SupplementaryDataAnamnesis data) {
        Cursor cursor;
        String whereClause = String.format("%s = ?", SUPPLEMENTARY_DATA_CPF);
        String[] whereArgs = {data.getCpf()};
        db = dbManager.getReadableDatabase();
        cursor = db.query(TB_SUPPLEMENTARY_DATA, new String[]{SUPPLEMENTARY_DATA_CPF}, whereClause, whereArgs, null, null, null, null);

        if (cursor != null) {
            return cursor.getCount() > 0;
        }
        return false;
    }

    private boolean checkAnswer(Answer answer) {
        Cursor cursor;
        String where = String.format("%s = ? AND %s = ?", ANSWER_INTER_COD, ANSWER_LIFE_CPF);
        String[] whereArgs = {String.valueOf(answer.getInternalCode()), answer.getLifeCPF()};
        db = dbManager.getReadableDatabase();
        cursor = db.query(TB_ANSWER, new String[]{ANSWER_ID}, where, whereArgs, null, null, null, null);

        if (cursor != null) {
            return cursor.getCount() > 0;
        }
        return false;
    }

    public boolean checkAnswers(String userCPF) {
        Cursor cursor;
        String where = String.format("%s = ?", ANSWER_USER_CPF);
        String[] whereArgs = {userCPF};
        db = dbManager.getReadableDatabase();
        cursor = db.query(TB_ANSWER, null, where, whereArgs, null, null, null, null);

        if (cursor != null) {
            return cursor.getCount() > 0;
        }
        return false;
    }

    // MARK: DELETE

    public int deleteAnswers(int idType, String lifeCPF) {
        String where = String.format("%s = ? AND %s = ?", ANSWER_IDTYPE, ANSWER_LIFE_CPF);
        String[] whereArgs = {String.valueOf(idType), lifeCPF};
        db = dbManager.getWritableDatabase();
        return db.delete(TB_ANSWER, where, whereArgs);
    }

    public int deleteAnswers(String userCPF) {
        String where = String.format("%s = ?", ANSWER_USER_CPF);
        String[] whereArgs = {userCPF};
        db = dbManager.getWritableDatabase();
        return db.delete(TB_ANSWER, where, whereArgs);
    }

    // MARK: Utility

    private ContentValues questionnaireValues(Questionnaire questionnaire) {
        ContentValues values = new ContentValues();
        values.put(QUESTIONNAIRE_ID, questionnaire.getId());
        values.put(QUESTIONNAIRE_IDTYPE, questionnaire.getIdType());
        values.put(QUESTIONNAIRE_TYPE, questionnaire.getType());
        values.put(QUESTIONNAIRE_DESC, questionnaire.getDesc());
        values.put(QUESTIONNAIRE_NUMBER, questionnaire.getIsCountable());
        return values;
    }

    private ContentValues questionValues(Question question) {
        ContentValues values = new ContentValues();
        values.put(QUESTION_ORDER, question.getOrder());
        values.put(QUESTION_QUESTIONNAIRE_ID, question.getQuestionnaire().getId());
        values.put(QUESTION_QUESTIONNAIRE_IDTYPE, question.getQuestionnaire().getIdType());
        values.put(QUESTION_TYPE, question.getType());
        values.put(QUESTION_NUMBER, question.isCountable());
        values.put(QUESTION_INTER_COD, question.getInternalCode());
        values.put(QUESTION_TEXT, question.getText());
        values.put(QUESTION_REQUIRED, question.isRequired());
        values.put(QUESTION_ONLY_RETIRED, question.getOnlyRetired());
        values.put(QUESTION_OMIT_RETIRED, question.getOmitRetired());
        values.put(QUESTION_OPTIONS, question.getOptions());
        values.put(QUESTION_DEFAULT_OPTION, question.getDefaultOption());
        values.put(QUESTION_EXPLICATIVE_TEXT, question.getExplicative());
        values.put(QUESTION_EXPLICATIVE_IMAGE, question.getPhoto());
        values.put(QUESTION_INTERN_COD_DEP, question.getInternalCodeDependency());
        values.put(QUESTION_OPTION_DEP, question.getOptionsDependency());
        values.put(QUESTION_CONDITIONAL_DEP, question.getConditionDependency());
        values.put(QUESTION_OPTION_NC, question.getOptionsNC());
        values.put(QUESTION_CONDITIONAL_NC, question.getConditionNC());
        values.put(QUESTION_EXCLUDE_OPTION, question.getExcludeOption());
        values.put(QUESTION_CONDITION_VALIDATE, question.getConditionValidate());
        values.put(QUESTION_OPTIONS_VALIDATE, question.getOptionsValidate());
        values.put(QUESTION_CONDITION_AGE_DEP, question.getConditionAgeDep());
        values.put(QUESTION_OPTIONS_AGE_DEP, question.getOptionsAgeDep());
        values.put(QUESTION_CONDITION_AGE_NC, question.getConditionAgeNC());
        values.put(QUESTION_OPTIONS_AGE_NC, question.getOptionsAgeNC());
        values.put(QUESTION_CAN_MULTIPLE, question.getCanMultiple());
        return values;
    }

    private ContentValues answerValues(Answer answer) {
        ContentValues values;
        values = new ContentValues();
        values.put(ANSWER_INTER_COD, answer.getInternalCode());
        values.put(ANSWER_TEXT, answer.getText());
        values.put(ANSWER_LIFE_CPF, answer.getLifeCPF());
        values.put(ANSWER_USER_CPF, answer.getUserCPF());
        values.put(ANSWER_CHANGED, answer.isAnswerChanged());
        values.put(ANSWER_IDTYPE, answer.getIdType());
        values.put(ANSWER_QUESTIONNAIRE_ID, answer.getIdQuest());
        values.put(ANSWER_EXCLUDE_OPTION, answer.getExcluding());
        return values;
    }

    private ContentValues supplementaryDataValues(SupplementaryDataAnamnesis data) {
        ContentValues values;
        values = new ContentValues();
        values.put(SUPPLEMENTARY_DATA_CPF, data.getCpf());
        values.put(SUPPLEMENTARY_DATA_WEIGHT, data.getWeight());
        values.put(SUPPLEMENTARY_DATA_HEIGHT, data.getHeight());
        values.put(SUPPLEMENTARY_DATA_BMI, data.getBodyMassIndex());
        values.put(SUPPLEMENTARY_DATA_BMI_RANGE, data.getBodyMassIndexRange());
        values.put(SUPPLEMENTARY_DATA_RELIGION_FK, data.getReligion().getId());
        return values;
    }

    private Questionnaire createQuestionnaire(Cursor cursor) {
        Questionnaire q = new Questionnaire();
        q.setId(cursor.getLong(cursor.getColumnIndex(QUESTIONNAIRE_ID)));
        q.setIdType(cursor.getInt(cursor.getColumnIndex(QUESTIONNAIRE_IDTYPE)));
        q.setType(cursor.getString(cursor.getColumnIndex(QUESTIONNAIRE_TYPE)));
        q.setDesc(cursor.getString(cursor.getColumnIndex(QUESTIONNAIRE_DESC)));
        q.setIsCountable(cursor.getInt(cursor.getColumnIndex(QUESTIONNAIRE_NUMBER)));
        return q;
    }

    private SupplementaryDataAnamnesisRequest createSupplementaryData(Cursor cursor) {
        SupplementaryDataAnamnesisRequest data = new SupplementaryDataAnamnesisRequest();
        data.setWeight(cursor.getFloat(cursor.getColumnIndex(SUPPLEMENTARY_DATA_WEIGHT)));
        data.setHeight(cursor.getFloat(cursor.getColumnIndex(SUPPLEMENTARY_DATA_HEIGHT)));
        //data.setReligion(cursor.getInt(cursor.getColumnIndex(SUPPLEMENTARY_DATA_RELIGION_FK)));
        return data;
    }

    private Question createQuestion(Cursor cursor) {
        Question q = new Question();
        q.setOrder(cursor.getInt(cursor.getColumnIndex(QUESTION_ORDER)));
        long idQuest = cursor.getLong(cursor.getColumnIndex(QUESTION_QUESTIONNAIRE_ID));
        int idTypeQuest = cursor.getInt(cursor.getColumnIndex(QUESTION_QUESTIONNAIRE_IDTYPE));
        q.setQuestionnaire(new Questionnaire(idQuest, idTypeQuest));
        q.setType(cursor.getInt(cursor.getColumnIndex(QUESTION_TYPE)));
        q.setCountable(cursor.getInt(cursor.getColumnIndex(QUESTION_NUMBER)));
        q.setInternalCode(cursor.getInt(cursor.getColumnIndex(QUESTION_INTER_COD)));
        q.setText(cursor.getString(cursor.getColumnIndex(QUESTION_TEXT)));
        q.setRequired(cursor.getInt(cursor.getColumnIndex(QUESTION_REQUIRED)));
        q.setOnlyRetired(cursor.getInt(cursor.getColumnIndex(QUESTION_ONLY_RETIRED)));
        q.setOmitRetired(cursor.getInt(cursor.getColumnIndex(QUESTION_OMIT_RETIRED)));
        q.setOptions(cursor.getString(cursor.getColumnIndex(QUESTION_OPTIONS)));
        q.setDefaultOption(cursor.getString(cursor.getColumnIndex(QUESTION_DEFAULT_OPTION)));
        q.setExplicative(cursor.getString(cursor.getColumnIndex(QUESTION_EXPLICATIVE_TEXT)));
        q.setPhoto(cursor.getInt(cursor.getColumnIndex(QUESTION_EXPLICATIVE_IMAGE)));
        q.setInternalCodeDependency(cursor.getLong(cursor.getColumnIndex(QUESTION_INTERN_COD_DEP)));
        q.setConditionDependency(cursor.getInt(cursor.getColumnIndex(QUESTION_CONDITIONAL_DEP)));
        q.setOptionsDependency(cursor.getString(cursor.getColumnIndex(QUESTION_OPTION_DEP)));
        q.setConditionNC(cursor.getInt(cursor.getColumnIndex(QUESTION_CONDITIONAL_NC)));
        q.setOptionsNC(cursor.getString(cursor.getColumnIndex(QUESTION_OPTION_NC)));
        q.setExcludeOption(cursor.getString(cursor.getColumnIndex(QUESTION_EXCLUDE_OPTION)));
        q.setConditionValidate(cursor.getInt(cursor.getColumnIndex(QUESTION_CONDITION_VALIDATE)));
        q.setOptionsValidate(cursor.getString(cursor.getColumnIndex(QUESTION_OPTIONS_VALIDATE)));
        q.setConditionAgeDep(cursor.getInt(cursor.getColumnIndex(QUESTION_CONDITION_AGE_DEP)));
        q.setOptionsAgeDep(cursor.getString(cursor.getColumnIndex(QUESTION_OPTIONS_AGE_DEP)));
        q.setConditionAgeNC(cursor.getInt(cursor.getColumnIndex(QUESTION_CONDITION_AGE_NC)));
        q.setOptionsAgeNC(cursor.getString(cursor.getColumnIndex(QUESTION_OPTIONS_AGE_NC)));
        q.setCanMultiple(cursor.getInt(cursor.getColumnIndex(QUESTION_CAN_MULTIPLE)));
        return q;
    }

    private Answer createAnswer(Cursor cursor) {
        Answer a = new Answer();
        a.setInternalCode(cursor.getLong(cursor.getColumnIndex(ANSWER_INTER_COD)));
        a.setText(cursor.getString(cursor.getColumnIndex(ANSWER_TEXT)));
        a.setLifeCPF(cursor.getString(cursor.getColumnIndex(ANSWER_LIFE_CPF)));
        a.setUserCPF(cursor.getString(cursor.getColumnIndex(ANSWER_USER_CPF)));
        int answerChanged = cursor.getInt(cursor.getColumnIndex(ANSWER_CHANGED));
        a.setAnswerChanged(answerChanged == 1);
        a.setExcluding(cursor.getInt(cursor.getColumnIndex(ANSWER_EXCLUDE_OPTION)));
        a.setIdType(cursor.getInt(cursor.getColumnIndex(ANSWER_IDTYPE)));
        a.setIdQuest(cursor.getInt(cursor.getColumnIndex(ANSWER_QUESTIONNAIRE_ID)));
        return a;
    }
}
