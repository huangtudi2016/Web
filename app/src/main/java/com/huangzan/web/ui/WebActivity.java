package com.huangzan.web.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.huangzan.web.R;
import com.huangzan.web.db.SQLManager;
import com.huangzan.web.module.BookMark;


public class WebActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView webview;
    //进度条
    private ProgressBar progressBar;

    private WebSettings webSettings;
    private WebViewClient client;

    //书签管理
    private SQLManager favAndHisManager;
    private boolean isBookMark = false;


    private FloatingActionButton refreshWeb = null;
    private FloatingActionButton closeWeb = null;
    private FloatingActionButton bookmarkWeb = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        favAndHisManager = new SQLManager(getApplicationContext());
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
            refreshWeb = (FloatingActionButton) findViewById(R.id.action_refresh_web);
            closeWeb = (FloatingActionButton) findViewById(R.id.action_close_web);
            bookmarkWeb = (FloatingActionButton) findViewById(R.id.action_bookmark_web);
            refreshWeb.setOnClickListener(this);
            closeWeb.setOnClickListener(this);
            bookmarkWeb.setOnClickListener(this);
            refreshWeb.setIcon(R.drawable.ic_refresh_black);
            closeWeb.setIcon(R.drawable.ic_close_black);

            isBookMark = favAndHisManager.isBookMarkExist(webview.getUrl());
            Log.i("11111111111111", "isBookMark:" + isBookMark);
            changeBookMarkIcon();

        }

    }


    @Override
    protected void onDestroy() {
        if (favAndHisManager != null) {
            favAndHisManager.closeDB();
        }
        super.onDestroy();
    }

    private void changeBookMarkIcon() {
        if (isBookMark) {
            bookmarkWeb.setIcon(R.drawable.ic_star_black);
        } else {
            bookmarkWeb.setIcon(R.drawable.ic_star_border_black);
        }
    }

    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.pb_home);
        webview = (WebView) findViewById(R.id.wb_home);

    }

    private class OwnerWebview extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            isBookMark = favAndHisManager.isBookMarkExist(url);
            Log.i("22222222222222", "isBookMark:" + isBookMark);
            changeBookMarkIcon();
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
            case R.id.action_refresh_web:
                webview.loadUrl(webview.getUrl());
                break;
            case R.id.action_close_web:
                webview.loadUrl("");
                webview = null;
                finish();
                break;
            case R.id.action_bookmark_web:
                isBookMark = favAndHisManager.isBookMarkExist(webview.getUrl());
                Log.i("333333333", "isBookMark:" + isBookMark);
                if (!isBookMark) {
                    bookmarkWeb.setIcon(R.drawable.ic_star_black);
                    BookMark bookMark = new BookMark();
                    bookMark.setUrl(webview.getUrl());
                    bookMark.setName(webview.getTitle());
                    favAndHisManager.addBookMark(bookMark);
                } else {
                    bookmarkWeb.setIcon(R.drawable.ic_star_border_black);
                    favAndHisManager.deleteBookMark(webview.getUrl());
                }

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
