package org.kyaru.pixiv.service.download.process.impl;

import org.jsoup.Jsoup;
import org.kyaru.pixiv.service.download.process.TaskEngine;
import org.kyaru.pixiv.service.download.process.TaskID;
import org.kyaru.pixiv.service.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.service.utils.requester.RequestClient;
import org.kyaru.pixiv.service.utils.requester.ReturnType;
import org.seimicrawler.xpath.JXDocument;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

public class GIFParseTask implements TaskEngine.Task {
    private final RequestClient requestClient;

    public GIFParseTask(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    public List<String> getSrcNames(String artworkID, String json) {
        List<Double> delayList = new ArrayList<>();
        JSONUtil.select(json, "body", "frames").toStringList().forEach(item -> {
            if (item != null) {
                String delayInfo = JSONUtil.select(item, "delay").toString();
                delayList.add(Double.parseDouble(delayInfo));
            }
        });
        int delayMs = (int) (delayList.stream().mapToDouble(Double::doubleValue).sum() / delayList.size());

        String html = requestClient.download("https://www.pixiv.net/artworks/%s".formatted(artworkID), ReturnType.STRING);
        String name = new JXDocument(Jsoup.parse(html).getAllElements()).selNOne("//head/title/text())").asString();
        Matcher matcher = Pattern.compile("(.*?).-.*?的动图 - pixiv").matcher(name);
        if (matcher.find()) {
            name = matcher.group(1);
        }
        return Collections.singletonList( "[GIF%s]%s.gif".formatted(delayMs, name));
    }

    public List<BufferedImage> getSrcImages(String json) {
        String zipUrl = JSONUtil.select(json, "body", "originalSrc").toString();
        byte[] bytes = this.requestClient.download(zipUrl, ReturnType.BYTES);

        List<BufferedImage> srcBytesList = new ArrayList<>();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ZipInputStream zis = new ZipInputStream(bais)) {
            while (zis.getNextEntry() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                srcBytesList.add(ImageIO.read(new ByteArrayInputStream(baos.toByteArray())));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return srcBytesList;
    }

    @Override
    public TaskItem.SaveData run(TaskID parameter) {
        TaskItem.ArtworkID artworkID = (TaskItem.ArtworkID) parameter;
        String url = "https://www.pixiv.net/ajax/illust/%s/ugoira_meta?lang=zh".formatted(artworkID.artworkID());
        String json = this.requestClient.download(url, ReturnType.STRING);
        return new TaskItem.SaveData(
                getSrcNames(artworkID.artworkID(), json), getSrcImages(json),
                TaskID.Step.AFTER_PARSING, new TaskID.Label(parameter.label().tag(), TaskID.Label.Format.GIF)
        );
    }
}