package org.kyaru.pixiv.service;

import org.kyaru.pixiv.service.download.Downloader;

public class ServiceInterface {
    public static Config config() {
        return Config.getConfig();
    }

    public static Downloader download() {
        return Downloader.getDownloader();
    }
}