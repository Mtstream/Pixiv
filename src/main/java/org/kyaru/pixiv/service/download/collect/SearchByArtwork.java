package org.kyaru.pixiv.service.download.collect;

import org.kyaru.pixiv.service.download.Downloader;
import org.kyaru.pixiv.service.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.service.utils.requester.RequestClient;
import org.kyaru.pixiv.service.utils.requester.ReturnType;

import java.util.ArrayList;
import java.util.List;

public class SearchByArtwork implements Downloader.Scheme {
    private final String artworkInfo;

    public SearchByArtwork(String artworkInfo) {
        this.artworkInfo = artworkInfo;
    }

    public List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit) {
        String url = "https://www.pixiv.net/ajax/illust/%s/recommend/init?limit=%s&lang=zh".formatted(this.artworkInfo, sourceLimit);
        String json = requestClient.download(url, ReturnType.STRING);
        List<String> artworkIDList = new ArrayList<>(List.of(artworkInfo));
        artworkIDList.addAll(JSONUtil.select(json, "body", "illusts").toStringList());
        return artworkIDList;
    }

    public String getFileName() {
        return this.artworkInfo;
    }

    @Override
    public Output get(RequestClient requestClient, int sourceLimit) {
        return new Output(getArtworkIDs(requestClient, sourceLimit), getFileName());
    }
}
