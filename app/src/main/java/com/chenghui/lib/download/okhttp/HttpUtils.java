package com.chenghui.lib.download.okhttp;

import android.content.Context;
import android.content.Intent;

import com.chenghui.lib.download.HttpDownService;
import com.chenghui.lib.download.entity.EntityFile;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by cdsunqinwei on 2018/3/15.
 */

public class HttpUtils {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";

    public static final String FILE_TYPE_FILE = "file/*";
    public static final String FILE_TYPE_IMAGE = "image/*";
    public static final String FILE_TYPE_AUDIO = "audio/*";
    public static final String FILE_TYPE_VIDEO = "video/*";

    /**
     * 标志是否下载完成
     */
    public static boolean isDownload = false;

    private HttpUtils(Context context) {

    }

    /**
     * 通过 get 方式获取 String
     *
     * @param url 网络地址
     * @return 下载的String文件
     */
    public static void requestGet(String url, CallBackUtil callBack) {
        requestGet(url, null, null, callBack);
    }

    public static void requestGet(String url, Object tag, CallBackUtil callBack) {
        requestGet(url, tag, null, callBack);
    }

    public static void requestGet(String url, Map<String, String> params, CallBackUtil callBack) {
        requestGet(url, null, params, callBack);
    }

    public static void requestGet(String url, Object tag, Map<String, String> params, CallBackUtil callBack) {
        requestGet(url, tag, params, null, callBack);
    }

    public static void requestGet(String url, Object tag, Map<String, String> params, Map<String, String> headerMap, CallBackUtil callBack) {
        new RequestUtil(METHOD_GET, tag, url, params, headerMap, callBack).execute();
    }

    /**
     * 通过 Post 方式 获取String
     *
     * @param url    网络地址
     * @param params 参数
     * @return 下载的String文件
     */
    public static void requestPost(String url, Map<String, String> params, CallBackUtil callBack) {
        requestPost(url, null, params, null, callBack);
    }

    public static void requestPost(String url, Object tag, Map<String, String> params, CallBackUtil callBack) {
        requestPost(url, tag, params, null, callBack);
    }

    public static void requestPost(String url, Map<String, String> params, Map<String, String> headerMap, CallBackUtil callBack) {
        requestPost(url, null, params, headerMap, callBack);
    }

    public static void requestPost(String url, Object tag, Map<String, String> paramsMap, Map<String, String> headerMap, CallBackUtil callBack) {
        new RequestUtil(METHOD_POST, tag, url, paramsMap, headerMap, callBack).execute();
    }

    /**
     * post请求，可以传递参数
     *
     * @param url：url
     * @param jsonStr：json格式的键值对参数                                                  // @param headerMap：map集合，封装请求头键值对
     * @param callBack：回调接口，onFailure方法在请求失败时调用，onResponse方法在请求成功后调用，这两个方法都执行在UI线程。
     */
    public static void requestPostJson(String url, String jsonStr, CallBackUtil callBack) {
        requestPostJson(url, null, jsonStr, null, callBack);
    }

    public static void requestPostJson(String url, Object tag, String jsonStr, Map<String, String> headerMap, CallBackUtil callBack) {
        new RequestUtil(METHOD_POST, tag, url, jsonStr, headerMap, callBack).execute();
    }

    /**
     * 下载文件
     *
     * @param urlStr       下载网址
     * @param callBackFile 文件回调
     */
    public static void downloadFile(String urlStr, CallBackUtil.CallBackFile callBackFile) {
        downloadFile(urlStr, null, callBackFile);
    }

    public static void downloadFile(String url, Map<String, String> paramsMap, CallBackUtil.CallBackFile callBack) {
        downloadFile(url, null, paramsMap, null, callBack);
    }

    public static void downloadFile(String url, Object tag, Map<String, String> paramsMap, CallBackUtil.CallBackFile callBack) {
        requestGet(url, paramsMap, null, callBack);
    }

    public static void downloadFile(String url, Object tag, Map<String, String> paramsMap, Map<String, String> headerMap, CallBackUtil.CallBackFile callBack) {
        HttpUtils.isDownload = true;
        requestPost(url, tag, paramsMap, headerMap, callBack);
    }

    /**
     * 下载文件并展示通知
     *
     * @param urlStr       下载地址
     * @param name         通知展示名称
     * @param icon         通知展示图标
     * @param callBackFile 回调
     */
    public static void downloadFileNotify(Context context, String urlStr, String name, int icon, final CallBackUtil.CallBackFile callBackFile) {
        downloadFileNotify(context, urlStr, name, icon, null, null, null, callBackFile);
    }

    public static void downloadFileNotify(Context context, String urlStr, String name, int icon, Object tag, Map<String, String> paramsMap, Map<String, String> headerMap, final CallBackUtil.CallBackFile callBackFile) {

        final EntityFile progress = new EntityFile();
        int id = (int) (Math.random() * 10000);
        progress.id = id;
        progress.name = name;
        progress.fileName = callBackFile.mDestFileDir + "/" + callBackFile.mdestFileName;
        //progress.length = -1;
        progress.progeress = 0;
        progress.downLength = 0;
        progress.iconId = icon;

        final CallBackUtil.CallBackFile c = new CallBackUtil.CallBackFile(callBackFile.mDestFileDir, callBackFile.mdestFileName) {
            @Override
            public void onFailure(Call call, Exception e) {
                callBackFile.onFailure(call, e);
            }

            @Override
            public void onProgress(int downProgress, long total) {
                callBackFile.onProgress(downProgress, total);

                progress.length = total;
                progress.progeress = downProgress >= 100 ? 99 : downProgress;
                EventBus.getDefault().post(progress);
            }

            @Override
            public void onResponse(File response) {
                progress.progeress = 100;
                EventBus.getDefault().post(progress);

                callBackFile.onResponse(response);
            }
        };

        startDownService(context);
        downloadFile(urlStr, tag, paramsMap, headerMap, c);
    }

    /**
     * post请求，上传单个文件
     *
     * @param url：url
     * @param file：File对象
     * @param fileKey：上传参数时file对应的键
     * @param fileType：File类型，是image，video，audio，file
     * @param callBack：回调接口，onFailure方法在请求失败时调用，onResponse方法在请求成功后调用，这两个方法都执行在UI线程。还可以重写onProgress方法，得到上传进度
     */
    public void uploadFile(String url, File file, String fileKey, String fileType, CallBackUtil callBack) {
        uploadFile(url, file, fileKey, fileType, null, callBack);
    }

    /**
     * post请求，上传单个文件
     *
     * @param url：url
     * @param file：File对象
     * @param fileKey：上传参数时file对应的键
     * @param fileType：File类型，是image，video，audio，file
     * @param paramsMap：map集合，封装键值对参数
     * @param callBack：回调接口，onFailure方法在请求失败时调用，onResponse方法在请求成功后调用，这两个方法都执行在UI线程。还可以重写onProgress方法，得到上传进度
     */
    public void uploadFile(String url, File file, String fileKey, String fileType, Map<String, String> paramsMap, CallBackUtil callBack) {
        uploadFile(url, null, file, fileKey, fileType, paramsMap, null, callBack);
    }

    /**
     * post请求，上传单个文件
     *
     * @param url：url
     * @param file：File对象
     * @param fileKey：上传参数时file对应的键
     * @param fileType：File类型，是image，video，audio，file
     * @param paramsMap：map集合，封装键值对参数
     * @param headerMap：map集合，封装请求头键值对
     * @param callBack：回调接口，onFailure方法在请求失败时调用，onResponse方法在请求成功后调用，这两个方法都执行在UI线程。还可以重写onProgress方法，得到上传进度
     */
    public void uploadFile(String url, Object tag, File file, String fileKey, String fileType, Map<String, String> paramsMap, Map<String, String> headerMap, CallBackUtil callBack) {
        new RequestUtil(METHOD_POST, tag, url, paramsMap, file, fileKey, fileType, headerMap, callBack).execute();
    }

    /**
     * post请求，上传多个文件，以list集合的形式
     *
     * @param url：url
     * @param fileList：集合元素是File对象
     * @param fileKey：上传参数时fileList对应的键
     * @param fileType：File类型，是image，video，audio，file
     * @param callBack：回调接口，onFailure方法在请求失败时调用，onResponse方法在请求成功后调用，这两个方法都执行在UI线程。
     */
    public static void uploadListFile(String url, List<File> fileList, String fileKey, String fileType, CallBackUtil callBack) {
        uploadListFile(url, null, fileList, fileKey, fileType, callBack);
    }

    /**
     * post请求，上传多个文件，以list集合的形式
     *
     * @param url：url
     * @param fileList：集合元素是File对象
     * @param fileKey：上传参数时fileList对应的键
     * @param fileType：File类型，是image，video，audio，file
     * @param paramsMap：map集合，封装键值对参数
     * @param callBack：回调接口，onFailure方法在请求失败时调用，onResponse方法在请求成功后调用，这两个方法都执行在UI线程。
     */
    public static void uploadListFile(String url, Map<String, String> paramsMap, List<File> fileList, String fileKey, String fileType, CallBackUtil callBack) {
        uploadListFile(url, null, paramsMap, fileList, fileKey, fileType, null, callBack);
    }

    /**
     * post请求，上传多个文件，以list集合的形式
     *
     * @param url：url
     * @param fileList：集合元素是File对象
     * @param fileKey：上传参数时fileList对应的键
     * @param fileType：File类型，是image，video，audio，file
     * @param paramsMap：map集合，封装键值对参数
     * @param headerMap：map集合，封装请求头键值对
     * @param callBack：回调接口，onFailure方法在请求失败时调用，onResponse方法在请求成功后调用，这两个方法都执行在UI线程。
     */
    public static void uploadListFile(String url, Object tag, Map<String, String> paramsMap, List<File> fileList, String fileKey, String fileType, Map<String, String> headerMap, CallBackUtil callBack) {
        new RequestUtil(METHOD_POST, tag, url, paramsMap, fileList, fileKey, fileType, headerMap, callBack).execute();
    }

    /**
     * post请求，上传多个文件，以map集合的形式
     *
     * @param url：url
     * @param fileMap：集合key是File对象对应的键，集合value是File对象
     * @param fileType：File类型，是image，video，audio，file
     * @param callBack：回调接口，onFailure方法在请求失败时调用，onResponse方法在请求成功后调用，这两个方法都执行在UI线程。
     */
    public static void uploadMapFile(String url, Map<String, File> fileMap, String fileType, CallBackUtil callBack) {
        uploadMapFile(url, fileMap, fileType, null, callBack);
    }

    /**
     * post请求，上传多个文件，以map集合的形式
     *
     * @param url：url
     * @param fileMap：集合key是File对象对应的键，集合value是File对象
     * @param fileType：File类型，是image，video，audio，file
     * @param paramsMap：map集合，封装键值对参数
     * @param callBack：回调接口，onFailure方法在请求失败时调用，onResponse方法在请求成功后调用，这两个方法都执行在UI线程。
     */
    public static void uploadMapFile(String url, Map<String, File> fileMap, String fileType, Map<String, String> paramsMap, CallBackUtil callBack) {
        uploadMapFile(url, null, fileMap, fileType, paramsMap, null, callBack);
    }

    /**
     * post请求，上传多个文件，以map集合的形式
     *
     * @param url：url
     * @param fileMap：集合key是File对象对应的键，集合value是File对象
     * @param fileType：File类型，是image，video，audio，file
     * @param paramsMap：map集合，封装键值对参数
     * @param headerMap：map集合，封装请求头键值对
     * @param callBack：回调接口，onFailure方法在请求失败时调用，onResponse方法在请求成功后调用，这两个方法都执行在UI线程。
     */
    public static void uploadMapFile(String url, Object tag, Map<String, File> fileMap, String fileType, Map<String, String> paramsMap, Map<String, String> headerMap, CallBackUtil callBack) {
        new RequestUtil(METHOD_POST, tag, url, paramsMap, fileMap, fileType, headerMap, callBack).execute();
    }


    /**
     * 是否有文件正在下载
     *
     * @return
     */
    public static boolean isDownloading() {
        return isDownload;
    }

    private static void startDownService(Context context) {
        Intent intent = new Intent(context, HttpDownService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    public void cancle(Object tag) {
        OkHttpClientUtil.cancle(tag);
    }

}
