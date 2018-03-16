package com.chenghui.lib.download;

import java.io.Serializable;

/**
 * 下载string监听
 * Created by cdsunqinwei on 2018/3/16.
 */

public interface DownStringListener extends Listener, Serializable {
    void onDownSuccess(String str); //
}
