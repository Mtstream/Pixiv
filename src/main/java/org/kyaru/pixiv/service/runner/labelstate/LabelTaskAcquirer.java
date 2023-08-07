package org.kyaru.pixiv.service.runner.labelstate;

import org.kyaru.pixiv.service.runner.TaskScheduler;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

public class LabelTaskAcquirer implements TaskScheduler.TaskAcquirer<String> {
    private final LabelTask labelTask;

    private final TaskScheduler taskScheduler;

    public LabelTaskAcquirer(TaskScheduler taskScheduler, RequestClient requestClient) {
        this.labelTask = new LabelTask(requestClient);
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void referTask(String parameter) {
        if (parameter != null) {
            this.taskScheduler.uploadTask(this.labelTask, parameter);
        }
    }
}
