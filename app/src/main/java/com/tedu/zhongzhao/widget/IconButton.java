package com.tedu.zhongzhao.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tedu.zhongzhao.R;
import com.tedu.base.util.AndroidUtils;

/**
 * 带icon的按钮
 * Created by huangyx on 2018/6/20.
 */
public class IconButton extends LinearLayout {

    private ImageView mIconView;
    private TextView mTextView;

    public IconButton(Context context) {
        this(context, null);
    }

    public IconButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setGravity(Gravity.CENTER);
        mIconView = new ImageView(context);
        mIconView.setScaleType(ImageView.ScaleType.FIT_XY);

        mTextView = new TextView(context);

        LayoutParams iconLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LayoutParams txtLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int textSize = 12, textSizePx = 0;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconButton);
            if (a.hasValue(R.styleable.IconButton_icon_width)) {
                int width = a.getDimensionPixelSize(R.styleable.IconButton_icon_width, 0);
                if (width > 0) {
                    iconLp.width = width;
                }
            }
            if (a.hasValue(R.styleable.IconButton_icon_height)) {
                int height = a.getDimensionPixelSize(R.styleable.IconButton_icon_height, 0);
                if (height > 0) {
                    iconLp.height = height;
                }
            }
            if (a.hasValue(R.styleable.IconButton_icon)) {
                mIconView.setImageResource(a.getResourceId(R.styleable.IconButton_icon, R.mipmap.ic_launcher));
            }

            int margin = a.getDimensionPixelSize(R.styleable.IconButton_space, AndroidUtils.dp2px(4));
            if (getOrientation() == HORIZONTAL) {
                txtLp.leftMargin = margin;
            } else {
                txtLp.topMargin = margin;
            }
            textSizePx = a.getDimensionPixelSize(R.styleable.IconButton_textSize, 0);
            ColorStateList colorList = a.getColorStateList(R.styleable.IconButton_textColor);
            if (colorList != null) {
                mTextView.setTextColor(colorList);
            } else {
                int color = a.getColor(R.styleable.IconButton_textColor, -1);
                if (color != -1) {
                    mTextView.setTextColor(color);
                }
            }
            if (a.hasValue(R.styleable.IconButton_text)) {
                mTextView.setText(a.getString(R.styleable.IconButton_text));
            }
            a.recycle();
        }
        if (textSizePx > 0) {
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx);
        } else {
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
        addView(mIconView, iconLp);
        addView(mTextView, txtLp);

    }

    public void setEnabled(boolean enabled, boolean showIcon) {
        setEnabled(enabled);
        mIconView.setVisibility(showIcon ? VISIBLE : GONE);
        mTextView.setEnabled(enabled);
        mIconView.setEnabled(enabled);
    }

    public void setValue(int icon, int text) {
        mIconView.setImageResource(icon);
        mTextView.setText(text);
    }

    public void setText(int text) {
        mTextView.setText(text);
    }
}
