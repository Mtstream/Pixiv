package org.kyaru.pixiv.service.runner.parsestate;

import org.jetbrains.annotations.NotNull;
import org.kyaru.pixiv.service.runner.Item;
import org.kyaru.pixiv.service.runner.TaskRunner;
import org.kyaru.pixiv.service.runner.TaskScheduler;
import org.kyaru.pixiv.service.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.service.utils.requester.ContentType;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IMGParseTask implements TaskRunner.TaskPiece<Item.ArtworkID, Item.SaveData> {
    private final RequestClient requestClient;

    IMGParseTask(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    public List<String> getSrcNames(String artworkID, @NotNull List<String> srcURLs) {
        List<String> nameList = new ArrayList<>();
        int page = 0;
        for (String srcURL : srcURLs) {
            String suffix = srcURL.substring(srcURLs.get(page).length() - 3);
            nameList.add(String.format("[IMG]%s_p%d.%s", artworkID, page, suffix));
            page++;
        }
        return nameList;
    }

    public List<BufferedImage> getSrcImages(List<String> srcURLs) {
        List<byte[]> bytesList = requestClient.download(srcURLs, ContentType.BYTE);
        List<BufferedImage> bufferedImageList = new ArrayList<>();
        for (byte[] bytes : bytesList) {
            try {
                bufferedImageList.add(ImageIO.read(new ByteArrayInputStream(bytes)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return bufferedImageList;
    }

    public List<String> getSrcURLs(String artworkID) {
        List<String> srcURLs = new ArrayList<>();
        String url = "https://www.pixiv.net/ajax/illust/%s/pages?lang=zh".formatted(artworkID);
        String json = this.requestClient.download(url, ContentType.JSON);
        JSONUtil.select(json, "body").toStringList().forEach(node -> {
            String srcURL = JSONUtil.select(node.toString(), "urls", "original").toString();
            srcURLs.add(srcURL);
        });
        return srcURLs;
    }

    @Override
    public TaskScheduler.TaskState<Item.SaveData> runTask(Item.@NotNull ArtworkID artworkID) {
        List<String> srcURLs = getSrcURLs(artworkID.artworkID());
        return new TaskScheduler.TaskState<>(
                TaskScheduler.TaskState.Step.AFTER_PARSING,
                new Item.SaveData(artworkID.tag(), artworkID.format(), getSrcNames(artworkID.artworkID(), srcURLs), getSrcImages(srcURLs))
        );
    }
}