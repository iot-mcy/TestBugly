package com.tcloudit.bugly;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.bugly.beta.Beta;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    private TextView tvCurrentVersion;
    private Button btnShowToast;
    private Button btnLoadPatch;
    private Button btnKillSelf;
    private Button btnLoadLibrary;
    private Button btnDownloadPatch;
    private Button btnUserPatch;
    private Button btnCheckUpgrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);

        textView.setText("好牛逼呀！");

        tvCurrentVersion = findViewById(R.id.tvCurrentVersion);
        btnShowToast = findViewById(R.id.btnShowToast);
        btnShowToast.setOnClickListener(this);
        btnKillSelf = findViewById(R.id.btnKillSelf);
        btnKillSelf.setOnClickListener(this);
        btnLoadPatch = findViewById(R.id.btnLoadPatch);
        btnLoadPatch.setOnClickListener(this);
        btnLoadLibrary = findViewById(R.id.btnLoadLibrary);
        btnLoadLibrary.setOnClickListener(this);
        btnDownloadPatch = findViewById(R.id.btnDownloadPatch);
        btnDownloadPatch.setOnClickListener(this);
        btnUserPatch = findViewById(R.id.btnPatchDownloaded);
        btnUserPatch.setOnClickListener(this);
        btnCheckUpgrade = findViewById(R.id.btnCheckUpgrade);
        btnCheckUpgrade.setOnClickListener(this);

        tvCurrentVersion.setText("当前版本：" + getCurrentVersion(this));

        rxPermissionTest();

    }

    private void rxPermissionTest() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean granted) throws Exception {
                if (granted) {
                    // TODO: 2018/12/5  
                } else {
                    // 权限被拒绝
                }
            }
        });
    }

    /**
     * 根据应用patch包前后来测试是否应用patch包成功.
     * <p>
     * 应用patch包前，提示"This is a bug class"
     * 应用patch包之后，提示"The bug has fixed"
     */
    public void testToast() {
        Toast.makeText(this, LoadBugClass.getBugString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnShowToast:  // 测试热更新功能
                testToast();
                break;
            case R.id.btnKillSelf: // 杀死进程
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.btnLoadPatch: // 本地加载补丁测试
                Beta.applyTinkerPatch(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
                break;
            case R.id.btnLoadLibrary: // 本地加载so库测试
//                TestJNI testJNI = new TestJNI();
//                testJNI.createANativeCrash();
                break;
            case R.id.btnDownloadPatch://手动下载补丁包
                Beta.downloadPatch();
                break;
            case R.id.btnPatchDownloaded://手动合成补丁
                Beta.applyDownloadedPatch();
                break;
            case R.id.btnCheckUpgrade://检查更新
                Beta.checkUpgrade();
                break;
        }
    }

    /**
     * 获取当前版本.
     *
     * @param context 上下文对象
     * @return 返回当前版本
     */
    public String getCurrentVersion(Context context) {
        try {
            PackageInfo packageInfo =
                    context.getPackageManager().getPackageInfo(this.getPackageName(),
                            PackageManager.GET_CONFIGURATIONS);
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;

            return versionName + "." + versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("MainActivity", "onBackPressed");

        Beta.unInit();
    }
}
