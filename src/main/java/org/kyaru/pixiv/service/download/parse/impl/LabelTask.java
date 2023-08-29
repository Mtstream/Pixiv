package org.kyaru.pixiv.service.download.parse.impl;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.kyaru.pixiv.service.download.parse.TaskEngine;
import org.kyaru.pixiv.service.download.parse.TaskID;
import org.kyaru.pixiv.service.utils.requester.RequestClient;
import org.kyaru.pixiv.service.utils.requester.ReturnType;
import org.seimicrawler.xpath.JXDocument;

public class LabelTask implements TaskEngine.Task {
    private final static String GIF_TAG_XPATH = "//head/title/text()";
    private final static String R18_TAG_XPATH = "//head/meta[@property=\"twitter:title\"]/@content";
    private final RequestClient requestClient;

    public LabelTask(@NotNull RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @Override
    public TaskItem.ArtworkID run(TaskID parameter) {
        TaskItem.ArtworkID artworkID = (TaskItem.ArtworkID) parameter;
        String artworkPage = this.requestClient.download("https://www.pixiv.net/artworks/" + artworkID.artworkID(), ReturnType.STRING);
        JXDocument jxDoc = new JXDocument(Jsoup.parse(artworkPage).getAllElements());
        return new TaskItem.ArtworkID(
                artworkID.artworkID(),
                TaskID.Step.AFTER_LABELING,
                new TaskID.Label(
                        jxDoc.selNOne(R18_TAG_XPATH).asString().contains("R-18") ? TaskID.Label.Tag.R18 : TaskID.Label.Tag.NOR,
                        jxDoc.selNOne(GIF_TAG_XPATH).asString().contains("动图") ? TaskID.Label.Format.GIF : TaskID.Label.Format.IMG
                )
        );
    }
}
