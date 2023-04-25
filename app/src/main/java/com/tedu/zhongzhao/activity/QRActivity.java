package com.tedu.zhongzhao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.qr.QRCaptureManager;
import com.tedu.zhongzhao.ui.PageInfo;
import com.tedu.zhongzhao.ui.UiUtil;
import com.tedu.zhongzhao.utils.TempIntentData;

/**
 * 扫码的Activity
 * Created by huangyx on 2018/3/28.
 */
public class QRActivity extends BaseActivity {

    private QRCaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_capture_layout);
        PageInfo pageInfo = (PageInfo) TempIntentData.getData(getPageInfoKey());
        if (pageInfo != null) {
            setExitAni(pageInfo.getAct());
            UiUtil.initTitleBar(mTitleBarView, pageInfo);
        } else {
            mTitleBarView.setTitle("扫一扫");
            mTitleBarView.setLeftBtn(R.mipmap.icon_back, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capture.onDestroy();
                }
            });
        }
        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.dbv_custom);
        capture = new QRCaptureManager(this, barcodeScannerView, mCallbackId);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected boolean doBackPressed() {
        Intent intent = new Intent(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.TIMEOUT, true);
        intent.putExtra(BaseActivity.KEY_CALLBACK_ID, mCallbackId);
        setResult(Activity.RESULT_CANCELED, intent);
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


}
