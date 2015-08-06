package blak.temp.backgroundwork.task;

import blak.temp.backgroundwork.utils.EqualableReference;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import java.lang.ref.Reference;
import java.util.List;

public class ClassTaskManager<Key> {
    private final ListMultimap<Class<? extends Task>, TaskListener> mClassListenersMap = LinkedListMultimap.create();
    private final ListMultimap<Class<? extends Task>, Reference<? extends TaskListener>> mClassListenerReferencesMap = LinkedListMultimap.create();

    public <Result, Progress> void addListener(Class<? extends Task<Result, Key, Progress>> taskClass, TaskListener<Result, Key, Progress> listener) {
        if (listener != null) {
            mClassListenersMap.put(taskClass, listener);
        }
    }

    public <Result, Progress> void addListener(Class<? extends Task<Result, Key, Progress>> taskClass, Reference<? extends TaskListener<Result, Key, Progress>> reference) {
        if ((reference != null) && (reference.get() != null)) {
            mClassListenerReferencesMap.put(taskClass, (Reference) reference);
        }
    }

    public <Result, Progress> void removeListener(Class<? extends Task<Result, Key, Progress>> taskClass, TaskListener<Result, Key, Progress> listener) {
        mClassListenersMap.remove(taskClass, listener);

        EqualableReference<? extends TaskListener<Result, Key, Progress>> reference = new EqualableReference(listener);
        mClassListenerReferencesMap.remove(taskClass, reference);
    }

    public <Result, Progress> void removeListener(Class<? extends Task<Result, Key, Progress>> taskClass, Reference<? extends TaskListener<Result, Key, Progress>> reference) {
        TaskListener<Result, Key, Progress> listener = reference.get();
        mClassListenersMap.remove(taskClass, listener);

        EqualableReference<? extends TaskListener<Result, Key, Progress>> equalableReference = new EqualableReference(listener);
        mClassListenerReferencesMap.remove(taskClass, equalableReference);
    }

    public <Result, Progress> void onTaskFinished(Task<Result, Key, Progress> task, Result result) {
        Class<? extends Task> taskClass = task.getClass();

        List<TaskListener> classListeners = getClassListeners(task);
        ListenerUtils.notifyListeners(classListeners, result, task);
        mClassListenersMap.removeAll(taskClass);

        List<Reference<? extends TaskListener>> classListenerReferences = getClassListenerReferences(task);
        ListenerUtils.notifyListenerReferences(classListenerReferences, result, task);
        mClassListenerReferencesMap.removeAll(taskClass);
    }

    public void onStop() {
        mClassListenersMap.clear();
        mClassListenerReferencesMap.clear();
    }

    private <Result, Progress> List<TaskListener> getClassListeners(Task<Result, Key, Progress> task) {
        Class<? extends Task> taskClass = task.getClass();
        return mClassListenersMap.get(taskClass);
    }

    private <Result, Progress> List<Reference<? extends TaskListener>> getClassListenerReferences(Task<Result, Key, Progress> task) {
        Class<? extends Task> taskClass = task.getClass();
        return mClassListenerReferencesMap.get(taskClass);
    }


}
