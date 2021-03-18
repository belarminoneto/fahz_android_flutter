package br.com.avanade.fahz.activities.anamnesis;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.avanade.fahz.Adapter.FamilyTreeAdapter;
import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.dao.QuestionnaireDAO;
import br.com.avanade.fahz.enums.SectionPosition;
import br.com.avanade.fahz.interfaces.OnLifeStatusClickListener;
import br.com.avanade.fahz.model.anamnesisModel.Answer;
import br.com.avanade.fahz.model.anamnesisModel.AnswerQuestionnaireRequest;
import br.com.avanade.fahz.model.anamnesisModel.AnswerQuestionnaireResponse;
import br.com.avanade.fahz.model.anamnesisModel.LifeAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.LifeStatusAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.Question;
import br.com.avanade.fahz.model.anamnesisModel.Questionnaire;
import br.com.avanade.fahz.model.anamnesisModel.QuestionnaireRequest;
import br.com.avanade.fahz.model.anamnesisModel.QuestionnaireRequest2;
import br.com.avanade.fahz.model.anamnesisModel.QuestionnaireResponse;
import br.com.avanade.fahz.model.anamnesisModel.SearchFamilyTreeRequest;
import br.com.avanade.fahz.model.anamnesisModel.SectionLifeStatusAnamnesis;
import br.com.avanade.fahz.network.APIServiceAnamnesis;
import br.com.avanade.fahz.util.AnamnesisSession;
import br.com.avanade.fahz.util.AnamnesisUtils;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.network.ServiceGeneratorAnamnesis.createAnamnesisService;
import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.ID_QUEST;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.ID_TYPE_QUEST;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.UNAUTHORIZED_ACCESS;

public class FamilyTreeAnamnesisActivity extends BaseAnamnesisActivity implements OnLifeStatusClickListener {

    @BindView(R.id.rvFamilyTree)
    RecyclerView mRvFamilyTree;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private FamilyTreeAdapter mAdapter;
    List<LifeStatusAnamnesis> lifeAnamnesisList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupUi();

        if (userAnamnesisSession.getToken() == null) {
            getSession(this::getAnswers);
        } else {
           if (userAnamnesisSession.getUserCPF().equals(FahzApplication.getInstance().getFahzClaims().getCPF())) {
               mApiService = createAnamnesisService(APIServiceAnamnesis.class);
           }else{
               userAnamnesisSession.setUserCPF(null);
               userAnamnesisSession.setToken(null);
               getSession(this::getAnswers);
           }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userAnamnesisSession.getToken() != null) {
            getAnswers();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_family_tree_anamnesis;
    }

    private void setupUi() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.questionnaires));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter = new FamilyTreeAdapter(this, this);
        mRvFamilyTree.setAdapter(mAdapter);
        mRvFamilyTree.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(@Nullable MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home) {
            finish();
        }
        assert item != null;
        return super.onOptionsItemSelected(item);
    }

    void getQuestionnaire(final LifeStatusAnamnesis lifeStatus) {
        setLoading(true);
        QuestionnaireRequest questionnaireRequest = new QuestionnaireRequest(lifeStatus.getIdTypeQuestionnaire(), lifeStatus.getCpf());
        Call<QuestionnaireResponse> call = mApiService.getQuestionnaire(questionnaireRequest);
        call.enqueue(new Callback<QuestionnaireResponse>() {
            @Override
            public void onResponse(@NonNull Call<QuestionnaireResponse> call, @NonNull Response<QuestionnaireResponse> response) {
                if (response.raw().code() == UNAUTHORIZED_ACCESS) {
                    getSession(() -> getQuestionnaire(lifeStatus));
                } else {
                    QuestionnaireResponse questionnaireResponse = response.body();
                    if (questionnaireResponse == null) {
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            String error = jObjError.getString("Message");
                            showAlert(error);
                        } catch (Exception e) {
                            showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), () -> finish());
                            LogUtils.error(getLocalClassName(), e);
                        }
                    } else {
                        insertQuestionnaire(questionnaireResponse);
                        startQuestionnaire(questionnaireResponse.getQuestionnaire());
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<QuestionnaireResponse> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), () -> finish());
                else if (t instanceof UnknownHostException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), () -> finish());
                else {
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), () -> finish());
                    LogUtils.error(getLocalClassName(), t);
                }

                setLoading(false);
            }
        });
    }

    void insertQuestionnaire(QuestionnaireResponse questionnaireResponse) {
        Questionnaire questionnaire = questionnaireResponse.getQuestionnaire();
        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        dao.insertQuestionnaire(questionnaire);
        LifeAnamnesis life = questionnaireResponse.getLife();
        userAnamnesisSession.setLife(life);
        if (life != null) {
            dao.saveSupplementaryData(life.getSupplementaryData());
        }
        for (Question question : questionnaire.getQuestions()) {
            question.setQuestionnaire(questionnaire);
            dao.insertQuestion(question);
            if (question.getAnswer() != null) {
                question.getAnswer().setUserCPF(userAnamnesisSession.getUserCPF());
                dao.saveAnswer(question.getAnswer());
            }
        }
    }

    void startQuestionnaire(Questionnaire questionnaire) {
        long idQuest = questionnaire.getId();
        int idTypeQuest = questionnaire.getIdType();

        Intent intent = new Intent(this, QuestionnaireActivity.class);
        intent.putExtra(ID_QUEST, idQuest);
        intent.putExtra(ID_TYPE_QUEST, idTypeQuest);
        startActivity(intent);
    }

    void startViewAnswers(LifeStatusAnamnesis lifeStatus) {
        Intent intent = new Intent(this, ViewAnswersActivity.class);
        intent.putExtra(ID_TYPE_QUEST, lifeStatus.getIdTypeQuestionnaire());
        startActivity(intent);
    }

    void getAnswers() {

        if(userAnamnesisSession.getUserCPF() == null){
            String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
            userAnamnesisSession.setUserCPF(cpf);
        }

        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        AnswerQuestionnaireRequest answerRequest = new AnswerQuestionnaireRequest();
        QuestionnaireRequest2 questionnaireRequest = new QuestionnaireRequest2();
        List<Answer> answerList = dao.selectAnswers(userAnamnesisSession.getUserCPF(), true);
        if (!answerList.isEmpty()) {
            Answer answer = answerList.get(0);
            questionnaireRequest.setIdType(answer.getIdType());
            questionnaireRequest.setIdQuestionnaire(answer.getIdQuest());
            questionnaireRequest.setAnswersList(answerList);
            answerRequest.setCpf(answer.getLifeCPF());
            answerRequest.setEnvironment(userAnamnesisSession.getEnvironment());
            answerRequest.setUserName(userAnamnesisSession.getUserCPF());
            answerRequest.setQuestionnaire(questionnaireRequest);
            keepAnswersDialog(answerRequest);
        } else {
            getFamilyTree(userAnamnesisSession.getUserCPF());
        }
    }

    void deleteAnswers(int questIdType, String lifeCPF) {
        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        dao.deleteAnswers(questIdType, lifeCPF);
    }

    void answerQuestionnaireRequest(final AnswerQuestionnaireRequest request) {
        setLoading(true);
        Call<AnswerQuestionnaireResponse> call = mApiService.answerQuestionnaire(request);
        call.enqueue(new Callback<AnswerQuestionnaireResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnswerQuestionnaireResponse> call, @NonNull Response<AnswerQuestionnaireResponse> response) {
                if (response.raw().code() == UNAUTHORIZED_ACCESS) {
                    getSession(() -> answerQuestionnaireRequest(request));
                } else {
                    AnswerQuestionnaireResponse body = response.body();
                    if (body == null) {
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            String error = jObjError.getString("Message");
                            showAlert(error);
                        } catch (Exception e) {
                            showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())));
                            LogUtils.error(getLocalClassName(), e);
                        }
                    } else if (!body.getInvalidAnswers().isEmpty()) {
                        showInvalidAnswersAlert(body.getInvalidAnswers());
                    } else {
                        deleteAnswers(body.getControl().getIdType(), body.getControl().getCpf());
                        getFamilyTree(userAnamnesisSession.getUserCPF());
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
                else {
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())));
                    LogUtils.error(getLocalClassName(), t);
                }
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

    void getFamilyTree(final String cpf) {
        setLoading(true);
        Call<List<LifeStatusAnamnesis>> call = mApiService.searchForFamilyTreeByCPF(new SearchFamilyTreeRequest(cpf));
        call.enqueue(new Callback<List<LifeStatusAnamnesis>>() {
            @Override
            public void onResponse(@NonNull Call<List<LifeStatusAnamnesis>> call, @NonNull Response<List<LifeStatusAnamnesis>> response) {
                if (response.raw().code() == UNAUTHORIZED_ACCESS) {
                    getSession(() -> getFamilyTree(cpf));
                } else {
                    List<LifeStatusAnamnesis> body = response.body();
                    if (body == null) {
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            String error = jObjError.getString("Message");
                            showAlert(error);
                        } catch (Exception e) {
                            showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), () -> finish());
                            LogUtils.error(getLocalClassName(), e);
                        }
                    } else {
                        lifeAnamnesisList = body;
                        reloadAdapter();
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<LifeStatusAnamnesis>> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), () -> finish());
                else if (t instanceof UnknownHostException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), () -> finish());
                else {
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), () -> finish());
                    LogUtils.error(getLocalClassName(), t);
                }

                setLoading(false);
            }
        });
    }

    void reloadAdapter() {
        List<SectionLifeStatusAnamnesis> sectionList = new ArrayList<>();
        SectionLifeStatusAnamnesis sectionDoing = null;
        SectionLifeStatusAnamnesis sectionToDo = null;
        SectionLifeStatusAnamnesis sectionDone = null;
        for (LifeStatusAnamnesis life : lifeAnamnesisList) {

            switch (life.getAnamnesisStatus()) {
                case DOING:
                    if (sectionDoing == null) {
                        sectionDoing = new SectionLifeStatusAnamnesis();
                        sectionDoing.setHeaderTitle(getResources().getString(R.string.questionnaires_doing));
                        sectionDoing.setPosition(SectionPosition.HORIZONTAL);
                    }
                    sectionDoing.getStatusAnamnesisList().add(life);
                    break;
                case TODO:
                    if (sectionToDo == null) {
                        sectionToDo = new SectionLifeStatusAnamnesis();
                        sectionToDo.setHeaderTitle(getResources().getString(R.string.questionnaires_todo));
                        sectionToDo.setPosition(SectionPosition.VERTICAL);
                    }
                    sectionToDo.getStatusAnamnesisList().add(life);
                    break;
                case DONE:
                    if (sectionDone == null) {
                        sectionDone = new SectionLifeStatusAnamnesis();
                        sectionDone.setHeaderTitle(getResources().getString(R.string.questionnaires_done));
                        sectionDone.setPosition(SectionPosition.VERTICAL);
                    }
                    sectionDone.getStatusAnamnesisList().add(life);
                    break;
            }
        }
        if (sectionDoing != null) {
            sectionList.add(sectionDoing);
        }
        if (sectionToDo != null) {
            sectionList.add(sectionToDo);
        }
        if (sectionDone != null) {
            sectionList.add(sectionDone);
        }
        mAdapter.setItems(sectionList);
    }

    @Override
    public void goToQuest(@Nullable LifeStatusAnamnesis lifeStatus) {
        if (lifeStatus != null) {
            userAnamnesisSession.setLifeCPF(lifeStatus.getCpf());
            userAnamnesisSession.setSituation(lifeStatus.getSituation());
            userAnamnesisSession.setAge(lifeStatus.getAge());
            AnamnesisSession.lifeSession = lifeStatus;

            if (lifeStatus.getPending() == 0 && lifeStatus.getEdit() == 0) {
                showAlert(getResources().getString(R.string.quest_already_answered));
            } else if (lifeStatus.getPercentage() == 0) {
                String message = getResources().getString(R.string.do_you_want_start_questionnaire);
                startQuestionnaireDialog(lifeStatus, message);
            } else {
                String message = getResources().getString(R.string.do_you_want_start_questionnaire_again);
                startQuestionnaireDialog(lifeStatus, message);
            }
        }
    }

    @Override
    public void goToAnswersList(@Nullable LifeStatusAnamnesis lifeStatus) {
        if (lifeStatus != null) {
            userAnamnesisSession.setLifeCPF(lifeStatus.getCpf());
            userAnamnesisSession.setSituation(lifeStatus.getSituation());
            userAnamnesisSession.setAge(lifeStatus.getAge());
            AnamnesisSession.lifeSession = lifeStatus;
            startViewAnswersDialog(lifeStatus);
        }
    }

    private void keepAnswersDialog(final AnswerQuestionnaireRequest answerRequest) {
        String title = getResources().getString(R.string.synchronize);
        String message = getResources().getString(R.string.do_you_want_send_answers);
        String yesMessage = getResources().getString(R.string.dialog_button_send);
        String noMessage = getResources().getString(R.string.delete);
        View.OnClickListener onYesClickListener = v -> {
            dialog.dismiss();
            answerQuestionnaireRequest(answerRequest);
        };
        View.OnClickListener onNoClickListener = v -> {
            deleteAnswers(answerRequest.getQuestionnaire().getIdType(), answerRequest.getCpf());
            dialog.dismiss();
            getFamilyTree(userAnamnesisSession.getUserCPF());
        };
        dialog = AnamnesisUtils.showQuestionDialog(title, message, yesMessage, noMessage,
                this, onYesClickListener, onNoClickListener);
    }

    private void startViewAnswersDialog(final LifeStatusAnamnesis lifeStatus) {
        final String typeQuest = lifeStatus.getTypeQuestionnaireFirstLetterCapitalized();
        String message = getResources().getString(R.string.do_you_want_see_answers);

        dialog = AnamnesisUtils.showQuestionDialog(typeQuest, message,
                this,
                v -> {
                    startViewAnswers(lifeStatus);
                    dialog.dismiss();
                });
    }

    private void startQuestionnaireDialog(final LifeStatusAnamnesis lifeStatus, String message) {
        final String typeQuest = lifeStatus.getTypeQuestionnaireFirstLetterCapitalized();
        dialog = AnamnesisUtils.showQuestionDialog(typeQuest, message,
                this,
                v -> {
                    getQuestionnaire(lifeStatus);
                    dialog.dismiss();
                });
    }
}