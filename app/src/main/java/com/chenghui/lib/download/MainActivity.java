package com.chenghui.lib.download;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "http://openbox.mobilem.360.cn/index/d/sid/3661956";
                DownLoadUtils.getInstance(getApplicationContext()).downloadFile(url, "Demo.apk", true, true, "Demo模块", R.drawable.ic_launcher, new DownFileListener() {
                    @Override
                    public void onDownSuccess(EntityFile file) {
                        ToastUtils.showLong("下载成功");
                    }

                    @Override
                    public void onDownProgress(EntityFile progress) {
                        ToastUtils.showLong("下载进度：" + progress.progeress);
                    }

                    @Override
                    public void onDownError(String msg) {
                        ToastUtils.showLong(msg);
                    }
                });
            }
        });
    }
}
