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

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;

import br.com.avanade.fahz.Adapter.ViewAnswersAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.dao.QuestionnaireDAO;
import br.com.avanade.fahz.interfaces.OnSessionListener;
import br.com.avanade.fahz.model.anamnesisModel.Answer;
import br.com.avanade.fahz.model.anamnesisModel.Question;
import br.com.avanade.fahz.model.anamnesisModel.Questionnaire;
import br.com.avanade.fahz.model.anamnesisModel.QuestionnaireRequest;
import br.com.avanade.fahz.model.anamnesisModel.QuestionnaireResponse;
import br.com.avanade.fahz.util.QuestionDependenceUtil;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.AnamnesisSession.lifeSession;
import static br.com.avanade.fahz.util.AnamnesisSession.userAnamnesisSession;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.ID_QUEST;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.ID_TYPE_QUEST;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.INTER_COD;
import static br.com.avanade.fahz.util.ConstantsAnamnesis.UNAUTHORIZED_ACCESS;

public class ViewAnswersActivity extends BaseAnamnesisActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rvAnswers)
    RecyclerView rvAnswers;

    Questionnaire mQuestionnaire;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        try {
            int idTypeQuest = getIntent().getIntExtra(ID_TYPE_QUEST, 0);

            String lifeCPF = userAnamnesisSession.getLifeCPF();
            setupUi();
            getQuestionnaire(idTypeQuest, lifeCPF);
        } catch (Exception e) {
            showAlert(getResources().getString(R.string.error_showing_answers));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mQuestionnaire != null) {
            selectChangedAnswer();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_view_answers;
    }

    private void setupUi() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.view_answers));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewAnswersAdapter mAdapter = new ViewAnswersAdapter(this);
        rvAnswers.setAdapter(mAdapter);
        rvAnswers.setLayoutManager(new LinearLayoutManager(this));
    }

    void getQuestionnaire(final int idTypeQuest, final String lifeCPF) {
        setLoading(true);
        QuestionnaireRequest questionnaireRequest = new QuestionnaireRequest(idTypeQuest, lifeCPF);
        Call<QuestionnaireResponse> call = mApiService.getQuestionnaire(questionnaireRequest);
        call.enqueue(new Callback<QuestionnaireResponse>() {
            @Override
            public void onResponse(@NonNull Call<QuestionnaireResponse> call, @NonNull Response<QuestionnaireResponse> response) {
                if (response.raw().code() == UNAUTHORIZED_ACCESS) {
                    getSession(new OnSessionListener() {
                        @Override
                        public void onSessionSuccess() {
                            getQuestionnaire(idTypeQuest, lifeCPF);
                        }
                    });
                } else {
                    QuestionnaireResponse body = response.body();
                    if (body == null) {
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            String error = jObjError.getString("Message");
                            showAlert(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        mQuestionnaire = body.getQuestionnaire();
                        insertQuestionnaire();
                        setupQuestionnaire();
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<QuestionnaireResponse> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())));
                else if (t instanceof UnknownHostException)
                    showAlert(getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())));
                else
                    showAlert(t.getMessage());
                setLoading(false);
            }
        });
    }

    void insertQuestionnaire() {
        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        dao.insertQuestionnaire(mQuestionnaire);
        for (Question question : mQuestionnaire.getQuestions()) {
            question.setQuestionnaire(mQuestionnaire);
            dao.insertQuestion(question);
            if (question.getAnswer() != null) {
                question.getAnswer().setUserCPF(userAnamnesisSession.getUserCPF());
                dao.saveAnswer(question.getAnswer());
            }
        }
    }

    private void selectChangedAnswer() {
        QuestionnaireDAO dao = new QuestionnaireDAO(this);
        for (Question question : mQuestionnaire.getQuestions()) {
            Answer answer = dao.selectAnswer(question, userAnamnesisSession.getUserCPF());
            if (answer != null) {
                question.setAnswer(answer);
            }
        }
        setupQuestionnaire();
    }

    void setupQuestionnaire() {
        QuestionDependenceUtil dependenceUtil = new QuestionDependenceUtil();
        mQuestionnaire = dependenceUtil.checkQuestionsDependence(mQuestionnaire, mQuestionnaire.getQuestions(), true);
        ((ViewAnswersAdapter) rvAnswers.getAdapter()).setQuestionnaire(mQuestionnaire.getQuestions(), lifeSession.getEdit() == 1);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(@NonNull View v) {
        if (lifeSession.getEdit() == 1) {
            int itemPosition = rvAnswers.getChildLayoutPosition(v);
            long idQuest = mQuestionnaire.getId();
            int idTypeQuest = mQuestionnaire.getIdType();
            long internalCode = mQuestionnaire.getQuestions().get(itemPosition).getInternalCode();

            Intent intent = new Intent(this, EditAnswerActivity.class);
            intent.putExtra(ID_QUEST, idQuest);
            intent.putExtra(ID_TYPE_QUEST, idTypeQuest);
            intent.putExtra(INTER_COD, internalCode);
            startActivity(intent);
        } else {
            showAlert(getResources().getString(R.string.this_quest_cannot_be_changed));
        }
    }
}
