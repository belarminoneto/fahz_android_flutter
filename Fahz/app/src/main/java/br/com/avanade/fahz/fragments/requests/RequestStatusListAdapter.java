package br.com.avanade.fahz.fragments.requests;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.Request;
import br.com.avanade.fahz.model.response.RequestStep;

public class RequestStatusListAdapter extends RecyclerView.Adapter<RequestStatusListAdapter.StepHolder> {

    private Context mContext;
    private List<RequestStep> mList;
    private Request mRequest;

    RequestStatusListAdapter(Context mContext, Request request, List<RequestStep> list) {
        this.mContext = mContext;
        this.mRequest = request;
        this.mList = list;
    }

    @NonNull
    @Override
    public StepHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.steps_list_row,
                parent, false);
        return new StepHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StepHolder holder, int position) {

        RequestStep step = mList.get(position);
        holder.init(step, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class StepHolder extends RecyclerView.ViewHolder {

        private ImageView mCircle1;
        private ImageView mLineTop;
        private ImageView mLineBottom;
        private ImageView mLineCenter;
        private TextView mTvStatusName;
        private TextView mTvStatusDate;

        StepHolder(View itemView) {
            super(itemView);
            mCircle1 = itemView.findViewById(R.id.circle);
            mLineTop = itemView.findViewById(R.id.line_top);
            mLineCenter = itemView.findViewById(R.id.line_center);
            mLineBottom = itemView.findViewById(R.id.line_bottom);
            mTvStatusName = itemView.findViewById(R.id.tvStatusName);
            mTvStatusDate = itemView.findViewById(R.id.tvStatusDate);
        }

        void init(RequestStep step, int position) {

            setColors(step, position);

            mTvStatusName.setText(step.getDescription());
            String stepDate = step.getDate();
            if (!TextUtils.isEmpty(stepDate)) {
                String date = mContext.getString(R.string.request_date) + DateEditText.parseToDateRequest(step.getDate());
                mTvStatusDate.setText(date);
                mTvStatusDate.setVisibility(View.VISIBLE);
            } else {
                mTvStatusDate.setVisibility(View.GONE);
            }
        }

        private void setColors(RequestStep step, int position) {
            int actualStepColor = R.color.green_steps;

            boolean hasPassedSuccess = false;
            boolean hasPassedFail = false;

            if (mRequest.getFinalResult() == Request.Result.APPROVED) {
                if (step.isLast()) {
                    if (step.isPassedOn()) {
                        mCircle1.setBackgroundResource(R.drawable.step_passed);
                    } else {
                        actualStepColor = R.color.grey_text;
                        mCircle1.setBackgroundResource(R.drawable.circle_shape_not_passed_on);
                    }
                } else {
                    actualStepColor = R.color.green_steps;
                    setViewColor(mCircle1, actualStepColor);
                }

                hasPassedSuccess = true;

            } else if (mRequest.getFinalResult() == Request.Result.REPROVED) {
                if (step.isSuccess()) {
                    actualStepColor = R.color.green;
                    setViewColor(mCircle1, actualStepColor);
                } else if (step.isPassedOn() && !TextUtils.isEmpty(step.getDate())) {
                    actualStepColor = R.color.grey_text;
                    mCircle1.setBackgroundResource(R.drawable.step_reproved);
                } else {
                    actualStepColor = R.color.grey_text;
                    mCircle1.setBackgroundResource(R.drawable.circle_shape_not_passed_on);
                }

                hasPassedFail = true;
            } else {
                if (step.isSuccess()) {
                    actualStepColor = R.color.green_steps;
                    setViewColor(mCircle1, actualStepColor);
                } else {
                    actualStepColor = R.color.yellow;
                    mCircle1.setBackgroundResource(R.drawable.circle_shape_passed_on);
                }
            }

            if(hasPassedFail)
                mLineBottom.setBackgroundColor(ContextCompat.getColor(mContext,actualStepColor));
            else if(hasPassedSuccess)
                mLineBottom.setBackgroundColor(ContextCompat.getColor(mContext,actualStepColor));
            else
                mLineBottom.setBackgroundColor(ContextCompat.getColor(mContext,R.color.yellow));

            if(hasPassedFail && step.isPassedOn() && !TextUtils.isEmpty(step.getDate()))
                mLineTop.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
            else
                mLineTop.setBackgroundColor(ContextCompat.getColor(mContext, actualStepColor));

            mLineCenter.setBackgroundColor(ContextCompat.getColor(mContext, actualStepColor));

            if (position == 0) {
                mLineTop.setVisibility(View.INVISIBLE);
            } else if (position < mList.size() - 1) {
                mLineTop.setVisibility(View.VISIBLE);
                mLineBottom.setVisibility(View.VISIBLE);
            } else {
                mLineBottom.setVisibility(View.INVISIBLE);
            }
        }

        private void setViewColor(View view, int color) {
            Drawable background = view.getBackground();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable) background).getPaint().setColor(ContextCompat.getColor(mContext, color));
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(ContextCompat.getColor(mContext, color));
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable) background).setColor(ContextCompat.getColor(mContext, color));
            }
        }
    }
}
