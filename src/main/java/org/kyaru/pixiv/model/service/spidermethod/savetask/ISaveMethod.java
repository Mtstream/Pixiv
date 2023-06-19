package org.kyaru.pixiv.model.service.spidermethod.savetask;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import org.kyaru.pixiv.model.service.spidermethod.TaskItem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



class SaveIMGMethod implements SaveTask.SaveMethod {
    @Override
    public TaskItem.SaveData save(TaskItem.SaveData saveData, Path outputPath) {
        for (int i = 0; i < saveData.nameList().size() && i < saveData.imageList().size(); i++) {
            try {
                String name = saveData.nameList().get(i);
                BufferedImage image = saveData.imageList().get(i);
                String suffix = name.substring(name.length() - 3);
                ImageIO.write(image, suffix, new File(Path.of(outputPath.toString(), name).toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return saveData;
    }
}

class SaveGIFMethod implements SaveTask.SaveMethod {
    private static final Pattern DELAY_PATTERN = Pattern.compile("\\[GIFd(\\d+)].*?");
    @Override
    public TaskItem.SaveData save(TaskItem.SaveData saveData, Path outputPath) {
        Matcher m = DELAY_PATTERN.matcher(saveData.nameList().get(0));
        if (m.find()) {
            int delayMs = Integer.parseInt(m.group(1));
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.start(outputPath.toString());
            e.setDelay(delayMs);
            saveData.imageList().forEach(e::addFrame);
            e.finish();
        }
        return saveData;
    }
}