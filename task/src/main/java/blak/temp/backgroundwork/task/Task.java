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

    @Override
    public void run() {
        Result result = execute();

        mTaskManager.oTaskFinished(this, result);
    }

    protected void publishProgress(Progress progress) {
        mTaskManager.publishProgress(this, progress);
    }

    public void setTaskManager(TaskManager<Key> taskManager) {
        mTaskManager = taskManager;
    }

    public abstract Key getKey();
    protected abstract Result execute();
}
