package com.chenghui.lib.download;

import java.io.Serializable;

/**
 * Created by cdsunqinwei on 2018/3/16.
 */

public interface Listener extends Serializable {
    void onDownError(String msg); // 下载出错
}
