package org.kyaru.pixiv.service.scheme;

import org.kyaru.pixiv.service.ServiceInterface;
import org.kyaru.pixiv.service.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.service.utils.requester.ContentType;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

import java.util.ArrayList;
import java.util.List;

public class SearchByArtwork implements ServiceInterface.IScheme {
    private final String artworkInfo;

    public SearchByArtwork(String artworkInfo) {
        this.artworkInfo = artworkInfo;
    }

    @Override
    public List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit) {
        String url = "https://www.pixiv.net/ajax/illust/%s/recommend/init?limit=%s&lang=zh".formatted(this.artworkInfo, sourceLimit);
        String json = requestClient.download(url, ContentType.JSON);
        List<String> artworkIDList = new ArrayList<>(List.of(artworkInfo));
        artworkIDList.addAll(JSONUtil.select(json, "body", "illusts").toStringList());
        return artworkIDList;
    }

    @Override
    public String getFileName(RequestClient requestClient) {
        return this.artworkInfo;
    }
}
