package com.chenghui.lib.download;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 传递参数
 * Created by cdsunqinwei on 2018/3/16.
 */

public class EntityParams implements Serializable {

    public int type; // 下载类型  // 1:downloadStringByGet  2:downloadStringByPost  3:downloadFile
    public Listener listener; // 下载监听
    public String url; // 下载地址
    public HashMap<String, String> params; // post 上传参数
    public String encode; // 编码方式
    public String path; // 保存的文件路径
    public String fileName; // 保存的文件名称
    public String name; // 显示在通知栏的文件名称
    public boolean isProgress; // 是否显示通知
    public boolean isDelete; // 是否删除原有文件  true:先删除再下载  false:如果下载大小与现在大小一致则不删除
    public int iconId; // 通知栏icon 图标  R.drawble.icon

    // get方式下载String
    public EntityParams(Listener listener, String url) {
        this.type = 1;
        this.listener = listener;
        this.url = url;
    }

    // post 方式下载String
    public EntityParams(Listener listener, String url, HashMap<String, String> params, String encode) {
        this.type = 2;
        this.listener = listener;
        this.url = url;
        this.params = params;
        this.encode = encode;
    }

    // 下载文件
    public EntityParams(Listener listener, String url, String path, String fileName, String name, boolean isProgress, boolean isDelete, int iconId) {
        this.type = 3;
        this.listener = listener;
        this.url = url;
        this.fileName = fileName;
        this.name = name;
        this.isProgress = isProgress;
        this.isDelete = isDelete;
        this.iconId = iconId;
        this.path = path;
    }
}
