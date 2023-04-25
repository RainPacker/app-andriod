package com.tedu.base.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tedu.base.R;

/**
 * Created by huangyx on 2018/8/16.
 */
public class PermissionDialog extends DialogFragment {

    private static final String KEY_REBUILD = "rebuild";
    private static final String KEY_PERMISSION = "permission";
    private static final String KEY_FUNCTION = "fuction";

    public static PermissionDialog newInstance(String permission, String function) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PERMISSION, permission);
        bundle.putString(KEY_FUNCTION, function);
        PermissionDialog dialog = new PermissionDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    private DialogInterface.OnDismissListener mDismissListener;

    public PermissionDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, 0);
    }

    @Override
    final public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        boolean isRebuild = false;
        if (savedInstanceState != null) {
            isRebuild = savedInstanceState.getBoolean(KEY_REBUILD, false);
        }
        if (!isRebuild) {
            if (savedInstanceState == null) {
                savedInstanceState = getArguments();
            }
            if (savedInstanceState != null) {
                AlertDialog dialog = (AlertDialog) getDialog();
                dialog.setTitle("申请权限");
                String message = getActivity().getString(R.string.txt_permission_open_des, getActivity().getString(R.string.app_name),
                        savedInstanceState.getString(KEY_PERMISSION, ""),
                        savedInstanceState.getString(KEY_FUNCTION, ""));
                dialog.setMessage(message);
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, getActivity().getString(R.string.txt_to_setting), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtil.toSettingPermission(getActivity());
                        dismiss();
                    }
                });
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getActivity().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
            }
        }
        return null;
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
        try {
            dismissAllowingStateLoss();
        } catch (Exception e) {
            super.dismiss();
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
        mDismissListener = null;
    }
}