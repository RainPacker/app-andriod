package com.tedu.zhongzhao.dialog;

import android.os.Bundle;
import android.view.View;

import com.tedu.zhongzhao.R;

/**
 * 选择照片对话框
 * Created by huangyx on 2018/5/9.
 */
public class SelectPicDialog extends BaseDialog implements View.OnClickListener {

    public static SelectPicDialog newInstance(int reqCode, SelectPicDialogListener listener) {
        Bundle bundle = new Bundle();
        bundle.putInt("reqCode", reqCode);
        SelectPicDialog dialog = new SelectPicDialog();
        dialog.setArguments(bundle);
        dialog.setListener(listener);
        return dialog;
    }

    private int reqCode;

    private SelectPicDialogListener mListener;

    private void setListener(SelectPicDialogListener listener) {
        this.mListener = listener;
    }


    @Override
    protected int getLayout() {
        return R.layout.dlg_select_pic_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        reqCode = savedInstanceState.getInt("reqCode");
        findViewById(R.id.take_photo_view).setOnClickListener(this);
        findViewById(R.id.take_take_video).setOnClickListener(this);
        findViewById(R.id.take_cancel_view).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo_view:
                if (mListener != null) {
                    mListener.toTakePhoto(reqCode);
                }
                break;
            case R.id.take_take_video:
                if (mListener != null) {
                    mListener.toTakeVideo(reqCode);
                }
                break;
            case R.id.take_cancel_view:
                break;
        }
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener = null;
    }

    public interface SelectPicDialogListener {

        /**
         * 进入相机
         *
         * @param reqCode 请求码
         */
        void toTakePhoto(int reqCode);

        /**
         * 进入录像
         *
         * @param reqCode 请求码
         */
        void toTakeVideo(int reqCode);
    }
}
