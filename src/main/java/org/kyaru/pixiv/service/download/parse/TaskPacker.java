package org.kyaru.pixiv.service.download.parse;

import org.kyaru.pixiv.service.download.parse.impl.*;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

import java.io.File;
import java.util.HashMap;

import static org.kyaru.pixiv.service.download.parse.TaskEngine.VOID_TASK;


public class TaskPacker {
    private final TaskContext taskContext;

    private final TaskReferSet taskReferSet;

    private final TaskEngine taskEngine;

    public TaskPacker(TaskContext taskContext, TaskReferSet taskReferSet) {
        this.taskContext = taskContext;
        this.taskReferSet = taskReferSet;
        this.taskEngine = new TaskEngine(this);
        if (!new File(this.taskContext.r18OutputFilePath()).exists()) {
            new File(this.taskContext.r18OutputFilePath()).mkdirs();
        }
        if (!new File(this.taskContext.normalOutputFilePath()).exists()) {
            new File(this.taskContext.normalOutputFilePath()).mkdirs();
        }
    }

    public static TaskPacker getDefault(TaskContext taskContext) {
        TaskGetter initial = (l, tc) -> new LabelTask(tc.requestClient());
        TaskGetter afterLabeling = (l, tc) -> switch (l.format()) {
                case IMG -> new IMGParseTask(tc.requestClient());
                case GIF -> new GIFParseTask(tc.requestClient());
                case BOTH -> null;
        };
        TaskGetter afterParsing = (l, tc) -> {
            String outputFilePath = switch (l.tag()) {
                case NOR -> tc.normalOutputFilePath();
                case R18 -> tc.r18OutputFilePath();
                case BOTH -> "";
            };
            return switch (l.format()) {
                case IMG -> new IMGSaveTask(outputFilePath);
                case GIF -> new GIFSaveTask(outputFilePath);
                case BOTH -> null;
            };
        };
        TaskReferSet taskReferSet = new TaskReferSet() {
            {
                put(TaskID.Step.INITIAL, initial);
                put(TaskID.Step.AFTER_LABELING, afterLabeling);
                put(TaskID.Step.AFTER_PARSING, afterParsing);
            }
        };
        return new TaskPacker(taskContext, taskReferSet);
    }

    public void handle(String artworkID) {
        this.forward(new TaskItem.ArtworkID(artworkID, TaskID.Step.INITIAL, TaskID.Label.DEFAULT));
    }

    void forward(TaskID taskID) {
        if (taskID.label().equals(this.taskContext.acceptableLabel)) {
            this.taskEngine.run(this.taskReferSet.getOrDefault(taskID.step(), (p1, p2) -> VOID_TASK).getTask(taskID.label(), this.taskContext), taskID);
            System.out.println(taskID + " -> TaskEngine");
        } else {
            System.out.printf("%s, %s filtered%n", taskID, taskID.label());
        }
    }

    public @FunctionalInterface interface TaskGetter {
        TaskEngine.Task getTask(TaskID.Label label, TaskContext taskContext);
    }

    public record TaskContext(RequestClient requestClient, String normalOutputFilePath, String r18OutputFilePath,
                              TaskID.Label acceptableLabel) {
    }

    public static class TaskReferSet extends HashMap<TaskID.Step, TaskGetter> {
    }
}


