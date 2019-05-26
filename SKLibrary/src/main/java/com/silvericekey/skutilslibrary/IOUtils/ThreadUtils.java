package com.silvericekey.skutilslibrary.IOUtils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class ThreadUtils {
    private static final String IO_HANDLER_NAME = "IO_HANDLER_NAME";
    private Handler mUIHandle;
    private Handler mIOHandle;
    private HandlerThread mIOThread;
    private static ThreadUtils mInstance;

    private ThreadUtils(){
        mUIHandle = new Handler(Looper.getMainLooper());
        mIOThread = new HandlerThread(IO_HANDLER_NAME);
        mIOThread.start();
        mIOHandle = new Handler(mIOThread.getLooper());
    }

    private static ThreadUtils getInstance() {
        if (mInstance==null){
            synchronized (ThreadUtils.class){
                mInstance = new ThreadUtils();
            }
        }
        return mInstance;
    }

    public static void runOnUiThread(Runnable runnable){
        getInstance().mUIHandle.post(runnable);
    }

    public static void runOnIOThread(Runnable runnable){
        getInstance().mIOHandle.post(runnable);
    }
}
