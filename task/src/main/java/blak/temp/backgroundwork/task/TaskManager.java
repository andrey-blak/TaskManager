package blak.temp.backgroundwork.task;

import blak.temp.backgroundwork.utils.EqualableWeakReference;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskManager<K> {
    private static final int THREAD_POOL_SIZE = 5;

    private final ListMultimap<K, WeakReference<TaskListener>> mClassListenerMap = LinkedListMultimap.create();

    private final Executor mExecutor = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());

    public <R, P> void execute(Task<R, K, P> task, TaskListener<R, K, P> listener) {
        K key = task.getKey();
        if (listener != null) {
            mClassListenerMap.put(key, new WeakReference(listener));
        }

        mExecutor.execute(task);
    }

    public <R, P> void addListener(K key, TaskListener<R, K, P> listener) {
        if (listener == null) {
            return;
        }
        mClassListenerMap.put(key, new WeakReference<TaskListener>(listener));
    }

    public <R, P> void removeListener(K key, TaskListener<R, K, P> listener) {
        EqualableWeakReference<TaskListener<R, K, P>> reference = new EqualableWeakReference<>(listener);
        mClassListenerMap.remove(key, reference);
    }

    public <R, P> void publishProgress(Task<R, K, P> task, P progress) {
        List<WeakReference<TaskListener>> classListeners = getClassListeners(task);
        notifyProgressListeners(classListeners, progress, task);
    }

    public <R, P> void oTaskFinished(Task<R, K, P> task, R result) {
        List<WeakReference<TaskListener>> classListeners = getClassListeners(task);
        notifyListeners(classListeners, result, task);
    }

    private <R, P> List<WeakReference<TaskListener>> getClassListeners(Task<R, K, P> task) {
        K key = task.getKey();
        return mClassListenerMap.get(key);
    }

    private static <R, K, P> void notifyListeners(List<WeakReference<TaskListener>> listeners, R result, Task<R, K, P> task) {
        for (WeakReference<TaskListener> weakListener : listeners) {
            TaskListener<R, K, P> listener = weakListener.get();
            notifyTaskListener(listener, result, task);
        }
    }

    private static <R, K, P> void notifyProgressListeners(List<WeakReference<TaskListener>> listeners, P progress, Task<R, K, P> task) {
        for (WeakReference<TaskListener> weakListener : listeners) {
            TaskListener<R, K, P> listener = weakListener.get();
            notifyProgressListener(listener, progress, task);
        }
    }

    private static <R, K, P> void notifyTaskListener(TaskListener<R, K, P> listener, R result, Task<R, K, P> task) {
        if (listener == null) {
            return;
        }

        listener.onSuccess(result, task);

        // todo implement listener.onError(result, task);
    }

    private static <R, K, P> void notifyProgressListener(TaskListener<R, K, P> listener, P progress, Task<R, K, P> task) {
        if (listener == null) {
            return;
        }
        listener.onProgress(progress, task);
    }
}
