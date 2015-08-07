package blak.temp.backgroundwork.task;

public class KeyTaskManager<Key> extends BaseTaskManager<Key, Key> {
    @Override
    public Key getKey(Task<?, Key, ?> task) {
        return task.getKey();
    }
}
