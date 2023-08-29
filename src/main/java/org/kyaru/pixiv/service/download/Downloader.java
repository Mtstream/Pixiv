package org.kyaru.pixiv.service.download;

import org.kyaru.pixiv.service.Config;
import org.kyaru.pixiv.service.download.collect.SearchByArtwork;
import org.kyaru.pixiv.service.download.collect.SearchByAuthor;
import org.kyaru.pixiv.service.download.collect.SearchByFollowing;
import org.kyaru.pixiv.service.download.collect.SearchByRanking;
import org.kyaru.pixiv.service.download.parse.TaskPacker;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

import java.util.List;

public class Downloader {
    private static Downloader downloader = null;

    public static Downloader getDownloader() {
        if (downloader == null) {
            downloader = new Downloader(Config.getConfig().getter());
        }
        return downloader;
    }
    private final Config.Getter getter;
    private Downloader(Config.Getter getter) {
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

    public void byCustom(Scheme scheme) {
        run(scheme);
    }

    private void run(Scheme scheme) {
        RequestClient requestClient = new RequestClient(getter.getCookie(), getter.getProxy());
        Scheme.Output output = scheme.get(requestClient, getter.getArtworkCount());
        TaskPacker taskPacker = TaskPacker.getDefault(
                new TaskPacker.TaskContext(
                        new RequestClient(getter.getCookie(), getter.getProxy()),
                        getter.getNormalArtworkPath().formatted(output.fileName()),
                        getter.getR18ArtworkPath().formatted(output.fileName()),
                        getter.getAcceptableLabel()
                )
        );
        output.artworkIDList.forEach(taskPacker::handle);
    }


    public @FunctionalInterface interface Scheme {
        Output get(RequestClient requestClient, int sourceLimit);

        record Output(List<String> artworkIDList, String fileName) {
        }
    }
}
