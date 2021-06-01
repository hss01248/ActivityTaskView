package cc.rome753.activitytask;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.startup.Initializer;

import java.util.ArrayList;
import java.util.List;

public class InitForActivityTaskView implements Initializer<String> {
    @NonNull
    @Override
    public String create(@NonNull Context context) {
        if(context instanceof Application){
            ActivityTaskHelper.init((Application) context);
        }else {
            Log.w("init","not application:"+context);
        }

        return "";
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return new ArrayList<>();
    }
}
