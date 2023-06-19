package org.kyaru.pixiv.model.service.spidermethod.parsetask;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.kyaru.pixiv.model.service.spidermethod.TaskItem;
import org.kyaru.pixiv.model.utils.requester.ContentType;
import org.kyaru.pixiv.model.utils.requester.RequestClient;
import org.seimicrawler.xpath.JXDocument;

import java.util.HashMap;
import java.util.Map;

public class ParseTask {
    private final static String GIF_TAG_XPATH = "//head/title/text()";
    private final static String R18_TAG_XPATH = "//head/meta[@property=\"twitter:title\"]/@content";
    private final static Map<TaskItem.Format, ParseMethod> METHOD_MAP = new HashMap<>() {{
            put(TaskItem.Format.IMG, new ParseIMGMethod());
            put(TaskItem.Format.GIF, new ParseGIFMethod());
    }};

    private final RequestClient requestClient;

    public ParseTask(@NotNull RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    public TaskItem.SaveData run(String id) {
        if (id == null) {
            System.out.println("Failed: id-NULL");
            return null;
        }
        JXDocument jxDoc = new JXDocument(Jsoup.parse(this.requestClient.download(getPageUrl(id), ContentType.HTML)).getAllElements());
        TaskItem.Type artworkType = new TaskItem.Type(
                jxDoc.selNOne(GIF_TAG_XPATH).asString().contains("动图") ? TaskItem.Format.GIF : TaskItem.Format.IMG,
                jxDoc.selNOne(R18_TAG_XPATH).asString().contains("R-18") ? TaskItem.Tag.R18 : TaskItem.Tag.NOR
        );
        TaskItem.SaveData saveData = METHOD_MAP.get(artworkType.format()).getSaveData(id, this.requestClient);
        TaskItem.SaveData result = new TaskItem.SaveData(saveData.nameList(), saveData.imageList(), artworkType);
        System.out.printf("Success: parse id-%s", id);
        return result;
    }

    private String getPageUrl(String artworkID) {
        return "https://www.pixiv.net/artworks/" + artworkID;
    }

    @FunctionalInterface protected interface ParseMethod {
        TaskItem.SaveData getSaveData(String id, RequestClient client);
    }
}