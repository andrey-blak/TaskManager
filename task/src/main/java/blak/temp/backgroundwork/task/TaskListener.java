package blak.temp.backgroundwork.task;

public interface TaskListener<Result, Key, Progress> {
    void onFinish(Result result, Task<Result, Key, Progress> task);
    void onProgress(Progress progress, Task<Result, Key, Progress> task);
    void onCanceled(Task<Result, Key, Progress> task);
}
