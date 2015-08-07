package blak.temp.backgroundwork.task;

public class ClassTaskManager extends BaseTaskManager<Class<? extends Task>, Object> {
    @Override
    public Class<? extends Task> getKey(Task<?, Object, ?> task) {
        return task.getClass();
    }
}
