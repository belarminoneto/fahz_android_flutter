package br.com.avanade.fahz.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.com.avanade.fahz.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReadTermsActivity extends AppCompatActivity {

    @BindView(R.id.news_container)
    RelativeLayout mNewsContainer;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.txtText)
    TextView txtText;

    String mTitle;
    String mText;

    Context mContext;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_read_terms);
        ButterKnife.bind(this);

        mContext = this;

        mTitle  = getIntent().getStringExtra("textTitle");
        mText  = getIntent().getStringExtra("readTerms");

        textTitle.setText(mTitle);
        mProgressDialog = new ProgressDialog(this);
        Spanned sp = Html.fromHtml(mText);
        txtText.setText(sp);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @OnClick(R.id.btnDialogCancel)
    public void cancel(View view) {
        finish();
    }
}
