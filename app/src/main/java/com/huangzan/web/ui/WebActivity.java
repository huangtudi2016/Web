package com.huangzan.web.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.huangzan.web.R;
import com.huangzan.web.db.SQLManager;
//import com.huangzan.web.manager.ImageDownloadManager;
//import com.huangzan.web.manager.RequestShowImageOnline;
import com.huangzan.web.module.BookMark;
import com.huangzan.web.module.HistoryWeb;
import com.huangzan.web.module.ItemLongClickedPopWindow;

import java.io.File;


public class WebActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private final static String TAG = "WebActivity";
    private String path = Environment.getExternalStorageDirectory() + File.separator +
            "webbrowser" + File.separator + "download";
    /**
     * 请求码（默认，代表不做处理）
     *
     * @param REQUEST_DEFAULT {@value #REQUEST_DEFAULT}
     */
    public static final int REQUEST_DEFAULT = -1;
    /**
     * 请求码（历史或者书签）
     *
     * @param REQUEST_OPEN_FAV_OR_HIS {@value #REQUEST_OPEN_FAV_OR_HIS}
     */
    public static final int REQUEST_OPEN_FAV_OR_HIS = 0;
    /**
     * 请求码（保存图片路径）
     *
     * @param REQUEST_SAVE_IMAGE_PATH {@value #REQUEST_SAVE_IMAGE_PATH}
     */
    public static final int REQUEST_SAVE_IMAGE_PATH = 1;


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
    private FloatingActionButton forwardWeb = null;


    private WebViewLongClickedListener webViewLongClickedListener;
    //弹出式菜单
    private ItemLongClickedPopWindow itemLongClickedPopWindow;
    private PopWindowMenu popWindowMenu;
    //保存图片弹出对话框
    private Dialog saveImageToChoosePath;

    //保存图片按钮
    private TextView choosePath;
    private TextView imgSaveName;

    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        favAndHisManager = new SQLManager(getApplicationContext());
        if (webview != null) {
            webSettings = webview.getSettings();
        }
        client = new OwnerWebview();
        webViewLongClickedListener = new WebViewLongClickedListener();
        webview.setOnLongClickListener(webViewLongClickedListener);
        webview.setWebViewClient(client);
        webview.setWebChromeClient(new OwnChomeClient());
        Intent intent = getIntent();
        String query = intent.getStringExtra("query");
        String url = intent.getStringExtra("url");
        if (TextUtils.isEmpty(query) && TextUtils.isEmpty(url)) {
            webview.loadUrl("http://www.baidu.com");
        } else if (!TextUtils.isEmpty(query)) {
            if (URLUtil.isNetworkUrl(query)) {
                webview.loadUrl(query);
            } else {
                webview.loadUrl("http://www.baidu.com/s?wd=" + query);
            }
        } else if (!TextUtils.isEmpty(url)) {
            webview.loadUrl(url);
        }
        webview.setOnTouchListener(this);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);


        final FloatingActionsMenu fabWeb = (FloatingActionsMenu) findViewById(R.id.fab_web);

        if (fabWeb != null) {
            refreshWeb = (FloatingActionButton) findViewById(R.id.action_refresh_web);
            closeWeb = (FloatingActionButton) findViewById(R.id.action_close_web);
            bookmarkWeb = (FloatingActionButton) findViewById(R.id.action_bookmark_web);
            forwardWeb = (FloatingActionButton) findViewById(R.id.action_forward_web);
            refreshWeb.setOnClickListener(this);
            closeWeb.setOnClickListener(this);
            bookmarkWeb.setOnClickListener(this);
            forwardWeb.setOnClickListener(this);
            refreshWeb.setIcon(R.drawable.ic_refresh_black);
            closeWeb.setIcon(R.drawable.ic_close_black);
            forwardWeb.setIcon(R.drawable.ic_navigate_next);

            isBookMark = favAndHisManager.isBookMarkExist(webview.getUrl());
            Log.d(TAG + "1111111111", "isBookMark:" + isBookMark);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN");
                PointerXY.x = (int) (event.getX());
                PointerXY.y = (int) (event.getY());
                Log.i(TAG, "1111111x:" + PointerXY.x);
                Log.i(TAG, "111111111y:" + PointerXY.y);
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "ACTION_MOVE");
                break;
            default:
                break;

        }
        return false;
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
            Log.d(TAG + "222222", "isBookMark:" + isBookMark);
            changeBookMarkIcon();

            HistoryWeb historyWeb = new HistoryWeb();
            historyWeb.setName(view.getTitle());
            historyWeb.setUrl(view.getUrl());
            historyWeb.setDate(System.currentTimeMillis());
            favAndHisManager.addHistory(historyWeb);
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
                Log.d(TAG + "333333333", "isBookMark:" + isBookMark);
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
            case R.id.action_forward_web:
                if (webview.canGoForward()) {
                    webview.goForward();
                }
                break;
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
     * OnClickListener自定义继承类
     * 用来解决菜单功能处理问题
     */
    private class PopWindowMenu implements View.OnClickListener {

        private int type;
        private String value;

        public PopWindowMenu(int type, String value) {
            this.type = type;
            this.value = value;
            Log.d(TAG, "type:" + type + ",value:" + value);
        }

        @Override
        public void onClick(View v) {
//            itemLongClickedPopWindow.dismiss();
//            if (v.getId() == R.id.item_longclicked_viewImage) {
//                //图片菜单-查看图片
//                new RequestShowImageOnline(WebActivity.this).execute(value);
//            } else if (v.getId() == R.id.item_longclicked_saveImage) {
//                //图片菜单-保存图片
//                final String imgName = value.substring(value.lastIndexOf("/") + 1);
//                new ImageDownloadManager(WebActivity.this).execute(imgName, value, path);
//
//            } else if (v.getId() == R.id.item_longclicked_viewImageAttributes) {
//                //图片菜单-查看属性
//            }
        }

    }

    private class WebViewLongClickedListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            WebView.HitTestResult result = ((WebView) v).getHitTestResult();
            if (null == result) return false;

            int type = result.getType();
            if (type == WebView.HitTestResult.UNKNOWN_TYPE) return false;

            if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) {
                // let TextView handles context menu
                return true;
            }

            // Setup custom handling depending on the type
            switch (type) {
                case WebView.HitTestResult.PHONE_TYPE:
                    // 处理拨号
                    break;
                case WebView.HitTestResult.EMAIL_TYPE:
                    // 处理Email
                    break;
                case WebView.HitTestResult.GEO_TYPE:
                    // TODO
                    break;
                case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                    // 超链接
                    Log.d(TAG, "超链接");
                    break;
                case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
//                case WebView.HitTestResult.IMAGE_TYPE:
//                    // 处理长按图片的菜单项
//                    Log.d(TAG, "图片");
//                    itemLongClickedPopWindow = new ItemLongClickedPopWindow(WebActivity.this, ItemLongClickedPopWindow.IMAGE_VIEW_POPUPWINDOW, screenWidth / 3, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    itemLongClickedPopWindow.showAsDropDown(v, PointerXY.x, -PointerXY.y * 2);
//                    TextView viewImage = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_viewImage);
//                    TextView saveImage = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_saveImage);
//                    TextView viewImageAttributes = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_viewImageAttributes);
//                    popWindowMenu = new PopWindowMenu(result.getType(), result.getExtra());
//                    viewImage.setOnClickListener(popWindowMenu);
//                    saveImage.setOnClickListener(popWindowMenu);
//                    viewImageAttributes.setOnClickListener(popWindowMenu);
//                    break;
                default:
                    break;
            }
            return true;
        }

    }

    private static class PointerXY {
        public static int x;
        public static int y;

        public static int getX() {
            return x;
        }

        public static int getY() {
            return y;
        }

    }

}
