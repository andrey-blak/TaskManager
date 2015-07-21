package blak.temp.backgroundwork.task;

public abstract class Task<Result, Key, Progress> implements Runnable {
    private boolean mCancelled;

    private TaskManager<Key> mTaskManager;

    @Override
    public void run() {
        if (!mCancelled) {
            execute();
        }
    }

    public void cancel() {
        mCancelled = true;
        mTaskManager.onTaskFinished(this, null);
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
        if (!mCancelled) {
            mTaskManager.onTaskFinished(this, result);
        }
    }

    public abstract Key getKey();
    protected abstract void execute();
}
