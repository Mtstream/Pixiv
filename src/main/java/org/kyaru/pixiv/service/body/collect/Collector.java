package org.kyaru.pixiv.service.body.collect;

import org.kyaru.pixiv.service.body.Downloader;
import org.kyaru.pixiv.service.body.process.TaskPacker;
import org.kyaru.pixiv.service.utils.network.RequestClient;

import java.util.List;

public interface Collector {
    Output get(RequestClient requestClient, int sourceLimit);

    record Output(List<String> artworkIDList, String fileName) {
    }
}
