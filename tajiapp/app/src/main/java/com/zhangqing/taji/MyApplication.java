package com.zhangqing.taji;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.media.MediaService;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.taobao.tae.sdk.callback.InitResultCallback;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.socialize.PlatformConfig;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.database.DatabaseManager;

import java.io.File;
import java.lang.ref.WeakReference;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Application
 */
public class MyApplication extends Application {
    private static RequestQueue requestQueue;

    public static MediaService mediaService;

    public static boolean rcHasConnect = false;
    private static DisplayImageOptions circleOptions;
    private static DisplayImageOptions cornerOptions;

    //用于回收测试，该行可删
    public static WeakReference<ViewGroup> viewGroupWeakReference;
    private static DisplayImageOptions normalOptions;


    public static RequestQueue getRequestQeuee() {
        return requestQueue;
    }

    public static DisplayImageOptions getCircleDisplayImageOptions() {
        return circleOptions;
    }

    public static DisplayImageOptions getNormalDisplayImageOptions() {
        return normalOptions;
    }

    public static DisplayImageOptions getCornerDisplayImageOptions() {
        return cornerOptions;
    }


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        //初始化Volley队列
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //初始化UserClass单例
        UserClass.init(getSharedPreferences("taji", MODE_PRIVATE));

        //初始化UniversalImageLoader
        initImageLoader(getApplicationContext());

        //初始化融云IM
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
            RongIM.init(this);
        }

        //初始化用户表数据库
        DatabaseManager.init(getApplicationContext());


        /** 友盟 设置是否对日志信息进行加密, 默认false(不加密). */
        AnalyticsConfig.enableEncrypt(true);


        //alibaba init
        AlibabaSDK.asyncInit(getApplicationContext(), new InitResultCallback() {
            @Override
            public void onSuccess() {
                Log.e("AlibabaSDK", "-----initTaeSDK----onSuccess()-------");
                mediaService = AlibabaSDK.getService(MediaService.class);
                mediaService.enableHttpDNS(); //果用户为了避免域名劫持，可以启用HttpDNS
                mediaService.enableLog(); //在调试时，可以打印日志。正式上线前可以关闭
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.e("AlibabaSDK", "-------onFailure----msg:" + msg + "  code:" + code);
            }
        });

        PlatformConfig.setWeixin("wxad4154993e83b779", "bb2a7edb7a9fc6de838554ef96deccfa");
        //微信 appid appsecret
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad");
        //新浪微博 appkey appsecret
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        // QQ和Qzone appid appkey
    }


    private static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "universalimageloader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(2) //线程池内线程的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
                .memoryCache(new WeakMemoryCache())
                // .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                //.memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
                .diskCacheSize(50 * 1024 * 1024)  // SD卡缓存的最大值
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 由原先的discCache -> diskCache
                .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        //全局初始化此配置
        ImageLoader.getInstance().init(config);

        circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(0) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(0) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_launcher) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
                .build(); // 构建完成

        cornerOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(0) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(0) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_launcher) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(20, 0)) // 设置成圆角图片
                .build(); // 构建完成

        /**
         EXACTLY :图像将完全按比例缩小的目标大小
         EXACTLY_STRETCHED:图片会缩放到目标大小完全
         IN_SAMPLE_INT:图像将被二次采样的整数倍
         IN_SAMPLE_POWER_OF_2:图片将降低2倍，直到下一减少步骤，使图像更小的目标大小
         NONE:图片不会调整
         */
        normalOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(0) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(0) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_launcher) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new SimpleBitmapDisplayer())
                //.imageScaleType(ImageScaleType.NONE_SAFE)
                .build(); // 构建完成

//        ImageLoader.getInstance().clearDiskCache();
//        ImageLoader.getInstance().clearMemoryCache();


    }


    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    private static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static boolean connect(Context context) {


        String token = UserClass.getInstance().getStringByKey("rcToken");
        if (token.equals("")) return false;

        if (context.getApplicationInfo().packageName.equals(getCurProcessName(context.getApplicationContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d("LoginActivity", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("LoginActivity", "--onTokenSuccess");
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
        Log.e("connect", "OK");
        rcHasConnect = true;
        return true;
    }


}
