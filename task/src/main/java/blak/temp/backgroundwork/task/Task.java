package blak.temp.backgroundwork.task;

public abstract class Task<Result, Key, Progress> implements Runnable {
    private boolean mCancelled;

    private TaskManager<Key> mTaskManager;

    public void cancel() {
        mCancelled = true;
    }

    public boolean isCancelled() {
        return mCancelled;
    }

    protected void publishProgress(Progress progress) {
        mTaskManager.publishProgress(this, progress);
    }

    public void setTaskManager(TaskManager<Key> taskManager) {
        mTaskManager = taskManager;
    }

    public TaskManager<Key> getTaskManager() {
        return mTaskManager;
    }

    protected void onFinish(Result result) {
        mTaskManager.oTaskFinished(this, result);
    }

    public abstract Key getKey();
}
