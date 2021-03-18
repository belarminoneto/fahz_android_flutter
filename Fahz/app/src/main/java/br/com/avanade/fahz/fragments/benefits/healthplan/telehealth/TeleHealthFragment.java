package br.com.avanade.fahz.fragments.benefits.healthplan.telehealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.healthplan.BaseHealthControlActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TeleHealthFragment extends Fragment {

    @BindView(R.id.webview_telehealth)
    WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_telehealth, container, false);
        ButterKnife.bind(this, view);

        setupUi();

        Intent intent = getActivity().getIntent();
        String url = intent.getStringExtra("url");

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        if (url != null) {
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onPermissionRequest(PermissionRequest request) {
                    request.grant(request.getResources());
                }
            });
            webView.loadUrl(url);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setupUi() {
        ((BaseHealthControlActivity) getActivity()).toolbarTitle.setText(getActivity().getString(R.string.telehealth));
    }

}