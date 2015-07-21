package blak.temp.backgroundwork.task;

import blak.temp.backgroundwork.utils.EqualableReference;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import java.lang.ref.Reference;
import java.util.LinkedList;
import java.util.List;

public class TaskManager<Key> {
    private final List<TaskListener> mCommonListeners = new LinkedList<>();
    private final List<Reference<TaskListener>> mCommonListenerReferences = new LinkedList<>();
    private final ListMultimap<Class<? extends Task>, TaskListener> mClassListenersMap = LinkedListMultimap.create();
    private final ListMultimap<Class<? extends Task>, Reference<TaskListener>> mClassListenerReferencesMap = LinkedListMultimap.create();
    private final ListMultimap<Key, TaskListener> mKeyListenerMap = LinkedListMultimap.create();
    private final ListMultimap<Key, Reference<TaskListener>> mKeyListenerReferenceMap = LinkedListMultimap.create();

    private final ListMultimap<Key, Task<?, Key, ?>> mTasksMap = LinkedListMultimap.create();

    private TaskExecutor mExecutor;

    public TaskManager() {
        mExecutor = new ThreadPoolExecutor();
    }

    public TaskManager(TaskExecutor executor) {
        mExecutor = executor;
    }

    public <Result, Progress> void execute(Task<Result, Key, Progress> task) {
        executeTask(task, task.getKey());
    }

    public <Result, Progress> void execute(Task<Result, Key, Progress> task, TaskListener<Result, Key, Progress> listener) {
        Key key = task.getKey();
        if (listener != null) {
            mKeyListenerMap.put(key, listener);
        }

        executeTask(task, key);
    }

    public <Result, Progress> void execute(Task<Result, Key, Progress> task, Reference<TaskListener<Result, Key, Progress>> listener) {
        Key key = task.getKey();
        if (listener != null) {
            mKeyListenerReferenceMap.put(key, (Reference) listener);
        }

        executeTask(task, key);
    }

    public void cancel(Key key) {
        List<Task<?, Key, ?>> tasks = mTasksMap.get(key);
        for (Task<?, Key, ?> task : tasks) {
            task.cancel();
        }
    }

    public void addListener(TaskListener listener) {
        mCommonListeners.add(listener);
    }

    public void addListener(Reference<TaskListener> listener) {
        mCommonListenerReferences.add(listener);
    }

    public void removeListener(TaskListener listener) {
        mCommonListeners.remove(listener);
    }

    public void removeListener(Reference<TaskListener> reference) {
        TaskListener listener = reference.get();
        if (listener == null) {
            return;
        }
        EqualableReference equalableReference = new EqualableReference(listener);
        mCommonListenerReferences.remove(equalableReference);
    }

    public <Result, Progress> void addListener(Class<? extends Task<Result, Key, Progress>> taskClass, TaskListener<Result, Key, Progress> listener) {
        if (listener != null) {
            mClassListenersMap.put(taskClass, listener);
        }
    }

    public <Result, Progress> void addListener(Class<? extends Task<Result, Key, Progress>> taskClass, Reference<TaskListener<Result, Key, Progress>> reference) {
        if ((reference != null) && (reference.get() != null)) {
            mClassListenerReferencesMap.put(taskClass, (Reference) reference);
        }
    }

    public <Result, Progress> void removeListener(Class<? extends Task<Result, Key, Progress>> taskClass, TaskListener<Result, Key, Progress> listener) {
        mClassListenersMap.remove(taskClass, listener);

        EqualableReference<TaskListener<Result, Key, Progress>> reference = new EqualableReference(listener);
        mClassListenerReferencesMap.remove(taskClass, reference);
    }

    public <Result, Progress> void removeListener(Class<? extends Task<Result, Key, Progress>> taskClass, Reference<TaskListener<Result, Key, Progress>> reference) {
        TaskListener<Result, Key, Progress> listener = reference.get();
        mClassListenersMap.remove(taskClass, listener);

        EqualableReference<TaskListener<Result, Key, Progress>> equalableReference = new EqualableReference(listener);
        mClassListenerReferencesMap.remove(taskClass, equalableReference);
    }

    public <Result, Progress> void addListener(Key key, TaskListener<Result, Key, Progress> listener) {
        if (listener != null) {
            mKeyListenerMap.put(key, listener);
        }
    }

    public <Result, Progress> void addListener(Key key, Reference<TaskListener<Result, Key, Progress>> reference) {
        if ((reference != null) && (reference.get() != null)) {
            mKeyListenerReferenceMap.put(key, (Reference) reference);
        }
    }

    public <Result, Progress> void removeListener(Key key, TaskListener<Result, Key, Progress> listener) {
        mKeyListenerMap.remove(key, listener);

        EqualableReference<TaskListener<Result, Key, Progress>> reference = new EqualableReference(listener);
        mKeyListenerReferenceMap.remove(key, reference);
    }

    public <Result, Progress> void removeListener(Key key, Reference<TaskListener<Result, Key, Progress>> reference) {
        TaskListener<Result, Key, Progress> listener = reference.get();
        mKeyListenerMap.remove(key, listener);

        EqualableReference<TaskListener<Result, Key, Progress>> equalableReference = new EqualableReference(listener);
        mKeyListenerReferenceMap.remove(key, equalableReference);
    }

    public <Result, P> void publishProgress(Task<Result, Key, P> task, P progress) {
        List<TaskListener> keyListeners = getKeyListeners(task);
        notifyProgressListeners(keyListeners, progress, task);

        List<Reference<TaskListener>> keyListenerReferences = getKeyListenerReferences(task);
        notifyProgressListenerReferences(keyListenerReferences, progress, task);
    }

    public <Result, Progress> void onTaskFinished(Task<Result, Key, Progress> task, Result result) {
        Key key = task.getKey();
        mTasksMap.remove(key, task);

        List<TaskListener> commonListeners = getCommonListeners();
        notifyListeners(commonListeners, result, task);

        List<Reference<TaskListener>> commonListenerReferences = getCommonListenerReferences();
        notifyListenerReferences(commonListenerReferences, result, task);

        Class<? extends Task> taskClass = task.getClass();

        List<TaskListener> classListeners = getClassListeners(task);
        notifyListeners(classListeners, result, task);
        mClassListenersMap.removeAll(taskClass);

        List<Reference<TaskListener>> classListenerReferences = getClassListenerReferences(task);
        notifyListenerReferences(classListenerReferences, result, task);
        mClassListenerReferencesMap.removeAll(taskClass);

        List<TaskListener> keyListeners = getKeyListeners(task);
        notifyListeners(keyListeners, result, task);
        mKeyListenerMap.removeAll(key);

        List<Reference<TaskListener>> keyListenerReferences = getKeyListenerReferences(task);
        notifyListenerReferences(keyListenerReferences, result, task);
        mKeyListenerReferenceMap.removeAll(key);
    }

    public void onStop() {
        mCommonListeners.clear();
        mCommonListenerReferences.clear();
        mClassListenersMap.clear();
        mClassListenerReferencesMap.clear();
        mKeyListenerMap.clear();
        mKeyListenerReferenceMap.clear();
    }

    private <Result, Progress> void executeTask(Task<Result, Key, Progress> task, Key key) {
        mTasksMap.put(key, task);
        task.setTaskManager(this);
        mExecutor.execute(task);
    }

    private List<TaskListener> getCommonListeners() {
        return mCommonListeners;
    }

    private List<Reference<TaskListener>> getCommonListenerReferences() {
        return mCommonListenerReferences;
    }

    private <Result, Progress> List<TaskListener> getClassListeners(Task<Result, Key, Progress> task) {
        Class<? extends Task> taskClass = task.getClass();
        return mClassListenersMap.get(taskClass);
    }

    private <Result, Progress> List<Reference<TaskListener>> getClassListenerReferences(Task<Result, Key, Progress> task) {
        Class<? extends Task> taskClass = task.getClass();
        return mClassListenerReferencesMap.get(taskClass);
    }

    private <Result, Progress> List<TaskListener> getKeyListeners(Task<Result, Key, Progress> task) {
        Key key = task.getKey();
        return mKeyListenerMap.get(key);
    }

    private <Result, Progress> List<Reference<TaskListener>> getKeyListenerReferences(Task<Result, Key, Progress> task) {
        Key key = task.getKey();
        return mKeyListenerReferenceMap.get(key);
    }

    private static <Result, Key, Progress> void notifyListeners(Iterable<TaskListener> listeners, Result result, Task<Result, Key, Progress> task) {
        for (TaskListener<Result, Key, Progress> listener : listeners) {
            notifyListener(listener, result, task);
        }
    }

    private static <Result, Key, Progress> void notifyListenerReferences(Iterable<Reference<TaskListener>> listeners, Result result, Task<Result, Key, Progress> task) {
        for (Reference<TaskListener> reference : listeners) {
            TaskListener<Result, Key, Progress> listener = reference.get();
            if (listener != null) {
                notifyListener(listener, result, task);
            }
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

    private static <Result, Key, Progress> void notifyProgressListenerReferences(Iterable<Reference<TaskListener>> listeners, Progress progress, Task<Result, Key, Progress> task) {
        for (Reference<TaskListener> reference : listeners) {
            TaskListener<Result, Key, Progress> listener = reference.get();
            if (listener != null) {
                listener.onProgress(progress, task);
            }
        }
    }
}
