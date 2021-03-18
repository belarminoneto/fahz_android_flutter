package br.com.avanade.fahz.fragments.requests;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.Request;
import br.com.avanade.fahz.model.response.RequestStep;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RequestDetailsFragment extends Fragment {

    @BindView(R.id.overlay)
    ViewGroup mOverlay;

    @BindView(R.id.content)
    ViewGroup mContent;

    @BindView(R.id.steps_list)
    RecyclerView mStepsList;

    @BindView(R.id.tv_request_title)
    TextView mTvRequestTitle;

    @BindView(R.id.tv_request_date)
    TextView mTvRequestDate;

    @BindView(R.id.tv_request_status)
    TextView mTvRequestStatus;

    Request request;

    private YoYo.AnimatorCallback onFinishAnimationListener;

    public static RequestDetailsFragment newInstance(Request request) {
        RequestDetailsFragment fragment = new RequestDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("request", request);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setOnFinishAnimationListener(YoYo.AnimatorCallback onFinishAnimationListener) {
        this.onFinishAnimationListener = onFinishAnimationListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            this.request = (Request) args.getSerializable("request");
        }

        View view = inflater.inflate(R.layout.fragment_request_details, container, false);
        ButterKnife.bind(this, view);

        if (request.getFinalResult() == Request.Result.PENDENT) {
            view.findViewById(R.id.layout_see_more).setVisibility(View.GONE);
        }

        mOverlay.setVisibility(View.INVISIBLE);
        mContent.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initHeader();

        initStatusHistoryList();

        startInitialAnimation();
    }

    private void initHeader() {
        mTvRequestTitle.setText(request.getBehaviorDescription());
        String date = getString(R.string.request_date) + DateEditText.parseTODate(request.getDate());
        mTvRequestDate.setText(date);

        mTvRequestStatus.setText(request.getStatus().description);
        Context context = getContext();
        if (context != null) {
            if (request.getStatus().description.equals(context.getString(R.string.approved_request))) {
                mTvRequestStatus.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.square_green_info_filled));
            } else if (request.getStatus().description.equals(context.getString(R.string.pending_request))) {
                mTvRequestStatus.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.square_yellow_info_filled));
            } else {
                mTvRequestStatus.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.square_red_info));
            }

        }
    }

    private void startInitialAnimation() {
        YoYo.with(Techniques.FadeIn).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mOverlay.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        mContent.setVisibility(View.VISIBLE);
                    }
                }).duration(200).playOn(mContent);
            }
        }).duration(200).playOn(mOverlay);
    }

    private void initStatusHistoryList() {
        if (this.request != null) {
            List<RequestStep> steps = new ArrayList<>(request.getFlows());
            //Collections.reverse(steps);
            RequestStatusListAdapter requestStatusListAdapter = new RequestStatusListAdapter(getContext(), request, steps);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mStepsList.setLayoutManager(layoutManager);
            mStepsList.setAdapter(requestStatusListAdapter);
        }
    }

    @OnClick(R.id.tv_see_more)
    public void seeMoreClick(View view) {
        String message = request.getMessage();
        if(message == null || message.equals(""))
            message = getString(R.string.no_approve_message);


        Utils.showSimpleDialog(getString(R.string.reason_request_card_label), message, getString(R.string.close), getActivity(), null);
    }

    @OnClick(R.id.overlay)
    public void overlayClick(View view) {
        startCloseAnimation();
    }

    @OnClick(R.id.btn_close)
    public void closeClick(View view) {
        startCloseAnimation();
    }

    public void startCloseAnimation() {
        YoYo.with(Techniques.SlideOutDown).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mContent.setVisibility(View.INVISIBLE);
                YoYo.with(Techniques.FadeOut).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        mOverlay.setVisibility(View.INVISIBLE);
                        if (onFinishAnimationListener != null) {
                            onFinishAnimationListener.call(animator);
                        }

                    }
                }).duration(200).playOn(mOverlay);
            }
        }).duration(200).playOn(mContent);
    }
}
