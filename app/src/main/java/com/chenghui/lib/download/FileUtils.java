package com.chenghui.lib.download;

import android.os.Environment;
import android.text.TextUtils;

import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileUtils {
    protected static String SDCardRoot;
    protected static String FILE_DIRS_NAME = "download";

    public FileUtils() {
        if (TextUtils.isEmpty(SDCardRoot)) {
            // 如果Sd卡可用 存储在SD卡
            if (SDCardUtils.isSDCardEnable()) {
                // 得到当前外部存储设备的目录
                SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            } else {
                SDCardRoot = Utils.getApp().getCacheDir().getPath();
            }
        }
    }

    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    private File createFileInSDCard(String fileName, String dir)
            throws IOException {
        File file = new File(SDCardRoot + dir + File.separator + fileName);
        System.out.println("file---->" + file);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     */
    private File creatSDDir(String dir) {
        File dirFile = new File(SDCardRoot + dir + File.separator);
        System.out.println(dirFile.mkdirs());
        return dirFile;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileName     文件名称
     * @param path         存储路径
     * @param urlStr       下载的url
     * @param isDeleteFile 是否删除文件
     * @return
     */
    @SuppressWarnings("resource")
    protected boolean isFileExist(String fileName, String path, String urlStr, boolean isDeleteFile) {
        boolean flag = false;
        File file = new File(SDCardRoot + path + File.separator + fileName);
        if (file.exists()) {
            //如果文件存在则判断文件大小
            try {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                int size = fis.available(); // 文件长度
                fis.close();//必须先关闭才能删除
                if (isDeleteFile) {
                    file.delete();//删除文件重新下载
                    flag = false;
                } else {
                    //如果文件大小为0，重新下载
                    if (size == 0) {
                        file.delete();//删除文件重新下载
                        flag = false;
                    }
                    //如果不为0，则比较已存在文件大小与下载文件大小，若不一致，也要重新下载
                    else {
                        URLConnection connection = null;
                        URL url = new URL(urlStr);
                        connection = url.openConnection();
                        int fileLength = connection.getContentLength();
                        if (size != fileLength) {
                            file.delete();
                            flag = false;
                        } else {
                            flag = true;
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return flag;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @throws IOException
     */
    protected File write2SDFromInput(HttpDownTask httpDownTask, EntityParams params, InputStream input) throws Exception {

        File file = null;
        OutputStream output = null;
        URLConnection connection = null;

        URL url;
        try {
            url = new URL(params.url);
            connection = url.openConnection();
            creatSDDir(params.path);
            file = createFileInSDCard(params.fileName, params.path);
            output = new FileOutputStream(file);
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.connect();

            //connection.getContentLength()有时返回-1
            long fileLength = connection.getContentLength();
            long downloadFileLength = 0; // 已下载长度
            byte buffer[] = new byte[4 * 1024];

            EntityFile progress = new EntityFile();
            int id = (int) (Math.random() * 100);
            if (params.isProgress && fileLength != -1) {
                progress.id = id;
                progress.name = params.name;
                progress.fileName = params.fileName;
                progress.length = fileLength;
                progress.progeress = 0;
                progress.downLength = 0;
                progress.iconId = params.iconId;
                EventBus.getDefault().post(progress);
            }
            int temp = 0;
            int n = 0;// 下载百分比
            while (downloadFileLength < fileLength || temp != -1) {
                n++;
                temp = input.read(buffer);
                if (temp > 0) {
                    output.write(buffer, 0, temp);//避免写入乱字符
                    DownLoadUtils.isDownload = true;

                    if (params.isProgress && fileLength != -1) {
                        downloadFileLength += temp;
                        int x = (int) (downloadFileLength * 100 / fileLength);
                        //控制更新速度，5%更新一次，不然会频繁更新
                        if (n % 100 == 0 && x < 100) {
                            progress.downLength = downloadFileLength;
                            progress.progeress = x;
                            httpDownTask.onProgressUpdate(progress); // 更新回调进度
                            EventBus.getDefault().post(progress); // 更新通知栏进度
                        }
                    }
                }
            }

            if (params.isProgress) {
                progress.downLength = fileLength;
                progress.progeress = 100;
                httpDownTask.onProgressUpdate(progress); // 更新回调进度
                EventBus.getDefault().post(progress);
            }

            output.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}