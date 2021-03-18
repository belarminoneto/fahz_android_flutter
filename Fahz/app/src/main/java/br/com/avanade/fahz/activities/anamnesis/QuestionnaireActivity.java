package br.com.avanade.fahz.activities.anamnesis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.avanade.fahz.Adapter.DivisionAdapter;
import br.com.avanade.fahz.Adapter.QuestionAdapter;
import br.com.avanade.fahz.Adapter.QuestionStatusAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.dao.QuestionnaireDAO;
import br.com.avanade.fahz.interfaces.OnDivisorClickListener;
import br.com.avanade.fahz.interfaces.OnQuestionChangeListener;
import br.com.avanade.fahz.interfaces.OnSessionListener;
import br.com.avanade.fahz.interfaces.QuestionnaireActivityListener;
import br.com.avanade.fahz.model.anamnesisModel.Answer;
import br.com.avanade.fahz.model.anamnesisModel.AnswerQuestionnaireRequest;
import br.com.avanade.fahz.model.anamnesisModel.AnswerQuestionnaireResponse;
import br.com.avanade.fahz.model.anamnesisModel.Question;
import br.com.avanade.fahz.model.anamnesisModel.Questionnaire;
import br.com.avanade.fahz.model.anamnesisModel.QuestionnaireRequest2;
import br.com.avanade.fahz.model.anamnesisModel.SupplementaryDataAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.SupplementaryDataAnamnesisRequest;
import br.com.avanade.fahz.util.AnamnesisUtils;
import br.com.avanade.fahz.util.CustomViewPager;
import br.com.avanade.fahz.util.QuestionDependenceUtil;
import br.com.avanade.fahz.util.ZoomOutPageTransformer;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static br.com.avanade.fahz.Adapter.QuestionAdapter.DATA_PAGES;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.COMMENT;
import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.ID_QUEST;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.ID_TYPE_QUEST;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.UNAUTHORIZED_ACCESS;

public class QuestionnaireActivity extends BaseAnamnesisActivity implements QuestionnaireActivityListener, OnDivisorClickListener {

    @BindView(R.id.pager)
    CustomViewPager mPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rvQuestionsStatus)
    RecyclerView rvQuestionsStatus;

    @BindView(R.id.bottomLinear)
    LinearLayout bottomLinear;

    @BindView(R.id.btnBack)
    Button btnBack;

    @BindView(R.id.btnNext)
    Button btnNext;

    private RecyclerView rvDivision;

    private BottomSheetDialog mDivisionListBottomDialog;
    private MenuItem showDivisorMenuItem;

    Questionnaire mQuestionnaire;
    private List<Question> allQuestions;
    private Question currentQuestion;
    private int currentItem = 0;

    private String typeQuest;
    String lifeCPF;
    private boolean isLastPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        isLastPage = false;
        long idQuest = getIntent().getLongExtra(ID_QUEST, 0);
        int idType = getIntent().getIntExtra(ID_TYPE_QUEST, 0);
        lifeCPF = userAnamnesisSession.getLifeCPF();

        selectQuestionnaire(idQuest, idType);
        setupUi();
        completeQuestionnaireSetup();
        int index = currentAnswer();
        goToQuestion(index);
    }

    private int currentAnswer() {
        Question question;
        boolean haveAnswers = false;
        for (Question q : mQuestionnaire.getQuestions()) {
            if (q.getAnswer() != null) {
                haveAnswers = true;
                break;
            }
        }
        if (haveAnswers) {
            for (int i = 0; i < mQuestionnaire.getQuestions().size(); i++) {
                question = mQuestionnaire.getQuestions().get(i);
                if (question.getAnswer() == null && question.getType() != COMMENT) {
                    return i + (userAnamnesisSession.getLife() == null ? 0 : DATA_PAGES);
                }
            }
        }
        return 0;
    }

    private int getCurrentQuestionIndex() {
        if (currentQuestion == null) {
            return currentItem;
        } else {
            for (int i = 0; i < mQuestionnaire.getQuestions().size(); i++) {
                Question question = mQuestionnaire.getQuestions().get(i);
                if (question.getInternalCode() == currentQuestion.getInternalCode()) {
                    return i + (userAnamnesisSession.getLife() == null ? 0 : DATA_PAGES);
                }
            }
        }
        return currentItem;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_questionnaire;
    }

    private void setupUi() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(typeQuest);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        QuestionAdapter questionAdapter = new QuestionAdapter(getSupportFragmentManager(), this, userAnamnesisSession.getLife());
        mPager.setPagingEnabled(false);
        mPager.setCurrentItem(3);
        mPager.setAdapter(questionAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        QuestionStatusAdapter questionStatusAdapter = new QuestionStatusAdapter(userAnamnesisSession.getLife());
        questionStatusAdapter.setCurrentPosition(3);
        rvQuestionsStatus.setAdapter(questionStatusAdapter);
        rvQuestionsStatus.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mDivisionListBottomDialog = new BottomSheetDialog(this);
        mDivisionListBottomDialog.setContentView(R.layout.dialog_bottom_division_list);
        rvDivision = mDivisionListBottomDialog.findViewById(R.id.rvDivision);
        if (rvDivision != null) {
            rvDivision.setAdapter(new DivisionAdapter(this));
            rvDivision.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvDivision.setHasFixedSize(true);
        }
    }

    private void selectQuestionnaire(long idQuest, int idType) {
        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        mQuestionnaire = dao.selectQuestionnaire(new Questionnaire(idQuest, idType));
        allQuestions = dao.selectQuestions(mQuestionnaire);
        for (Question question : allQuestions) {
            question.setAnswer(dao.selectAnswer(question, lifeCPF));
        }
        typeQuest = mQuestionnaire.getTypeFirstLetterCapitalized();
    }

    @Override
    public boolean onCreateOptionsMenu(@Nullable Menu menu) {
        getMenuInflater().inflate(R.menu.questionnaire_menu, menu);
        showDivisorMenuItem = Objects.requireNonNull(menu).findItem(R.id.showDivisor);
        if (userAnamnesisSession.getLife() != null) {
            showDivisorMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@Nullable MenuItem item) {
        switch (Objects.requireNonNull(item).getItemId()) {
            case android.R.id.home:
                if (isLastPage) {
                    finish();
                } else {
                    quitQuestionnaireDialog();
                }
                break;
            case R.id.showDivisor:
                showDivisionListBottomDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isLastPage) {
            finish();
        } else {
            quitQuestionnaireDialog();
        }
    }

    @OnClick(R.id.btnBack)
    public void onBtnBackPressed() {
        onBack();
    }

    @OnClick(R.id.btnNext)
    public  void onBtnNextPressed() {
        answerCurrentQuestion();

        // checar se foi respondido
        completeQuestionnaireSetup();
        int currentItemTemp = getCurrentQuestionIndex();
        if (currentItemTemp == Objects.requireNonNull(mPager.getAdapter()).getCount() - 2) {
            answerQuestionnaireDialog();
        } else if (currentItemTemp < mPager.getAdapter().getCount() - 1) {
            goToQuestion(currentItemTemp + 1);
        }
    }

    private void completeQuestionnaireSetup() {
        QuestionDependenceUtil dependenceUtil = new QuestionDependenceUtil();
        mQuestionnaire = dependenceUtil.checkQuestionsDependence(mQuestionnaire, allQuestions, false);
        ((DivisionAdapter) rvDivision.getAdapter()).setupDivisor(dependenceUtil.mDivisorList);
        ((QuestionAdapter) Objects.requireNonNull(mPager.getAdapter())).setQuestionnaire(mQuestionnaire);
        ((QuestionStatusAdapter) rvQuestionsStatus.getAdapter()).setQuestionList(mQuestionnaire.getQuestions());
    }

    private void showQuestion(int currentItem) {
        Object currentFragment = Objects.requireNonNull(mPager.getAdapter()).instantiateItem(mPager, currentItem);
        if (currentFragment instanceof OnQuestionChangeListener) {
            ((OnQuestionChangeListener) currentFragment).onQuestionChanged();
        }
    }

    void showImageQuestion(Bitmap image) {
        AnamnesisUtils.showExplicativeDialogWithImage(image, this);
    }

    private void goToQuestion(int item) {
        showQuestion(item);
        if (userAnamnesisSession.getLife() == null) {
            currentQuestion = mQuestionnaire.getQuestions().get(item);
        } else {
            if (item >= DATA_PAGES) {
                currentQuestion = mQuestionnaire.getQuestions().get(item - DATA_PAGES);
            } else {
                currentQuestion = null;
                currentItem = item;
            }
        }
        mPager.setCurrentItem(item);

        if (userAnamnesisSession.getLife() != null && item < DATA_PAGES) {
            rvQuestionsStatus.setVisibility(GONE);
            if (showDivisorMenuItem != null) {
                showDivisorMenuItem.setVisible(false);
            }
        } else {
            if (showDivisorMenuItem != null) {
                showDivisorMenuItem.setVisible(rvDivision.getAdapter().getItemCount() > 0);
            }
            rvQuestionsStatus.setVisibility(VISIBLE);
            ((QuestionStatusAdapter) rvQuestionsStatus.getAdapter()).setCurrentPosition(item);
            rvQuestionsStatus.smoothScrollToPosition(item);
        }
        if (item == Objects.requireNonNull(mPager.getAdapter()).getCount() - 2) {
            btnNext.setText(R.string.finish);
        } else {
            btnNext.setText(R.string.proximo);
        }
        if (item == 0) {
            btnBack.setText(R.string.quit);
        } else {
            btnBack.setText(R.string.anterior);
        }
    }

    private void answerCurrentQuestion() {
        if (currentQuestion != null && currentQuestion.getAnswer() != null) {
            insertAnswer(currentQuestion.getAnswer());
            setAnswer(currentQuestion);
        }
    }

    private void insertAnswer(Answer answer) {
        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        dao.saveAnswer(answer);
    }

    private void setAnswer(Question question) {
        for (Question q : allQuestions) {
            if (question.getInternalCode() == q.getInternalCode()) {
                q.setAnswer(question.getAnswer());
                break;
            }
        }
    }

    void goToLastPage() {
        isLastPage = true;
        bottomLinear.setVisibility(GONE);
        mPager.setCurrentItem(Objects.requireNonNull(mPager.getAdapter()).getCount());
        ((QuestionStatusAdapter) rvQuestionsStatus.getAdapter()).setCurrentPosition(-1);
    }

    private void onBack() {
        if (getCurrentQuestionIndex() == 0) {
            quitQuestionnaireDialog();
        } else {
            completeQuestionnaireSetup();
            int previousItem = getCurrentQuestionIndex() - 1;
            goToQuestion(previousItem);
        }
    }

    private void quitQuestionnaireDialog() {
        String title = getResources().getString(R.string.quit);
        String message = getResources().getString(R.string.do_you_want_save_answers_before_quit);
        String yesMessage = getResources().getString(R.string.button_save);
        String noMessage = getResources().getString(R.string.delete);
        View.OnClickListener onYesClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getAnswers();
            }
        };
        View.OnClickListener onNoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAnswers(mQuestionnaire.getIdType(), lifeCPF);
                dialog.dismiss();
                QuestionnaireActivity.this.finish();
            }
        };
        dialog = AnamnesisUtils.showQuestionDialog(title, message, yesMessage, noMessage, this,
                onYesClickListener, onNoClickListener);
    }

    private void answerQuestionnaireDialog() {
        String title = getResources().getString(R.string.finish);
        String message = getResources().getString(R.string.do_you_want_finish_and_save);
        String confirm = getResources().getString(R.string.yes);
        String cancel = getResources().getString(R.string.no);

        dialog = AnamnesisUtils.showQuestionDialog(title, message, confirm, cancel,
                this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        getAnswers();
                    }
                });
    }

    @Override
    public void onAnswerChanged(@Nullable Question questionWithAnswerChanged) {
        if (currentQuestion != null && currentQuestion.getInternalCode() == Objects.requireNonNull(questionWithAnswerChanged).getInternalCode()) {
            this.currentQuestion = questionWithAnswerChanged;
        }
    }

    @Override
    public void onDataChanged(@NonNull SupplementaryDataAnamnesis dataAnamnesis) {
        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        dao.saveSupplementaryData(dataAnamnesis);
    }

    @Override
    public void enableNextButton(@NonNull Boolean enable) {
        btnNext.setAlpha(enable ? 1f : .5f);
        btnNext.setEnabled(enable);
    }

    @Override
    public void onClickDivisor(@NonNull Question divisor) {
        for (int i = 0; i < mQuestionnaire.getQuestions().size(); i++) {
            Question divisorTemp = mQuestionnaire.getQuestions().get(i);
            if (Objects.requireNonNull(divisor).getInternalCode() == divisorTemp.getDivider().getInternalCode()) {
                goToQuestion(i + (userAnamnesisSession.getLife() == null ? 0 : DATA_PAGES));
                mDivisionListBottomDialog.dismiss();
                break;
            }
        }
    }

    @Override
    public void onQuestionError(@Nullable String errorMsg) {
        showAlert(errorMsg);
    }

    void getAnswers() {
        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        AnswerQuestionnaireRequest answerRequest = new AnswerQuestionnaireRequest();
        QuestionnaireRequest2 questionnaireRequest = new QuestionnaireRequest2();
        List<Answer> answerList = new ArrayList<>();
        for (Question question : mQuestionnaire.getQuestions()) {
            Answer a = dao.selectAnswer(question, lifeCPF);
            if (a != null) {
                if (a.isAnswerChanged()) {
                    answerList.add(a);
                }
            }
        }
        questionnaireRequest.setIdType(mQuestionnaire.getIdType());
        questionnaireRequest.setIdQuestionnaire(mQuestionnaire.getId());
        questionnaireRequest.setAnswersList(answerList);
        answerRequest.setQuestionnaire(questionnaireRequest);

        SupplementaryDataAnamnesisRequest dataAnamnesis = dao.selectSupplementaryData(lifeCPF);
        if (dataAnamnesis != null) {
            answerRequest.setData(dataAnamnesis);
        }
        if (dataAnamnesis != null || !answerList.isEmpty()) {
            answerRequest.setCpf(userAnamnesisSession.getLifeCPF());
            answerRequest.setEnvironment(userAnamnesisSession.getEnvironment());
            answerRequest.setUserName(userAnamnesisSession.getUserCPF());
            answerQuestionnaireRequest(answerRequest);
        } else {
            String title = getResources().getString(R.string.dialog_title);
            String message = getResources().getString(R.string.no_answers_to_save);
            goBackDialog(title, message);
        }
    }

    void deleteAnswers(int questIdType, String lifeCPF) {
        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        dao.deleteAnswers(questIdType, lifeCPF);
    }

    @Override
    public void downloadImageRequested(@Nullable String imageName) {
        setLoading(true);
        Call<ResponseBody> call = mApiService.getImage(imageName);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@Nullable Call<ResponseBody> call, @Nullable Response<ResponseBody> response) {
                if (Objects.requireNonNull(response).isSuccessful() && response.body() != null) {
                    Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                    showImageQuestion(bmp);
                } else {
                    showAlert(getResources().getString(R.string.error_downloading_image));
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@Nullable Call<ResponseBody> call, @NonNull Throwable t) {
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

    void answerQuestionnaireRequest(final AnswerQuestionnaireRequest request) {
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
                    AnswerQuestionnaireResponse questionnaireResponse = response.body();
                    if (questionnaireResponse == null) {
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            String error = jObjError.getString("Message");
                            showAlert(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (!questionnaireResponse.getInvalidAnswers().isEmpty()) {
                        showInvalidAnswersAlert(questionnaireResponse.getInvalidAnswers());
                    } else if (questionnaireResponse.getControl().getPercentage() != 100) {
                        deleteAnswers(questionnaireResponse.getControl().getIdType(), questionnaireResponse.getControl().getCpf());
                        String title = getResources().getString(R.string.success);
                        String message = getResources().getString(R.string.answers_sent_successfully);
                        goBackDialog(title, message);
                    } else {
                        deleteAnswers(questionnaireResponse.getControl().getIdType(), questionnaireResponse.getControl().getCpf());
                        goToLastPage();
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

    void showInvalidAnswersAlert(String invalidAnswers) {
        String[] invalidAnswersArray = invalidAnswers.split("\\|");
        StringBuilder error = new StringBuilder(getResources().getQuantityString(R.plurals.question_problem, invalidAnswersArray.length));
        for (String invalidAnswer : invalidAnswersArray) {
            error.append(" ");
            error.append(invalidAnswer);
            error.append(",");
        }
        error = new StringBuilder(error.substring(0, error.length() - 1));
        error.append("!");
        showAlert(error.toString());
    }

    void goBackDialog(String title, String message) {
        String btnMessage = getResources().getString(R.string.dialog_ok);
        dialog = AnamnesisUtils.showSimpleDialogGoBack(title, message, btnMessage, this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                QuestionnaireActivity.this.finish();
            }
        });
    }

    private void showDivisionListBottomDialog() {
        mDivisionListBottomDialog.show();
    }

}