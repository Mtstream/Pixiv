package org.kyaru.pixiv.service.runner.savestate;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import org.jetbrains.annotations.NotNull;
import org.kyaru.pixiv.service.runner.Item;
import org.kyaru.pixiv.service.runner.TaskRunner;
import org.kyaru.pixiv.service.runner.TaskScheduler;
import org.kyaru.pixiv.view.ViewInterface;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GIFSaveTask implements TaskRunner.TaskPiece<Item.SaveData, Void> {
    private static final Pattern DELAY_PATTERN = Pattern.compile("\\[GIFd(\\d+)].*?");
    private final String outputPath;

    public GIFSaveTask(String outputPath) {
        this.outputPath = outputPath;
    }

    public void save(Item.@NotNull SaveData saveData) {
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
    public TaskScheduler.TaskState<Void> runTask(Item.SaveData saveData) {
        save(saveData);
        ViewInterface.getViewInterface().addDisplayImage(saveData.imageList().get(0));
        return new TaskScheduler.TaskState<>(
                TaskScheduler.TaskState.Step.AFTER_SAVING,
                null
        );
    }
}
