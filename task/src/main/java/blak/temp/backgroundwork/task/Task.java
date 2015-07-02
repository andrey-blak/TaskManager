package blak.temp.backgroundwork.task;

import blak.temp.backgroundwork.TaskApp;

public abstract class Task<R, K, P> implements Runnable {
    private boolean mCancelled;

    public void cancel() {
        mCancelled = true;
    }

    public boolean isCancelled() {
        return mCancelled;
    }

    @Override
    public void run() {
        R result = execute();

        getTaskManager().oTaskFinished(this, result);
    }

    protected void publishProgress(P progress) {
        getTaskManager().publishProgress(this, progress);
    }

    private TaskManager getTaskManager() {
        return TaskApp.getTaskManager();
    }

    public abstract K getKey();
    protected abstract R execute();
}
