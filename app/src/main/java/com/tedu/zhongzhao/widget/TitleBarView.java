package com.tedu.zhongzhao.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tedu.base.util.AndroidUtils;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.activity.BaseActivity;
import com.tedu.zhongzhao.ui.NavBtnInfo;
import com.tedu.zhongzhao.ui.NavStyleInfo;
import com.tedu.zhongzhao.ui.PageInfo;
import com.tedu.zhongzhao.ui.UiPageConstans;
import com.tedu.zhongzhao.ui.UiUtil;

import java.util.List;

@SuppressWarnings("deprecation")
public class TitleBarView extends RelativeLayout {

    private TextView mTitleView;
    private ImageView mLeftView;
    private ImageView mRightView;

    private TextView mLeftDotView, mRightDotView;

    private Drawable mBgDrawable;
    private OnClickListener mLeftClickListener;

    private int mBtnWidth, mBtnHeight;

    public TitleBarView(Context context) {
        super(context);
        init(context);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mBtnWidth = context.getResources().getDimensionPixelSize(R.dimen.title_bar_width);
        mBtnHeight = context.getResources().getDimensionPixelSize(R.dimen.title_bar_height);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setPaddingTop(AndroidUtils.getStatusHeight());
        }*/
        LayoutInflater.from(context).inflate(R.layout.wgt_title_bar, this);
        this.mTitleView = (TextView) findViewById(R.id.wgt_title_v);
        this.mLeftView = (ImageView) findViewById(R.id.wgt_title_back_v);
        this.mRightView = (ImageView) findViewById(R.id.wgt_title_right_v);
        this.mRightView.setVisibility(View.INVISIBLE);

        mLeftDotView = (TextView) findViewById(R.id.wgt_title_back_point);
        mRightDotView = (TextView) findViewById(R.id.wgt_title_right_point);
    }

    public void setTitle(int resid) {
        this.mTitleView.setText(resid);
        if (mBgDrawable == null) {
            setBackgroundColor(getResources().getColor(R.color.title_bg));
        }
    }

    public void setTitle(CharSequence title) {
        this.mTitleView.setText(title);
        if (mBgDrawable == null) {
            setBackgroundColor(getResources().getColor(R.color.title_bg));
        }
    }

    public TextView getTitleView() {
        return this.mTitleView;
    }

    public void setLeftListener(OnClickListener listener) {
        this.mLeftClickListener = listener;
    }

    public void setPaddingTop(int top) {
        this.setPadding(this.getPaddingLeft(), top, this.getPaddingRight(), this.getPaddingBottom());
    }

    public int getNavBtnWidth() {
        return mBtnWidth;
    }

    public int getNavBtnHeight() {
        return mBtnHeight;
    }

   /* public void setRightText(String text) {
        if (TextUtils.isEmpty(text)) {
            this.mRightView.setVisibility(View.INVISIBLE);
        } else {
            this.mRightView.setVisibility(View.VISIBLE);
            this.mRightView.setText(text);
        }
    }*/

    public void setRightDrawable(int resid) {
        this.mRightView.setBackgroundResource(resid);
    }

   /* public void setRightTextSize(int size) {
        this.mRightView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }*/

    public void setRightListener(OnClickListener l) {
        this.mRightView.setOnClickListener(l);
    }

    public void showLeftDot(int count, boolean showNum) {
        if (count <= 0) {
            mLeftDotView.setVisibility(GONE);
        } else {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mLeftDotView.getLayoutParams();
            if (showNum) {
                lp.width = lp.height = AndroidUtils.dp2px(16);
                lp.leftMargin = -lp.width;
                mLeftDotView.setText(String.valueOf(count));
            } else {
                lp.width = lp.height = AndroidUtils.dp2px(6);
                lp.leftMargin = -lp.width;
                mLeftDotView.setText("");
            }
            mLeftDotView.setVisibility(View.VISIBLE);
        }
    }

    public void showRightDot(int count, boolean showNum) {
        if (count <= 0) {
            mRightDotView.setVisibility(GONE);
        } else {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRightDotView.getLayoutParams();
            if (showNum) {
                lp.width = lp.height = AndroidUtils.dp2px(16);
                lp.leftMargin = -lp.width;
                mRightDotView.setText(String.valueOf(count));
            } else {
                lp.width = lp.height = AndroidUtils.dp2px(6);
                lp.leftMargin = -lp.width;
                mRightDotView.setText("");
            }
            mRightDotView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置标题以及背景
     *
     * @param title 标题
     * @param style 背景风格
     */
    public void setTitle(String title, NavStyleInfo style) {
        setTitle(title);
        mBgDrawable = UiUtil.getDrawable(style);
        if (mBgDrawable == null) {
            setBackgroundColor(getResources().getColor(R.color.title_bg));
        } else {
            setBackgroundDrawable(mBgDrawable);
        }
    }

    private void setBtnDrawable(Drawable d, boolean left) {
        if (d != null) {
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }
        if (left) {
            mLeftView.setImageDrawable(d);
        } else {
            mRightView.setImageDrawable(d);
        }
    }

    public void setLeftBtn(int resId, View.OnClickListener listener) {
        mLeftView.setImageResource(resId);
        mLeftView.setOnClickListener(listener);
        mLeftView.setVisibility(VISIBLE);
    }

    /**
     * 设置左按钮
     *
     * @param btn NavBtnInfo
     */
    public void setLeftBtn(final NavBtnInfo btn) {
        if (btn != null) {
            if (UiPageConstans.NAV_BLACK.equalsIgnoreCase(btn.getType())) {
                setBtnDrawable(getResources().getDrawable(R.mipmap.icon_back), true);
//                mLeftView.setText("");
                if (mLeftClickListener != null) {
                    mLeftView.setOnClickListener(mLeftClickListener);
                }
            } else {
                Drawable d = null;
                if (btn.getStyle() != null) {
                    d = UiUtil.getStateListDrawable(btn.getStyle(), mBtnWidth, mBtnHeight);
                }
                setBtnDrawable(d, true);
                d = UiUtil.getDrawable(btn.getStyle());
                if (d != null) {
                    mLeftView.setBackgroundDrawable(d);
                }
//                if (d == null) {
//                    mLeftView.setText(btn.getTitle());
//                } else {
//                    mLeftView.setText("");
//                }
                mLeftView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UiPageConstans.NAV_BUTTON.equalsIgnoreCase(btn.getType())) {
                            UiUtil.doActive((BaseActivity) getContext(), btn.getFirst());
                        } else if (UiPageConstans.NAV_MENU.equalsIgnoreCase(btn.getType())) {
                            showMenu(mLeftView, btn.getItems());
                        }
//                        getContext().startActivity(new Intent(getContext(), MapActivity.class));
                    }
                });
            }
            mLeftView.setVisibility(VISIBLE);
        }
    }

    /**
     * 显示下拉菜单
     *
     * @param v     View
     * @param pages 子页面列表
     */
    private void showMenu(View v, List<PageInfo> pages) {
        if (pages != null && !pages.isEmpty()) {
            TitleMenuWidget widget = new TitleMenuWidget(getContext(), pages);
            widget.showAsDropDown(mLeftView, v == mLeftView ? 0 : getWidth() - widget.getWidth(), 0);
        }
    }

    /**
     * 设置右按钮
     *
     * @param btn NavBtnInfo
     */
    public void setRightBtn(final NavBtnInfo btn) {
        if (btn != null) {
            if (!UiPageConstans.NAV_BLACK.equalsIgnoreCase(btn.getType())) {
                Drawable d = null;
                if (btn.getStyle() != null) {
                    d = UiUtil.getStateListDrawable(btn.getStyle(), mBtnWidth, mBtnHeight);
                }
                setBtnDrawable(d, false);
                d = UiUtil.getDrawable(btn.getStyle());
                if (d != null) {
                    mRightView.setBackgroundDrawable(d);
                }

//                if (d == null) {
//                    mRightView.setText(btn.getTitle());
//                } else {
//                    mRightView.setText("");
//                }
                mRightView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UiPageConstans.NAV_BUTTON.equalsIgnoreCase(btn.getType())) {
                            UiUtil.doActive((BaseActivity) getContext(), btn.getFirst());
                        } else if (UiPageConstans.NAV_MENU.equalsIgnoreCase(btn.getType())) {
                            showMenu(mRightView, btn.getItems());
                        }
                    }
                });
            }
            mRightView.setVisibility(VISIBLE);
        }
    }
}
