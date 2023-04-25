package com.tedu.zhongzhao.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tedu.zhongzhao.fragment.WebFragment;

import java.util.List;

/**
 * Tab Adapter
 * Created by huangyx on 2018/3/12.
 */
public class TabFragmentAdapter extends FragmentPagerAdapter {

    private List<WebFragment> list;

    public TabFragmentAdapter(FragmentManager fm, List<WebFragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public WebFragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void destroy() {
        list = null;
    }
}
