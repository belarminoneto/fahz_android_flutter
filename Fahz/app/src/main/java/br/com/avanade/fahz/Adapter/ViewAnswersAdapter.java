package br.com.avanade.fahz.Adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.anamnesisModel.Answer;
import br.com.avanade.fahz.model.anamnesisModel.Question;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.COMMENT;
import static br.com.avanade.fahz.fragments.anamnesis.QuestionFragment.DIVIDER;

public class ViewAnswersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final View.OnClickListener mOnClickListener;
    private List<Question> questionList;
    private boolean canChange;

    public ViewAnswersAdapter(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;

        switch (viewType) {
            case DIVIDER:
                itemView = inflater.inflate(R.layout.item_answers_divider, parent, false);
                return new ViewAnswersAdapter.ViewHolderDivider(itemView);
            case COMMENT:
                itemView = inflater.inflate(R.layout.item_answers_comment, parent, false);
                return new ViewAnswersAdapter.ViewHolderComment(itemView);
            default:
                itemView = inflater.inflate(R.layout.item_answers, parent, false);
                itemView.setOnClickListener(mOnClickListener);
                return new ViewAnswersAdapter.ViewHolderAnswer(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return questionList.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Question question = questionList.get(position);
        switch (holder.getItemViewType()) {
            case DIVIDER:
                ViewHolderDivider holderDivider = (ViewHolderDivider) holder;
                Spanned textDivider = Html.fromHtml(question.getText());
                holderDivider.txtText.setText(textDivider);
                break;
            case COMMENT:
                ViewHolderComment holderComment = (ViewHolderComment) holder;
                Spanned textComment = Html.fromHtml(question.getText());
                holderComment.txtText.setText(textComment);
                break;
            default:
                ViewHolderAnswer holderAnswer = (ViewHolderAnswer) holder;
                holderAnswer.imgEdit.setVisibility(canChange ? VISIBLE : GONE);
                holderAnswer.txtTitle.setText(question.getText());
                holderAnswer.txtAnswer.setText(setupAnswer(question.getAnswer()));
        }
    }

    private String setupAnswer(Answer answer) {
        String answerString = "";
        if (answer != null && !answer.getText().isEmpty()) {
            StringBuilder answerBuilder = new StringBuilder();

            String[] answerArray = answer.getText().split("\\|");
            for (int i = 0; i < answerArray.length; i++) {
                String answerTemp = answerArray[i];
                String[] answerAuxDataArray = answerTemp.split("@");
                if (answerAuxDataArray.length > 1) {
                    answerBuilder.append(answerAuxDataArray[1]);
                } else {
                    answerBuilder.append(answerTemp);
                }
                if (answerArray.length > 1) {
                    if (i == answerArray.length - 1) {
                        answerBuilder.append(".");
                    } else {
                        answerBuilder.append("; ");
                    }
                }
            }
            answerString = answerBuilder.toString();
        }
        return answerString;
    }

    public void setQuestionnaire(List<Question> questionList, boolean canChange) {
        this.questionList = questionList;
        this.canChange = canChange;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return questionList == null ? 0 : questionList.size();
    }

    public static class ViewHolderAnswer extends RecyclerView.ViewHolder {
        TextView txtTitle, txtAnswer;
        ImageView imgEdit;

        ViewHolderAnswer(View item) {
            super(item);
            txtTitle = item.findViewById(R.id.txtTitle);
            txtAnswer = item.findViewById(R.id.txtAnswer);
            imgEdit = item.findViewById(R.id.imgEdit);
        }
    }

    public static class ViewHolderDivider extends RecyclerView.ViewHolder {
        TextView txtText;

        ViewHolderDivider(View item) {
            super(item);
            txtText = item.findViewById(R.id.txtText);
        }
    }

    public static class ViewHolderComment extends RecyclerView.ViewHolder {
        TextView txtText;

        ViewHolderComment(View item) {
            super(item);
            txtText = item.findViewById(R.id.txtText);
        }
    }
}
