package org.kyaru.pixiv.service.runner.labelstate;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.kyaru.pixiv.service.runner.Item;
import org.kyaru.pixiv.service.runner.TaskRunner;
import org.kyaru.pixiv.service.runner.TaskScheduler;
import org.kyaru.pixiv.service.utils.requester.ContentType;
import org.kyaru.pixiv.service.utils.requester.RequestClient;
import org.seimicrawler.xpath.JXDocument;

public class LabelTask implements TaskRunner.TaskPiece<String, Item.ArtworkID> {
    private final static String GIF_TAG_XPATH = "//head/title/text()";
    private final static String R18_TAG_XPATH = "//head/meta[@property=\"twitter:title\"]/@content";
    private final RequestClient requestClient;

    LabelTask(@NotNull RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @Override
    public TaskScheduler.TaskState<Item.ArtworkID> runTask(String artworkID) {
        String artworkPage = this.requestClient.download("https://www.pixiv.net/artworks/" + artworkID, ContentType.HTML);
        JXDocument jxDoc = new JXDocument(Jsoup.parse(artworkPage).getAllElements());
        return new TaskScheduler.TaskState<>(
                TaskScheduler.TaskState.Step.AFTER_LABELING,
                new Item.ArtworkID(
                        jxDoc.selNOne(R18_TAG_XPATH).asString().contains("R-18") ? Item.Tag.R18 : Item.Tag.NOR,
                        jxDoc.selNOne(GIF_TAG_XPATH).asString().contains("动图") ? Item.Format.GIF : Item.Format.IMG,
                        artworkID
                )
        );
    }
}
