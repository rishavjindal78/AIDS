package org.shunya.shared;

public class TaskFuture<T> {
    private int progress;
    private T result;
    private boolean done = false;
    private boolean failed = false;
    private Throwable throwable;
    private TaskState taskState = TaskState.NEW;

    public synchronized T get() throws InterruptedException {
        while (!isDone())
            wait();
        if (failed)
            throw new RuntimeException("Task Execution failed.", throwable);
        return result;
    }

    public synchronized boolean isDone() {
        return done;
    }

    public synchronized void setResult(T result) {
        this.result = result;
        done = true;
        notifyAll();
    }

    public synchronized void setException(Throwable throwable) {
        this.throwable = throwable;
        done = true;
        failed = true;
        notifyAll();
    }

    public synchronized void cancel() {
        //TODO future implementation. Keep handle of the job and send interrupt to the agent.
    }

    public void setTaskState(TaskState taskState){
        this.taskState = taskState;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public int getProgress(){
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
