package com.huangzan.web;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.huangzan.web.common.ToolsPopWindow;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";
    private CoordinatorLayout mainContent;
    private AppBarLayout appbar;
    private Toolbar toolBar;
    private WebView webview;
    //进度条
    private ProgressBar progressBar;
    /*底部按钮*/
    private ImageButton preButton;
    private ImageButton nextButton;
    private ImageButton homeButton;
    private ImageButton toolsButton;
    private ImageButton windowButton;

private ToolsPopWindow toolsPopWindow;

    private WebSettings webSettings;
    private WebViewClient client;

    private static boolean isExit = false;
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        setSupportActionBar(toolBar);

        webview = (WebView) findViewById(R.id.wb_home);
        if (webview != null) {
            webSettings = webview.getSettings();
        }
        client = new OwnerWebview();
        webview.setWebViewClient(client);
        webview.setWebChromeClient(new OwnChomeClient());
        webview.loadUrl("http://www.baidu.com");
        preButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);
        toolsButton.setOnClickListener(this);
        windowButton.setOnClickListener(this);

        toolsPopWindow = new ToolsPopWindow(this, this.getWindowManager().getDefaultDisplay().getWidth()-30,
                this.getWindowManager().getDefaultDisplay().getHeight()/3);
    }

    private void initView() {
        mainContent = (CoordinatorLayout) findViewById(R.id.main_content);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.pb_home);
        preButton = (ImageButton) findViewById(R.id.pre_home);
        nextButton = (ImageButton) findViewById(R.id.next_home);
        homeButton = (ImageButton) findViewById(R.id.home_home);
        toolsButton = (ImageButton) findViewById(R.id.tools_home);
        windowButton = (ImageButton) findViewById(R.id.window_home);

    }

    private class OwnerWebview extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.getSettings().setDefaultTextEncodingName("utf-8");
            view.getSettings().setJavaScriptEnabled(true);
            changeStatueOfWebToolsButton();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return true;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query.startsWith("http://")) {
                webview.loadUrl(query);
            } else {
                webview.loadUrl("http://www.baidu.com/baidu?word=" + query);

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            if (!isExit) {
                isExit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessageDelayed(0, 2000);
            } else {
                finish();
                System.exit(0);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pre_home:
                Log.i(TAG, "pre_home is pressed");
                if (webview.canGoBack()) {
                    webview.goBack();
                }
                break;
            case R.id.next_home:
                if (webview.canGoForward()) {
                    webview.goForward();
                }
                break;
            case R.id.tools_home:
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View toolView = inflater.inflate(R.layout.activity_tools, null);
                toolsPopWindow.showAtLocation(toolView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, toolsButton.getHeight()+20);
                Button refresh = (Button) toolsPopWindow.getView(R.id.tools_normal_refresh);
                Button favorites = (Button) toolsPopWindow.getView(R.id.tools_normal_favorites);
                refresh.setOnClickListener(this);
                favorites.setOnClickListener(this);
                break;
            case R.id.home_home:
                webview.loadUrl("http://www.baidu.com");
                break;
            case R.id.window_home:
                break;
            case R.id.tools_normal_refresh:
               webview.loadUrl(webview.getOriginalUrl());
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
    /**
     * 设置工具栏回溯历史是否可用
     * */
    private void changeStatueOfWebToolsButton(){
        if(webview.canGoBack()){
            //设置可使用状态
            preButton.setEnabled(true);
        }else{
            //设置禁止状态
            preButton.setEnabled(false);
        }
        if(webview.canGoForward()){
            //设置可使用状态
            nextButton.setEnabled(true);
        }else{
            //设置禁止状态
            nextButton.setEnabled(false);
        }
    }
}
