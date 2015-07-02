package blak.temp.backgroundwork.task;

public interface TaskListener<R, K, P> {
    void onSuccess(R result, Task<R, K, P> task);
    void onProgress(P progress, Task<R, K, P> task);
}
