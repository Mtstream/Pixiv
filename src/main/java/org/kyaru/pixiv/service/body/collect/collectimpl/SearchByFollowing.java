package org.kyaru.pixiv.service.body.collect.collectimpl;

import org.kyaru.pixiv.service.body.collect.Collector;
import org.kyaru.pixiv.service.utils.json.JSONUtil;
import org.kyaru.pixiv.service.utils.network.RequestClient;
import org.kyaru.pixiv.service.utils.network.ReturnType;

import java.util.ArrayList;
import java.util.List;

public class SearchByFollowing implements Collector {
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
