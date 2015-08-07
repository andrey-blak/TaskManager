package blak.temp.backgroundwork.task;

import java.lang.ref.Reference;

public interface ITaskManager<Key, TaskKey> {
    void addListener(Key key, TaskListener listener);
    void addListener(Key key, Reference<? extends TaskListener> reference);
    void removeListener(Key key, TaskListener listener);
    void removeListener(Key key, Reference<? extends TaskListener> reference);
    <Result> void onTaskFinished(Task<Result, TaskKey, ?> task, Result result);
    <Progress> void publishProgress(Task<?, TaskKey, Progress> task, Progress progress);
    Key getKey(Task<?, TaskKey, ?> task);
    void onStop();
}
