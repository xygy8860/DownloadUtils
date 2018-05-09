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
	        compile 'com.github.xygy8860:DownloadUtils:1.6'
	}
````


2.在代码中使用
--------

1.普通Get
-------

````
HttpUtils.requestGet(url, new CallBackUtil.CallBackString() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "下载完成" + response, Toast.LENGTH_LONG).show();
                    }
                });
````


2.普通 Post
---

````
HttpUtils.requestPost(string_url, null, new CallBackUtil.CallBackString() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "下载完成" + response, Toast.LENGTH_LONG).show();
                    }
                });
````


3.下载文件
------

````
HttpUtils.downloadFile(apk_url, new CallBackUtil.CallBackFile(SDCardRoot, "demo.apk") {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProgress(int progress, long total) {
                        super.onProgress(progress, total);

                        Toast.makeText(getApplicationContext(), "下载进度" + progress, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(File response) {
                        Toast.makeText(getApplicationContext(), "下载完成" + response.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }
                });
````


4.下载文件并展示通知栏进度
--------------

````
HttpUtils.downloadFileNotify(context, url, "通知栏显示名称", R.mipmap.ic_launcher, new CallBackUtil.CallBackFile(SDCardRoot, "demo.apk") {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProgress(int progress, long total) {
                        super.onProgress(progress, total);

                        Toast.makeText(getApplicationContext(), "下载进度" + progress, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(File response) {
                        Toast.makeText(getApplicationContext(), "下载完成" + response.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }
                });
````
