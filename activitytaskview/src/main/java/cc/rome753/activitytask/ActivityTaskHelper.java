package cc.rome753.activitytask;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class ActivityTaskHelper {
    static String appName;
    static boolean hasRegisted;

    public static void init(Application app) {
        appName = getAppName(app);
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(app)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + app.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            app.startActivity(intent);
        } else {
            if(hasRegisted){
                return;
            }
            app.registerActivityLifecycleCallbacks(new ActivityTaskHelper().activityLifecycleImpl);
            ActivityTask.start(app);
            hasRegisted = true;
        }
    }

     static  String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private ActivityLifecycleImpl activityLifecycleImpl = new ActivityLifecycleImpl();

    private void handleFragment(Fragment fragment) {
        if(fragment == null || fragment.getActivity() == null) {
            Log.e("ActivityTaskHelper", "handleFragment null");
            return;
        }
        sendBroadcast(fragment.getActivity(), fragment);
    }

    private void handleFragment(Fragment fragment, Context context) {
        if(fragment == null || !(context instanceof Activity)) {
            Log.e("ActivityTaskHelper", "handleFragment null");
            return;
        }
        sendBroadcast((Activity) context, fragment);
    }

    private void handleActivity(Activity activity) {
        if(activity == null) {
            Log.e("ActivityTaskHelper", "handleActivity null");
            return;
        }
        sendBroadcast(activity, null);
    }

    private void sendBroadcast(Activity activity, Fragment fragment) {
        String lifecycle = Thread.currentThread().getStackTrace()[5].getMethodName();
        String task = appName + "@0x" + Integer.toHexString(activity.getTaskId());
        //String task = "cc.rome753.activitytask" + "@0x" + Integer.toHexString(activity.getTaskId());
        List<String> fragents = getAllFragments(fragment);
        /*String lifecycle = Thread.currentThread().getStackTrace()[5].getMethodName();
        String packageName = "cc.rome753.activitytask";
        Intent intent = new Intent(packageName + ".ACTION_UPDATE_LIFECYCLE");
        intent.setPackage(packageName);
        intent.putExtra("lifecycle", lifecycle);
        intent.putExtra("task", );
        intent.putExtra("task", activity.getPackageName() + "@0x" + Integer.toHexString(activity.getTaskId()));
        intent.putExtra("activity", getSimpleName(activity));
        if(fragment != null) {
            intent.putStringArrayListExtra("fragments", getAllFragments(fragment));
        }
        activity.sendBroadcast(intent);*/

       // Log.d("chao", lifecycle + " " + task + " " + activity + " " + fragents);
        if(ActivityTask.activityTaskView ==  null){
            ActivityTask.start(activity.getApplication());
        }

        ActivityTask.add(lifecycle, task,getSimpleName(activity), fragents);
    }

    private ArrayList<String> getAllFragments(Fragment fragment){
        ArrayList<String> res = new ArrayList<>();
        while(fragment != null){
            res.add(getSimpleName(fragment));
            fragment = fragment.getParentFragment();
        }
        if(res.isEmpty()){
            return null;
        }
        return res;
    }

    private String getSimpleName(Object obj){
        return obj.getClass().getSimpleName() + "@0x" + Integer.toHexString(obj.hashCode());
    }

    private class ActivityLifecycleImpl implements Application.ActivityLifecycleCallbacks{
        int count;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            count++;
            if(activity instanceof FragmentActivity){
                ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleImpl(), true);
            }
            handleActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            handleActivity(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            handleActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            handleActivity(activity);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            handleActivity(activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            handleActivity(activity);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            handleActivity(activity);
            count--;
            if(count ==0){
                ActivityTask.clear();
            }
        }


    }

    private class FragmentLifecycleImpl extends FragmentManager.FragmentLifecycleCallbacks{

        @Override
        public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
            handleFragment(f, context);
        }

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            handleFragment(f, context);
        }

        @Override
        public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            handleFragment(f);
        }

        @Override
        public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            handleFragment(f);
        }

        @Override
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            handleFragment(f);
        }

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentResumed(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentPaused(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentStopped(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
            handleFragment(f);
        }

        @Override
        public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }
    }

}
