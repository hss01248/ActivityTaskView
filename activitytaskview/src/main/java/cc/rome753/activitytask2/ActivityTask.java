package cc.rome753.activitytask2;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cc.rome753.activitytask2.model.LifecycleInfo;
import cc.rome753.activitytask2.view.ActivityTaskView;

/**
 * Created by rome753@163.com on 2017/4/16.
 */

public class ActivityTask {

    private static final String TAG = ActivityTask.class.getSimpleName();

     static ActivityTaskView activityTaskView;

    public static long interval = 100;
    static Context context;

    public static void start(Context context) {
        ActivityTask.context = context;
        if (activityTaskView == null) {
            activityTaskView = new ActivityTaskView(context);
            addViewToWindow(context, activityTaskView);
        }
    }

    public static void clear() {
        if (activityTaskView != null) {
            activityTaskView.clear();
            //WindowManager windowManager = (WindowManager) activityTaskView.getContext().getSystemService(Context.WINDOW_SERVICE);
           // windowManager.removeView(activityTaskView);
        }

    }

    private static void addViewToWindow(Context context, View view) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= 26) {// Android 8.0
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = context.getResources().getDisplayMetrics().heightPixels - 500;
        if (windowManager != null) {
            windowManager.addView(view, params);
        }
    }

    private static QueueHandler queueHandler;

    public static void add(String lifecycle, String task, String activity, List<String> fragments) {
        if (queueHandler == null) {
            queueHandler = new QueueHandler();
        }
        LifecycleInfo info = new LifecycleInfo(lifecycle, task, activity, fragments);
        queueHandler.send(info);
    }

    private static class QueueHandler extends Handler {

        private Queue<LifecycleInfo> queue;
        private long lastTime;

        QueueHandler() {
            super(Looper.getMainLooper());
            lastTime = 0;
            queue = new LinkedList<>();
        }

        void send(LifecycleInfo info) {
            queue.add(info);
            sendEmptyMessage(0);
        }

        @Override
        public void handleMessage(Message msg) {
            if (System.currentTimeMillis() - lastTime < interval) {
                sendEmptyMessageDelayed(0, interval / 5);
            } else {
                lastTime = System.currentTimeMillis();
                LifecycleInfo info = queue.poll();
                //Log.d("chao","queue.poll():"+info+"");
               // Log.d("chao","activityTaskView:"+activityTaskView+"");

                if (info != null && activityTaskView != null) {
                    if (info.fragments != null ) {
                        if (info.lifecycle.contains("PreAttach")) {
                            activityTaskView.addF(info);
                        } else if (info.lifecycle.contains("Detach")) {
                            activityTaskView.removeF(info);
                        } else {
                            activityTaskView.updateF(info);
                        }
                    } else {
                        if (info.lifecycle.contains("Create")) {
                            activityTaskView.add(info);
                        } else if (info.lifecycle.contains("Destroy")) {
                            activityTaskView.remove(info);
                        } else {
                            activityTaskView.update(info);
                        }
                    }
                }else {
                    Log.w("chao","info:"+info+",activityTaskView:"+activityTaskView);
                    if(activityTaskView == null){
                        start(context);
                    }
                }
            }
        }

    }

}
