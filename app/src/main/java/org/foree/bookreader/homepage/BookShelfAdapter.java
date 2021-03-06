package org.foree.bookreader.homepage;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.foree.bookreader.R;
import org.foree.bookreader.bean.book.Book;
import org.foree.bookreader.common.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by foree on 16-7-22.
 */
public class BookShelfAdapter extends BaseRecyclerAdapter<BookShelfAdapter.MyViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Book> bookList;
    private SparseBooleanArray mSelectedItemsIds;
    private Context mContext;

    public BookShelfAdapter(Context context, List<Book> itemList) {
        mLayoutInflater = LayoutInflater.from(context);
        bookList = itemList;
        mSelectedItemsIds = new SparseBooleanArray();
        mContext = context;
    }

    @Override
    public BookShelfAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.rv_book_shelf_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(final BookShelfAdapter.MyViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (bookList != null && !bookList.isEmpty()) {
            Book book = bookList.get(position);

            holder.tvBookName.setText(book.getBookName());

            // 设置选中的背景颜色
            holder.itemView.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);

            if (book.getBookCoverUrl() != null) {
                Glide.with(mContext).load(book.getBookCoverUrl()).crossFade().into(holder.imageView);
            }

            // 是否显示更新小圆点
            if (book.getUpdateTime().after(book.getModifiedTime())) {
                holder.imageViewUpdate.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewUpdate.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return (null != bookList ? bookList.size() : 0);
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    private void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemsIds.put(position, value);
        } else {
            mSelectedItemsIds.delete(position);
        }

        notifyDataSetChanged();
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedItemsIds() {
        return mSelectedItemsIds;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookName;
        ImageView imageView;
        ImageView imageViewUpdate;

        public MyViewHolder(View view) {
            super(view);
            tvBookName = (TextView) view.findViewById(R.id.tv_novel_name);
            imageView = (ImageView) view.findViewById(R.id.iv_novel_image);
            imageViewUpdate = (ImageView) view.findViewById(R.id.tv_update_ig);
        }
    }
}

