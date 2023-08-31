package org.kyaru.pixiv.service;

import org.kyaru.pixiv.service.body.Downloader;
import org.kyaru.pixiv.service.config.FilterConfig;
import org.kyaru.pixiv.service.config.TaskConfig;

import java.nio.file.Path;

public class Pixiv {
    private final TaskConfig.Builder taskConfig;
    private final FilterConfig.Builder filterConfig;

    public Pixiv(Path settingFilePath) {
        this.taskConfig = new TaskConfig.Builder(settingFilePath);
        this.filterConfig = new FilterConfig.Builder(settingFilePath);
    }

    public TaskConfig.Builder taskConfig() {
        return this.taskConfig;
    }
    public FilterConfig.Builder FilterConfig() {
        return this.filterConfig;
    }

    public Downloader download() {
        return new Downloader(new TaskConfig(this.taskConfig.confirm()), new FilterConfig(this.filterConfig.confirm()));
    }
}