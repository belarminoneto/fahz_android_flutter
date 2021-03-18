package br.com.avanade.fahz.fragments.anamnesis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.anamnesis.SearchAuxiliaryDataActivity;
import br.com.avanade.fahz.interfaces.OnQuestionChangeListener;
import br.com.avanade.fahz.interfaces.QuestionnaireActivityListener;
import br.com.avanade.fahz.model.anamnesisModel.Answer;
import br.com.avanade.fahz.model.anamnesisModel.AuxDataBridge;
import br.com.avanade.fahz.model.anamnesisModel.Question;
import br.com.avanade.fahz.util.AnamnesisUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.AUX_DATA_INTENT;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.AUX_DATA_TYPE;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.CAN_MULTI_OPTIONS_AUX_DATA;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.REQUEST_AUX_DATA;
import static br.com.avanade.fahz.util.QuestionDependenceUtil.BETWEEN;
import static br.com.avanade.fahz.util.QuestionDependenceUtil.EQUAL;
import static br.com.avanade.fahz.util.QuestionDependenceUtil.GREATER;
import static br.com.avanade.fahz.util.QuestionDependenceUtil.GREATER_OR_EQUAL;
import static br.com.avanade.fahz.util.QuestionDependenceUtil.LESS;
import static br.com.avanade.fahz.util.QuestionDependenceUtil.LESS_OR_EQUAL;
import static br.com.avanade.fahz.util.QuestionDependenceUtil.NOT_EQUAL;
import static br.com.avanade.fahz.util.QuestionDependenceUtil.auxDataBridgeToString;
import static br.com.avanade.fahz.util.QuestionDependenceUtil.stringToAuxDataBridges;
import static br.com.avanade.fahz.util.QuestionDependenceUtil.stringToInt;

public class QuestionFragment extends Fragment implements OnQuestionChangeListener {

    // answersType
    public static final int DIVIDER = 0;
    public static final int COMMENT = 1;
    private static final int TEXT = 2;
    private static final int TEXT_MULTILINE = 3;
    private static final int RADIO_GROUP = 4;
    private static final int CHECK_BOX = 5;
    private static final int COMBOBOX = 6;
    private static final int INTEGER = 7;
    private static final int DECIMAL = 8;
    public static final int YEAR = 9;
    public static final int DATE = 10;
    public static final int AGE = 11;
    public static final int DISEASE = 12;
    public static final int MEDICAL_SPECIALTY = 13;
    public static final int MEDICINE = 14;
    public static final int ORGAN = 15;
    public static final int RELIGION = 16;

    public static final String CURRENT_VALUE = "atual";
    private final int ID_VIEW = 1000;

    @BindView(R.id.txtDivider)
    TextView txtDivider;

    @BindView(R.id.txtText)
    TextView txtText;

    @BindView(R.id.imgExplanatoryTextDivider)
    ImageView imgExplanatoryTextDivider;

    @BindView(R.id.imgExplanatoryImageDivider)
    ImageView imgExplanatoryImageDivider;

    @BindView(R.id.imgExplanatoryTextQuestion)
    ImageView imgExplanatoryTextQuestion;

    @BindView(R.id.imgExplanatoryImageQuestion)
    ImageView imgExplanatoryImageQuestion;

    @BindView(R.id.edtSearchAuxData)
    EditText edtSearchAuxData;

    @BindView(R.id.answersFrame)
    LinearLayout answersFrame;

    @BindView(R.id.chbExclude)
    CheckBox chbExclude;

    @BindView(R.id.txtRequiredAnswer)
    TextView txtRequiredAnswer;

    @BindView(R.id.txtErraseAnswer)
    TextView txtErraseAnswer;

    private QuestionnaireActivityListener questionnaireListener;
    private LayoutInflater inflater;
    Question mQuestion;
    private boolean isExcluded;
    private boolean canErase = true;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            createQuestion();
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = getResources().getString(R.string.question_error, mQuestion.getInternalCode());
            questionnaireListener.onQuestionError(errorMsg);
        }
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUX_DATA) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                if (b != null) {
                    List<AuxDataBridge> list = (List<AuxDataBridge>) b.getSerializable(AUX_DATA_INTENT);
                    if (list != null) {
                        setAnswer(auxDataBridgeToString(list));
                        addAnswerInAuxDataFrame(list);
                    }
                }
            } else if (resultCode == 0) {
                System.out.println("RESULT CANCELLED");
            }
        }
    }

    @Override
    public void onQuestionChanged() {
        // FIXME
//        try {
//            createQuestion();
//        } catch (Exception e) {
//            e.printStackTrace();
//            String errorMsg = String.format("Erro na questão %d!!", mQuestion.getInternalCode());
//            answerListener.onQuestionError(errorMsg);
//        }
    }

    @NonNull
    public static QuestionFragment newInstance(@NonNull Question question, @NonNull QuestionnaireActivityListener listener) {
        QuestionFragment fragment = new QuestionFragment();
        fragment.mQuestion = question;
        fragment.questionnaireListener = listener;
        return fragment;
    }

    @OnClick(R.id.imgExplanatoryTextDivider)
    public void onImgExplanatoryTextDividerClick() {
        String text = mQuestion.getDivider().getExplicative();
        AnamnesisUtils.showExplicativeDialog(text, getContext());
    }

    @OnClick(R.id.imgExplanatoryImageDivider)
    public void onImgExplanatoryImageDividerClick() {
        String urlImage = String.valueOf(mQuestion.getDivider().getPhoto());
        questionnaireListener.downloadImageRequested(urlImage);
    }

    @OnClick(R.id.imgExplanatoryTextQuestion)
    public void onImgExplanatoryTextQuestionClick() {
        String text = mQuestion.getExplicative();
        AnamnesisUtils.showExplicativeDialog(text, getContext());
    }

    @OnClick(R.id.imgExplanatoryImageQuestion)
    public void onImgExplanatoryImageQuestionClick() {
        String urlImage = String.valueOf(mQuestion.getPhoto());
        questionnaireListener.downloadImageRequested(urlImage);
    }

    @OnClick(R.id.edtSearchAuxData)
    public void onEdtSearchAuxDataClick() {
        List<AuxDataBridge> auxDataList;
        if (mQuestion.getAnswer() != null && mQuestion.getAnswer().getExcluding() != 1) {
            auxDataList = stringToAuxDataBridges(mQuestion.getAnswer().getText());
        } else {
            auxDataList = new ArrayList<>();
        }
        Intent intent = new Intent(getActivity(), SearchAuxiliaryDataActivity.class);
        intent.putExtra(AUX_DATA_INTENT, (Serializable) auxDataList);
        intent.putExtra(AUX_DATA_TYPE, mQuestion.getType());
        intent.putExtra(CAN_MULTI_OPTIONS_AUX_DATA, mQuestion.getCanMultiple() == 1);
        startActivityForResult(intent, REQUEST_AUX_DATA);
    }

    void setAnswer(String answerText) {
        if (mQuestion.getAnswer() == null) {
            Answer answer = new Answer();
            answer.setLifeCPF(userAnamnesisSession.getLifeCPF());
            answer.setIdQuest(mQuestion.getQuestionnaire().getId());
            answer.setIdType(mQuestion.getQuestionnaire().getIdType());
            answer.setInternalCode(mQuestion.getInternalCode());
            mQuestion.setAnswer(answer);
        }
        mQuestion.getAnswer().setUserCPF(userAnamnesisSession.getUserCPF());
        mQuestion.getAnswer().setText(answerText);
        mQuestion.getAnswer().setExcluding(isExcluded ? 1 : 0);
        mQuestion.getAnswer().setAnswerChanged(true);
        questionnaireListener.onAnswerChanged(mQuestion);
    }

    private void createQuestion() throws Exception {
        setupQuestion();
        switch (mQuestion.getType()) {
            case TEXT:
                createText(mQuestion);
                break;
            case TEXT_MULTILINE:
                createTextMultiline(mQuestion);
                break;
            case RADIO_GROUP:
                createRadioButton(mQuestion.getOptions(), mQuestion.getAnswer(), mQuestion.getDefaultOption(), inflater);
                break;
            case CHECK_BOX:
                createCheckbox(mQuestion.getOptions(), mQuestion.getAnswer(), mQuestion.getDefaultOption(), inflater);
                break;
            case COMBOBOX:
                createCombobox(mQuestion.getOptions(), mQuestion.getAnswer(), mQuestion.getDefaultOption());
                break;
            case INTEGER:
                createInteger(mQuestion);
                break;
            case DECIMAL:
                createDecimal(mQuestion);
                break;
            case YEAR:
                createYear(mQuestion);
                break;
            case DATE:
                createDate(mQuestion);
                break;
            case AGE:
                createAge(mQuestion);
                break;
            case DISEASE:
            case MEDICAL_SPECIALTY:
            case MEDICINE:
            case ORGAN:
                createAuxTableQuestion(mQuestion);
                break;
        }
        checkExcludeOption();
    }

    private void setupQuestion() {
        Question divider = mQuestion.getDivider();
        if (divider != null) {
            String textDivider = "";
            if (mQuestion.getDivider().getCount() > 0) {
                textDivider += mQuestion.getDivider().getCount() + " - ";
            }

            if (divider.getText() != null) {
                textDivider += divider.getText();
            }

            txtDivider.setText(Html.fromHtml(textDivider));

            if (divider.getExplicative() != null) {
                imgExplanatoryTextDivider.setVisibility(VISIBLE);
            }
            if (divider.getPhoto() != 0) {
                imgExplanatoryImageDivider.setVisibility(VISIBLE);
            }
        }
        if (mQuestion.getExplicative() != null) {
            imgExplanatoryTextQuestion.setVisibility(VISIBLE);
        }
        if (mQuestion.getPhoto() != 0) {
            imgExplanatoryImageQuestion.setVisibility(VISIBLE);
        }
        String text = "";
        if (mQuestion.getCount() > 0) {
            text += mQuestion.getCount() + " - ";
        }
        text += mQuestion.getText();

        isExcluded = mQuestion.getAnswer() != null && mQuestion.getAnswer().getExcluding() == 1;
        if (mQuestion.isRequired() == 1) {
            txtRequiredAnswer.setVisibility(VISIBLE);
            text += "<font color='#EE0000'>*</font>";
        }
        txtText.setText(Html.fromHtml(text));

        if (mQuestion.getType() == COMMENT) {
            txtErraseAnswer.setVisibility(GONE);
        }
    }

    private void createText(Question question) {
        EditText editText = new EditText(getContext());
        editText.setHint(R.string.type_your_answer);
        editText.setLayoutParams(getLayoutParams());
        addAnswerInEditText(question, editText);
        editText.addTextChangedListener(getTextWatcher(null, null, null, null, editText));
        answersFrame.addView(editText);
    }

    private void createTextMultiline(Question question) {
        EditText editText = new EditText(getContext());
        editText.setHint(R.string.type_your_answer);
        editText.setLayoutParams(getLayoutParams());
        editText.setSingleLine(false);
        editText.setMaxLines(20);
        addAnswerInEditText(question, editText);
        editText.addTextChangedListener(getTextWatcher(null, null, null, null, editText));
        answersFrame.addView(editText);
    }

    private void createRadioButton(String options, Answer answer, String defaultOption, LayoutInflater inflater) {
        RadioGroup radioGroup = new RadioGroup(getContext());
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setLayoutParams(getLayoutParams());
        String[] optionsArray = options.split("\\|");
        RadioButton radioButton;
        View view;
        String option;
        for (int i = 0; i < optionsArray.length; i++) {
            option = optionsArray[i];
            view = inflater.inflate(R.layout.radiobutton_anamnesis, radioGroup, true);
            radioButton = view.findViewById(R.id.radio_button);
            radioButton.setId(ID_VIEW + i);
            radioButton.setTextColor(getColorList());
            radioButton.setText(option);
            addAnswerInRadioButton(answer, defaultOption, radioButton, option);
        }
        radioGroup.setOnCheckedChangeListener(getOnCheckedChangeListener(optionsArray));
        answersFrame.addView(radioGroup);
    }

    private void createCheckbox(String options, Answer answer, String defaultOption, LayoutInflater inflater) {
        String[] optionsArray = options.split("\\|");
        CheckBox checkBox;
        View view;
        String option;
        for (int i = 0; i < optionsArray.length; i++) {
            option = optionsArray[i];
            view = inflater.inflate(R.layout.checkbox_anamnesis, answersFrame, true);
            checkBox = view.findViewById(R.id.checkbox);
            checkBox.setId(ID_VIEW + i);
            checkBox.setText(option);
            addAnswerInCheckBox(answer, defaultOption, checkBox, option);
            checkBox.setOnCheckedChangeListener(getChangeListener());
        }
    }

    private void createCombobox(String options, Answer answer, String defaultOption) {
        Spinner spinner = new Spinner(getContext());
        final String[] answersArray = options.split("\\|");
        ArrayList<String> spinnerArray = new ArrayList<>(Arrays.asList(answersArray));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setLayoutParams(getLayoutParams());
        addAnswerInSpinner(answer, defaultOption, spinner, spinnerArray);
        spinner.setOnItemSelectedListener(getSelectedListener(answersArray));
        answersFrame.addView(spinner);
    }

    private void createInteger(Question question) {
        EditText editText = new EditText(getContext());
        editText.setHint(R.string.type_your_answer);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setLayoutParams(getLayoutParams());
        addAnswerInEditText(question, editText);
        setConditionValidateInEditText(question, editText);
        answersFrame.addView(editText);
    }

    private void createDecimal(Question question) {
        EditText editText = new EditText(getContext());
        editText.setHint(R.string.type_your_answer);
        int decimalInputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
        editText.setInputType(decimalInputType);
        editText.setLayoutParams(getLayoutParams());
        addAnswerInEditText(question, editText);
        setConditionValidateInEditText(question, editText);
        answersFrame.addView(editText);
    }

    private void createYear(Question question) {
        final NumberPicker yearPicker = new NumberPicker(getContext());
        yearPicker.setFocusable(false);
        final Calendar myCalendar = Calendar.getInstance();
        int year = myCalendar.get(Calendar.YEAR);
        String[] values = setConditionValidateInNumberPicker(question, year, 1900, 2050, yearPicker);
        addAnswerInNumberPicker(question, yearPicker, values);
        yearPicker.setOnValueChangedListener(getOnValueChangeListener(values));
        yearPicker.setLayoutParams(getLayoutParams());
        answersFrame.addView(yearPicker);
    }

    private void createDate(Question question) throws Exception {
        EditText editText = new EditText(getContext());
        editText.setHint(R.string.select_date);
        editText.setFocusable(false);
        editText.setLayoutParams(getLayoutParams());

        Calendar myCalendar = Calendar.getInstance();
        String dateFormat = "dd/MM/yyyy";
        DatePickerDialog.OnDateSetListener dateListener = getDateListener(question, myCalendar, dateFormat, editText);
        int year = myCalendar.get(Calendar.YEAR);
        int month = myCalendar.get(Calendar.MONTH);
        int dayOfMonth = myCalendar.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog dialog = new DatePickerDialog(Objects.requireNonNull(getContext()), R.style.DateDialogTheme, dateListener, year, month, dayOfMonth);

        setConditionValidateInDatePicker(question, dateFormat, dialog);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        addAnswerInEditText(question, editText);
        answersFrame.addView(editText);
    }

    private void createAge(Question question) {
        final NumberPicker agePicker = new NumberPicker(getContext());
        agePicker.setFocusable(false);
        int currentAge = userAnamnesisSession.getAge();
        String[] values = setConditionValidateInNumberPicker(question, currentAge, 0, 120, agePicker);
        addAnswerInNumberPicker(question, agePicker, values);
        agePicker.setOnValueChangedListener(getOnValueChangeListener(values));
        agePicker.setLayoutParams(getLayoutParams());
        answersFrame.addView(agePicker);
    }

    private void createAuxTableQuestion(Question question) {
        edtSearchAuxData.setVisibility(VISIBLE);
        if (question.getAnswer() != null && question.getAnswer().getExcluding() != 1) {
            addAnswerInAuxDataFrame(stringToAuxDataBridges(question.getAnswer().getText()));
        }
    }

    private void checkExcludeOption() {
        String excludeOption = mQuestion.getExcludeOption();
        if (excludeOption != null) {
            setupExcludeCheckbox(excludeOption, mQuestion.getAnswer());
        }
    }

    private void setupExcludeCheckbox(String excludeOption, Answer answer) {
        chbExclude.setVisibility(VISIBLE);
        chbExclude.setText(excludeOption);
        if (answer != null && answer.getExcluding() == 1) {
            chbExclude.setChecked(true);
            disableOptions(true);
        }
    }

    private void disableOptions(boolean disable) {
        isExcluded = disable;
        answersFrame.setAlpha(disable ? 0.3f : 1.0f);
        for (int i = 0; i < answersFrame.getChildCount(); i++) {
            View view = answersFrame.getChildAt(i);
            view.setEnabled(!disable);
        }
        txtErraseAnswer.setVisibility(!disable && canErase ? VISIBLE : INVISIBLE);
    }

    @OnCheckedChanged(R.id.chbExclude)
    public void onExcludeCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
        disableOptions(isChecked);
        if (isChecked) {
            setAnswer((String) buttonView.getText());
            erraseAnswer();
        } else {
            erraseAnswer();
        }
    }

    // MARK: errase Answer

    @OnClick(R.id.txtErraseAnswer)
    public void erraseAnswer() {
        try {
            switch (mQuestion.getType()) {
                case TEXT:
                case TEXT_MULTILINE:
                case INTEGER:
                case DECIMAL:
                    erraseEditText();
                    break;
                case RADIO_GROUP:
                    erraseRadioGroup();
                    break;
                case CHECK_BOX:
                    erraseCheckBox();
                    break;
                case COMBOBOX:
                    erraseSpinner();
                    break;
                case YEAR:
                case AGE:
                    erraseNumberPicker();
                    break;
                case DATE:
                    erraseEditText();
                    setAnswer("");
                    break;
            }
        } catch (Exception e) {
            String errorMsg = getResources().getString(R.string.delete_question_error, mQuestion.getInternalCode());
            questionnaireListener.onQuestionError(errorMsg);
        }
    }

    void erraseEditText() {
        TextView textView = (TextView) answersFrame.getChildAt(0);
        textView.setText("");
    }

    private void erraseRadioGroup() {
        RadioGroup radioGroup = (RadioGroup) answersFrame.getChildAt(0);
        radioGroup.clearCheck();
    }

    private void erraseCheckBox() {
        CheckBox checkBox;
        for (int i = 0; i < answersFrame.getChildCount(); i++) {
            checkBox = (CheckBox) answersFrame.getChildAt(i);
            checkBox.setChecked(false);
        }
    }

    private void erraseSpinner() {
        Spinner spinner = (Spinner) answersFrame.getChildAt(0);
        spinner.setSelection(0);
    }

    private void erraseNumberPicker() {
        NumberPicker numberPicker = (NumberPicker) answersFrame.getChildAt(0);
        numberPicker.setValue(0);
        setAnswer("");
    }

    // MARK: getListener

    private DatePickerDialog.OnDateSetListener getDateListener(Question question, final Calendar myCalendar, final String myFormat, final EditText editText) {
        final String optionsValidate = question.getOptionsValidate();
        final int conditionValidate = question.getConditionValidate();

        return new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                String date = sdf.format(myCalendar.getTime());
                editText.setText(date);
                setAnswer(date);

                if (optionsValidate != null) {
                    String[] validateArray = optionsValidate.split("\\|");
                    if (conditionValidate == NOT_EQUAL) {
                            if (validateArray.length > 0) {
                                try {
                                    Date notEqualDate;
                                    if (validateArray[0].equals(CURRENT_VALUE)) {
                                        notEqualDate = new Date();
                                    } else {
                                        notEqualDate = sdf.parse(validateArray[0]);
                                    }
                                    if (Objects.requireNonNull(notEqualDate).compareTo(myCalendar.getTime()) == 0) {
                                        editText.setError("Data inválida! Não pode ser igual a " + date + "!");
                                        setAnswer("");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                    }
                }
            }
        };
    }

    private TextWatcher getTextWatcher(final Float min, final Float max, final Float equal, final Float notEqual, final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                String errorMessage = null;
                if (!text.isEmpty()) {

                    switch (mQuestion.getType()) {
                        case INTEGER:
                            try {
                                int intText = Integer.parseInt(text);
                                if (equal != null && equal != intText) {
                                    errorMessage = getResources().getString(R.string.equal_value_int, Math.round(equal));
                                } else if (notEqual != null && notEqual == intText) {
                                    errorMessage = getResources().getString(R.string.not_equal_value_int, Math.round(notEqual));
                                } else {
                                    if (min != null && intText < min) {
                                        errorMessage = getResources().getString(R.string.minimum_value_int, Math.round(min));
                                    }
                                    if (max != null && intText > max) {
                                        errorMessage = getResources().getString(R.string.maximum_value_int, Math.round(max));
                                    }
                                }
                            } catch (Exception e) {
                                errorMessage = getResources().getString(R.string.invalid_value);
                                erraseEditText();
                            }
                            break;
                        case DECIMAL:
                            try {
                                text = text.replace(',', '.');
                                float floatText = Float.parseFloat(text);
                                if (equal != null && equal != floatText) {
                                    errorMessage = getResources().getString(R.string.equal_value_float, equal);
                                } else if (notEqual != null && notEqual == floatText) {
                                    errorMessage = getResources().getString(R.string.not_equal_value_float, notEqual);
                                } else {
                                    if (min != null && floatText < min) {
                                        errorMessage = getResources().getString(R.string.minimum_value_float, min);
                                    }
                                    if (max != null && floatText > max) {
                                        errorMessage = getResources().getString(R.string.maximum_value_float, max);
                                    }
                                }
                                text = String.valueOf(floatText);
                            } catch (Exception e) {
                                errorMessage = getResources().getString(R.string.invalid_value);
                                erraseEditText();
                            }
                            break;
                    }
                }
                if (errorMessage == null) {
                    setAnswer(text);
                } else {
                    editText.setError(errorMessage);
                    setAnswer("");
                }
            }
        };
    }

    private NumberPicker.OnValueChangeListener getOnValueChangeListener(final String[] values) {
        return new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (newVal == 0) {
                    setAnswer("");
                } else {
                    setAnswer(values[newVal]);
                }
            }
        };
    }

    private RadioGroup.OnCheckedChangeListener getOnCheckedChangeListener(final String[] optionsArray) {
        return new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i >= ID_VIEW) {
                    setAnswer(optionsArray[i - ID_VIEW]);
                } else {
                    setAnswer("");
                }
            }
        };
    }

    private AdapterView.OnItemSelectedListener getSelectedListener(final String[] answersArray) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = answersArray[position];
                setAnswer(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener getChangeListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String answers = "";
                CheckBox checkBox;
                for (int i = 0; i < answersFrame.getChildCount(); i++) {
                    checkBox = (CheckBox) answersFrame.getChildAt(i);
                    if (checkBox.isChecked()) {
                        answers += String.format("%s|", checkBox.getText());
                    }
                }
                if (!answers.isEmpty()) {
                    answers = answers.substring(0, answers.length() - 1);
                }
                setAnswer(answers);
            }
        };
    }

    // MARK: addAnswer

    private void addAnswerInEditText(Question question, EditText editText) {
        Answer answer = question.getAnswer();
        String defaultOption = question.getDefaultOption();
        if (answer != null && answer.getExcluding() != 1) {
            editText.setText(answer.getText());
        } else if (defaultOption != null) {
            editText.setText(defaultOption);
            setAnswer(defaultOption);
        }
    }

    private void addAnswerInRadioButton(Answer answer, String defaultOption, RadioButton radioButton, String option) {
        if (answer != null && answer.getExcluding() != 1) {
            if (option.equals(answer.getText())) {
                radioButton.setChecked(true);
            }
        } else if (defaultOption != null) {
            if (option.equals(defaultOption)) {
                radioButton.setChecked(true);
                setAnswer(defaultOption);
            }
        }
    }

    private void addAnswerInCheckBox(Answer answer, String defaultOption, CheckBox checkBox, String option) {
        if (answer != null && answer.getExcluding() != 1) {
            String[] answers = answer.getText().split("\\|");
            for (String answerText : answers) {
                if (option.equals(answerText)) {
                    checkBox.setChecked(true);
                }
            }
        } else if (defaultOption != null) {
            if (option.equals(defaultOption)) {
                checkBox.setChecked(true);
                setAnswer(defaultOption);
            }
        }
    }

    private void addAnswerInSpinner(Answer answer, String defaultOption, Spinner spinner, ArrayList<String> spinnerArray) {
        String spinnerItem;
        boolean haveWhiteSpace = false;
        for (int i = 0; i < spinnerArray.size(); i++) {
            spinnerItem = spinnerArray.get(i);
            if (answer != null && answer.getExcluding() != 1) {
                if (spinnerItem.equals(answer.getText())) {
                    spinner.setSelection(i);
                }
            } else if (defaultOption != null) {
                if (spinnerItem.equals(defaultOption)) {
                    spinner.setSelection(i);
                    setAnswer(defaultOption);
                }
            }
            if (spinnerItem.isEmpty()) {
                haveWhiteSpace = true;
            }
        }
        canErase = haveWhiteSpace;
        txtErraseAnswer.setVisibility(haveWhiteSpace ? VISIBLE : GONE);
    }

    private void addAnswerInNumberPicker(Question question, NumberPicker numberPicker, String[] values) {
        Answer answer = question.getAnswer();
        if (answer != null && !answer.getText().isEmpty() && answer.getExcluding() != 1) {
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(answer.getText())) {
                    numberPicker.setValue(i);
                    break;
                }
            }
        } else {
            numberPicker.setValue(0);
            setAnswer("");
        }
    }

    void addAnswerInAuxDataFrame(final List<AuxDataBridge> auxDataBridges) {
        answersFrame.removeAllViews();
        View view;
        TextView textView;

        for (final AuxDataBridge auxDataTemp : auxDataBridges) {
            view = getLayoutInflater().inflate(R.layout.textfield_auxdata, answersFrame, false);
            textView = view.findViewById(R.id.txtDataSelected);
            textView.setText(auxDataTemp.getTextShownFirstLetterCapitalized());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auxDataBridges.remove(auxDataTemp);
                    setAnswer(auxDataBridgeToString(auxDataBridges));
                    addAnswerInAuxDataFrame(auxDataBridges);
                }
            });
            answersFrame.addView(view);
        }
    }

    // MARK: setConditionValidate

    private void setConditionValidateInEditText(Question question, EditText editText) {
        String optionsValidate = question.getOptionsValidate();
        int conditionValidate = question.getConditionValidate();
        Float min = null;
        Float max = null;
        Float equalValidate = null;
        Float notEqualValidate = null;

        if (optionsValidate != null) {
            String[] validateArray = optionsValidate.split("\\|");
            switch (conditionValidate) {
                case EQUAL:
                    if (validateArray.length > 0) {
                        String stringValue = validateArray[0].replace(',', '.');
                        equalValidate = Float.parseFloat(stringValue);
                    }
                    break;
                case NOT_EQUAL:
                    if (validateArray.length > 0) {
                        String stringValue = validateArray[0].replace(',', '.');
                        notEqualValidate = Float.parseFloat(stringValue);
                    }
                    break;
                case GREATER:
                    if (validateArray.length > 0) {
                        String stringValue = validateArray[0].replace(',', '.');
                        min = Float.parseFloat(stringValue) + 1;
                    }
                    break;
                case GREATER_OR_EQUAL:
                    if (validateArray.length > 0) {
                        String stringValue = validateArray[0].replace(',', '.');
                        min = Float.parseFloat(stringValue);
                    }
                    break;
                case LESS:
                    if (validateArray.length > 0) {
                        String stringValue = validateArray[0].replace(',', '.');
                        max = Float.parseFloat(stringValue) - 1;
                    }
                    break;
                case LESS_OR_EQUAL:
                    if (validateArray.length > 0) {
                        String stringValue = validateArray[0].replace(',', '.');
                        max = Float.parseFloat(stringValue);
                    }
                    break;
                case BETWEEN:
                    if (validateArray.length > 1) {
                        String stringMinValue = validateArray[0].replace(',', '.');
                        min = Float.parseFloat(stringMinValue);

                        String stringMaxValue = validateArray[1].replace(',', '.');
                        max = Float.parseFloat(stringMaxValue);
                    }
                    break;
            }
        }
        editText.addTextChangedListener(getTextWatcher(min, max, equalValidate, notEqualValidate, editText));
    }

    private String[] setConditionValidateInNumberPicker(Question question, int current, int minDefault, int maxDefault, NumberPicker numberPicker) {
        String optionsValidate = question.getOptionsValidate();
        int conditionValidate = question.getConditionValidate();
        int min = minDefault;
        int max = maxDefault;
        Integer notEqual = null;
        if (optionsValidate != null) {
            String[] validateArray = optionsValidate.split("\\|");
            switch (conditionValidate) {
                case EQUAL:
                    if (validateArray.length > 0) {
                        min = checkValidate(validateArray[0], current);
                        max = checkValidate(validateArray[0], current);
                    }
                    break;
                case NOT_EQUAL:
                    if (validateArray.length > 0) {
                        notEqual = checkValidate(validateArray[0], current);
                    }
                    break;
                case GREATER:
                    if (validateArray.length > 0) {
                        min = checkValidate(validateArray[0], current) + 1;
                    }
                    break;
                case GREATER_OR_EQUAL:
                    if (validateArray.length > 0) {
                        min = checkValidate(validateArray[0], current);
                    }
                    break;
                case LESS:
                    if (validateArray.length > 0) {
                        max = checkValidate(validateArray[0], current) - 1;
                    }
                    break;
                case LESS_OR_EQUAL:
                    if (validateArray.length > 0) {
                        max = checkValidate(validateArray[0], current);
                    }
                    break;
                case BETWEEN:
                    if (validateArray.length > 1) {
                        min = checkValidate(validateArray[0], current);
                        max = checkValidate(validateArray[1], current);
                    }
                    break;
            }
        }
        int total = notEqual == null ? (max - min) + 2 : (max - min) + 1;
        String[] values = new String[total];
        values[0] = "-";
        for (int i = 1; i < total; i++) {
            if (notEqual == null) {
                values[i] = String.valueOf(min);
            } else {
                if (notEqual != min) {
                    values[i] = String.valueOf(min);
                }
            }
            min++;
        }
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(total - 1);
        numberPicker.setDisplayedValues(values);
        return values;
    }

    private int checkValidate(String validate, int current) {
        if (validate.equals(CURRENT_VALUE)) {
            return current;
        } else {
            return stringToInt(validate);
        }
    }

    private void setConditionValidateInDatePicker(Question question, String dateFormat, DatePickerDialog dialog) throws Exception {
        String optionsValidate = question.getOptionsValidate();
        int conditionValidate = question.getConditionValidate();
        if (optionsValidate != null) {
            long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, new Locale("pt", "br"));
            String[] validateArray = optionsValidate.split("\\|");
            switch (conditionValidate) {
                case EQUAL:
                    if (validateArray.length > 0) {
                        long equalDate = checkDateValidate(validateArray[0], sdf);
                        dialog.getDatePicker().setMinDate(equalDate);
                        dialog.getDatePicker().setMaxDate(equalDate);
                    }
                    break;
                case NOT_EQUAL:
                    break;
                case GREATER:
                    if (validateArray.length > 0) {
                        long min = checkDateValidate(validateArray[0], sdf) + oneDayInMillis;
                        dialog.getDatePicker().setMinDate(min);
                    }
                    break;
                case GREATER_OR_EQUAL:
                    if (validateArray.length > 0) {
                        long min = checkDateValidate(validateArray[0], sdf);
                        dialog.getDatePicker().setMinDate(min);
                    }
                    break;
                case LESS:
                    if (validateArray.length > 0) {
                        long max = checkDateValidate(validateArray[0], sdf) - oneDayInMillis;
                        dialog.getDatePicker().setMaxDate(max);
                    }
                    break;
                case LESS_OR_EQUAL:
                    if (validateArray.length > 0) {
                        long max = checkDateValidate(validateArray[0], sdf);
                        dialog.getDatePicker().setMaxDate(max);
                    }
                    break;
                case BETWEEN:
                    if (validateArray.length > 1) {
                        long min = checkDateValidate(validateArray[0], sdf);
                        dialog.getDatePicker().setMinDate(min);

                        long max = checkDateValidate(validateArray[1], sdf);
                        dialog.getDatePicker().setMaxDate(max);
                    }
                    break;
            }
        }
    }

    private long checkDateValidate(String validate, SimpleDateFormat sdf) throws Exception {
        if (validate.equals(CURRENT_VALUE)) {
            return System.currentTimeMillis();
        } else {
            Date date = sdf.parse(validate);
            return Objects.requireNonNull(date).getTime();
        }
    }

    // MARK: Util

    private ColorStateList getColorList() {
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_checked}  // checked
        };
        int[] colors = new int[]{
                getResources().getColor(R.color.grey_text),
                getResources().getColor(R.color.colorPrimary),
        };
        return new ColorStateList(states, colors);
    }

    private LinearLayout.LayoutParams getLayoutParams() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}