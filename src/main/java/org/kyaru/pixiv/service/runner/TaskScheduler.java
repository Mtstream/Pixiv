package org.kyaru.pixiv.service.runner;

import org.kyaru.pixiv.service.runner.labelstate.LabelTaskAcquirer;
import org.kyaru.pixiv.service.runner.parsestate.ParseTaskAcquirer;
import org.kyaru.pixiv.service.runner.savestate.SaveTaskAcquirer;
import org.kyaru.pixiv.service.utils.requester.RequestClient;
import org.kyaru.pixiv.view.ViewInterface;


public class TaskScheduler {
    private final LabelTaskAcquirer labelTaskAcquirer;
    private final ParseTaskAcquirer parseTaskAcquirer;
    private final SaveTaskAcquirer saveTaskAcquirer;
    private final TaskRunner taskRunner;

    private int runningTaskCount = 0;
    private int finishedTaskCount = 0;

    public TaskScheduler(RequestClient requestClient, SaveTaskAcquirer.outputFilePath outputFileMap) {
        this.labelTaskAcquirer = new LabelTaskAcquirer(this, requestClient);
        this.parseTaskAcquirer = new ParseTaskAcquirer(this, requestClient);
        this.saveTaskAcquirer = new SaveTaskAcquirer(this, outputFileMap);
        this.taskRunner = new TaskRunner(this);
    }

    public <V> void handleTaskState(TaskState<V> state) {
        switch (state.step) {
            case INITIAL -> {
                this.labelTaskAcquirer.referTask((String) state.parameter);
                this.runningTaskCount++;
            }
            case AFTER_LABELING -> this.parseTaskAcquirer.referTask((Item.ArtworkID) state.parameter);
            case AFTER_PARSING -> this.saveTaskAcquirer.referTask((Item.SaveData) state.parameter);
            case AFTER_SAVING -> {
                this.runningTaskCount--;
                this.finishedTaskCount++;
            }
        }
        ViewInterface.getViewInterface().display("%s : %s%n".formatted(state.parameter.toString(), state.step.toString()));
        ViewInterface.getViewInterface().updateProcess(this.finishedTaskCount / (this.finishedTaskCount + this.runningTaskCount) * 100);
    }

    public <I, O> void uploadTask(TaskRunner.TaskPiece<I, O> taskPiece, I parameter) {
        this.taskRunner.runTask(taskPiece, parameter);
    }

    public @FunctionalInterface interface TaskAcquirer<I> {
        void referTask(I parameter);
    }

    public static class TaskState<V> {
        public Step step;
        public V parameter;

        public TaskState(Step step, V parameter) {
            this.step = step;
            this.parameter = parameter;
        }
        public enum Step {
            INITIAL,
            AFTER_LABELING,
            AFTER_PARSING,
            AFTER_SAVING,
            FAIL
        }
    }
}
