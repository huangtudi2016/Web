package com.huangzan.web;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;


public class WebActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView webview;
    //进度条
    private ProgressBar progressBar;

    private WebSettings webSettings;
    private WebViewClient client;

    private static int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        webview = (WebView) findViewById(R.id.wb_home);
        if (webview != null) {
            webSettings = webview.getSettings();
        }
        client = new OwnerWebview();
        webview.setWebViewClient(client);
        webview.setWebChromeClient(new OwnChomeClient());
        Intent intent = getIntent();
        String query = intent.getStringExtra("query");
        if (TextUtils.isEmpty(query)) {
            webview.loadUrl("http://www.baidu.com");
        } else {
            webview.loadUrl("http://www.baidu.com/s?wd=" + query);
        }
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);

        final FloatingActionsMenu fabWeb = (FloatingActionsMenu) findViewById(R.id.fab_web);

        if (fabWeb != null) {
            final FloatingActionButton refreshWeb = (FloatingActionButton) findViewById(R.id.action_refresh_web);
            final FloatingActionButton closeWeb = (FloatingActionButton) findViewById(R.id.action_close_web);
            refreshWeb.setOnClickListener(this);
            closeWeb.setOnClickListener(this);
//            FloatingActionButton actionC = new FloatingActionButton(getBaseContext());
//            actionC.setTitle("Hide/Show Action above");
//            actionC.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    refreshWeb.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
//                }
//            });

//            fabWeb.addButton(actionC);
//            actionA.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    actionA.setTitle("Action A clicked");
//                }
//            });

        }

    }

    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.pb_home);

    }

    private class OwnerWebview extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

    }


    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            webview.loadUrl("");
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.pre_home:
////                Log.i(TAG, "pre_home is pressed");
//                if (webview.canGoBack()) {
//                    webview.goBack();
//                }
//                break;
//            case R.id.next_home:
//                if (webview.canGoForward()) {
//                    webview.goForward();
//                }
//                break;
//            case R.id.tools_home:
//                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//                View toolView = inflater.inflate(R.layout.activity_tools, null);
//                toolsPopWindow.showAtLocation(toolView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, toolsButton.getHeight() + 20);
//                Button refresh = (Button) toolsPopWindow.getView(R.id.tools_normal_refresh);
//                Button favorites = (Button) toolsPopWindow.getView(R.id.tools_normal_favorites);
//                refresh.setOnClickListener(this);
//                favorites.setOnClickListener(this);
//                break;
//            case R.id.home_home:
//                webview.loadUrl("http://www.baidu.com");
//                break;
//            case R.id.window_home:
//                break;
            case R.id.action_refresh_web:
                webview.loadUrl(webview.getOriginalUrl());
                break;
            case R.id.action_close_web:
                webview.loadUrl("");
                webview=null;
                finish();
                break;
//            case R.id.tools_normal_favorites:
//
//                break;
            default:
                break;
        }
    }

    private class OwnChomeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        }

    }


}
