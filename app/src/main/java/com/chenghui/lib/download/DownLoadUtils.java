package com.chenghui.lib.download;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import java.util.HashMap;

/**
 * Created by cdsunqinwei on 2018/3/15.
 */

public class DownLoadUtils {

    /**
     * 标志是否下载完成
     */
    protected static boolean isDownload = false;

    private static DownLoadUtils instance;
    private Context context;

    private DownLoadUtils(Context context) {
        this.context = context;
        Utils.init(context.getApplicationContext());
    }

    public static DownLoadUtils getInstance(Context context) {
        if (instance == null) {
            instance = new DownLoadUtils(context);
        }
        return instance;
    }

    /**
     * 通过 get 方式获取 String
     *
     * @param url 网络地址
     * @return 下载的String文件
     */
    public void downloadStringByGet(String url, DownFileListener listener) {
        EntityParams params = new EntityParams(listener, url);
        startDownService(params);
    }

    /**
     * 通过 Post 方式 获取String
     *
     * @param url    网络地址
     * @param params 参数
     * @param encode 编码方式
     * @return 下载的String文件
     */
    public void downloadStringByPost(String url, HashMap<String, String> params, String encode, DownFileListener listener) {
        EntityParams entityParams = new EntityParams(listener, url, params, encode);
        startDownService(entityParams);
    }

    /**
     * 下载文件
     *
     * @param urlStr   下载网址
     * @param name     保存文件的名称
     * @param listener 下载监听
     */
    public void downloadFile(String urlStr, String name, DownFileListener listener) {
        downloadFile(urlStr, name, false, false, name, 0, listener);
    }

    public void downloadFile(final String urlStr, final String fileName, final boolean isProgressbar, final boolean isDeleteFile, final String name, final int iconId, final DownFileListener listener) {
        downloadFile(urlStr, FileUtils.FILE_DIRS_NAME, fileName, isProgressbar, isDeleteFile, name, iconId, listener);
    }


    /**
     * 下载文件 该函数返回整形
     *
     * @param urlStr        下载地址
     * @param path          存放路径
     * @param fileName      文件名称
     * @param isProgressbar 是否显示进度通知
     * @param isDeleteFile  是否删除文件
     * @return -2:没有外部存储权限 -1：代表下载文件出错 0：代表下载文件成功 1：代表文件已经存在
     */
    public void downloadFile(final String urlStr, final String path, final String fileName, final boolean isProgressbar, final boolean isDeleteFile, final String name, final int iconId, final DownFileListener listener) {

        DownLoadUtils.isDownload = true;
        // 有权限直接下载
        if (PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EntityParams params = new EntityParams(listener, urlStr, path, fileName, name, isProgressbar, isDeleteFile, iconId);
            startDownService(params);
        } else {
            PermissionUtils.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE).callback(new PermissionUtils.SimpleCallback() {
                @Override
                public void onGranted() {
                    EntityParams params = new EntityParams(listener, urlStr, path, fileName, name, isProgressbar, isDeleteFile, iconId);
                    startDownService(params);
                }

                @Override
                public void onDenied() {
                    DownLoadUtils.isDownload = false;
                    ToastUtils.showLong("您拒绝了权限，下载失败");
                    PermissionUtils.launchAppDetailsSettings();
                }
            }).request();
        }
    }

    /**
     * 是否有文件正在下载
     *
     * @return
     */
    public boolean isDownloading() {
        return isDownload;
    }

    private void startDownService(EntityParams params) {
        HttpDownTask task = new HttpDownTask(params.type, params.listener);
        task.execute(params);

        Intent intent = new Intent(context, HttpDownService.class);
        //intent.putExtra("params", params);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

}
