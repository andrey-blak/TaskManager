package blak.temp.backgroundwork.task;

public interface TaskExecutor<T extends Task> {
    void execute(T task);
}
