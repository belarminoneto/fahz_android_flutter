package br.com.avanade.fahz.activities.anamnesis;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONObject;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.dao.QuestionnaireDAO;
import br.com.avanade.fahz.fragments.anamnesis.QuestionFragment;
import br.com.avanade.fahz.interfaces.OnSessionListener;
import br.com.avanade.fahz.interfaces.QuestionnaireActivityListener;
import br.com.avanade.fahz.model.anamnesisModel.Answer;
import br.com.avanade.fahz.model.anamnesisModel.AnswerQuestionnaireRequest;
import br.com.avanade.fahz.model.anamnesisModel.AnswerQuestionnaireResponse;
import br.com.avanade.fahz.model.anamnesisModel.Question;
import br.com.avanade.fahz.model.anamnesisModel.Questionnaire;
import br.com.avanade.fahz.model.anamnesisModel.QuestionnaireRequest2;
import br.com.avanade.fahz.model.anamnesisModel.SupplementaryDataAnamnesis;
import br.com.avanade.fahz.util.AnamnesisUtils;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.ID_QUEST;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.ID_TYPE_QUEST;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.INTER_COD;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.UNAUTHORIZED_ACCESS;

public class EditAnswerActivity extends BaseAnamnesisActivity implements QuestionnaireActivityListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Question currentQuestionWithAnswer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setupUi();

        long idQuest = getIntent().getLongExtra(ID_QUEST, 0);
        int idTypeQuest = getIntent().getIntExtra(ID_TYPE_QUEST, 0);
        String lifeCPF = userAnamnesisSession.getLifeCPF();
        long internalCode = getIntent().getLongExtra(INTER_COD, 0);

        Questionnaire questionnaire = new Questionnaire(idQuest, idTypeQuest);
        Question question = selectQuestion(questionnaire, internalCode, lifeCPF);
        initQuestionFragment(question);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_edit_answer;
    }

    private void setupUi() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.view_answers));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private Question selectQuestion(Questionnaire questionnaire, long internalCode, String lifeCPF) {
        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        Question question = dao.selectQuestion(questionnaire, internalCode);
        if (question != null) {
            question.setAnswer(dao.selectAnswer(question, lifeCPF));
        }
        return question;
    }

    private void initQuestionFragment(Question question) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        QuestionFragment questionFragment = QuestionFragment.newInstance(question, this);
        fragmentTransaction.add(R.id.questionFrame, questionFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@Nullable MenuItem item) {
        if (Objects.requireNonNull(item).getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btnOK)
    public void onBtnOkPressed() {
        if (currentQuestionWithAnswer != null) {
            answerCurrentQuestionDialog();
        }
    }

    private void answerCurrentQuestionDialog() {
        String title = getResources().getString(R.string.confirm_edit);
        String message = getResources().getString(R.string.do_you_confirm_edit);
        String confirm = getResources().getString(R.string.yes);
        String cancel = getResources().getString(R.string.no);

        dialog = AnamnesisUtils.showQuestionDialog(title, message, confirm, cancel,
                this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setupAnswerRequest();
                        dialog.dismiss();
                    }
                });
    }

    private void setupAnswerRequest() {
        AnswerQuestionnaireRequest answerRequest = new AnswerQuestionnaireRequest();
        QuestionnaireRequest2 questionnaireRequest = new QuestionnaireRequest2();
        List<Answer> answerList = new ArrayList<>();
        Answer a = currentQuestionWithAnswer.getAnswer();
        if (a != null) {
            if (a.isAnswerChanged()) {
                answerList.add(a);
            }
        }
        if (!answerList.isEmpty()) {
            questionnaireRequest.setIdType(Objects.requireNonNull(a).getIdType());
            questionnaireRequest.setIdQuestionnaire(a.getIdQuest());
            questionnaireRequest.setAnswersList(answerList);
            answerRequest.setCpf(userAnamnesisSession.getLifeCPF());
            answerRequest.setEnvironment(userAnamnesisSession.getEnvironment());
            answerRequest.setUserName(userAnamnesisSession.getUserCPF());
            answerRequest.setQuestionnaire(questionnaireRequest);
            answerQuestionnaireRequest(answerRequest);
        } else {
            String title = getResources().getString(R.string.error);
            String message = getResources().getString(R.string.error_saving_answers);
            goBackDialog(title, message);
        }
    }

    private void answerQuestionnaireRequest(final AnswerQuestionnaireRequest request) {
        setLoading(true);
        Call<AnswerQuestionnaireResponse> call = mApiService.answerQuestionnaire(request);
        call.enqueue(new Callback<AnswerQuestionnaireResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnswerQuestionnaireResponse> call, @NonNull Response<AnswerQuestionnaireResponse> response) {
                if (response.raw().code() == UNAUTHORIZED_ACCESS) {
                    getSession(new OnSessionListener() {
                        @Override
                        public void onSessionSuccess() {
                            answerQuestionnaireRequest(request);
                        }
                    });
                } else {
                    AnswerQuestionnaireResponse body = response.body();
                    if (body == null) {
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            String error = jObjError.getString("Message");
                            showAlert(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (!body.getInvalidAnswers().isEmpty()) {
                        showInvalidAnswersAlert(body.getInvalidAnswers());
                    } else {
                        answerCurrentQuestion();
                        String title = getResources().getString(R.string.success);
                        String message = getResources().getString(R.string.successfully_edited_answer);
                        goBackDialog(title, message);
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<AnswerQuestionnaireResponse> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())));
                else if (t instanceof UnknownHostException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())));
                else if (t instanceof EOFException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())));
                else
                    showAlert(t.getMessage());
                setLoading(false);
            }
        });
    }

    private void goBackDialog(String title, String message) {
        String btnMessage = getResources().getString(R.string.dialog_ok);
        dialog = AnamnesisUtils.showSimpleDialogGoBack(title, message, btnMessage, this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                EditAnswerActivity.this.finish();
            }
        });
    }

    private void showInvalidAnswersAlert(String invalidAnswers) {
        String[] invalidAnswersArray = invalidAnswers.split("\\|");
        StringBuilder error = new StringBuilder(getResources().getQuantityString(R.plurals.question_problem, invalidAnswersArray.length));
        for (String invalidAnswer : invalidAnswersArray) {
            error.append(" ");
            error.append(invalidAnswer);
            error.append(",");
        }
        error = new StringBuilder(error.substring(error.length() - 1, error.length() - 1));
        error.append("!");
        showAlert(error.toString());
    }

    private void answerCurrentQuestion() {
        if (currentQuestionWithAnswer != null) {
            insertAnswer(currentQuestionWithAnswer.getAnswer());
        }
    }

    private void insertAnswer(Answer answer) {
        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        dao.saveAnswer(answer);
    }

    @Override
    public void onAnswerChanged(@Nullable Question question) {
        this.currentQuestionWithAnswer = question;
    }

    @Override
    public void onDataChanged(@Nullable SupplementaryDataAnamnesis dataAnamnesis) {
    }

    @Override
    public void enableNextButton(@Nullable Boolean enable) {
    }

    @Override
    public void onQuestionError(@Nullable String errorMsg) {
        showAlert(errorMsg);
    }

    @Override
    public void downloadImageRequested(@Nullable String imageName) {
    }
}
