package com.tedu.zhongzhao.dialog;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.widget.MaterialProgressDrawable;


/**
 * Loading对话框，已集成至Activity的基类中，可直接在Activity中使用，禁止任何人实例化使用该类
 * Created by huangyx on 2016.2.25.
 */
public class LoadingDialog extends DialogFragment {

    private String TAG = LoadingDialog.class.getSimpleName();
    private static final String KEY_REBUILD = "rebuild";

    private LoadingDialogListener mListener;
    private MaterialProgressDrawable mDrawable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(R.style.base_dialog_style, 0);
    }

    public static LoadingDialog newInstance() {
        return new LoadingDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dlg_loading_layout, container, false);
        ImageView iv = (ImageView) root.findViewById(R.id.dlg_loading_iv);
        mDrawable = new MaterialProgressDrawable(getActivity(), iv);
        iv.setImageDrawable(mDrawable);
        //背景
        mDrawable.setBackgroundColor(0xFFF1F1F1);
        //颜色
        int[] colors = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF};
        mDrawable.setColorSchemeColors(colors);
        //旋转角度，0-1
        mDrawable.setProgressRotation(0f);
        //圆环范围，0-1
        mDrawable.setStartEndTrim(0f, 1f);
        //箭头大小，0-1
        mDrawable.setArrowScale(0f);
        //透明度，0-255
        mDrawable.setAlpha(120);
        mDrawable.start();
        return root;
    }

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

    @Override
    public void dismiss() {
        if (mDrawable != null) {
            mDrawable.stop();
        }
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

    public void show(Activity activity) {
        if (isShowing() || activity == null || activity.isFinishing()) {
            return;
        }
        try {
            FragmentManager fm = activity.getFragmentManager();
//            Fragment f = fm.findFragmentByTag(TAG);
//            if (f != null) {
            fm.beginTransaction().remove(this).commit();
//            }
            super.show(fm, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListener(LoadingDialogListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener = null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mListener != null) {
            mListener.onCancel();
        }
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    @Override
    public void onStart() {
        if (getActivity() != null && getActivity().isFinishing()) {
            return;
        }
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.dimAmount = 0f;
            window.setAttributes(wlp);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface LoadingDialogListener {
        void onCancel();
    }
}
