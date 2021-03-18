package br.com.avanade.fahz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.anamnesisModel.LifeAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.Question;

import static br.com.avanade.fahz.Adapter.QuestionAdapter.DATA_PAGES;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.COMMENT;

public class QuestionStatusAdapter extends RecyclerView.Adapter<QuestionStatusAdapter.ViewHolder> {

    private List<Question> mQuestionList;
    private LifeAnamnesis mLife;
    private int currentPosition;

    public QuestionStatusAdapter(LifeAnamnesis life) {
        this.mLife = life;
    }

    @NonNull
    @Override
    public QuestionStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_question_status, parent, false);
        return new QuestionStatusAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionStatusAdapter.ViewHolder holder, int position) {
        Question question = mQuestionList.get(position);

        if (currentPosition < 0) {
            holder.viewStatus.setBackgroundResource(R.drawable.round_green_button);
        } else if (position == currentPosition) {
            holder.viewStatus.setBackgroundResource(R.drawable.round_blue_light_button);

        } else if (checkValidAnswer(question)
                || (position < currentPosition && question.getType() == COMMENT)) {
            holder.viewStatus.setBackgroundResource(R.drawable.round_green_button);

        } else if (!checkValidAnswer(question)
                && position < currentPosition
                && question.isRequired() == 1) {
            holder.viewStatus.setBackgroundResource(R.drawable.round_red_button);

        } else {
            holder.viewStatus.setBackgroundResource(R.drawable.round_gray_light_button);
        }
    }

    private boolean checkValidAnswer(Question question) {
        return question.getAnswer() != null && !question.getAnswer().getText().isEmpty();
    }

    @Override
    public int getItemCount() {
        return mQuestionList == null ? 0 : mQuestionList.size();
    }

    public void setQuestionList(List<Question> questionList) {
        this.mQuestionList = questionList;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position) {
        int index = position;
        if (mLife != null) {
            index -= DATA_PAGES;
        }
        this.currentPosition = index;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View viewStatus;

        public ViewHolder(View item) {
            super(item);
            viewStatus = item.findViewById(R.id.viewStatus);
        }
    }
}
