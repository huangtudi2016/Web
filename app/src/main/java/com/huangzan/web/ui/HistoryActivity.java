package com.huangzan.web.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huangzan.recyclerview.decoration.GridSpacingItemDecoration;
import com.huangzan.recyclerview.listener.RecyclerTouchListener;
import com.huangzan.web.R;
import com.huangzan.web.adapter.BookMarkAdapter;
import com.huangzan.web.db.SQLManager;
import com.huangzan.web.module.BookMark;
import com.huangzan.web.module.HistoryWeb;
import com.huangzan.web.module.ItemLongClickedPopWindow;

import java.util.ArrayList;
import java.util.List;


public class HistoryActivity extends AppCompatActivity /*implements SwipeRefreshLayout.OnRefreshListener*/ {

    private static final String DEG_TAG = "HistoryActivity";
    public static final int RESULT_FAV_HIS = 0;
    public static final int RESULT_DEFAULT = -1;

    private FrameLayout frameLayout;
    //收藏历史按钮
    private Button bookmark;
    private Button history;

    //收藏历史的内容
//    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView bookmarkContent;
    private RecyclerView historyContent;


    private RecyclerTouchListener onTouchListener;

    private StaggeredGridLayoutManager slManager;

    //popupwindow弹窗
    private ItemLongClickedPopWindow itemLongClickedPopWindow;

    private LinkMovementMethod linkMovementMethod;

    //书签历史管理
    private SQLManager markAndHisManager;

    private List<BookMark> bookMarks = new ArrayList<>();
    private List<HistoryWeb> historyWebs;

    private BookMarkAdapter mybookmarkAdapter;

    private int currentPage = 0;

    //监听
    private ButtonClickedListener buttonClickedListener;
    private ListViewOnItemLongListener itemLongListener;
    private ListViewOnItemClickedListener itemClickedListener;

    private int screenWidth;

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
//        bookmarkContent.setOnItemLongClickListener(itemLongListener);
//        historyContent.setOnItemLongClickListener(itemLongListener);
//
//        bookmarkContent.setOnItemClickListener(itemClickedListener);
//        historyContent.setOnItemClickListener(itemClickedListener);

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        //初始化数据
        initBookMarkData(this);
        initBookMarksView();
//        if (bookMarks.size() < 10) {
//            mybookmarkAdapter.setHasMoreDataAndFooter(false, true);
//            return;
//        }
//        initDataHistory();


        //添加默认返回值
//        setResult(RESULT_DEFAULT);

    }

    private void initBookMarkData(HistoryActivity context) {
        bookMarks.clear();
        markAndHisManager = new SQLManager(context);
        bookMarks.addAll(markAndHisManager.getAllBookMark());

    }

    private void initView() {
        //初始化
        bookmark = (Button) findViewById(R.id.btn_bookmark);
        history = (Button) findViewById(R.id.btn_history);

//        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_bh_bhactivity);
        bookmarkContent = (RecyclerView) findViewById(R.id.bookmark_bh_bhactivity);
        historyContent = (RecyclerView) findViewById(R.id.history_bh_bhactivity);

        frameLayout = (FrameLayout) findViewById(R.id.fragment_bhactivity);
    }


    /**
     * 初始化ListView中的数据
     */
    @SuppressWarnings("deprecation")
    private void initBookMarksView() {
//        mRefreshLayout.setOnRefreshListener(this);
        bookmarkContent.addItemDecoration(new GridSpacingItemDecoration(1, 12, false));
        slManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        bookmarkContent.setLayoutManager(slManager);
        //获取书签管理

        mybookmarkAdapter = new BookMarkAdapter(this);
        mybookmarkAdapter.setDataList(bookMarks);
        mybookmarkAdapter.setHasMoreData(true);
        mybookmarkAdapter.setHasMoreDataAndFooter(false,true);
        bookmarkContent.setAdapter(mybookmarkAdapter);
//        bookmarkContent.addOnScrollListener(scrollListener);
        //添加触摸监听
        onTouchListener = new RecyclerTouchListener(this, bookmarkContent);
        onTouchListener.setIndependentViews(R.id.rowButton).setViewsToFade(R.id.rowButton).setClickable(new RecyclerTouchListener.OnRowClickListener() {
            @Override
            public void onRowClicked(int position) {//item点击监听
                Intent intent = new Intent(HistoryActivity.this, WebActivity.class);
                intent.putExtra("url", bookMarks.get(position).getUrl().toString());
                startActivity(intent);
                finish();
            }

            @Override
            public void onIndependentViewClicked(int independentViewID, int position) {//button点击监听
//                        Toast.makeText(getApplicationContext(), "Button in row " + (position + 1) + " clicked!", Toast.LENGTH_SHORT).show();
            }
        }).setSwipeOptionViews(R.id.edit, R.id.delete).setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
            @Override
            public void onSwipeOptionClicked(int viewID, final int position) {//侧拉出现的按钮监听事件
                if (viewID == R.id.edit) {
                    editBookMark(position);
                } else if (viewID == R.id.delete) {
                    deleteBookMark(position);
                }
//                mybookmarkAdapter.notifyDataSetChanged();
            }
        });
        bookmarkContent.addOnItemTouchListener(onTouchListener);


    }

    private void editBookMark(final int position) {
        LayoutInflater modifyFavoritesInflater = LayoutInflater.from(HistoryActivity.this);
        View modifyFavoritesView = modifyFavoritesInflater.inflate(R.layout.dialog_modify, null);
        final TextView item_name_input = (TextView) modifyFavoritesView.findViewById(R.id.dialog_name_input);
        final TextView item_url_input = (TextView) modifyFavoritesView.findViewById(R.id.dialog_url_input);
        item_name_input.setText(bookMarks.get(position).getName());
        item_url_input.setText(bookMarks.get(position).getUrl());
        new AlertDialog.Builder(HistoryActivity.this).setTitle("编辑书签").setView(modifyFavoritesView).setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(DEG_TAG, "id:" + bookMarks.get(position).getId() + ",name:" + bookMarks.get(position).getName() + ",url:" + bookMarks.get(position).getUrl());

                BookMark bookMark = bookMarks.get(position);
                bookMark.setName(item_name_input.getText().toString());
                bookMark.setUrl(item_url_input.getText().toString());
                boolean result = markAndHisManager.updateBookMark(bookMark);
                if (result) {
                    snackbarShowSuccess("修改成功");
                    mybookmarkAdapter.notifyDataSetChanged();
                } else {
                    snackbarShowFail("修改失败");
                }
            }

        }).setNegativeButton("取消", null).create().show();
    }

    private void deleteBookMark(final int position) {
//        new AlertDialog.Builder(HistoryActivity.this).setTitle("删除书签").setMessage(getMessage("书签", "")).setPositiveButton("删除", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
                BookMark bookMark = bookMarks.get(position);
                String url = bookMark.getUrl();
                boolean result = markAndHisManager.deleteBookMark(url);
                if (result) {
                    //删除成功
                    snackbarShowSuccess("删除成功");
                } else {
                    snackbarShowFail("删除失败");
                }
//            }
//        }).setNegativeButton("取消", null).create().show();
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

    private String getMessage(String lable, String item_name) {
        String message = "";
        if (TextUtils.isEmpty(item_name)) {
            message = "是否要删除这个" + lable + "？";
        } else {
            message = "是否要删除\"" + item_name + "\"这条" + lable + "？";
        }
        return message;
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
//            if (parent.getId() == R.id.bookmark_bh_bhactivity) {
//                itemLongClickedPopWindow = new ItemLongClickedPopWindow(HistoryActivity.this, ItemLongClickedPopWindow.FAVORITES_ITEM_POPUPWINDOW, screenWidth / 3, ViewGroup.LayoutParams.WRAP_CONTENT);
//                itemLongClickedPopWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.favandhis_activity));
//                itemLongClickedPopWindow.showAsDropDown(view, view.getWidth() / 2, -view.getHeight() / 2);
//                TextView modifyFavorite = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_modifyFavorites);
//                TextView deleteFavorite = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_deleteFavorites);
//                ItemClickedListener itemClickedListener = new ItemClickedListener(view);
//                modifyFavorite.setOnClickListener(itemClickedListener);
//                deleteFavorite.setOnClickListener(itemClickedListener);
//            } else if (parent.getId() == R.id.history_bh_bhactivity) {
//                itemLongClickedPopWindow = new ItemLongClickedPopWindow(HistoryActivity.this, ItemLongClickedPopWindow.HISTORY_ITEM_POPUPWINDOW, screenWidth / 3, ViewGroup.LayoutParams.WRAP_CONTENT);
//                itemLongClickedPopWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.favandhis_activity));
//                itemLongClickedPopWindow.showAsDropDown(view, view.getWidth() / 2, -view.getHeight() / 2);
//                TextView deleteHistory = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_deleteHistory);
//                TextView deleteAllHistories = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_deleteAllHistories);
//                ItemClickedListener itemClickedListener = new ItemClickedListener(view);
//                deleteHistory.setOnClickListener(itemClickedListener);
//                deleteAllHistories.setOnClickListener(itemClickedListener);
//            }
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
//    private class ItemClickedListener implements OnClickListener {
//
//        private String item_id;
//        private String item_name;
//        private String item_url;
//        private String item_date;
//
//        public ItemClickedListener(View item) {
//            this.item_id = ((TextView) item.findViewById(R.id.item_id)).getText().toString();
//            this.item_name = ((TextView) item.findViewById(R.id.item_name)).getText().toString();
//            this.item_url = ((TextView) item.findViewById(R.id.item_url)).getText().toString();
//            this.item_date = ((TextView) item.findViewById(R.id.item_date)).getText().toString();
//        }
//
//        @Override
//        public void onClick(final View view) {
//            //取消弹窗
//            itemLongClickedPopWindow.dismiss();
//
//            if (view.getId() == R.id.item_longclicked_modifyFavorites) {
//                //弹出修改窗口
//                LayoutInflater modifyFavoritesInflater = LayoutInflater.from(HistoryActivity.this);
//                View modifyFavoritesView = modifyFavoritesInflater.inflate(R.layout.dialog_modify, null);
//                final TextView item_name_input = (TextView) modifyFavoritesView.findViewById(R.id.dialog_name_input);
//                final TextView item_url_input = (TextView) modifyFavoritesView.findViewById(R.id.dialog_url_input);
//                item_name_input.setText(item_name);
//                item_url_input.setText(item_url);
//                new AlertDialog.Builder(HistoryActivity.this).setTitle("编辑书签").setView(modifyFavoritesView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d(DEG_TAG, "id:" + item_id + ",name:" + item_name + ",url:" + item_url);
//                        BookMark bookMark = new BookMark(Integer.valueOf(item_id), item_url_input.getText().toString(), item_name_input.getText().toString());
//                        boolean result = markAndHisManager.updateBookMark(bookMark);
//                        if (result) {
////                            Toast.makeText(HistoryActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
//                            snackbarShowSuccess("修改成功");
//                            mybookmarkAdapter.notifyItemChanged(bookmarkContent.getChildAdapterPosition(view));
////                            initBookMarksView();
////                            bookmarkContent.setLayoutManager(slManager);
////                            bookmarkContent.invalidateItemDecorations();
////                            bookmarkContent.getAdapter().notifyItemChanged(bookmarkContent.getChildAdapterPosition(view));
//                        } else {
//                            snackbarShowFail("修改失败");
////                            Toast.makeText(HistoryActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                }).setNegativeButton("取消", null).create().show();
//            } else if (view.getId() == R.id.item_longclicked_deleteFavorites) {
//                new AlertDialog.Builder(HistoryActivity.this).setTitle("删除书签").setMessage(getMessage("书签")).setPositiveButton("删除", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (markAndHisManager.deleteBookMark(item_url)) {
//                            //删除成功
//                            snackbarShowSuccess("删除成功");
////                            Toast.makeText(HistoryActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
////                            initBookMarksView();
////                            bookmarkContent.setLayoutManager(slManager);
//                            mybookmarkAdapter.notifyItemRemoved(bookmarkContent.getChildAdapterPosition(view));
////                            bookmarkContent.invalidateItemDecorations();
////                            bookmarkContent.getAdapter().notifyItemRemoved(bookmarkContent.getChildAdapterPosition(view));
//                        } else {
//                            snackbarShowFail("删除失败");
////                            Toast.makeText(HistoryActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).setNegativeButton("取消", null).create().show();
//            } else if (view.getId() == R.id.item_longclicked_deleteHistory) {
//                new AlertDialog.Builder(HistoryActivity.this).setTitle("删除历史").setMessage(getMessage("历史记录")).setPositiveButton("删除", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        long date = Long.valueOf(item_date);
//                        if (markAndHisManager.deleteHistory(date)) {
//                            snackbarShowSuccess("删除成功");
////                            initDataHistory();
//                            historyContent.invalidate();
//                        } else {
//                            snackbarShowFail("删除失败");
//                        }
//                    }
//                }).setNegativeButton("取消", null).create().show();
//            } else if (view.getId() == R.id.item_longclicked_deleteAllHistories) {
//                new AlertDialog.Builder(HistoryActivity.this).setTitle("清空历史").setMessage("是否要清空历史？").setPositiveButton("清空", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (markAndHisManager.deleteAllHistory()) {
//                            //删除成功
//                            snackbarShowSuccess("成功清空");
////                            initDataHistory();
//                            historyContent.invalidate();
//                        } else {
//                            snackbarShowFail("清空失败");
//                        }
//                    }
//                }).setNegativeButton("取消", null).create().show();
//            }
//
//        }
//

//    }

//    private ScrollListener scrollListener = new ScrollListener(slManager) {
//        @Override
//        public void onLoadMore() {
//            loadMore();
//            currentPage++;
////            Toast.makeText(HistoryActivity.this, "加载更多 page->" + currentPage, Toast.LENGTH_SHORT).show();
//        }
//
//    };
//
//    private void loadMore() {
//        //模拟加载更多
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (bookMarks.size() < 10) {
//                    mybookmarkAdapter.setHasMoreDataAndFooter(false, true);
//                    return;
//                }
//                initBookMarkData(HistoryActivity.this);
//                bookmarkContent.setAdapter(mybookmarkAdapter);
//                ScrollListener.setLoadMore(!ScrollListener.loadMore);
//            }
//        }, 1000);
//    }

//    @Override
//    public void onRefresh() {
//        //避免刷新过快
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mRefreshLayout.setRefreshing(false);
//            }
//        }, 1000);
//    }

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
