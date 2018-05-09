package com.chenghui.lib.download.okhttp;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

/**
 * Created by cdsunqinwei on 2018/5/9.
 */

public class OkHttpClientUtil {

    private static OkHttpClient mOkHttpClient; //OKhttpClient对象

    private OkHttpClientUtil() {
    }

    /**
     * 获取 OkHttpClient 的单例
     *
     * @return
     */
    public static OkHttpClient getInstance() {
        if (mOkHttpClient == null) {
            synchronized (OkHttpClientUtil.class) {
                if (mOkHttpClient == null) {
                    mOkHttpClient = new OkHttpClient();
                }
            }
        }

        return mOkHttpClient;
    }

    /**
     * 通过 tag 取消网络请求
     *
     * @param tag
     */
    public static void cancle(Object tag) {
        if (mOkHttpClient != null) {
            Dispatcher dispatcher = mOkHttpClient.dispatcher();
            synchronized (dispatcher) {
                for (Call call : dispatcher.queuedCalls()) {
                    if (tag.equals(call.request().tag())) {
                        call.cancel();
                    }
                }
                for (Call call : dispatcher.runningCalls()) {
                    if (tag.equals(call.request().tag())) {
                        call.cancel();
                    }
                }
            }
        }
    }


}
