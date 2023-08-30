package org.kyaru.pixiv.service.download.collect;

import org.kyaru.pixiv.service.download.Downloader;
import org.kyaru.pixiv.service.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.service.utils.requester.RequestClient;
import org.kyaru.pixiv.service.utils.requester.ReturnType;

import java.util.ArrayList;
import java.util.List;

public class SearchByFollowing implements Downloader.Scheme {
    public List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit) {
        int sumPage = sourceLimit / 60 + 1;
        List<String> urlList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        for (int page = 1; page <= sumPage; page++) {
            urlList.add("https://www.pixiv.net/ajax/follow_latest/illust?p=%s&mode=all&lang=z".formatted(page));
        }
        List<String> finalIdList = idList;
        requestClient.download(urlList, ReturnType.STRING)
                .forEach(json -> finalIdList.addAll(JSONUtil.select(json, "body", "page", "ids").toStringList()));
        idList = idList.size() > sourceLimit ? idList.subList(0, sourceLimit) : idList;
        return idList;
    }

    public String getFileName() {
        return "Following";
    }

    @Override
    public Output get(RequestClient requestClient, int sourceLimit) {
        return new Output(getArtworkIDs(requestClient, sourceLimit), getFileName());
    }
}
