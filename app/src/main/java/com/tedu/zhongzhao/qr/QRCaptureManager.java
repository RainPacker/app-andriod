package com.tedu.zhongzhao.qr;

import android.app.Activity;
import android.content.Intent;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.tedu.zhongzhao.activity.BaseActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 重写扫码的CaptureManager
 * Created by huangyx on 2018/4/2.
 */
public class QRCaptureManager extends CaptureManager {

    private Activity activity;
    private String callbackId;

    public QRCaptureManager(Activity activity, DecoratedBarcodeView barcodeView) {
        super(activity, barcodeView);
        this.activity = activity;
    }

    public QRCaptureManager(Activity activity, DecoratedBarcodeView barcodeView, String callbackId) {
        super(activity, barcodeView);
        this.callbackId = callbackId;
        this.activity = activity;
    }

    @Override
    protected void returnResult(BarcodeResult rawResult) {
        try {
            Method method = this.getClass().getSuperclass().getDeclaredMethod("getBarcodeImagePath", new Class[]{BarcodeResult.class});
            method.setAccessible(true);
            String imagePath = (String) method.invoke(this, rawResult);
            Intent intent = resultIntent(rawResult, imagePath);
            intent.putExtra(BaseActivity.KEY_CALLBACK_ID, callbackId);
            activity.setResult(Activity.RESULT_OK, intent);
            closeAndFinish();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            activity = null;
        }
    }

    @Override
    protected void returnResultTimeout() {
        Intent intent = new Intent(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.TIMEOUT, true);
        intent.putExtra(BaseActivity.KEY_CALLBACK_ID, callbackId);
        activity.setResult(Activity.RESULT_CANCELED, intent);
        closeAndFinish();
        activity = null;
    }
}
