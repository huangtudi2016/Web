package com.huangzan.web.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.huangzan.web.R;
import com.huangzan.web.db.SQLManager;
import com.huangzan.web.module.BookMark;
import com.huangzan.web.module.HistoryWeb;
import com.huangzan.web.module.ItemLongClickedPopWindow;

import java.util.List;
import java.util.zip.Inflater;


public class HistoryActivity extends AppCompatActivity {

    private static final String DEG_TAG = "HistoryActivity";
    public static final int RESULT_FAV_HIS = 0;
    public static final int RESULT_DEFAULT = -1;

    private FrameLayout frameLayout;
    //收藏历史按钮
    private Button bookmark;
    private Button history;

    //收藏历史的内容
    private ListView bookmarkContent;
    private ListView historyContent;

    //popupwindow弹窗
    private ItemLongClickedPopWindow itemLongClickedPopWindow;

    private LinkMovementMethod linkMovementMethod;

    //书签历史管理
    private SQLManager markAndHisManager;

    private List<BookMark> bookMarks;
    private List<HistoryWeb> historyWebs;

    //监听
    private ButtonClickedListener buttonClickedListener;
    private ListViewOnItemLongListener itemLongListener;
    private ListViewOnItemClickedListener itemClickedListener;

    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmak_history);
        initView();


        buttonClickedListener = new ButtonClickedListener();
        itemLongListener = new ListViewOnItemLongListener();
        itemClickedListener = new ListViewOnItemClickedListener();

        //添加监听
        history.setSelected(true);
        bookmark.setSelected(false);
        bookmark.setOnClickListener(buttonClickedListener);
        history.setOnClickListener(buttonClickedListener);
//
        bookmarkContent.setOnItemLongClickListener(itemLongListener);
        historyContent.setOnItemLongClickListener(itemLongListener);

        bookmarkContent.setOnItemClickListener(itemClickedListener);
        historyContent.setOnItemClickListener(itemClickedListener);

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        //初始化数据
        initDataBookmarks();
        initDataHistory();

        //添加默认返回值
        setResult(RESULT_DEFAULT);

    }

    private void initView() {
        //初始化
        bookmark = (Button) findViewById(R.id.btn_bookmark);
        history = (Button) findViewById(R.id.btn_history);

        bookmarkContent = (ListView) this.findViewById(R.id.bookmark_bh_bhactivity);
        historyContent = (ListView) this.findViewById(R.id.history_bh_bhactivity);

        frameLayout = (FrameLayout) findViewById(R.id.fragment_bhactivity);
    }


    /**
     * 初始化ListView中的数据
     */
    @SuppressWarnings("deprecation")
    private void initDataBookmarks() {
        //获取书签管理
        markAndHisManager = new SQLManager(this);
        bookMarks = markAndHisManager.getAllBookMark();
        bookmarkContent.setAdapter(new MyBookMarkAdapter(this));
    }

    private class MyBookMarkAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局 /*构造函数*/

        public MyBookMarkAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return bookMarks.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.id = (TextView) view.findViewById(R.id.item_id);
                holder.name = (TextView) view.findViewById(R.id.item_name);
                holder.url = (TextView) view.findViewById(R.id.item_url);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Log.i(DEG_TAG, "id:" + bookMarks.get(i).getId());
            holder.id.setText(String.valueOf(bookMarks.get(i).getId()));
            holder.name.setText(bookMarks.get(i).getName());
            holder.url.setText(bookMarks.get(i).getUrl());
            return view;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }

    private class ViewHolder {
        private TextView id;
        private TextView date;
        private TextView name;
        private TextView url;

    }


    /**
     * 初始化ListView中History的数据
     */
    @SuppressWarnings("deprecation")
    private void initDataHistory() {
        //获取历史记录管理
        markAndHisManager = new SQLManager(this);
        historyWebs = markAndHisManager.getAllHistory();
        historyContent.setAdapter(new MyHistoryAdapter(this));
    }

    private class MyHistoryAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局 /*构造函数*/

        public MyHistoryAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return historyWebs.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.id = (TextView) view.findViewById(R.id.item_id);
                holder.name = (TextView) view.findViewById(R.id.item_name);
                holder.url = (TextView) view.findViewById(R.id.item_url);
                holder.date = (TextView) view.findViewById(R.id.item_date);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Log.i(DEG_TAG, "id:" + historyWebs.get(i).getId());
            holder.id.setText(String.valueOf(historyWebs.get(i).getId()));
            holder.name.setText(historyWebs.get(i).getName());
            holder.url.setText(historyWebs.get(i).getUrl());
            holder.date.setText(String.valueOf(historyWebs.get(i).getDate()));
            return view;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }


    /**
     * 长按单项事件
     * 覆盖如下方法
     * 1.	onItemLongClick
     */
    private class ListViewOnItemLongListener implements OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(DEG_TAG, "long item cliced");
            if (parent.getId() == R.id.bookmark_bh_bhactivity) {
                itemLongClickedPopWindow = new ItemLongClickedPopWindow(HistoryActivity.this, ItemLongClickedPopWindow.FAVORITES_ITEM_POPUPWINDOW, screenWidth / 3, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemLongClickedPopWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.favandhis_activity));
                itemLongClickedPopWindow.showAsDropDown(view, view.getWidth() / 2, -view.getHeight() / 2);
                TextView modifyFavorite = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_modifyFavorites);
                TextView deleteFavorite = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_deleteFavorites);
                ItemClickedListener itemClickedListener = new ItemClickedListener(view);
                modifyFavorite.setOnClickListener(itemClickedListener);
                deleteFavorite.setOnClickListener(itemClickedListener);
            } else if (parent.getId() == R.id.history_bh_bhactivity) {
                itemLongClickedPopWindow = new ItemLongClickedPopWindow(HistoryActivity.this, ItemLongClickedPopWindow.HISTORY_ITEM_POPUPWINDOW, screenWidth / 3, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemLongClickedPopWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.favandhis_activity));
                itemLongClickedPopWindow.showAsDropDown(view, view.getWidth() / 2, -view.getHeight() / 2);
                TextView deleteHistory = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_deleteHistory);
                TextView deleteAllHistories = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_deleteAllHistories);
                ItemClickedListener itemClickedListener = new ItemClickedListener(view);
                deleteHistory.setOnClickListener(itemClickedListener);
                deleteAllHistories.setOnClickListener(itemClickedListener);
            }
            return true;
        }

    }

    /**
     * ListView单击单项事件
     * 覆盖如下方法
     * 1.	onClick
     */
    private class ListViewOnItemClickedListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Intent intent = new Intent(HistoryActivity.this, WebActivity.class);
            intent.putExtra("url", ((TextView) arg1.findViewById(R.id.item_url)).getText().toString());
            startActivity(intent);
            finish();
        }

    }


    /**
     * OnClickListener自定义继承类
     * 覆盖如下方法
     * 1.	onClick
     */
    private class ButtonClickedListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_bookmark) {
                if (!bookmarkContent.isShown()) {
                    bookmark.setSelected(true);
                    history.setSelected(false);
                    bookmarkContent.setVisibility(View.VISIBLE);
                    historyContent.setVisibility(View.GONE);
                }
            } else if (v.getId() == R.id.btn_history) {
                if (!historyContent.isShown()) {
                    history.setSelected(true);
                    bookmark.setSelected(false);
                    bookmarkContent.setVisibility(View.GONE);
                    historyContent.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    /**
     * popupwindow按钮事件处理类
     * 传入的ListView条目
     * 用来获取其中的id、name、url这三个值
     * 覆盖如下方法：
     * 1.	onClick
     */
    private class ItemClickedListener implements OnClickListener {

        private String item_id;
        private String item_name;
        private String item_url;
        private String item_date;

        public ItemClickedListener(View item) {
            this.item_id = ((TextView) item.findViewById(R.id.item_id)).getText().toString();
            this.item_name = ((TextView) item.findViewById(R.id.item_name)).getText().toString();
            this.item_url = ((TextView) item.findViewById(R.id.item_url)).getText().toString();
            this.item_date = ((TextView)item.findViewById(R.id.item_date)).getText().toString();
        }

        @Override
        public void onClick(View view) {
            //取消弹窗
            itemLongClickedPopWindow.dismiss();

            if (view.getId() == R.id.item_longclicked_modifyFavorites) {
                //弹出修改窗口
                LayoutInflater modifyFavoritesInflater = LayoutInflater.from(HistoryActivity.this);
                View modifyFavoritesView = modifyFavoritesInflater.inflate(R.layout.dialog_modify, null);
                final TextView item_name_input = (TextView) modifyFavoritesView.findViewById(R.id.dialog_name_input);
                final TextView item_url_input = (TextView) modifyFavoritesView.findViewById(R.id.dialog_url_input);
                item_name_input.setText(item_name);
                item_url_input.setText(item_url);
                new AlertDialog.Builder(HistoryActivity.this).setTitle("编辑书签").setView(modifyFavoritesView).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(DEG_TAG, "id:" + item_id + ",name:" + item_name + ",url:" + item_url);
                        BookMark bookMark = new BookMark(Integer.valueOf(item_id), item_url_input.getText().toString(), item_name_input.getText().toString());
                        boolean result = markAndHisManager.updateBookMark(bookMark);
                        if (result) {
//                            Toast.makeText(HistoryActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            snackbarShowSuccess("修改成功");
                            initDataBookmarks();
                            bookmarkContent.invalidate();
                        } else {
                            snackbarShowFail("修改失败");
//                            Toast.makeText(HistoryActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                }).setNegativeButton("取消", null).create().show();
            } else if (view.getId() == R.id.item_longclicked_deleteFavorites) {
                new AlertDialog.Builder(HistoryActivity.this).setTitle("删除书签").setMessage(getMessage("书签")).setPositiveButton("删除", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (markAndHisManager.deleteBookMark(item_url)) {
                            //删除成功
                            snackbarShowSuccess("删除成功");
//                            Toast.makeText(HistoryActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            initDataBookmarks();
                            bookmarkContent.invalidate();
                        } else {
                            snackbarShowFail("删除失败");
//                            Toast.makeText(HistoryActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
            } else if (view.getId() == R.id.item_longclicked_deleteHistory) {
                new AlertDialog.Builder(HistoryActivity.this).setTitle("删除历史").setMessage(getMessage("历史记录")).setPositiveButton("删除", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long date = Long.valueOf(item_date);
                        if (markAndHisManager.deleteHistory(date)) {
                            snackbarShowSuccess("删除成功");
                            initDataHistory();
                            historyContent.invalidate();
                        } else {
                            snackbarShowFail("删除失败");
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
            } else if (view.getId() == R.id.item_longclicked_deleteAllHistories) {
                new AlertDialog.Builder(HistoryActivity.this).setTitle("清空历史").setMessage("是否要清空历史？").setPositiveButton("清空", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (markAndHisManager.deleteAllHistory()) {
                            //删除成功
                            snackbarShowSuccess("成功清空");
                            initDataHistory();
                            historyContent.invalidate();
                        } else {
                            snackbarShowFail("清空失败");
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
            }

        }

        private void snackbarShowFail(String text) {
            Snackbar snackbarDeleteFail = Snackbar.make(frameLayout, text, Snackbar.LENGTH_SHORT);
            View snackbarView = snackbarDeleteFail.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.half_blue));
            snackbarDeleteFail.show();
        }

        private void snackbarShowSuccess(String text) {
            //删除成功
            Snackbar snackbarDeleteSuccess = Snackbar.make(frameLayout, text, Snackbar.LENGTH_SHORT);
            View snackbarView = snackbarDeleteSuccess.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.half_blue));
            snackbarDeleteSuccess.show();
        }

        private String getMessage(String lable){
            String message = "";
            if (TextUtils.isEmpty(item_name)){
                message= "是否要删除这个"+lable+"？";
            }else{
                message = "是否要删除\"" + item_name + "\"这条"+lable+"？";
            }
            return message;
        }

    }

    @Override
    public void finish() {
        if (markAndHisManager != null) {
            markAndHisManager.closeDB();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (markAndHisManager != null) {
            markAndHisManager.closeDB();
        }
        super.onDestroy();
    }


}
