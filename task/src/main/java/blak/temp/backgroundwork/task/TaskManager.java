package blak.temp.backgroundwork.task;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.List;

public class TaskManager<Key> {
    private final ListMultimap<Key, TaskListener> mKeyListenerMap = LinkedListMultimap.create();
    private final ListMultimap<Key, Task<?, Key, ?>> mTasksMap = LinkedListMultimap.create();

    private TaskExecutor mExecutor;

    public TaskManager() {
        mExecutor = new ThreadPoolExecutor();
    }

    public TaskManager(TaskExecutor executor) {
        mExecutor = executor;
    }

    public <Result, Progress> void execute(Task<Result, Key, Progress> task) {
        execute(task, null);
    }

    public <Result, Progress> void execute(Task<Result, Key, Progress> task, TaskListener<Result, Key, Progress> listener) {
        Key key = task.getKey();
        if (listener != null) {
            mKeyListenerMap.put(key, listener);
        }

        mTasksMap.put(key, task);
        task.setTaskManager(this);
        mExecutor.execute(task);
    }

    public <Result, Progress> void addListener(Key key, TaskListener<Result, Key, Progress> listener) {
        if (listener == null) {
            return;
        }
        mKeyListenerMap.put(key, listener);
    }

    public void cancel(Key key) {
        List<Task<?, Key, ?>> tasks = mTasksMap.get(key);
        for (Task<?, Key, ?> task : tasks) {
            task.cancel();
        }
    }

    public <Result, Progress> void removeListener(Key key, TaskListener<Result, Key, Progress> listener) {
        mKeyListenerMap.remove(key, listener);
    }

    public <Result, P> void publishProgress(Task<Result, Key, P> task, P progress) {
        List<TaskListener> classListeners = getClassListeners(task);
        notifyProgressListeners(classListeners, progress, task);
    }

    public <Result, Progress> void onTaskFinished(Task<Result, Key, Progress> task, Result result) {
        Key key = task.getKey();
        mTasksMap.remove(key, task);

        List<TaskListener> classListeners = getClassListeners(task);
        notifyListeners(classListeners, result, task);
        mKeyListenerMap.removeAll(key);
    }

    private <Result, Progress> List<TaskListener> getClassListeners(Task<Result, Key, Progress> task) {
        Key key = task.getKey();
        return mKeyListenerMap.get(key);
    }

    private static <Result, Key, Progress> void notifyListeners(Iterable<TaskListener> listeners, Result result, Task<Result, Key, Progress> task) {
        for (TaskListener<Result, Key, Progress> listener : listeners) {
            notifyListener(listener, result, task);
        }
    }

    private static <Result, Key, Progress> void notifyListener(TaskListener<Result, Key, Progress> listener, Result result, Task<Result, Key, Progress> task) {
        if (task.isCancelled()) {
            listener.onCanceled(task);
        } else {
            listener.onFinish(result, task);
        }
    }

    private static <Result, Key, Progress> void notifyProgressListeners(Iterable<TaskListener> listeners, Progress progress, Task<Result, Key, Progress> task) {
        for (TaskListener<Result, Key, Progress> listener : listeners) {
            listener.onProgress(progress, task);
        }
    }
}
