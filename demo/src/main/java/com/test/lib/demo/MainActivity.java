package com.test.lib.demo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.chenghui.lib.download.okhttp.CallBackUtil;
import com.chenghui.lib.download.okhttp.HttpUtils;

import java.io.File;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    public static String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

    public static String apk_url = "";
    public static String string_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtils.downloadFileNotify(getApplicationContext(), apk_url, "测试", R.mipmap.ic_launcher, new CallBackUtil.CallBackFile(SDCardRoot, "demo.apk") {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProgress(int progress, long total) {
                        super.onProgress(progress, total);

                        Toast.makeText(getApplicationContext(), "下载进度" + progress, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(File response) {
                        Toast.makeText(getApplicationContext(), "下载完成" + response.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // 下载文件
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HttpUtils.downloadFile(apk_url, new CallBackUtil.CallBackFile(SDCardRoot, "demo.apk") {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProgress(int progress, long total) {
                        super.onProgress(progress, total);

                        Toast.makeText(getApplicationContext(), "下载进度" + progress, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(File response) {
                        Toast.makeText(getApplicationContext(), "下载完成" + response.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // 下载 get
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HttpUtils.requestGet(string_url, new CallBackUtil.CallBackString() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "下载完成" + response, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // 下载 post
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HttpUtils.requestPost(string_url, null, new CallBackUtil.CallBackString() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "下载完成" + response, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
