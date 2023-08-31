package org.kyaru.pixiv.service.body;

import org.kyaru.pixiv.service.body.collect.*;
import org.kyaru.pixiv.service.body.collect.collectimpl.SearchByArtwork;
import org.kyaru.pixiv.service.body.collect.collectimpl.SearchByAuthor;
import org.kyaru.pixiv.service.body.collect.collectimpl.SearchByFollowing;
import org.kyaru.pixiv.service.body.collect.collectimpl.SearchByRanking;
import org.kyaru.pixiv.service.body.collect.filterimpl.FilterProperty;
import org.kyaru.pixiv.service.body.process.TaskPacker;
import org.kyaru.pixiv.service.config.FilterConfig;
import org.kyaru.pixiv.service.config.TaskConfig;
import org.kyaru.pixiv.service.utils.network.RequestClient;

import java.util.List;

public class Downloader {
    private final TaskConfig taskConfig;
    private final FilterConfig filterConfig;

    public Downloader(TaskConfig taskConfig, FilterConfig filterConfig) {
        this.taskConfig = taskConfig;
        this.filterConfig = filterConfig;
    }

    public Downloader byAuthor(int artworkCount, String authorID) {
        return run(new SearchByAuthor(authorID), artworkCount);
    }

    public Downloader byArtwork(int artworkCount, String artworkID) {
        return run(new SearchByArtwork(artworkID), artworkCount);
    }

    public Downloader byRanking(int artworkCount) {
        return run(new SearchByRanking(), artworkCount);
    }

    public Downloader byFollowing(int artworkCount) {
        return run(new SearchByFollowing(), artworkCount);
    }

    public Downloader byCustom(List<String> artworkList, String fileName) {
        return run(((requestClient, sourceLimit) -> new Collector.Output(artworkList, fileName)), artworkList.size());
    }

    private Downloader run(Collector collector, int artworkCount) {
        RequestClient requestClient = taskConfig.getRequestClient();
        Collector.Output output = collector.get(taskConfig.getRequestClient(), artworkCount);
        this.filterConfig.getFilter().filter(output.artworkIDList(), requestClient).forEach(artworkID ->
                taskConfig.getTaskPacker(output.fileName()).handle(artworkID, FilterProperty.Label.getActualValue(Filter.Property.getHTML(artworkID, requestClient)))
        );
        return this;
    }
}
