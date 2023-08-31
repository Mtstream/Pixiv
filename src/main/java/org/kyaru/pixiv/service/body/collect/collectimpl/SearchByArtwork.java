package org.kyaru.pixiv.service.body.collect.collectimpl;

import org.kyaru.pixiv.service.body.collect.Collector;
import org.kyaru.pixiv.service.utils.json.JSONUtil;
import org.kyaru.pixiv.service.utils.network.RequestClient;
import org.kyaru.pixiv.service.utils.network.ReturnType;

import java.util.ArrayList;
import java.util.List;

public class SearchByArtwork implements Collector {
    private final String artworkInfo;

    public SearchByArtwork(String artworkInfo) {
        this.artworkInfo = artworkInfo;
    }

    public List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit) {
        String url = "https://www.pixiv.net/ajax/illust/%s/recommend/init?limit=%s&lang=zh".formatted(this.artworkInfo, sourceLimit);
        String json = requestClient.download(url, ReturnType.STRING);
        List<String> idList = new ArrayList<>(List.of(artworkInfo));
        idList.addAll(JSONUtil.select(json, "body", "illusts").toStringList());
        idList = idList.size() > sourceLimit ? idList.subList(0, sourceLimit) : idList;
        return idList;
    }

    public String getFileName() {
        return this.artworkInfo;
    }

    @Override
    public Collector.Output get(RequestClient requestClient, int sourceLimit) {
        return new Output(getArtworkIDs(requestClient, sourceLimit), getFileName());
    }
}
