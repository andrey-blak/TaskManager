package blak.temp.backgroundwork.task;

import blak.temp.backgroundwork.utils.EqualableReference;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import java.lang.ref.Reference;
import java.util.List;

public class KeyTaskManager<Key> {
    private final ListMultimap<Key, TaskListener> mKeyListenerMap = LinkedListMultimap.create();
    private final ListMultimap<Key, Reference<? extends TaskListener>> mKeyListenerReferenceMap = LinkedListMultimap.create();

    public <Result, Progress> void addListener(Key key, TaskListener<Result, Key, Progress> listener) {
        if (listener != null) {
            mKeyListenerMap.put(key, listener);
        }
    }

    public <Result, Progress> void addListener(Key key, Reference<? extends TaskListener<Result, Key, Progress>> reference) {
        if ((reference != null) && (reference.get() != null)) {
            mKeyListenerReferenceMap.put(key, (Reference) reference);
        }
    }

    public <Result, Progress> void removeListener(Key key, TaskListener<Result, Key, Progress> listener) {
        mKeyListenerMap.remove(key, listener);

        EqualableReference<? extends TaskListener<Result, Key, Progress>> reference = new EqualableReference(listener);
        mKeyListenerReferenceMap.remove(key, reference);
    }

    public <Result, Progress> void removeListener(Key key, Reference<? extends TaskListener<Result, Key, Progress>> reference) {
        TaskListener<Result, Key, Progress> listener = reference.get();
        mKeyListenerMap.remove(key, listener);

        EqualableReference<? extends TaskListener<Result, Key, Progress>> equalableReference = new EqualableReference(listener);
        mKeyListenerReferenceMap.remove(key, equalableReference);
    }

    public <Result, Progress> void onTaskFinished(Task<Result, Key, Progress> task, Result result) {
        Key key = task.getKey();

        List<TaskListener> keyListeners = getKeyListeners(task);
        ListenerUtils.notifyListeners(keyListeners, result, task);
        mKeyListenerMap.removeAll(key);

        List<Reference<? extends TaskListener>> keyListenerReferences = getKeyListenerReferences(task);
        ListenerUtils.notifyListenerReferences(keyListenerReferences, result, task);
        mKeyListenerReferenceMap.removeAll(key);
    }

    public <Result, P> void publishProgress(Task<Result, Key, P> task, P progress) {
        List<TaskListener> keyListeners = getKeyListeners(task);
        ListenerUtils.notifyProgressListeners(keyListeners, progress, task);

        List<Reference<? extends TaskListener>> keyListenerReferences = getKeyListenerReferences(task);
        ListenerUtils.notifyProgressListenerReferences(keyListenerReferences, progress, task);
    }

    public void onStop() {
        mKeyListenerMap.clear();
        mKeyListenerReferenceMap.clear();
    }

    private <Result, Progress> List<TaskListener> getKeyListeners(Task<Result, Key, Progress> task) {
        Key key = task.getKey();
        return mKeyListenerMap.get(key);
    }

    private <Result, Progress> List<Reference<? extends TaskListener>> getKeyListenerReferences(Task<Result, Key, Progress> task) {
        Key key = task.getKey();
        return mKeyListenerReferenceMap.get(key);
    }
}
