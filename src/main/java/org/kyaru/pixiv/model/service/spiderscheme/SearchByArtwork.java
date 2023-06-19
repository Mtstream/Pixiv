package org.kyaru.pixiv.model.service.spiderscheme;

import org.kyaru.pixiv.model.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.model.utils.requester.ContentType;
import org.kyaru.pixiv.model.utils.requester.RequestClient;

import java.util.List;

public class SearchByArtwork implements IScheme {
    private final String artworkInfo;

    public SearchByArtwork(String artworkInfo) {
        this.artworkInfo = artworkInfo;
    }

    @Override
    public List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit) {
        String url = "https://www.pixiv.net/ajax/illust/%s/recommend/init?limit=%s&lang=zh".formatted(this.artworkInfo, sourceLimit);
        String json = requestClient.download(url, ContentType.JSON);
        List<String> idList = JSONUtil.select(json, "body", "illusts").toStringList();
        idList.add(artworkInfo);
        return idList;
    }

    @Override
    public String getFileName(RequestClient requestClient) {
        return this.artworkInfo;
    }
}
