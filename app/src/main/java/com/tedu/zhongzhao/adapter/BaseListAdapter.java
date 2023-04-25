package com.tedu.zhongzhao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter基类
 * Created by huangyx on 2015.9.14.
 */
@SuppressWarnings("Convert2Diamond")
public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected List<T> mItems;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public BaseListAdapter(Context context) {
        this.mItems = new ArrayList<T>();
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void addItems(List<T> items) {
        if (items != null && !items.isEmpty()) {
            boolean changed = false;
            for (T t : items) {
                if (!mItems.contains(t)) {
                    mItems.add(t);
                    changed = true;
                }
            }
            if (changed) {
                notifyDataSetChanged();
            }
        }
    }

    public void setItems(List<T> items) {
        mItems.clear();
        if (items != null && !items.isEmpty()) {
            mItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        if (item != null && !mItems.contains(item)) {
            mItems.add(item);
            notifyDataSetChanged();
        }
    }

    public void deleteItem(T item) {
        if (item != null && !mItems.isEmpty() && mItems.remove(item)) {
            notifyDataSetChanged();
        }
    }

    public void deleteItems(List<T> items) {
        boolean changed = false;
        if (items != null && !items.isEmpty() && !items.isEmpty()) {
            for (T t : items) {
                if (mItems.contains(t)) {
                    mItems.remove(t);
                    changed = true;
                }
            }
        }
        if (changed) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void destroy() {
        mContext = null;
        if (mItems != null) {
            mItems.clear();
        }
        mInflater = null;
    }
}
