package org.kyaru.pixiv.service.body.process;

import org.kyaru.pixiv.service.body.process.impl.*;
import org.kyaru.pixiv.service.utils.network.RequestClient;

import java.io.File;
import java.util.HashMap;

import static org.kyaru.pixiv.service.body.process.TaskEngine.VOID_TASK;


public class TaskPacker {
    private final TaskContext taskContext;

    private final TaskReferRules taskReferRules;

    private final TaskEngine taskEngine;

    public TaskPacker(TaskContext taskContext, TaskReferRules taskReferRules) {
        this.taskContext = taskContext;
        this.taskReferRules = taskReferRules;
        this.taskEngine = new TaskEngine(this);
        if (!new File(this.taskContext.r18OutputFilePath()).exists()) {
            new File(this.taskContext.r18OutputFilePath()).mkdirs();
        }
        if (!new File(this.taskContext.normalOutputFilePath()).exists()) {
            new File(this.taskContext.normalOutputFilePath()).mkdirs();
        }
    }

    public static TaskPacker getDefault(RequestClient requestClient, String normalOutputFilePath, String r18OutputFilePath) {
        TaskGetter waitingForParsing = (l, tc) -> switch (l.format()) {
                case IMG -> new IMGParseTask(tc.requestClient());
                case GIF -> new GIFParseTask(tc.requestClient());
                case BOTH -> null;
        };
        TaskGetter waitingForSaving = (l, tc) -> {
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
        TaskReferRules taskReferRules = new TaskReferRules() {
            {
                put(TaskID.Step.WAITING_FOR_PARSING, waitingForParsing);
                put(TaskID.Step.WAITING_FOR_SAVE, waitingForSaving);
            }
        };
        return new TaskPacker(new TaskContext(requestClient, normalOutputFilePath, r18OutputFilePath), taskReferRules);
    }

    public void handle(String artworkID, TaskID.Label label) {
        this.forward(new TaskItem.ArtworkID(artworkID, TaskID.Step.WAITING_FOR_PARSING, label));
    }

    void forward(TaskID taskID) {
        this.taskEngine.run(this.taskReferRules.getOrDefault(taskID.step(), (p1, p2) -> VOID_TASK).getTask(taskID.label(), this.taskContext), taskID);
        System.out.println(taskID + " -> TaskEngine");
    }

    public @FunctionalInterface interface TaskGetter {
        TaskEngine.Task getTask(TaskID.Label label, TaskContext taskContext);
    }

    private record TaskContext(RequestClient requestClient, String normalOutputFilePath, String r18OutputFilePath) {
    }

    public static class TaskReferRules extends HashMap<TaskID.Step, TaskGetter> {
    }
}


