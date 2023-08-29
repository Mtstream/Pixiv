package org.kyaru.pixiv.service.download.parse.impl;

import org.jetbrains.annotations.NotNull;
import org.kyaru.pixiv.service.download.parse.TaskEngine;
import org.kyaru.pixiv.service.download.parse.TaskID;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;


public class IMGSaveTask implements TaskEngine.Task {
    private final String outputPath;

    public IMGSaveTask(String outputPath) {
        this.outputPath = outputPath;
    }

    public void save(TaskItem.@NotNull SaveData saveData) {
        for (int i = 0; i < saveData.nameList().size() && i < saveData.imageList().size(); i++) {
            try {
                String name = saveData.nameList().get(i);
                BufferedImage image = saveData.imageList().get(i);
                String suffix = name.substring(name.length() - 3);
                ImageIO.write(image, suffix, new File(Path.of(this.outputPath, name).toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public TaskItem.SaveData run(TaskID parameter) {
        TaskItem.SaveData saveData = (TaskItem.SaveData) parameter;
        save(saveData);
        return new TaskItem.SaveData(saveData.nameList(), saveData.imageList(), TaskID.Step.AFTER_SAVING, saveData.label());
    }
}