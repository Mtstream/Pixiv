package org.kyaru.pixiv.model.service.spidermethod.savetask;

import org.kyaru.pixiv.model.service.spidermethod.TaskItem;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SaveTask {
    private static final Map<TaskItem.Format, SaveMethod> METHOD_MAP = new HashMap<>() {
        {
            put(TaskItem.Format.IMG, new SaveIMGMethod());
            put(TaskItem.Format.GIF, new SaveGIFMethod());
        }
    };

    private final HashMap<TaskItem.Type, Path> outputPathMap;

    public SaveTask(HashMap<TaskItem.Type, Path> outputPathMap){
        this.outputPathMap = outputPathMap;
        this.outputPathMap.forEach((type, path) -> path.toFile().mkdirs());
    }

    public TaskItem.SaveData run(TaskItem.SaveData saveData) {
        if (saveData == null) {
            System.out.println("Failed: SaveData-NULL");
            return null;
        }
        METHOD_MAP.get(saveData.type().format()).save(saveData, outputPathMap.get(saveData.type()));
        System.out.printf("Success: save SaveData-%s%n", saveData);
        return saveData;
    }

    @FunctionalInterface protected interface SaveMethod {
        TaskItem.SaveData save(TaskItem.SaveData saveData, Path outputPath);
    }
}
