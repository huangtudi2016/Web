package com.huangzan.web.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangzan.recyclerview.adapter.BaseAdapter;
import com.huangzan.web.R;
import com.huangzan.web.module.BookMark;


/**
 * Created by huangzan on 16/7/24.
 */
public class BookMarkAdapter extends BaseAdapter<BookMark, BookMarkAdapter.ItemViewHolder> {

    private Context context;
    private final LayoutInflater mLayoutInflater;

    public BookMarkAdapter(Context context) {
        super(context);
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mLayoutInflater.inflate(R.layout.recycler_row, parent, false));
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder holder, final int position) {
        BookMark bookMark = getItemData(position);
        if (bookMark != null) {
            holder.id.setText(String.valueOf(bookMark.getId()));
            holder.name.setText(bookMark.getName());
            holder.url.setText(bookMark.getUrl());
        }
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeRemoved(position, dataList.size());
            }
        });
        Log.i("BookMarkAdapter", "onBindItemViewHolder:" + holder.name.getText().toString() + "------" + holder.url.getText().toString());
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView id;
        private TextView name;
        private TextView date;
        private TextView url;
        private RelativeLayout deleteBtn;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.item_id);
            name = (TextView) itemView.findViewById(R.id.item_name);
            date = (TextView) itemView.findViewById(R.id.item_date);
            url = (TextView) itemView.findViewById(R.id.item_url);
            deleteBtn = (RelativeLayout) itemView.findViewById(R.id.delete);

        }
    }

}
