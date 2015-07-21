package blak.temp.backgroundwork.task;

import android.os.AsyncTask;

import java.util.concurrent.Executor;

public class ThreadPoolExecutor implements TaskExecutor {
    private final Executor mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;

    @Override
    public void execute(Task task) {
        mExecutor.execute(task);
    }
}
