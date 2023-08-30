package org.kyaru.pixiv.service.download.parse.impl;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.kyaru.pixiv.service.download.parse.TaskEngine;
import org.kyaru.pixiv.service.download.parse.TaskID;
import org.kyaru.pixiv.service.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.service.utils.requester.RequestClient;
import org.kyaru.pixiv.service.utils.requester.ReturnType;
import org.seimicrawler.xpath.JXDocument;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IMGParseTask implements TaskEngine.Task {
    private final RequestClient requestClient;

    public IMGParseTask(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    public List<String> getSrcNames(String artworkID, @NotNull List<String> srcURLs) {
        List<String> nameList = new ArrayList<>();
        int page = 0;
        for (String srcURL : srcURLs) {
            String html = requestClient.download("https://www.pixiv.net/artworks/%s".formatted(artworkID), ReturnType.STRING);
            String name = new JXDocument(Jsoup.parse(html).getAllElements()).selNOne("//head/title/text())").asString();
            Matcher matcher = Pattern.compile("(.*?).-.*?的插画 - pixiv").matcher(name);
            if (matcher.find()) {
                name = matcher.group(1);
            }
            String suffix = srcURL.substring(srcURLs.get(page).length() - 3);
            nameList.add(String.format("[IMG]%s|p%d.%s", name, page, suffix));
            page++;
        }
        return nameList;
    }

    public List<BufferedImage> getSrcImages(List<String> srcURLs) {
        List<byte[]> bytesList = requestClient.download(srcURLs, ReturnType.BYTES);
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
        String json = this.requestClient.download(url, ReturnType.STRING);
        JSONUtil.select(json, "body").toStringList().forEach(node -> {
            String srcURL = JSONUtil.select(node, "urls", "original").toString();
            srcURLs.add(srcURL);
        });
        return srcURLs;
    }

    @Override
    public TaskItem.SaveData run(TaskID parameter) {
        TaskItem.ArtworkID artworkID = (TaskItem.ArtworkID) parameter;
        List<String> srcURLs = getSrcURLs(artworkID.artworkID());
        return new TaskItem.SaveData(
                getSrcNames(artworkID.artworkID(), srcURLs), getSrcImages(srcURLs),
                TaskID.Step.AFTER_PARSING, new TaskID.Label(parameter.label().tag(), TaskID.Label.Format.IMG)
        );
    }
}