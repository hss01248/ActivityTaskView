package cc.rome753.activitytask2.model;

import java.util.List;

public class LifecycleInfo {
    public String lifecycle;
    public String task;
    public String activity;
    public List<String> fragments;

    public LifecycleInfo(String lifecycle, String task, String activity, List<String> fragments) {
        this.lifecycle = lifecycle;
        this.task = task;
        this.activity = activity;
        this.fragments = fragments;
    }

    @Override
    public String toString() {
        return "LifecycleInfo{" +
                "lifecycle='" + lifecycle + '\'' +
                ", task='" + task + '\'' +
                ", activity='" + activity + '\'' +
                ", fragments=" + fragments +
                '}';
    }
}
