package org.kyaru.pixiv.service;

import org.kyaru.pixiv.service.download.Downloader;

public class Pixiv {
    public static Config config() {
        return Config.getConfig();
    }

    public static Downloader download() {
        return new Downloader(config().getter());
    }
}