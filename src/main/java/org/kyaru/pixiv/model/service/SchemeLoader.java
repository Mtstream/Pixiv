package org.kyaru.pixiv.model.service;

import org.kyaru.pixiv.model.service.spidermethod.TaskItem;
import org.kyaru.pixiv.model.service.spidermethod.parsetask.ParseTask;
import org.kyaru.pixiv.model.service.spidermethod.savetask.SaveTask;
import org.kyaru.pixiv.model.service.spiderscheme.IScheme;
import org.kyaru.pixiv.model.utils.requester.RequestClient;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SchemeLoader {
    private final Path defaultPath;
    private final HashMap<TaskItem.Type, Path> outPutFilePaths;
    private final int sourceLimit;
    private final RequestClient requestClient;

    public SchemeLoader(RequestClient requestClient, Path defaultPath, HashMap<TaskItem.Type, Path> outPutFilePaths, int sourceLimit) {
        this.requestClient = requestClient;
        this.sourceLimit = sourceLimit;
        this.defaultPath = defaultPath;
        this.outPutFilePaths = outPutFilePaths;
    }

    public Downloader getDownloader(IScheme scheme) {
        return () -> {
            HashMap<TaskItem.Type, Path> outputFilePaths = new HashMap<>();
            List.of(TaskItem.Type.NOR_IMG, TaskItem.Type.R18_IMG, TaskItem.Type.NOR_GIF, TaskItem.Type.R18_GIF).forEach(type ->
                    outputFilePaths.put(type, Path.of(defaultPath.toString(), scheme.getFileName(requestClient), this.outPutFilePaths.get(type).toString()))
            );
            ParseTask parseTask = new ParseTask(requestClient);
            SaveTask saveTask = new SaveTask(outputFilePaths);
            ExecutorService pool = Executors.newCachedThreadPool();

            scheme.getArtworkIDs(requestClient, sourceLimit).forEach(artworkID -> saveTask.run(parseTask.run(artworkID)));
        };
    }

    @FunctionalInterface public interface Downloader {
        void download();
    }
}


