package com.chenghui.lib.download;

import java.io.Serializable;

/**
 * 下载监听
 * Created by cdsunqinwei on 2018/3/16.
 */

public interface DownFileListener extends Listener, Serializable {
    void onDownSuccess(EntityFile file); // -1:下载失败 0：下载成功  1：文件已存在

    void onDownProgress(EntityFile progress);
}
