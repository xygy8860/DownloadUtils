package com.chenghui.lib.download;

/**
 * Created by cdsunqinwei on 2018/3/15.
 */

public class EntityFile {
    public int id; // 通知ID
    public String name; // 下载显示名称
    public String fileName; // 保存的文件名称
    public long length; // 总大小
    public long downLength; // 已下载大小
    public int progeress; // 已下载百分比
    public int iconId; // R.drawable.ic_launcher
}
