package blak.temp.backgroundwork;

import blak.temp.backgroundwork.task.TaskManager;

import android.app.Application;

public class TaskApp extends Application {
    private static TaskApp sInstance;

    private TaskManager<String> mTaskManager = new TaskManager<String>();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        init();
    }

    private void init() {
    }

    public static TaskApp getInstance() {
        return sInstance;
    }

    public static TaskManager<String> getTaskManager() {
        return sInstance.mTaskManager;
    }
}
