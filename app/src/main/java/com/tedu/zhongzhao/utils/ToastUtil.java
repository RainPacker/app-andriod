package com.tedu.zhongzhao.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.tedu.base.util.AndroidUtils;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.WorkApplication;

/**
 * Toast工具
 */
public class ToastUtil {

    private static Toast sToast;
    private static TextView mToastTxtView;
    private static Handler sHandler;
    private static String sMsg;
    private static boolean showLong;

    static {
        sHandler = new Handler(Looper.getMainLooper());
    }

    public static void show(int resId) {
        try {
            show(WorkApplication.getApplication().getResources().getString(resId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void show(String tipMsg) {
        show(tipMsg, false);
    }

    private static void show(String tipMsg, boolean isShowLong) {
        sMsg = tipMsg;
        showLong = isShowLong;
        sHandler.post(showRunnable);
    }

    private static Runnable showRunnable = new Runnable() {
        @Override
        public void run() {
            if (sToast == null) {
                sToast = new Toast(WorkApplication.getApplication());
                mToastTxtView = (TextView) LayoutInflater.from(WorkApplication.getApplication()).inflate(R.layout.wgt_toast_layout, null);
                mToastTxtView.setMaxWidth(Math.round(AndroidUtils.getScreenSize().first * 0.8f));
                sToast.setView(mToastTxtView);
                sToast.setGravity(Gravity.CENTER, 0, 0);
            }
            mToastTxtView.setText(sMsg);
            sToast.setDuration(showLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            try {
                sToast.show();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    };

    public static void cancel() {
        if (sToast != null) {
            sToast.cancel();
        }
    }

}
