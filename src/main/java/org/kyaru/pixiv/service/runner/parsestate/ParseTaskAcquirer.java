package org.kyaru.pixiv.service.runner.parsestate;

import org.jetbrains.annotations.NotNull;
import org.kyaru.pixiv.service.runner.Item;
import org.kyaru.pixiv.service.runner.TaskScheduler;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

public class ParseTaskAcquirer implements TaskScheduler.TaskAcquirer<Item.ArtworkID> {
    private final IMGParseTask imgParseTask;
    private final GIFParseTask gifParseTask;
    private final TaskScheduler taskScheduler;

    public ParseTaskAcquirer(TaskScheduler taskScheduler, @NotNull RequestClient requestClient) {
        this.taskScheduler = taskScheduler;
        this.imgParseTask = new IMGParseTask(requestClient);
        this.gifParseTask = new GIFParseTask(requestClient);
    }

    @Override
    public void referTask(Item.ArtworkID artworkID) {
        if (artworkID != null) {
            this.taskScheduler.uploadTask(
                    switch (artworkID.format()) {
                        case IMG -> this.imgParseTask;
                        case GIF -> this.gifParseTask;
                    },
                    artworkID
            );
        }
    }
}