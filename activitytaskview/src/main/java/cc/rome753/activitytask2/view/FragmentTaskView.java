package cc.rome753.activitytask2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.List;

import cc.rome753.activitytask2.model.FTree;
import cc.rome753.activitytask2.model.LifecycleInfo;
import cc.rome753.activitytask2.model.ViewPool;


/**
 * Created by rome753 on 2017/3/31.
 */

public class FragmentTaskView extends LinearLayout {

    FTree mTree = new FTree();

    public FragmentTaskView(Context context) {
        this(context, null);
    }

    public FragmentTaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void add(LifecycleInfo info) {
        mTree.add(info.fragments, info.lifecycle);
        notifyData();
    }

    public void remove(LifecycleInfo info) {
        mTree.remove(info.fragments);
        notifyData();

        if(getChildCount() == 0) {
            ViewPool.get().removeF(info.activity);
        }
    }

    public void update(LifecycleInfo info) {
        mTree.updateLifecycle(info.fragments.get(0), info.lifecycle);
        ViewPool.get().notifyLifecycleChange(info);
    }

    private void notifyData(){
        ViewPool.get().recycle(this);
        removeAllViews();
        if(mTree != null){
            List<String> strings = mTree.convertToList();
            for(String s : strings){
                ATextView textView = ViewPool.get().getOne(getContext());
                String[] arr = s.split(String.valueOf('\u2500')); // -
                String name = arr[arr.length - 1];
                textView.setInfoText(s, mTree.getLifecycle(name));
                addView(textView);
            }
        }
    }

}
