package com.tedu.zhongzhao.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.activity.BaseActivity;
import com.tedu.zhongzhao.adapter.BaseListAdapter;
import com.tedu.zhongzhao.ui.PageInfo;
import com.tedu.zhongzhao.ui.PageNotifiManager;
import com.tedu.zhongzhao.ui.UiUtil;
import com.tedu.base.util.AndroidUtils;

import java.util.List;

import static android.view.View.GONE;

/**
 * 标题栏菜单
 * Created by huangyx on 2018/3/12.
 */
public class TitleMenuWidget extends PopupWindow implements AdapterView.OnItemClickListener {

    private Context mContext;
    private ItemsAdapter mAdapter;
    private ListView mListView;

    public TitleMenuWidget(Context context, List<PageInfo> pages) {
        this.mContext = context;
        this.setFocusable(true);
        setOutsideTouchable(true);
        this.setWidth(AndroidUtils.dp2px(100));
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));


        View contentView = LayoutInflater.from(context).inflate(R.layout.wgt_title_menu_layout, null);
        setContentView(contentView);
        mListView = (ListView) contentView.findViewById(R.id.title_menu_list_view);
        mAdapter = new ItemsAdapter(context);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                destroy();
            }
        });
        init(pages);
    }

    private void init(List<PageInfo> pages) {
        mAdapter.setItems(pages);
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View listItem = mAdapter.getView(i, null, mListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = totalHeight + (mListView.getDividerHeight() * (mAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        UiUtil.doActive((BaseActivity) mContext, mAdapter.getItem(i));
        dismiss();
    }

    private class ItemsAdapter extends BaseListAdapter<PageInfo> {

        public ItemsAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_wgt_title_menu_layout, null);
            }
            PageInfo page = getItem(i);
            String title = page.getName();
            TextView titleView = (TextView) view.findViewById(R.id.menu_text_view);
            titleView.setText(title);
            TextView dotView = (TextView) view.findViewById(R.id.menu_tip_view);
            showDot(dotView, PageNotifiManager.getShelfNotifiCount(page), true);
            return view;
        }

        private void showDot(TextView tv, int count, boolean showNum) {
            if (count <= 0) {
                tv.setVisibility(GONE);
            } else {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                if (showNum) {
                    lp.width = lp.height = AndroidUtils.dp2px(16);
                    tv.setText(String.valueOf(count));
                } else {
                    lp.width = lp.height = AndroidUtils.dp2px(6);
                    tv.setText("");
                }
                tv.setVisibility(View.VISIBLE);
            }
        }
    }


    public void destroy() {
        mContext = null;
        if (mAdapter != null) {
            mAdapter.destroy();
        }
    }
}
