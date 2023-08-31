package org.kyaru.pixiv.service.body.process.impl;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import org.jetbrains.annotations.NotNull;
import org.kyaru.pixiv.service.body.process.TaskEngine;
import org.kyaru.pixiv.service.body.process.TaskID;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GIFSaveTask implements TaskEngine.Task {
    private static final Pattern DELAY_PATTERN = Pattern.compile("\\[GIF(\\d+)].*?");
    private final String outputPath;

    public GIFSaveTask(String outputPath) {
        this.outputPath = outputPath;
    }

    public void save(TaskItem.@NotNull SaveData saveData) {
        String name = saveData.imageList().get(0).toString();
        Matcher m = DELAY_PATTERN.matcher(saveData.nameList().get(0));
        if (m.find()) {
            int delayMs = Integer.parseInt(m.group(1));
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.start(Path.of(this.outputPath, name).toString());
            e.setDelay(delayMs);
            saveData.imageList().forEach(e::addFrame);
            e.finish();
        }
    }

    @Override
    public TaskItem.SaveData run(TaskID parameter) {
        TaskItem.SaveData saveData = (TaskItem.SaveData) parameter;
        save(saveData);
        return new TaskItem.SaveData(saveData.nameList(), saveData.imageList(), TaskID.Step.FINISH, saveData.label());
    }
}
