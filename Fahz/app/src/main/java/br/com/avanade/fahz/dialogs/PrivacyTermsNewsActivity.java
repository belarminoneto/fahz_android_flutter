package br.com.avanade.fahz.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.Adapter.lgpd.CardViewNewsAdapter;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.lgpdModel.NewsPlainText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrivacyTermsNewsActivity extends AppCompatActivity {

    public TextView toolbarTitle;
    public List<NewsPlainText> newsPlainTextList = new ArrayList<>();

    @BindView(R.id.recycle_view)
    RecyclerView recycleView;

    Context mContext;
    @BindView(R.id.news_container)
    ConstraintLayout mNewsContainer;
    CardViewNewsAdapter mCardViewNewsAdapter;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_privacy_terms_news);
        ButterKnife.bind(this);
        mContext = this;

        Intent intent = getIntent();
        newsPlainTextList = intent.getParcelableArrayListExtra("News");

        recycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        if (newsPlainTextList.size() > 0) {
            mCardViewNewsAdapter = new CardViewNewsAdapter(PrivacyTermsNewsActivity.this, newsPlainTextList);
            recycleView.setAdapter(mCardViewNewsAdapter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @OnClick(R.id.btnDialogCancel)
    public void cancel() {
        finish();
    }
}
