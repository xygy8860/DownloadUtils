独立下载模块
======

独立模块，下载文件和String请求。接入简单。

使用步骤
====

1.gradle
----

Step 1. Add the JitPack repository to your build file
````
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
````

Step 2. Add the dependency

````
	dependencies {
	        compile 'com.github.xygy8860:DownloadUtils:1.3'
	}
````


2.Java文件
--------

DownLoadUtils.getInstance(context).downloadFile(url, fileName, true, false, name, R.mipmap.ic_launcher, new DownFileListener() {
            @Override
            public void onDownSuccess(EntityFile file) {
                ToastUtils.showLong("下载成功");
            }

            @Override
            public void onDownProgress(EntityFile progress) {
                ToastUtils.showLong("下载进度：" + progress.progeress);
            }

            @Override
            public void onDownError(String msg) {
                ToastUtils.showLong(msg);
            }
        });

至此，已经完成集成了。