package com.huangzan.web;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HomeActivity extends AppCompatActivity {
    private WebView webview;
    private WebSettings webSettings;
    private WebViewClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        webview = (WebView) findViewById(R.id.wb_home);
        webSettings = webview.getSettings();
        client = new OwnerWebview();
        webSettings.setDefaultTextEncodingName("utf-8");
        webview.setWebViewClient(client);
        webview.loadUrl("http://www.baidu.com");
        webSettings.setJavaScriptEnabled(true);
    }
    private  class  OwnerWebview extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
