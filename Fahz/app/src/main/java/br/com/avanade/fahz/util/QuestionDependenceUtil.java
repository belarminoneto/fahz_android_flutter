package br.com.avanade.fahz.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.com.avanade.fahz.model.anamnesisModel.AuxDataBridge;
import br.com.avanade.fahz.model.anamnesisModel.Question;
import br.com.avanade.fahz.model.anamnesisModel.Questionnaire;

import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.AGE;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.CURRENT_VALUE;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.DATE;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.DISEASE;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.DIVIDER;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.MEDICAL_SPECIALTY;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.MEDICINE;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.ORGAN;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.YEAR;
import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;

public class QuestionDependenceUtil {

    public static final int EQUAL = 0;
    public static final int NOT_EQUAL = 1;
    public static final int GREATER = 2;
    public static final int GREATER_OR_EQUAL = 3;
    public static final int LESS = 4;
    public static final int LESS_OR_EQUAL = 5;
    public static final int BETWEEN = 6;

    private final String RETIRED_KEY = "APOSENTADO";

    private List<Question> mDependentQuestions;
    private List<Question> mCurrentQuestions;
    public List<Question> mDivisorList;
    private Questionnaire mQuestionnaire;

    public Questionnaire checkQuestionsDependence(Questionnaire questionnaire, List<Question> allQuestions, boolean addDivider) {
        mQuestionnaire = questionnaire;
        final int countingRule = questionnaire.getIsCountable();
        initialQuestionnaireSetup(allQuestions, addDivider, countingRule);
        mQuestionnaire.setNonCompliance(false);
        for (int i = 0; i < mCurrentQuestions.size(); i++) {
            checkQuestion(mCurrentQuestions.get(i));
        }
        countQuestions(countingRule);
        mQuestionnaire.setQuestions(mCurrentQuestions);
        return mQuestionnaire;
    }

    private void initialQuestionnaireSetup(List<Question> allQuestions, boolean addDivider, int countingRule) {
        mCurrentQuestions = new ArrayList<>();
        mDependentQuestions = new ArrayList<>();
        Question tempDivider = new Question();
        mDivisorList = new ArrayList<>();
        int countDivider = 0;

        Collections.sort(allQuestions, new Comparator<Question>() {
            @Override
            public int compare(Question question, Question t1) {
                return Integer.valueOf(question.getOrder()).compareTo(t1.getOrder());
            }
        });
        for (Question question : allQuestions) {

            if (!checkRetiredRule(question)) {
                continue;
            }
            if (question.getType() == DIVIDER && !addDivider) {
                if (countingRule == 1) {
                    countDivider++;
                    question.setCount(countDivider);
                }
                tempDivider = question;
                mDivisorList.add(question);
                continue;
            }
            question.setDivider(tempDivider);
            if (question.getInternalCodeDependency() == 0) {
                mCurrentQuestions.add(question);
            } else {
                mDependentQuestions.add(question);
            }
        }
    }

    private boolean checkRetiredRule(Question question) {
        boolean isRetired = userAnamnesisSession.getSituation().equals(RETIRED_KEY);
        if (question.getOnlyRetired() == 1) {
            return isRetired;
        } else if (question.getOmitRetired() == 1) {
            return !isRetired;
        } else {
            return true;
        }
    }

    private void countQuestions(int countingRule) {
        int count = 0;

        Collections.sort(mCurrentQuestions, new Comparator<Question>() {
            @Override
            public int compare(Question question, Question t1) {
                return Integer.valueOf(question.getOrder()).compareTo(t1.getOrder());
            }
        });
        if (countingRule == 1) {
            // contagem por divisao
            Question tempDivider = new Question();
            for (Question question : mCurrentQuestions) {
                if (question.isCountable() == 1) {
                    if (question.getDivider().getInternalCode() != tempDivider.getInternalCode()) {
                        count = 0;
                        tempDivider = question.getDivider();
                    }
                    count++;
                    question.setCount(count);
                }
            }
        } else {
            // contagem ignorando divisao
            for (Question question : mCurrentQuestions) {
                if (question.isCountable() == 1) {
                    count++;
                    question.setCount(count);
                }
            }
        }
    }

    private void checkQuestion(Question currentQuestion) {
        if (checkValidAnswer(currentQuestion)) {
            if (currentQuestion.getOptionsNC() != null && checkAgeNonConformityRule(currentQuestion)) {
                checkNonConformityRule(currentQuestion);
            }
            if (checkAgeDependenceRule(currentQuestion)) {
                checkDependenceRule(currentQuestion);
            }
        }
    }

    private boolean checkValidAnswer(Question currentQuestion) {
        return currentQuestion.getAnswer() != null
                && !currentQuestion.getAnswer().getText().isEmpty();
    }

    private boolean checkAgeDependenceRule(Question currentQuestion) {
        String options = currentQuestion.getOptionsAgeDep();
        int questionType = currentQuestion.getType();
        int condition = currentQuestion.getConditionAgeDep();
        String age = String.valueOf(userAnamnesisSession.getAge());
        if (options != null) {
            return checkAnswerCondition(age, questionType, options, condition);
        }
        return true;
    }

    private boolean checkAgeNonConformityRule(Question currentQuestion) {
        String options = currentQuestion.getOptionsAgeNC();
        int questionType = currentQuestion.getType();
        int condition = currentQuestion.getConditionAgeNC();
        String age = String.valueOf(userAnamnesisSession.getAge());
        if (options != null) {
            return checkAnswerCondition(age, questionType, options, condition);
        }
        return true;
    }

    private void checkNonConformityRule(Question currentQuestion) {
        String currentAnswer = checkAuxDataAnswers(currentQuestion);
        int questionType = currentQuestion.getType();
        String options = currentQuestion.getOptionsNC();
        int condition = currentQuestion.getConditionNC();
        if (checkAnswerCondition(currentAnswer, questionType, options, condition)) {
            mQuestionnaire.setNonCompliance(true);
        }
    }

    private void checkDependenceRule(Question currentQuestion) {
        for (Question dependentQuestion : mDependentQuestions) {
            if (dependentQuestion.getInternalCodeDependency() == currentQuestion.getInternalCode()) {

                String currentAnswer = checkAuxDataAnswers(currentQuestion);
                int questionType = currentQuestion.getType();
                String options = dependentQuestion.getOptionsDependency();
                int condition = dependentQuestion.getConditionDependency();
                if (checkAnswerCondition(currentAnswer, questionType, options, condition)) {
                    addQuestion(dependentQuestion);
                }
            }
        }
    }

    private boolean checkAnswerCondition(String currentAnswer, int questionType, String options, int condition) {
        try {
            switch (condition) {
                case EQUAL:
                    return checkEqual(currentAnswer, questionType, options);
                case NOT_EQUAL:
                    return checkNotEqual(currentAnswer, questionType, options);
                case GREATER:
                    if (questionType == DATE) {
                        return checkGreaterDate(currentAnswer, options);
                    } else {
                        return checkGreater(currentAnswer, questionType, options);
                    }
                case GREATER_OR_EQUAL:
                    if (questionType == DATE) {
                        return checkGreaterOrEqualDate(currentAnswer, options);
                    } else {
                        return checkGreaterOrEqual(currentAnswer, questionType, options);
                    }
                case LESS:
                    if (questionType == DATE) {
                        return checkLessDate(currentAnswer, options);
                    } else {
                        return checkLess(currentAnswer, questionType, options);
                    }
                case LESS_OR_EQUAL:
                    if (questionType == DATE) {
                        return checkLessOrEqualDate(currentAnswer, options);
                    } else {
                        return checkLessOrEqual(currentAnswer, questionType, options);
                    }
                case BETWEEN:
                    if (questionType == DATE) {
                        return checkBetweenDate(currentAnswer, options);
                    } else {
                        return checkBetween(currentAnswer, questionType, options);
                    }
                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkEqual(String currentAnswer, int questionType, String options) {
        final String[] optionsArray = options.split("\\|");
        final String[] answerArray = currentAnswer.split("\\|");

        for (String option : optionsArray) {
            String optionTemp = checkCurrentConditionOption(option, questionType);
            for (String answerText : answerArray) {
                if (answerText.equals(optionTemp)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkNotEqual(String currentAnswer, int questionType, String options) {
        final String[] optionsArray = options.split("\\|");
        final String[] answerArray = currentAnswer.split("\\|");

        for (String option : optionsArray) {
            String optionTemp = checkCurrentConditionOption(option, questionType);
            for (String answerText : answerArray) {
                if (answerText.equals(optionTemp)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkGreater(String currentAnswer, int questionType, String option) {
        int answer = Integer.parseInt(currentAnswer);
        int intOption = Integer.parseInt(checkCurrentConditionOption(option, questionType));
        return answer > intOption;
    }

    private boolean checkGreaterDate(String currentAnswer, String option) {
        Date answer = convertToDate(currentAnswer);
        Date dateOption = checkCurrentConditionDate(option);
        return answer.after(dateOption);
    }

    private boolean checkGreaterOrEqual(String currentAnswer, int questionType, String option) {
        int answer = Integer.parseInt(currentAnswer);
        int intOption = Integer.parseInt(checkCurrentConditionOption(option, questionType));
        return answer >= intOption;
    }

    private boolean checkGreaterOrEqualDate(String currentAnswer, String option) {
        Date answer = convertToDate(currentAnswer);
        Date dateOption = checkCurrentConditionDate(option);
        return answer.getTime() >= dateOption.getTime();
    }

    private boolean checkLess(String currentAnswer, int questionType, String option) {
        int answer = Integer.parseInt(currentAnswer);
        int intOption = Integer.parseInt(checkCurrentConditionOption(option, questionType));
        return answer < intOption;
    }

    private boolean checkLessDate(String currentAnswer, String option) {
        Date answer = convertToDate(currentAnswer);
        Date dateOption = checkCurrentConditionDate(option);
        return answer.before(dateOption);
    }

    private boolean checkLessOrEqual(String currentAnswer, int questionType, String option) {
        int answer = Integer.parseInt(currentAnswer);
        int intOption = Integer.parseInt(checkCurrentConditionOption(option, questionType));
        return answer <= intOption;
    }

    private boolean checkLessOrEqualDate(String currentAnswer, String option) {
        Date answer = convertToDate(currentAnswer);
        Date dateOption = checkCurrentConditionDate(option);
        return answer.getTime() <= dateOption.getTime();
    }

    private boolean checkBetween(String currentAnswer, int questionType, String options) {
        String[] optionsArray = options.split("\\|");
        if (optionsArray.length > 1) {
            int answer = Integer.parseInt(currentAnswer);
            int min = Integer.parseInt(checkCurrentConditionOption(optionsArray[0], questionType));
            int max = Integer.parseInt(checkCurrentConditionOption(optionsArray[1], questionType));
            return answer >= min && answer <= max;
        }
        return false;
    }

    private boolean checkBetweenDate(String currentAnswer, String options) {
        String[] optionsArray = options.split("\\|");
        if (optionsArray.length > 1) {
            Date answer = convertToDate(currentAnswer);
            Date min = checkCurrentConditionDate(optionsArray[0]);
            Date max = checkCurrentConditionDate(optionsArray[1]);
            return answer.getTime() >= min.getTime() && answer.getTime() <= max.getTime();
        }
        return false;
    }

    private void addQuestion(Question otherQuestion) {
        if (!alreadyExists(otherQuestion)) {
            mCurrentQuestions.add(otherQuestion);
        }
    }

    private boolean alreadyExists(Question newQuestion) {
        for (Question question : mCurrentQuestions) {
            if (question.getInternalCode() == newQuestion.getInternalCode()) {
                return true;
            }
        }
        return false;
    }

    public static int stringToInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            return 0;
        }
    }

    public static List<AuxDataBridge> stringToAuxDataBridges(String answer) {
        List<AuxDataBridge> auxDataBridges = new ArrayList<>();
        String[] answersList = answer.split("\\|");
        for (final String answerTemp : answersList) {
            String[] answerPart = answerTemp.split("@");
            if (answerPart.length == 2) {
                AuxDataBridge auxData = new AuxDataBridge();
                auxData.setId(answerPart[0]);
                auxData.setTextShown(answerPart[1]);
                auxDataBridges.add(auxData);
            }
        }
        return auxDataBridges;
    }

    public static String auxDataBridgeToString(List<AuxDataBridge> auxDataBridges) {
        StringBuilder answer = new StringBuilder();
        for (AuxDataBridge auxData : auxDataBridges) {
            String answerTemp = String.format("%s|", auxData.getId());
            answer.append(answerTemp);
        }
        if (answer.length() > 0) {
            answer.deleteCharAt(answer.length() - 1);
        }
        return answer.toString();
    }

    private String checkAuxDataAnswers(Question currentQuestion) {
        String currentAnswer = currentQuestion.getAnswer().getText();
        switch (currentQuestion.getType()) {
            case DISEASE:
            case MEDICAL_SPECIALTY:
            case MEDICINE:
            case ORGAN:
                break;
            default:
                return currentAnswer;
        }

        StringBuilder answerTemp = new StringBuilder();
        String[] answersList = currentAnswer.split("\\|");
        for (String answer : answersList) {
            String[] answerPart = answer.split("@");
            if (answerPart.length == 2) {
                answerTemp.append(answerPart[0]).append("|");
            }
        }
        if (answerTemp.length() > 0) {
            answerTemp.deleteCharAt(answerTemp.length() - 1);
        }
        return answerTemp.toString();
    }

    private Date convertToDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private Date checkCurrentConditionDate(String option) {
        if (option.equalsIgnoreCase(CURRENT_VALUE)) {
            return new Date();
        } else {
            return convertToDate(option);
        }
    }

    private String checkCurrentConditionOption(String option, int questionType) {
        switch (questionType) {
            case AGE:
                return option.equalsIgnoreCase(CURRENT_VALUE) ? String.valueOf(userAnamnesisSession.getAge()) : option;
            case DATE:
                if (option.equalsIgnoreCase(CURRENT_VALUE)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    return sdf.format(new Date());
                } else {
                    return option;
                }
            case YEAR:
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                return option.equalsIgnoreCase(CURRENT_VALUE) ? String.valueOf(currentYear) : option;
            default:
                return option;
        }
    }
}
