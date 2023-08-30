package org.kyaru.pixiv.service.download.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskEngine {
    public static final Task VOID_TASK = p -> null;
    private final TaskPacker taskPacker;
    private final ExecutorService taskPool;

    TaskEngine(TaskPacker taskPacker) {
        this.taskPacker = taskPacker;
        this.taskPool = Executors.newCachedThreadPool();
    }

    void run(Task task, TaskID parameter) {
        if (task == null || parameter == null) {
            return;
        }
        this.taskPool.submit(() -> {
            TaskID result = task.run(parameter);
            this.taskPacker.forward(result);
        });
    }

    public @FunctionalInterface interface Task {
        TaskID run(TaskID parameter);
    }
}
