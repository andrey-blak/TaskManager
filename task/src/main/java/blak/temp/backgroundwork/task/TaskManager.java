package blak.temp.backgroundwork.task;

import blak.temp.backgroundwork.utils.EqualableWeakReference;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executor;

public class TaskManager<K> {
    private final ListMultimap<K, WeakReference<TaskListener>> mKeyListenerMap = LinkedListMultimap.create();
    private final ListMultimap<K, Task<?, K, ?>> mTasksMap = LinkedListMultimap.create();

    private final Executor mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;

    public <R, P> void execute(Task<R, K, P> task, TaskListener<R, K, P> listener) {
        K key = task.getKey();
        if (listener != null) {
            mKeyListenerMap.put(key, new WeakReference(listener));
        }

        mTasksMap.put(key, task);
        mExecutor.execute(task);
    }

    public <R, P> void addListener(K key, TaskListener<R, K, P> listener) {
        if (listener == null) {
            return;
        }
        mKeyListenerMap.put(key, new WeakReference<TaskListener>(listener));
    }

    public void cancel(K key) {
        List<Task<?, K, ?>> tasks = mTasksMap.get(key);
        for (Task<?, K, ?> task : tasks) {
            task.cancel();
        }
    }

    public <R, P> void removeListener(K key, TaskListener<R, K, P> listener) {
        EqualableWeakReference<TaskListener<R, K, P>> reference = new EqualableWeakReference<>(listener);
        mKeyListenerMap.remove(key, reference);
    }

    public <R, P> void publishProgress(Task<R, K, P> task, P progress) {
        List<WeakReference<TaskListener>> classListeners = getClassListeners(task);
        notifyProgressListeners(classListeners, progress, task);
    }

    public <R, P> void oTaskFinished(Task<R, K, P> task, R result) {
        K key = task.getKey();
        mTasksMap.remove(key, task);

        List<WeakReference<TaskListener>> classListeners = getClassListeners(task);
        notifyListeners(classListeners, result, task);
    }

    private <R, P> List<WeakReference<TaskListener>> getClassListeners(Task<R, K, P> task) {
        K key = task.getKey();
        return mKeyListenerMap.get(key);
    }

    private static <R, K, P> void notifyListeners(Iterable<WeakReference<TaskListener>> listeners, R result, Task<R, K, P> task) {
        for (WeakReference<TaskListener> weakListener : listeners) {
            TaskListener<R, K, P> listener = weakListener.get();
            notifyListener(listener, result, task);
        }
    }

    private static <R, K, P> void notifyProgressListeners(Iterable<WeakReference<TaskListener>> listeners, P progress, Task<R, K, P> task) {
        for (WeakReference<TaskListener> weakListener : listeners) {
            TaskListener<R, K, P> listener = weakListener.get();
            notifyProgressListener(listener, progress, task);
        }
    }

    private static <R, K, P> void notifyListener(TaskListener<R, K, P> listener, R result, Task<R, K, P> task) {
        if (listener == null) {
            return;
        }

        listener.onFinish(result, task);
    }

    private static <R, K, P> void notifyProgressListener(TaskListener<R, K, P> listener, P progress, Task<R, K, P> task) {
        if (listener == null) {
            return;
        }
        listener.onProgress(progress, task);
    }
}
