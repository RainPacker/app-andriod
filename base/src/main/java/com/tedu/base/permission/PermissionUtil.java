package com.tedu.base.permission;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.PermissionChecker;

import com.tedu.base.util.AndroidUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by huangyx on 2018/8/15.
 */
public class PermissionUtil {


    /**
     * 判断是否缺少权限
     *
     * @param context    Context
     * @param permission 权限
     * @return true/false
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AndroidUtils.getTargetSDKVersion() >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can use Context#checkSelfPermission
                result = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;
            }
        }
        return result;
    }

    /**
     * 检测是否授权
     *
     * @param context     Context
     * @param permissions 权限列表
     * @return 已授权的下标
     */
    public static int[] isPermissionGranted(Context context, String[] permissions) {
        int[] result = null;
        if (permissions != null) {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (isPermissionGranted(context, permissions[i])) {
                    list.add(i);
                }
            }
            if (!list.isEmpty()) {
                result = new int[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    result[i] = list.get(i);
                }
            }
        }
        return result;
    }

    /**
     * Miui 权限设置界面
     *
     * @param context Context
     */
    private static boolean gotoMiuiPermission(Context context) {
        boolean result = false;
        try {
            Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
            ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            i.setComponent(componentName);
            i.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(i);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        if (!result) {
            try {
                Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
                ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                i.setComponent(componentName);
                i.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(i);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }

    /**
     * 跳转魅族权限设置
     */
    private static boolean gotoMeizuPermission(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", context.getPackageName());
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 华为权限设置
     *
     * @return true/false
     */
    private static boolean gotoHuaweiPermission(Context context) {
        try {
            Intent intent = new Intent(context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 跳转至权限设置页面
     *
     * @param context Context
     */
    public static void toSettingPermission(Context context) {
        String brand = Build.BRAND;//手机厂商
        boolean result = false;
        if ("redmi".equalsIgnoreCase(brand) || "xiaomi".equalsIgnoreCase(brand)) {
            result = gotoMiuiPermission(context);//小米
        } else if ("meizu".equalsIgnoreCase(brand)) {
            result = gotoMeizuPermission(context);
        } else if ("huawei".equalsIgnoreCase(brand) || "honor".equalsIgnoreCase(brand)) {
            result = gotoHuaweiPermission(context);
        }
        if (!result) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            context.startActivity(intent);
        }
    }

}
