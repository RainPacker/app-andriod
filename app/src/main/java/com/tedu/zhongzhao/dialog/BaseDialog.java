package com.tedu.zhongzhao.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.tedu.zhongzhao.R;


/**
 * dialog基类
 * Created by huangYx on 2017/10/26.
 */
public abstract class BaseDialog extends DialogFragment {
    private static final String KEY_REBUILD = "rebuild";

    private FrameLayout mRootLayout;
    private DialogInterface.OnDismissListener mDismissListener;

    public BaseDialog() {
    }

    @Override
    final public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(R.style.base_dialog_style, 0);
    }

    @Override
    final public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    final public void onStart() {
        try {
            // fixed: android.os.TransactionTooLargeException
            super.onStart();
            if (getActivity() != null && getActivity().isFinishing()) {
                return;
            }
            startDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startDialog() {
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int animation = getAnimations();
            if (animation > 0) {
                window.setWindowAnimations(animation); // 添加动画
            }
            window.setGravity(getGravity());
        }
    }

    protected int getGravity() {
        return Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
    }

    protected int getAnimations() {
        return R.style.base_bottom_dialog_anim_style;
    }

    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootLayout = new FrameLayout(getActivity());
        inflater.inflate(getLayout(), mRootLayout, true);
        boolean isRebuild = false;
        if (savedInstanceState != null) {
            isRebuild = savedInstanceState.getBoolean(KEY_REBUILD, false);
        }
        if (!isRebuild) {
            initView(savedInstanceState == null ? getArguments() : savedInstanceState);
        }
        return mRootLayout;
    }

    final public View findViewById(int id) {
        return mRootLayout.findViewById(id);
    }

    abstract protected int getLayout();

    abstract protected void initView(Bundle savedInstanceState);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_REBUILD, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(KEY_REBUILD, false)) {
                dismiss();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRootLayout != null) {
            mRootLayout.removeAllViews();
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.mDismissListener = dismissListener;
    }

    public void show(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            show(activity.getFragmentManager());
        }
    }

    public void show(FragmentManager ft) {
        if (isShowing()) {
            return;
        }
        try {
            Fragment f = ft.findFragmentByTag(this.getClass().getName());
            if (f != null && f.isAdded()) {
                ft.beginTransaction().remove(f).commit();
            }
            super.show(ft, this.getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        if (Build.VERSION.SDK_INT > 11) {
            dismissAllowingStateLoss();
        } else {
            try {
                super.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mRootLayout = null;
        mDismissListener = null;
    }
}
