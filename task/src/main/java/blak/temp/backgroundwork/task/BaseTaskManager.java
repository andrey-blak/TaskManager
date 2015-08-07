package blak.temp.backgroundwork.task;

import blak.temp.backgroundwork.utils.EqualableReference;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import java.lang.ref.Reference;
import java.util.List;

public abstract class BaseTaskManager<Key, TaskKey> implements ITaskManager<Key, TaskKey> {
    private final ListMultimap<Key, TaskListener> mListenerMap = LinkedListMultimap.create();
    private final ListMultimap<Key, Reference<? extends TaskListener>> mListenerReferenceMap = LinkedListMultimap.create();

    @Override
    public void addListener(Key key, TaskListener listener) {
        if (listener != null) {
            mListenerMap.put(key, listener);
        }
    }

    @Override
    public void addListener(Key key, Reference<? extends TaskListener> reference) {
        if ((reference != null) && (reference.get() != null)) {
            mListenerReferenceMap.put(key, (Reference) reference);
        }
    }

    @Override
    public void removeListener(Key key, TaskListener listener) {
        mListenerMap.remove(key, listener);

        EqualableReference<? extends TaskListener> reference = new EqualableReference(listener);
        mListenerReferenceMap.remove(key, reference);
    }

    @Override
    public void removeListener(Key key, Reference<? extends TaskListener> reference) {
        TaskListener listener = reference.get();
        mListenerMap.remove(key, listener);

        EqualableReference<? extends TaskListener> equalableReference = new EqualableReference(listener);
        mListenerReferenceMap.remove(key, equalableReference);
    }

    @Override
    public <Result> void onTaskFinished(Task<Result, TaskKey, ?> task, Result result) {
        Key key = getKey(task);

        List<TaskListener> keyListeners = getKeyListeners(task);
        ListenerUtils.notifyListeners(keyListeners, result, task);
        mListenerMap.removeAll(key);

        List<Reference<? extends TaskListener>> keyListenerReferences = getKeyListenerReferences(task);
        ListenerUtils.notifyListenerReferences(keyListenerReferences, result, task);
        mListenerReferenceMap.removeAll(key);
    }

    @Override
    public <Progress> void publishProgress(Task<?, TaskKey, Progress> task, Progress progress) {
        List<TaskListener> keyListeners = getKeyListeners(task);
        ListenerUtils.notifyProgressListeners(keyListeners, progress, task);

        List<Reference<? extends TaskListener>> keyListenerReferences = getKeyListenerReferences(task);
        ListenerUtils.notifyProgressListenerReferences(keyListenerReferences, progress, task);
    }

    @Override
    public void onStop() {
        mListenerMap.clear();
        mListenerReferenceMap.clear();
    }

    private List<TaskListener> getKeyListeners(Task<?, TaskKey, ?> task) {
        Key key = getKey(task);
        return mListenerMap.get(key);
    }

    private <Result, Progress> List<Reference<? extends TaskListener>> getKeyListenerReferences(Task<?, TaskKey, ?> task) {
        Key key = getKey(task);
        return mListenerReferenceMap.get(key);
    }
}
