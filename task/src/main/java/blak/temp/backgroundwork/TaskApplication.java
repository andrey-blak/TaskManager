package blak.temp.backgroundwork;

import blak.temp.backgroundwork.task.TaskManager;

import android.app.Application;

public class TaskApplication extends Application {
    private static TaskApplication sInstance;

    private TaskManager<String> mTaskManager = new TaskManager<String>();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        init();
    }

    private void init() {
    }

    public static TaskApplication getInstance() {
        return sInstance;
    }

    public static TaskManager<String> getTaskManager() {
        return sInstance.mTaskManager;
    }
}
