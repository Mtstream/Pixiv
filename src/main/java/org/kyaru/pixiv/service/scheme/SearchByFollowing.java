package org.kyaru.pixiv.service.scheme;

import org.kyaru.pixiv.service.ServiceInterface;
import org.kyaru.pixiv.service.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.service.utils.requester.ContentType;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

import java.util.ArrayList;
import java.util.List;

public class SearchByFollowing implements ServiceInterface.IScheme {
    @Override
    public List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit) {
        int sumPage = sourceLimit / 60 + 1;
        List<String> urlList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        for (int page = 1; page <= sumPage; page++) {
            urlList.add("https://www.pixiv.net/ajax/follow_latest/illust?p=%s&mode=all&lang=z".formatted(page));
        }
        requestClient.download(urlList, ContentType.JSON).forEach(json -> {
            idList.addAll(JSONUtil.select(json, "body", "page", "ids").toStringList());
        });
        return idList;
    }

    @Override
    public String getFileName(RequestClient requestClient) {
        return "Following";
    }
}
