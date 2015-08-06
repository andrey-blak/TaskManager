package blak.temp.backgroundwork.task;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import java.lang.ref.Reference;
import java.util.List;

public class TaskManager<Key> {
    private final ListMultimap<Key, Task<?, Key, ?>> mTasksMap = LinkedListMultimap.create();
    private TaskExecutor mExecutor;

    private CommonTaskManager<Key> mCommonTaskManager = new CommonTaskManager<>();
    private ClassTaskManager<Key> mClassTaskManager = new ClassTaskManager<>();
    private KeyTaskManager<Key> mKeyTaskManager = new KeyTaskManager();

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
            mKeyTaskManager.addListener(key, listener);
        }

        executeTask(task, key);
    }

    public <Result, Progress> void execute(Task<Result, Key, Progress> task, Reference<? extends TaskListener<Result, Key, Progress>> listener) {
        Key key = task.getKey();
        if (listener != null) {
            mKeyTaskManager.addListener(key, listener);
        }

        executeTask(task, key);
    }

    public void cancel(Key key) {
        List<Task<?, Key, ?>> tasks = mTasksMap.get(key);
        for (Task<?, Key, ?> task : tasks) {
            task.cancel();
        }
    }

    // common
    public void addListener(TaskListener listener) {
        mCommonTaskManager.addListener(listener);
    }

    public void addListener(Reference<? extends TaskListener> reference) {
        mCommonTaskManager.addListener(reference);
    }

    public void removeListener(TaskListener listener) {
        mCommonTaskManager.removeListener(listener);
    }

    public void removeListener(Reference<? extends TaskListener> reference) {
        mCommonTaskManager.removeListener(reference);
    }

    // class
    public <Result, Progress> void addListener(Class<? extends Task<Result, Key, Progress>> taskClass, TaskListener<Result, Key, Progress> listener) {
        mClassTaskManager.addListener(taskClass, listener);
    }

    public <Result, Progress> void addListener(Class<? extends Task<Result, Key, Progress>> taskClass, Reference<? extends TaskListener<Result, Key, Progress>> reference) {
        mClassTaskManager.addListener(taskClass, reference);
    }

    public <Result, Progress> void removeListener(Class<? extends Task<Result, Key, Progress>> taskClass, TaskListener<Result, Key, Progress> listener) {
        mClassTaskManager.removeListener(taskClass, listener);
    }

    public <Result, Progress> void removeListener(Class<? extends Task<Result, Key, Progress>> taskClass, Reference<? extends TaskListener<Result, Key, Progress>> reference) {
        mClassTaskManager.removeListener(taskClass, reference);
    }

    // key
    public <Result, Progress> void addListener(Key key, TaskListener<Result, Key, Progress> listener) {
        mKeyTaskManager.addListener(key, listener);
    }

    public <Result, Progress> void addListener(Key key, Reference<? extends TaskListener<Result, Key, Progress>> reference) {
        mKeyTaskManager.addListener(key, reference);
    }

    public <Result, Progress> void removeListener(Key key, TaskListener<Result, Key, Progress> listener) {
        mKeyTaskManager.removeListener(key, listener);
    }

    public <Result, Progress> void removeListener(Key key, Reference<? extends TaskListener<Result, Key, Progress>> reference) {
        mKeyTaskManager.removeListener(key, reference);
    }

    public <Result, P> void publishProgress(Task<Result, Key, P> task, P progress) {
        mKeyTaskManager.publishProgress(task, progress);
    }

    public <Result, Progress> void onTaskFinished(Task<Result, Key, Progress> task, Result result) {
        Key key = task.getKey();
        mTasksMap.remove(key, task);

        mCommonTaskManager.onTaskFinished(task, result);
        mClassTaskManager.onTaskFinished(task, result);
        mKeyTaskManager.onTaskFinished(task, result);
    }

    public void onStop() {
        mCommonTaskManager.onStop();
        mClassTaskManager.onStop();
        mKeyTaskManager.onStop();
    }

    private <Result, Progress> void executeTask(Task<Result, Key, Progress> task, Key key) {
        mTasksMap.put(key, task);
        task.setTaskManager(this);
        mExecutor.execute(task);
    }
}
