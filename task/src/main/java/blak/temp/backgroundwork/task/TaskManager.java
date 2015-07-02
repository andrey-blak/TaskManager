package blak.temp.backgroundwork.task;

import blak.temp.backgroundwork.utils.EqualableWeakReference;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executor;

public class TaskManager<Key> {
    private final ListMultimap<Key, WeakReference<TaskListener>> mKeyListenerMap = LinkedListMultimap.create();
    private final ListMultimap<Key, Task<?, Key, ?>> mTasksMap = LinkedListMultimap.create();

    private final Executor mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;

    public <Result, Progress> void execute(Task<Result, Key, Progress> task, TaskListener<Result, Key, Progress> listener) {
        Key key = task.getKey();
        if (listener != null) {
            mKeyListenerMap.put(key, new WeakReference(listener));
        }

        mTasksMap.put(key, task);
        task.setTaskManager(this);
        mExecutor.execute(task);
    }

    public <Result, Progress> void addListener(Key key, TaskListener<Result, Key, Progress> listener) {
        if (listener == null) {
            return;
        }
        mKeyListenerMap.put(key, new WeakReference<TaskListener>(listener));
    }

    public void cancel(Key key) {
        List<Task<?, Key, ?>> tasks = mTasksMap.get(key);
        for (Task<?, Key, ?> task : tasks) {
            task.cancel();
        }
    }

    public <Result, Progress> void removeListener(Key key, TaskListener<Result, Key, Progress> listener) {
        EqualableWeakReference<TaskListener<Result, Key, Progress>> reference = new EqualableWeakReference<>(listener);
        mKeyListenerMap.remove(key, reference);
    }

    public <Result, P> void publishProgress(Task<Result, Key, P> task, P progress) {
        List<WeakReference<TaskListener>> classListeners = getClassListeners(task);
        notifyProgressListeners(classListeners, progress, task);
    }

    public <Result, Progress> void oTaskFinished(Task<Result, Key, Progress> task, Result result) {
        Key key = task.getKey();
        mTasksMap.remove(key, task);
        if (task.isCancelled()) {
            return;
        }
        
        List<WeakReference<TaskListener>> classListeners = getClassListeners(task);
        notifyListeners(classListeners, result, task);
    }

    private <Result, Progress> List<WeakReference<TaskListener>> getClassListeners(Task<Result, Key, Progress> task) {
        Key key = task.getKey();
        return mKeyListenerMap.get(key);
    }

    private static <Result, Key, Progress> void notifyListeners(Iterable<WeakReference<TaskListener>> listeners, Result result, Task<Result, Key, Progress> task) {
        for (WeakReference<TaskListener> weakListener : listeners) {
            TaskListener<Result, Key, Progress> listener = weakListener.get();
            notifyListener(listener, result, task);
        }
    }

    private static <Result, Key, Progress> void notifyProgressListeners(Iterable<WeakReference<TaskListener>> listeners, Progress progress, Task<Result, Key, Progress> task) {
        for (WeakReference<TaskListener> weakListener : listeners) {
            TaskListener<Result, Key, Progress> listener = weakListener.get();
            notifyProgressListener(listener, progress, task);
        }
    }

    private static <Result, Key, Progress> void notifyListener(TaskListener<Result, Key, Progress> listener, Result result, Task<Result, Key, Progress> task) {
        if (listener == null) {
            return;
        }

        listener.onFinish(result, task);
    }

    private static <Result, Key, Progress> void notifyProgressListener(TaskListener<Result, Key, Progress> listener, Progress progress, Task<Result, Key, Progress> task) {
        if (listener == null) {
            return;
        }
        listener.onProgress(progress, task);
    }
}
