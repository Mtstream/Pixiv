package org.kyaru.pixiv.service.runner.savestate;

import org.jetbrains.annotations.NotNull;
import org.kyaru.pixiv.service.runner.Item;
import org.kyaru.pixiv.service.runner.TaskScheduler;


public class SaveTaskAcquirer implements TaskScheduler.TaskAcquirer<Item.SaveData> {
    public record outputFilePath(String normal, String r18) {
    }
    private final IMGSaveTask norIMGSaveTask;
    private final GIFSaveTask norGIFSaveTask;
    private final IMGSaveTask r18IMGSaveTask;
    private final GIFSaveTask r18GIFSaveTask;
    private final TaskScheduler taskScheduler;

    public SaveTaskAcquirer(TaskScheduler taskScheduler, @NotNull outputFilePath outputFilePath) {
        this.taskScheduler = taskScheduler;
        this.norIMGSaveTask = new IMGSaveTask(outputFilePath.normal);
        this.norGIFSaveTask = new GIFSaveTask(outputFilePath.normal);
        this.r18IMGSaveTask = new IMGSaveTask(outputFilePath.r18);
        this.r18GIFSaveTask = new GIFSaveTask(outputFilePath.r18);
    }

    @Override
    public void referTask(Item.SaveData saveData) {
        this.taskScheduler.uploadTask(
                switch (saveData.format()) {
                    case IMG -> switch (saveData.tag()) {
                        case NOR -> this.norIMGSaveTask;
                        case R18 -> this.r18IMGSaveTask;
                    };
                    case GIF -> switch (saveData.tag()) {
                        case NOR -> this.norGIFSaveTask;
                        case R18 -> this.r18GIFSaveTask;
                    };
                },
                saveData
        );
    }
}
