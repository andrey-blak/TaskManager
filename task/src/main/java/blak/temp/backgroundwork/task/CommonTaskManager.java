package blak.temp.backgroundwork.task;

import blak.temp.backgroundwork.utils.EqualableReference;

import java.lang.ref.Reference;
import java.util.LinkedList;
import java.util.List;

public class CommonTaskManager<Key> {
    private final List<TaskListener> mCommonListeners = new LinkedList<>();
    private final List<Reference<? extends TaskListener>> mCommonListenerReferences = new LinkedList<>();

    public void addListener(TaskListener listener) {
        mCommonListeners.add(listener);
    }

    public void addListener(Reference<? extends TaskListener> listener) {
        mCommonListenerReferences.add(listener);
    }

    public void removeListener(TaskListener listener) {
        mCommonListeners.remove(listener);
    }

    public void removeListener(Reference<? extends TaskListener> reference) {
        TaskListener listener = reference.get();
        if (listener == null) {
            return;
        }
        EqualableReference equalableReference = new EqualableReference(listener);
        mCommonListenerReferences.remove(equalableReference);
    }

    public <Result, Progress> void onTaskFinished(Task<Result, Key, Progress> task, Result result) {
        List<TaskListener> commonListeners = mCommonListeners;
        ListenerUtils.notifyListeners(commonListeners, result, task);

        List<Reference<? extends TaskListener>> commonListenerReferences = mCommonListenerReferences;
        ListenerUtils.notifyListenerReferences(commonListenerReferences, result, task);
    }

    public void onStop() {
        mCommonListeners.clear();
        mCommonListenerReferences.clear();
    }
}
