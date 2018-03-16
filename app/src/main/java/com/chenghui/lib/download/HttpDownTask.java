package com.chenghui.lib.download;

import android.os.AsyncTask;

/**
 * @author xygy
 * @version 2016-4-6 下午2:44:34
 *          类说明:
 */
public class HttpDownTask extends AsyncTask<EntityParams, Object, Void> {


    private int type; // 1:downloadStringByGet  2:downloadStringByPost  3:downloadFile
    private Listener listener;

    public HttpDownTask(int type, Listener listener) {
        this.type = type;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(EntityParams... params) {

        if (params != null && params.length > 0 && params[0] != null) {
            if (type == 1) {
                downloadStringByGet(params[0]);
            } else if (type == 2) {
                downloadStringByPost(params[0]);
            } else if (type == 3) {
                dowmloadFile(params[0]);
            }
        }

        return null;
    }

    /**
     * 进度回调
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        if (listener != null && values != null && values.length > 0) {

            // 处理错误信息
            if (values[0] instanceof String && ((String) values[0]).startsWith("error")) {
                listener.onDownError((String) values[0]);
                return;
            }

            // 处理正确结果回调
            if (listener instanceof DownFileListener && values[0] instanceof EntityFile) {
                EntityFile file = (EntityFile) values[0];
                ((DownFileListener) listener).onDownProgress(file);

                if (file.progeress >= 100) {
                    ((DownFileListener) listener).onDownSuccess(file);
                }
            } else if (listener instanceof DownStringListener && values[0] instanceof String) {
                ((DownStringListener) listener).onDownSuccess((String) values[0]);
            }
        }
    }

    private void downloadStringByGet(EntityParams params) {
        try {
            HttpDownloader downloader = new HttpDownloader();
            String str = downloader.download(params.url);
            publishProgress(str);
        } catch (Exception e) {
            publishProgress(e.getMessage());
        }
    }

    private void downloadStringByPost(EntityParams params) {
        try {
            String str = HttpUtils.submitPostData(params.url, params.params, params.encode);
            publishProgress(str);
        } catch (Exception e) {
            publishProgress(e.getMessage());
        }
    }

    private void dowmloadFile(EntityParams params) {
        try {
            HttpDownloader downloader = new HttpDownloader();
            downloader.downFile(this, params);
        } catch (Exception e) {
            publishProgress("error: " + e.getMessage());
        }
    }

}
