package org.kyaru.pixiv.service.runner.savestate;

import org.jetbrains.annotations.NotNull;
import org.kyaru.pixiv.service.runner.Item;
import org.kyaru.pixiv.service.runner.TaskRunner;
import org.kyaru.pixiv.service.runner.TaskScheduler;
import org.kyaru.pixiv.view.ViewInterface;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;


public class IMGSaveTask implements TaskRunner.TaskPiece<Item.SaveData, Void> {
    private final String outputPath;

    public IMGSaveTask(String outputPath) {
        this.outputPath = outputPath;
    }

    public void save(Item.@NotNull SaveData saveData) {
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
    public TaskScheduler.TaskState<Void> runTask(Item.SaveData saveData) {
        save(saveData);
        ViewInterface.getViewInterface().addDisplayImage(saveData.imageList().get(0));
        return new TaskScheduler.TaskState<>(
                TaskScheduler.TaskState.Step.AFTER_SAVING,
                null
        );
    }
}