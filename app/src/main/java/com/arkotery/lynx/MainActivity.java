package com.arkotery.lynx;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private ActionBar actionBar;
    private TextView Title;
    private WebView webView;
    private FrameLayout fullScreenContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_layout);
            Title = actionBar.getCustomView().findViewById(R.id.title_actionbar);
        }

        webView = findViewById(R.id.webview);
        fullScreenContainer = findViewById(R.id.fullscreen_container);

        if (webView != null) {
            WebSettings webSettings = webView.getSettings();

            // Enable JavaScript and DOM storage
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);

            // Set cache mode
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

            // Other settings for performance
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            webSettings.setEnableSmoothTransition(true);
            webSettings.setSupportZoom(false);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setDisplayZoomControls(false);

            if (savedInstanceState == null) {
                webView.loadUrl("https://www.google.com");
            } else {
                webView.restoreState(savedInstanceState);
            }

            webView.setWebViewClient(new WebViewClient());

            // Set the custom WebChromeClient for handling full-screen video
            webView.setWebChromeClient(new CustomWebClient());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null) {
            webView.saveState(outState);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Custom WebChromeClient for handling full-screen video
    private class CustomWebClient extends WebChromeClient {

        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public CustomWebClient() {
            mOriginalOrientation = getRequestedOrientation();
        }

        @Override
        public void onShowCustomView(View paramView, CustomViewCallback viewCallback) {
            if (mCustomView != null) {
                onHideCustomView();
                return;
            }

            mCustomView = paramView;
            mOriginalSystemUiVisibility = MainActivity.this.getWindow().getDecorView().getSystemUiVisibility();

            // Set the screen orientation to landscape when the custom view is shown
            MainActivity.this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            mCustomViewCallback = viewCallback;
            ((FrameLayout) MainActivity.this.getWindow().getDecorView()).addView(mCustomView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));

            // Set the UI to full-screen mode
            MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }

            ((FrameLayout) MainActivity.this.getWindow().getDecorView()).removeView(mCustomView);
            mCustomView = null;

            // Restore the original system UI visibility
            MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);

            // Reset the orientation back to the original orientation
            MainActivity.this.setRequestedOrientation(mOriginalOrientation);

            if (mCustomViewCallback != null) {
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
            }
        }
    }
}