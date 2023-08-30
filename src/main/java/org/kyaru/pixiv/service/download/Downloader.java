package org.kyaru.pixiv.service.download;

import org.kyaru.pixiv.service.Config;
import org.kyaru.pixiv.service.download.collect.*;
import org.kyaru.pixiv.service.download.collect.filterimpl.FilterPropertyImpl;
import org.kyaru.pixiv.service.download.process.TaskID;
import org.kyaru.pixiv.service.download.process.TaskPacker;
import org.kyaru.pixiv.service.download.process.impl.TaskItem;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

import java.util.List;

public class Downloader {
    private final Config.Getter getter;
    private Filter filter = null;
    public Downloader setFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    public Downloader(Config.Getter getter) {
        this.getter = getter;
    }

    public void byAuthor(String authorID) {
        run(new SearchByAuthor(authorID));
    }

    public void byArtwork(String artworkID) {
        run(new SearchByArtwork(artworkID));
    }

    public void byRanking() {
        run(new SearchByRanking());
    }

    public void byFollowing() {
        run(new SearchByFollowing());
    }

    public void byCustom(Collector collector) {
        run(collector);
    }

    private void run(Collector collector) {
        RequestClient requestClient = new RequestClient(getter.getCookie(), getter.getProxy());
        Collector.Output output = collector.get(requestClient, getter.getArtworkCount());
        TaskPacker taskPacker = TaskPacker.getDefault(
                new TaskPacker.TaskContext(
                        new RequestClient(getter.getCookie(), getter.getProxy()),
                        getter.getNormalArtworkPath().formatted(output.fileName()),
                        getter.getR18ArtworkPath().formatted(output.fileName())
                )
        );
        output.artworkIDList.forEach(artworkID -> {
            TaskID.Label label = FilterPropertyImpl.Label.getActualValue(Filter.Property.getHTML(artworkID, requestClient));
            if (this.filter == null) {
                taskPacker.handle(new TaskItem.ArtworkID(artworkID, TaskID.Step.INITIAL, label));
            } else {
                if (this.filter.isExpect(artworkID, requestClient)) {
                    taskPacker.handle(new TaskItem.ArtworkID(artworkID, TaskID.Step.INITIAL, label));
                }
            }
        });
    }

    public @FunctionalInterface interface Collector {
        Output get(RequestClient requestClient, int sourceLimit);

        record Output(List<String> artworkIDList, String fileName) {
        }
    }
}
