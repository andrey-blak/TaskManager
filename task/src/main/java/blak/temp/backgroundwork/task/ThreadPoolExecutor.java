package blak.temp.backgroundwork.task;

import android.os.AsyncTask;

public class ThreadPoolExecutor<T extends Runnable> implements Executor<T> {
    private final java.util.concurrent.Executor mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;

    @Override
    public void execute(T task) {
        mExecutor.execute(task);
    }
}
