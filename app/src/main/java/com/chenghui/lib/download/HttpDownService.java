package com.chenghui.lib.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * 显示下载通知
 *
 * @author xygy
 * @version 2015-12-9 下午2:06:38
 */
public class HttpDownService extends Service {
    private static final String ACTION_BUTTON = "com.notification.intent.action.ButtonClick";
    private NotificationManager manger;
    private NotificationCompat.Builder builder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        manger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*if (intent != null) {
            EntityParams params = (EntityParams) intent.getSerializableExtra("params");
            if (params != null) {
                HttpDownTask task = new HttpDownTask(params.type, params.listener);
                task.execute(params);
            }
        }*/

        return super.onStartCommand(intent, flags, startId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendNotification(EntityFile entity) {
        try {
            if (entity.progeress <= 100) {
                if (builder == null) {
                    builder = new NotificationCompat.Builder(this);
                    builder.setSmallIcon(entity.iconId);
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), entity.iconId));
                    //禁止用户点击删除按钮删除
                    builder.setAutoCancel(true);
                    //禁止滑动删除
                    builder.setOngoing(false);
                    builder.setShowWhen(true);
                    //builder.setChannelId("china");
                }

                builder.setContentTitle(entity.name.replace(".apk", ""));
                builder.setContentText("大小：" + (entity.length / 1024 / 1024) + "M  已下载：" + entity.progeress + "%");
                builder.setProgress(100, entity.progeress, false);

                if (entity.progeress == 100) {
                    //注册广播
                    ButtonBroadcastReceiver receiver = new ButtonBroadcastReceiver();
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(ACTION_BUTTON + entity.id);
                    registerReceiver(receiver, intentFilter);

                    //设置点击的事件
                    Intent buttonIntent = new Intent(ACTION_BUTTON + entity.id);
                    buttonIntent.putExtra("name", entity.fileName);
                    buttonIntent.putExtra("id", entity.id);
                    PendingIntent intent = PendingIntent.getBroadcast(this, 0x11, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(intent);
                }

                Notification notification = builder.build();
                manger.notify(entity.id, notification);
            }
        } catch (Exception e) {

        }
    }

    private void installApk(String name) {
        File file = new File(FileUtils.SDCardRoot + FileUtils.FILE_DIRS_NAME + File.separator + name);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(getUriForFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    /**
     * 兼容Android 7.0
     *
     * @param file
     * @return
     */
    private Uri getUriForFile(File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = FileProvider.getUriForFile(this.getApplicationContext(), this.getPackageName() + ".provider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * （通知栏中的点击事件是通过广播来通知的，所以在需要处理点击事件的地方注册广播即可）
     * 广播监听按钮点击事件
     */
    public class ButtonBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int id = intent.getIntExtra("id", 0);
            if (action.equals(ACTION_BUTTON + id)) {
                //通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
                String name = intent.getStringExtra("name");
                if (!TextUtils.isEmpty(name) && name.contains(".apk")) {
                    installApk(name);
                }
            }

            unregisterReceiver(this);
        }
    }
}
