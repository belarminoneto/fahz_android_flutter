package br.com.avanade.fahz.fragments.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import br.com.avanade.fahz.BuildConfig;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.BaseMessageActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewMessageFragment extends Fragment {
    @BindView(R.id.webview_telehealth)
    WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_telehealth, container, false);
        ButterKnife.bind(this, view);


        setupUi();

        Intent intent = Objects.requireNonNull(getActivity()).getIntent();
        String id = intent.getStringExtra("Id");
        String url = BuildConfig.WEB_URL + getString(R.string.url_view_message) + id;


        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.loadUrl(url);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setupUi() {
        ((BaseMessageActivity) Objects.requireNonNull(getActivity())).toolbarTitle.setText(getActivity().getString(R.string.view_message));
    }
}
