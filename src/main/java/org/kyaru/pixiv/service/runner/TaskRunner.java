package org.kyaru.pixiv.service.runner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRunner {
    private final TaskScheduler taskScheduler;
    private final ExecutorService statePool;

    protected TaskRunner(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.statePool = Executors.newCachedThreadPool();
    }

    protected <I, O> void runTask(TaskPiece<I, O> taskPiece, I parameter) {
        this.statePool.submit(() -> {
            TaskScheduler.TaskState<O> responseState = taskPiece.runTask(parameter);
            this.taskScheduler.handleTaskState(responseState);
        });
    }

    public @FunctionalInterface interface TaskPiece<I, O> {
        TaskScheduler.TaskState<O> runTask(I parameter);
    }
}
