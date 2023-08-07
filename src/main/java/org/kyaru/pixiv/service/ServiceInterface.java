package org.kyaru.pixiv.service;

import org.jetbrains.annotations.NotNull;
import org.kyaru.pixiv.service.runner.TaskScheduler;
import org.kyaru.pixiv.service.runner.savestate.SaveTaskAcquirer;
import org.kyaru.pixiv.service.scheme.SearchByArtwork;
import org.kyaru.pixiv.service.scheme.SearchByAuthor;
import org.kyaru.pixiv.service.scheme.SearchByFollowing;
import org.kyaru.pixiv.service.scheme.SearchByRanking;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ServiceInterface {
    private static ServiceInterface serviceInterface = null;

    public static ServiceInterface getServiceInterface() {
        if (ServiceInterface.serviceInterface == null) {
            ServiceInterface.serviceInterface = new ServiceInterface();
        }
        return ServiceInterface.serviceInterface;
    }

    public void resetParameterSet(ParameterSet parameterSet) {
        try {
            Configurator.storeSettingDataSet(parameterSet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ParameterSet getUsingParameterSet() {
        try {
            return Configurator.loadSettingDataSet();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadByArtwork(String artworkID) {
        this.enableScheme(new SearchByArtwork(artworkID));
    }

    public void downloadByAuthor(String authorID) {
        this.enableScheme(new SearchByAuthor(authorID));
    }

    public void downloadByFollowing() {
        this.enableScheme(new SearchByFollowing());
    }

    public void downloadByRanking() {
        this.enableScheme(new SearchByRanking());
    }

    public void downloadByConsumer(IScheme scheme) {
        this.enableScheme(scheme);
    }

    private void enableScheme(@NotNull IScheme scheme) {
        try {
            ParameterSet parameterSet = Configurator.loadSettingDataSet();
            RequestClient requestClient = new RequestClient(parameterSet.cookie);
            List<String> artworkIDList = scheme.getArtworkIDs(requestClient, parameterSet.sourceLimit);
            String fileName = scheme.getFileName(requestClient);
            SaveTaskAcquirer.outputFilePath newPath = new SaveTaskAcquirer.outputFilePath(
                    parameterSet.norFilePath.formatted(fileName),
                    parameterSet.r18FilePath.formatted(fileName)
            );
            TaskScheduler operator = new TaskScheduler(requestClient, newPath);
            artworkIDList.forEach(artworkID -> {
                TaskScheduler.TaskState<String> taskState = new TaskScheduler.TaskState<>(TaskScheduler.TaskState.Step.INITIAL, artworkID);
                operator.handleTaskState(taskState);
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public interface IScheme {
        List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit);

        String getFileName(RequestClient requestClient);
    }

    public record ParameterSet(int sourceLimit, String norFilePath, String r18FilePath, String cookie) {
    }
}




