package blak.temp.backgroundwork.task;

import java.lang.ref.Reference;

class ListenerUtils {
    public static <Result, Key, Progress> void notifyListeners(Iterable<TaskListener> listeners, Result result, Task<Result, Key, Progress> task) {
        for (TaskListener<Result, Key, Progress> listener : listeners) {
            notifyListener(listener, result, task);
        }
    }

    public static <Result, Key, Progress> void notifyListenerReferences(Iterable<Reference<? extends TaskListener>> listeners, Result result, Task<Result, Key, Progress> task) {
        for (Reference<? extends TaskListener> reference : listeners) {
            TaskListener<Result, Key, Progress> listener = reference.get();
            if (listener != null) {
                notifyListener(listener, result, task);
            }
        }
    }

    public static <Result, Key, Progress> void notifyListener(TaskListener<Result, Key, Progress> listener, Result result, Task<Result, Key, Progress> task) {
        if (task.isCancelled()) {
            listener.onCanceled(task);
        } else {
            listener.onFinish(result, task);
        }
    }

    public static <Result, Key, Progress> void notifyProgressListeners(Iterable<TaskListener> listeners, Progress progress, Task<Result, Key, Progress> task) {
        for (TaskListener<Result, Key, Progress> listener : listeners) {
            listener.onProgress(progress, task);
        }
    }

    public static <Result, Key, Progress> void notifyProgressListenerReferences(Iterable<Reference<? extends TaskListener>> listeners, Progress progress, Task<Result, Key, Progress> task) {
        for (Reference<? extends TaskListener> reference : listeners) {
            TaskListener<Result, Key, Progress> listener = reference.get();
            if (listener != null) {
                listener.onProgress(progress, task);
            }
        }
    }
}
